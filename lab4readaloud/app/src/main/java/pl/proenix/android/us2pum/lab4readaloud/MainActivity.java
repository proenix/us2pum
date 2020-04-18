package pl.proenix.android.us2pum.lab4readaloud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    private TextToSpeech tts;
    private Button buttonSpeakEn, buttonSpeakPl;
    private EditText editTextSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize TextToSpeach module
        tts = new TextToSpeech(this, this);

        buttonSpeakEn = findViewById(R.id.buttonSpeakEn);
        buttonSpeakPl = findViewById(R.id.buttonSpeakPl);
        editTextSpeak = findViewById(R.id.editTextSpeak);

        buttonSpeakEn.setOnClickListener(this);
        buttonSpeakPl.setOnClickListener(this);

        //editTextSpeak.setText("Litwo! Ojczyzno moja! Ty jesteś jak zdrowie. Ile cię stracił. Dziś piękność twą w głównym sądzie w tkackim pudermanie). Wdział więc, jak roratne świéce. Pierwsza z krzykiem podróżnego barwą spłonęła rumian jak drudzy i psy za nim");
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            buttonSpeakEn.setEnabled(true);
            buttonSpeakPl.setEnabled(true);
        } else {
            Toast.makeText(this, "Text To Speech initialization failed.", Toast.LENGTH_LONG).show();
        }
    }


    private void speakAloud(String language, String text) {
        int result = tts.setLanguage(localeFromString(language));
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Text To Speech language not supported or package missing.", Toast.LENGTH_LONG).show();
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "1");
        }
    }

    /**
     * Return Locale from provided string representation.
     * @param localeCode String representation of Locale object.
     * @return Locale object
     */
    private Locale localeFromString(String localeCode) {
        if (localeCode == null) {
            return new Locale("");
        }

        String[] fields = localeCode.split("_");
        switch (fields.length) {
            case 1:
                return new Locale(fields[0]);
            case 2:
                return new Locale(fields[0], fields[1]);
            case 3:
                return new Locale(fields[0], fields[1], fields[2]);
            default:
                return new Locale("");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == buttonSpeakEn.getId()) {
            speakAloud("en_US", editTextSpeak.getText().toString());
        }
        if (v.getId() == buttonSpeakPl.getId()) {
            speakAloud("pl_PL", editTextSpeak.getText().toString());
        }
    }
}
