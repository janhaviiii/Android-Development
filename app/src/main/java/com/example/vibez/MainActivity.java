package com.example.vibez;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        //asking for permission from the users device
        Dexter.withContext(this)

                //Dexter is an Android library that simplifies the process of requesting permissions at runtime.

                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        //Toast.makeText(MainActivity.this, "Runtime Permission given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = fetchsongs(Environment.getExternalStorageDirectory());
                        String[] item = new String[mySongs.size()];
                        for (int i=0;i<mySongs.size();i++){

                            //remove ".mp3" and replace with a null string. only show the audio name without any suffix
                            item[i]=mySongs.get(i).getName().replace(".mp3", "");
                        }

                        //to store the arrayList in a list view
                        ArrayAdapter<String> adapter =new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, item);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                               Intent intent = new Intent(MainActivity.this, PlaySong.class);
                               String currentSong= listView.getItemAtPosition(position).toString();
                               intent.putExtra("songList", mySongs);
                               intent.putExtra("currentSong", currentSong);
                               intent.putExtra("position", position);
                               startActivity(intent);
                           }
                       });
                    }

                    //regarding the permission which was asked by the user
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    //to fetch the songs from the internal storage of the device
    public ArrayList<File> fetchsongs(File file){
        ArrayList arrayList=new ArrayList();
        File[] songs= file.listFiles();
        if(songs !=null){
            for(File myFile: songs){
                if(!myFile.isHidden()&& myFile.isDirectory()){
                    arrayList.addAll(fetchsongs(myFile));
                }
                else{
                    if (myFile.getName().endsWith((".mp3")) &&!myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}