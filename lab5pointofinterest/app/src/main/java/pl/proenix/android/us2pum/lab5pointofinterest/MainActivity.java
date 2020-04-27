package pl.proenix.android.us2pum.lab5pointofinterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    LinearLayout scrollViewPlaces;
    LinearLayout linearLayoutPlaceList;

    public static City[] cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sample data set
        // TODO: 27/04/2020 Fetch Data from external API
        cities = new City[]{
                new City(1, "Bielsko-Biała", new Place[]{
                        new Place(1,1,"Zamek Sułkowskich", "", R.drawable.sample, 49.8216338,19.0442456),
                        new Place(2,1,"Teatr Polski", "", R.drawable.sample,49.8211129,19.0449966),
                        new Place(3,1,"Bielsko-Biała Główna", "", R.drawable.sample,49.82877,19.0456071),
                }),
                new City(2, "Cieszyn", new Place[]{
                        new Place(4,2,"Zamek Cieszyn", "", R.drawable.sample,49.7505583,18.6271512),
                        new Place(5,2,"Rotunda pw. św. Mikołaja", "", R.drawable.sample,49.7511398,18.6255786),
                }),
        };

        scrollViewPlaces = findViewById(R.id.scrollViewPlaces);
        // Loop through Cities and add to scrollView
        for (int i=0; i < cities.length; i++) {
            final View placesRoot = getLayoutInflater().inflate(R.layout.place_root, null);
            placesRoot.setId(View.generateViewId());

            TextView textViewCityName = placesRoot.findViewById(R.id.textViewPlaceName);
            textViewCityName.setText(cities[i].getName());

            linearLayoutPlaceList = placesRoot.findViewById(R.id.linearLayoutPlaceList);
            scrollViewPlaces.addView(placesRoot);

            // Loop through Places related to City
            Place[] places = cities[i].getPlaces();
            if (places.length > 0) {
                for (int j = 0; j < places.length; j++) {
                    final View placeRow = getLayoutInflater().inflate(R.layout.place_row, null);
                    placeRow.setId(View.generateViewId());
                    placeRow.setTag(i + ";" + j);

                    TextView textViewPlaceName = placeRow.findViewById(R.id.textViewPlaceName);
                    ImageView imageViewPlaceThumb = placeRow.findViewById(R.id.imageViewPlaceThumb);

                    textViewPlaceName.setText(places[j].getName());
                    imageViewPlaceThumb.setImageResource(places[j].getImageResource());

                    placeRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] ids = v.getTag().toString().split(";",2);

                            Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                            intent.putExtra("city_id", Integer.valueOf(ids[0]));
                            intent.putExtra("place_id", Integer.valueOf(ids[1]));
                            startActivity(intent);
                        }
                    });
                    linearLayoutPlaceList.addView(placeRow);
                }
            }
        }

    }
}
