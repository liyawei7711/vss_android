package huaiye.com.vvs.common.cmd;

import com.huaiye.sdk.logger.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CMD {

    public interface onFinishListener{
        void onFinish();
    }

    public static void runCmd(final StreamGobbler.MsgListener outputListener, final StreamGobbler.MsgListener errorListener,final onFinishListener finishListener, String... cmd) throws IOException, InterruptedException {
        DataOutputStream dos = null;
        final Process p = Runtime.getRuntime().exec("sh");
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
    }



    public static void runCmd(final StreamGobbler.MsgListener outputListener, final StreamGobbler.MsgListener errorListener, String... cmd) throws IOException, InterruptedException {
        runCmd(outputListener,errorListener,null,cmd);
    }

    public  static  String runCmdSync(String... cmd) {
        try {
            Process p = Runtime.getRuntime().exec("sh");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());

            for (String oneCmd : cmd) {
                dos.writeBytes(oneCmd + "\n");
                dos.flush();
            }
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            DataInputStream dis = new DataInputStream(p.getInputStream());
            String line = null;
            String result = "";
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}
