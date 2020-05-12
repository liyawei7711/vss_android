package huaiye.com.vvs.ui.meet.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.views.pickers.SelectItemDialog;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: MeetCreateHeaderView
 */
@BindLayout(R.layout.header_meet_create)
public class MeetCreateHeaderView extends RelativeLayout {

    @BindView(R.id.tv_hint1)
    TextView tv_hint1;
    @BindView(R.id.edt_meet_name)
    EditText edt_meet_name;
    @BindView(R.id.cb_record)
    CheckBox cb_record;
    @BindView(R.id.tv_label)
    TextView tv_label;
    @BindView(R.id.tv_label_user)
    TextView tv_label_user;
    @BindView(R.id.need_record)
    View need_record;
    @BindView(R.id.ll_divider)
    View ll_divider;

    boolean isMaster;

    ArrayList<SelectItemDialog.SelectBean> selectBeans = new ArrayList<>();
    String Temp = "yyyy-MM-dd HH:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(Temp, Locale.CHINA);

    public MeetCreateHeaderView(Context context, final boolean isMeet, boolean isOrder) {
        this(context, null);

        setMaster(true);
        tv_hint1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_meet_name.requestFocus();
                AppUtils.showKeyboard(edt_meet_name);
                edt_meet_name.setSelection(edt_meet_name.getText().toString().length());
            }
        });

        edt_meet_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    if (isMeet) {
                        tv_hint1.setHint(AppUtils.getString(R.string.meet_diaodu_name));
                    } else {
                        tv_hint1.setHint(AppUtils.getString(R.string.quanliao_name));
                    }
                } else {
                    tv_hint1.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        need_record.setVisibility(VISIBLE);
        ll_divider.setVisibility(VISIBLE);
        tv_label.setText(AppUtils.getString(R.string.meet_diaodu_title));
        tv_hint1.setHint(AppUtils.getString(R.string.meet_diaodu_name));
        tv_label_user.setText(AppUtils.getString(R.string.canhuiren_list));

    }

    public MeetCreateHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetCreateHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injectors.get().injectView(this);
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
        if (!isMaster) {
            edt_meet_name.setEnabled(false);
            tv_hint1.setEnabled(false);
            cb_record.setEnabled(false);
        } else {
            tv_hint1.setEnabled(true);
            edt_meet_name.setEnabled(true);
            cb_record.setEnabled(true);
        }
    }

    public String getMeetName() {
        return edt_meet_name.getText().toString();
    }

    public EditText getNameView() {
        return edt_meet_name;
    }


    public boolean isMeetRecord() {
        return cb_record.isChecked();
    }

    /**
     * 展示信息
     *
     * @param cGetMeetingInfoRsp
     */
    public void showInfo(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
        edt_meet_name.setText(cGetMeetingInfoRsp.strMeetingName);

        cb_record.setChecked(cGetMeetingInfoRsp.nRecordID == 0 ? false : true);
    }

}
