package com.example.chat2021;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private ListMessage mMessageList;
    private String login;
    private ColorHandler colorHandler;
    public MessageListAdapter(Context context, ListMessage messageList, String CurrentLogin, int backGroundColor) {
        mContext = context;
        mMessageList = messageList;
        login = CurrentLogin;
        colorHandler = new ColorHandler();
        colorHandler.generateOther(backGroundColor);
    }

    public void addItem(Message message) {
        mMessageList.messages.add(message);
        notifyDataSetChanged();
    }

    // Inflates the appropriate layout according to the ViewType.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.own_message, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_message, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.messages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.messages.size();
    }
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.messages.get(position);
        Log.i("LE4","Login : "+login+ " et login mess : "+message.getAuteur() + "et message = " + message.getContenu());
        if (message.getAuteur().equals(login)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText;
        CardView card;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
            nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
            card = itemView.findViewById(R.id.card_gchat_message_other);
        }

        void bind(Message message) {
            messageText.setText(message.getContenu());
            nameText.setText(message.getAuteur());
            messageText.setTextColor(colorHandler.getTextColor());
            nameText.setTextColor(colorHandler.getComplementaryColor());
            card.setCardBackgroundColor(colorHandler.getSecondColor());
        }
    }
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        CardView card;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            card = itemView.findViewById(R.id.card_gchat_message_me);
        }

        void bind(Message message) {
            messageText.setText(message.getContenu());
            messageText.setTextColor(Color.BLACK);
            if (colorHandler.isColorDark(colorHandler.getComplementaryColor()))
                messageText.setTextColor(Color.WHITE);
            card.setCardBackgroundColor(colorHandler.getComplementaryColor());
        }
    }

}
