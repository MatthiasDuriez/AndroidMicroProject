package com.example.chat2021;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sp;
    TextInputEditText edtLogin;
    TextInputEditText edtPasse;
    CheckBox cbRemember;
    Button btnOK;
    SharedPreferences.Editor editor;
    ColorHandler colorHandler;
    private final String CAT = "LE4-SI";

    class JSONAsyncTask extends AsyncTask<String, Void, String> {
        // Params, Progress, Result

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(LoginActivity.this.CAT,"onPreExecute");
        }

        @Override
        protected String doInBackground(String... qs) {
            // String... : ellipse
            // Lors de l'appel, on fournit les arguments ?? la suite, s??par??s par des virgules
            // On r??cup??re ces arguments dans un tableau
            // pas d'interaction avec l'UI Thread ici
            Log.i(LoginActivity.this.CAT,"doInBackground");
            Log.i(LoginActivity.this.CAT,qs[0]);
            Log.i(LoginActivity.this.CAT,qs[1]);
            String result = requete(qs[0], qs[1]);
            Log.i(LoginActivity.this.CAT,result);
            String hash = "";
            //String hash="4e28dafe87d65cca1482d21e76c61a06";

            // TODO : ne traite pas les erreurs de connexion !

            try {

                JSONObject obR = new JSONObject(result);
                hash = obR.getString("hash");


                String res = "{\"promo\":\"2020-2021\",\"enseignants\":[{\"prenom\":\"Mohamed\",\"nom\":\"Boukadir\"},{\"prenom\":\"Thomas\",\"nom\":\"Bourdeaud'huy\"}]}";
                JSONObject ob = new JSONObject(res);
                String promo = ob.getString("promo");
                JSONArray profs = ob.getJSONArray("enseignants");
                JSONObject tom = profs.getJSONObject(1);
                String prenom = tom.getString("prenom");
                Log.i(LoginActivity.this.CAT,"promo:" + promo + " prenom:" + prenom);

                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .disableHtmlEscaping()
                        .setPrettyPrinting()
                        .create();

                String res2 = gson.toJson(ob);
                Log.i(LoginActivity.this.CAT,"chaine recue:" + res);
                Log.i(LoginActivity.this.CAT,"chaine avec gson:" + res2);

                Promo unePromo = gson.fromJson(res,Promo.class);
                Log.i(LoginActivity.this.CAT,unePromo.toString());



            } catch (JSONException e) {
                e.printStackTrace();
            }
            return hash;
        }

        protected void onPostExecute(String hash) {
            Log.i(LoginActivity.this.CAT,"onPostExecute");
            Log.i(LoginActivity.this.CAT,hash);
            LoginActivity.this.alerter(hash);


            Intent iVersChoixConv = new Intent(LoginActivity.this,ChoixConvActivity.class);
            Bundle bdl = new Bundle();
            bdl.putString("hash",hash);
            bdl.putInt("color",colorHandler.getBackgroundColor());
            bdl.putString("login",edtLogin.getText().toString());
            Log.i("LE4","Le login est :"+edtLogin.getText().toString());
            iVersChoixConv.putExtras(bdl);
            startActivity(iVersChoixConv);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        edtLogin = findViewById(R.id.login_edtLogin);
        edtPasse = findViewById(R.id.login_edtPasse);
        cbRemember = findViewById(R.id.login_cbRemember);
        btnOK = findViewById(R.id.login_btnOK);

        btnOK.setOnClickListener(this);

        colorHandler = new ColorHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sp.getInt("colorPreference", 0xffff0000)!=colorHandler.getBackgroundColor()){
            colorHandler.generateOther(sp.getInt("colorPreference", 0xffff0000));
            windowAdaptColor();
        }

        alerter("onResume called");
        // TODO: Au (re)chargement de l'activit??,
        // Lire les pr??f??rences partag??es
        if (sp.getBoolean("remember",false)) {
            // et remplir (si n??cessaire) les champs pseudo, passe, case ?? cocher
            cbRemember.setChecked(true);
            edtLogin.setText(sp.getString("login",""));
            edtPasse.setText(sp.getString("passe",""));
        }

        // V??rifier l'??tat du r??seau
        if (verifReseau()) {
            btnOK.setEnabled(true); // activation du bouton
        } else {
            btnOK.setEnabled(false); // d??sactivation du bouton
        }
    }

    private void windowAdaptColor() {
        LinearLayout linearLayout1 = findViewById(R.id.LinearLayout1);
        linearLayout1.setBackgroundColor(colorHandler.getBackgroundColor());
        btnOK.setBackgroundColor(colorHandler.getSecondColor());
        btnOK.setTextColor(colorHandler.getTextColor());
        TextView titre = findViewById(R.id.login_titre);
        titre.setTextColor(colorHandler.getComplementaryColor());
        cbRemember.setButtonTintList(ColorStateList.valueOf(colorHandler.getComplementaryColor()));//setButtonTintList is accessible directly on API>19
        cbRemember.setTextColor(colorHandler.getComplementaryColor());
    }

    @Override
    public void onClick(View v) {
        // Lors de l'appui sur le bouton OK
        // si case est coch??e, enregistrer les donn??es dans les pr??f??rences
        alerter("click sur OK");
        if (cbRemember.isChecked()) {
            editor.putBoolean("remember",true);
            editor.putString("login", edtLogin.getText().toString());
            editor.putString("passe", edtPasse.getText().toString());
            editor.commit();

        } else {
            editor.clear();
            editor.commit();
        }

        // On envoie une requete HTTP
        JSONAsyncTask jsonT = new JSONAsyncTask();
        jsonT.execute(sp.getString("urlData","http://tomnab.fr/chat-api/")+"authenticate",
                "user=" + edtLogin.getText().toString()
                        + "&password=" + edtPasse.getText().toString());

    }



    // Afficher les ??l??ments du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Utiliser menu.xml pour cr??er le menu (Pr??f??rences, Mon Compte)
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void alerter(String s) {
        Log.i(CAT,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        t.show();
    }

    // Gestionnaire d'??v??nement pour le menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings :
                alerter("Pr??f??rences");
                // Changer d'activit?? pour afficher PrefsActivity

                Intent change2Prefs = new Intent(this,PrefsActivity.class);
                startActivity(change2Prefs);
                break;
            case R.id.action_account :
                alerter("Compte");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean verifReseau()
    {
        // On v??rifie si le r??seau est disponible,
        // si oui on change le statut du bouton de connexion
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();

        String sType = "Aucun r??seau d??tect??";
        Boolean bStatut = false;
        if (netInfo != null)
        {

            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0)
            {
                bStatut = true;
                int netType= netInfo.getType();
                switch (netType)
                {
                    case ConnectivityManager.TYPE_MOBILE :
                        sType = "R??seau mobile d??tect??"; break;
                    case ConnectivityManager.TYPE_WIFI :
                        sType = "R??seau wifi d??tect??"; break;
                }

            }
        }

        alerter(sType);
        return bStatut;
    }
    public String requete(String urlData, String qs) {
        DataOutputStream dataout = null; // new:POST
        if (qs != null)
        {
            try {
                URL url = new URL(urlData); // new:POST
                Log.i(CAT,"url utilis??e : " + url.toString());
                HttpURLConnection urlConnection = null;
                urlConnection = (HttpURLConnection) url.openConnection();

                // new:POST
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setAllowUserInteraction(false);
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                dataout = new DataOutputStream(urlConnection.getOutputStream());
                dataout.writeBytes(qs);
                // new:POST

                InputStream in = null;
                in = new BufferedInputStream(urlConnection.getInputStream());
                String txtReponse = convertStreamToString(in);
                urlConnection.disconnect();
                return txtReponse;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "";
    }

    private String convertStreamToString(InputStream in) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}