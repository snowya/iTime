package com.jnu.student.myapplication.home.data;

import android.content.Context;


import com.jnu.student.myapplication.home.data.model.Event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileDataSource {
    private Context context;
    private ArrayList<Event> events = new ArrayList<>();

    public FileDataSource(Context context) {
        this.context = context;
    }

    public void save () {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("Serializable.txt", Context.MODE_PRIVATE)
            );
            outputStream.writeObject(events);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Event> load () {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Serializable.txt")
            );
            events = (ArrayList<Event>) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public void addEvent (Event event) {
        events=load();
        events.add(event);
        save();
    }
}
