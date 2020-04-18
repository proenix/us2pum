package pl.proenix.android.us2pum.lab4readaloud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.SimpleFileVisitor;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    private final static int REQUEST_CODE_CHOOSE_FILE = 1;

    private TextToSpeech tts;
    private float readingSpeed = 1;
    private Button buttonSpeakEn, buttonSpeakPl, buttonOpenFile;
    private EditText editTextSpeak;
    private SeekBar seekBarSpeakSpeed;
    private TextView textViewReadSpeed;

    private String lastDirOpenPath;
    private String fileToRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize TextToSpeach module
        tts = new TextToSpeech(this, this);

        buttonSpeakEn = findViewById(R.id.buttonSpeakEn);
        buttonSpeakPl = findViewById(R.id.buttonSpeakPl);
        editTextSpeak = findViewById(R.id.editTextSpeak);
        seekBarSpeakSpeed = findViewById(R.id.seekBar);
        textViewReadSpeed = findViewById(R.id.textViewReadSpeed);
        textViewReadSpeed.setText(String.valueOf(readingSpeed));
        buttonOpenFile = findViewById(R.id.buttonOpenFile);

        buttonSpeakEn.setOnClickListener(this);
        buttonSpeakPl.setOnClickListener(this);
        buttonOpenFile.setOnClickListener(this);

        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ||
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY) {
            lastDirOpenPath = Environment.getRootDirectory().getAbsolutePath();
        } else {
            lastDirOpenPath = getFilesDir().getAbsolutePath();
        }

        // Create sample file
        String filename = "sample.text";
        String fileContents = "Litwo! Ojczyzno moja! Ty jesteś jak zdrowie. Ile cię stracił. Dziś piękność twą w głównym sądzie w tkackim pudermanie). Wdział więc, jak roratne świéce. Pierwsza z krzykiem podróżnego barwą spłonęła rumian jak drudzy i psy za nim";
        Context context = getBaseContext();
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Sets speed of reading. Check seekBar bar progress.
        seekBarSpeakSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        readingSpeed = (float) 0.5;
                        break;
                    case 1:
                        readingSpeed = (float) 0.75;
                        break;
                    case 3:
                        readingSpeed = (float) 1.5;
                        break;
                    case 4:
                        readingSpeed = (float) 2.0;
                        break;
                    case 2:
                    default:
                        readingSpeed = (float) 1.0;
                }
                textViewReadSpeed.setText(String.valueOf(readingSpeed));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        editTextSpeak.setText("Litwo! Ojczyzno moja! Ty jesteś jak zdrowie. Ile cię stracił. Dziś piękność twą w głównym sądzie w tkackim pudermanie). Wdział więc, jak roratne świéce. Pierwsza z krzykiem podróżnego barwą spłonęła rumian jak drudzy i psy za nim");
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    /**
     * Check if Text To Speech module is loaded correctly.
     * @param status Status of Text To Speech initialization.
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            buttonSpeakEn.setEnabled(true);
            buttonSpeakPl.setEnabled(true);
        } else {
            Toast.makeText(this, "Text To Speech initialization failed.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Run action of reading aloud of provided text.
     * @param language String representation of Locale object.
     * @param text Text to be read aloud.
     */
    private void speakAloud(String language, String text) {
        int result = tts.setLanguage(localeFromString(language));
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Text To Speech language not supported or package missing.", Toast.LENGTH_LONG).show();
        } else {
            tts.setSpeechRate(readingSpeed);
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

    /**
     * On click handler for Main Activity.
     * @param v View of Main Activity.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == buttonSpeakEn.getId()) {
            speakAloud("en_US", editTextSpeak.getText().toString());
        }
        if (v.getId() == buttonSpeakPl.getId()) {
            speakAloud("pl_PL", editTextSpeak.getText().toString());
        }
        if (v.getId() == buttonOpenFile.getId()) {
            Intent intent = new Intent(getApplicationContext(), FileChooser.class);
            intent.putExtra("lastDirOpenPath", lastDirOpenPath);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_FILE:
                if (resultCode == RESULT_OK) {
                    lastDirOpenPath = data.getStringExtra("lastDirOpenPath");
                    fileToRead = data.getStringExtra("selectedDir");

                    File file = new File(fileToRead);
                    StringBuilder text = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;

                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                        }
                        br.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    editTextSpeak.setText(text.toString());
                }
                break;
        }
    }
}
