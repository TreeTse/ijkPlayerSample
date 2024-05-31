package com.example.ijkplayersample;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private PlayerType mPlayerType = PlayerType.IJK;//default ijk
    private boolean isSoftWareDecode = false;
    private boolean isOnCast = false;
    private boolean isOpenAd = false;
    Button btnPlay;
    Button btnClear;
    EditText etUrlText;
    RadioGroup rgPlayerType;
    ListView lvLiveProgram;
    ListView lvVodProgram;
    Switch swCast;
    Switch swAd;
    Button btnConfig;
    PlayConfig playConfig = new PlayConfig();
    PlayConfigDialog playConfigDialog = new PlayConfigDialog(dialog ->
            setPlayConfig(dialog.getPlayConfig()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        btnPlay = findViewById(R.id.mStartPlay);
        btnClear = findViewById(R.id.mClearUrl);
        etUrlText = findViewById(R.id.mPlayUrl);
        rgPlayerType = findViewById(R.id.mPlayerType);
        swCast = findViewById(R.id.mEnableCast);
        swAd = findViewById(R.id.mEnableAd);
        btnConfig = findViewById(R.id.mPlayConfig);

        lvLiveProgram = findViewById(R.id.mLiveProgram);
        String[] livePrograms = {"SBT HD(avc/ts)", "BrasilTV MOSAICO(hevc/ts/四宫格)", "TV Cultura HD(avc/ts)", "GLOBO SP HD(hevc/ts)",
                "task_udp_264ts(内网)", "task_udp_265ts(内网)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_1, livePrograms);
        lvLiveProgram.setAdapter(adapter);
        lvLiveProgram.setOnItemClickListener((parent, view, position, id) -> {
            isOnCast = swCast.isChecked();
            isOpenAd = swAd.isChecked();
            switch (position) {
                case 0:
                    gotoVideoActivity("live", 0);
                    break;
                case 1:
                    gotoVideoActivity("live", 1);
                    break;
                case 2:
                    gotoVideoActivity("live", 2);
                    break;
                case 3:
                    gotoVideoActivity("live", 3);
                    break;
                case 4:
                    gotoVideoActivity("live", 4);
                    break;
                case 5:
                    gotoVideoActivity("live", 5);
                    break;
                default:
                    break;
            }
        });

        lvVodProgram = findViewById(R.id.mVodProgram);
        String[] vodPrograms = {"Earwig and the witch(hevc/ts)", "CODA(hevc/ts)", "Tazza:One-Eyed Jack(hevc/mp4)", "A Escolha Dublado(avc/mp4)", "Avatar: The Way of Water(hevc/ts)", "Elementos(hevc/ts)",
                "Godzilla y Kong: El nuevo imperio", "test_多字幕_单源多清晰度(内网)", "test_单源多清晰度(内网)"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.list_item_1, vodPrograms);
        lvVodProgram.setAdapter(adapter2);
        lvVodProgram.setOnItemClickListener((parent, view, position, id) -> {
            isOnCast = swCast.isChecked();
            isOpenAd = swAd.isChecked();
            switch (position) {
                case 0:
                    gotoVideoActivity("vod", 0);
                    break;
                case 1:
                    gotoVideoActivity("vod", 1);
                    break;
                case 2:
                    gotoVideoActivity("vod", 2);
                    break;
                case 3:
                    gotoVideoActivity("vod", 3);
                    break;
                case 4:
                    gotoVideoActivity("vod", 4);
                    break;
                case 5:
                    gotoVideoActivity("vod", 5);
                    break;
                case 6:
                    gotoVideoActivity("vod", 6);
                    break;
                case 7:
                    gotoVideoActivity("vod", 7);
                    break;
                case 8:
                    gotoVideoActivity("vod", 8);
                    break;
                default:
                    break;
            }
        });

        rgPlayerType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.mIjkPlayer:
                    mPlayerType = PlayerType.IJK;
                    isSoftWareDecode = false;
                    break;
                case R.id.mMediaPlayer:
                    mPlayerType = PlayerType.NATIVE;
                    break;
                case R.id.mExoPlayer:
                    mPlayerType = PlayerType.EXO;
                    break;
                case R.id.mSoftDecoder:
                    mPlayerType = PlayerType.IJK;
                    isSoftWareDecode = true;
                    break;
            }
        });

        btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, VideoActivity.class);

            String videoUrl = etUrlText.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("videoUrl", videoUrl);
            bundle.putString("tag", "file");
            bundle.putSerializable("playerType", mPlayerType);
            bundle.putString("audio", playConfig.getPreferredAudio());
            bundle.putBoolean("mediacodec", !isSoftWareDecode);
            bundle.putBoolean("autoSwitchPlayer", playConfig.isAutoSwitchPlayer());
            intent.putExtras(bundle);
            startActivity(intent);
        });

        btnClear.setOnClickListener(v -> etUrlText.getText().clear());

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playConfigDialog.show(getSupportFragmentManager(), "Play Config");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void gotoVideoActivity(String tag, int code) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, VideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putInt("code", code);
        String envParams = playConfig.getEnvParams();
        if (!envParams.isEmpty()) {
            bundle.putString("params", envParams);
        } else {
            bundle.putString("params", "");
        }
        bundle.putBoolean("isOnCast", isOnCast);
        bundle.putBoolean("isOpenAd", isOpenAd);
        String expired = playConfig.getExpiredTime();
        bundle.putString("expired", expired);
        bundle.putSerializable("priority", playConfig.getSourcePriority());
        bundle.putSerializable("playerType", mPlayerType);
        bundle.putString("audio", playConfig.getPreferredAudio());
        bundle.putBoolean("mediacodec", !isSoftWareDecode);
        bundle.putBoolean("autoSwitchPlayer", playConfig.isAutoSwitchPlayer());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setPlayConfig(PlayConfig playConfig) {
        this.playConfig = playConfig;
    }
}
