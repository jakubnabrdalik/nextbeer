import grails.plugin.quartz2.ClosureJob
import org.quartz.JobExecutionContext
import org.quartz.impl.triggers.SimpleTriggerImpl
import org.springframework.context.ApplicationContext

grails.plugin.quartz2.autoStartup = true

org{
    quartz{
        //anything here will get merged into the quartz.properties so you don't need another file
        scheduler.instanceName = 'smsProposalsScheduller'
        threadPool.class = 'org.quartz.simpl.SimpleThreadPool'
        threadPool.threadCount = 8
        threadPool.threadsInheritContextClassLoaderOfInitializingThread = true
        jobStore.class = 'org.quartz.simpl.RAMJobStore'
    }
}