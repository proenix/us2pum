package pl.proenix.android.us2pum.lab5pointofinterest;

import android.view.View;

/**
 * Place object representation.
 */
public class Place {
    private Integer id; // TODO: 27/04/2020 ID of fetched object from API.
    private Integer city_id; // TODO: 27/04/2020 ID of parent object form API.

    private String name;
    private String description;

    private Integer imageResource;

    private Double longitude;
    private Double latitude;

    public String getName() {
        return name;
    }

    public String getDescription() {
        // TODO: 27/04/2020 Return description of place.
        return "Lorem ipsum dolor sit amet, eget ac. A dolor, lacinia adipiscing enim, fringilla imperdiet a. Erat est dis, nonummy adipiscing ultrices, neque sed quis. Modi class tortor. Quam non per, quisque faucibus dui, auctor ac. Consectetur ut. Eros mauris, nonummy scelerisque, facilisi mauris et. Nulla placerat, cras pellentesque dolor, libero dui. Sodales volutpat, donec eu eu, ligula nunc. Phasellus elit viverra, proin pede.\n" +
                "\n" +
                "Mauris in ante, aliquam nunc vestibulum, nulla et urna. Eleifend nec nulla. Proin eleifend. Turpis faucibus platea. Nascetur wisi vestibulum, vivamus vel. Ac sociosqu nec, dui mollitia, vel rhoncus.\n" +
                "\n" +
                "Faucibus in sapien, donec nec, euismod elit. Quis lacus, reprehenderit eget amet. Aliquam vestibulum. Consequat at volutpat, cras elementum molestie, vitae erat adipiscing. Proin mi, vulputate vel, fermentum parturient. Mauris ultricies, libero mauris, aliquam volutpat eu. Ut sed, sed scelerisque urna, sit risus. Ipsum taciti, luctus mattis interdum, elit tortor. Tellus urna pulvinar, dictum vitae id. Magna leo fringilla. Ullamcorper curabitur. Id phasellus fringilla, proin accumsan, nullam amet risus.\n" +
                "\n" +
                "Nunc lectus et, egestas blandit. Vitae justo. Massa at ultricies. Volutpat risus, nibh nec. Sociosqu rutrum vestibulum. Lacus amet aliqua, maecenas vitae vel. Lorem orci felis\n" +
                "\n" +
                "Vestibulum a. Lectus consectetuer porttitor. Porta consectetuer. Phasellus egestas quis. Dignissim nec ut. Dapibus tempus sapien, ipsum egestas neque, commodo ipsum. Sociosqu maecenas vitae.\n" +
                "\n" +
                "Nibh purus lorem, sit libero eget, velit ac. In purus. Elit ultrices facilis. Risus commodo, sapien vel dui, integer libero fusce. Vel sapien proin, id suspendisse ut, venenatis justo ipsum. Mi lectus imperdiet, enim quam, tristique auctor. Veritatis convallis. Leo amet sem, etiam posuere pede, class auctor. Pede ornare venenatis, metus lacus. Culpa sagittis, nam vestibulum sit, lobortis facilisis aliquam. Metus et suscipit. Vestibulum quibusdam lacus, sapien curabitur.\n" +
                "\n" +
                "Vestibulum mauris, mi pellentesque. Elit arcu arcu, eget amet ipsum, proin id. Nascetur tristique. Risus eu, nec vestibulum, lectus tincidunt lacus. Suspendisse eget, a integer. Id in lacinia, ipsum nulla ac, viverra arcu amet. Velit posuere amet, in tortor id.";
    }

    public Integer getImageResource() {
        return imageResource;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Place(Integer id, Integer city_id, String name, String description, Integer imageResource, Double longitude, Double latitude) {
        this.id = id;
        this.city_id = city_id;
        this.name = name;
        this.description = description;
        this.imageResource = imageResource;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
