package pl.proenix.android.us2pum.lab4readaloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class FileChooser extends AppCompatActivity {

    private LinearLayout llFileChooser;
    private TextView textViewCurentPath;
    private File currentDir;
    private File[] currentDirFiles;
    private String lastDirOpenPath;

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        llFileChooser = findViewById(R.id.llFileChooser);
        textViewCurentPath = findViewById(R.id.textViewCurrentPath);

        // Get lastDirOpenPath from MainActivity
        Intent intent = getIntent();
        lastDirOpenPath = intent.getStringExtra("lastDirOpenPath");

        // Load file list view.
        refreshView(lastDirOpenPath);
    }

    /**
     * Prepare items on scrollView.
     * @param selectedDir Absolute path to directory that is to be displayed.
     */
    private void refreshView(final String selectedDir) {
        // TODO: 18/04/2020 Add switcher between external and internal storage.
        try {
            llFileChooser.removeAllViews();
            textViewCurentPath.setText(lastDirOpenPath);

            currentDir = new File(selectedDir);
            currentDirFiles = currentDir.listFiles();
            File file;

            back = new Button(this);
            back.setText("..");
            back.setId(View.generateViewId());
            llFileChooser.addView(back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FileChooser.this, "..", Toast.LENGTH_SHORT).show();
                    try {
                        // TODO: 18/04/2020 Do not go below current app directory.
                        String newPath = currentDir.getParentFile().getAbsolutePath();
                        lastDirOpenPath = newPath;
                    } catch (NullPointerException e) {
                        Toast.makeText(FileChooser.this, "Parent does not exist.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    refreshView(lastDirOpenPath);
                }
            });
            for (int i = 1; i < currentDirFiles.length; i++) {
                file = currentDirFiles[i];

                Button et = new Button(this);
                et.setId(i);
                if (file.isDirectory()) {
                    et.setText("[DIR] " + currentDirFiles[i].getName());
                } else {
                    et.setText(file.getName());
                }
                et.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newPath = currentDirFiles[v.getId()].getAbsolutePath();
                        // Check if selected Path is Directory
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
                llFileChooser.addView(et);
            }
        } catch (Exception e) {
            Log.e("FileManipulation", lastDirOpenPath +"  "+ e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("lastDirOpenPath", lastDirOpenPath);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}