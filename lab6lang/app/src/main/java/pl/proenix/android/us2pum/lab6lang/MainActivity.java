package pl.proenix.android.us2pum.lab6lang;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "AndroidLang";
    public static DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHandler(this);
//        DatabaseHandler db = new DatabaseHandler(this);
        db.resetDatabase();

        int eng_word = -1;
        int eng_word_2 = -1;
        int pol_word_1 = -1;
        int pol_word_2 = -1;
        Log.d(DEBUG_TAG, "Reset table state and insert sample data.");

        // Add first English Word.
        eng_word = db.addWord(new Word("car", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,1));
        pol_word_1 = db.addWord(new Word("samochód", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0));
        db.addWordsRelation(eng_word, pol_word_1);
        pol_word_2 = db.addWord(new Word("auto", Word.WORD_LANGUAGE_POLISH, Word.WORD_NOT_LEARNABLE,0));
        db.addWordsRelation(eng_word, pol_word_2);

        // Add second English Word.
        eng_word_2 = db.addWord(new Word("vehicle", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,1));
        db.addWordsRelation(eng_word_2, pol_word_1);
        db.addWordsRelation(eng_word_2, pol_word_2);

        db.addWordsRelation(eng_word, eng_word_2);
        db.addWordsRelation(eng_word_2, eng_word);

        eng_word = db.addWord(new Word("city", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0));
        pol_word_1 = db.addWord(new Word("miasto", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0));
        db.addWordsRelation(eng_word, pol_word_1);

        eng_word = db.addWord(new Word("computer", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0));
        pol_word_1 = db.addWord(new Word("komputer", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0));
        db.addWordsRelation(eng_word, pol_word_1);

        eng_word = db.addWord(new Word("parsley", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0));
        pol_word_1 = db.addWord(new Word("pietruszka", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0));
        db.addWordsRelation(eng_word, pol_word_1);

        Log.d(DEBUG_TAG, "Total Words Count: "+ String.valueOf(db.getWordsCount()));
        Log.d(DEBUG_TAG, "English Words Count: "+ String.valueOf(db.getWordsCountByLanguage(Word.WORD_LANGUAGE_ENGLISH)));


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // TODO: 06/05/2020 Create migration routine - data imported once for application lifecycle. Preferably to write something in DatabaseHandler.

        // TODO: 06/05/2020 Feature: add step between menu and learning activity - list of words and learning status.
    }

    /**
     * Inflate Menu with options from xml file.
     * @param menu Menu.
     * @return Boolean true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle Menu option selections.
     * @param item MenuItem clicked.
     * @return Boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            openPopupAboutApp();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Open Popup About App on Main Activity.
     */
    private void openPopupAboutApp() {
        // Show About App.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_about_app, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setElevation(20);
        popupWindow.showAtLocation(findViewById(R.id.mainActivity), Gravity.CENTER, 0,0 );
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return false;
            }
        });
    }
}
