package com.example.keerthana.chatbot;

import android.Manifest;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.alicebot.ab.Chat;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.os.SystemClock.sleep;

public class MainActivity extends AppCompatActivity {

    private static final int[] MY_PERMISSIONS_REQUEST = new int[5];
    String query = "CREATE TABLE IF NOT EXISTS REMINDER(NAME VARCHAR PRIMARY KEY, DATE VARCHAR, TIME VARCHAR);";
    private TextView txtSpeechInput;
    private EditText dateTxt;
    private TextView botOutput;
    private Button btnSpeak;
    DbHelper dh;
    private Chat chat;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    TextToSpeech ttobj;
    FragmentManager fm;
    FragmentTransaction ft;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.frameLayout, new ExampleComms());
        ft.commit();
        dh = new DbHelper(this, query);

        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttobj.setLanguage(Locale.UK);
            }
        });
        txtSpeechInput = (TextView) findViewById(R.id.textView);
        botOutput = (TextView) findViewById(R.id.textView2);
        btnSpeak = (Button) findViewById(R.id.button);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        getWindow().setEnterTransition(new Explode());
    }

    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry, your device doesn't support voice input.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    String ask = txtSpeechInput.getText().toString();
                    String[] words = ask.split(" ");
                    String t = new String();
                    for (int i = 1; i < words.length; i++) {
                        t = t + words[i];
                    }
                    if (txtSpeechInput.getText().toString().contains("set a reminder")) {
                        Intent in = new Intent(this, RemList.class);
                        startActivity(in, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    } else if (txtSpeechInput.getText().toString().contains("hi") || txtSpeechInput.getText().toString().contains("hello") || txtSpeechInput.getText().toString().contains("hi there")) {
                        botOutput.setText("Hi. What do you want me to do?");
                        ttobj.speak("Hi. What do you want me to do?", TextToSpeech.QUEUE_FLUSH, null);
                    } else if (words[0].contains("search")) {
                        botOutput.setText("Searching " + t);
                        ttobj.speak("Searching" + t, TextToSpeech.QUEUE_FLUSH, null);
                        sleep(3000);
                        Uri uri = Uri.parse("https://www.google.co.in/search?q=" + t);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else if (txtSpeechInput.getText().toString().contains("open camera") || txtSpeechInput.getText().toString().contains("take a picture")) {
                        botOutput.setText("Opening camera");
                        ttobj.speak("Opening camera", TextToSpeech.QUEUE_FLUSH, null);
                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/");
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    } else if (words[0].contains("call")) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", t, null)));
                    }
                    else if(words[0].contains("add")){
                        String ans = String.valueOf(Integer.parseInt(words[1]) + Integer.parseInt(words[3]));
                        botOutput.setText("The answer is " + ans);
                        ttobj.speak("The answer is " + ans, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if(words[0].contains("subtract")){
                        String ans = String.valueOf(Integer.parseInt(words[1]) - Integer.parseInt(words[3]));
                        botOutput.setText("The answer is " + ans);
                        ttobj.speak("The answer is " + ans, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if(words[0].contains("multiply")){
                        String ans = String.valueOf(Integer.parseInt(words[1]) * Integer.parseInt(words[3]));
                        botOutput.setText("The answer is " + ans);
                        ttobj.speak("The answer is " + ans, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if(words[0].contains("divide")){
                        String ans = String.valueOf(Integer.parseInt(words[1]) / Integer.parseInt(words[3]));
                        botOutput.setText("The answer is " + ans);
                        ttobj.speak("The answer is " + ans, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if(txtSpeechInput.getText().toString().contains("turn on Bluetooth")) {
                        botOutput.setText("Turning Bluetooth on");
                        ttobj.speak("Turning Bluetooth on", TextToSpeech.QUEUE_FLUSH, null);
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter != null) {
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(
                                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivity(enableBtIntent);
                            }
                        }
                    }
                    else if(txtSpeechInput.getText().toString().contains("turn on WiFi")||txtSpeechInput.getText().toString().contains("turn on wifi")) {
                        botOutput.setText("Turning Wifi on");
                        ttobj.speak("Turning Wifi on", TextToSpeech.QUEUE_FLUSH, null);
                        WifiManager wifiManager = (WifiManager)this.getSystemService(this.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                    }
                    else if(txtSpeechInput.getText().toString().contains("turn off WiFi")||txtSpeechInput.getText().toString().contains("turn off wifi")) {
                        botOutput.setText("Turning Wifi off");
                        ttobj.speak("Turning Wifi off", TextToSpeech.QUEUE_FLUSH, null);
                        WifiManager wifiManager = (WifiManager)this.getSystemService(this.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(false);
                    }
                    else if(words[0].contains("open"))
                    {
                        PackageManager manager = this.getPackageManager();
                        if(words[1].contains("Facebook")||words[1].contains("facebook")||words[1].contains("FaceBook")){
                            Intent i = manager.getLaunchIntentForPackage("com.facebook.katana");
                            i.addCategory(Intent.CATEGORY_LAUNCHER);
                            this.startActivity(i);
                        }
                        else if(words[1].contains("music")){
                            Intent i = manager.getLaunchIntentForPackage("com.google.android.music");
                            i.addCategory(Intent.CATEGORY_LAUNCHER);
                            this.startActivity(i);
                        }
                        else if(words[1].contains("youtube")||words[1].contains("youTube")||words[1].contains("YouTube")){
                            Intent i = manager.getLaunchIntentForPackage("com.google.android.youtube");
                            i.addCategory(Intent.CATEGORY_LAUNCHER);
                            this.startActivity(i);
                        }
                        else
                        {
                            botOutput.setText("I'm sorry but that's currently beyond my reach.");
                            ttobj.speak("I'm sorry but that's currently beyond my reach.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                    else {
                        botOutput.setText("I'm sorry. My developer was too foolish to add that feature.");
                        ttobj.speak("I'm sorry. My developer was too foolish to add that feature.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                break;
            }

        }
    }
}
