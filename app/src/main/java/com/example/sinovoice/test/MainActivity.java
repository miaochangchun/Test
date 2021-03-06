package com.example.sinovoice.test;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sinovoice.util.HciCloudAsrHelper;
import com.example.sinovoice.util.HciCloudSysHelper;
import com.example.sinovoice.util.HciCloudTtsHelper;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button cnRecorder;
    private Button uyRecorder;
    private Button cnPlay;
    private Button uyPlay;
//    private TextView cnResult;
    private TextView uyResult;
    private HciCloudSysHelper myHciCloudSysHelper;
    private HciCloudAsrHelper myHciCloudAsrHelper;
    private HciCloudTtsHelper myHciCloudTtsHelper;
    //公有云配置参数
    private String cnTtsCapkey = "tts.cloud.wangjing";
    private String uyTtsCapkey = "tts.cloud.uyghur";
    private String cnAsrCapkey= "asr.cloud.freetalk";
    private String uyAsrCapkey = "asr.cloud.freetalk.uyghur";

    /**
     * 私有云配置参数
     */
//    private String ttsCapkey = "tts.cloud.synth";
//    private String asrCapkey= "asr.cloud.freetalk";

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
                    uyResult.setText(result);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initSinovoice();
    }

    private void initSinovoice() {
        myHciCloudSysHelper = HciCloudSysHelper.getInstance();
        myHciCloudAsrHelper = HciCloudAsrHelper.getInstance();
        myHciCloudTtsHelper = HciCloudTtsHelper.getInstance();
        int errorCode = myHciCloudSysHelper.init(this);
        if (errorCode != HciErrorCode.HCI_ERR_NONE) {
            Toast.makeText(this, "系统初始化失败，错误码=" + errorCode, Toast.LENGTH_SHORT).show();
            return;
        }
        //私有云配置
        //myHciCloudAsrHelper.initAsrRecorder(this, asrCapkey);
        //公有云配置
        boolean bool = myHciCloudAsrHelper.initAsrRecorder(this, cnAsrCapkey + ";" + uyAsrCapkey);
        if (bool == false) {
            Toast.makeText(this, "ASR初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }
        myHciCloudAsrHelper.setMyHander(new MyHandler());

        //私有云配置参数
        //myHciCloudTtsHelper.initTtsPlayer(this, ttsCapkey);
        //公有云配置参数
        bool = myHciCloudTtsHelper.initTtsPlayer(this, cnTtsCapkey + ";" + uyTtsCapkey);
        if (bool == false) {
            Toast.makeText(this, "TTS初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 初始化按钮
     */
    private void initView() {
        cnRecorder = (Button) findViewById(R.id.cn_recorder);
//        cnResult = (TextView) findViewById(R.id.result_cn);
        uyRecorder = (Button) findViewById(R.id.uy_recorder);
        uyResult = (TextView) findViewById(R.id.result_uy);
        cnPlay = (Button) findViewById(R.id.cn_play);
        uyPlay = (Button) findViewById(R.id.uy_play);

        cnRecorder.setOnClickListener(this);
        cnPlay.setOnClickListener(this);
        uyRecorder.setOnClickListener(this);
        uyPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cn_play:  //中文语音合成
                //私有云配置
                //myHciCloudTtsHelper.playTtsPlayer("123456", ttsCapkey, "cn_wangjing_common");
                //公有云配置
                myHciCloudTtsHelper.playTtsPlayer("123456", cnTtsCapkey, "");
                break;
            case R.id.uy_play:  //维语语音合成
                //私有云配置
//                myHciCloudTtsHelper.playTtsPlayer("123456", ttsCapkey, "uyghur_uyghur_common");
                //公有云配置
                myHciCloudTtsHelper.playTtsPlayer("123456", uyTtsCapkey, "");
                break;
            case R.id.cn_recorder:  //中文语音识别
                //私有云配置
                //myHciCloudAsrHelper.startAsrRecorder(asrCapkey, "common");
                //公有云配置
                myHciCloudAsrHelper.startAsrRecorder(cnAsrCapkey, "common");
                break;
            case R.id.uy_recorder:  //维语语音识别
                //私有云配置
                //myHciCloudAsrHelper.startAsrRecorder(asrCapkey, "music");
                //公有云配置
                myHciCloudAsrHelper.startAsrRecorder(uyAsrCapkey, "common");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (myHciCloudTtsHelper != null) {
            myHciCloudTtsHelper.releaseTtsPlayer();
        }
        if (myHciCloudAsrHelper != null) {
            myHciCloudAsrHelper.releaseAsrRecorder();
        }
        if (myHciCloudSysHelper != null) {
            myHciCloudSysHelper.release();
        }
        super.onDestroy();
    }
}
