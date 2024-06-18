package com.example.ijk.ui;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.example.ijk.R;
import com.example.ijk.constant.PlayerEnum;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private PlayerEnum mPlayerType = PlayerEnum.IJK;//default ijk
    private boolean isSoftWareDecode = false;
    Button btnPlay;
    Button btnClear;
    EditText etUrlText;
    RadioGroup rgPlayerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        btnPlay = findViewById(R.id.mStartPlay);
        btnClear = findViewById(R.id.mClearUrl);
        etUrlText = findViewById(R.id.mPlayUrl);
        rgPlayerType = findViewById(R.id.mPlayerType);

        rgPlayerType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.mIjkPlayer:
                    mPlayerType = PlayerEnum.IJK;
                    isSoftWareDecode = false;
                    break;
                case R.id.mMediaPlayer:
                    mPlayerType = PlayerEnum.NATIVE;
                    break;
                case R.id.mExoPlayer:
                    mPlayerType = PlayerEnum.EXO;
                    break;
                case R.id.mSoftDecoder:
                    mPlayerType = PlayerEnum.IJK;
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
            bundle.putSerializable("playerType", mPlayerType);
            bundle.putBoolean("mediacodec", !isSoftWareDecode);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        btnClear.setOnClickListener(v -> etUrlText.getText().clear());
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
