package nextbeer.openApi

interface OpenApiFacade {
    boolean hasPermissionToGetLocation(String phoneNumber)
    void askForPermissionToGetLocation(String phoneNumber)
    Location getLocation(String phoneNumber)
    void sendSms(String phoneNumber, String text)
}

class Location {
    String latitude
    String longitude
}
