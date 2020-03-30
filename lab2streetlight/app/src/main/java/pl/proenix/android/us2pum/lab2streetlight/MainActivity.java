package pl.proenix.android.us2pum.lab2streetlight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonRed, buttonYellow, buttonGreen;
    ImageView imageViewLightTop, imageViewLightMiddle, imageViewLightBottom;

    /*
     State of light.
     Circle through light states.
     0 -> uninitialized all actions allowed
     1 -> red -> only yellow allowed
     2 -> red & yellow -> only green allowed
     3 -> green -> only yellow allowed
     4 -> yellow -> only red allowed (and return to state 1)
    */
    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonGreen = (Button) findViewById(R.id.buttonGreen);
        buttonGreen.setOnClickListener(this);
        buttonYellow = (Button) findViewById(R.id.buttonYellow);
        buttonYellow.setOnClickListener(this);
        buttonRed = (Button) findViewById(R.id.buttonRed);
        buttonRed.setOnClickListener(this);

        imageViewLightTop = (ImageView) findViewById(R.id.imageViewLightTop);
        imageViewLightMiddle = (ImageView) findViewById(R.id.imageViewLightMiddle);
        imageViewLightBottom = (ImageView) findViewById(R.id.imageViewLightBottom);
    }

    /*
     Change Light Visualisation based on state provided.
     @param int state State to change to.
     */
    protected void changeLightState(int state) {
        switch (state) {
            case 0:
                imageViewLightTop.setImageResource(R.drawable.light_off);
                imageViewLightMiddle.setImageResource(R.drawable.light_off);
                imageViewLightBottom.setImageResource(R.drawable.light_off);
                break;
            case 1:
                imageViewLightTop.setImageResource(R.drawable.red_on);
                imageViewLightMiddle.setImageResource(R.drawable.light_off);
                break;
            case 2:
                imageViewLightMiddle.setImageResource(R.drawable.yellow_on);
                break;
            case 3:
                imageViewLightTop.setImageResource(R.drawable.light_off);
                imageViewLightMiddle.setImageResource(R.drawable.light_off);
                imageViewLightBottom.setImageResource(R.drawable.green_on);
                break;
            case 4:
                imageViewLightBottom.setImageResource(R.drawable.light_off);
                imageViewLightMiddle.setImageResource(R.drawable.yellow_on);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == buttonRed.getId()) {
            if(state == 4) {
                state = 1;
                changeLightState(state);
            } else if(state == 0) {
                state = 1;
                changeLightState(state);
            } else {
                Toast.makeText(getApplicationContext(), R.string.LightChangeNotAllowed, Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == buttonYellow.getId()) {
            if(state == 1 || state == 3) {
                state++; // Set state 2 or 4 respectively.
                changeLightState(state);
            } else if(state == 0) {
                state = 4;
                changeLightState(state);
            } else {
                Toast.makeText(getApplicationContext(), R.string.LightChangeNotAllowed, Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == buttonGreen.getId()) {
            if(state == 2) {
                state++;
                changeLightState(state);
            } else if(state == 0) {
                state = 3;
                changeLightState(state);
            } else {
                Toast.makeText(getApplicationContext(), R.string.LightChangeNotAllowed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
