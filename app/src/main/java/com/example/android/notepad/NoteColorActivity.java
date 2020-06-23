package com.example.android.notepad;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

public class NoteColorActivity extends Activity {

    private Cursor mCursor;
    private Uri mUri;
    private int color;
    private static final int COLUMN_INDEX_TITLE = 1;
    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_BACK_COLOR,
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_bgcolor);
        mUri = getIntent().getData();//传入数据

        mCursor = managedQuery(
                mUri,        // The URI for the note that is to be retrieved.
                PROJECTION,  // The columns to retrieve
                null,        // No selection criteria are used, so no where columns are needed.
                null,        // No where columns are used, so no where values are needed.
                null         // No sort order is needed.
        );

    }

    @Override
    protected void onResume() {
        if (mCursor != null) {
            mCursor.moveToFirst();
            color = mCursor.getInt(COLUMN_INDEX_TITLE);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ContentValues values = new ContentValues();
        values.put(NotePad.Notes.COLUMN_NAME_BACK_COLOR, color);//传入颜色
        getContentResolver().update(mUri, values, null, null);
    }

    public void white(View view) {
        color = NotePad.Notes.DEFAULT_COLOR;
        finish();
    }

    public void yellow(View view) {
        color = NotePad.Notes.YELLOW_COLOR;
        finish();
    }

    public void blue(View view) {
        color = NotePad.Notes.BLUE_COLOR;
        finish();
    }

    public void green(View view) {
        color = NotePad.Notes.GREEN_COLOR;
        finish();
    }

    public void pink(View view) {
        color = NotePad.Notes.PINK_COLOR;
        finish();
    }

    public void pic1(View view) {
        color = NotePad.Notes.PIC1;
        finish();
    }

    public void pic2(View view) {
        color = NotePad.Notes.PIC2;
        finish();
    }

    public void pic3(View view) {
        color = NotePad.Notes.PIC3;
        finish();
    }

    public void pic4(View view) {
        color = NotePad.Notes.PIC4;
        finish();
    }

    public void pic5(View view) {
        color = NotePad.Notes.PIC5;
        finish();
    }
}
