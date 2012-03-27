package nextbeer

import nextbeer.openApi.OpenApiFacade

class OpenapiController {
    int checkPermissionIntervalInSeconds = 30
    OpenApiFacade openApiFacade
    SmsAdvisor smsAdvisor
    SmsJobPlanner smsJobPlanner

    def propose() {
        String phoneNumber = params.api."request[1].sender"
        String text = params.api."request[1].text"
        log.debug("Sms received. Sender: $phoneNumber, text: $text")
        int rangeInMeters = (text != null) ? text.toInteger() : 3000

        if(openApiFacade.hasPermissionToGetLocation(phoneNumber)) {
            smsAdvisor.sendSmsWithProposalsForCurrentLocation(phoneNumber, rangeInMeters)
        } else {
            openApiFacade.askForPermissionToGetLocation(phoneNumber)
            if(!smsJobPlanner.isThereAlreadyAnSmsPlannedToBeSent(phoneNumber)) {
                smsJobPlanner.scheduleQuartzJobToSendSmsWhenPermissionGranted(phoneNumber, rangeInMeters, checkPermissionIntervalInSeconds)
            }
        }
        render "Ok"
    }
}