package com.example.lucas.porterapp;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * Created by cindy on 11/11/2017.
 */

/**
 * Custom adapter for populating completed task list view
 */
public class CursorAdapter extends ResourceCursorAdapter {

    public CursorAdapter(Context context, int layout, Cursor cursor, int flags) {
        super(context, layout, cursor, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Find views and set values for Views from cursor
        TextView completedListWardName = (TextView) view.findViewById(R.id.completedListWardName);
        completedListWardName.setText(cursor.getString(cursor.getColumnIndex("WARD_NAME")));

        TextView timetaken = (TextView) view.findViewById(R.id.completedListTimeTaken);
        timetaken.setText(cursor.getString(cursor.getColumnIndex("TIMETAKEN")) + " Mins");

        TextView dest = (TextView) view.findViewById(R.id.completedListDestiantion);
        dest.setText(cursor.getString(cursor.getColumnIndex("DESTINATION")));

        TextView completedListTimestamp = (TextView) view.findViewById(R.id.completedListTimestamp);
        completedListTimestamp.setText(cursor.getString(cursor.getColumnIndex("TIMESTAMP_COMPLETED")));

    }
}
