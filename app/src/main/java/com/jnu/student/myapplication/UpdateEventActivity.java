package com.jnu.student.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jnu.student.myapplication.home.data.FileDataSource;
import com.jnu.student.myapplication.home.data.model.Event;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class UpdateEventActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private Context context;
    RelativeLayout pic_layout;
    // 显示的控件
    private TextView date_describe, period_describe;
    private EditText eTitle,eDescription;

    private int year, month, day, hour, minute;
    //在TextView上显示的字符
    private StringBuffer dateTime, time;
    private static final int GALLERY_CODE = 1;
    private static final int CROP_CODE = 2;
    // 应用创建的背景图片
    File img;
    // 主题颜色
    int color;
    FileDataSource fileDataSource;

    // 传过来的内容
    int position;
    String date,cover,title,description,period;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        context = this;
        dateTime = new StringBuffer();
        time = new StringBuffer();
        color = getIntent().getIntExtra("color", 0);
        initView();
        if(color==0) {
            position = getIntent().getIntExtra("position", -1);
            date = getIntent().getStringExtra("date");
            title = getIntent().getStringExtra("title");
            cover = getIntent().getStringExtra("cover");
            description = getIntent().getStringExtra("description");
            period = getIntent().getStringExtra("period");
            initData();
        } else {
            initDateTime();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        // 返回控件和保存控件
        Toolbar toolbar = (Toolbar) findViewById(R.id.event_activity_toolbar);
        Button back = toolbar.findViewById(R.id.new_event_back);
        Button save = toolbar.findViewById(R.id.save);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateEventActivity.this.finish();
            }
        });
        save.setOnClickListener(this);

        // 文本框控件
        eTitle = findViewById(R.id.title);
        eDescription = findViewById(R.id.description);

        // 日期时间选择控件
        RelativeLayout date_layout = (RelativeLayout) findViewById(R.id.date_layout);
        date_layout.setOnClickListener(this);
        date_describe = (TextView) findViewById(R.id.date_describe);

        // 周期选择控件
        RelativeLayout period_layout = (RelativeLayout) findViewById(R.id.period_layout);
        period_layout.setOnClickListener(this);
        period_describe = (TextView) findViewById(R.id.period_describe);

        // 显示图片的控件
        RelativeLayout image_layout = (RelativeLayout) findViewById(R.id.image_layout);
        image_layout.setOnClickListener(this);
        pic_layout = (RelativeLayout) findViewById(R.id.top);
        if(color!=0)
            pic_layout.setBackgroundColor(color);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        eTitle.setText(title);
        eDescription.setText(description);
        date_describe.setText(MainActivity.dateToString(new Date(date),"yyyy年MM月dd日 HH时mm分"));
        period_describe.setText(period);
        if(!cover.equals("")) {
            pic_layout.setBackground(Drawable.createFromPath(cover));
        } else {
            pic_layout.setBackground(getResources().getDrawable(R.drawable.event));
        }
        setDateTime();
    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
    }

    // 设置日期和时间
    private void setDateTime () {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(date));
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_layout:
                // 弹出日期时间选择框
                date_dialog();
                break;
            case R.id.period_layout:
                // 弹出周期选择框
                period_dialog();
                break;
            case R.id.image_layout:
                // 从相册选取图片
                chooseFromGallery();
                break;
            case R.id.save:
                if(color==0)
                    edit();
                else
                    save();
                break;
        }
    }

    // 保存数据
    private void save () {
        Date date = MainActivity.stringToDate(date_describe.getText().toString()+"00秒");
        String imgPath="";
        if(img!=null){
            imgPath=img.getAbsolutePath();
        }
        Event event = new Event(imgPath, eTitle.getText().toString(), eDescription.getText().toString(), period_describe.getText().toString(), date);
        fileDataSource=new FileDataSource(UpdateEventActivity.this);
        fileDataSource.addEvent(event);

        setResult(RESULT_OK,new Intent());
        UpdateEventActivity.this.finish();
    }

    // 修改数据
    private void edit () {
        Date date = MainActivity.stringToDate(date_describe.getText().toString()+"00秒");
        Event event = new Event(cover, eTitle.getText().toString(), eDescription.getText().toString(), period_describe.getText().toString(), date);
        fileDataSource=new FileDataSource(UpdateEventActivity.this);
        ArrayList<Event> events=fileDataSource.load();
        events.set(position,event);
        fileDataSource.save();

        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.putExtra("cover", event.getCover());
        intent.putExtra("date", event.getDate().toString());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("period", event.getPeriod());
        setResult(RESULT_OK,intent);
        UpdateEventActivity.this.finish();
    }

    // 弹出日期时间选择框
    private void date_dialog() {
        AlertDialog.Builder dateBuilder = new AlertDialog.Builder(context);
        // 选择完日期之后，选择时间
        dateBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dateTime.length() > 0) { //清除上次记录的日期
                    dateTime.delete(0, dateTime.length());
                }
                dateTime.append(String.valueOf(year)).append("年").append(String.valueOf(month+1)).append("月").append(day).append("日");
                time_dialog();
            }
        });
        dateBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = dateBuilder.create();
        View dialogView = View.inflate(this, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month - 1, day, this);
    }

    // 弹出时间选择框
    private void time_dialog() {
        AlertDialog.Builder timeBuilder = new AlertDialog.Builder(context);
        timeBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (time.length() > 0) { //清除上次记录的日期
                    time.delete(0, time.length());
                }
                time.append(String.valueOf(hour)).append("时").append(String.valueOf(minute)).append("分");
                date_describe.setText(dateTime + " " + time);
                dialog.dismiss();
            }
        });
        timeBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = timeBuilder.create();
        View dialogView = View.inflate(context, R.layout.dialog_time, null);
        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setIs24HourView(true); //设置24小时制
        timePicker.setOnTimeChangedListener((TimePicker.OnTimeChangedListener) context);
        dialog.setView(dialogView);
        dialog.show();
    }

    // 日期改变的监听事件
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    // 时间改变的监听事件
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
    }

    // 弹出周期选择框
    private void period_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Period");
        //    指定下拉列表的显示数据
        final String[] period = {"Week", "Month", "year", "Custom"};
        //    设置一个下拉的列表选择项
        builder.setItems(period, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                period_describe.setText(period[which]);
            }
        });
        builder.show();
    }

    /**
     * 从相册选择图片
     */
    private void chooseFromGallery() {
        //构建一个内容选择的Intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //设置选择类型为图片类型
        intent.setType("image/*");
        verifyPermission(this);
        //打开图片选择
        startActivityForResult(intent, GALLERY_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALLERY_CODE:
                if (data == null) {
                    return;
                } else {
                    // 用户从图库选择图片后会返回所选图片的Uri，获取到用户所选图片的Uri
                    Uri uri = data.getData();
                    // 通过Uri传递图像信息以供裁剪
                    startImageZoom(uri);
                }
                break;
            case CROP_CODE:
                if (data == null) {
                    return;
                } else {
                    try {
                        Uri uri = data.getData();
                        //获取到裁剪后的图像
                        Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        Drawable drawable = new BitmapDrawable(getResources(), bm);
                        pic_layout.setBackground(drawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 通过Uri传递图像信息以供裁剪
     *
     * @param uri
     */
    private void startImageZoom(Uri uri) {
        //新建文件夹用于存放裁剪后的图片
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/temp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        //新建文件存储裁剪后的图片
        img = new File(tmpDir.getAbsolutePath() + getPhotoFileName());

        //构建隐式Intent来启动裁剪程序
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置数据uri和类型为图片类型
        intent.setDataAndType(uri, "image/*");
        //显示View为可裁剪的
        intent.putExtra("crop", true);
        //裁剪的宽高的比例为3:2
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 2);
        //输出图片的宽高为300和200
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 200);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(img));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //裁剪之后的数据是通过Intent返回
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_CODE);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void verifyPermission(Context context) {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    UpdateEventActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}


