package com.example.ijkplayersample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
//import com.bytedance.raphael.Raphael;

enum PlayerType {
    IJK, MEDIA, EXO
}

public class MainActivity extends AppCompatActivity {
    private PlayerType mplayerType = PlayerType.IJK;//ijk dafault
    Button btnPlay;
    Button btnClear;
    EditText editText;
    Switch hwSwitch;
    RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPlay = findViewById(R.id.btn_play);
        btnClear = findViewById(R.id.btn_clear);
        editText = findViewById(R.id.player_url);
        hwSwitch = findViewById(R.id.hw_switch);
        rg = findViewById(R.id.rgPlay);
        /*Raphael.start(
            Raphael.MAP64_MODE|Raphael.ALLOC_MODE|0x0F0000|2,
            "/storage/emulated/0/raphael", // need sdcard permission
            null
        );*///".*libijkplayer\\.so$"
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath();  //get the path

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rb1:
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
                boolean isEnableHw = false;
                if(hwSwitch.isChecked()) {
                    isEnableHw = true;
                }
                Bundle bundle = new Bundle();
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", isEnableHw);
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
