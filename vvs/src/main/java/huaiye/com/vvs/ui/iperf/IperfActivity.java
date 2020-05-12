package huaiye.com.vvs.ui.iperf;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaiye.sdk.logger.Logger;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.cmd.CMD;
import huaiye.com.vvs.common.cmd.CmdOperator;
import huaiye.com.vvs.common.cmd.StreamGobbler;
import huaiye.com.vvs.dao.AppDatas;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPCallback;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * udp 可以显示出丢包率 延迟
 */
@BindLayout(R.layout.activity_iperf)
public class IperfActivity extends AppBaseActivity {

    private final String BASE_FILE_DIR = "/data/data/com.huaiye.vss/";
    private final String CMD_FORMAT = "iperf -c %s -p %s -t %s -i 1";

    @BindView(R.id.btn_start)
    Button btn_start;
    @BindView(R.id.et_cmd)
    EditText et_cmd;
    @BindView(R.id.rbt_tcp)
    RadioButton rbt_tcp;
    @BindView(R.id.rbt_udp)
    RadioButton rbt_udp;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.et_port)
    EditText et_port;
    @BindView(R.id.et_interval)
    EditText et_interval;
    @BindView(R.id.rg_trans)
    RadioGroup rg_trans;

    @BindView(R.id.tv_info)
    TextView tv_info;
    @BindView(R.id.ll_width)
    LinearLayout ll_width;
    @BindView(R.id.et_width)
    EditText et_width;
    @BindView(R.id.ll_delay)
    LinearLayout ll_delay;
    @BindView(R.id.ll_bandwidth)
    LinearLayout ll_bandwidth;
    @BindView(R.id.ll_lost)
    LinearLayout ll_lost;
    @BindView(R.id.tv_delay)
    TextView tv_delay;
    @BindView(R.id.tv_bandwidth)
    TextView tv_bandwidth;
    @BindView(R.id.tv_lost)
    TextView tv_lost;

    String usePath;

    Handler handler;

