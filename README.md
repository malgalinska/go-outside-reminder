wersja 1.0:

Program wysyła maila z przypomnieniem o wychodzeniu na zewnątrz i obecną pogodą (opis, temperatura).

W kodzie programu jest przykładowe konto służące do tesowania, aby korzystać z aplikacji należy zmienić dane konta na swoje własne.

Docelowo program powinien być dodany do programów startowych. Wtedy każde uruchomienie systemu przypomni nam o tym, że może lepiej najpierw się trochę przewietrzyć, potem będzie się lepiej pracować.

Jest to program zaliczeniowy na Języki i narzędzia programowania 2.

wersja 2.0:

Projekt został rozszerzony o przypominajkę zadań do zrobienia. Po uruchomieniu komputera automatycznie co ustalony czas (np. 30 min) wysyłany jest mail o aktualnym zadaniu do wykonania. Po odpisaniu mailem o treści "Done" aplikacja będzie informować nas o kolejnym zadaniu z listy. Lista oraz informacja o już wykonanych zadaniach jest przechowywana w pliku, dzięki czemu nie tracimy tych informacji, gdy wyłączymy program. Docelowo, po uruchomieniu komputera, program przypomina nam o zadaniu którego nie dokończyliśmy dzień wcześniej.

Listę zadań możemy edytować w pliku, z którego czyta ją aplikacja. Modyfikując listę, powinno się zrestartować program. Edytując listę możemy pozbyć się zrealizowanych zadań (jeśli nie potrzebujemy informacji o nich), dodać nowe oraz zmieniać kolejność zadań do realizacji.

Program uruchamiamy linijką:
mvn compile exec:java -PReminder  

W kodzie programu należy ustawić własne konto mailowe, lokalizację i nazwę pliku z zadaniami oraz miasto, z którego chcemy dostawać informacje o pogodzie.

Lista zadań ma konkretny format: w pierwszej linii znajduje się liczba już zrealizowanych zadań (patrząc od góry listy), a następnie lista zadań o malejącym priorytecie. Każde zadanie znajduje się w osobnej linii. Opis zadania może być dowolnie szczegółowy, zosatnie on w całości wysłany mailem.
