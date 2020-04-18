package pl.proenix.android.us2pum.lab4readaloud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    private final static int REQUEST_CODE_CHOOSE_FILE = 1;

    private TextToSpeech tts;
    private float readingSpeed = 1;
    private Button buttonSpeakEn, buttonSpeakPl, buttonOpenFile;
    private EditText editTextSpeak;
    private TextView textViewReadSpeed;

    private String lastDirOpenPath;
    private String appFilesPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize TextToSpeech module
        tts = new TextToSpeech(this, this);

        buttonSpeakEn = findViewById(R.id.buttonSpeakEn);
        buttonSpeakPl = findViewById(R.id.buttonSpeakPl);
        editTextSpeak = findViewById(R.id.editTextSpeak);
        buttonOpenFile = findViewById(R.id.buttonOpenFile);
        textViewReadSpeed = findViewById(R.id.textViewReadSpeed);
        textViewReadSpeed.setText(String.valueOf(readingSpeed));
        SeekBar seekBarSpeakSpeed = findViewById(R.id.seekBar);

        buttonSpeakEn.setOnClickListener(this);
        buttonSpeakPl.setOnClickListener(this);
        buttonOpenFile.setOnClickListener(this);

        if ((Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) ||
            (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {
            appFilesPath = getExternalFilesDir(null).getAbsolutePath();
        } else {
            appFilesPath = getFilesDir().getAbsolutePath();
        }
        lastDirOpenPath = appFilesPath;

        // Create sample file in storage
        createSampleFile();

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

        editTextSpeak.setText(R.string.sample_text);
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
            Toast.makeText(this, R.string.error_tts_initialization_failed, Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, R.string.error_tts_lang_not_supported, Toast.LENGTH_LONG).show();
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

    /**
     * Create sample file in external or internal application storage.
     */
    private void createSampleFile() {
        String filename = "sample.txt";
        String fileContents = (String) getText(R.string.sample_text);
        Context context = getBaseContext();

        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (IOException e) {
            Toast.makeText(context, R.string.error_creating_sample_file, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load text from selected file to text field.
     * @param fileToRead Path to file.
     */
    private void loadTextToReadFromFile(String fileToRead)
    {
        StringBuilder text = new StringBuilder();
        File file = new File(fileToRead);
        try {
            BufferedReader br;
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Toast.makeText(this, R.string.error_cannot_load_text_from_file, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        editTextSpeak.setText(text.toString());
    }

    /**
     * Process returned activity results.
     * @param requestCode Returned activity code.
     * @param resultCode Returned activity result code.
     * @param data Data transferred from closed activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                String fileToRead = data.getStringExtra("selectedDir");

                if (fileToRead != null) {
                    loadTextToReadFromFile(fileToRead);
                }
            }
        }
    }
}
