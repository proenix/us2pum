package pl.proenix.android.us2pum.lab6notes;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHandler db;
    public static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appContext = getApplicationContext();

        db = new DatabaseHandler(this);
        // Reset DB for testing.
        db.resetDatabase();
        db.addNote(new Note(1L, "Testowa notatka 1.", "Tresc testowej notatki ktora powinna miec jakas tam dlugosc zeby bylo widac co sie dzieje.", Note.CATEGORY_HOME, Note.STATUS_IN_PROGRESS, Note.PRIORITY_DEFAULT, null));
        db.addNote(new Note(2L, "Testowa notatka 2.", "Tresc testowej notatki ktora powinna miec jakas tam dlugosc zeby bylo widac co sie dzieje.", Note.CATEGORY_FINANCE, Note.STATUS_IN_PROGRESS, Note.PRIORITY_DEFAULT, null));
        db.addNote(new Note(3L, "Testowa notatka 3.", "Tresc testowej notatki ktora powinna miec jakas tam dlugosc zeby bylo widac co sie dzieje.", Note.CATEGORY_WORK, Note.STATUS_IN_PROGRESS, Note.PRIORITY_DEFAULT, null));
        db.addNote(new Note(4L, "Testowa notatka 4.", "Tresc testowej notatki ktora powinna miec jakas tam dlugosc zeby bylo widac co sie dzieje.", Note.CATEGORY_VACATION, Note.STATUS_IN_PROGRESS, Note.PRIORITY_DEFAULT, null));
        db.addNote(new Note(4L, "Testowa notatka 5.", "Tresc testowej notatki ktora powinna miec jakas tam dlugosc zeby bylo widac co sie dzieje.", Note.CATEGORY_SCHOOL, Note.STATUS_IN_PROGRESS, Note.PRIORITY_DEFAULT, null));

//        // Check if device has camera
//        PackageManager packageManager = getApplicationContext().getPackageManager();
//        packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
