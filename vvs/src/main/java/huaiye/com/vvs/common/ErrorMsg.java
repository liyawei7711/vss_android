package huaiye.com.vvs.common;

import huaiye.com.vvs.R;

/**
 * Created by Administrator on 2018\3\9 0009.
 */

public class ErrorMsg {
    public static final int meet_not_koten_code = 1720310003;
    public static final int meet_not_exit_code = 1720410012;
    public static final int meet_max_num_code = 1720310004;
    private static final String meet_not_exit = AppUtils.getString(R.string.meet_not_exit);
    private static final String meet_max_num = AppUtils.getString(R.string.meet_max_num);

    public static final int white_board_not_exist_code = 1720410013; //会议没有白板分享
    public static final int white_board_has_exist_code = 1720410014; //会议已存在白板分享
    public static final int white_board_not_opener_code = 1720410015; //不是白板分享发起者
    public static final int white_board_exist_code = 1720410016; //不是白板分享发起者
    public static final int white_board_start_code = 27;
    private static final String white_board_not_exist = AppUtils.getString(R.string.white_board_not_exist);
    private static final String white_board_has_exist = AppUtils.getString(R.string.white_board_has_exist);
    private static final String white_board_not_opener = AppUtils.getString(R.string.white_board_not_opener);
    private static final String white_board_exist = AppUtils.getString(R.string.white_board_exist);
    private static final String white_board_start = AppUtils.getString(R.string.white_board_start);

    public static final int re_load_code = 1720200002;
    public static final int login_err_code = 0;
    public static final int login_empty_code = 1010100003;
    public static final int login_error_code = 1010100005;
    public static final int create_meet_err_code = 1;
    public static final int raise_hands_err_code = 2;
    public static final int joine_err_code = 3;
    public static final int send_nofity_err_code = 4;
    public static final int start_play_err_code = 5;
    public static final int delete_meet_err_code = 6;
    public static final int update_meet_err_code = 7;
    public static final int invite_user_err_code = 8;
    public static final int get_meet_info_err_code = 9;
    public static final int quite_talk_err_code = 10;
    public static final int kitout_err_code = 11;
    public static final int change_video_err_code = 12;
    public static final int getlayout_info_err_code = 13;
    public static final int jinyan_close_err_code = 14;
    public static final int jinyan_open_err_code = 15;
    public static final int start_talk_err_code = 16;
    public static final int join_talk_err_code = 17;
    public static final int create_group_err_code = 18;
    public static final int get_err_code = 19;
    public static final int delete_err_code = 20;
    public static final int quite_err_code = 21;
    public static final int update_err_code = 22;
    public static final int add_err_code = 23;
    public static final int open_white_board_code = 24;
    public static final int close_white_board_code = 25;
    public static final int update_white_board_code = 26;
    public static final int switch_meet_layout_code = 27;
    public static final int start_record_code = 28;
    public static final int upload_code = 29;
    public static final int cancel_invite_user_err_code = 30;
    private static String login_err = AppUtils.getString(R.string.login_err);
    private static String login_empty = AppUtils.getString(R.string.login_empty);
    private static String login_error = AppUtils.getString(R.string.login_error_txt);
    private static String create_meet_err = AppUtils.getString(R.string.create_meet_err);
    private static String raise_hands_err = AppUtils.getString(R.string.raise_hands_err);
    private static String joine_err = AppUtils.getString(R.string.joine_err);
    private static String send_nofity = AppUtils.getString(R.string.send_nofity);
    private static String start_play_err = AppUtils.getString(R.string.start_play_err);
    private static String delete_meet_err = AppUtils.getString(R.string.delete_meet_err);
    private static String update_meet_err = AppUtils.getString(R.string.update_meet_err);
    private static String invite_user_err = AppUtils.getString(R.string.invite_user_err);
    private static String canel_invite_user_err = AppUtils.getString(R.string.cancel_invite_user_err);
    private static String get_meet_info_err = AppUtils.getString(R.string.get_meet_info_err);
    private static String quite_talk_err = AppUtils.getString(R.string.quite_talk_err);
    private static String kitout_err = AppUtils.getString(R.string.kitout_err);
    private static String change_video_err = AppUtils.getString(R.string.change_video_err);
    private static String getlayout_info_err = AppUtils.getString(R.string.getlayout_info_err);
    private static String jinyan_close_err = AppUtils.getString(R.string.jinyan_close_err);
    private static String jinyan_open_err = AppUtils.getString(R.string.jinyan_open_err);
    private static String start_talk_err = AppUtils.getString(R.string.start_talk_err);
    private static String join_talk_err = AppUtils.getString(R.string.join_talk_err);
    private static String create_group_err = AppUtils.getString(R.string.create_group_err);
    private static String get_err = AppUtils.getString(R.string.get_err);
    private static String delete_err = AppUtils.getString(R.string.delete_err);
    private static String quite_err = AppUtils.getString(R.string.quite_err);
    private static String update_err = AppUtils.getString(R.string.update_err);
    private static String add_err = AppUtils.getString(R.string.add_err);
    private static String open_white_board = AppUtils.getString(R.string.open_white_board);
    private static String close_white_board = AppUtils.getString(R.string.close_white_board);
    private static String update_white_board = AppUtils.getString(R.string.update_white_board);
    private static String switch_meet_layout = AppUtils.getString(R.string.switch_meet_layout);
    private static String start_record = AppUtils.getString(R.string.start_record);
    private static String upload = AppUtils.getString(R.string.upload);


