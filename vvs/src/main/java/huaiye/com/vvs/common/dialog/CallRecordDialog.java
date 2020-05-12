package huaiye.com.vvs.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import huaiye.com.vvs.R;
import huaiye.com.vvs.dao.msgs.CallRecordMessage;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 通话记录选择对话框
 *
 * @author Administrator
 */
public class CallRecordDialog extends Dialog {

    private Context context;
    private View tv_talk;
    private View tv_talk_video;
    private View tv_meet;

    private CompleteListener listener;
    private CallRecordMessage callRecordMessage;

    public CallRecordMessage getCallRecordMessage() {
        return callRecordMessage;
    }

    public void setCallRecordMessage(CallRecordMessage callRecordMessage) {
        this.callRecordMessage = callRecordMessage;
    }

    public CallRecordDialog(Context context) {
        super(context);
        this.context = context;
        themeInit();
        lazyInit();
    }

    private void themeInit() {
        this.getWindow().setGravity(Gravity.CENTER);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        this.getWindow().setGravity(Gravity.BOTTOM);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        lp.width = MATCH_PARENT;
        lp.height = WRAP_CONTENT; // 宽度

        dialogWindow.setAttributes(lp);
    }

    private void lazyInit() {

        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_call_record, null);
        setContentView(rootView);

        WindowManager.LayoutParams params = this.getWindow().getAttributes();
//        getWindow().setGravity(Gravity.BOTTOM);
        params.width = context.getResources().getDisplayMetrics().widthPixels;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(params);

        tv_talk = rootView.findViewById(R.id.tv_talk);
        tv_talk_video = rootView.findViewById(R.id.tv_talk_video);
        tv_meet = rootView.findViewById(R.id.tv_meet);

        actionInit();
    }

    private void actionInit() {
        tv_talk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.talk(callRecordMessage);
                }
            }
        });
        tv_talk_video.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.talkVideo(callRecordMessage);
                }
            }
        });
        tv_meet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.meet(callRecordMessage);
                }
            }
        });
    }

    public void setCompleteListener(CompleteListener listener) {
        this.listener = listener;
    }

    public interface CompleteListener {

        void talk(CallRecordMessage callRecordMessage);

        void talkVideo(CallRecordMessage callRecordMessage);

        void meet(CallRecordMessage callRecordMessage);
    }
}
