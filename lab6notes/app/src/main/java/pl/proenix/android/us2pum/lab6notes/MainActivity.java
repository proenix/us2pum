package pl.proenix.android.us2pum.lab6notes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHandler db;
    private static Context appContext;
    private static final String CHANNEL_ID = "NoteDueDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appContext = getApplicationContext();
        db = new DatabaseHandler(this);


        // Show all due date notifications and today notifications. Once on app opening.
        db.initializeSorterFilter();
        createNotificationChannel();
        List<Note> notes = db.findAllNotes();
        for (Note note : notes) {
            if (note.isDueDateBeforeTomorrow()) {
                Log.d("AndroidNotes", "Creating notification");
                createNotification(note);
            }
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    /**
     * On back navigate up.
     * Catches event on back navigation button in toolbar pressed from CreateUpdate Fragment.
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void createNotification(Note note) {
        Intent intent = new Intent(getAppContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_priority_critical)
                .setContentTitle((note.isAfterDue()) ? ("Due date missed: " + note.getFormattedDate() + " " + note.getFormattedTime()) : ("Due date today: " + note.getFormattedDate() + " " + note.getFormattedTime()))
                .setContentText(note.getTitleShort() + " " + note.getContentShort())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(note.getID().intValue(), builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "NoteDueDate", importance);
            channel.setDescription("Note due date notifications!");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
