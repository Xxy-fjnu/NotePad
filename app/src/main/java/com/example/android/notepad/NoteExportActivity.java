package com.example.android.notepad;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class NoteExportActivity extends Activity {

    private static final String[] PROJECTION = new String[]{//首先读取数据库中存放的笔记数据
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_NOTE, // 2
            NotePad.Notes.COLUMN_NAME_CREATE_DATE, // 3
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // 4
    };
    private String TITLE;//存放笔记标题
    private String NOTE;//存放笔记内容
    private String CREATE_DATE;//存放笔记创建时间
    private String MODIFICATION_DATE;//存放笔记修改时间
    private Cursor mCursor;
    private EditText mName;//导出文件的名字
    private Uri mUri;//读取传入的数据
    private boolean flag = false;
    private static final int COLUMN_INDEX_TITLE = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_text);

        mUri = getIntent().getData();
        mCursor = managedQuery(
                mUri,        // The URI for the note that is to be retrieved.
                PROJECTION,  // The columns to retrieve
                null,        // No selection criteria are used, so no where columns are needed.
                null,        // No where columns are used, so no where values are needed.
                null         // No sort order is needed.
        );
        mName = (EditText) findViewById(R.id.export_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCursor != null) {
            mCursor.moveToFirst();
            mName.setText(mCursor.getString(COLUMN_INDEX_TITLE));//默认为标题
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCursor != null) {
            TITLE = mCursor.getString(mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE));//读取标题
            NOTE = mCursor.getString(mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_NOTE));//读取内容
            CREATE_DATE = mCursor.getString(mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_CREATE_DATE));//读取创建时间
            MODIFICATION_DATE = mCursor.getString(mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE));//读取修改时间
            if (flag == true) {//点击导出按钮时执行写文件
                write();
            }
            flag = false;
        }
    }

    public void exportOk(View v) {//点击OK按钮时导出文件
        flag = true;
        finish();
    }

    public void exit(View v) {//点击return按钮时返回
        flag = false;
        finish();
    }

    private void write() {
        try {
            if (Environment.getExternalStorageState().equals(//获取状态
                    Environment.MEDIA_MOUNTED)) {
                File sdCardDir = Environment.getExternalStorageDirectory();  // 获取SD卡的目录
                File targetFile = new File(sdCardDir.getCanonicalPath() + "/" + mName.getText() + ".txt");    //创建目录
                PrintWriter wr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8"));  //写入
                wr.println("标题:" + TITLE);
                wr.println(NOTE);
                wr.println("创建时间：" + CREATE_DATE);
                wr.println("最近修改时间：" + MODIFICATION_DATE);
                wr.close();
                Toast.makeText(this, "保存到:" + sdCardDir.getCanonicalPath() + "/" + mName.getText() + ".txt", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
