package huaiye.com.vvs.ui.home;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.map.baidu.LocationService;

import static huaiye.com.vvs.common.AppUtils.STRING_KEY_LOCATION_FREQUENCY;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_LOCATION_FREQUENCY_HIGH;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_LOCATION_FREQUENCY_LOW;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_LOCATION_FREQUENCY_MIDDLE;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_false;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_map_type;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_true;

/**
 * author: admin
 * date: 2018/06/15
 * version: 0
 * mail: secret
 * desc: AudioSettingActivity
 */

@BindLayout(R.layout.activity_map_setting)
public class MapSettingActivity extends AppBaseActivity {

    @BindView(R.id.rg_map_weixing)
    RadioGroup rg_map_weixing;
    @BindView(R.id.rbt_map_weixing_close)
    RadioButton rbt_weixing_close;
    @BindView(R.id.rbt_map_weixing_open)
    RadioButton rbt_weixing_open;
    @BindView(R.id.rg_location_frequency)
    RadioGroup rg_location_frequency;
    @BindView(R.id.rbt_location_frequency_low)
    RadioButton rbt_location_frequency_low;
    @BindView(R.id.rbt_location_frequency_middle)
    RadioButton rbt_location_frequency_middle;
    @BindView(R.id.rbt_location_frequency_high)
    RadioButton rbt_location_frequency_high;



    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.map_setting))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        boolean mapShowType = Boolean.parseBoolean(SP.getParam(STRING_KEY_map_type, STRING_KEY_false).toString());
        if (mapShowType){
            rbt_weixing_open.setChecked(true);
            rbt_weixing_close.setChecked(false);
        }else {
            rbt_weixing_open.setChecked(false);
            rbt_weixing_close.setChecked(true);
        }

        rg_map_weixing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbt_map_weixing_open){
                    SP.setParam(STRING_KEY_map_type, STRING_KEY_true);
                }else {
                    SP.setParam(STRING_KEY_map_type, STRING_KEY_false);
                }

            }
        });
        String locationFrequency = (String) SP.getParam(STRING_KEY_LOCATION_FREQUENCY, STRING_KEY_LOCATION_FREQUENCY_HIGH);
        switch (locationFrequency){
            case STRING_KEY_LOCATION_FREQUENCY_LOW:
                rbt_location_frequency_low.setChecked(true);
                rbt_location_frequency_middle.setChecked(false);
                rbt_location_frequency_high.setChecked(false);
                break;
            case STRING_KEY_LOCATION_FREQUENCY_MIDDLE:
                rbt_location_frequency_low.setChecked(false);
                rbt_location_frequency_middle.setChecked(true);
                rbt_location_frequency_high.setChecked(false);
                break;
            case STRING_KEY_LOCATION_FREQUENCY_HIGH:
                rbt_location_frequency_low.setChecked(false);
                rbt_location_frequency_middle.setChecked(false);
                rbt_location_frequency_high.setChecked(true);
                break;
        }
        rg_location_frequency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LocationService locationService = ((MCApp) getApplication()).locationService;
                if (checkedId == R.id.rbt_location_frequency_low){
                    SP.putString(STRING_KEY_LOCATION_FREQUENCY,STRING_KEY_LOCATION_FREQUENCY_LOW);
                    locationService.updateStrategy(STRING_KEY_LOCATION_FREQUENCY_LOW);
                }
                if (checkedId == R.id.rbt_location_frequency_middle){
                    SP.putString(STRING_KEY_LOCATION_FREQUENCY,STRING_KEY_LOCATION_FREQUENCY_MIDDLE);
                    locationService.updateStrategy(STRING_KEY_LOCATION_FREQUENCY_MIDDLE);
                }
                if (checkedId == R.id.rbt_location_frequency_high){
                    SP.putString(STRING_KEY_LOCATION_FREQUENCY,STRING_KEY_LOCATION_FREQUENCY_HIGH);
                    locationService.updateStrategy(STRING_KEY_LOCATION_FREQUENCY_HIGH);
                }
            }
        });



    }




}
