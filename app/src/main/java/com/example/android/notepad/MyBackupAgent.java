package com.example.android.notepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MyBackupAgent extends Activity {
    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // 2 修改时间
    };
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_restore);
    }

    private final static String EXTERNAL_STORAGE_BACKUP_DIR = "Backup";

    // 数据备份
    public void dataBackup(View v) {
        Backup();
        finish();
    }

    // 数据恢复
    public void dataRecover(View v) {
        restore();
        finish();
    }

    private void Backup() {
        File dbFile = getApplicationContext().getDatabasePath("note_pad.db");
        File exportDir = new File(Environment.getExternalStorageDirectory(), EXTERNAL_STORAGE_BACKUP_DIR);//   SD卡路径
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File backup = new File(exportDir, dbFile.getName());//备份文件与原数据库文件名一致
        try {
            backup.createNewFile();
            fileCopy(dbFile, backup);//数据库文件拷贝至备份文件
            Toast.makeText(this, "已备份到:" + exportDir.getCanonicalPath() + "/" + backup.getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLog", "backup fail! 备份文件名：" + backup.getName());
        }
    }

    private void restore() {
        File dbFile = getApplicationContext().getDatabasePath("note_pad.db");// 默认路径是 /data/data/(包名)/databases/*
        File exportDir = new File(Environment.getExternalStorageDirectory(), EXTERNAL_STORAGE_BACKUP_DIR);//   SD卡路径
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File backup = new File(exportDir, dbFile.getName());//备份文件与原数据库文件名一致
        try {
            fileCopy(backup, dbFile);//备份文件拷贝至数据库文件
            //Toast.makeText(this, "已还原", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(MyBackupAgent.this, NotesList.class);
            MyBackupAgent.this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "并未备份", Toast.LENGTH_LONG).show();
            Log.d("myLog", "restore fail! 数据库文件名：" + dbFile.getName());
        }
    }

    private void fileCopy(File dbFile, File backup) throws IOException {
        FileChannel inChannel = new FileInputStream(dbFile).getChannel();
        FileChannel outChannel = new FileOutputStream(backup).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}