package com.vsga.catatanharian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_STORAGE=100;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT>=23){
            if (periksaIzinPenyimpanan()){
                ambilListFilePadaFolder();
            }
        }else {
            ambilListFilePadaFolder();
        }
    }
    public boolean periksaIzinPenyimpanan(){
        if (Build.VERSION.SDK_INT>=23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);
                return false;
            }
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_STORAGE:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    ambilListFilePadaFolder();
                }
                break;
        }
    }
    private void ambilListFilePadaFolder(){
        String path= Environment.getExternalStorageDirectory().toString()+"/catatanharian";
        File directory=new File(path);
        if (directory.exists()){
            File[] files=directory.listFiles();
            String[] filenames=new String[files.length];
            String[] dateCreated=new String[files.length];
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MM yyyy HH:mm:ss");
            ArrayList<Map<String,String>> itemDataList=new ArrayList<>();
            for (int i=0;i<files.length;i++){
                filenames[i]=files[i].getName();
                Date lastModDate=new Date(files[i].lastModified());
                dateCreated[i]=simpleDateFormat.format(lastModDate);
                Map<String,String> itemMap=new HashMap<>();
                itemMap.put("name",filenames[i]);
                itemMap.put("date",dateCreated[i]);
                itemDataList.add(itemMap);
            }
            SimpleAdapter simpleAdapter=new SimpleAdapter(this, itemDataList,
                    android.R.layout.simple_list_item_2,
                    new String[]{"name","date"},new int[]{android.R.id.text1,
            android.R.id.text1});
            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }
}