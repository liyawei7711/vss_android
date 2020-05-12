package huaiye.com.vvs.common.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import huaiye.com.vvs.R;

/**
 * @Describe 基于dialogFragment的确认dialog
 * @Author lxf
 * @date 2018-12-25
 */
public class ConfirmDialogFragment extends DialogFragment {
    private View.OnClickListener  mOkListener;
    private static String PARAMS_INFO = "info";
    public static ConfirmDialogFragment getInstance(String info){
        ConfirmDialogFragment updateInfoDialog = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAMS_INFO,info);
        updateInfoDialog.setArguments(bundle);
        return updateInfoDialog;
    }


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_logic,container,false);
        String info = getArguments().getString(PARAMS_INFO);
        TextView tvMsg =  view.findViewById(R.id.tv_message);
        TextView tvCancel =  view.findViewById(R.id.tv_cancel);
        TextView tvConfirm =  view.findViewById(R.id.tv_confirm);
        tvMsg.setText(info);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOkListener != null){
                    mOkListener.onClick(v);
                }

            }
        });

        return view;
    }

    public void onStart() {
        super.onStart();
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.time_dialog);
        Dialog dialog = this.getDialog();
        if(dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        if (Build.VERSION.SDK_INT >= 19){
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }


    public void setOkListener(View.OnClickListener listener){
        mOkListener = listener;
    }
}