    public static String getMsg(int code) {
        switch (code) {
            case login_err_code:
                return login_err;
            case login_empty_code:
                return login_empty;
            case login_error_code:
                return login_error;
            case create_meet_err_code:
                return create_meet_err;
            case raise_hands_err_code:
                return raise_hands_err;
            case joine_err_code:
                return joine_err;
            case send_nofity_err_code:
                return send_nofity;
            case start_play_err_code:
                return start_play_err;
            case delete_meet_err_code:
                return delete_meet_err;
            case update_meet_err_code:
                return update_meet_err;
            case invite_user_err_code:
                return invite_user_err;
            case cancel_invite_user_err_code:
                return canel_invite_user_err;
            case get_meet_info_err_code:
                return get_meet_info_err;
            case quite_talk_err_code:
                return quite_talk_err;
            case kitout_err_code:
                return kitout_err;
            case change_video_err_code:
                return change_video_err;
            case getlayout_info_err_code:
                return getlayout_info_err;
            case jinyan_close_err_code:
                return jinyan_close_err;
            case jinyan_open_err_code:
                return jinyan_open_err;
            case start_talk_err_code:
                return start_talk_err;
            case join_talk_err_code:
                return join_talk_err;
            case create_group_err_code:
                return create_group_err;
            case get_err_code:
                return get_err;
            case delete_err_code:
                return delete_err;
            case quite_err_code:
                return quite_err;
            case update_err_code:
                return update_err;
            case add_err_code:
                return add_err;
            case open_white_board_code:
                return open_white_board;
            case close_white_board_code:
                return close_white_board;
            case update_white_board_code:
                return update_white_board;
            case meet_not_koten_code:
            case meet_not_exit_code:
                return meet_not_exit;
            case meet_max_num_code:
                return meet_max_num;
            case switch_meet_layout_code:
                return switch_meet_layout;
            case start_record_code:
                return start_record;
            case upload_code:
                return upload;

        }
        return AppUtils.getString(R.string.error);
    }

    public static String getMsgWhiterBoard(int code) {
        switch (code) {
            case white_board_has_exist_code:
                return white_board_has_exist;
            case white_board_not_opener_code:
                return white_board_not_opener;
            case white_board_not_exist_code:
                return white_board_not_exist;
            case white_board_exist_code:
                return white_board_exist;
        }
        return white_board_start;
    }
}
