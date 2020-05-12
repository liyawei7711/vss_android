package huaiye.com.vvs.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import huaiye.com.vvs.dao.auth.AppAuth;
import huaiye.com.vvs.dao.msgs.AppMessages;
import ttyy.com.datasdao.Core;
import ttyy.com.datasdao.DaoBuilder;
import ttyy.com.datasdao.Datas;
import ttyy.com.sp.multiprocess.AppStores;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppDatas
 */

public class AppDatas {
    public static final int VERSION = 7;
    public static final String DBNAME = "VSS";

    static Context context;
    static AppConstants CONSTANTS;

    private AppDatas() {

    }

    public static void init(Context context) {
        AppDatas.context = context.getApplicationContext();

        DaoBuilder builder = DaoBuilder.from(AppDatas.context)
                .setDebug(true)
                .setVersion(VERSION)
                .setDbName(DBNAME)
                .setDbDir(AppDatas.context.getExternalFilesDir("dbs").getAbsolutePath())
                .setCallback(new DaoBuilder.Callback() {
                    @Override
                    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//                        if (i == 7 && i1 == 8){
//                            //Datas.from(sqLiteDatabase).
//                        }
//                        else
                        {
                            Datas.from(sqLiteDatabase).dropAllTables();
                        }
                    }

                    @Override
                    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

                    }
                });

        Datas.createSqliteDatabase(builder);
        AppStores.get(AppDatas.context);

        CONSTANTS = new AppConstants();
    }

    public static Core DB() {
        return Datas.from(DBNAME);
    }

    public static AppConstants Constants() {
        return CONSTANTS;
    }

    public static AppAuth Auth() {
        return AppAuth.get();
    }

    public static AppMessages Messages() {
        return AppMessages.get();
    }

}
