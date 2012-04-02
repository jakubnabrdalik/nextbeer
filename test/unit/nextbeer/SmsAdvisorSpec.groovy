
package nextbeer

import nextbeer.google.GooglePlacesFacade
import nextbeer.openApi.Location
import nextbeer.openApi.OpenApiFacade
import nextbeer.google.places.Place
import nextbeer.google.details.PlaceDetails

class SmsAdvisorSpec extends spock.lang.Specification {
    static final String phoneNumber = "7623657847"
    static final int rangeInMeters = 3000
    static final String latitude = "52.233418"
    static final String longitude = "21.019419"
    OpenApiFacade openApiFacade = Mock()
    GooglePlacesFacade googlePlacesFacade = Mock()
    SmsAdvisor smsAdvisor = new SmsAdvisor(openApiFacade, googlePlacesFacade)

    def setup() {
        openApiGivesUsLocation()
    }

    private void openApiGivesUsLocation() {
        openApiFacade.askForPermissionToGetLocation(phoneNumber) >> true
        openApiFacade.getLocation(phoneNumber) >> new Location(latitude: latitude, longitude: longitude)
    }

    def "should send sms with proposals for current location"() {
        given:
        googleGivesUsPlaces()

        when:
        smsAdvisor.sendSmsWithProposalsForCurrentLocation(phoneNumber, rangeInMeters)

        then:
        1 * openApiFacade.sendSms(phoneNumber, URLEncoder.encode("Drink-bar, Wspólna 52/54, Warsaw, +48 22 629 26 25\n" +
                                                                 "Pepper Pub & Restaurant, Wilcza 35/41, Warsaw, +48 22 621 35 06"))
    }

    private void googleGivesUsPlaces() {
        Map place1Properties = [reference: "a1", name: "Drink-bar", vicinity: "Wspólna 52/54, Warsaw"]
        Map place2Properties = [reference: "a2", name: "Pepper Pub & Restaurant", vicinity: "Wilcza 35/41, Warsaw"]
        googlePlacesFacade.getInVicinity(latitude, longitude , rangeInMeters) >> [new Place(place1Properties), new Place(place2Properties)]
        googlePlacesFacade.getDetailsAsync(["a1", "a2"]) >>
                [new PlaceDetails(place1Properties.clone() << [phone: "+48 22 629 26 25"]),
                 new PlaceDetails(place2Properties.clone() << [phone: "+48 22 621 35 06"])]
    }

    def "should send we`re sorry sms when no places found"() {
        given:
        googleGivesUsNothing()

        when:
        smsAdvisor.sendSmsWithProposalsForCurrentLocation(phoneNumber, rangeInMeters)

        then:
        1 * openApiFacade.sendSms(phoneNumber, "Przykro mi, ale w Twoim pobliżu google nie znalazło żadnej knajpy :(")
    }

    private void googleGivesUsNothing() {
        googlePlacesFacade.getInVicinity(latitude, longitude , rangeInMeters) >> []
    }
}
