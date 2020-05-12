package huaiye.com.vvs.ui.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.views.CheckableLinearLayout;

/**
 * author: admin
 * date: 2018/05/21
 * version: 0
 * mail: secret
 * desc: MediaLocalIndexActivity
 */
@BindLayout(R.layout.activity_local)
public class MediaLocalIndexActivity extends AppBaseActivity implements MediaLocalParent {

    @BindView(R.id.tv_edit)
    TextView tv_edit;
    @BindView(R.id.bottom_linear)
    View bottom_linear;
    @BindView(R.id.iv_choose_status)
    ImageView iv_choose_status;
    @BindView(R.id.tv_choose_status)
    TextView tv_choose_status;

    @BindView(R.id.checkable_video)
    CheckableLinearLayout checkable_video;
//    @BindView(R.id.checkable_image)
//    CheckableLinearLayout checkable_image;

    MediaLocalBaseFragment fragment_video;
    MediaLocalBaseFragment fragment_image;

    MediaLocalBaseFragment fragment_current;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        fragment_video = (MediaLocalBaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video);
        fragment_image = (MediaLocalBaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_image);
        fragment_image.setParentIntf(this);
        fragment_video.setParentIntf(this);

        getSupportFragmentManager().beginTransaction()
                .hide(fragment_image)
                .show(fragment_video)
                .commit();
        fragment_current = fragment_video;
        checkable_video.setChecked(true);
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {

    }

    @OnClick({
            R.id.tv_edit,
            R.id.view_chooseall,
            R.id.tv_delete,
            R.id.tv_upload,
            R.id.view_back,
            R.id.checkable_video,
            R.id.checkable_image
    })
    public void onWidgetClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                if (bottom_linear.getVisibility() == View.GONE) {
                    tv_edit.setText(AppUtils.getString(R.string.cancel));
                    bottom_linear.setVisibility(View.VISIBLE);
                    fragment_current.setModeEdit();
                } else {
                    tv_edit.setText(AppUtils.getString(R.string.edit));
                    bottom_linear.setVisibility(View.GONE);
                    fragment_current.cancelCurrentAction();

                    fragment_current.clearStates();
                    iv_choose_status.setImageResource(R.drawable.ic_status_choosed_no);
                    tv_choose_status.setText(AppUtils.getString(R.string.selected_all));
                }
                break;
            case R.id.view_chooseall:
                if (!fragment_current.isAllChoosed()) {
                    iv_choose_status.setImageResource(R.drawable.ic_status_choosed_yes);
                    tv_choose_status.setText(AppUtils.getString(R.string.selected_all_not));
                    fragment_current.chooseAll();
                } else {
                    iv_choose_status.setImageResource(R.drawable.ic_status_choosed_no);
                    tv_choose_status.setText(AppUtils.getString(R.string.selected_all));
                    fragment_current.clearChoosed();
                }
                break;
            case R.id.tv_delete:
                getLogicDialog().setTitleText(AppUtils.getString(R.string.delete))
                        .setMessageText(AppUtils.getString(R.string.delete_confirm))
                        .setConfirmClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fragment_current.deleteChoosed();
                            }
                        })
                        .setCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getLogicDialog().dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.tv_upload:
                fragment_current.uploadChoosed();
                break;
            case R.id.view_back:
                if (fragment_current.isUploading()) {
                    fragment_current.cancelCurrentAction();
                }
                onBackPressed();
                break;
            case R.id.checkable_video:
                if (checkable_video.isChecked()) {
                    return;
                } else if (fragment_current.isUploading()) {
                    showToast(AppUtils.getString(R.string.img_is_upload));
                    return;
                }

//                checkable_image.setChecked(false);
                checkable_video.setChecked(true);

                fragment_current.clearStates();
                iv_choose_status.setImageResource(R.drawable.ic_status_choosed_no);
                tv_choose_status.setText(AppUtils.getString(R.string.selected_all));
                tv_edit.setText(AppUtils.getString(R.string.edit));

                bottom_linear.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .hide(fragment_image)
                        .show(fragment_video)
                        .commit();
                fragment_current = fragment_video;
                break;
            case R.id.checkable_image:
//                if (checkable_image.isChecked()) {
//                    return;
//                } else
                if (fragment_current.isUploading()) {
                    showToast(AppUtils.getString(R.string.video_is_upload));
                    return;
                }

//                checkable_image.setChecked(true);
                checkable_video.setChecked(false);

                fragment_current.clearStates();
                iv_choose_status.setImageResource(R.drawable.ic_status_choosed_no);
                tv_choose_status.setText(AppUtils.getString(R.string.selected_all));
                tv_edit.setText(AppUtils.getString(R.string.edit));

                bottom_linear.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .hide(fragment_video)
                        .show(fragment_image)
                        .commit();
                fragment_current = fragment_image;
                break;
        }
    }

    @Override
    public void setChooseAll(boolean value) {
        if (value) {
            iv_choose_status.setImageResource(R.drawable.ic_status_choosed_yes);
            tv_choose_status.setText(AppUtils.getString(R.string.selected_all_not));
        } else {
            iv_choose_status.setImageResource(R.drawable.ic_status_choosed_no);
            tv_choose_status.setText(AppUtils.getString(R.string.selected_all));
        }
    }

    @Override
    public void onBackPressed() {
        if (bottom_linear.getVisibility() != View.GONE) {
            tv_edit.setText(AppUtils.getString(R.string.edit));
            bottom_linear.setVisibility(View.GONE);

            fragment_current.cancelCurrentAction();
            fragment_current.clearStates();
            iv_choose_status.setImageResource(R.drawable.ic_status_choosed_no);
            tv_choose_status.setText(AppUtils.getString(R.string.selected_all));
            return;
        }
        super.onBackPressed();

    }
}
