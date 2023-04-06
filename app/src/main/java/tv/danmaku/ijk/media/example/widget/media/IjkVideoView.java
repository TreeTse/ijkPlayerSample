/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.example.widget.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import swl.lib.common.SwlDeviceInfo;
import tv.danmaku.ijk.media.example.jni.RangerJniImpl;
import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.IMediaFormat;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkMediaFormat;
import com.example.ijkplayersample.R;
import com.google.gson.Gson;
import com.wisecloud.jni.NativeJni;
import com.wisecloud.jni.PlayInfo;
import com.wisecloud.jni.ProgramInfo;
import com.wisecloud.jni.RangerBeanCallback;
import com.wisecloud.jni.RangerStrCallback;
import com.wisecloud.jni.Sources;
import com.wisecloud.utils.DateCalc;
import com.wisecloud.utils.MD5Utils;

import tv.danmaku.ijk.media.example.application.Settings;
import tv.danmaku.ijk.media.example.services.MediaPlayerService;

/**** add: IJKMediaController instead of MediaController ***/
public class IjkVideoView extends FrameLayout implements IJKMediaController.MediaPlayerControl {
    private String TAG = "IjkVideoView";
    // settable by the client
    private Uri mUri;
    private String mManifestString;
    private Map<String, String> mHeaders;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private static final String sn = SwlDeviceInfo.getStbSn();//add: ranger sn
    private static String user_id;

    //add: ranger program info
    private String[] Live_ProgramInfo_Json = {
            "{\"app_ctx\":\"Live\",\"buss\":\"live\",\"delay\":15000000000,\"desc\":\"Cancao Nova\",\"lang\":\"\",\"program_code\":\"Cançãonova\",\"quality\":\"480p\",\"sources\":[{\"auth\":\"session_id=AUJpC8qKTOKB&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&ctrl_type=stb&dev_id=8e.08-22.03-11512293&app_ver=54001&group=1da45f1b165dbd6400151ff26a4b3a00&media_encrypted=0&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=E6411383D6A3DC22EED72E87F1E500A9\",\"format\":\"\",\"id\":\"br_live_other_local\",\"id_code\":\"e6fee46608f12e366ea86ada215b9fb3b7240a09d56abf60a7401c7f09d3c08e\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=D61FEE44-2B94-44E5-bc4f-3390CF023093&expired=1676366548&token=A85CDCEB55C48A8BA1BD874FFD9E2FA6\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"D61FEE44-2B94-44E5-bc4f-3390CF023093\",\"priority\":1,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=rlFMTggiRhxL&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://goruyuln.cvfr4a.com/v3/youshi/&spared_addr=http://goruyuln.cvfr4a.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=1425767A5F244E6E588EB3A5E4A6FAE0\",\"format\":\"\",\"id\":\"br_inter_google_all\",\"id_code\":\"a422b0c901c9589c29a950667b1a90d78b42f551936b0acfdb6ee3735b3120cc\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m2_Rn60mQpoOKyFd4vzs2CDEYT5_720p&expired=1676366548&token=16CDC3E820F4504DF0697246C7483F39\",\"main_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"main_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"media_code\":\"m2_Rn60mQpoOKyFd4vzs2CDEYT5_720p\",\"priority\":2,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"spared_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"tag\":\"4\",\"weight\":20},{\"auth\":\"session_id=T8tLH3l39hej&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://zenruk.rugo1o.com/v3/youshi/&spared_addr=http://zenruk.rugo1o.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=33D24693E159082D377A007554DE6EE4\",\"format\":\"\",\"id\":\"br_inter_zencdn\",\"id_code\":\"e558784fd558bdaab6841e0029fec0c7\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m2_Rn60mQpoOKyFd4vzs2CDEYT5_720p&expired=1676366548&token=16CDC3E820F4504DF0697246C7483F39\",\"main_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"main_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"media_code\":\"m2_Rn60mQpoOKyFd4vzs2CDEYT5_720p\",\"priority\":3,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"spared_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"tag\":\"4\",\"weight\":0}],\"start\":0,\"timeout\":12000000000}",
            "{\"app_ctx\":\"Live\",\"buss\":\"live\",\"delay\":15000000000,\"desc\":\"BrasilTV MOSAICO\",\"lang\":\"\",\"program_code\":\"58801347516903245112223793570182\",\"quality\":\"480p\",\"sources\":[{\"auth\":\"session_id=AUJpC8qKTOKB&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&ctrl_type=stb&dev_id=8e.08-22.03-11512293&app_ver=54001&group=1da45f1b165dbd6400151ff26a4b3a00&media_encrypted=0&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=E6411383D6A3DC22EED72E87F1E500A9\",\"format\":\"\",\"id\":\"br_live_other_local\",\"id_code\":\"e6fee46608f12e366ea86ada215b9fb3b7240a09d56abf60a7401c7f09d3c08e\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=63051503545499013251089585784865&expired=1676366548&token=0BB0DA7498BD37855004E32D8EBB7E6A\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"63051503545499013251089585784865\",\"priority\":1,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=1D9NnjpOBJ2&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&ctrl_type=stb&dev_id=8e.08-22.03-11512293&app_ver=54001&group=1da45f1b165dbd6400151ff26a4b3a00&media_encrypted=0&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=5B61F7DD1561F15BE7744E5EE933CF3B\",\"format\":\"\",\"id\":\"br_live_other_local_bak\",\"id_code\":\"e6fee46608f12e366ea86ada215b9fb32141d486230ded828cb5fad6c55b3d28\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=63051503545499013251089585784865&expired=1676366548&token=0BB0DA7498BD37855004E32D8EBB7E6A\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"63051503545499013251089585784865\",\"priority\":6,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20}],\"start\":0,\"timeout\":12000000000}",
            "{\"app_ctx\":\"Live\",\"buss\":\"live\",\"delay\":15000000000,\"desc\":\"Destaques da Copa do Nordeste\",\"lang\":\"\",\"program_code\":\"DestaquesdaCopadoNordeste_720p\",\"quality\":\"480p\",\"sources\":[{\"auth\":\"session_id=AUJpC8qKTOKB&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&ctrl_type=stb&dev_id=8e.08-22.03-11512293&app_ver=54001&group=1da45f1b165dbd6400151ff26a4b3a00&media_encrypted=0&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=E6411383D6A3DC22EED72E87F1E500A9\",\"format\":\"\",\"id\":\"br_live_other_local\",\"id_code\":\"e6fee46608f12e366ea86ada215b9fb3b7240a09d56abf60a7401c7f09d3c08e\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=pt_smjB4uUu991glkE5sH_720p&expired=1676366548&token=4701FC5A77257B76AE4866A87E0B71AC\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"pt_smjB4uUu991glkE5sH_720p\",\"priority\":1,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=rlFMTggiRhxL&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://goruyuln.cvfr4a.com/v3/youshi/&spared_addr=http://goruyuln.cvfr4a.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=1425767A5F244E6E588EB3A5E4A6FAE0\",\"format\":\"\",\"id\":\"br_inter_google_all\",\"id_code\":\"a422b0c901c9589c29a950667b1a90d78b42f551936b0acfdb6ee3735b3120cc\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m2_smjB4uUu991glkE5sH_720p&expired=1676366548&token=F1B56DB8CDAD98B384E41C96D4B4E0BB\",\"main_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"main_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"media_code\":\"m2_smjB4uUu991glkE5sH_720p\",\"priority\":2,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"spared_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"tag\":\"4\",\"weight\":20},{\"auth\":\"session_id=T8tLH3l39hej&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://zenruk.rugo1o.com/v3/youshi/&spared_addr=http://zenruk.rugo1o.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=33D24693E159082D377A007554DE6EE4\",\"format\":\"\",\"id\":\"br_inter_zencdn\",\"id_code\":\"e558784fd558bdaab6841e0029fec0c7\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m2_smjB4uUu991glkE5sH_720p&expired=1676366548&token=F1B56DB8CDAD98B384E41C96D4B4E0BB\",\"main_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"main_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"media_code\":\"m2_smjB4uUu991glkE5sH_720p\",\"priority\":3,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"spared_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"tag\":\"4\",\"weight\":0}],\"start\":0,\"timeout\":12000000000}",
            "{\"app_ctx\":\"Live\",\"buss\":\"live\",\"delay\":15000000000,\"desc\":\"TV Brasil  Esperança\",\"lang\":\"\",\"program_code\":\"TVBrasil\",\"quality\":\"480p\",\"sources\":[{\"auth\":\"session_id=AUJpC8qKTOKB&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&ctrl_type=stb&dev_id=8e.08-22.03-11512293&app_ver=54001&group=1da45f1b165dbd6400151ff26a4b3a00&media_encrypted=0&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=E6411383D6A3DC22EED72E87F1E500A9\",\"format\":\"\",\"id\":\"br_live_other_local\",\"id_code\":\"e6fee46608f12e366ea86ada215b9fb3b7240a09d56abf60a7401c7f09d3c08e\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=4D127A13-C38C-43FA-9100-453FD2B19A93&expired=1676366548&token=805E6456C33D95CE56B253164DD77FD5\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"4D127A13-C38C-43FA-9100-453FD2B19A93\",\"priority\":1,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=rlFMTggiRhxL&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://goruyuln.cvfr4a.com/v3/youshi/&spared_addr=http://goruyuln.cvfr4a.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=1425767A5F244E6E588EB3A5E4A6FAE0\",\"format\":\"\",\"id\":\"br_inter_google_all\",\"id_code\":\"a422b0c901c9589c29a950667b1a90d78b42f551936b0acfdb6ee3735b3120cc\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m2_wELX3b2rIQxpSkv7lC9ORcdY_720p&expired=1676366548&token=1043F3E6868E760AC6B6395003A2DEB8\",\"main_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"main_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"media_code\":\"m2_wELX3b2rIQxpSkv7lC9ORcdY_720p\",\"priority\":2,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"spared_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"tag\":\"4\",\"weight\":20},{\"auth\":\"session_id=T8tLH3l39hej&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://zenruk.rugo1o.com/v3/youshi/&spared_addr=http://zenruk.rugo1o.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=33D24693E159082D377A007554DE6EE4\",\"format\":\"\",\"id\":\"br_inter_zencdn\",\"id_code\":\"e558784fd558bdaab6841e0029fec0c7\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m2_wELX3b2rIQxpSkv7lC9ORcdY_720p&expired=1676366548&token=1043F3E6868E760AC6B6395003A2DEB8\",\"main_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"main_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"media_code\":\"m2_wELX3b2rIQxpSkv7lC9ORcdY_720p\",\"priority\":3,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"spared_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"tag\":\"4\",\"weight\":0}],\"start\":0,\"timeout\":12000000000}",
            "{\"app_ctx\":\"Live\",\"buss\":\"live\",\"delay\":15000000000,\"desc\":\"TV Senado\",\"lang\":\"\",\"program_code\":\"tvsen ado\",\"quality\":\"480p\",\"sources\":[{\"auth\":\"session_id=AUJpC8qKTOKB&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&ctrl_type=stb&dev_id=8e.08-22.03-11512293&app_ver=54001&group=1da45f1b165dbd6400151ff26a4b3a00&media_encrypted=0&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=E6411383D6A3DC22EED72E87F1E500A9\",\"format\":\"\",\"id\":\"br_live_other_local\",\"id_code\":\"e6fee46608f12e366ea86ada215b9fb3b7240a09d56abf60a7401c7f09d3c08e\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=20F71D57-4E89-422B-99d2-554FD865A55E&expired=1676366548&token=A5DAE03B18668F3D5A5162C9451C8DE5\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"20F71D57-4E89-422B-99d2-554FD865A55E\",\"priority\":1,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=rlFMTggiRhxL&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://goruyuln.cvfr4a.com/v3/youshi/&spared_addr=http://goruyuln.cvfr4a.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=1425767A5F244E6E588EB3A5E4A6FAE0\",\"format\":\"\",\"id\":\"br_inter_google_all\",\"id_code\":\"a422b0c901c9589c29a950667b1a90d78b42f551936b0acfdb6ee3735b3120cc\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m4_Pu6pRskc1ilGxJaKFgNeWqCO_720p&expired=1676366548&token=7ADB834A179C8BEC1DB1C4BC1AB3E1CC\",\"main_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"main_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"media_code\":\"m4_Pu6pRskc1ilGxJaKFgNeWqCO_720p\",\"priority\":2,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"spared_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"tag\":\"4\",\"weight\":20},{\"auth\":\"session_id=T8tLH3l39hej&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://zenruk.rugo1o.com/v3/youshi/&spared_addr=http://zenruk.rugo1o.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=33D24693E159082D377A007554DE6EE4\",\"format\":\"\",\"id\":\"br_inter_zencdn\",\"id_code\":\"e558784fd558bdaab6841e0029fec0c7\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m4_Pu6pRskc1ilGxJaKFgNeWqCO_720p&expired=1676366548&token=7ADB834A179C8BEC1DB1C4BC1AB3E1CC\",\"main_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"main_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"media_code\":\"m4_Pu6pRskc1ilGxJaKFgNeWqCO_720p\",\"priority\":3,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"spared_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"tag\":\"4\",\"weight\":0}],\"start\":0,\"timeout\":12000000000}",
            "{\"app_ctx\":\"Live\",\"buss\":\"live\",\"delay\":15000000000,\"desc\":\"TV Justica\",\"lang\":\"\",\"program_code\":\"tvjustica\",\"quality\":\"480p\",\"sources\":[{\"auth\":\"session_id=AUJpC8qKTOKB&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&ctrl_type=stb&dev_id=8e.08-22.03-11512293&app_ver=54001&group=1da45f1b165dbd6400151ff26a4b3a00&media_encrypted=0&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=E6411383D6A3DC22EED72E87F1E500A9\",\"format\":\"\",\"id\":\"br_live_other_local\",\"id_code\":\"e6fee46608f12e366ea86ada215b9fb3b7240a09d56abf60a7401c7f09d3c08e\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=E4A60DDB-6B9E-48E0-9c8a-1B0AFC747BA4&expired=1676366548&token=2E1BA92F588361218FD49B525EFEE2CA\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"E4A60DDB-6B9E-48E0-9c8a-1B0AFC747BA4\",\"priority\":1,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=rlFMTggiRhxL&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://goruyuln.cvfr4a.com/v3/youshi/&spared_addr=http://goruyuln.cvfr4a.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=1425767A5F244E6E588EB3A5E4A6FAE0\",\"format\":\"\",\"id\":\"br_inter_google_all\",\"id_code\":\"a422b0c901c9589c29a950667b1a90d78b42f551936b0acfdb6ee3735b3120cc\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m3_3fFPkbQ8ScCRt9wlZNH0ie6M_720p&expired=1676366548&token=698AE03D43B160061217006F8B0FC99B\",\"main_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"main_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"media_code\":\"m3_3fFPkbQ8ScCRt9wlZNH0ie6M_720p\",\"priority\":2,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://goruyuln.cvfr4a.com/v3/youshi/\",\"spared_addr_code\":\"9911b971743c45d38bd12fcf9f1d85c8beaac7e3bbda067ba53f7f0f6ef00396f50b2416e1f833c4193602a3b6c45c3d\",\"tag\":\"4\",\"weight\":20},{\"auth\":\"session_id=T8tLH3l39hej&app_id=com.interactive.brasiliptv&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=218.18.7.49&dev_id=8e.08-22.03-11512293&app_ver=54001&media_encrypted=0&main_addr=http://zenruk.rugo1o.com/v3/youshi/&spared_addr=http://zenruk.rugo1o.com/v3/youshi/&user_id=70157234&expired=1675776321&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=33D24693E159082D377A007554DE6EE4\",\"format\":\"\",\"id\":\"br_inter_zencdn\",\"id_code\":\"e558784fd558bdaab6841e0029fec0c7\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=m3_3fFPkbQ8ScCRt9wlZNH0ie6M_720p&expired=1676366548&token=698AE03D43B160061217006F8B0FC99B\",\"main_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"main_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"media_code\":\"m3_3fFPkbQ8ScCRt9wlZNH0ie6M_720p\",\"priority\":3,\"quality\":\"480p\",\"rule_id_code\":\"cba60ae4d5f7654fe81d9564fa878b7a\",\"spared_addr\":\"http://zenruk.rugo1o.com/v3/youshi/\",\"spared_addr_code\":\"5c211e4b7aceb1a6a055a2e85872a754e18069e2dbc521ed6ab16e56bd0733aee61c9cd27ce3f7efdc88677e837fe8e0\",\"tag\":\"4\",\"weight\":0}],\"start\":0,\"timeout\":12000000000}"
    };

