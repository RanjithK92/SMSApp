package com.activities.sms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SenderAdapter extends BaseAdapter {

    Context context;
    private final LayoutInflater inflater;
    ListView listView;
    Map<String, List<SMSMessage>> messages;
    List<String> keys;

    public SenderAdapter(Context context, ListView listView, Map<String, List<SMSMessage>> messages, List<String> keys) {
        this.context = context;
        this.listView = listView;
        inflater = LayoutInflater.from(this.listView.getContext());
        this.messages = messages;
        this.keys = keys;
    }

    @Override
    public int getCount() {
        return keys.size();
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.sender_item, null);
            holder = new ViewHolder();
            holder.sender = convertView.findViewById(R.id.sender);
            holder.date = convertView.findViewById(R.id.date);
            holder.message = convertView.findViewById(R.id.message);
            convertView.setTag(R.id.holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.holder);
        }

        holder.sender.setText(keys.get(position));
        List<SMSMessage> m = messages.get(keys.get(position));
        if (m != null && m.size() > 0) {
            m.sort(Comparator.comparingLong(SMSMessage::getDate));
            holder.message.setText(GlobalData.decrypt(m.get(m.size() - 1).getMessage()));
            holder.date.setText(m.get(m.size() - 1).getDateString());
        } else {
            holder.message.setText("");
            holder.date.setText("");
        }

        convertView.setTag(R.id.item_position, position);
        convertView.setOnClickListener(view -> {
            int p = (int) view.getTag(R.id.item_position);
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(
                    messages.get(keys.get(p)), new TypeToken<List<SMSMessage>>() {
                    }.getType());
            Intent intent = new Intent(context, SendSMSActivity.class);
            intent.putExtra("MESSAGES", jsonElement.toString());
            intent.putExtra("SENDER", keys.get(p));
            context.startActivity(intent);
        });

        return convertView;
    }

    static class ViewHolder {
        TextView sender, date, message;
    }
}
