package com.example.ijkplayersample;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private int playerIndex = 0;
    private int playerType = 0;
    private int checkedItem = 0;
    private AlertDialog alertDialog;
    Button btnPlay;
    Button btnClear;
    Button btnChoose;
    EditText editText;
    Switch hwSwitch;
    RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPlay = findViewById(R.id.btn_play);
        btnClear = findViewById(R.id.btn_clear);
        //btnChoose = findViewById(R.id.btn_choose);
        editText = findViewById(R.id.player_url);
        hwSwitch = findViewById(R.id.hw_switch);
        rg = findViewById(R.id.rgPlay);
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath();  //get the path

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rb1:
                        playerType = 0;
                        break;
                    case R.id.rb2:
                        playerType = 1;
                        break;
                    case R.id.rb3:
                        playerType = 2;
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
                if(hwSwitch.isChecked() == true) {
                    isEnableHw = true;
                } else {
                    isEnableHw = false;
                }
                Bundle bundle = new Bundle();
                bundle.putString("videoUrl", videoUrl);
                bundle.putBoolean("isEnableHw", isEnableHw);
                bundle.putInt("playerType",playerType);
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

    /*public void chooseVideoPlayer(View view) {
        final String[] items = {"AndroidMediaPlayer", "ijkMediaPlayer", "IjkExoMediaPlayer"};
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Choose Player");
        alertBuilder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playerIndex = i;
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playerType = playerIndex;
                checkedItem = playerIndex;
                switch (playerType) {
                    case 0:
                        Toast.makeText(getApplicationContext()," " + items[playerType],Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext()," " + items[playerType],Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), " " + items[playerType],Toast.LENGTH_SHORT).show();
                        break;
                    default :
                        break;
                }
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.show();
    }*/

    @Override
    public void onStop() {
        super.onStop();
    }
}
