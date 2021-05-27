package com.example.chat2021;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CAT = "LE4-SI";
    APIInterface apiService;
    String hash;
    ListMessage lm;
    EditText messageBody;
    Button okBtn;
    ColorHandler colorHandler;
    String currentLogin;
    String isActive;
    Menu Mmenu;
    int idConversation;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversation);

        messageBody = findViewById(R.id.conversation_edtMessage);
        okBtn = findViewById(R.id.conversation_btnOK);
        okBtn.setOnClickListener(this);
        colorHandler = new ColorHandler();

        // Récupération des informations du bundle
        Bundle bdl = this.getIntent().getExtras();
        colorHandler.generateOther(bdl.getInt("color"));
        currentLogin = bdl.getString("login");
        hash = bdl.getString("hash");
        isActive = bdl.getString("isActive");
        idConversation = Integer.parseInt(bdl.getString("conv"));

        apiService = APIClient.getClient().create(APIInterface.class);

        Call<ListMessage> call1 = apiService.doGetListMessage(hash, Integer.parseInt(bdl.getString("conv")));
        call1.enqueue(new Callback<ListMessage>() {
            @Override
            public void onResponse(Call<ListMessage> call, Response<ListMessage> response) {
                lm = response.body();

                mMessageRecycler = (RecyclerView) findViewById(R.id.recycler_gchat);
                mMessageAdapter = new MessageListAdapter(ConvActivity.this, lm,currentLogin,colorHandler.getBackgroundColor());

                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConvActivity.this);
                //linearLayoutManager.setStackFromEnd(true);

                mMessageRecycler.setLayoutManager(linearLayoutManager);
                mMessageRecycler.setAdapter(mMessageAdapter);

                mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount()-1);
                mMessageRecycler.setBackgroundColor(colorHandler.getBackgroundColor());
            }

            @Override
            public void onFailure(Call<ListMessage> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        windowAdaptColor();
    }

    private void windowAdaptColor() {
        ConstraintLayout conversationMainLayout = findViewById(R.id.conversationMainLayout);
        conversationMainLayout.setBackgroundColor(colorHandler.getBackgroundColor());
        RelativeLayout layout_gchat_chatbox = findViewById(R.id.layout_gchat_chatbox);
        layout_gchat_chatbox.setBackgroundColor(colorHandler.getBackgroundColor());
    }

    private void alerter(String s) {

        Log.i(CAT,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        t.show();
    }

    /**
     * Quand on clique sur le bouton "envoyer le message", s'il y a du contenu dans le champ texte
     * on ajoute le message dans la conversation
     * @param v
     */
    @Override
    public void onClick(View v) {

        String contenu = messageBody.getText().toString();

        if (contenu.length() > 0){
            Call<Message> call2 = apiService.doSetListMessage(hash, idConversation, contenu);
            call2.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Message newMessage = new Message(contenu,currentLogin);

                    mMessageAdapter.addItem(newMessage);
                    messageBody.setText("");
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {call.cancel();}
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Mmenu = menu;

        getMenuInflater().inflate(R.menu.display, menu);
        MenuItem item = menu.findItem(R.id.action_display);

        // Si la conversation est active au chargement du menu, on affiche un oeuil blanc,
        // sinon un oeil rouge
        if (isActive.equals("1"))
            item.setIcon(getDrawable(R.drawable.active));
        else
            item.setIcon(getDrawable(R.drawable.inactive));

        return true;
    }

    /**
     * Quand on clique sur l'oeil du menu, on switch l'état de la conversation
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switchDisplay();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Si la conversation était active, on inverse tous les états (oeil devient rouge, on la désactive en base...)
     * et inversement
     */
    private void switchDisplay() {
        MenuItem item = Mmenu.findItem(R.id.action_display);

        if (isActive.equals("1")) {

            Call<ResponseBody> call2 = apiService.doSetInactive(hash, idConversation);
            call2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    isActive = "0";

                    item.setIcon(getDrawable(R.drawable.inactive));
                    alerter("Conversation désactivée");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {call.cancel();}
            });

        }
        else {

            Call<ResponseBody> call2 = apiService.doSetActive(hash, idConversation);
            call2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    isActive = "1";

                    item.setIcon(getDrawable(R.drawable.active));
                    alerter("Conversation activée");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {call.cancel();}
            });
        }
    }
}