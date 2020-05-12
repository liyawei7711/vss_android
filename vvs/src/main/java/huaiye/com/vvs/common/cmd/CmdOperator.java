package huaiye.com.vvs.common.cmd;

import com.huaiye.sdk.logger.Logger;

import java.io.DataOutputStream;
import java.io.IOException;

public class CmdOperator {
    private Process p ;
    public CmdOperator(){}

    public void runCmd(final StreamGobbler.MsgListener outputListener, final StreamGobbler.MsgListener errorListener, final CMD.onFinishListener finishListener, String... cmd) throws IOException, InterruptedException {
        DataOutputStream dos = null;
        p = Runtime.getRuntime().exec("sh");
        dos = new DataOutputStream(p.getOutputStream());

        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR", new StreamGobbler.MsgListener() {
            @Override
            public void onMsg(String line) {
                Logger.debug("runCmd err line  " + line);
                if (errorListener != null) {
                    errorListener.onMsg(line);
                }
            }
        });
        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", new StreamGobbler.MsgListener() {
            @Override
            public void onMsg(String line) {
                Logger.debug("runCmd output line  " + line);
                if (outputListener != null) {
                    outputListener.onMsg(line);
                }

            }
        });

        errorGobbler.start();
        outputGobbler.start();
        for (String oneCmd : cmd) {
            dos.writeBytes(oneCmd + "\n");
            dos.flush();
        }

        dos.writeBytes("exit\n");
        dos.flush();

        Logger.debug("runCmd start");
        int exitValue = p.waitFor();
        Logger.debug("runCmd end " + exitValue);
        if (finishListener != null){
            finishListener.onFinish();
        }
        clear();
    }



    public void runCmd(final StreamGobbler.MsgListener outputListener, final StreamGobbler.MsgListener errorListener, String... cmd) throws IOException, InterruptedException {
        runCmd(outputListener,errorListener,null,cmd);
    }

    public void clear(){
        if (p != null){
            p.destroy();
            p = null;
        }
    }
}
