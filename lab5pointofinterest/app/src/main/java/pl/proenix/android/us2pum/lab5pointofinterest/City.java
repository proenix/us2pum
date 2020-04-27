package pl.proenix.android.us2pum.lab5pointofinterest;

import android.view.View;

/**
 * City object representation.
 */
public class City {
    private Integer id; // TODO: 27/04/2020 ID of fetched object from API.s

    private String name;
    private Place[] places;

    Place[] getPlaces() {
        return places;
    }

    /**
     * Get Place by its index in places array.
     * @param index Integer index.
     * @return Place object.
     */
    Place getPlace(int index) {
        return places[index];
    }

    public String getName() {
        return name;
    }

    City(Integer id, String name, Place[] places) {
        this.id = id;
        this.name = name;
        this.places = places;
    }
}
