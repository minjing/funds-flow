package ministudio.fundsflow;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by min on 15/12/25.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fundsflow";
    private static final int DB_VERSION = 1;

    private static final String CREATE_DB_TABLES =
            "create table account (" +
                    "id integer primary key autoincrement, " +
                    "name text not null " +
            ")";

    private static final String DROP_DB_TABLES  = "drop table if exist account";

    DbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_DB_TABLES);
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
        Log.i("", "Upgrade database from " + oldv + " to " + newv);
        sqLiteDatabase.execSQL(DROP_DB_TABLES);
        onCreate(sqLiteDatabase);
    }
}
