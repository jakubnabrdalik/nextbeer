package nextbeer.google

import org.junit.Before
import org.junit.Test

import nextbeer.google.places.Place
import nextbeer.google.details.PlaceDetails

class GooglePlacesFacadeTest {
    private static final String WARSAW_CENTER_LATITUDE = "52.233418"
    private static final String WARSAW_CENTER_LONGITUDE = "21.019419"
    GooglePlacesFacade googlePlacesFacade

    @Before
    void createGooglePlacesFacadeWithGoogleApiKey() {
        Properties properties = new Properties()
        properties.load(new FileReader(new File("grails-app/conf/external-config.properties")))
        String googleApiKey = properties.get("google.places.api.key")
        googlePlacesFacade = new GooglePlacesFacade(googleApiKey)
    }

    @Test
    void shouldGetPlacesInVicinity() {
        //when
        Collection<Place> places = googlePlacesFacade.getInVicinity(WARSAW_CENTER_LATITUDE, WARSAW_CENTER_LONGITUDE, 3000)

        //then
        assert places != null
        assert places.size() > 0
    }
    
    @Test
    void shouldGetPlaceDetailsAsynchronously() {
        //given
        Collection<Place> places = googlePlacesFacade.getInVicinity(WARSAW_CENTER_LATITUDE, WARSAW_CENTER_LONGITUDE, 3000)
        
        //when
        List<PlaceDetails> detailsOfPlaces = googlePlacesFacade.getDetailsAsync(places.collect {it.reference})

        //then
        assert detailsOfPlaces != null
        assert detailsOfPlaces.size() == places.size()
    }
}
