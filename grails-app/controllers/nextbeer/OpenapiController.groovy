package nextbeer

import nextbeer.openApi.OpenApiFacade

class OpenapiController {
    int checkPermissionIntervalInSeconds = 30
    OpenApiFacade openApiFacade
    SmsAdvisor smsAdvisor
    SmsJobPlanner smsJobPlanner

    def propose() {
        String phoneNumber = params.from
        int rangeInMeters = (params.text != null) ? params.getInt("text") : 3000

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