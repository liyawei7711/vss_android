package huaiye.com.vvs.common.cmd;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
    InputStream is;
    String type;
    MsgListener msgListener;

    StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }

    StreamGobbler(InputStream is, String type, MsgListener msgListener) {
        this.is = is;
        this.type = type;
        this.msgListener = msgListener;
    }

    public void run() {
        try {
            Log.d("StreamGobbler","thead start " + type);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (msgListener != null){
                    msgListener.onMsg(line);
                }
                Log.d("StreamGobbler",type +" >" + line);
            }
        } catch (IOException ioe) {
            Log.d("StreamGobbler","thead err");
            ioe.printStackTrace();
        }
        Log.d("StreamGobbler","thead done " + type);

    }


    public interface MsgListener{
        void onMsg(String line);
    }
}