    //add: ranger program code
    private static final String[] Live_Program_Code = {
            "Cançãonova",
            "58801347516903245112223793570182",
            "DestaquesdaCopadoNordeste_720p",
            "TVBrasil",
            "tvsen ado",
            "tvjustica"
    };

    private static final String[] Live_Program_Desc = {
            "Cancao Nova",
            "BrasilTV MOSAICO",
            "Destaques da Copa do Nordeste",
            "TV Brasil  Esperança",
            "TV Senado",
            "TV Justica"
    };

    private String[] Vod_ProgramInfo_Json = {
            "{\"app_ctx\":\"vod\",\"buss\":\"vod\",\"delay\":15000000000,\"desc\":\"Âya to majo\",\"lang\":\"por,jpn\",\"program_code\":\"152DF1FC2DD69580776BFA13E0EF9C8C\",\"quality\":\"1080p\",\"sources\":[{\"auth\":\"session_id=DeVyMJ5tyjWy&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=bramslb.abzhlslb.com&cdn_type=1&spared_addr=mslb.ttuvc.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=2B215EEDC2020CCC5FDA59E95D36F275\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"por,jpn\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=5ECDF91380484D429F530CB1DED133CF&expired=1675852166&token=4DE6684F2D501DBD6F29A48F2C290B24\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"5ECDF91380484D429F530CB1DED133CF\",\"priority\":1,\"quality\":\"1080p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=sl571BEPb9nI&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=http://lmvd.whitl12vd.com&cdn_type=4&spared_addr=http://lmvd.whitl12vd.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=FB6151E969A100AE6A1076B8E3AE3667\",\"format\":\"\",\"id\":\"vod_aws_lambda_new\",\"id_code\":\"9e03f46c7ca6e7e137c83c6f615e72cf9497419a719fd565220f62016c4991f5\",\"lang\":\"por,jpn\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=5ECDF91380484D429F530CB1DED133CF&expired=1675852166&token=4DE6684F2D501DBD6F29A48F2C290B24\",\"main_addr\":\"http://lmvd.whitl12vd.com\",\"main_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"media_code\":\"5ECDF91380484D429F530CB1DED133CF\",\"priority\":2,\"quality\":\"1080p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"http://lmvd.whitl12vd.com\",\"spared_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"tag\":\"4\",\"weight\":0},{\"auth\":\"session_id=DeVyMJ5tyjWy&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=bramslb.abzhlslb.com&cdn_type=1&spared_addr=mslb.ttuvc.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=2B215EEDC2020CCC5FDA59E95D36F275\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"unkown\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=63DDCB7A427C46F38027B615FC9A832B&expired=1675852166&token=478C3097F54E999731FD8D826A6AF1A1\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"63DDCB7A427C46F38027B615FC9A832B\",\"priority\":1,\"quality\":\"1080p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20}],\"start\":0,\"timeout\":12000000000}",
            "{\"app_ctx\":\"vod\",\"buss\":\"vod\",\"delay\":15000000000,\"desc\":\"Rio Sangrento\",\"lang\":\"por\",\"program_code\":\"F3801D00DE4511E9B458AC1F6B68ECB2\",\"quality\":\"720p\",\"sources\":[{\"auth\":\"session_id=DeVyMJ5tyjWy&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=bramslb.abzhlslb.com&cdn_type=1&spared_addr=mslb.ttuvc.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=2B215EEDC2020CCC5FDA59E95D36F275\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"por\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=EBA087BE86AA44D58835E865314AD365&expired=1675852486&token=07E1703C9325D1C1F4B11736A5DA3CB7\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"EBA087BE86AA44D58835E865314AD365\",\"priority\":1,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=sl571BEPb9nI&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=http://lmvd.whitl12vd.com&cdn_type=4&spared_addr=http://lmvd.whitl12vd.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=FB6151E969A100AE6A1076B8E3AE3667\",\"format\":\"\",\"id\":\"vod_aws_lambda_new\",\"id_code\":\"9e03f46c7ca6e7e137c83c6f615e72cf9497419a719fd565220f62016c4991f5\",\"lang\":\"por\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=EBA087BE86AA44D58835E865314AD365&expired=1675852486&token=07E1703C9325D1C1F4B11736A5DA3CB7\",\"main_addr\":\"http://lmvd.whitl12vd.com\",\"main_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"media_code\":\"EBA087BE86AA44D58835E865314AD365\",\"priority\":2,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"http://lmvd.whitl12vd.com\",\"spared_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"tag\":\"4\",\"weight\":0}],\"start\":0,\"timeout\":20000000000}",
            "{\"app_ctx\":\"vod\",\"buss\":\"vod\",\"delay\":15000000000,\"desc\":\"Vale dos Cangurus\",\"lang\":\"por\",\"program_code\":\"F826F19C23DAD9F933C41C3FC140B359\",\"quality\":\"1080p\",\"sources\":[{\"auth\":\"session_id=DeVyMJ5tyjWy&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=bramslb.abzhlslb.com&cdn_type=1&spared_addr=mslb.ttuvc.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=2B215EEDC2020CCC5FDA59E95D36F275\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"por\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=E9D297728C6B42B7824AC1283CD8D5C5&expired=1675852576&token=F465A6E6091E28BC9D91D53808FFD6DD\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"E9D297728C6B42B7824AC1283CD8D5C5\",\"priority\":1,\"quality\":\"1080p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=sl571BEPb9nI&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=http://lmvd.whitl12vd.com&cdn_type=4&spared_addr=http://lmvd.whitl12vd.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=FB6151E969A100AE6A1076B8E3AE3667\",\"format\":\"\",\"id\":\"vod_aws_lambda_new\",\"id_code\":\"9e03f46c7ca6e7e137c83c6f615e72cf9497419a719fd565220f62016c4991f5\",\"lang\":\"por\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=E9D297728C6B42B7824AC1283CD8D5C5&expired=1675852576&token=F465A6E6091E28BC9D91D53808FFD6DD\",\"main_addr\":\"http://lmvd.whitl12vd.com\",\"main_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"media_code\":\"E9D297728C6B42B7824AC1283CD8D5C5\",\"priority\":2,\"quality\":\"1080p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"http://lmvd.whitl12vd.com\",\"spared_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"tag\":\"4\",\"weight\":0},{\"auth\":\"session_id=DeVyMJ5tyjWy&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=bramslb.abzhlslb.com&cdn_type=1&spared_addr=mslb.ttuvc.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=2B215EEDC2020CCC5FDA59E95D36F275\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"por\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=2B42F4FBE21249D8AE36144C20A9E96E&expired=1675852576&token=BE1A9F854D7B5E12A226BB0702C2D6DA\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"2B42F4FBE21249D8AE36144C20A9E96E\",\"priority\":1,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20}],\"start\":0,\"timeout\":20000000000}",
            "{\"app_ctx\":\"vod\",\"buss\":\"vod\",\"delay\":15000000000,\"desc\":\"Puss in Boots: The Last Wish\",\"lang\":\"por,eng\",\"program_code\":\"91FFFF4C59FCCBFC797C267D3CDC41A2\",\"quality\":\"1080p\",\"sources\":[{\"auth\":\"session_id=DeVyMJ5tyjWy&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=bramslb.abzhlslb.com&cdn_type=1&spared_addr=mslb.ttuvc.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=2B215EEDC2020CCC5FDA59E95D36F275\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"por,eng\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=03DA8C112FA84F3296B4BBAC1FA73F53&expired=1675852665&token=B7DBDCE52AD46CE60EB6CF1E72AE71F1\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"03DA8C112FA84F3296B4BBAC1FA73F53\",\"priority\":1,\"quality\":\"1080p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=sl571BEPb9nI&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=http://lmvd.whitl12vd.com&cdn_type=4&spared_addr=http://lmvd.whitl12vd.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=FB6151E969A100AE6A1076B8E3AE3667\",\"format\":\"\",\"id\":\"vod_aws_lambda_new\",\"id_code\":\"9e03f46c7ca6e7e137c83c6f615e72cf9497419a719fd565220f62016c4991f5\",\"lang\":\"por,eng\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=03DA8C112FA84F3296B4BBAC1FA73F53&expired=1675852665&token=B7DBDCE52AD46CE60EB6CF1E72AE71F1\",\"main_addr\":\"http://lmvd.whitl12vd.com\",\"main_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"media_code\":\"03DA8C112FA84F3296B4BBAC1FA73F53\",\"priority\":2,\"quality\":\"1080p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"http://lmvd.whitl12vd.com\",\"spared_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"tag\":\"4\",\"weight\":0},{\"auth\":\"session_id=DeVyMJ5tyjWy&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&ctrl_type=stb&client_ip=116.30.199.35&main_addr=bramslb.abzhlslb.com&cdn_type=1&spared_addr=mslb.ttuvc.com&media_encrypted=0&group=a02d548b90a53d65e135c993439c6ed4&user_id=70157234&app_ver=54001&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&expired=1675780110&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=2B215EEDC2020CCC5FDA59E95D36F275\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"por,eng\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=AB645C0D4893489BB9ABEF9355C1D495&expired=1675852665&token=AAEEC450B610755C5663152BBE9D5A6D\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"AB645C0D4893489BB9ABEF9355C1D495\",\"priority\":1,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20}],\"start\":0,\"timeout\":20000000000}",
            "{\"app_ctx\":\"vod\",\"buss\":\"vod\",\"delay\":15000000000,\"desc\":\"Trap for Cinderella\",\"lang\":\"eng\",\"program_code\":\"253F0B2E98D711E9B0BAAC1F6B68ECB2\",\"quality\":\"720p\",\"sources\":[{\"auth\":\"session_id=DwwCrXccpzy5&media_encrypted=0&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=116.30.199.96&ctrl_type=stb&spared_addr=mslb.ttuvc.com&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&app_ver=54001&group=a02d548b90a53d65e135c993439c6ed4&cdn_type=1&main_addr=bramslb.abzhlslb.com&user_id=70157234&expired=1676461061&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=EB0785BA386A7C1C8D1658C6E8D8EFA2\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"eng\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=D43CF8EA5D0342CA83A9DA70DCFD60C7&expired=1676533197&token=08A50C5BA6B52A38DCD1B46A33AF897B\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"D43CF8EA5D0342CA83A9DA70DCFD60C7\",\"priority\":1,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"session_id=nbFWfIwCXkhc&media_encrypted=0&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=116.30.199.96&ctrl_type=stb&spared_addr=http://lmvd.whitl12vd.com&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&app_ver=54001&group=a02d548b90a53d65e135c993439c6ed4&cdn_type=4&main_addr=http://lmvd.whitl12vd.com&user_id=70157234&expired=1676461061&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=40D0CF64F314D48C7D4DD303D36EFEC3\",\"format\":\"\",\"id\":\"vod_aws_lambda_new\",\"id_code\":\"9e03f46c7ca6e7e137c83c6f615e72cf9497419a719fd565220f62016c4991f5\",\"lang\":\"eng\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=D43CF8EA5D0342CA83A9DA70DCFD60C7&expired=1676533197&token=08A50C5BA6B52A38DCD1B46A33AF897B\",\"main_addr\":\"http://lmvd.whitl12vd.com\",\"main_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"media_code\":\"D43CF8EA5D0342CA83A9DA70DCFD60C7\",\"priority\":2,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"http://lmvd.whitl12vd.com\",\"spared_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"tag\":\"4\",\"weight\":0},{\"auth\":\"session_id=DwwCrXccpzy5&media_encrypted=0&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&client_ip=116.30.199.96&ctrl_type=stb&spared_addr=mslb.ttuvc.com&app_id=com.interactive.brasiliptv&dev_id=8e.08-22.03-11512293&app_ver=54001&group=a02d548b90a53d65e135c993439c6ed4&cdn_type=1&main_addr=bramslb.abzhlslb.com&user_id=70157234&expired=1676461061&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=EB0785BA386A7C1C8D1658C6E8D8EFA2\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"eng\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=FA9CCCAB57E84C8BB56573C3E097EA42&expired=1676533197&token=3C8BA07BB230F50835BFD9F424676C08\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"FA9CCCAB57E84C8BB56573C3E097EA42\",\"priority\":1,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20}],\"start\":0,\"timeout\":12000000000}",
            "{\"app_ctx\":\"vod\",\"buss\":\"vod\",\"delay\":15000000000,\"desc\":\"Trolls: TrollsTopia Temp.4\",\"lang\":\"und\",\"program_code\":\"F58E5D50528D11ECB13E001E67221C7E\",\"quality\":\"720p\",\"sources\":[{\"auth\":\"dev_id=8e.08-22.03-11512293&ctrl_type=stb&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&group=a02d548b90a53d65e135c993439c6ed4&client_ip=116.30.199.96&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&session_id=diBdAUBSokCO&media_encrypted=0&app_id=com.interactive.brasiliptv&user_id=70157234&app_ver=54001&expired=1676467047&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=240D01C575C0FF542363133ED9890ED0\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"und\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=F602E486528D11ECB13E001E67221C7E&expired=1676539419&token=3C92791B45FDA51B63EC274F67D48FA7\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"F602E486528D11ECB13E001E67221C7E\",\"priority\":1,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20},{\"auth\":\"dev_id=8e.08-22.03-11512293&ctrl_type=stb&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&group=a02d548b90a53d65e135c993439c6ed4&client_ip=116.30.199.96&cdn_type=4&main_addr=http://lmvd.whitl12vd.com&spared_addr=http://lmvd.whitl12vd.com&session_id=2QgKl1ENamUU&media_encrypted=0&app_id=com.interactive.brasiliptv&user_id=70157234&app_ver=54001&expired=1676467047&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=16BDEB2A8831A275BFA08175149BD057\",\"format\":\"\",\"id\":\"vod_aws_lambda_new\",\"id_code\":\"9e03f46c7ca6e7e137c83c6f615e72cf9497419a719fd565220f62016c4991f5\",\"lang\":\"und\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=F602E486528D11ECB13E001E67221C7E&expired=1676539419&token=3C92791B45FDA51B63EC274F67D48FA7\",\"main_addr\":\"http://lmvd.whitl12vd.com\",\"main_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"media_code\":\"F602E486528D11ECB13E001E67221C7E\",\"priority\":2,\"quality\":\"720p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"http://lmvd.whitl12vd.com\",\"spared_addr_code\":\"5beb883ca6aceacdd47a9132e4b61a11e7db901ac800007c7433a87bbbf709ed\",\"tag\":\"4\",\"weight\":0},{\"auth\":\"dev_id=8e.08-22.03-11512293&ctrl_type=stb&auth_id=70157234_com.interactive.brasiliptv_cloud-c2-redis_0&group=a02d548b90a53d65e135c993439c6ed4&client_ip=116.30.199.96&cdn_type=1&main_addr=bramslb.abzhlslb.com&spared_addr=mslb.ttuvc.com&session_id=diBdAUBSokCO&media_encrypted=0&app_id=com.interactive.brasiliptv&user_id=70157234&app_ver=54001&expired=1676467047&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&token=240D01C575C0FF542363133ED9890ED0\",\"format\":\"\",\"id\":\"br_vod_vivo_local\",\"id_code\":\"0435cfdc2f9ca55797d0fa646ed11689204612c74a2bf93da9e19212a13df3da\",\"lang\":\"und\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=426BAABE528F11ECB13E001E67221C7E&expired=1676539419&token=CC3B9C246568851A109F3CC63512E878\",\"main_addr\":\"bramslb.abzhlslb.com\",\"main_addr_code\":\"4b8c8196ffbc5747dd3cc2a5d879cdaf82376eed75d5b41a50f2dbf7e75d654d\",\"media_code\":\"426BAABE528F11ECB13E001E67221C7E\",\"priority\":1,\"quality\":\"480p\",\"rule_id_code\":\"2eadb263b752e203c110212eb68cfe09\",\"spared_addr\":\"mslb.ttuvc.com\",\"spared_addr_code\":\"1dbd17c791bccf442e35ca7eda79eff5\",\"tag\":\"1\",\"weight\":20}],\"start\":0,\"timeout\":20000000000}"
    };

