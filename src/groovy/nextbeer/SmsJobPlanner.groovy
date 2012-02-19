/*
 * Copyright (c) (2005 - 2011) TouK sp. z o.o. s.k.a.
 * All rights reserved
 */
 
package nextbeer

import grails.plugin.quartz2.ClosureJob
import grails.plugin.quartz2.SimpleJobDetail
import nextbeer.openApi.OpenApiFacade
import org.springframework.context.ApplicationContext
import org.quartz.*
import static org.quartz.SimpleScheduleBuilder.simpleSchedule

class SmsJobPlanner {
    private OpenApiFacade openApiFacade
    private SmsAdvisor smsAdvisor
    private Scheduler quartzScheduler

    public SmsJobPlanner(OpenApiFacade openApiFacade, Scheduler quartzScheduler, SmsAdvisor smsAdvisor) {
        this.openApiFacade = openApiFacade
        this.quartzScheduler = quartzScheduler
        this.smsAdvisor = smsAdvisor
    }

    public boolean isThereAlreadyAnSmsPlannedToBeSent(String phoneNumber) {
        return quartzScheduler.checkExists(new TriggerKey(createTriggerName(phoneNumber)))
    }

    public void scheduleQuartzJobToSendSmsWhenPermissionGranted(String phoneNumber, int rangeInMeters, int checkPermissionIntervalInSeconds) {
        SimpleJobDetail jobDetail = ClosureJob.createJob(name: "jobSendProposalsTo" + phoneNumber, concurrent: false) {
            JobExecutionContext jobExecutionContext, ApplicationContext applicationContext ->
            if (openApiFacade.hasPermissionToGetLocation(phoneNumber)) {
                smsAdvisor.sendSmsWithProposalsForCurrentLocation(phoneNumber, rangeInMeters)
                quartzScheduler.unscheduleJob(new TriggerKey(createTriggerName(phoneNumber)))
            }
        }
        Trigger trigger = createJobTrigger(phoneNumber, checkPermissionIntervalInSeconds)
        quartzScheduler.scheduleJob(jobDetail, trigger)
    }

    private Trigger createJobTrigger(String phoneNumber, int checkPermissionIntervalInSeconds) {
        Trigger trigger = TriggerBuilder.newTrigger().
                withIdentity(createTriggerName(phoneNumber)).
                withSchedule(
                        simpleSchedule()
                                .withIntervalInSeconds(checkPermissionIntervalInSeconds)
                                .withRepeatCount(4)
                ).
                build()
        return trigger
    }

    private String createTriggerName(String phoneNumber) {
        return "triggerSendProposalsTo" + phoneNumber
    }
}
