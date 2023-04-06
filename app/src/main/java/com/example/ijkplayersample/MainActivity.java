package com.example.ijkplayersample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

//import com.bytedance.raphael.Raphael;

enum PlayerType {
    IJK, MEDIA, EXO
}

public class MainActivity extends AppCompatActivity {
    private PlayerType mplayerType = PlayerType.IJK;//ijk dafault
    private boolean mIsEnableHw = true;
    Button btnPlay;
    Button btnClear;
    Button btnLive1;
    Button btnLive2;
    Button btnLive3;
    Button btnLive4;
    Button btnLive5;
    Button btnLive6;
    Button btnLive7;
    Button btnVod1;
    Button btnVod2;
    Button btnVod3;
    Button btnVod4;
    Button btnVod5;
    Button btnVod6;
    Button btnVod7;
    EditText editText;
    EditText edtTracker;
    Button btnTrackerClear;
    RadioGroup rg;
    Switch p2pSwitch;
    Spinner sourceSp;
    private int mSourcePriority = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPlay = findViewById(R.id.btn_play);
        btnClear = findViewById(R.id.btn_clear);
        btnLive1 = findViewById(R.id.btn_live1);
        btnLive1.requestFocus();
        btnLive2 = findViewById(R.id.btn_live2);
        btnLive3 = findViewById(R.id.btn_live3);
        btnLive4 = findViewById(R.id.btn_live4);
        btnLive5 = findViewById(R.id.btn_live5);
        btnLive6 = findViewById(R.id.btn_live6);
        btnVod1 = findViewById(R.id.btn_vod1);
        btnVod2 = findViewById(R.id.btn_vod2);
        btnVod3 = findViewById(R.id.btn_vod3);
        btnVod4 = findViewById(R.id.btn_vod4);
        btnVod5 = findViewById(R.id.btn_vod5);
        btnVod6 = findViewById(R.id.btn_vod6);
        btnVod7 = findViewById(R.id.btn_vod7);
        btnLive7 = findViewById(R.id.btn_live7);
        editText = findViewById(R.id.player_url);
        rg = findViewById(R.id.rgPlay);
        p2pSwitch = findViewById(R.id.p2p_switch);
        sourceSp = findViewById(R.id.sp_source);
        edtTracker = findViewById(R.id.track_list);
        btnTrackerClear = findViewById(R.id.tracker_clear);
        /*Raphael.start(
            Raphael.MAP64_MODE|Raphael.ALLOC_MODE|0x0F0000|1024,
            "/storage/emulated/0/raphael", // need sdcard permission
            null
        );*///".*libijkplayer\\.so$"
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath();  //get the path

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rb1:
                        mIsEnableHw = true;
                        mplayerType = PlayerType.IJK;
                        break;
                    case R.id.rb4:
                        mIsEnableHw = false;
                        mplayerType = PlayerType.IJK;
                        break;
                    case R.id.rb2:
                        mplayerType = PlayerType.MEDIA;
                        break;
                    case R.id.rb3:
                        mplayerType = PlayerType.EXO;
                        break;
                }
            }
        });

        btnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putSerializable("playerType", mplayerType);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnClear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.getText().clear();
            }
        });

        btnTrackerClear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTracker.getText().clear();
            }
        });

        btnLive1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "live");
                bundle.putInt("sourceIndex", 1);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnLive2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "live");
                bundle.putInt("sourceIndex", 2);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnLive3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "live");
                bundle.putInt("sourceIndex", 3);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnLive4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "live");
                bundle.putInt("sourceIndex", 4);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnLive5.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "live");
                bundle.putInt("sourceIndex", 5);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnLive6.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "live");
                bundle.putInt("sourceIndex", 6);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnVod1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "vod");
                bundle.putInt("sourceIndex", 1);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnVod2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "vod");
                bundle.putInt("sourceIndex", 2);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnVod3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "vod");
                bundle.putInt("sourceIndex", 3);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnVod4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "vod");
                bundle.putInt("sourceIndex", 4);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnVod5.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "vod");
                bundle.putInt("sourceIndex", 5);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnVod6.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "vod");
                bundle.putInt("sourceIndex", 6);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnVod7.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "vod");
                bundle.putInt("sourceIndex", 7);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        btnLive7.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);

                String videoUrl = editText.getText().toString();
                boolean isEnableP2P = false;
                if(p2pSwitch.isChecked()) {
                    isEnableP2P = true;
                }
                String trackerList = edtTracker.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("playTag", "live");
                bundle.putInt("sourceIndex", 7);
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", mIsEnableHw);
                bundle.putBoolean("isEnableP2P", isEnableP2P);
                bundle.putString("trackerList", trackerList);
                bundle.putSerializable("playerType", mplayerType);
                bundle.putInt("priority", mSourcePriority);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );

        sourceSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String[] source = getResources().getStringArray(R.array.source);
                if(position == 0) {
                    mSourcePriority = 0;
                } else if(position == 1) {
                    mSourcePriority = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        //Raphael.print();
    }
}