    private static final String[] Vod_Program_Code = {
            "152DF1FC2DD69580776BFA13E0EF9C8C",
            "F3801D00DE4511E9B458AC1F6B68ECB2",
            "F826F19C23DAD9F933C41C3FC140B359",
            "91FFFF4C59FCCBFC797C267D3CDC41A2",
            "253F0B2E98D711E9B0BAAC1F6B68ECB2",
            "F58E5D50528D11ECB13E001E67221C7E"
    };

    private static final String[] Vod_Program_Desc = {
            "Âya to majo",
            "Rio Sangrento",
            "Vale dos Cangurus",
            "Puss in Boots: The Last Wish",
            "Trap for Cinderella",
            "Trolls: TrollsTopia Temp.4"
    };

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    // All the stuff we need for playing and showing a video
    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    private IMediaPlayer mMediaPlayer = null;
    // private int         mAudioSession;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private IMediaController mMediaController;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;
    private int mInstance = 0;//add: ranger instance
    private String mExpiredTime = "";//add: ranger expired time

    /** Subtitle rendering widget overlaid on top of the video. */
    // private RenderingWidget mSubtitleWidget;

    /**
     * Listener for changes to subtitle data, used to redraw when needed.
     */
    // private RenderingWidget.OnChangedListener mSubtitlesChangedListener;

