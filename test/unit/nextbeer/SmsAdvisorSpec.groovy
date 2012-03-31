
package nextbeer

import nextbeer.google.GooglePlacesFacade
import nextbeer.openApi.Location
import nextbeer.openApi.OpenApiFacade
import nextbeer.google.places.Place
import nextbeer.google.details.PlaceDetails

class SmsAdvisorSpec extends spock.lang.Specification {
    String phoneNumber = "7623657847"
    int rangeInMeters = 3000
    String latitude = "52.233418"
    String longitude = "21.019419"
    OpenApiFacade openApiFacade = Mock()
    GooglePlacesFacade googlePlacesFacade = Mock()
    Map place1Properties = [reference: "a1", name: "Drink-bar", vicinity: "Wspólna 52/54, Warsaw"]
    Map place2Properties = [reference: "a2", name: "Pepper Pub & Restaurant", vicinity: "Wilcza 35/41, Warsaw"]

    def "should send sms with proposals for current location"() {
        given:
        openApiGivesUsLocation()
        googleGivesUsPlaces()
        SmsAdvisor smsAdvisor = new SmsAdvisor(openApiFacade, googlePlacesFacade)

        when:
        smsAdvisor.sendSmsWithProposalsForCurrentLocation(phoneNumber, rangeInMeters)

        then:
        1 * openApiFacade.sendSms(phoneNumber, URLEncoder.encode("Drink-bar, Wspólna 52/54, Warsaw, +48 22 629 26 25\n" +
                                                                 "Pepper Pub & Restaurant, Wilcza 35/41, Warsaw, +48 22 621 35 06"))
    }

    private void openApiGivesUsLocation() {
        openApiFacade.askForPermissionToGetLocation(phoneNumber) >> true
        openApiFacade.getLocation(phoneNumber) >> new Location(latitude: latitude, longitude: longitude)
    }

    private void googleGivesUsPlaces() {
        googlePlacesFacade.getInVicinity(latitude, longitude , rangeInMeters) >> [new Place(place1Properties), new Place(place2Properties)]
        googlePlacesFacade.getDetailsAsync(["a1", "a2"]) >>
                [new PlaceDetails(place1Properties.clone() << [phone: "+48 22 629 26 25"]),
                 new PlaceDetails(place2Properties.clone() << [phone: "+48 22 621 35 06"])]
    }
}
