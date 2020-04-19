package pl.proenix.android.us2pum.lab4readaloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

public class FileChooser extends AppCompatActivity {

    private LinearLayout llFileChooser;
    private TextView textViewCurrentPath;
    private File currentDir;
    private File[] currentDirFiles;

    private String lastDirOpenPath;
    private String topDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        llFileChooser = findViewById(R.id.llFileChooser);
        textViewCurrentPath = findViewById(R.id.textViewCurrentPath);
        ImageButton imageButtonBackToTopDir = findViewById(R.id.imageButtonBackToAppDir);
        imageButtonBackToTopDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get back to base app dir.
                refreshView(topDir);
            }
        });

        CheckBox checkBoxToggleExternal = findViewById(R.id.checkBoxToggleExternal);
        if (isExternalStorageAvailable()) {
            checkBoxToggleExternal.setVisibility(View.VISIBLE);
        }
        checkBoxToggleExternal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Reset opened dir on checked change.
                if (buttonView.isChecked() && isExternalStorageAvailable()) {
                    try {
                        lastDirOpenPath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath();
                    } catch (NullPointerException e) {
                        lastDirOpenPath = getFilesDir().getAbsolutePath();
                    }
                } else {
                    lastDirOpenPath = getFilesDir().getAbsolutePath();
                }
                topDir = lastDirOpenPath;
                refreshView(lastDirOpenPath);
            }
        });

        // Set safe topDir (always internal app storage)
        topDir = getFilesDir().getAbsolutePath();

        // Get lastDirOpenPath from MainActivity
        Intent intent = getIntent();
        lastDirOpenPath = intent.getStringExtra("lastDirOpenPath");

        // Load file list view.
        refreshView(lastDirOpenPath);
    }

    /**
     * Check if external app storage is available.
     * @return True if external app storage is available.
     */
    private boolean isExternalStorageAvailable() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) || (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY));
    }

    /**
     * Prepare items on scrollView.
     * @param selectedDir Absolute path to directory that is to be displayed.
     */
    private void refreshView(final String selectedDir) {
        llFileChooser.removeAllViews();
        textViewCurrentPath.setText(selectedDir);
        try {
            currentDir = new File(selectedDir);
            currentDirFiles = currentDir.listFiles();
            addPreviousButton(llFileChooser);
            for (int i = 1; i < currentDirFiles.length; i++) {
                if (currentDirFiles[i].isDirectory()) {
                    this.addButton("[DIR] " + currentDirFiles[i].getName(), llFileChooser, i);
                } else {
                    this.addButton(currentDirFiles[i].getName(), llFileChooser, i);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Add button to directory/file list in selected Linear Layout.
     * @param text Name to display on button.
     * @param ll LinearLayout to which button is added.
     * @param id Custom Id of button.
     */
    private void addButton(String text, LinearLayout ll, Integer id) {
        Button button = new Button(this);
        button.setText(text);
        button.setId(id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPath = currentDirFiles[v.getId()].getAbsolutePath();

                File tmp = new File(newPath);
                if (tmp.isDirectory()) {
                    lastDirOpenPath = newPath;
                    refreshView(lastDirOpenPath);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("lastDirOpenPath", lastDirOpenPath);
                    intent.putExtra("selectedDir", newPath);
                    setResult(RESULT_OK, intent);
                    FileChooser.super.onBackPressed();
                }
            }
        });
        ll.addView(button);
    }

    /**
     * Add parent folder in selected Linear Layout.
     * @param ll LinearLayout to which button is added.
     */
    private void addPreviousButton(LinearLayout ll) {
        Button button = new Button(this);
        button.setText("..");
        button.setId(View.generateViewId());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    lastDirOpenPath = Objects.requireNonNull(currentDir.getParentFile()).getAbsolutePath();
                } catch (NullPointerException e) {
                    Toast.makeText(FileChooser.this, "Parent directory does not exist.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                refreshView(lastDirOpenPath);
            }
        });
        ll.addView(button);
    }

    /**
     * Return data to previous activity.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("lastDirOpenPath", lastDirOpenPath);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