    private Context mAppContext;
    private Settings mSettings;
    private IRenderView mRenderView;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private InfoHudViewHolder mHudViewHolder;

    private long mPrepareStartTime = 0;
    private long mPrepareEndTime = 0;

    private long mSeekStartTime = 0;
    private long mSeekEndTime = 0;

    private TextView subtitleDisplay;

    private boolean mIsEnableHw = false;
    private TextView mTvLog;//add: view log
    private int mTouchCount = 0;
    private boolean mIsRangerMode = false;//add: enable/disable ranger
    private RangerJniImpl mRangerJniImpl = null;//add: ranger
    private int mRangerSourceIndex = 0;
    private String mRangerPlayTag = "";
    private Handler mRangerHandler;//add: ranger test
    private Runnable mRangerTask;
    private Handler mRangerLogHandler;//add: ranger log
    private Runnable mRangerLogTask;
    private String mRangerDashBoard = "";//add: playinfo
    private int mSourcePriority = 0;//add: icdn or aws
    private String mTrackerList = "199.189.86.249:5333,5.180.41.123:5333";

    public IjkVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    // REMOVED: onMeasure
    // REMOVED: onInitializeAccessibilityEvent
    // REMOVED: onInitializeAccessibilityNodeInfo
    // REMOVED: resolveAdjustedSize

    public void setPlayerType(int type) {
        mSettings.setPlayerType(type);
    }

    public void setEnableHw(boolean isEnableHw) {
        mIsEnableHw = isEnableHw;
    }

    /* add: test ranger */
    private void initRangerTask() {
        mRangerHandler = new Handler();
        mRangerTask = new Runnable() {
            @Override
            public void run() {
                int delayTime = (int)(Math.random() * 50 * 1000);
                Log.d(TAG, "switch source, delayTime: " + delayTime);
                mRangerHandler.postDelayed(this, delayTime);
                switchRangerStream(mRangerSourceIndex, mRangerSourceIndex + 1);
            }
        };
    }

    /* add: ranger */
    public void releaseRangerTask() {
        if(mRangerHandler != null) {
            mRangerHandler.removeCallbacksAndMessages(null);
            mRangerHandler = null;
        }
    }

    /* add: ranger playinfo */
    private void initRangerLogTask() {
        mRangerLogHandler = new Handler();
        mRangerLogTask = new Runnable() {
            @Override
            public void run() {
                mRangerLogHandler.postDelayed(this, 1000);
                RangerBeanCallback rangerBeanCallback = new RangerBeanCallback() {
                    @Override
                    public void callback(PlayInfo result) {
                        if(result == null) return;
                        Log.d(TAG, "getPullStreamState res: " + result.toString());
                        mRangerDashBoard = result.getDashboard();
                    }
                };
                if(mRangerPlayTag.equals("vod"))
                    mRangerJniImpl.getStreamState(mInstance, Vod_Program_Code[mRangerSourceIndex - 1], rangerBeanCallback);
                else if(mRangerPlayTag.equals("live"))
                    mRangerJniImpl.getStreamState(mInstance, Live_Program_Code[mRangerSourceIndex - 1], rangerBeanCallback);
            }
        };
    }

    public void releaseRangerLogTask() {
        if(mRangerLogTask != null) {
            mRangerLogHandler.removeCallbacksAndMessages(null);
            mRangerLogHandler = null;
        }
    }

    public String getDashBoard() {
        return mRangerDashBoard;
    }

    /* add: ranger expired time */
    private void calcExpiredTime(String timeFormat) {
        DateCalc dateCalc = new DateCalc();
        dateCalc.setTimeFormat(timeFormat);
        mExpiredTime = dateCalc.date2TimeStamp(dateCalc.getExpiredTime());
    }

