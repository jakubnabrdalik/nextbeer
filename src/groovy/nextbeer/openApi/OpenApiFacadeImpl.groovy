package nextbeer.openApi

import groovy.transform.PackageScope
import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.apache.commons.logging.LogFactory
import groovyx.net.http.HttpResponseException

class OpenApiFacadeImpl implements OpenApiFacade{
    private static final String hasPermissionUrlSuffix = "api/permission/list"
    private static final String askForPermissionUrlSuffix = "api/permission/get"
    private static final String getLocationUrlSuffix = "api/location/get"
    private static final String sendSmsUrlSuffix = "api/messaging/sms"
    private final String apiKey
    private final RESTClient openApiClient
    private static final log = LogFactory.getLog(this)

    OpenApiFacadeImpl(String apiKey, String apiUrl) {
        this.apiKey = apiKey
        openApiClient = new RESTClient(apiUrl)
    }

    @Override
    public boolean hasPermissionToGetLocation(String phoneNumber) {
        Map queryParams = [appkey: apiKey, target: phoneNumber]
        HttpResponseDecorator placesResponseDecorator = callOpenApi(hasPermissionUrlSuffix, queryParams)
        OpenApiResponseValidator.verify(placesResponseDecorator)
        return hasLocationPermission(placesResponseDecorator.data)
    }

    @PackageScope boolean hasLocationPermission(NodeChild data) {
        return data.depthFirst().find { it.name() == "permission" && it?.type?.text() == "location" } != null
    }

    @Override
    public void askForPermissionToGetLocation(String phoneNumber) {
        Map queryParams = [appkey: apiKey, target: phoneNumber, permissions:"location", period:"10"]
        HttpResponseDecorator placesResponseDecorator = callOpenApi(askForPermissionUrlSuffix, queryParams)
        OpenApiResponseValidator.verify(placesResponseDecorator)
    }

    @Override
    public Location getLocation(String phoneNumber) {
        Map queryParams = [appkey: apiKey, target: phoneNumber, async:"false", delegate:"false"]
        HttpResponseDecorator placesResponseDecorator = callOpenApi(getLocationUrlSuffix, queryParams)
        OpenApiResponseValidator.verify(placesResponseDecorator)
        return getLocation(placesResponseDecorator.data)
    }

    @PackageScope Location getLocation(NodeChild data) {
        def latitude = data.depthFirst().find() {it.name() == "lat"}.text()
        def longitude = data.depthFirst().find() {it.name() == "lon"}.text()
        return new Location(latitude: latitude, longitude: longitude)
    }

    @Override
    public void sendSms(String phoneNumber, String text) {
        Map queryParams = [appkey: apiKey, to:phoneNumber, text:text]
        HttpResponseDecorator placesResponseDecorator = callOpenApi(sendSmsUrlSuffix, queryParams)
        OpenApiResponseValidator.verify(placesResponseDecorator)
    }

    private HttpResponseDecorator callOpenApi(String urlSuffix, LinkedHashMap<String, String> queryParams) {
        HttpResponseDecorator placesResponseDecorator
        try {
            log.debug("Calling $urlSuffix withg params: $queryParams")
            placesResponseDecorator = openApiClient.get(path: urlSuffix, query: queryParams)
        } catch( HttpResponseException exception ) {
            log.error("OpenApi call ended with exception. Response: $exception.response", exception)
            log.error("Response status: $exception.response.status")
            log.error("Response headers: $exception.response.allHeaders")
            throw exception
        }
        return placesResponseDecorator
    }
}
