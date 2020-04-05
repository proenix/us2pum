package pl.proenix.android.us2pum.pizza;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String ciastoTyp[];
    private String ciastoTypOdmiana[];
    private String ciastoTypCena[];

    private String ciastoRozmiar[];
    private String ciastoRozmiarOdmiana[];
    private String ciastoRozmiarCena[];

    private String skladnikiPodstawowe[];
    private String skladnikiPodstawoweOdmiana[];
    private String skladnikiPodstawoweCena[];

    private String skladnikiDodatkowe[];
    private String skladnikiDodatkoweOdmiana[];
    private String skladnikiDodatkoweCena[];

    private RadioGroup rBCiastoGroup;
    private RadioGroup rBSizeGroup;

    private RadioButton[] rBCiasto;
    private RadioButton[] rBSize;

    private CheckBox[] cBSklPodst;
    private CheckBox[] cBSklDod;

    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rBCiastoGroup = findViewById(R.id.rBCiastoGroup);
        rBCiasto = new RadioButton[2];
        rBCiasto[0] = findViewById(R.id.rBCiasto);
        rBCiasto[1] = findViewById(R.id.rBCiasto2);

        ciastoTyp = getResources().getStringArray(R.array.pizza_ciasto_array);
        ciastoTypOdmiana = getResources().getStringArray(R.array.pizza_ciasto_odmiana_array);
        ciastoTypCena = getResources().getStringArray(R.array.pizza_ciasto_cena_array);

        for (int i=0; i < rBCiasto.length; i++) {
            rBCiasto[i].setText(generateLabel(ciastoTyp[i], ciastoTypCena[i]));
        }

        rBSizeGroup = findViewById(R.id.rBSizeGroup);
        rBSize = new RadioButton[3];
        rBSize[0] = findViewById(R.id.rBSize);
        rBSize[1] = findViewById(R.id.rBSize2);
        rBSize[2] = findViewById(R.id.rBSize3);

        ciastoRozmiar = getResources().getStringArray(R.array.pizza_rozmiar_array);
        ciastoRozmiarOdmiana = getResources().getStringArray(R.array.pizza_rozmiar_odmiana_array);
        ciastoRozmiarCena = getResources().getStringArray(R.array.pizza_rozmiar_cena_array);

        for (int i=0; i < rBSize.length; i++) {
            rBSize[i].setText(generateLabel(ciastoRozmiar[i], ciastoRozmiarCena[i]));
        }

        cBSklPodst = new CheckBox[7];
        cBSklPodst[0] = findViewById(R.id.cBSklPodst);
        cBSklPodst[1] = findViewById(R.id.cBSklPodst2);
        cBSklPodst[2] = findViewById(R.id.cBSklPodst3);
        cBSklPodst[3] = findViewById(R.id.cBSklPodst4);
        cBSklPodst[4] = findViewById(R.id.cBSklPodst5);
        cBSklPodst[5] = findViewById(R.id.cBSklPodst6);
        cBSklPodst[6] = findViewById(R.id.cBSklPodst7);

        skladnikiPodstawowe = getResources().getStringArray(R.array.skladniki_podstawowe_array);
        skladnikiPodstawoweOdmiana = getResources().getStringArray(R.array.skladniki_podstawowe_odmiana_array);
        skladnikiPodstawoweCena = getResources().getStringArray(R.array.skladniki_podstawowe_cena_array);

        for (int i=0; i < cBSklPodst.length; i++) {
            cBSklPodst[i].setText(generateLabel(skladnikiPodstawowe[i], skladnikiPodstawoweCena[i]));
            cBSklPodst[i].setOnClickListener(this);
        }

        cBSklDod = new CheckBox[8];
        cBSklDod[0] = findViewById(R.id.cBSklDod);
        cBSklDod[1] = findViewById(R.id.cBSklDod2);
        cBSklDod[2] = findViewById(R.id.cBSklDod3);
        cBSklDod[3] = findViewById(R.id.cBSklDod4);
        cBSklDod[4] = findViewById(R.id.cBSklDod5);
        cBSklDod[5] = findViewById(R.id.cBSklDod6);
        cBSklDod[6] = findViewById(R.id.cBSklDod7);
        cBSklDod[7] = findViewById(R.id.cBSklDod8);

        skladnikiDodatkowe = getResources().getStringArray(R.array.skladniki_dodatkowe_array);
        skladnikiDodatkoweOdmiana = getResources().getStringArray(R.array.skladniki_dodatkowe_odmiana_array);
        skladnikiDodatkoweCena = getResources().getStringArray(R.array.skladniki_dodatkowe_cena_array);

        for (int i=0; i < cBSklDod.length; i++) {
            cBSklDod[i].setText(generateLabel(skladnikiDodatkowe[i], skladnikiDodatkoweCena[i]));
            cBSklDod[i].setOnClickListener(this);
        }

        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);
    }

    private String generateLabel(String label, String price) {
        if (price.equals("0.00")) {
            return label;
        }
        return label + " (+" + price + " PLN)";
    }

    private void generateOrder() {
        Double basePrice = 10.00;
        StringBuilder pizzaType = new StringBuilder(getString(R.string.orderedPart1));
        int podst = 0; // liczba skladnikow podstawowych
        int dod = 0; // liczba skladnikow dodatkowych
        for (int i=0; i < rBSize.length; i++) {
            if (rBSize[i].isChecked()) {
                basePrice += Double.parseDouble(ciastoRozmiarCena[i]);
                pizzaType.append(ciastoRozmiarOdmiana[i]);
            }
        }
        pizzaType.append(getString(R.string.orderedPart2));
        for (int i=0; i < rBCiasto.length; i++) {
            if (rBCiasto[i].isChecked()) {
                 basePrice += Double.parseDouble(ciastoTypCena[i]);
                 pizzaType.append(ciastoTypOdmiana[i]);
            }
        }
        pizzaType.append(getString(R.string.orderedPart3));
        for (int i=0; i < cBSklPodst.length; i++) {
            if (cBSklPodst[i].isChecked()) {
                basePrice += Double.parseDouble(skladnikiPodstawoweCena[i]);
                pizzaType.append(skladnikiPodstawoweOdmiana[i]).append(", ");
                podst += 1;
            }
        }
        for (int i=0; i < cBSklDod.length; i++) {
            if (cBSklDod[i].isChecked()) {
                basePrice += Double.parseDouble(skladnikiDodatkoweCena[i]);
                pizzaType.append(skladnikiDodatkoweOdmiana[i]).append(", ");
                dod += 1;
            }
        }
        pizzaType = new StringBuilder(pizzaType.substring(0, pizzaType.length() - 2)); // Remove last comma and space.
        pizzaType.append(getString(R.string.typCena, String.valueOf(basePrice)));

        if (podst < 3) {
            Toast.makeText(getApplicationContext(), R.string.min3stand, Toast.LENGTH_LONG).show();
            return;
        }
        if (dod > 2) {
            Toast.makeText(getApplicationContext(), R.string.max2dodatkowe, Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getApplicationContext(), pizzaType.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        // Count dodatkowe skladniki.
        int dod = 0;
        for (int i=0; i < cBSklDod.length; i++) {
            if (cBSklDod[i].isChecked()) {
                dod += 1;
            }
        }
        if (v.getId() == buttonSubmit.getId()) {
            generateOrder();
        }
        // Check for max number of allowed checkboxes and unset last checked checkbox if too many checked.
        for (int i=0; i < cBSklDod.length; i++) {
            if ((cBSklDod[i].getId() == v.getId()) && (dod > 2) && (cBSklDod[i].isChecked())) {
                cBSklDod[i].setChecked(false);
                Toast.makeText(getApplicationContext(), R.string.max2dodatkowe, Toast.LENGTH_LONG).show();
            }
        }
    }
}
