package com.activities.sms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends BaseAdapter {

    Context context;
    private final LayoutInflater inflater;
    ListView listView;
    List<SMSMessage> messages;

    public MessageAdapter(Context context, ListView listView, List<SMSMessage> messages) {
        this.context = context;
        this.listView = listView;
        inflater = LayoutInflater.from(this.listView.getContext());
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderMessage holder;
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.message_item, null);
            holder = new ViewHolderMessage();
            holder.date = convertView.findViewById(R.id.date);
            holder.message = convertView.findViewById(R.id.message);
            holder.messageView = convertView.findViewById(R.id.messageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderMessage) convertView.getTag();
        }

        if (messages.get(position).getType() == 1) {
            holder.messageView.setBackgroundResource(R.drawable.border_receive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.START;
            params.rightMargin = 50;
            holder.messageView.setLayoutParams(params);
        } else {
            holder.messageView.setBackgroundResource(R.drawable.border_send);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            params.leftMargin = 50;
            holder.messageView.setLayoutParams(params);
        }

        holder.message.setText(GlobalData.decrypt(Objects.requireNonNull(messages.get(position)).getMessage()));
        holder.date.setText(Objects.requireNonNull(messages.get(position)).getDateString());
        return convertView;
    }

    static class ViewHolderMessage {
        TextView date, message;
        LinearLayout messageView;
    }
}
