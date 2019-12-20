package com.jnu.student.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnu.student.myapplication.home.data.FileDataSource;
import com.jnu.student.myapplication.home.data.model.Event;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.jnu.student.myapplication.MainActivity.REQUEST_CODE_EDIT_EVENT;
import static com.jnu.student.myapplication.MainActivity.REQUEST_CODE_UPDATE_EVENT;

public class UpdateEventActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    int position;
    long day,hour,minute,second,ms;
    TextView time_to_day;
    String date,cover,title,description,period;
    FileDataSource fileDataSource=new FileDataSource(this);
    Boolean stopThread=false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        // 顶部标题栏
        Toolbar toolbar = findViewById(R.id.event_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // 标题栏按钮
        toolbar.setOnMenuItemClickListener(this);
        // 返回按钮
        Button back = toolbar.findViewById(R.id.update_event_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread=true;
                UpdateEventActivity.this.finish();
            }
        });
        // 初始化控件
        initView(getIntent());
    }

    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initView(Intent intent) {
        ImageView img=findViewById(R.id.cover);
        TextView vTitle=findViewById(R.id.title);
        time_to_day=findViewById(R.id.time_to_day);
        TextView date_description=findViewById(R.id.date_description);

        position=intent.getIntExtra("position",-1);
        date=intent.getStringExtra("date");
        title=intent.getStringExtra("title");
        cover=intent.getStringExtra("cover");
        description=intent.getStringExtra("description");
        period=intent.getStringExtra("period");

        vTitle.setText(title);
        date_description.setText(date);
        if(!cover.equals(""))
            img.setBackground(Drawable.createFromPath(cover));

        ms=new Date(date).getTime()-new Date().getTime();
        timeOut(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ms-=1000;
                setTime(ms);
            }
        });
    }

    private void setTime (long ms) {
        int tag=1;
        if (ms<0){
            ms=-ms;
            tag=0;
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        day = ms / dd;
        hour = (ms - day * dd) / hh;
        minute = (ms - day * dd - hour * hh) / mi;
        second = (ms - day * dd - hour * hh - minute * mi) / ss;
        setTime_to_day(tag);
    }

    private void timeOut (final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!stopThread) {
                        handler.sendMessage(new Message());
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    public void setTime_to_day(int tag) {
        String text=day+" 天 "+hour+" 小时 "+minute+" 分钟 "+second+" 秒";
        if(tag==0) {
            text="Gone "+text;
        }
        time_to_day.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.REQUEST_CODE_EDIT_EVENT:
                if (resultCode == RESULT_OK) {
                    stopThread=false;
                    initView(data);
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_empty:
                ArrayList<Event> events=fileDataSource.load();
                events.remove(position);
                fileDataSource.save();
                setResult(RESULT_OK,new Intent());
                stopThread=true;
                UpdateEventActivity.this.finish();
                break;
            case R.id.action_share:
                // do sth
                break;
            case R.id.action_edit:
                Intent intent = new Intent(this, NewEventActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("cover", cover);
                intent.putExtra("date", date);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("period", period);
                stopThread=true;
                startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_EVENT);
                break;
        }
        return false;
    }
}
