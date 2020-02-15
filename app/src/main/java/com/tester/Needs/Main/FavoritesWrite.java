package com.tester.Needs.Main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tester.Needs.R;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.tester.Needs.Main.MainActivity.id_nickName;
import static com.tester.Needs.Main.MainActivity.id_uid;
import static com.tester.Needs.Main.MainActivity.id_value;
import static com.tester.Needs.Main.SubActivity.fragmentNumber;
import static com.tester.Needs.Main.SubActivity.point;


@RequiresApi(api = Build.VERSION_CODES.O)
public class FavoritesWrite extends AppCompatActivity {


    Button free_ok;
    Button free_cancel;

    EditText free_content_write;
    EditText free_title_write;

    FirebaseFirestore db = FirebaseFirestore.getInstance();//firestore객체

    String title;
    String content;
    String documentName;

    Instant nowUtc = Instant.now();
    ZoneId asiaSeoul = ZoneId.of("Asia/Seoul");
    ZonedDateTime nowAsiaSeoul = ZonedDateTime.ofInstant(nowUtc, asiaSeoul);

    String year = String.valueOf(nowAsiaSeoul.getYear());
    String month = String.valueOf(nowAsiaSeoul.getMonthValue());
    String day1 = String.valueOf(nowAsiaSeoul.getDayOfMonth());
    String hour = String.valueOf(nowAsiaSeoul.getHour());
    String minute = String.valueOf(nowAsiaSeoul.getMinute());
    String second = String.valueOf(nowAsiaSeoul.getSecond());

    String fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
    String pointNum = point;


    private ProgressDialog mProgressDialog;
    private BackgroundThread mBackThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_favorites_write);

        fragmentNumber = 1;

        if(month.length() == 1){
            month = "0" + month;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
        }
        if(day1.length() ==1){
            day1 = "0" + day1;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
        }


        if (hour.length() == 1) {
            hour = "0" + hour;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute;
        }

        if (minute.length() == 1) {
            minute = "0" + minute;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute;
        }

        if (second.length() == 1) {
            second = "0" + second;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
        }

        Intent intent = getIntent();
        free_ok = findViewById(R.id.free_ok);
        free_ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                int compareNum = Integer.parseInt(pointNum);
                if (compareNum >= 2) {

                    int number2 = Integer.parseInt(pointNum);
                    number2 = number2 - 2;
                    point = Integer.toString(number2);

                    db.collection("user").document(id_uid)
                            .update(
                                    "id_point", point
                            );

                    free_content_write = findViewById(R.id.free_content_write);
                    free_title_write = findViewById(R.id.free_title_write);

                    title = free_title_write.getText().toString();//getText까지는 string 형태가 아님
                    content = free_content_write.getText().toString();//getText까지는 string 형태가 아님

                    Map<String, Object> user = new HashMap<>();
                    user.put("title", title);
                    user.put("content", content);
                    user.put("writer", id_value);
                    user.put("day", fullDay);
                    //user.put("visit_num", "0");
                   // user.put("good_num", "0");
                    user.put("visit_num", 0);
                    user.put("good_num", 0);
                    user.put("write", id_nickName);


                    //try {
                    Log.d("docName출력 1번테스트", "docName출력 1번테스트");
                    db.collection("freeData")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("docName출력 2번테스트", "docName출력 2번테스트");
                                    documentName = documentReference.getId();
                                    Log.d("docName출력 5번테스트", "docName출력 5번테스트" + documentName);
                                    execute();
                                }
                            });
                    mProgressDialog = ProgressDialog.show(FavoritesWrite.this, "Loading"
                            , "글작성중입니다..");

                    mBackThread = new BackgroundThread();
                    mBackThread.setRunning(true);
                    mBackThread.start();
                       /* Thread.sleep(1000);
                        Log.d("docName출력 3번테스트","docName출력 3번테스트");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////

                    //////////////////////////////touser///////////////////////////////////////////////
                        /*
                    Intent intent_write = new Intent(FavoritesWrite.this, SubActivity.class);

                    startActivity(intent_write);*/


                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesWrite.this);

                    builder.setTitle("등록불가").setMessage("포인트가 부족합니다");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }

            }
        });

        free_cancel = findViewById(R.id.free_cancel);
        free_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesWrite.this.finish();
            }
        });
    }

    public void execute() {
        Map<String, Object> toUser = new HashMap<>();
        toUser.put("title", title);
        toUser.put("content", content);
        toUser.put("id_value", id_value);
        toUser.put("day", fullDay);
        toUser.put("visitnum", "0");
        toUser.put("good", "0");
        toUser.put("documentName", documentName);
        toUser.put("id", id_nickName);

        db.collection("user").document(id_uid).collection("write").document(title + content)
                .set(toUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });


        Map<String, Object> member = new HashMap<>();
        member.put("day", fullDay);
        member.put("point", "-2");
        member.put("type", "사용");

        db.collection("user").document(id_uid).collection("pointHistory")
                .add(member)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });
        db.collection("freeData").document(documentName)
                .update(
                        "document_name", documentName
                );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FavoritesWrite.this.finish();
    }

    public class BackgroundThread extends Thread {
        volatile boolean running = false;
        int cnt;

        void setRunning(boolean b) {
            running = b;
            cnt = 7;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    sleep(300);
                    if (cnt-- == 0) {
                        running = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendMessage(handler.obtainMessage());
        }

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();

            boolean retry = true;
            while (retry) {
                try {
                    mBackThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(FavoritesWrite.this, " 글이작성되었습니다", Toast.LENGTH_SHORT).show();

            FavoritesWrite.this.finish();
            Intent intent = new Intent(FavoritesWrite.this, SubActivity.class);
            startActivity(intent);
        }

    };
}

