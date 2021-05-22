package com.example.chat2021;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CAT = "LE4-SI";
    ScrollView conversation;
    LinearLayout conversationLayout;
    APIInterface apiService;
    String hash;
    ListMessage lm;
    EditText messageBody;
    Button okBtn;
    int idConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversation);
        conversation = findViewById(R.id.conversation_svMessages);
        conversationLayout = (LinearLayout) findViewById(R.id.conversation_svLayoutMessages);

        messageBody = findViewById(R.id.conversation_edtMessage);
        okBtn = findViewById(R.id.conversation_btnOK);

        okBtn.setOnClickListener(this);

        Bundle bdl = this.getIntent().getExtras();
        hash = bdl.getString("hash");
        apiService = APIClient.getClient().create(APIInterface.class);
        idConversation = Integer.parseInt(bdl.getString("conv"));
        Call<ListMessage> call1 = apiService.doGetListMessage(hash, Integer.parseInt(bdl.getString("conv")));
        call1.enqueue(new Callback<ListMessage>() {
            @Override
            public void onResponse(Call<ListMessage> call, Response<ListMessage> response) {
                lm = response.body();
                for(Message m : lm.messages) {
                    TextView message = new TextView(ConvActivity.this);
                    message.setText(m.contenu);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    message.setLayoutParams(params);
                    conversationLayout.addView(message);
                }

                Log.i(CAT,lm.toString());
            }

            @Override
            public void onFailure(Call<ListMessage> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void alerter(String s) {
        Log.i(CAT,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        t.show();
    }

    @Override
    public void onClick(View v) {
        String contenu = messageBody.getText().toString();

        if (contenu.length() > 0){
            Call<Message> call2 = apiService.doSetListMessage(hash, idConversation, contenu);
            call2.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    TextView message = new TextView(ConvActivity.this);
                    message.setText(contenu);
                    conversationLayout.addView(message);
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {

                }
            });
        }

    }
}