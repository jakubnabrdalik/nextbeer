package nextbeer.openApi

import groovy.transform.PackageScope
import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

class OpenApiFacadeImpl implements OpenApiFacade{
    private static final String hasPermissionUrlSuffix = "api/permission/list"
    private static final String askForPermissionUrlSuffix = "api/permission/get"
    private static final String getLocationUrlSuffix = "api/location/get"
    private static final String sendSmsUrlSuffix = "api/messaging/sms"
    private final String apiKey
    private final RESTClient openApiClient

    OpenApiFacadeImpl(String apiKey, String apiUrl) {
        this.apiKey = apiKey
        openApiClient = new RESTClient(apiUrl)
        System.out.println(apiUrl)
    }

    @Override
    public boolean hasPermissionToGetLocation(String phoneNumber) {
        Map queryParams = [appkey: apiKey, target: phoneNumber]
        HttpResponseDecorator placesResponseDecorator = openApiClient.get(path:hasPermissionUrlSuffix, query:queryParams)
        OpenApiResponseValidator.verify(placesResponseDecorator)
        return hasLocationPermission(placesResponseDecorator.data)
    }

    @PackageScope boolean hasLocationPermission(NodeChild data) {
        return data.depthFirst().find { it.name() == "permission" && it?.type?.text() == "location" } != null
    }

    @Override
    public void askForPermissionToGetLocation(String phoneNumber) {
        Map queryParams = [appkey: apiKey, target: phoneNumber, permissions:"location", period:"10"]
        HttpResponseDecorator placesResponseDecorator = openApiClient.get(path:askForPermissionUrlSuffix, query:queryParams)
        OpenApiResponseValidator.verify(placesResponseDecorator)
    }

    @Override
    public Location getLocation(String phoneNumber) {
        Map queryParams = [appkey: apiKey, target: phoneNumber, async:"false", delegate:"false"]
        HttpResponseDecorator placesResponseDecorator = openApiClient.get(path:getLocationUrlSuffix, query:queryParams)
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
        HttpResponseDecorator placesResponseDecorator = openApiClient.get(path:sendSmsUrlSuffix, query:queryParams)
        OpenApiResponseValidator.verify(placesResponseDecorator)
    }
}
