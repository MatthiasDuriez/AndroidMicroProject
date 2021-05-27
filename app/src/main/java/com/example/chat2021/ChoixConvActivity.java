package com.example.chat2021;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.transition.Slide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChoixConvActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CAT = "LE4-SI";
    APIInterface apiService;
    String hash;
    AutoCompleteTextView listeConv;
    ListConversation lc;
    Button btnOK;
    ColorHandler colorHandler;
    String currentLogin;
    CheckBox displayInactive;
    String isActive;
    int idItemSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);

        idItemSelected = -1;
        listeConv = findViewById(R.id.choixConversation_choixConv);
        btnOK = findViewById(R.id.choixConversation_btnOK);
        btnOK.setOnClickListener(this);

        // Récupération des informations du bundle
        Bundle bdl = this.getIntent().getExtras();
        hash = bdl.getString("hash");
        currentLogin = bdl.getString("login");

        // Récupération et appliquation de la fonction de la checkbox permettant de choisir l'affichage
        // des conversations activées ou désactivées
        displayInactive = findViewById(R.id.display_inactiveConv);
        displayInactive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Quand l'état de la CB change, on appelle getConv() avec en paramètre un boolean
                // indiquant si on vuet les conversations inactives ou non
                if (isChecked)
                    getConv(true);
                else
                    getConv(false);
            }
        });

        colorHandler = new ColorHandler();
        colorHandler.generateOther(bdl.getInt("color"));

        getConv(false);
    }

    /**
     * Fonction récupère les conversations selon le paramètre (active ou inactive) et les insère
     * dans le spinner.
     * @param active
     */
    protected void getConv(boolean active) {

        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ListConversation> call1;

        // Récupération des conversation active ou inactive
        if (active)
            call1 = apiService.doGetListConversationInactive(hash);
        else
            call1 = apiService.doGetListConversationActive(hash);

        call1.enqueue(new Callback<ListConversation>() {
            @Override
            public void onResponse(Call<ListConversation> call, Response<ListConversation> response) {
                lc = response.body();

                List<String> convArray =  new ArrayList<String>();
                List<Integer> idArray = new ArrayList<Integer>();

                // Récupération des éléments qui nous intéresse dans un objet conversation (thème et id)
                for(Conversation c : lc.conversations) {
                    convArray.add(c.theme);
                    idArray.add(Integer.parseInt(c.id));
                }

                // Adaptateur et ajout des thèmes dans la dropdown list
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChoixConvActivity.this, android.R.layout.simple_dropdown_item_1line, convArray);
                listeConv.setAdapter(adapter);
                listeConv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    // Quand on clique sur une conversation dans la liste, on récupère son id et
                    // le boolean indiquant si elle est active ou non
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                        idItemSelected = idArray.get(position);
                        isActive = lc.conversations.get(position).getIsActive();

                        alerter("conversation sélectionnée : " + Integer.toString(idArray.get(position)));
                    }
                });
            }

            @Override
            public void onFailure(Call<ListConversation> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        windowAdaptColor();

        // Quand on revient sur le menu des choix de conversations (après un retour en arrière),
        // on remet à jour la liste des conversations pour prendre en compte l'activation ou la
        // désactivation de cette qui était selectionnée.
        getConv(false);
        displayInactive.setChecked(false);
    }

    private void windowAdaptColor() {
        LinearLayout linearLayout1 = findViewById(R.id.LinearLayout1);
        linearLayout1.setBackgroundColor(colorHandler.getBackgroundColor());

        btnOK.setBackgroundColor(colorHandler.getSecondColor());
        btnOK.setTextColor(colorHandler.getTextColor());

        TextView titre = findViewById(R.id.choixConversation_titre);
        titre.setTextColor(colorHandler.getComplementaryColor());

    }

    @Override
    public void onClick(View v) {

        if (idItemSelected == -1){
            alerter("Veuillez séléctionner une conversation");
            return;
        }

        alerter("Click sur OK Conv");

        Intent change2Conv = new Intent(this,ConvActivity.class);

        Bundle bdl = new Bundle();
        // Conversation conv = (Conversation) listeConv.getSelectedItem();
        bdl.putString("conv", Integer.toString(idItemSelected));
        bdl.putString("hash", hash);
        bdl.putInt("color",colorHandler.getBackgroundColor());
        bdl.putString("login",currentLogin);
        bdl.putString("isActive", isActive);

        change2Conv.putExtras(bdl);

        alerter("t la"+hash+" "+Integer.toString(idItemSelected));

        startActivity(change2Conv);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void alerter(String s) {

        Log.i(CAT,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        t.show();
    }
}
