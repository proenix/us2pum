package pl.proenix.android.us2pum.lab6notes;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHandler db;
    private static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appContext = getApplicationContext();
        db = new DatabaseHandler(this);
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
}
