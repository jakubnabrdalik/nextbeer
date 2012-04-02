
package nextbeer.google

import nextbeer.google.details.PlaceDetails
import nextbeer.google.places.Place
import spock.lang.Specification
import spock.lang.Shared

class GooglePlacesFacadeSpec extends Specification {
    private static final String WARSAW_CENTER_LATITUDE = "52.233418"
    private static final String WARSAW_CENTER_LONGITUDE = "21.019419"
    @Shared GooglePlacesFacade googlePlacesFacade = createGooglePlacesFacadeWithGoogleApiKey()

    GooglePlacesFacade createGooglePlacesFacadeWithGoogleApiKey() {
        Properties properties = new Properties()
        properties.load(new FileReader(new File("grails-app/conf/external-config.properties")))
        String googleApiKey = properties.get("google.places.api.key")
        return new GooglePlacesFacade(googleApiKey)
    }

    def "get places in vicinity"() {
        when:
        Collection<Place> places = googlePlacesFacade.getInVicinity(WARSAW_CENTER_LATITUDE, WARSAW_CENTER_LONGITUDE, 3000)

        then:
        places != null
        places.size() > 0
    }

    def "get details for places asynchronously"() {
        given:
        Collection<Place> places = googlePlacesFacade.getInVicinity(WARSAW_CENTER_LATITUDE, WARSAW_CENTER_LONGITUDE, 3000)

        when:
        List<PlaceDetails> detailsOfPlaces = googlePlacesFacade.getDetailsAsync(places.collect {it.reference})

        then:
        detailsOfPlaces != null
        detailsOfPlaces.size() == places.size()
    }

}
