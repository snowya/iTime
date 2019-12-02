package com.jnu.student.myapplication.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.jnu.student.myapplication.MainActivity;
import com.jnu.student.myapplication.R;
import com.jnu.student.myapplication.home.data.model.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private ArrayList<Event> theEvents=new ArrayList<Event>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        ListView listView = view.findViewById(R.id.list_view);
        initData();
        EventsArrayAdapter eventsArrayAdapter=new EventsArrayAdapter(getActivity(),R.layout.event_item,theEvents);
        listView.setAdapter(eventsArrayAdapter);
        return view;
    }

    void initData () {
        theEvents.add(new Event(R.drawable.event,123,"123",new Date(123)));
        theEvents.add(new Event(R.drawable.event,123,"123",new Date(123)));
    }

    public void setTheEvents(ArrayList<Event> theEvents) {
        this.theEvents = theEvents;
    }

    protected class EventsArrayAdapter extends ArrayAdapter<Event> {

        private int resourceId;
        public EventsArrayAdapter(@NonNull Context context, int resource, @NonNull List<Event> objects) {
            super(context, resource, objects);
            resourceId=resource;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater mInflater= LayoutInflater.from(this.getContext());
            View item = mInflater.inflate(this.resourceId,null);

            ImageView img = (ImageView)item.findViewById(R.id.cover);
            TextView day = (TextView)item.findViewById(R.id.day);
            TextView title = (TextView)item.findViewById(R.id.title);
            TextView date = (TextView)item.findViewById(R.id.date);
            TextView stroke = (TextView)item.findViewById(R.id.stroke);


            Event good_item= this.getItem(position);
            img.setImageResource(good_item.getCoverId());
            day.setText(good_item.getDay() + " Days");
            title.setText(good_item.getTitle());
            date.setText(good_item.getDate().toString());

            // 设置图片透明度
            img.setAlpha(230);

            // 设置右边的边框
            Bitmap bitmap= ((BitmapDrawable)img.getDrawable()).getBitmap();
            int x=1,y=1;
            while(bitmap.getPixel(x,y)== Color.parseColor("#FFFFFF")){
                x++;
                y++;
            }
            int color = bitmap.getPixel(x,y);
            stroke.setBackgroundColor(color);

            // 设置边框
            View constraintLayout=item.findViewById(R.id.constranintLayout);
            GradientDrawable gradientDrawable1= (GradientDrawable) constraintLayout.getBackground();
            gradientDrawable1.setStroke(1,getResources().getColor(R.color.gray));
            constraintLayout.setBackground(gradientDrawable1);

            return item;
        }
    }
}