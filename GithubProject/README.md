Instrukcja kompilacji:

cd GithubProject\src  
javac ServerApp.java  
java ServerApp.java {username}  

{username} jest opcjonalnym argumentem oznaczającym użytkownika, którego chcemy sprawdzić. Program sam o niego zapyta w przypadku braku podania go jako parametru.

Uwagi.  

Program opiera się na wysyłaniu zapytań do Github API i z nich pobiera pewne dane. Ma to niestety swoje ograniczenia - program może wykonać jedynie 60 zapytań na godzinę.
Z tego powodu część kodu odpowiadająca za wypisanie używanych języków została w kodzie zakomentowana. Ten fragment kodu tworzył nowe zapytanie do każdego repozytorium o wszystkie języki, 
których używa, przez co w przypadku większych repozytoriów niemożliwe było jego wykonanie. Ogólny wniosek jest więc taki, że nawet jeśli wykorzystałoby się pewne sposoby na powiększenie
limitu zapytań to i tak ten pomysł nie jest zbyt dobry i należy pomyśleć o czymś innym niż Github API. Oczywiście w większości przypadków taki system wystarczy, ponieważ aby sprawdzić 
zwykłego użytkownika, który nie ma zbyt wiele repozytoriów, wyślemy pojedyncze zapytanie. Z innych uwag, można pomyśleć o lepszym sposobie wyszukiwania konkretnych danych. Okazuje się, 
że wyszukanie "name" gdzieś na stronie, niekoniecznie zwróci nazwę repozytorium. 
