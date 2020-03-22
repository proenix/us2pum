package pl.proenix.android.us2pum.lab1flashlight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    // Holds current value for transparency;
    int transparency = 0;

    // Holds current value for colors;
    int red, blue, green = 0;

    // Components
    Button btnWhite, btnRed, btnBlue, btnGreen;
    SeekBar seekBar;
    View vBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWhite = (Button) findViewById(R.id.bWhite);
        btnWhite.setOnClickListener(this);
        btnRed = (Button) findViewById(R.id.bRed);
        btnRed.setOnClickListener(this);
        btnBlue = (Button) findViewById(R.id.bBlue);
        btnBlue.setOnClickListener(this);
        btnGreen = (Button) findViewById(R.id.bGreen);
        btnGreen.setOnClickListener(this);

        vBackground = (View) findViewById(R.id.vBackground);

        seekBar = (SeekBar) findViewById(R.id.sbrOpacity);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBackgroundColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Sets color of background based on current colors and transparency value.
     */
    private void setBackgroundColor() {
        transparency = 255 - seekBar.getProgress();
        int backgroundColor = Color.argb(transparency, red, green, blue);
        ColorStateList colorStateList = ColorStateList.valueOf(backgroundColor);
        vBackground.setBackgroundTintList(colorStateList);
    }

    /**
     * Reset BackgroundTint color of buttons to none.
     */
    private void resetButtonFocus() {
        btnWhite.setBackgroundTintList(null);
        btnRed.setBackgroundTintList(null);
        btnGreen.setBackgroundTintList(null);
        btnBlue.setBackgroundTintList(null);
    }

    @Override
    public void onClick(View v) {
        resetButtonFocus();
        if (v.getId() == btnWhite.getId()) {
            // From Black to White
            red = 0;
            blue = 0;
            green = 0;
            ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY);
            v.setBackgroundTintList(colorStateList);
        }
        if (v.getId() == btnRed.getId()) {
            red = 255;
            blue = 0;
            green = 0;
            ColorStateList colorStateList = ColorStateList.valueOf(Color.RED);
            v.setBackgroundTintList(colorStateList);
        }
        if (v.getId() == btnBlue.getId()) {
            red = 0;
            blue = 255;
            green = 0;
            ColorStateList colorStateList = ColorStateList.valueOf(Color.BLUE);
            v.setBackgroundTintList(colorStateList);
        }
        if (v.getId() == btnGreen.getId()) {
            red = 0;
            blue = 0;
            green = 255;
            ColorStateList colorStateList = ColorStateList.valueOf(Color.GREEN);
            v.setBackgroundTintList(colorStateList);
        }
        setBackgroundColor();
    }
}
