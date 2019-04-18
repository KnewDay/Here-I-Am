package com.example.dmberry.HereIAm;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class MultiView extends SimpleCursorAdapter {

    public MultiView(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);

    }

    public MultiView(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

    }
}
