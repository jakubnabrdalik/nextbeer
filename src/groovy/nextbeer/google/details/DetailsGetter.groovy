package nextbeer.google.details

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import java.util.concurrent.Future
import net.sf.json.JSONObject
import nextbeer.ResponseValidator

class DetailsGetter {
    private static final String placeDetailsUrlSuffix = "details/json"
    private final String apiKey
    private final String googlePlaceApiUrl

    public DetailsGetter(String apiKey, String googlePlaceApiUrl) {
        this.apiKey = apiKey
        this.googlePlaceApiUrl = googlePlaceApiUrl
    }

    public List<PlaceDetails> getDetailsAsync(Collection<String> placeReferences) {
        AsyncHTTPBuilder asyncHttpBuilder = new AsyncHTTPBuilder(poolSize:placeReferences.size(), uri:googlePlaceApiUrl, contentType:ContentType.JSON)
        List<Future<PlaceDetails>> detailsInFuture = []
        placeReferences.each {
            detailsInFuture << asyncHttpBuilder.get(path: placeDetailsUrlSuffix, query: createDetailsQueryParams(it)) { HttpResponseDecorator response, JSONObject json ->
                ResponseValidator.verify(response)
                return getPlaceDetails(json.result)
            }
        }
        return detailsInFuture.collect { it.get() }
    }

    private LinkedHashMap<String, Serializable> createDetailsQueryParams(String placeReference) {
        return [key: apiKey, reference: placeReference, sensor: false]
    }

    private PlaceDetails getPlaceDetails(JSONObject place) {
        return new PlaceDetails(name: place.name, reference: place.reference, vicinity: place.vicinity, website: place.website, phone: place.international_phone_number)
    }
}
