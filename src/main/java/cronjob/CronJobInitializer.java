package cronjob;

import cronjob.jobs.NotifyReleasingGames;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import javax.persistence.EntityManagerFactory;

public class CronJobInitializer {
    private EntityManagerFactory factory;
    private static CronJobInitializer instance;

    public static CronJobInitializer getInstance(EntityManagerFactory factory) {
        if (instance == null) {
            instance = new CronJobInitializer(factory);
        }
        return instance;
    }

    CronJobInitializer(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void initializeJobs() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            scheduleNotifyReleasingGames(scheduler);

            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotifyReleasingGames(Scheduler scheduler) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(NotifyReleasingGames.class)
            .withIdentity("notifyReleasingGamesJob", "group1")
            .usingJobData(new JobDataMap(java.util.Collections.singletonMap("entityManagerFactory", this.factory)))
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("notifyReleasingGamesTrigger", "group1")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * * * ?"))
            .build();
        // Execute cronjob every minute: "0 */1 * * * ?"
        // Execute cronjob every start of day: "0 0 0 * * ?"

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
