package com.example.chat2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
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

        Bundle bdl = this.getIntent().getExtras();

        colorHandler = new ColorHandler();
        colorHandler.generateOther(bdl.getInt("color"));

        currentLogin = bdl.getString("login");

        hash = bdl.getString("hash");
        apiService = APIClient.getClient().create(APIInterface.class);
        idConversation = Integer.parseInt(bdl.getString("conv"));
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
                Log.i(CAT,lm.toString());
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

    @Override
    public void onClick(View v) {
        String contenu = messageBody.getText().toString();
        Log.i(CAT,"Oui : "+contenu);
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
                public void onFailure(Call<Message> call, Throwable t) {

                }
            });
        }

    }

}