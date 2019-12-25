package com.jnu.student.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_NEW_EVENT = 902;
    public static final int REQUEST_CODE_UPDATE_EVENT = 903;
    public static final int REQUEST_CODE_EDIT_EVENT = 904;
    private AppBarConfiguration mAppBarConfiguration;
    // 主题颜色
    private int themeColor = Color.parseColor("#03A9F4");
    private int changingColor = Color.parseColor("#03A9F4");
    // seekBar的进度
    private float circleX = 0;
    public static NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 顶部标题栏
        Toolbar toolbar = findViewById(R.id.event_activity_toolbar);
        setSupportActionBar(toolbar);

        // 右下点击图标及其相应事件
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UpdateEventActivity.class);
                intent.putExtra("color", themeColor);
                startActivityForResult(intent, REQUEST_CODE_NEW_EVENT);
            }
        });

        // 获取主页及侧边导航栏控件
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // 设置侧边导航栏选项
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_color) {
            AlertDialog.Builder seekBarBuilder = new AlertDialog.Builder(this);
            seekBarBuilder.setTitle("选择主题色");
            seekBarBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    themeColor = changingColor;
                    dialog.dismiss();
                }
            });
            seekBarBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setThemeColor();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = seekBarBuilder.create();
            View dialogView = View.inflate(this, R.layout.dialog_color_seekbar, null);
            ColorSeekBar seekBar = dialogView.findViewById(R.id.seekBar);
            seekBar.setOnColorSelectedListener(new ColorSeekBar.OnColorSelectedListener() {
                @Override
                public void onColorSelected(int color, float x) {
                    circleX = x;
                    changingColor = color;
                    changeThemeColor(color);
                }
            });
            if (circleX != 0)
                seekBar.setCircleX(circleX);
            seekBar.setColor(Color.RED, Color.BLACK, Color.BLUE, Color.GREEN, Color.YELLOW);
            dialog.setView(dialogView);
            dialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_NEW_EVENT:
                if (resultCode == RESULT_OK) {
                    navController.navigate(R.id.nav_home);
                }
                break;
        }
    }

    // 设置为主题色
    public void setThemeColor() {
        // 修改悬浮按钮的颜色
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(themeColor));
        // 修改标题栏的颜色
        Toolbar toolbar = findViewById(R.id.event_activity_toolbar);
        toolbar.setBackgroundColor(themeColor);
    }

    // 改变主题色
    public void changeThemeColor(int color) {
        // 修改悬浮按钮的颜色
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
        // 修改标题栏的颜色
        Toolbar toolbar = findViewById(R.id.event_activity_toolbar);
        toolbar.setBackgroundColor(color);
    }

    // 字符串转换为日期
    static public Date stringToDate(String s) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        try {
            //字符串转日期
            return sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 日期转化为字符串；formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }
}
