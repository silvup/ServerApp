import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerApp {
    private static String FindValues(String text, String start) // Znajdujemy kolejne wystąpienia słowa "name" lub "stargazers_count"
    {
        String actList = "";
        int lastIndex = text.lastIndexOf(start); // Ostatnie wystąpienie poszukiwanego słowa
        int nextIndex = 0;
        do {
            int actIndex = text.indexOf(start, nextIndex); // Kolejne wystąpienie poszukiwanego słowa
            actIndex = actIndex + start.length() + 1; // Tutaj zaczyna się właściwa nazwa lub liczba gwiazdek ( po znakach ": )
            int endIndex = text.indexOf(",", actIndex); // Znajdujemy kolejny przecinek, który oznacza że poszukiwane słowo się skończyło
            actList += text.substring(actIndex, endIndex);
            actList += "\n";
            nextIndex = actIndex; // Szukamy kolejnego wystapienia poszukiwanego słowa
        }
        while (nextIndex < lastIndex);
            return actList;

    }

    private static String listRepositories(HttpResponse response) // przekazujemy słowo do wyszukania
    {
        String rawBody = response.body().toString();
        return (FindValues(rawBody, "\"full_name\""));
    }

    private static String getStars(HttpResponse response) // przekazujemy słowo do wyszukania
    {
        String rawBody = response.body().toString();
        return (FindValues(rawBody, "\"stargazers_count\""));
    }

    private static String getMostPopularLanguages(HttpResponse response) // budujemy nowe zapytanie o języki w repozytorium
    {
        String rawBody = response.body().toString();
        String languageUrl = FindValues(rawBody, "\"languages_url\"");
        languageUrl = languageUrl.replace("\"", "");
        return languageUrl;
    }

    private static HttpResponse<String> BuildResponse(HttpClient client, String name, int page) throws IOException, InterruptedException {
        String httpAddress = String.format("https://api.github.com/users/%s/repos?per_page=100&page=%d", name, page); // funkcja pomocnicza do budowy zapytania
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpAddress))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.setOut(new PrintStream(new FileOutputStream(java.io.FileDescriptor.out), true, "Cp852"));
        String name;
        if (args.length != 1) // sprawdzenie danych
        {
            System.out.println("Enter valid Github username:");
            Scanner scanner = new Scanner(System.in);
            name = scanner.nextLine();
        } else {
            name = args[0];
        }
        try {
            HttpClient client = HttpClient.newBuilder() // stworzenie klienta
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            int sumOfStars = 0;
            Map<String, Integer> popularCodes = new HashMap<>(); // Tu wyświetlimy sumę bajtów kodu i nazwę kodu

            int page = 1;
            int count;
            do {
                count = 0;
                HttpResponse<String> response = BuildResponse(client, name, page);
                page++;
                String repositoryList = listRepositories(response);
                String starList = getStars(response);
                String languageLinks = getMostPopularLanguages(response);
                Scanner linkScanner = new Scanner(languageLinks);
                Scanner repScanner = new Scanner(repositoryList);
                Scanner starScanner = new Scanner(starList);
                /*while (linkScanner.hasNextLine()) // tworzymy dużo nowych zapytań do podstron zawierających używane języki, (przekracza limity zapytań)
                {
                    String langHttpAddress = linkScanner.nextLine();
                    HttpRequest langRequest = HttpRequest.newBuilder()
                            .uri(URI.create(langHttpAddress))
                            .GET()
                            .build();
                    HttpResponse<String> linkResponse = client.send(langRequest, HttpResponse.BodyHandlers.ofString());
                    String rawBody = linkResponse.body();
                    rawBody = rawBody.replace('{', ',');
                    rawBody = rawBody.replace('}', ',');
                    if (rawBody.length() < 3)
                        continue;
                    int firstLang = rawBody.indexOf("\"");
                    int lastLang = rawBody.lastIndexOf("\"");
                    int firstNum = rawBody.indexOf(":");
                    int lastNum = rawBody.indexOf(",", 1);
                    int nextLang;
                    do {
                        nextLang = rawBody.indexOf("\"", firstLang + 1);
                        String lang = rawBody.substring(firstLang + 1, nextLang);
                        firstLang = rawBody.indexOf("\"", nextLang + 1);
                        String bytes = rawBody.substring(firstNum + 1, lastNum);
                        firstNum = rawBody.indexOf(":", firstNum + 1);
                        lastNum = rawBody.indexOf(",", lastNum + 1);
                        int bytesToInt = Integer.parseInt(bytes);
                        if (popularCodes.containsKey(lang)) {
                            popularCodes.put(lang, popularCodes.get(lang) + bytesToInt);
                        } else {
                            popularCodes.put(lang, bytesToInt);
                        }
                    }
                    while (nextLang < lastLang);
                }*/
                while (repScanner.hasNextLine()) {
                    count++;
                    String starNumber = starScanner.nextLine();
                    System.out.println("Repository name: " + repScanner.nextLine() + "\nStars count: " + starNumber + "\n");
                    sumOfStars += Integer.parseInt(starNumber);
                }
            }
            while (count == 100);
            System.out.println("Sum of all stars in every repository: " + sumOfStars);
            System.out.println("\nList of used languages with corresponding byte code size value:");
            System.out.println("This part of the code has been commented due to Github's requests limit...");
            /*for (Map.Entry<String, Integer> entry : popularCodes.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                System.out.println(key + ":" + value);
            }*/
        } catch (Exception exception) {
            System.out.println("Program failed due to some reason...");
        }
    }
}
