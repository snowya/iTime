package com.jnu.student.myapplication.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.jnu.student.myapplication.MainActivity;
import com.jnu.student.myapplication.NewEventActivity;
import com.jnu.student.myapplication.R;
import com.jnu.student.myapplication.UpdateEventActivity;
import com.jnu.student.myapplication.home.data.FileDataSource;
import com.jnu.student.myapplication.home.data.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.jnu.student.myapplication.MainActivity.REQUEST_CODE_NEW_EVENT;
import static com.jnu.student.myapplication.MainActivity.REQUEST_CODE_UPDATE_EVENT;
import static com.jnu.student.myapplication.MainActivity.navController;
import static java.lang.Math.min;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<Event> theEvents=new ArrayList<Event>();
    private FileDataSource fileDataSource;
    private EventsArrayAdapter eventsArrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        initData();

        ListView listView = view.findViewById(R.id.list_view);
        eventsArrayAdapter=new EventsArrayAdapter(getActivity(),R.layout.event_item,theEvents);
        listView.setAdapter(eventsArrayAdapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    private void initData() {
        fileDataSource=new FileDataSource(getActivity());
        theEvents=fileDataSource.load();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event event=theEvents.get(position);
        Intent intent = new Intent(getContext(), UpdateEventActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("cover", event.getCover());
        intent.putExtra("date", event.getDate().toString());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("period", event.getPeriod());
        startActivityForResult(intent, MainActivity.REQUEST_CODE_UPDATE_EVENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_UPDATE_EVENT:
                if (resultCode == RESULT_OK) {
                    MainActivity.navController.navigate(R.id.nav_home);
                }
                break;
        }
    }

    class EventsArrayAdapter extends ArrayAdapter<Event> {

        private int resourceId;
        EventsArrayAdapter(@NonNull Context context, int resource, @NonNull List<Event> objects) {
            super(context, resource, objects);
            resourceId=resource;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater mInflater= LayoutInflater.from(this.getContext());
            View item = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            ImageView img = (ImageView)item.findViewById(R.id.cover);
            TextView day = (TextView)item.findViewById(R.id.day);
            TextView title = (TextView)item.findViewById(R.id.title);
            TextView date = (TextView)item.findViewById(R.id.date);
            TextView stroke = (TextView)item.findViewById(R.id.stroke);

            Event event_item= getItem(position);
            Bitmap bitmap;
            if(!event_item.getCover().equals("")) {
                bitmap = BitmapFactory.decodeFile(event_item.getCover());
            } else {
                bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.event);
            }
            bitmap = centerSquareScaleBitmap(bitmap);
            img.setImageBitmap(bitmap);
            String dayText="";
            if(event_item.getDay()<0) {
                dayText="已经\n"+-event_item.getDay()+"";
            } else {
                dayText=event_item.getDay()+"";
            }
            day.setText(dayText + " Days");
            title.setText(event_item.getTitle());
            date.setText(MainActivity.dateToString(event_item.getDate(),"yyyy年MM月dd日"));

            // 设置图片透明度
            img.setAlpha(230);

            // 设置右边的边框
            Bitmap bm= ((BitmapDrawable)img.getDrawable()).getBitmap();
            int x=1,y=1;
            while(bm.getPixel(x,y)== Color.parseColor("#FFFFFF")){
                x++;
                y++;
            }
            int color = bm.getPixel(x,y);
            stroke.setBackgroundColor(color);

            // 设置边框
            View constraintLayout=item.findViewById(R.id.constranintLayout);
            GradientDrawable gradientDrawable1= (GradientDrawable) constraintLayout.getBackground();
            gradientDrawable1.setStroke(1,getResources().getColor(R.color.gray));
            constraintLayout.setBackground(gradientDrawable1);

            return item;
        }
    }

    /**
     * @param bitmap      原图
     * @return  缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap)
    {
        if(null == bitmap)
        {
            return  null;
        }
        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        int edgeLength=min(widthOrg,heightOrg);

        //压缩到一个最小长度是edgeLength的bitmap
        int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / min(widthOrg, heightOrg));
        int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
        int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
        Bitmap scaledBitmap;

        try{
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        }
        catch(Exception e){
            return null;
        }

        //从图中截取正中间的正方形部分。
        int xTopLeft = (scaledWidth - edgeLength) / 2;
        int yTopLeft = (scaledHeight - edgeLength) / 2;

        try{
            result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
            scaledBitmap.recycle();
        }
        catch(Exception e){
            return null;
        }

        return result;
    }
}