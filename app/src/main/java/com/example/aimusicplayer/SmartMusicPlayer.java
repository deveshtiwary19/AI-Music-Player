package com.example.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.aimusicplayer.MainActivity2.musicFiles;

public class SmartMusicPlayer extends AppCompatActivity {

    private RelativeLayout parentrelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent  speechRecognizerIntent;
    private String keeper="ON";


    private ImageView pausePlayBtn,nextBtn,previousBtn;
    private TextView songNameTxt;

    //string to hold the mode of voice enables btn
    private String mode="";

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnablesBtn;


  private int position;
   static  ArrayList<MusicFiles> mySongs=new ArrayList<>();
   static Uri uri;
   private MediaPlayer mymediaPlayer;
   private String mSongName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_music_player);

        checkVoiceCommandPermission();

        //Intializing the fields
        pausePlayBtn=findViewById(R.id.play_pause_btn);
        nextBtn=findViewById(R.id.next_btn);
        previousBtn=findViewById(R.id.previous_btn);
        imageView=findViewById(R.id.logo);


        lowerRelativeLayout=findViewById(R.id.lower);
        voiceEnablesBtn=findViewById(R.id.voice_enabled_button);
        songNameTxt=findViewById(R.id.songName);






        parentrelativeLayout=findViewById(R.id.parent_relative_layout);


        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(SmartMusicPlayer.this);
        speechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReciveValuesAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.logo1);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> matchesFound=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matchesFound!=null)
                {
                   if (mode.equals("ON"))
                   {
                       keeper=matchesFound.get(0);


                        if (keeper.equals("pause")||keeper.equals("play the song")||keeper.equals("stop") || keeper.equals("play"))
                       {
                           playingPlayPauseSong();

                           Toast.makeText(SmartMusicPlayer.this, " Command Recieved", Toast.LENGTH_SHORT).show();
                           keeper="";
                       }
                       else if (keeper.equals("play next song") || keeper.equals("next")  )
                       {
                           playNextSong();

                           Toast.makeText(SmartMusicPlayer.this, "Play Next Command Recieved", Toast.LENGTH_SHORT).show();
                           keeper="";
                       }
                       else if (keeper.equals("play previous song") || keeper.equals("previous") )
                       {
                           playNextSong();

                           Toast.makeText(SmartMusicPlayer.this, "Play Previous Command Recieved", Toast.LENGTH_SHORT).show();
                           keeper="";
                       }
                       else if (keeper.equals("close") )
                       {
                           Toast.makeText(SmartMusicPlayer.this, "Exit Command recieved", Toast.LENGTH_SHORT).show();
                           mymediaPlayer.stop();
                           mymediaPlayer.release();
                           finish();
                           keeper="";
                       }
                   }


                }



            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });



        parentrelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;


                }
                return false;

            }
        });


        voiceEnablesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("ON"))
                {
                    mode="OFF";
                    voiceEnablesBtn.setText("Voice Enabled Mode: OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    mode="ON";
                    voiceEnablesBtn.setText("Voice Enabled Mode: ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });


        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playingPlayPauseSong();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mymediaPlayer.getCurrentPosition()>0)
                {
                    playPreviousSong();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mymediaPlayer.getCurrentPosition()>0)
                {
                    playNextSong();
                }
            }
        });


    }


    private void validateReciveValuesAndStartPlaying()
    {
        if (mymediaPlayer!=null)
        {
            mymediaPlayer.stop();
            mymediaPlayer.release();
        }

        position=getIntent().getIntExtra("position",-1);
        mySongs=musicFiles;
        mSongName=mySongs.get(position).getTitle();

        songNameTxt.setText(mSongName);
        songNameTxt.setSelected(true);



        uri=Uri.parse(mySongs.get(position).getPath().toString());

        mymediaPlayer=MediaPlayer.create(SmartMusicPlayer.this,uri);
       mymediaPlayer.start();
        //Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show(); commented just to check url









    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mymediaPlayer.stop();
        mymediaPlayer.release();
    }

    private void checkVoiceCommandPermission(){
        //Method to check the voice command

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(!(ContextCompat.checkSelfPermission(SmartMusicPlayer.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED))
            {
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playingPlayPauseSong() //Method to play pause function
    {

        imageView.setBackgroundResource(R.drawable.four);

        if (mymediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.play);
            mymediaPlayer.pause();
        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
            mymediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.logo1);
        }
    }

    private void playNextSong() //Method for next function
    {
        mymediaPlayer.pause();
        mymediaPlayer.stop();
        mymediaPlayer.release();

        position=((position+1)%mySongs.size());
        Uri uri=Uri.parse(mySongs.get(position).getPath().toString());

        mymediaPlayer=MediaPlayer.create(SmartMusicPlayer.this,uri);


        mSongName=mySongs.get(position).getTitle();

        songNameTxt.setText(mSongName);
        mymediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);

        if (mymediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);

        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.logo1);
        }


    }

    private void playPreviousSong()
    {
        mymediaPlayer.pause();
        mymediaPlayer.stop();
        mymediaPlayer.release();

        position=((position-1)<0?(mySongs.size()-1): (position-1));

        Uri uri=Uri.parse(mySongs.get(position).getPath().toString());
        mymediaPlayer=MediaPlayer.create(SmartMusicPlayer.this,uri);

        mSongName=mySongs.get(position).getTitle();

        songNameTxt.setText(mSongName);
        mymediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);

        if (mymediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);

        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.logo1);
        }




    }





}