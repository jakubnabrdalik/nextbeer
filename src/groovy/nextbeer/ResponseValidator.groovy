package nextbeer

import groovyx.net.http.HttpResponseDecorator

class ResponseValidator {
    public static void verify(HttpResponseDecorator placesResponseDecorator) {
        verifyResponseStatusIs200(placesResponseDecorator)
    }

    public static verifyResponseStatusIs200(HttpResponseDecorator placesResponseDecorator) {
        if (placesResponseDecorator.status != 200) {
            throw new RuntimeException("Returned status is not 200. Data: " + placesResponseDecorator.toString())
        }
    }
}
