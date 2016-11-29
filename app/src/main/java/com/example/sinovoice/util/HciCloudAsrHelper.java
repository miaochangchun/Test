package com.example.sinovoice.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sinovoice.hcicloudsdk.android.asr.recorder.ASRRecorder;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;
import com.sinovoice.hcicloudsdk.recorder.RecorderEvent;

/**
 * Created by miaochangchun on 2016/11/28.
 */
public class HciCloudAsrHelper {
    private static final String TAG = HciCloudAsrHelper.class.getSimpleName();
    private static HciCloudAsrHelper mHciCloudAsrHelper = null;
    private ASRRecorder mASRRecorder;
    private Handler myHander;
//    private String voicePath;   //音频文件保存路径

//    public void setVoicePath(String voicePath) {
//        this.voicePath = voicePath;
//    }

//    public String getVoicePath() {
//        return voicePath;
//    }

    public void setMyHander(Handler myHander) {
        this.myHander = myHander;
    }

    public Handler getMyHander() {
        return myHander;
    }

    private HciCloudAsrHelper() {
    }

    public static HciCloudAsrHelper getInstance(){
        if (mHciCloudAsrHelper == null) {
            return  new HciCloudAsrHelper();
        }
        return mHciCloudAsrHelper;
    }

    /**
     * 录音机初始化
     * @param context   上下文
     * @param initCapkeys    初始化录音机时设置的capkey，可以设置为多个。
     *
     */
    public void initAsrRecorder(Context context, String initCapkeys) {
        mASRRecorder = new ASRRecorder();
        String strConfig = getAsrInitParam(context, initCapkeys);
        // 语法相关的配置,若使用自由说能力可以不必配置该项
        String grammarData = "";
        String grammarConfigString = "capkey=" + initCapkeys + ",isFile=no,grammarType=jsgf";
        mASRRecorder.init(strConfig, grammarConfigString, grammarData, new ASRRecorderCallback());
    }

    /**
     * 获取初始化时的参数配置
     * @param context   上下文
     * @param initCapkeys   需要初始化的capkey，可以设置为多个
     * @return
     */
    private String getAsrInitParam(Context context, String initCapkeys) {
        AsrInitParam asrInitParam = new AsrInitParam();
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, initCapkeys);
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, "android_so");
        String dataPath = context.getFilesDir().getAbsolutePath().replace("files", "lib");
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
        return asrInitParam.getStringConfig();
    }

    /**
     * 开启录音机识别
     * @param capkey    开启录音机时使用的capkey
     * @param domain    设置识别的领域
     */
    public void startAsrRecorder(String capkey, String domain) {
        if (mASRRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_IDLE) {
            String strConfig = getAsrConfigParam(capkey, domain);
            mASRRecorder.start(strConfig);
        }
    }

    /**
     * 获取asr识别时的配置参数
     * @param capkey    录音机识别是的配置参数capkey
     * @param domain    设置的领域值
     * @return
     */
    private String getAsrConfigParam(String capkey, String domain) {
        AsrConfig asrConfig = new AsrConfig();
        asrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
        asrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE, "speex");
        asrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
        asrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, "yes");
        asrConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_ADD_PUNC, "yes");
        asrConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_DOMAIN, domain);
        return asrConfig.getStringConfig();
    }

    /**
     * 录音机release接口
     */
    public void releaseAsrRecorder() {
        if (mASRRecorder != null) {
            mASRRecorder.release();
        }
    }

    /**
     * ASR录音机回调类
     */
    private class ASRRecorderCallback implements ASRRecorderListener {
        String result = "";
        @Override
        public void onRecorderEventStateChange(RecorderEvent recorderEvent) {
            String state = "状态为：初始状态";
            if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECORD) {
                state = "状态为：开始录音";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECOGNIZE) {
                state = "状态为：开始识别";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_NO_VOICE_INPUT) {
                state = "状态为：无音频输入";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_HAVING_VOICE) {
                state = "状态为：录音中";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_END_RECORD) {
                state = "状态为：录音结束";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_COMPLETE) {
                state = "状态为：识别结束";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_VOICE_BUFFER_FULL) {
                state = "状态为：缓冲满";
            }

//            //把录音机状态传递到Activity上
//            Message message = new Message();
//            message.arg1 = 3;
//            Bundle bundle = new Bundle();
//            bundle.putString("state", state);
//            message.setData(bundle);
//            myHander.sendMessage(message);
        }

        @Override
        public void onRecorderEventRecogFinsh(RecorderEvent recorderEvent, AsrRecogResult asrRecogResult) {
            if (asrRecogResult != null) {
                if (asrRecogResult.getRecogItemList().size() > 0) {
                    //识别结果
                    result = asrRecogResult.getRecogItemList().get(0).getRecogResult();
                    //置信度
                    int score = asrRecogResult.getRecogItemList().get(0).getScore();

                    //把识别结果传递到Activity上
                    Message message = new Message();
                    message.arg1 = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("result", "识别结果：" + result);
                    message.setData(bundle);
                    myHander.sendMessage(message);
                }
            }
        }

        @Override
        public void onRecorderEventRecogProcess(RecorderEvent recorderEvent, AsrRecogResult asrRecogResult) {

        }

        @Override
        public void onRecorderEventError(RecorderEvent recorderEvent, int i) {
            String error = "错误码为：" + i;

//            //把错误信息传递到Activity上
//            Message message = new Message();
//            message.arg1 = 2;
//            Bundle bundle = new Bundle();
//            bundle.putString("error", error);
//            message.setData(bundle);
//            myHander.sendMessage(message);
        }

        @Override
        public void onRecorderRecording(byte[] bytes, int i) {
//            File file = new File(voicePath);
//            if (!file.exists()) {
//                file.getParentFile().mkdirs();
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                FileOutputStream outputStream = new FileOutputStream(file);
//                outputStream.write(bytes);
//                outputStream.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
