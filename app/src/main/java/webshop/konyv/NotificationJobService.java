package webshop.konyv;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

public class NotificationJobService extends JobService {
    private  NotificationHelper mNotificationHelper;

    @Override
    public boolean onStartJob(JobParameters params) {
        mNotificationHelper = new NotificationHelper(getApplicationContext());
        mNotificationHelper.send("Vár a sok könyv!");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
