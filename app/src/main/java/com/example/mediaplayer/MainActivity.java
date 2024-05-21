package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    SeekBar sb;
    int duration=0,current=0;
    boolean finish=false,pauseFinish=false;
    Button btPlay,btPause,btOpen,btStop;
    TextView tv;
    String temp="";
    TextView tvSong;
    ImageView ivSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btOpen = findViewById(R.id.btOpen);
        btPlay = findViewById(R.id.btPlay);
        btPause =findViewById(R.id.btPause);
        btStop = findViewById(R.id.btStop);
        sb = findViewById(R.id.sb);
        tv = findViewById(R.id.tv);
        tvSong = findViewById(R.id.tvSong);
        ivSong = findViewById(R.id.ivSong);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(GlobalMedia.mp!=null)
                {
                    current = sb.getProgress();
                    tv.setText(""+current+"/"+duration);
                    GlobalMedia.mp.seekTo(current*1000);
                }
            }
        });

        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("audio/*");
                startActivityForResult(Intent.createChooser(i,"Select audio file"),150);
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalMedia.mp!=null)
                {
                    pauseFinish = false;
                    finish=false;
                    GlobalMedia.mp.start();
                }
                else
                {
                    Toast.makeText(getApplication(),"Media not selected...!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalMedia.mp!=null)
                {
                    GlobalMedia.mp.pause();
                    pauseFinish = true;
                }
                else
                {
                    Toast.makeText(getApplication(),"Media not selected...!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalMedia.mp!=null)
                {
                    GlobalMedia.mp.stop();
                    sb.setProgress(0);
                    tv.setText("0/0");
                    finish = true;
                    pauseFinish = true;
                    current = 0;
                    duration = 0;
                    tvSong.setText("Spotify");
                    ivSong.setImageBitmap(null);
                    ivSong.setBackgroundResource(R.drawable.img_2);

                    GlobalMedia.mp = null;
                }
                else
                {
                    Toast.makeText(getApplication(),"Media not selected...!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode,int resCode,Intent data)
    {
        if(resCode==RESULT_OK && reqCode==150)
        {
            Uri uri = data.getData();
            if(GlobalMedia.mp!=null)
            {
                GlobalMedia.mp.stop();
            }
            GlobalMedia.mp = MediaPlayer.create(getApplicationContext(),uri);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getApplicationContext(), uri);
            temp = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            byte[] albumArt = retriever.getEmbeddedPicture();


            if (albumArt != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
                ivSong.setBackgroundResource(R.drawable.ic_launcher_background);
                int colorValue = Color.parseColor("#040D12");
                ivSong.setBackgroundColor(colorValue);
                ivSong.setImageBitmap(bitmap);
            }
            else
            {
                ivSong.setImageResource(R.drawable.img_2); // Replace with your default image
            }


            temp = temp.substring(temp.lastIndexOf('/') + 1);
            tvSong.setText(temp);
            GlobalMedia.mp.start();

            notifyMe(uri);

            duration = GlobalMedia.mp.getDuration();
            duration = duration / 1000;

            sb.setMax(duration);
            tv.setText("0/"+duration);
            finish = false;
            pauseFinish = false;

            new Thread(new Runnable() {
                @Override
                public void run() {

                while(!finish)
                {

                    try { Thread.sleep(1000); }
                    catch(Exception e) { }

                    if(!pauseFinish)
                    {
                        current = GlobalMedia.mp.getCurrentPosition();
                        current = current / 1000;
                        sb.setProgress(current);
                        tv.post(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(""+current+"/"+duration);
                            }
                        });

                    }

                    if(current == duration)
                    {
                        sb.setProgress(0);
                        tv.setText("0/0");
                        finish = true;
                        pauseFinish = true;
                        current = 0;
                        duration = 0;
                        if(GlobalMedia.mp!=null)
                        {
                            GlobalMedia.mp.stop();
                        }
                        GlobalMedia.mp = null;
                    }
                }
                }
            }).start();
        }
    }

    public void notifyMe(Uri uri)
    {
        String path = tvSong.getText().toString();
        Intent i1 = new Intent(getApplicationContext(),MainActivity.class);
        //startActivity(i1);
         // startActivityForResult(i1);
        PendingIntent pi1 = PendingIntent.getActivity(getApplicationContext(),0,i1,PendingIntent.FLAG_IMMUTABLE);

        Intent i2 = new Intent(getApplicationContext(),PauseService.class);
        PendingIntent pi2 = PendingIntent.getService(getApplicationContext(),0,i2,PendingIntent.FLAG_IMMUTABLE);

        Intent i3 = new Intent(getApplicationContext(),PlayService.class);
        PendingIntent pi3 = PendingIntent.getService(getApplicationContext(),0,i3,PendingIntent.FLAG_IMMUTABLE);

        NotificationManager man = getSystemService(NotificationManager.class);

        NotificationChannel channel = new NotificationChannel("2024","spotify",NotificationManager.IMPORTANCE_HIGH);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"2024")
                .setContentTitle("KHUSHI SONGS")
                .setContentText(path)
                .setSmallIcon(R.drawable.song)
                .setContentIntent(pi1)
                .addAction(R.drawable.ic_pause_button,"Pause",pi2)
                .addAction(R.drawable.baseline_play_circle_filled_24,"Play",pi3);

        man.createNotificationChannel(channel);
        man.notify(1011,builder.build());


    }

}