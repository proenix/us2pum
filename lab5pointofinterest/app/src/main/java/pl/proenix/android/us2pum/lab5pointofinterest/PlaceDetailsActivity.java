package pl.proenix.android.us2pum.lab5pointofinterest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class PlaceDetailsActivity extends AppCompatActivity {

    TextView textViewTitle;
    int city_id;
    int place_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        // Get data from intent.
        Intent intent = getIntent();
        city_id = intent.getIntExtra("city_id", -1);
        place_id = intent.getIntExtra("place_id", -1);

        textViewTitle = findViewById(R.id.textViewPlaceName);
        textViewTitle.setText(String.format("%s - %s", MainActivity.cities[city_id].getName(), MainActivity.cities[city_id].getPlace(place_id).getName()));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             * Load fragment depending on tab clicked.
             * @param tab Tab clicked.
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                if (tabPosition == 0) {
                    loadDescription(MainActivity.cities[city_id].getPlace(place_id));
                } else {
                    loadMap(MainActivity.cities[city_id].getPlace(place_id));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Load description at start
        loadDescription(MainActivity.cities[city_id].getPlace(place_id));
    }

    /**
     * Load fragment with description.
     * @param place Place object.
     */
    public void loadDescription(Place place) {
        loadFragment(FragmentDescription.newInstance(place.getDescription(), place.getImageResource()));
    }

    /**
     * Load fragment with Google Maps.
     * @param place Place object.
     */
    public void loadMap(Place place) {
        loadFragment(FragmentMap.newInstance(place.getLongitude(), place.getLatitude(), place.getName()));
    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        transaction.replace(R.id.frameLayout, fragment);
        //transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }
}