    /* add: ranger json */
    private void updateVodJson(int index) {
        String auth_salt = "C3D758E0BA554F9DAA9BC54F58889A5418C0E53A6E7442AD840B59543442BB31";
        String license_salt = "U2RFXEeR4oPmdPlQoPGvG7hGDr0p4zd7QEWYTkjgtp5Q6nVOf6p6s8txNTfjV6i4";
        String new_expired = "expired=" + mExpiredTime;
        Gson gson = new Gson();
        ProgramInfo programInfo = gson.fromJson(Vod_ProgramInfo_Json[index - 1],ProgramInfo.class);
        List<Sources> sources = programInfo.getSources();
        for(int j = 0; j < sources.size(); j++) {
            /************ 处理auth **********/
            String auth = sources.get(j).getAuth();
            auth = auth.replaceAll("expired=[0-9]+", new_expired);
            auth = auth.replaceAll("(?<=dev_id=)[^&]*", sn);
            auth = auth.replaceAll("(?<=user_id=)[^&]*", user_id);

            String auth_md5Data = "";
            String auth_token;
            String auth_regex = "&token";
            Pattern pattern = Pattern.compile(auth_regex);
            Matcher matcher = pattern.matcher(auth);
            if (matcher.find()) {
                int start = matcher.start();
                String before_token = auth.substring(0, start);
                auth_md5Data = before_token + auth_salt;
            }
            auth_token = MD5Utils.string2MD5(auth_md5Data);

            String new_token = "token=" + auth_token;
            String recalc_auth = auth.replaceAll("token=.*", new_token);
            sources.get(j).setAuth(recalc_auth);//使修改后的auth在source中生效

            /************ 处理license **********/
            String license = sources.get(j).getLicense();
            license = license.replaceAll("expired=[0-9]+", new_expired);
            String license_md5Data = "";
            String license_token;
            String license_regex = "&token";
            Pattern pattern2 = Pattern.compile(license_regex);
            Matcher matcher2 = pattern2.matcher(license);
            if (matcher2.find()) {
                int start = matcher2.start();
                String before_token = license.substring(0, start);
                license_md5Data = before_token + license_salt;
            }
            license_token = MD5Utils.string2MD5(license_md5Data);

            new_token = "token=" + license_token;
            String recalc_license = license.replaceAll("token=.*", new_token);
            sources.get(j).setLicense(recalc_license);//使修改后的license在source中生效

            /************ 处理priority **********/
            if(mSourcePriority == 1) {
                int priority = sources.get(j).getPriority();
                if(priority == 1) {
                    sources.get(j).setPriority(2);
                } else if(priority == 2) {
                    sources.get(j).setPriority(1);
                }
            }
        }
        programInfo.setSources(sources);//使所有更改的sources生效
        Vod_ProgramInfo_Json[index - 1] = gson.toJson(programInfo);
    }

    private void updateLiveJson(int index) {
        String auth_salt = "C3D758E0BA554F9DAA9BC54F58889A5418C0E53A6E7442AD840B59543442BB31";
        String license_salt = "U2RFXEeR4oPmdPlQoPGvG7hGDr0p4zd7QEWYTkjgtp5Q6nVOf6p6s8txNTfjV6i4";
        String new_expired = "expired=" + mExpiredTime;
        Gson gson = new Gson();
        ProgramInfo programInfo = gson.fromJson(Live_ProgramInfo_Json[index - 1],ProgramInfo.class);
        List<Sources> sources = programInfo.getSources();
        for(int j = 0; j < sources.size(); j++) {
            /************ 处理auth **********/
            String auth = sources.get(j).getAuth();
            auth = auth.replaceAll("expired=[0-9]+", new_expired);
            auth = auth.replaceAll("(?<=dev_id=)[^&]*", sn);
            auth = auth.replaceAll("(?<=user_id=)[^&]*", user_id);

            String auth_md5Data = "";
            String auth_token;
            String auth_regex = "&token";
            Pattern pattern = Pattern.compile(auth_regex);
            Matcher matcher = pattern.matcher(auth);
            if (matcher.find()) {
                int start = matcher.start();
                String before_token = auth.substring(0, start);
                auth_md5Data = before_token + auth_salt;
            }
            auth_token = MD5Utils.string2MD5(auth_md5Data);

            String new_token = "token=" + auth_token;
            String recalc_auth = auth.replaceAll("token=.*", new_token);
            sources.get(j).setAuth(recalc_auth);//使修改后的auth在source中生效

            /************ 处理license **********/
            String license = sources.get(j).getLicense();
            license = license.replaceAll("expired=[0-9]+", new_expired);
            String license_md5Data = "";
            String license_token;
            String license_regex = "&token";
            Pattern pattern2 = Pattern.compile(license_regex);
            Matcher matcher2 = pattern2.matcher(license);
            if (matcher2.find()) {
                int start = matcher2.start();
                String before_token = license.substring(0, start);
                license_md5Data = before_token + license_salt;
            }
            license_token = MD5Utils.string2MD5(license_md5Data);

            new_token = "token=" + license_token;
            String recalc_license = license.replaceAll("token=.*", new_token);
            sources.get(j).setLicense(recalc_license);//使修改后的license在source中生效

            /************ 处理priority **********/
            if(mSourcePriority == 1) {
                int priority = sources.get(j).getPriority();
                if(priority == 1) {
                    sources.get(j).setPriority(3);
                } else if(priority > 2) {
                    sources.get(j).setPriority(1);
                }
            }
        }
        programInfo.setSources(sources);//使所有更改的sources生效
        Live_ProgramInfo_Json[index - 1] = gson.toJson(programInfo);
    }

    /*** add: set ranger and start ***/
    public void setRangerMode(boolean isRangerMode, IjkVideoView videoView, AppCompatActivity activity, String playTag, int sourceIndex, boolean enableP2P, int priority, String trackerList) {
        mIsRangerMode = isRangerMode;
        if(mIsRangerMode) {
            user_id = String.valueOf(sn.hashCode() % (int)Math.pow(10, 8));
            mSourcePriority = priority;
            mRangerJniImpl = new RangerJniImpl(videoView);
            mRangerJniImpl.setJniCallBack(activity);
            String config_json;
            if(enableP2P) {
                //config_json = "{\"advertising_id\":\"\",\"android_id\":\"e3b3934db7f8e39a\",\"app\":\"com.interactive.brasiliptv\",\"app_version\":\"54001\",\"ca_info\":\"/data/user/0/com.interactive.brasiliptv/files/cacert.pem\",\"communication_key\":\"6a6f4d9f-69a9-43f3-9244-5012ba6d4ecc\",\"dev_id\":\"\",\"params\":\"exp=3&max_cartons_1min=2&max_carton_duration_1min=15&last_retries=5&svs_address=xsvs.vfltbr.com:18084&svs_address_spare=xsvs.evlslb.com:18084&live_pcdn_mode=p2sp&tracker_list=84.17.45.67:5333,199.189.86.249:5333,5.180.41.123:5333&vod_proxy=0&delay_ref=30&min_cache=15&max_cache=30&autodelay_icdn_min_delay=15&autodelay_icdn_max_delay=30&autodelay_icdn_enabled=1&http_stream_recv_timeout=13&blacklist_clear=1&source_weights_clear=1&source_weights_enabled=0&live_pcdn_xtimeout=2&blacklist_enabled=0&transmit_protocol=http&min_peers=8&max_peers=10&limit_min_rate=400000&star_proxy=1&mem_cache_enable=on&hls_min_cache=60&hls_max_cache=60&live_hls_pcdn_mode=p2sp&max_switch_sources=20&max_switch_source_round=10\",\"player\":\"ijk\",\"sn\":\"8e.08-22.03-11512293\",\"user_id\":\"70157234\"}";
                config_json = "{\"advertising_id\":\"\",\"android_id\":\"e3b3934db7f8e39a\",\"app\":\"com.interactive.brasiliptv\",\"app_version\":\"54001\",\"ca_info\":\"/data/user/0/com.interactive.brasiliptv/files/cacert.pem\",\"communication_key\":\"6a6f4d9f-69a9-43f3-9244-5012ba6d4ecc\",\"dev_id\":\"\",\"params\":\"live_pcdn_mode=p2sp&tracker_list=199.189.86.249:5333,5.180.41.123:5333&delay_ref=30&min_cache=7&max_cache=30&live_hls_pcdn_mode=p2sp&star_proxy=1&mem_cache_enable=on&min_peers=8&max_peers=10&limit_min_rate=400000&source_weights_enabled=0&blacklist_enabled=0&transmit_protocol=cdp_2.0&max_switch_sources=20&max_switch_source_round=10\",\"player\":\"ijk\",\"sn\":\"" + sn + "\",\"user_id\":\"" + user_id + "\"}";
            } else {
                //config_json = "{\"advertising_id\":\"\",\"android_id\":\"e3b3934db7f8e39a\",\"app\":\"com.interactive.brasiliptv\",\"app_version\":\"54001\",\"ca_info\":\"/data/user/0/com.interactive.brasiliptv/files/cacert.pem\",\"communication_key\":\"6a6f4d9f-69a9-43f3-9244-5012ba6d4ecc\",\"dev_id\":\"\",\"params\":\"exp=3&max_cartons_1min=2&max_carton_duration_1min=15&last_retries=5&svs_address=xsvs.vfltbr.com:18084&svs_address_spare=xsvs.evlslb.com:18084&live_pcdn_mode=off&tracker_list=84.17.45.67:5333,199.189.86.249:5333,5.180.41.123:5333&vod_proxy=0&delay_ref=30&min_cache=15&max_cache=30&autodelay_icdn_min_delay=15&autodelay_icdn_max_delay=30&autodelay_icdn_enabled=1&http_stream_recv_timeout=13&blacklist_clear=1&source_weights_clear=1&source_weights_enabled=0&live_pcdn_xtimeout=2&blacklist_enabled=0&transmit_protocol=http&min_peers=0&max_peers=0&limit_min_rate=400000&star_proxy=1&mem_cache_enable=on&hls_min_cache=60&hls_max_cache=60&live_hls_pcdn_mode=off&max_switch_sources=20&max_switch_source_round=10\",\"player\":\"ijk\",\"sn\":\"" + sn + "\",\"user_id\":\"70157234\"}";
                config_json = "{\"advertising_id\":\"\",\"android_id\":\"e3b3934db7f8e39a\",\"app\":\"com.interactive.brasiliptv\",\"app_version\":\"54001\",\"ca_info\":\"/data/user/0/com.interactive.brasiliptv/files/cacert.pem\",\"communication_key\":\"6a6f4d9f-69a9-43f3-9244-5012ba6d4ecc\",\"dev_id\":\"\",\"params\":\"live_pcdn_mode=off&tracker_list=199.189.86.249:5333,5.180.41.123:5333&delay_ref=30&min_cache=7&max_cache=30&live_hls_pcdn_mode=off&star_proxy=1&mem_cache_enable=on&min_peers=0&max_peers=0&limit_min_rate=400000&source_weights_enabled=0&blacklist_enabled=0&transmit_protocol=cdp_2.0&max_switch_sources=20&max_switch_source_round=10\",\"player\":\"ijk\",\"sn\":\"" + sn + "\",\"user_id\":\"" + user_id + "\"}";
            }
            if(!mTrackerList.equals(trackerList) && !trackerList.equals("")) {
                config_json = config_json.replaceAll("(?<=tracker_list=)[^&]*", trackerList);
            }
            Log.i(TAG, "config json info: " + config_json);
            NativeJni.getJni().setRangerConfig(config_json);
            calcExpiredTime("yyyy-MM-dd HH:mm:ss");
            if(playTag.equals("vod")) {
                if(sourceIndex == 7) {
                    sourceIndex = 1;
                    updateVodJson(sourceIndex);
                    mRangerJniImpl.prepareProgram(mInstance, Vod_Program_Code[sourceIndex - 1], Vod_ProgramInfo_Json[sourceIndex - 1]);
                    mRangerSourceIndex = sourceIndex;
                    initRangerTask();
                    mRangerHandler.postDelayed(mRangerTask, 8000);
                } else {
                    updateVodJson(sourceIndex);
                    mRangerJniImpl.prepareProgram(mInstance, Vod_Program_Code[sourceIndex - 1], Vod_ProgramInfo_Json[sourceIndex - 1]);
                    Log.d(TAG, "programinfo json: " + Vod_ProgramInfo_Json[sourceIndex - 1]);
                    mRangerSourceIndex = sourceIndex;
                    addLogTv(mTvLog, "prepare vod program: " + sourceIndex + ". " + Vod_Program_Desc[sourceIndex - 1]);
                }
            }
            else if(playTag.equals("live")) {
                if(sourceIndex == 7) {
                    sourceIndex = 1;
                    updateLiveJson(sourceIndex);
                    mRangerJniImpl.prepareProgram(mInstance, Live_Program_Code[sourceIndex - 1], Live_ProgramInfo_Json[sourceIndex - 1]);
                    mRangerSourceIndex = sourceIndex;
                    initRangerTask();
                    mRangerHandler.postDelayed(mRangerTask, 8000);
                } else {
                    updateLiveJson(sourceIndex);
                    mRangerJniImpl.prepareProgram(mInstance, Live_Program_Code[sourceIndex - 1], Live_ProgramInfo_Json[sourceIndex - 1]);
                    mRangerSourceIndex = sourceIndex;
                    addLogTv(mTvLog, "prepare live program: " + sourceIndex + ". " + Live_Program_Desc[sourceIndex - 1]);
                }
            }
            mRangerPlayTag = playTag;
            initRangerLogTask();
            mRangerLogHandler.postDelayed(mRangerLogTask, 1000);
        }
    }

