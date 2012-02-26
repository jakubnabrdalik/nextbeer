# Wprowadzenie


Od zaprzyjaźnionego zespołu, tworzącego portal dla T-Mobile, dostałem wczesny dostęp do  Open API, czyli RESTowego API dla usług komórkowych. Czym Open API jest, nie będę się rozpisywał, bo to widać prawie natychmiast, spróbuję jednak pokazać przykład, co z takim API można zrobić.


# User story


Jest piątek wieczór, siedzisz w pubie ze znajomymi, których dawno nie widziałeś. Pub jest jakiś dziwny i zamykają go o dziesiątej, imprezę trzeba przenieść gdzie indziej. Wyciągasz swój wspaniały smartphone za miliard baksów, żeby znaleźć w necie nowe miejsce, ale niestety, bateria rozładowana. Niespodzianka? 


Śliczna brunetka po prawej wyciąga swoją starą Nokię, wysyła smsa i po chwili dostaje smsa zwrotnego, z nazwami, adresami i numerami telefonów pubów w okolicy trzech kilometrów. Teraz możecie tam zadzwonić i sprawdzić, do której będą otwarte i czy warto się tam przenosić. Impreza uratowana.


Aplikację, która obsłuży smsa ślicznej brunetki, napiszemy my. Będzie podpowiadać miejsca na wypicie kolejnego piwa, więc nazwjmy ją „nextbeer”.


# Projekt wstępny


Koncepcja jest prosta: odbieramy smsa, wyciągamy loOverviewSequenceDiagramkalizację użytkownika (nie potrzebny GPS, sieć poda nam ją na podstawie odległości od masztów i takie przybliżenie nam wystarczy), pytamy Google o pobliskie puby, ich numery telefonów i adresy, wysyłamy to wszystko z powrotem do użytkownika smsem.


