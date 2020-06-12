package com.example.android.notepad;


import android.Manifest;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;

public class NoteSearch extends ListActivity implements SearchView.OnQueryTextListener {
    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // 2 修改时间
    };

    SearchView searchview;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_search);
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        SpeechUtility.createUtility(this, SpeechConstant.APPID+"=5edf7c34");//初始化语音识别的sdk
        searchview = (SearchView) findViewById(R.id.search_view);
        searchview.setSubmitButtonEnabled(true);//显示提交按钮
        searchview.setOnQueryTextListener(NoteSearch.this);//为searchview增加监听器
        imageButton= (ImageButton) findViewById(R.id.voice_button);
        final VoiceSearch voiceSearch=new VoiceSearch();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startSpeechClick(view);
            }
        });


    }


    @Override
    public boolean onQueryTextSubmit(String query) {//提交方法
        if(getListAdapter().getCount()==0){
            Toast.makeText(this, "no found", Toast.LENGTH_SHORT).show();//若无查询结果则提示无结果
        }
        else{
            Toast.makeText(this, getListAdapter().getCount()+" "+"found", Toast.LENGTH_SHORT).show();//提示找到几条数据
        }
        SearchView searchview = (SearchView) findViewById(R.id.search_view);
        if (searchview!= null) {
            // 得到输入管理对象
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchview.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
            }
            searchview.clearFocus(); // 不获取焦点
        }
        return true;

    }

    @Override
    public boolean onQueryTextChange(String newText) {//文本框有变化时做的方法
        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";
        String[] selectionArgs = {"%" + newText + "%"};
        Cursor cursor = managedQuery(
                getIntent().getData(),
                PROJECTION,
                selection,     // The columns for the where clause
                selectionArgs, // The values for the where clause
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
        String[] dataColumns = {NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE};
        int[] viewIDs = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adapter
                = new SimpleCursorAdapter(
                this,                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor,                           // The cursor to get items from
                dataColumns,
                viewIDs
        );
        setListAdapter(adapter);
        return true;
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {//点击搜索到的笔记时调用的方法

        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

        // Gets the action from the incoming Intent
        String action = getIntent().getAction();

        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {

            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
    public void startSpeechClick(final View view){//语音识别函数
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        //2.设置accent、language等参数
        final SearchView searchview= (SearchView) view.findViewById(R.id.search_view);
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        mDialog.show();
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    String result = parseVoice(recognizerResult.getResultString());
                    searchview.setQuery(result,false);
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入

    }

    public String parseVoice(String resultString) {//语音解析
        Gson gson = new Gson();
        VoiceSearch.Voice voiceBean = gson.fromJson(resultString, VoiceSearch.Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<VoiceSearch.Voice.WSBean> ws = voiceBean.ws;
        for (VoiceSearch.Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }
    public class Voice {//语音封装

        public ArrayList<VoiceSearch.Voice.WSBean> ws;

        public class WSBean {
            public ArrayList<VoiceSearch.Voice.CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }

}
