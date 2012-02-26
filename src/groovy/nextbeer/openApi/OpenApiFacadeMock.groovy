package nextbeer.openApi

class OpenApiFacadeMock implements OpenApiFacade {
    private int hasPermissionCall = 0
    private int whichCallToHasPermissionShouldReturnTrue
    List<String> calls = []

    public OpenApiFacadeMock(int whichCallToHasPermissionShouldReturnTrue) {
        this.whichCallToHasPermissionShouldReturnTrue = whichCallToHasPermissionShouldReturnTrue
    }

    public boolean hasPermissionToGetLocation(String phoneNumber) {
        calls << "hasPermissionToGetLocation"
        if(hasPermissionCall < whichCallToHasPermissionShouldReturnTrue) {
            println "OpenApi asked if has permission for: " + phoneNumber + "; Returning false"
            hasPermissionCall++
            return false
        }
        println "OpenApi asked if has permission for: " + phoneNumber + "; Returning true"
        hasPermissionCall = 0
        return true
    }

    public void askForPermissionToGetLocation(String phoneNumber) {
        calls << "askForPermissionToGetLocation"
        println "OpenApi asked for location permission for: " + phoneNumber
    }

    public Location getLocation(String phoneNumber) {
        calls << "getLocation"
        println "OpenApi asked for location for: " + phoneNumber
        return new Location(latitude: "52.233418", longitude: "21.019419")
    }

    public void sendSms(String phoneNumber, String text) {
        calls << "sendSms"
        println "OpenApi sending sms: " + text
    }
}