//    boolean isRunning;

    private final int STATUS_NONE = 0;
    private final int STATUS_PREPARE = 1;
    private final int STATUS_RUNNING = 2;
    private final int STATUS_CLEARING = 3;

    int runningStatus = STATUS_NONE;

    CmdOperator cmdOperator ;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.bandwidth_detection))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
        handler = new Handler();
        cmdOperator = new CmdOperator();
    }

    @Override
    public void doInitDelay() {

        initDefaultValue();
        initListener();

        ensureIperfFile();

        String strBinDir = getTargetBin(getAbi());
        usePath = BASE_FILE_DIR + strBinDir;
        Logger.debug("IperfActivity usePath " + usePath);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runningStatus == STATUS_RUNNING){
                    stopIperf();
                    return;
                }
                tv_delay.setText("--");
                tv_bandwidth.setText("--");
                tv_lost.setText("--");
                tv_info.setText("");
                tv_info.scrollTo(0,0);
                callServiceStart();
            }
        });
    }

    private void callServiceStart(){
        final String startFormat = "http://%s:80/cgi-bin/sys.cgi?method=iperf&cmd=start&port=%s&ts=%s";
        String strTrans = rbt_tcp.isChecked() ? "tcp" :"udp";
        String strStartCmd = String.format(startFormat,et_address.getText().toString(), et_port.getText().toString(), strTrans);
        runningStatus = STATUS_PREPARE;
        btn_start.setEnabled(false);
        Https.get(strStartCmd)
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        Logger.debug("IperfActivity callServiceStart  onPreStart");

                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {

                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {
                        Logger.debug("IperfActivity callServiceStart  onSuccess");
                        postRunOnMain(new Runnable() {
                            @Override
                            public void run() {
                                runIperf();
                            }
                        });
                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {

                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        postRunOnMain(new Runnable() {
                            @Override
                            public void run() {
                                runningStatus = STATUS_NONE;
                                btn_start.setText(AppUtils.getString(R.string.bandwidth_start));
                            }
                        });
                        Logger.debug("IperfActivity callServiceStart  onFailure");

                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                })
                .build()
                .requestAsync();
    }


    private void callServiceStop(){
        final String startFormat = "http://%s:80/cgi-bin/sys.cgi?method=iperf&cmd=stop&port=%s&ts=%s";
        String strTrans = rbt_tcp.isChecked() ? "tcp" :"udp";
        String strStartCmd = String.format(startFormat,et_address.getText().toString(), et_port.getText().toString(), strTrans);

        Https.get(strStartCmd)
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        Logger.debug("IperfActivity callServiceStop  onPreStart");

                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {

                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {
                        Logger.debug("IperfActivity callServiceStop  onSuccess");
                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {

                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        Logger.debug("IperfActivity callServiceStart  onFailure");

                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                })
                .build()
                .requestAsync();
    }



    private void initDefaultValue() {
        rbt_tcp.setChecked(true);
        et_address.setText(AppDatas.Constants().getAddressIP());
        et_port.setText("5001");
        et_interval.setText("10");
        et_width.setText("2");
        generateCMD();
        tv_info.setMovementMethod(ScrollingMovementMethod.getInstance());

    }

    private void initListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                generateCMD();
            }
        };
        et_address.addTextChangedListener(textWatcher);
        et_port.addTextChangedListener(textWatcher);
        et_interval.addTextChangedListener(textWatcher);
        et_width.addTextChangedListener(textWatcher);

        rg_trans.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                generateCMD();
                if (checkedId == R.id.rbt_tcp) {
                    changeView(true);
                } else {
                    changeView(false);
                }

            }
        });
        changeView(true);
    }


    void changeView(boolean tcp){
        if (tcp) {
            ll_width.setVisibility(View.GONE);
            ll_delay.setVisibility(View.GONE);
            ll_lost.setVisibility(View.GONE);
        } else {
            ll_width.setVisibility(View.VISIBLE);
            ll_delay.setVisibility(View.VISIBLE);
            ll_lost.setVisibility(View.VISIBLE);
        }
    }

    private void generateCMD() {
        String strCMD = String.format(CMD_FORMAT, et_address.getText().toString(), et_port.getText().toString(), et_interval.getText().toString());
        if (rbt_udp.isChecked()) {
            strCMD = strCMD + " -u";
            String width = et_width.getText().toString();
            if (!TextUtils.isEmpty(width)) {
                strCMD = strCMD + " -b " + width + "M";
            }
        }
        et_cmd.setText(strCMD);
    }

    /**
     * 确保文件夹下面有iperf文件的存在
     */
    private void ensureIperfFile() {
        File vssFileDir = new File(BASE_FILE_DIR);
        File fileIperf = new File(vssFileDir, "iperf");
        if (!fileIperf.exists()) {
            Logger.debug("IperfActivity !fileIperf.exists ");
            copyAndRelease("iperf");
            changtToX("iperf");

        }
        File fileIperfMips = new File(vssFileDir, "iperf_mips");
        if (!fileIperfMips.exists()) {
            Logger.debug("IperfActivity !fileIperfMips.exists() ");
            copyAndRelease("iperf_mips");
            changtToX("iperf_mips");

        }
        File fileIperfX86 = new File(vssFileDir, "iperf_x86");
        if (!fileIperfX86.exists()) {
            Logger.debug("IperfActivity !fileIperfX86.exists() ");
            copyAndRelease("iperf_x86");
            changtToX("iperf_x86");
        }
    }


    /**
     * 将iperf文件解压到对应的文件夹
     * @param name
     */
    private void copyAndRelease(String name) {
        copyFile2Sdcard(name + ".zip",BASE_FILE_DIR);
        File targetDir = new File(BASE_FILE_DIR,name);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        try {
            File vssFileDir = new File(BASE_FILE_DIR);
            AppUtils.UnZipFolder(vssFileDir.getPath() + "/" + name + ".zip", targetDir.getPath());
        } catch (Exception e) {
            Logger.debug("IperfActivity copyAndRelease error  " + e);
            e.printStackTrace();
        }
    }

    /**
     * 把assert文件拷贝到文件夹里
     *
     * @param assetsFileName
     * @return
     */
    private File copyFile2Sdcard(String assetsFileName,String targetDir) {
        try {
            InputStream inputStream = getAssets().open(assetsFileName);
            File extFileDir = new File(targetDir);
            if (!extFileDir.exists()) {
                extFileDir.mkdirs();
            }
            File outputIperf = new File(extFileDir, assetsFileName);
            if (outputIperf.exists()) {
                outputIperf.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(outputIperf);
            byte[] buff = new byte[1024];
            while (inputStream.read(buff) != -1) {
                fileOutputStream.write(buff);
            }
            fileOutputStream.close();
            inputStream.close();
            return outputIperf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTargetBin(String mAbi) {
        if (mAbi.startsWith("arm")) {
//            mAbi = "arm";
            return "iperf/bin/";
        } else if (mAbi.contains("i386")) {
//            mAbi = "x86";
            return "iperf_x86/bin/";
        } else if (mAbi.contains("mip")) {
            return "iperf_mips/bin/";

        } else if ((mAbi.compareTo("x86") != 0) && (mAbi.compareTo("mips") != 0)) {
            if (mAbi.compareTo("Unknown") == 0) {
                Logger.debug("IperfActivity Error: ABI is unknown, defaulting to 'armeabi'");
                return "iperf/bin/";
            } else if (mAbi.compareTo("none") == 0) {
                if (System.getProperty("os.arch").compareTo("i686") == 0) {
                    return "iperf_x86/bin/";
                }
            }
        }
        Logger.debug("IperfActivity Error: ABI '" + mAbi + "' is not supported, defaulting to 'armeabi'");
        return "iperf/bin/";

    }

    /**
     * 将文件变为可执行
     * @param dirName
     */
    private void changtToX(final String dirName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cmdChdir = "chdir " + BASE_FILE_DIR +dirName +"/bin/";
                String chmod  = "chmod 755 iperf";
                try {
                    CMD.runCmd(null,null,cmdChdir, chmod);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopIperf(){
        if (runningStatus == STATUS_RUNNING){
            btn_start.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (runningStatus == STATUS_RUNNING){
                        runningStatus = STATUS_CLEARING;
                        cmdOperator.clear();
                        runningStatus = STATUS_NONE;
                    }
                    postRunOnMain(new Runnable() {
                        @Override
                        public void run() {
                            btn_start.setEnabled(true);
                            btn_start.setText(AppUtils.getString(R.string.bandwidth_start));
                        }
                    });
                }
            }).start();
        }
    }


    private void runIperf(){
        runningStatus = STATUS_RUNNING;
        btn_start.setEnabled(true);
        btn_start.setText(AppUtils.getString(R.string.bandwidth_stop));
        new Thread(new Runnable() {
            @Override
            public void run() {

                String strCmd = "chdir " + usePath;
                final String strCmd2 = "./"+et_cmd.getText().toString();
                try {
                    cmdOperator.runCmd(new StreamGobbler.MsgListener() {
                        @Override
                        public void onMsg(String line) {
                            Logger.debug("IperfActivity read output line  " + line);
                            showInfo(line);
                            if (strCmd2.contains("-u")) {
                                parseInfoUDP(line);
                            } else {
                                parseInfoTCP(line);
                            }
                        }
                    }, new StreamGobbler.MsgListener() {
                        @Override
                        public void onMsg(String line) {
                            Logger.debug("IperfActivity read err line  " + line);
                            showInfo(line);
                        }
                    }, new CMD.onFinishListener() {
                        @Override
                        public void onFinish() {
                            postRunOnMain(new Runnable() {
                                @Override
                                public void run() {
                                    runningStatus = STATUS_NONE;
                                    btn_start.setText(AppUtils.getString(R.string.bandwidth_start));
                                }
                            });
                            callServiceStop();
                        }
                    },strCmd, strCmd2);
                } catch (IOException e) {
                    runningStatus = STATUS_NONE;
                    btn_start.setText(AppUtils.getString(R.string.bandwidth_start));
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    runningStatus = STATUS_NONE;
                    btn_start.setText(AppUtils.getString(R.string.bandwidth_start));
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getAbi() {
        String strAbi = CMD.runCmdSync("getprop ro.product.cpu.abi");
        Logger.debug("IperfActivity abi " + strAbi);
        return strAbi;
    }

    private void showInfo(final String line) {
        if (isDestroyed() || isFinishing() || handler == null ){
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(tv_info.getText());
                stringBuilder.append(line);
                stringBuilder.append("\n");
                tv_info.setText(stringBuilder.toString());
                int offset = tv_info.getLineCount() * tv_info.getLineHeight();
                if (offset > tv_info.getHeight()) {
                    tv_info.scrollTo(0, offset - tv_info.getHeight());
                }
            }
        });
    }

    private void parseInfoTCP(String line) {
        if (isDestroyed() || isFinishing() || handler == null ){
            return;
        }
        if (!TextUtils.isEmpty(line) && line.contains("sec")) {
            String[] infos = line.split(" ");
            if (infos.length <= 2) {
                return;
            }
            final String speed = infos[infos.length - 2];
            final String speedUnit = infos[infos.length - 1];
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tv_bandwidth.setText(speed+speedUnit);
                }
            });
            Logger.debug("IperfActivity current speed  " + speed + speedUnit);
        }
    }

    //     0.0- 5.0 sec   642 KBytes  1.05 Mbits/sec   3.461 ms    0/  447 (0%)
    private void parseInfoUDP(String line) {
        if (isDestroyed() || isFinishing() || handler == null ){
            return;
        }
        if (!TextUtils.isEmpty(line) && line.contains("%")) {
            String[] infos = line.split(" ");
            String strDelay = null;
            String bandWidth =  null;
            for (int i = 0; i < infos.length; i++) {
                Logger.debug("IperfActivity parseInfoUDP   [" + i + "] " + infos[i]);
                if (infos[i].equals("ms")){
                    strDelay = infos[i-1]+infos[i];
                }
                if (infos[i].contains("/sec")){
                    bandWidth = infos[i-1] + infos[i];
                }
            }
            int length = infos.length;
            String strLostOrigin = infos[length - 1];
            final String strLost = strLostOrigin.substring(1, strLostOrigin.length() - 1);
            final String finalStrDelay = strDelay;
            final String finalBandWidth = bandWidth;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tv_bandwidth.setText(finalBandWidth);
                    tv_lost.setText(strLost);
                    tv_delay.setText(finalStrDelay);
                }
            });
            Logger.debug("IperfActivity parseInfoUDP  strDelay" + strDelay + " strLost " + strLost + " bandWidth " + bandWidth);
        }
    }

    private void postRunOnMain(Runnable runnable){
        if (isDestroyed() || isFinishing() || handler == null ){
            return;
        }
        handler.post(runnable);
    }



}
