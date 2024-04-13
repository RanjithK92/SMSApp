package com.activities.sms;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SendSMSActivity extends AppCompatActivity {

    List<SMSMessage> allMessages = new ArrayList<>();
    ListView messageList;
    LinearLayout messageLayout;
    EditText message;
    ImageButton send;
    MessageAdapter adapter;
    String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        messageList = findViewById(R.id.messageList);
        messageLayout = findViewById(R.id.messageLayout);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);

        Gson gson = new Gson();
        allMessages = gson.fromJson(getIntent().getStringExtra("MESSAGES"),
                new TypeToken<ArrayList<SMSMessage>>() {
                }.getType());
        phoneNo = getIntent().getStringExtra("SENDER");

        allMessages.sort(Comparator.comparingLong(SMSMessage::getDate));
        setTitle(phoneNo);

        messageList.post(() -> {
            showSMS();
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                messageLayout.setEnabled(false);
                messageLayout.setAlpha(0.5f);
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 666);
            } else {
                messageLayout.setEnabled(true);
                messageLayout.setAlpha(1f);
            }
        });

        send.setOnClickListener(view -> {
            if (!message.getText().toString().trim().isEmpty()) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    String msg = GlobalData.encrypt(message.getText().toString().trim());
                    ArrayList<String> msgArray = smsManager.divideMessage(msg);
                    smsManager.sendMultipartTextMessage(phoneNo, null, msgArray, null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent",
                            Toast.LENGTH_LONG).show();
                    SMSMessage m = new SMSMessage(
                            msg, phoneNo, System.currentTimeMillis(), false, 2, -1, ""
                    );
                    allMessages.add(m);
                    adapter.notifyDataSetChanged();
                    message.setText("");
                    View v = this.getCurrentFocus();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied, unable to send SMS", Toast.LENGTH_SHORT).show();
            messageLayout.setEnabled(false);
            messageLayout.setAlpha(0.5f);
        } else {
            messageLayout.setEnabled(true);
            messageLayout.setAlpha(1f);
        }
    }

    private void showSMS() {
        adapter = new MessageAdapter(this, messageList, allMessages);
        messageList.setAdapter(adapter);
    }
}