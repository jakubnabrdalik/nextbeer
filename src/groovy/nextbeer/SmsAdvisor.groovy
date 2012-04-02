package nextbeer

import nextbeer.openApi.Location
import nextbeer.google.places.Place
import nextbeer.google.details.PlaceDetails
import nextbeer.openApi.OpenApiFacade
import nextbeer.google.GooglePlacesFacade

class SmsAdvisor {
    OpenApiFacade openApiFacade
    GooglePlacesFacade googlePlacesFacade

    public SmsAdvisor(OpenApiFacade openApiFacade, GooglePlacesFacade googlePlacesFacade) {
        this.openApiFacade = openApiFacade
        this.googlePlacesFacade = googlePlacesFacade
    }

    public void sendSmsWithProposalsForCurrentLocation(String phoneNumber, int rangeInMeters) {
        Location location = openApiFacade.getLocation(phoneNumber)
        Collection<Place> places = googlePlacesFacade.getInVicinity(location.latitude, location.longitude, rangeInMeters)
        if(places.size() == 0) {
            openApiFacade.sendSms(phoneNumber, "Przykro mi, ale w Twoim pobliżu google nie znalazło żadnej knajpy :(")
        } else {
            List<PlaceDetails> placesWithDetails = googlePlacesFacade.getDetailsAsync(places.collect {it.reference})
            String smsText = placesWithDetails.collect {it.name + ", " + it.vicinity + ", " + it.phone}.join("; ")
            openApiFacade.sendSms(phoneNumber, smsText)
        }
    }
}
