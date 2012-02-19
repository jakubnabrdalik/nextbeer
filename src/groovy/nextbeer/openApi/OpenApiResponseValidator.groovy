package nextbeer.openApi

import groovyx.net.http.HttpResponseDecorator
import nextbeer.ResponseValidator
import groovy.util.slurpersupport.NodeChild
import groovy.transform.PackageScope

class OpenApiResponseValidator {
    public static void verify(HttpResponseDecorator placesResponseDecorator) {
        ResponseValidator.verifyResponseStatusIs200(placesResponseDecorator)
        verifyXmlContainsNoFails(placesResponseDecorator.data)
    }

    @PackageScope static verifyXmlContainsNoFails(NodeChild data) {
        if (data.depthFirst().find { it.text() == "failed" || it.text() == "failure" } != null) {
            throw new RuntimeException("Returned xml indicates a failure. Data: " + data.text())
        }
    }
}
