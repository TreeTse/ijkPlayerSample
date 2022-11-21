package com.wisecloud.jni;

public class PlayInfo {

    String program;    // 节目Code
    String desc;    // 节目描述
    String media;    // 当前正在使用的资源ID，即MediaCode
    String protocol;    //协议
    String format;  //封装格式
    String video_codec; //视频编码
    String player;  //播放器

    String tag;            // 资源的标签，此处用于记录cdntype平台，值为数字：1、4、5（取决于api/portalCore/v7/getSlbInfo接口返回的cdn_type字段）等
    String source_url;    // 源URL（SLB）
    String quality;        // 清晰度
    String lang;        //音轨语言（用于点播多源多音轨）
    //对于非播放事件（stream_**, manifest_**以外）以下字段可能为空
    String trans_id;        // 播放ID（GslbId-SlbId-ProgramId-PlayId-RetryId）组成，暂时没用上
    String media_url;    // 媒资URL（SLB响应的两个URL中当前使用的），可以直接播放的外网地址，可以用于投屏
    String snapinfo_url; //缩略图信息URL
    String snapshot_url;  // 缩略图Url
    String snapshot_queue; //缩略图队列
    String auth;    //播放鉴权Auth部分，用于导出播放URL供投屏使用
    String license; //播放鉴权License部分，用于导出播放URL供投屏使用？
    String slb_code;       //	SLB地址Mark
    String live_pcdn_mode;
    String server_code;    // 边缘服务器Mark（上述URL对应的服务器Code）
    String play_url;        // Ranger代理的播放Url，用于给播放器播放
    String app_ctx; //APK上下文数据
    String dashboard;   //Debug面板展示信息
    String group;   //服务器分组
    String p2p_mode;
    String user_id; //用户ID
    String local_ip;    //本地IP
    String media_encrypt;   //媒资是否加密
    long gop_duration;    //GOP时长
    int source_count;    //累计切源个数
    int priority;    //源优先级
    int serial;  //源序号,下标从0开始的
    int Status;        // 流状态，当且仅当RecvDuration > PlayDuration || RecvX30s >= 28时为TRUE
    long schedule_spent; // SLB调度耗时，单位ns
    long media_spent; // 拉流耗时（从建立连接到开始接收到数据），单位ns，用于采集首屏加载时长
    long media_duration;// 点播/回看媒体时长
    long play_duration;// 用户播放时长，即任务持续时间（Alive），单位ns
    long recv_duration;// 接收的流媒体资源的时长，单位ns
    long recv_bytes;    // 接收数据大小
    long archive_bytes;    // 当前源存档数据大小
    long expire_bytes;    // 当前源过期数据大小
    long recv30s;        // recv data in recent 30s，单位byte
    long recvx30s;    // recv data duration in recent 30s，单位ns
    long express;      // 体验级别（包含PCDN的开关）
    long p2p_err;     //P2p业务的错误码，不影响播放（主要是连接Tracker失败等）
    long recv_peer_bytes;  //	从Peer下载字节数
    long send_peer_bytes;  //	分享给Peer字节数
    long send_player_bytes;  //	当前源向Player发送数据大小
    long total_recv_peer_bytes;  //	累计从Peer接收数据大小
    long total_send_peer_bytes;  //	累计向Peer发送数据大小
    long recv_server_bytes;  //	当前源从Server接收数据大小
    long total_recv_server_bytes;  //	累计从Server接收数据大小
    long peer_num;   //当前连接Peer个数
    long buffer_duration;
    long buffer_bytes;
    long in_latency;    //输入直播流延迟，单位ns
    long out_latency;    //输出直播流延迟，单位ns
    long rtt;    //spent time for establishing tcp connection，单位ns
    long delay;    //自适应延迟值
    String cache;    //边缘缓存命中，例如:"HIT","MISS"
    String source_id_code;	//源id mark
    String rule_id_code;	//策略id mark
    int source_weight_in_use;	//当前使用的源的权重值

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getVideo_codec() {
        return video_codec;
    }

