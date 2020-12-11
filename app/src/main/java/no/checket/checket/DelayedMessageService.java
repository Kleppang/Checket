package no.checket.checket;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

// Notifications not finished, will write about it in the report.
public class DelayedMessageService extends IntentService {

    public static final String EXTRA_MESSAGE = "message";
    public static final int NOTIFICATION_ID = 9876;

    public DelayedMessageService() {
        super("DelayedMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            try {
                // Could not figure out what to use here. One thought was to
                // take intended time - current time + 15 minutes, but
                // that would be a poor solution.
                wait(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String text = intent.getStringExtra(EXTRA_MESSAGE);
        showText(text);
    }

    private void showText(final String text) {
        // Creating the notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Tasks")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Checket")
                .setContentText("You have an upcoming task in 15 minutes!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[] {0, 1000})
                .setAutoCancel(true);

        // Creating the action
        Intent actionIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(actionPendingIntent);

        // Sending out the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
