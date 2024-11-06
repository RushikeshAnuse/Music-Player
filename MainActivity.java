
package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ListView listView;
    ImageView home,playlist,video;
    AutoCompleteTextView actxtview;
    ArrayList<File> mySongs=new ArrayList<>();
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE=1002;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        home=findViewById(R.id.Home);
        playlist=findViewById(R.id.playlist);
        video=findViewById(R.id.video);
        actxtview=findViewById(R.id.actxtView);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //click on next intent
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(MainActivity.this, Video.class);
                //startActivity(intent);
            }

        });


         // Check if the permission is already granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // You have the permission; do your read storage related task here.
                    //create array list for
                   ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());

                    displaySongs(mySongs);


                } else {
                    // You don't have the permission; request it.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
                }
            }

            // Override onRequestPermissionsResult to handle the user's response.
            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);


                if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // The permission is granted; do your read storage related task here.
                        Toast.makeText(this, "Request is granted", Toast.LENGTH_SHORT).show();
                       // ArrayList<File> mySongs = fetchSongs();
                    }

                    ArrayList<File> mySongs=fetchSongs(Environment.getExternalStorageDirectory());
                    String[] items=new String[mySongs.size()];
                    int i;
                    for (i=0;i<mySongs.size();i++){

                        items[i]=mySongs.get(i).getName().replace(".mp3","");
                    }
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent=new Intent(MainActivity.this,PlaySong.class);
                            String currentSong=listView.getItemAtPosition(position).toString();
                            intent.putExtra("sonslist",mySongs);
                            intent.putExtra("currentSong",currentSong);
                            intent.putExtra("position",position);
                            startActivity(intent);

                        }
                    });

                }
            }

    private void displaySongs(ArrayList<File> songs) {
        String[] items = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            items[i] = songs.get(i).getName().replace(".mp3", "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

       // actxtview.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PlaySong.class);
                String currentSong = listView.getItemAtPosition(position).toString();
                intent.putExtra("songslist", songs);
                intent.putExtra("currentSong", currentSong);
                intent.putExtra("position", position);
                startActivity(intent);

                }

        });
    }


    public ArrayList<File> fetchSongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        String[] projection = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME};
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                File songFile = new File(filePath);
                arrayList.add(songFile);
            }
            cursor.close();
        }
        return arrayList;
    }



}