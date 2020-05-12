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
import android.widget.Button;

import huaiye.com.vvs.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 设置性别对话框
 *
 * @author Administrator
 */
public class SetDialog extends Dialog {

    private Context context;

    private View btn_delete;
    private Button btn_continue_pause;
    private View btn_update;
    private View btnCancle;

    private CompleteListener listener;

    public SetDialog(Context context) {
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
        this.getWindow().setGravity(Gravity.BOTTOM);
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

        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_set_picker, null);
        setContentView(rootView);

        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        getWindow().setGravity(Gravity.BOTTOM);
        params.width = context.getResources().getDisplayMetrics().widthPixels;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(params);

        btn_delete = rootView.findViewById(R.id.btn_delete);
        btn_continue_pause = rootView.findViewById(R.id.btn_continue_pause);
        btn_update = rootView.findViewById(R.id.btn_update);
        btnCancle = rootView.findViewById(R.id.btn_cancel);

        actionInit();
    }

    private void actionInit() {

        btnCancle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.delete();
                }
            }
        });
        btn_continue_pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.tocontinue(btn_continue_pause.getText().toString());
                }
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.update();
                }
            }
        });
    }


    public void showContinue(boolean value, String str) {
        btn_continue_pause.setText(str);
        if (value) {
            btn_continue_pause.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.GONE);
        } else {
            btn_continue_pause.setVisibility(View.GONE);
            btn_update.setVisibility(View.VISIBLE);
        }

    }

    public void setCompleteListener(CompleteListener listener) {
        this.listener = listener;
    }

    public interface CompleteListener {
        void onComplete(String birthDay);

        void tocontinue(String str);

        void delete();

        void update();
    }
}
