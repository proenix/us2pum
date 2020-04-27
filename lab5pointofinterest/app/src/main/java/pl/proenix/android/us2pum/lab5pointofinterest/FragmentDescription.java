package pl.proenix.android.us2pum.lab5pointofinterest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDescription#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDescription extends Fragment {

    private View view;

    // the fragment initialization parameters
    private static final String PLACE_DESCRIPTION = "place_description";
    private static final String PLACE_IMAGE = "place_image";

    private String placeDescription;
    private Integer placeImage;

    public FragmentDescription() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param placeDescription String Place description.
     * @param placeImage Integer Resource id to show as image.
     * @return A new instance of fragment FragmentDescription.
     */
    public static FragmentDescription newInstance(String placeDescription, Integer placeImage) {
        FragmentDescription fragment = new FragmentDescription();
        Bundle args = new Bundle();
        args.putString(PLACE_DESCRIPTION, placeDescription);
        args.putInt(PLACE_IMAGE, placeImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeDescription = getArguments().getString(PLACE_DESCRIPTION);
            placeImage = getArguments().getInt(PLACE_IMAGE);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_description, container, false);

        TextView textViewPlaceDescription = view.findViewById(R.id.textViewPlaceDescription);
        textViewPlaceDescription.setText(placeDescription);

        ImageView imageViewPlacePhoto = view.findViewById(R.id.imageViewPlacePhoto);
        imageViewPlacePhoto.setImageResource(placeImage);

        return view;
    }
}