Do rozmawiania z siecią GSM użyjemy Open API. Do rozmawiania z Googlem, wykorzystamy Google Places Api (http://code.google.com/apis/maps/documentation/places/).


Rzeczy na które musimy zwrócić szczególną uwagę w przypadku tych serwisów to:

*       aby pobrać lokalizację użytkownika, trzeba dostać od niego pozwolenie,
*       proszenie o pozwolenie jest asynchroniczne, tzn. my wysyłamy prośbę, sieć po jakimś czasie dostarczy użytkownikowi smsa z pytaniem, użytkownik odpowie lub nie,
*       z powyższych dwóch uwag wynika, że powinniśmy odpytywać sieć o pozwolenie co jakiś czas z sensownym limitem prób,
*       żeby wyciągnąć numery telefonów pobliskich pubów z Google'a, będziemy musieli zapytać raz o listę pubów w pobliżu, a następnie tyle razy ile miejsc dostaniemy, o ich szczegóły – warto ten fragment zrównoleglić.


Jeden obrazek wart jest tysiąc słów, zatem oto uproszczony diagram sekwencji, dla naszej aplikacji:

![OverviewSequenceDiagram](https://github.com/jakubnabrdalik/nextbeer/raw/master/docs/OverviewSequenceDiagram.png)


Wygląda prosto, prawda? I dokładnie tak jest. Przejdźmy zatem do działania.


# Technologia


Open API jest serwisem REST'owym (JSON lub XML po HTTP), podobnie Google Places, zatem możemy skorzystać z dowolnej technologii webowej. Ze względu na prostotę i czytelność, przykład zrobimy w Grails (wersja 2.0.1). Do opóźnionego odpytywania, wykorzystamy bibliotekę Quartz i plugin do jej nowszej wersji (https://github.com/9ci/grails-quartz2). Komunikację REST'ową uprościmy sobie przy pomocy pluginu HTTPBuilder (http://groovy.codehaus.org/modules/http-builder).


Cały kod aplikacji jest dostępny w githubie: https://github.com/jakubnabrdalik/nextbeer


# Zaczynamy


Instalujemy Javę 6 lub 7, Grails'y 2.0.1 (http://grails.org/), otwieramy basha i tworzymy nową aplikację poleceniem:


        grails create-app nextbeer


Dodajemy plugin do quartza:


        grails install-plugin quartz2


Rejestrujemy się w Open API oraz w GoogleApi. 


Ze względu na to, że mam dostęp do wersji niepublicznej, nie mogę podać linku rejestracyjnego i instrukcji dla Open API, dla Google znajdziemy wszystko tutaj: https://code.google.com/apis/console.


Efektem rejestracji w obu serwisach będą klucze (stringi), które wykorzystamy do uwierzytelniania naszej aplikacji w serwisach zewnętrznych. Przy rejestracji w Open API, będziemy musieli jeszcze skonfigurować sms callback, czyli URL, pod jaki zostaną wysłane sms'y od użytkowników. Dostaniemy również numer, na który będą mogli te sms'y wysyłać.


Całą uzyskaną konfigurację zapisujemy w pliku grails-app/conf/external-config.properties i będzie ona wyglądać mniej więcej tak:


        google.places.api.key=bardzolosowyklucz
        OpenAPI.key=jeszczebardziejlosowyklucz
        OpenAPI.testNumber=4899999999
        OpenAPI.url=http://jakisurl/core/api/OverviewSequenceDiagram


Poza dwoma kluczami przyda nam się numer do testów pół-automatycznych (OpenAPI.testNumber),  i należy tam wpisać numer własnej komórki.


Ponadto, ponieważ tworząc tą aplikację nie wiem jeszcze pod jakim URL Open API będzie dostępne oficjalnie (korzystam z wczesnej bety), to również zapisuję jako property (OpenAPI.url), do późniejszego zmodyfikowania.


Plik nazywam external-config, ponieważ jest to jedyna rzecz, której nie umieszczę w publicznie dostępnym repozytorium i każdy korzystający z projektu, musi go dodać/wypełnić sam.


Pozostało jedynie sprawić by plik ten był wczytywany przy starcie aplikacji, co robimy dodając do grails-app/conf/Config.groovy linijkę
grails.config.locations = [ "classpath:external-config.properties"]
Pliki w katalogu conf są automatycznie dodawane do classpath.


# Jak to przetestować?


Zgodnie z duchem Test Driven Development, zaczynamy od testu. Nasza aplikacja ma jeden punkt wejścia, który w dodatku będzie zwykłym kontrolerem Grailsowym (wzorzecz MVC). Po jakimś czasie (maksimum ustalone jest przez nas) od jego wywołania, Open API powinno dostać od nas smsa z lokalizacjami. 


Stworzymy test integracyjny end-to-end, który odpowie nam czy aplikacja działa. Teraz mamy do wyboru dwie możliwości: albo zaślepiamy zewnętrzne serwisy tworząc mocki/stuby, co da nam powtarzalność testów, albo testujemy z wykorzystaniem zewnętrznych serwisów, co nas uzależnia od dostawców.


Ponieważ nasza aplikacja jest nic nie warta bez zewnętrznych serwisów, a ich kontrakty mogą się w każdej chwili zmienić (używam Open API w wersji beta, zaś Google Places w momencie pisania tych słów jest w wersji experimental), wolę dowiedzieć się już w testach, że coś się zmieniło, zatem w przypadku Google wybieram opcję z testem end-to-end na prawdziwym serwisie. 


W przypadku Open API, każde wywołanie będzie nas jednak prawdopodobnie kosztowało kilka groszy, zatem tu zastosuję mock. Pomoże mi to również zweryfikować, czy faktycznie wysłałem smsa.


Założymy też, że użytkownik może w smsie podać promień, w jakim interesują go ewentualne puby. Nasza śliczna brunetka, może być w szpilkach i nie chcieć daleko chodzić.


Nasz test wygląda zatem tak:


https://github.com/jakubnabrdalik/nextbeer/blob/master/test/integration/nextbeer/OpenAPIControllerTests.groovy


W rzeczywistości będziemy się odpytywać Open API o pozwolenie użytkownika pięć razy co trzydzieści sekund, ale ponieważ taki test trwałby bardzo długo, parametryzujemy ten czas i ustawiamy w teście na sekundę. Test nadal będzie się wykonywał długo, ale jako że jest to jedyny  test akceptacyjny w naszej małej aplikacji, możemy sobie na to pozwolić. W przypadku większej aplikacji, powinniśmy ustalić sztuczną jednostkę czasu na potrzeby testu, by mieć go w pełni pod kontrolą. Test jest dość słaby (nie walidujemy wysyłanego smsa), ale na początek wystarczy.


No dobrze, wiemy już że będziemy mieć kontroler Open APIController z parametrem checkPermissionIntervalInSeconds i metodą propose(). Wiemy również, że potrzebujemy mieć jakąś fasadę do Open API, z interfejsem, implementacją oraz mockiem na potrzeby testów i developmentu, który w dodatku będziemy odpytywać o ostatnie wywołanie. 


Zacznimy od interfejsu Open API. Wiemy co Open API nam udostępnia i czego oczekujemy, zatem nasz interfejs będzie wyglądał tak:


https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/OpenAPI/OpenAPIFacade.groovy



Jego mock będzie odrobinę bardziej skomplikowany. Grails generalnie preferuje ręczne tworzenie mocków ponad wykorzystanie gotowych bibliotek mockujących (choć oczywiście posiada MockFor), więc tak też zrobimy. Potrzebujemy przechowywać wywołania by później je zweryfikować. Dobrze by było też, by nasz mock zachowywał się jak prawdziwe Open API, tzn. zapytany o uprawnienie do lokalizacji użytkownika, zwracał „true” dopiero po którymś wywołaniu.


https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/OpenAPI/OpenAPIFacadeMock.groovy



Tak stworzony mock, możemy podać do kontrolera w teście, ale ze względów oszczędnościowych przyda nam się także podczas developmentu, więc zadeklarujemy go sobie w profilu „test” kontekstu Springowego. By to zrobić, do pliku  grails-app/conf/spring/resources.groovy dodajemy:


        if (GrailsUtil.environment == "test") {
                OpenAPIFacade(OpenAPIFacadeMock, 4) {}
        } else {
                OpenAPIFacade(OpenAPIFacadeImpl, application.config.OpenAPI.key, application.config.OpenAPI.url) {}
        }


Dla osób nie znających Grailsów, a przyzwyczajonych do Springa, taka konfiguracja może się wydawać odrobinę dziwna, więc przyda się wytłumaczenie:
GrailsUtil.environment działa podobnie jak profile w Springu 3.1. Rejestracji beanów dokonuje się DSL'em (Domain Specific Language), podając nazwę pod jaką bean będzie zarejestrowany  (tu: OpenAPIFacade), jako pierwszy parametr, jego klasę, jako kolejne parametry, wartości przekazane do konstruktora, zaś w nawiasach klamrowych uzupełniając jego properties.


Brakuje nam jeszcze prawdziwej implementacji, która będzie wykorzystana w środowisku produkcyjnym (OpenAPIFacadeImpl). Co do tego fragmentu nie ma sensu tworzyć założeń a priori, lepiej napisać klasę stosując prototypowanie i sprawdzając odpowiedzi „fizycznie”.


W efekcie powstaje nam klasa o takim wyglądzie:
https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/OpenAPI/OpenAPIFacadeImpl.groovy



Wszystkie metody mają podobny schemat, tzn. przygotowują parametry requesta (HTTP GET), wysyłają go, weryfikują odpowiedź (w moim przypadku XML, ale dostępny jest również JSON) i ewentualnie parsują ją, zwracając przyjazne nam obiekty.


Przy weryfikacji odpowiedzi sprawdzimy nagłówek HTTP. W dokumentacji wersji beta Open API, nie mam jeszcze informacji o oczekiwanych odpowiedziach w przypadku błedu, ale w praktyce wygląda na to, że wystarczy wyszukać w XML'u wartości „failed” lub „failure”. 


OpenAPIResponseValidator będzie zatem wyglądał tak:


https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/OpenAPI/OpenAPIResponseValidator.groovy


Warto sobie pomóc przy testach manualnych klasy OpenAPIFacadeImpl (na wypadek zmiany api) i napisać test do wywołania ręcznego: 


https://github.com/jakubnabrdalik/nextbeer/blob/master/test/integration/nextbeer/OpenAPI/OpenAPIFacadeImplTest.groovy


Jecznocześnie metody parsujące xml'a i walidator,  możemy przetestować testem jednostkowym:


https://github.com/jakubnabrdalik/nextbeer/blob/master/test/unit/nextbeer/OpenAPI/OpenAPIFacadeImplUnitTest.groovy



https://github.com/jakubnabrdalik/nextbeer/blob/master/test/unit/nextbeer/OpenAPI/OpenAPIResponseValidatorTest.groovy



# Kontroler


Skoro już załatwiliśmy sprawę Open API, napiszmy w końcu sam kontroler.


Jego zadaniem jest nadzorowanie całej operacji, zatem odbierze smsa i sprawdzi uprawnienia. Jeśli je mamy, przekarze sterowanie klasie odpowiedzialnej za sugerowanie następnego pubu (nazwiemy ją SmsAdvisor), a jeśli nie, poprosi Open API o nie i zaplanoje uruchomienie zadania w Quartzu. Uruchamianie zadań w Quartzu wygląda na odpowiedzialność innego obiektu, więc stworzymy do tego celu osobną klasę: SmsJobPlanner.


https://github.com/jakubnabrdalik/nextbeer/blob/master/grails-app/controllers/nextbeer/OpenAPIController.groovy



Przy okazji powinniśmy sprawdzać, czy zadanie wysłania smsa nie jest już zaplanowane. Użytkownik może wysłać nieświadomie dwa.


# SmsAdvisor


Zadaniem SmsAdvisora jest pobrać lokalizację użytkownika z Open API, poprosić Google o okoliczne miejsca, gdzie można wypić piwo, i wysłać smsa. Zadanie będzie proste, jeśli stworzymy sobie fasadę do Google Api, podobnie jak to uczyniliśmy z Open API. Fasada będzie miała tylko jedną implementację, zatem nie potrzebujemy interfejsu. Zamiast niego, przyda się nam rozbić dwie metody Google Api z których będziemy korzystać: „search” i „details”, ponieważ drugą chcemy zdecydowanie wywołac asynchronicznie (będziemy mieli wiele miejsc o których detale musimy zapytać), co w naszym przypadku oznacza inne mechanizmy.


https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/google/GooglePlacesFacade.groovy



https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/google/places/PlacesGetter.groovy



https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/google/places/Place.groovy



PlacesGetter, mimo wykorzystania JSON'a, niewiele się różni od fasady Open API.  Weryfikację odpowiedzi ograniczymy do nagłówka.


https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/ResponseValidator.groovy



Zupełnie innym przypadkiem jest DetailsGetter. Tutaj skorzystamy z klasy AsyncHTTPBuilder, która pozwoli nam wywołać HTTP GET asynchronicznie, następnie zaczekamy na wszystkie wyniki i zgromadzimy je w postaci listy.


https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/google/details/DetailsGetter.groovy



https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/google/details/PlaceDetails.groovy



# SmsJobPlanner


Pozostał nam już tylko zadanie odwleczone w czasie, czyli reakcja na brak uprawnień. To odpowiedzialność SmsJobPlannera


https://github.com/jakubnabrdalik/nextbeer/blob/master/src/groovy/nextbeer/SmsJobPlanner.groovy


Trzeba to jeszcze wszystko spiąć kontekstem springowym, dodając do  grails-app/conf/spring/resources.groovy kolejne obiekty


googlePlacesFacade(GooglePlacesFacade, application.config.google.places.api.key) {}
smsAdvisor(SmsAdvisor, OpenAPIFacade, googlePlacesFacade) {}
smsJobPlanner(SmsJobPlanner, OpenAPIFacade, ref("quartzScheduler"), smsAdvisor) {}


I gotowe.


# Odpalamy wszystko razem


(TODO: wstawić obrazki, komórka z smsem wysyłanym i przychodzącym)


# Czego brakuje


Aplikacja ta raczej nie zarobi dla nas kokosów, biorąc pod uwagę jak mało miejsc jest wpisanych w Google Places w Polsce. Sensowne wyniki dostaniemy jedynie dla głównych miast, a i tu jest krucho. Mam nadzieję jednak, że jako przykład wystarczy.


Co należałoby dodać? 


Nie zrobiliśmy logowania, które z pewnością przydałoby się w wersji produkcyjnej. Możemy również pobrać informacje o modelu komórki, z której przyszedł sms, i dostosować do niej odpowiedź. Dla komórek posiadających przeglądarkę, choćby ubogą, można wysłać adres strony WWW poszczególnych miejsc lub zdjęcie z niej pobrane. Moglibyśmy się też pokusić o parsowanie stron i wyszukiwanie informacji o godzinach otwarcia (niestety Google Places jej nie ma, a puby nie zawsze je zamieszczają na stronach). Moglibyśmy również wyeliminować miejsca, które nie pasują do naszego profilu (pijalnia czekolady?).


Ewentualnie możemy aplikację uogólnić i wczytać z smsa nie tylko promień, ale także hasło po którym wyszukamy miejsca w Google Api.


# Problemy z wersją beta


Cały przedstawiony przykład działa jeszcze przed upublicznieniem oficjalnej wersji Open API, zaś Google Places Api jest w fazie „experimental”, zatem niekoniecznie będzie on odpowiadał wersjom ostatecznym poszczególnych serwisów. Warto skorzystać z aktualnej dokumentacji (której niestety nie posiadam), przed wysłaniem bomby do autora.