    /*** add: view log ***/
    public void setTextView(TextView tv) {
        mTvLog = tv;
    }

    private void addLogTv(TextView tv, String content) {
        SimpleDateFormat sysTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS  ");
        String sysTimeStr = sysTime.format(new Date());
        tv.append(sysTimeStr);
        tv.append(content + "\n");
    }
    /*******************/

    private void initVideoView(Context context) {
        mAppContext = context.getApplicationContext();
        mSettings = new Settings(mAppContext);

        initBackground();
        initRenders();

        mVideoWidth = 0;
        mVideoHeight = 0;
        // REMOVED: getHolder().addCallback(mSHCallback);
        // REMOVED: getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        // REMOVED: mPendingSubtitleTracks = new Vector<Pair<InputStream, MediaFormat>>();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;

        subtitleDisplay = new TextView(context);
        subtitleDisplay.setTextSize(24);
        subtitleDisplay.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams layoutParams_txt = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        addView(subtitleDisplay, layoutParams_txt);
    }

    public void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null)
                mMediaPlayer.setDisplay(null);

            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            removeView(renderUIView);
        }

        if (renderView == null)
            return;

        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = mRenderView.getView();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);

        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);
    }

    public void setRender(int render) {
        switch (render) {
            case RENDER_NONE:
                setRenderView(null);
                break;
            case RENDER_TEXTURE_VIEW: {
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                    renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(mCurrentAspectRatio);
                }
                setRenderView(renderView);
                break;
            }
            case RENDER_SURFACE_VIEW: {
                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
                setRenderView(renderView);
                break;
            }
            default:
                Log.e(TAG, String.format(Locale.getDefault(), "invalid render %d\n", render));
                break;
        }
    }

    public void setHudView(TableLayout tableLayout) {
        mHudViewHolder = new InfoHudViewHolder(getContext(), tableLayout);
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        if (path.contains("adaptationSet")){
            mManifestString = path;
            setVideoURI(Uri.parse("ijklas:"));
        } else {
            setVideoURI(Uri.parse(path));
        }
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    private void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    // REMOVED: addSubtitleSource
    // REMOVED: mPendingSubtitleTracks

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            addLogTv(mTvLog,"stop");
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            if (mHudViewHolder != null)
                mHudViewHolder.setMediaPlayer(null);
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            mHudViewHolder.setIsPrepared(false);//add: getDuration should int the right state
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);

        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        try {
            mMediaPlayer = createPlayer(mSettings.getPlayerType());
            // TODO: create SubtitleController in MediaPlayer, but we need
            // a context for the subtitle renderers
            final Context context = getContext();
            // REMOVED: SubtitleController

            // REMOVED: mAudioSession
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
            mCurrentBufferPercentage = 0;
            String scheme = mUri.getScheme();
            addLogTv(mTvLog, "setDataSource");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    mSettings.getUsingMediaDataSource() &&
                    (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            }  else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            } else {
                mMediaPlayer.setDataSource(mUri.toString());
            }
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mPrepareStartTime = System.currentTimeMillis();
            addLogTv(mTvLog, "preparing");
            mMediaPlayer.prepareAsync();
            if (mHudViewHolder != null)
                mHudViewHolder.setMediaPlayer(mMediaPlayer);

            // REMOVED: mPendingSubtitleTracks

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            addLogTv(mTvLog, "error");
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            addLogTv(mTvLog, "error");
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } finally {
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }

    public void setMediaController(IMediaController controller) {
        if (mMediaController != null) {
            mMediaController.hide();
        }
        mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View) this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());
        }
    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        if (mRenderView != null) {
                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        }
                        // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                        requestLayout();
                    }
                }
            };

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            mPrepareEndTime = System.currentTimeMillis();
            mHudViewHolder.updateLoadCost(mPrepareEndTime - mPrepareStartTime);
            mCurrentState = STATE_PREPARED;
            /****** add: ranger log ******/
            if(mIsRangerMode) {
                if(mRangerPlayTag.equals("vod")) {
                    addLogTv(mTvLog, mRangerPlayTag + " program: " + mRangerSourceIndex + ". " + Vod_Program_Desc[mRangerSourceIndex - 1] + " prepared");
                } else if(mRangerPlayTag.equals("live")) {
                    addLogTv(mTvLog, mRangerPlayTag + " program: " + mRangerSourceIndex + ". " + Live_Program_Desc[mRangerSourceIndex - 1] + " prepared");
                }
            }
            /*************************/
            else
                addLogTv(mTvLog, "prepared");
            if(mIsRangerMode) {
                mRangerJniImpl.notifyPlayerEvent("onPrepared", 0,0,0);//add: ranger mediaPlayerEvent
            }
            // Get the capabilities of the player for this stream
            // REMOVED: Metadata

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            mHudViewHolder.setIsPrepared(mCurrentState == STATE_PREPARED);

            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
                // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                        // We didn't actually change the size (it was already at the size
                        // we need), so we won't get a "surface changed" callback, so
                        // start the video here instead of in the callback.
                        if (mTargetState == STATE_PLAYING) {
                            start();
                            if (mMediaController != null) {
                                mMediaController.show();
                            }
                        } else if (!isPlaying() &&
                                (seekToPosition != 0 || getCurrentPosition() > 0)) {
                            if (mMediaController != null) {
                                // Show the media controls when we're paused into a video and make 'em stick.
                                mMediaController.show(0);
                            }
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                public void onCompletion(IMediaPlayer mp) {
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;
                    addLogTv(mTvLog, "completed");
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }
                    addLogTv(mTvLog, "onInfo,what:" + arg1 + ",extra:" + arg2);
                    if(mIsRangerMode)
                        mRangerJniImpl.notifyPlayerEvent("onInfo", arg1,arg2,0);//add: ranger mediaPlayerEvent
                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                            Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                            mVideoRotationDegree = arg2;
                            Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                            if (mRenderView != null)
                                mRenderView.setVideoRotation(arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                            break;
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    addLogTv(mTvLog, "error");
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }

                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }

                    /* Otherwise, pop up an error dialog so the user knows that
                     * something bad has happened. Only try and pop up the dialog
                     * if we're attached to a window. When we're going away and no
                     * longer have a window, don't bother showing the user an error.
                     */
                    if (getWindowToken() != null) {
                        Resources r = mAppContext.getResources();
                        int messageId;

                        if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                            messageId = R.string.VideoView_error_text_invalid_progressive_playback;
                        } else {
                            messageId = R.string.VideoView_error_text_unknown;
                        }

                        new AlertDialog.Builder(getContext())
                                .setMessage(messageId)
                                .setPositiveButton(R.string.VideoView_error_button,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                /* If we get here, there is no onError listener, so
                                                 * at least inform them that the video is over.
                                                 */
                                                if (mOnCompletionListener != null) {
                                                    mOnCompletionListener.onCompletion(mMediaPlayer);
                                                }
                                            }
                                        })
                                .setCancelable(false)
                                .show();
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                }
            };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            addLogTv(mTvLog, "seek complete");
            mSeekEndTime = System.currentTimeMillis();
            mHudViewHolder.updateSeekCost(mSeekEndTime - mSeekStartTime);
        }
    };

    private IMediaPlayer.OnTimedTextListener mOnTimedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            if (text != null) {
                subtitleDisplay.setText(text.getText());
            }
        }
    };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    // REMOVED: mSHCallback
    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }
            mSurfaceHolder = holder;
            if (mMediaPlayer != null)
                bindSurfaceHolder(mMediaPlayer, holder);
            else
                openVideo();
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }

            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hide();
            // REMOVED: release(true);
            releaseWithoutStop();
        }

        @Override
        public void onSurfaceUpdated(@NonNull IRenderView.ISurfaceHolder holder, long time) {//add: get frame(test)
            /*if(time > 8400 && time < 8450) {
                IRenderView render = holder.getRenderView();
                if(render instanceof TextureRenderView) {
                    TextureRenderView r = (TextureRenderView) render;
                    Bitmap bm = r.getBitmap();
                    File file = new File("/storage/emulated/0/frametmp/", "hwframe");
                    try {
                        FileOutputStream imgOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 80, imgOut);
                        imgOut.flush();
                        imgOut.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }*/
        }
    };

    public void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    /*
     * release the media player in any state
     */
    public void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
            mCurrentState = STATE_IDLE;
            addLogTv(mTvLog, "idle");
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        /*** add: selectTrack on phone ***/
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchCount++;
            if(mTouchCount == 2) {
                mTouchCount = 0;
                int audioTrackSize = 0;
                ITrackInfo trackInfo[] = mMediaPlayer.getTrackInfo();
                for(int i = 0; i < trackInfo.length; i++) {
                    if(trackInfo[i].getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO)
                        audioTrackSize++;
                }
                int curAudioTrack = mMediaPlayer.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
                if(curAudioTrack >= audioTrackSize) {
                    curAudioTrack = 1;
                } else {
                    curAudioTrack++;
                }
                mMediaPlayer.selectTrack(curAudioTrack);
                return true;
            }
        }
        /***********************/
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if(mIsRangerMode) {
                    if(mRangerPlayTag.equals("live"))
                        mRangerJniImpl.resumeProgram(mInstance, Live_Program_Code[mRangerSourceIndex - 1]);
                    else if(mRangerPlayTag.equals("vod"))
                        mRangerJniImpl.resumeProgram(mInstance, Vod_Program_Code[mRangerSourceIndex - 1]);
                }
                if (!mMediaPlayer.isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    if(mIsRangerMode) {
                        if(mRangerPlayTag.equals("live"))
                            mRangerJniImpl.pauseProgram(mInstance, Live_Program_Code[mRangerSourceIndex - 1]);
                        else if(mRangerPlayTag.equals("vod"))
                            mRangerJniImpl.pauseProgram(mInstance, Vod_Program_Code[mRangerSourceIndex - 1]);
                    }
                    mMediaController.show();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                    || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mMediaController.dispatchKeyEvent(event);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_2) {
                if(mIsRangerMode) {
                }
                else {
                    int audioTrackSize = 0;
                    ITrackInfo trackInfo[] = mMediaPlayer.getTrackInfo();
                    for(int i = 0; i < trackInfo.length; i++) {
                        if(trackInfo[i].getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO)
                            audioTrackSize++;
                    }
                    int curAudioTrack = mMediaPlayer.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
                    if(curAudioTrack >= audioTrackSize) {
                        curAudioTrack = 1;
                    } else {
                        curAudioTrack++;
                    }
                    mMediaPlayer.selectTrack(curAudioTrack);
                    return true;
                }
            } else if (keyCode == KeyEvent.KEYCODE_8) {
                if(mIsRangerMode) {
                    RangerBeanCallback rangerBeanCallback = new RangerBeanCallback() {
                        @Override
                        public void callback(PlayInfo result) {
                            if(result == null) return;
                            Log.i(TAG, "getPullStreamState res: " + result.toString());
                        }
                    };
                    if(mRangerPlayTag.equals("vod"))
                        mRangerJniImpl.getStreamState(mInstance, Vod_Program_Code[mRangerSourceIndex - 1], rangerBeanCallback);
                    else if(mRangerPlayTag.equals("live"))
                        mRangerJniImpl.getStreamState(mInstance, Live_Program_Code[mRangerSourceIndex - 1], rangerBeanCallback);
                }
            } else if (keyCode == KeyEvent.KEYCODE_4) {
                if(mIsRangerMode) {
                    if(mRangerPlayTag.equals("vod")) {
                        long curPos = mMediaPlayer.getCurrentPosition();
                        long desPos = curPos - 10 * 1000L;
                        long bufferPos = mMediaPlayer.getBufferPosition();
                        int internalSeek = bufferPos >= desPos ? 1 : 0;//back seek?
                        NativeJni.getJni().seekStream(mInstance, Vod_Program_Code[mRangerSourceIndex - 1], desPos, (int)curPos, internalSeek, new RangerStrCallback() {
                            @Override
                            public void callback(String result) {
                                seekTo((int)desPos);
                            }
                        });
                    }
                }
            } else if (keyCode == KeyEvent.KEYCODE_6) {
                if(mIsRangerMode) {
                    if(mRangerPlayTag.equals("vod")) {
                        long curPos = mMediaPlayer.getCurrentPosition();
                        long desPos = curPos + 10 * 1000L;
                        long bufferPos = mMediaPlayer.getBufferPosition();
                        int internalSeek = bufferPos >= desPos ? 1 : 0;
                        NativeJni.getJni().seekStream(mInstance, Vod_Program_Code[mRangerSourceIndex - 1], desPos, (int)curPos, internalSeek, new RangerStrCallback() {
                            @Override
                            public void callback(String result) {
                                seekTo((int)desPos);
                            }
                        });
                    }
                }
            } else if (keyCode == KeyEvent.KEYCODE_7) {
                if(mIsRangerMode) {
                    int targetSourceIndex = mRangerSourceIndex - 1;
                    switchRangerStream(mRangerSourceIndex, targetSourceIndex);
                }
            } else if (keyCode == KeyEvent.KEYCODE_9) {
                if(mIsRangerMode) {
                    int targetSourceIndex = mRangerSourceIndex + 1;
                    switchRangerStream(mRangerSourceIndex, targetSourceIndex);
                }
            } else if (keyCode == KeyEvent.KEYCODE_0) {//add: show media info
                showMediaInfo();
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void switchRangerStream(int curSourceIndex, int targetSourceIndex) {//add: ranger switch program
        calcExpiredTime("yyyy-MM-dd HH:mm:ss");
        if(mRangerPlayTag.equals("vod")) {
            if(targetSourceIndex > 6)
                targetSourceIndex = 1;
            else if(targetSourceIndex < 1)
                targetSourceIndex = 6;
            updateVodJson(targetSourceIndex);
            mRangerJniImpl.stopProgram(mInstance, Vod_Program_Code[curSourceIndex - 1]);
            mRangerJniImpl.prepareProgram(mInstance, Vod_Program_Code[targetSourceIndex - 1], Vod_ProgramInfo_Json[targetSourceIndex - 1]);
            addLogTv(mTvLog, "prepare vod program: " + targetSourceIndex + ". " + Vod_Program_Desc[targetSourceIndex - 1]);
            mRangerSourceIndex = targetSourceIndex;
        } else if(mRangerPlayTag.equals("live")) {
            if(targetSourceIndex > 6)
                targetSourceIndex = 1;
            else if(targetSourceIndex < 1)
                targetSourceIndex = 6;
            updateLiveJson(targetSourceIndex);
            mRangerJniImpl.stopProgram(mInstance, Live_Program_Code[curSourceIndex - 1]);
            mRangerJniImpl.prepareProgram(mInstance, Live_Program_Code[targetSourceIndex - 1], Live_ProgramInfo_Json[targetSourceIndex - 1]);
            addLogTv(mTvLog, "prepare live program: " + targetSourceIndex + ". " + Live_Program_Desc[targetSourceIndex - 1]);
            mRangerSourceIndex = targetSourceIndex;
        }
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            addLogTv(mTvLog, "start");
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                addLogTv(mTvLog,"pause");
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }

        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mSeekStartTime = System.currentTimeMillis();
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    // REMOVED: getAudioSessionId();
    // REMOVED: onAttachedToWindow();
    // REMOVED: onDetachedFromWindow();
    // REMOVED: onLayout();
    // REMOVED: draw();
    // REMOVED: measureAndLayoutSubtitleWidget();
    // REMOVED: setSubtitleWidget();
    // REMOVED: getSubtitleLooper();

    //-------------------------
    // Extend: Aspect Ratio
    //-------------------------

    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            // IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentAspectRatio = s_allAspectRatio[1];//add: we can do select, fit parent or fill parent

    public int toggleAspectRatio() {
        mCurrentAspectRatioIndex++;
        mCurrentAspectRatioIndex %= s_allAspectRatio.length;

        mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];
        if (mRenderView != null)
            mRenderView.setAspectRatio(mCurrentAspectRatio);
        return mCurrentAspectRatio;
    }

    //-------------------------
    // Extend: Render
    //-------------------------
    public static final int RENDER_NONE = 0;
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;

    private List<Integer> mAllRenders = new ArrayList<Integer>();
    private int mCurrentRenderIndex = 0;
    private int mCurrentRender = RENDER_NONE;

    private void initRenders() {
        mAllRenders.clear();

        if (mSettings.getEnableSurfaceView())
            mAllRenders.add(RENDER_SURFACE_VIEW);
        if (mSettings.getEnableTextureView() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            mAllRenders.add(RENDER_TEXTURE_VIEW);
        if (mSettings.getEnableNoView())
            mAllRenders.add(RENDER_NONE);
        if (mAllRenders.isEmpty())
            mAllRenders.add(RENDER_SURFACE_VIEW);
        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
    }

    public int toggleRender() {
        mCurrentRenderIndex++;
        mCurrentRenderIndex %= mAllRenders.size();

        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
        return mCurrentRender;
    }

    @NonNull
    public static String getRenderText(Context context, int render) {
        String text;
        switch (render) {
            case RENDER_NONE:
                text = context.getString(R.string.VideoView_render_none);
                break;
            case RENDER_SURFACE_VIEW:
                text = context.getString(R.string.VideoView_render_surface_view);
                break;
            case RENDER_TEXTURE_VIEW:
                text = context.getString(R.string.VideoView_render_texture_view);
                break;
            default:
                text = context.getString(R.string.N_A);
                break;
        }
        return text;
    }

    //-------------------------
    // Extend: Player
    //-------------------------
    public int togglePlayer() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();

        if (mRenderView != null)
            mRenderView.getView().invalidate();
        openVideo();
        return mSettings.getPlayer();
    }

    @NonNull
    public static String getPlayerText(Context context, int player) {
        String text;
        switch (player) {
            case Settings.PV_PLAYER__AndroidMediaPlayer:
                text = context.getString(R.string.VideoView_player_AndroidMediaPlayer);
                break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
                text = context.getString(R.string.VideoView_player_IjkMediaPlayer);
                break;
            case Settings.PV_PLAYER__IjkExoMediaPlayer:
                text = context.getString(R.string.VideoView_player_IjkExoMediaPlayer);
                break;
            default:
                text = context.getString(R.string.N_A);
                break;
        }
        return text;
    }

    public IMediaPlayer createPlayer(int playerType) {
        IMediaPlayer mediaPlayer = null;

        switch (playerType) {
            case Settings.PV_PLAYER__IjkExoMediaPlayer: {
                IjkExoMediaPlayer IjkExoMediaPlayer = new IjkExoMediaPlayer(mAppContext);
                mediaPlayer = IjkExoMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__AndroidMediaPlayer: {
                AndroidMediaPlayer androidMediaPlayer = new AndroidMediaPlayer();
                mediaPlayer = androidMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
            default: {
                IjkMediaPlayer ijkMediaPlayer = null;
                if (mUri != null) {
                    ijkMediaPlayer = new IjkMediaPlayer();
                    ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
                    /*ijkMediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {//add:reconnect
                        @Override
                        public boolean onNativeInvoke(int i, Bundle bundle) {
                            return true;
                        }
                    });*/
                    //ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames",500);//add: switch audio stream
                    //ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 200*1024);//add: buffer size
                    if (mManifestString != null) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "iformat", "ijklas");
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "find_stream_info", 0);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "manifest_string", mManifestString);
                    }
                    if (mIsEnableHw) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1);// add: all-videos
                        if (mSettings.getUsingMediaCodecAutoRotate()) {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                        } else {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
                        }
                        if (mSettings.getMediaCodecHandleResolutionChange()) {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
                        } else {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
                        }
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                    }

                    if (mSettings.getUsingOpenSLES()) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                    }

                    String pixelFormat = mSettings.getPixelFormat();
                    if (TextUtils.isEmpty(pixelFormat)) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
                    }
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                    //ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
                    // add: cancel parallel download, ranger requires serial
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http_multiple" , 0);
                    // add: sync apk
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "live-streaming", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "delay-optimization", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "timeout", (long) 20 * 1000);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "get-av-frame-timeout", (long)20 * 1000);
                }
                mediaPlayer = ijkMediaPlayer;
            }
            break;
        }

        if (mSettings.getEnableDetachedSurfaceTextureView()) {
            mediaPlayer = new TextureMediaPlayer(mediaPlayer);
        }

        return mediaPlayer;
    }

    //-------------------------
    // Extend: Background
    //-------------------------

    private boolean mEnableBackgroundPlay = false;

    private void initBackground() {
        mEnableBackgroundPlay = mSettings.getEnableBackgroundPlay();
        if (mEnableBackgroundPlay) {
            MediaPlayerService.intentToStart(getContext());
            mMediaPlayer = MediaPlayerService.getMediaPlayer();
            if (mHudViewHolder != null)
                mHudViewHolder.setMediaPlayer(mMediaPlayer);
        }
    }

    public boolean isBackgroundPlayEnabled() {
        return mEnableBackgroundPlay;
    }

    public void enterBackground() {
        MediaPlayerService.setMediaPlayer(mMediaPlayer);
    }

    public void stopBackgroundPlay() {
        MediaPlayerService.setMediaPlayer(null);
    }

    //-------------------------
    // Extend: Background
    //-------------------------
    public void showMediaInfo() {
        if (mMediaPlayer == null)
            return;

        int selectedVideoTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
        int selectedAudioTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        int selectedSubtitleTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);

        TableLayoutBinder builder = new TableLayoutBinder(getContext());
        builder.setTableLayoutColor(0xFF4A4949);//add: show media info
        builder.appendSection(R.string.mi_player);
        builder.appendRow2(R.string.mi_player, MediaPlayerCompat.getName(mMediaPlayer));
        builder.appendSection(R.string.mi_media);
        builder.appendRow2(R.string.mi_resolution, buildResolution(mVideoWidth, mVideoHeight, mVideoSarNum, mVideoSarDen));
        builder.appendRow2(R.string.mi_length, buildTimeMilli(mMediaPlayer.getDuration()));

        ITrackInfo trackInfos[] = mMediaPlayer.getTrackInfo();
        if (trackInfos != null) {
            int index = -1;
            for (ITrackInfo trackInfo : trackInfos) {
                index++;

                int trackType = trackInfo.getTrackType();
                if (index == selectedVideoTrack) {
                    builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, index) + " " + getContext().getString(R.string.mi__selected_video_track));
                } else if (index == selectedAudioTrack) {
                    builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, index) + " " + getContext().getString(R.string.mi__selected_audio_track));
                } else if (index == selectedSubtitleTrack) {
                    builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, index) + " " + getContext().getString(R.string.mi__selected_subtitle_track));
                } else {
                    builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, index));
                }
                builder.appendRow2(R.string.mi_type, buildTrackType(trackType));
                builder.appendRow2(R.string.mi_language, buildLanguage(trackInfo.getLanguage()));

                IMediaFormat mediaFormat = trackInfo.getFormat();
                if (mediaFormat == null) {
                } else if (mediaFormat instanceof IjkMediaFormat) {
                    switch (trackType) {
                        case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                            builder.appendRow2(R.string.mi_codec, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                            builder.appendRow2(R.string.mi_profile_level, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                            builder.appendRow2(R.string.mi_pixel_format, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PIXEL_FORMAT_UI));
                            builder.appendRow2(R.string.mi_resolution, mediaFormat.getString(IjkMediaFormat.KEY_IJK_RESOLUTION_UI));
                            builder.appendRow2(R.string.mi_frame_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_FRAME_RATE_UI));
                            builder.appendRow2(R.string.mi_bit_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                            break;
                        case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                            builder.appendRow2(R.string.mi_codec, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                            builder.appendRow2(R.string.mi_profile_level, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                            builder.appendRow2(R.string.mi_sample_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_SAMPLE_RATE_UI));
                            builder.appendRow2(R.string.mi_channels, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CHANNEL_UI));
                            builder.appendRow2(R.string.mi_bit_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        AlertDialog.Builder adBuilder = builder.buildAlertDialogBuilder();
        adBuilder.setTitle(R.string.media_information);
        adBuilder.setNegativeButton(R.string.close, null);
        adBuilder.show();
    }

    private String buildResolution(int width, int height, int sarNum, int sarDen) {
        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append(" x ");
        sb.append(height);

        if (sarNum > 1 || sarDen > 1) {
            sb.append("[");
            sb.append(sarNum);
            sb.append(":");
            sb.append(sarDen);
            sb.append("]");
        }

        return sb.toString();
    }

    private String buildTimeMilli(long duration) {
        long total_seconds = duration / 1000;
        long hours = total_seconds / 3600;
        long minutes = (total_seconds % 3600) / 60;
        long seconds = total_seconds % 60;
        if (duration <= 0) {
            return "--:--";
        }
        if (hours >= 100) {
            return String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    private String buildTrackType(int type) {
        Context context = getContext();
        switch (type) {
            case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                return context.getString(R.string.TrackType_video);
            case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                return context.getString(R.string.TrackType_audio);
            case ITrackInfo.MEDIA_TRACK_TYPE_SUBTITLE:
                return context.getString(R.string.TrackType_subtitle);
            case ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                return context.getString(R.string.TrackType_timedtext);
            case ITrackInfo.MEDIA_TRACK_TYPE_METADATA:
                return context.getString(R.string.TrackType_metadata);
            case ITrackInfo.MEDIA_TRACK_TYPE_UNKNOWN:
            default:
                return context.getString(R.string.TrackType_unknown);
        }
    }

    private String buildLanguage(String language) {
        if (TextUtils.isEmpty(language))
            return "und";
        return language;
    }

    public ITrackInfo[] getTrackInfo() {
        if (mMediaPlayer == null)
            return null;

        return mMediaPlayer.getTrackInfo();
    }

    public void selectTrack(int stream) {
        MediaPlayerCompat.selectTrack(mMediaPlayer, stream);
    }

    public void deselectTrack(int stream) {
        MediaPlayerCompat.deselectTrack(mMediaPlayer, stream);
    }

    public int getSelectedTrack(int trackType) {
        return MediaPlayerCompat.getSelectedTrack(mMediaPlayer, trackType);
    }
}
