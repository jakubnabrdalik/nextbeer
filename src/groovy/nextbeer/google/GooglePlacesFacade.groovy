package nextbeer.google

import nextbeer.google.places.PlacesGetter
import nextbeer.google.places.Place
import nextbeer.google.details.PlaceDetails
import nextbeer.google.details.DetailsGetter

class GooglePlacesFacade {
    private static final String placesUrl = "https://maps.googleapis.com/maps/api/place/"
    private final PlacesGetter placesGetter
    private final DetailsGetter detailsGetter

    public GooglePlacesFacade(String apiKey) {
        placesGetter = new  PlacesGetter(apiKey, placesUrl)
        detailsGetter = new DetailsGetter(apiKey, placesUrl)
    }

    public Collection<Place> getInVicinity(String latitude, String longitude, int radiusInMeters = 3000 ) {
        placesGetter.getInVicinity(latitude, longitude, radiusInMeters)
    }

    public List<PlaceDetails> getDetailsAsync(Collection<String> placeReferences) {
        detailsGetter.getDetailsAsync(placeReferences)
    }
}