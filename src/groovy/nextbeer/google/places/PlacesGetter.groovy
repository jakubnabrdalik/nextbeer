package nextbeer.google.places

import groovyx.net.http.HttpResponseDecorator
import net.sf.json.JSONObject
import groovyx.net.http.RESTClient
import nextbeer.ResponseValidator

class PlacesGetter {
    private static final String placesSearchUrlSuffix = "search/json"
    private final String apiKey
    private final RESTClient googlePlacesClient

    public PlacesGetter(String apiKey, String googlePlaceApiUrl) {
        this.apiKey = apiKey
        googlePlacesClient = new RESTClient(googlePlaceApiUrl)
    }

    public Collection<Place> getInVicinity(String latitude, String longitude, int radiusInMeters) {
        Map queryParams = createQueryParams(latitude, longitude, radiusInMeters)
        HttpResponseDecorator placesResponseDecorator = googlePlacesClient.get(path:placesSearchUrlSuffix, query:queryParams)
        ResponseValidator.verify(placesResponseDecorator)
        return getPlaces(placesResponseDecorator)
    }

    private Collection<Place> getPlaces(HttpResponseDecorator placesResponseDecorator) {
        return placesResponseDecorator.data.results.collect { JSONObject place ->
            new Place(name: place.name, reference: place.reference, vicinity: place.vicinity)
        }
    }

    private Map createQueryParams(String latitude, String longitude, int radiusInMeters) {
        return [key: apiKey, location: (latitude + "," + longitude), radius: radiusInMeters, sensor: false, keyword: "drink"]
    }
}