    public void setVideo_codec(String video_codec) {
        this.video_codec = video_codec;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getSnapinfo_url() {
        return snapinfo_url;
    }

    public void setSnapinfo_url(String snapinfo_url) {
        this.snapinfo_url = snapinfo_url;
    }

    public String getSnapshot_url() {
        return snapshot_url;
    }

    public void setSnapshot_url(String snapshot_url) {
        this.snapshot_url = snapshot_url;
    }

    public String getSnapshot_queue() {
        return snapshot_queue;
    }

    public void setSnapshot_queue(String snapshot_queue) {
        this.snapshot_queue = snapshot_queue;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getSlb_code() {
        return slb_code;
    }

    public void setSlb_code(String slb_code) {
        this.slb_code = slb_code;
    }

    public String getLive_pcdn_mode() {
        return live_pcdn_mode;
    }

    public void setLive_pcdn_mode(String live_pcdn_mode) {
        this.live_pcdn_mode = live_pcdn_mode;
    }

    public String getServer_code() {
        return server_code;
    }

    public void setServer_code(String server_code) {
        this.server_code = server_code;
    }

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public String getApp_ctx() {
        return app_ctx;
    }

    public void setApp_ctx(String app_ctx) {
        this.app_ctx = app_ctx;
    }

    public String getDashboard() {
        return dashboard;
    }

    public void setDashboard(String dashboard) {
        this.dashboard = dashboard;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getP2p_mode() {
        return p2p_mode;
    }

    public void setP2p_mode(String p2p_mode) {
        this.p2p_mode = p2p_mode;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLocal_ip() {
        return local_ip;
    }

    public void setLocal_ip(String local_ip) {
        this.local_ip = local_ip;
    }

    public String getMedia_encrypt() {
        return media_encrypt;
    }

    public void setMedia_encrypt(String media_encrypt) {
        this.media_encrypt = media_encrypt;
    }

    public long getGop_duration() {
        return gop_duration;
    }

    public void setGop_duration(long gop_duration) {
        this.gop_duration = gop_duration;
    }

    public int getSource_count() {
        return source_count;
    }

    public void setSource_count(int source_count) {
        this.source_count = source_count;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getSerial() {
        return serial+1;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public long getSchedule_spent() {
        return schedule_spent;
    }

    public void setSchedule_spent(long schedule_spent) {
        this.schedule_spent = schedule_spent;
    }

    public long getMedia_spent() {
        return media_spent;
    }

    public void setMedia_spent(long media_spent) {
        this.media_spent = media_spent;
    }

    public long getMedia_duration() {
        return media_duration;
    }

    public void setMedia_duration(long media_duration) {
        this.media_duration = media_duration;
    }

    public long getPlay_duration() {
        return play_duration;
    }

    public void setPlay_duration(long play_duration) {
        this.play_duration = play_duration;
    }

    public long getRecv_duration() {
        return recv_duration;
    }

    public void setRecv_duration(long recv_duration) {
        this.recv_duration = recv_duration;
    }

    public long getRecv_bytes() {
        return recv_bytes;
    }

    public void setRecv_bytes(long recv_bytes) {
        this.recv_bytes = recv_bytes;
    }

    public long getArchive_bytes() {
        return archive_bytes;
    }

    public void setArchive_bytes(long archive_bytes) {
        this.archive_bytes = archive_bytes;
    }

    public long getExpire_bytes() {
        return expire_bytes;
    }

    public void setExpire_bytes(long expire_bytes) {
        this.expire_bytes = expire_bytes;
    }

    public long getRecv30s() {
        return recv30s;
    }

    public void setRecv30s(long recv30s) {
        this.recv30s = recv30s;
    }

    public long getRecvx30s() {
        return recvx30s;
    }

    public void setRecvx30s(long recvx30s) {
        this.recvx30s = recvx30s;
    }

    public long getExpress() {
        return express;
    }

    public void setExpress(int express) {
        this.express = express;
    }

    public long getRtt() {
        return rtt;
    }

    public void setRtt(long rtt) {
        this.rtt = rtt;
    }

    public void setExpress(long express) {
        this.express = express;
    }

    public long getP2p_err() {
        return p2p_err;
    }

    public void setP2p_err(long p2p_err) {
        this.p2p_err = p2p_err;
    }

    public long getRecv_peer_bytes() {
        return recv_peer_bytes;
    }

    public void setRecv_peer_bytes(long recv_peer_bytes) {
        this.recv_peer_bytes = recv_peer_bytes;
    }

    public long getSend_peer_bytes() {
        return send_peer_bytes;
    }

    public void setSend_peer_bytes(long send_peer_bytes) {
        this.send_peer_bytes = send_peer_bytes;
    }

    public long getSend_player_bytes() {
        return send_player_bytes;
    }

    public void setSend_player_bytes(long send_player_bytes) {
        this.send_player_bytes = send_player_bytes;
    }

    public long getTotal_recv_peer_bytes() {
        return total_recv_peer_bytes;
    }

    public void setTotal_recv_peer_bytes(long total_recv_peer_bytes) {
        this.total_recv_peer_bytes = total_recv_peer_bytes;
    }

    public long getTotal_send_peer_bytes() {
        return total_send_peer_bytes;
    }

    public void setTotal_send_peer_bytes(long total_send_peer_bytes) {
        this.total_send_peer_bytes = total_send_peer_bytes;
    }

    public long getRecv_server_bytes() {
        return recv_server_bytes;
    }

    public void setRecv_server_bytes(long recv_server_bytes) {
        this.recv_server_bytes = recv_server_bytes;
    }

    public long getTotal_recv_server_bytes() {
        return total_recv_server_bytes;
    }

    public void setTotal_recv_server_bytes(long total_recv_server_bytes) {
        this.total_recv_server_bytes = total_recv_server_bytes;
    }

    public long getPeer_num() {
        return peer_num;
    }

    public void setPeer_num(long peer_num) {
        this.peer_num = peer_num;
    }

    public long getBuffer_duration() {
        return buffer_duration;
    }

    public void setBuffer_duration(long buffer_duration) {
        this.buffer_duration = buffer_duration;
    }

    public long getBuffer_bytes() {
        return buffer_bytes;
    }

    public void setBuffer_bytes(long buffer_bytes) {
        this.buffer_bytes = buffer_bytes;
    }

    public long getIn_latency() {
        return in_latency;
    }

    public void setIn_latency(long in_latency) {
        this.in_latency = in_latency;
    }

    public long getOut_latency() {
        return out_latency;
    }

    public void setOut_latency(long out_latency) {
        this.out_latency = out_latency;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getSource_id_code() {
        return source_id_code;
    }

    public void setSource_id_code(String source_id_code) {
        this.source_id_code = source_id_code;
    }

    public String getRule_id_code() {
        return rule_id_code;
    }

    public void setRule_id_code(String rule_id_code) {
        this.rule_id_code = rule_id_code;
    }

   /* public Object getSource_weights() {
        return source_weights;
    }

    public void setSource_weights(Object source_weights) {
        this.source_weights = source_weights;
    }*/

    public int getSource_weight_in_use() {
        return source_weight_in_use;
    }

    public void setSource_weights_in_use(int source_weight_in_use) {
        this.source_weight_in_use = source_weight_in_use;
    }

    @Override
    public String toString() {
        return "PlayInfo{" +
                "program='" + program + '\'' +
                ", desc='" + desc + '\'' +
                ", media='" + media + '\'' +
                ", protocol='" + protocol + '\'' +
                ", format='" + format + '\'' +
                ", video_codec='" + video_codec + '\'' +
                ", player='" + player + '\'' +
                ", tag='" + tag + '\'' +
                ", source_url='" + source_url + '\'' +
                ", quality='" + quality + '\'' +
                ", lang='" + lang + '\'' +
                ", trans_id='" + trans_id + '\'' +
                ", media_url='" + media_url + '\'' +
                ", snapinfo_url='" + snapinfo_url + '\'' +
                ", snapshot_url='" + snapshot_url + '\'' +
                ", snapshot_queue='" + snapshot_queue + '\'' +
                ", auth='" + auth + '\'' +
                ", license='" + license + '\'' +
                ", slb_code='" + slb_code + '\'' +
                ", live_pcdn_mode='" + live_pcdn_mode + '\'' +
                ", server_code='" + server_code + '\'' +
                ", play_url='" + play_url + '\'' +
                ", app_ctx='" + app_ctx + '\'' +
                ", dashboard='" + dashboard + '\'' +
                ", group='" + group + '\'' +
                ", p2p_mode='" + p2p_mode + '\'' +
                ", user_id='" + user_id + '\'' +
                ", local_ip='" + local_ip + '\'' +
                ", media_encrypt='" + media_encrypt + '\'' +
                ", gop_duration=" + gop_duration +
                ", source_count=" + source_count +
                ", priority=" + priority +
                ", serial=" + serial +
                ", Status=" + Status +
                ", schedule_spent=" + schedule_spent +
                ", media_spent=" + media_spent +
                ", media_duration=" + media_duration +
                ", play_duration=" + play_duration +
                ", recv_duration=" + recv_duration +
                ", recv_bytes=" + recv_bytes +
                ", archive_bytes=" + archive_bytes +
                ", expire_bytes=" + expire_bytes +
                ", recv30s=" + recv30s +
                ", recvx30s=" + recvx30s +
                ", express=" + express +
                ", p2p_err=" + p2p_err +
                ", recv_peer_bytes=" + recv_peer_bytes +
                ", send_peer_bytes=" + send_peer_bytes +
                ", send_player_bytes=" + send_player_bytes +
                ", total_recv_peer_bytes=" + total_recv_peer_bytes +
                ", total_send_peer_bytes=" + total_send_peer_bytes +
                ", recv_server_bytes=" + recv_server_bytes +
                ", total_recv_server_bytes=" + total_recv_server_bytes +
                ", peer_num=" + peer_num +
                ", buffer_duration=" + buffer_duration +
                ", buffer_bytes=" + buffer_bytes +
                ", in_latency=" + in_latency +
                ", out_latency=" + out_latency +
                ", rtt=" + rtt +
                ", cache=" + cache +
                ", source_id_code=" + source_id_code +
                ", rule_id_code=" + rule_id_code +
//                ", source_weights=" + source_weights +
//                ", blocked_srv_ips=" + blocked_srv_ips +
                ", source_weights_in_use=" + source_weight_in_use +
                '}';
    }
}
