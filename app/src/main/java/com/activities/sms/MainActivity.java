package com.activities.sms;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    List<SMSMessage> allMessages = new ArrayList<>();
    Map<String, List<SMSMessage>> groupedMessages = new HashMap<>();
    List<String> keys = new ArrayList<>();
    ListView senderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senderList = findViewById(R.id.senderList);

        findViewById(R.id.newMessage).setOnClickListener(view -> {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                launcher.launch(i);
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 777);
            }
        });
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Uri contactUri = result.getData().getData();
                    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String number = cursor.getString(numberIndex);
                        startNewMessage(number);
                    }
                    assert cursor != null;
                    cursor.close();
                }
            });

    private void startNewMessage(String number) {
        List<SMSMessage> msg = new ArrayList<>();
        if (groupedMessages.containsKey(number)) {
            msg = groupedMessages.get(number);
        }
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(
                msg, new TypeToken<List<SMSMessage>>() {
                }.getType());
        Intent intent = new Intent(this, SendSMSActivity.class);
        intent.putExtra("MESSAGES", jsonElement.toString());
        intent.putExtra("SENDER", number);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senderList.post(this::checkPermission);
    }

    private void checkPermission() {
        if (checkSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            readSMS();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS}, 666);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 666) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readSMS();
            } else {
                Toast.makeText(this, "Permission denied, unable to read SMS", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 777) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                launcher.launch(i);
            } else {
                Toast.makeText(this, "Permission denied, unable to read Contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readSMS() {
        allMessages = new ArrayList<>();
        allMessages.addAll(readMessages("inbox"));
        allMessages.addAll(readMessages("sent"));

        //noinspection ResultOfMethodCallIgnored
        allMessages.stream()
                .sorted(Comparator.comparingLong(SMSMessage::getDate))
                .collect(Collectors.groupingBy(SMSMessage::getSender))
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        groupedMessages = new HashMap<>();
        keys = new ArrayList<>();
        for (SMSMessage message : allMessages) {
            String sender = message.getSender();
            if (!groupedMessages.containsKey(sender)) {
                groupedMessages.put(sender, new ArrayList<>());
                keys.add(sender);
            }
            Objects.requireNonNull(groupedMessages.get(sender)).add(message);
        }
        senderList.setAdapter(new SenderAdapter(this, senderList, groupedMessages, keys));
    }

    private List<SMSMessage> readMessages(String type) {
        List<SMSMessage> messages = new ArrayList<>();
        Cursor cursor = this.getContentResolver().query(
                Uri.parse("content://sms/" + type),
                null,
                null,
                null,
                null
        );
        if (cursor != null) {
            try {
                int indexMessage = cursor.getColumnIndex("body");
                int indexSender = cursor.getColumnIndex("address");
                int indexDate = cursor.getColumnIndex("date");
                int indexRead = cursor.getColumnIndex("read");
                int indexType = cursor.getColumnIndex("type");
                int indexThread = cursor.getColumnIndex("thread_id");
                int indexService = cursor.getColumnIndex("service_center");

                while (cursor.moveToNext()) {
                    messages.add(
                            new SMSMessage(
                                    cursor.getString(indexMessage),
                                    cursor.getString(indexSender),
                                    cursor.getLong(indexDate),
                                    Boolean.parseBoolean(cursor.getString(indexRead)),
                                    cursor.getInt(indexType),
                                    cursor.getInt(indexThread),
                                    cursor.getString(indexService) != null ? cursor.getString(indexService) : ""
                            )
                    );
                }
            } finally {
                cursor.close();
            }
        }
        return messages;
    }

}