package ministudio.fundsflow;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.base.Strings;

import ministudio.fundsflow.domain.Account;

/**
 * Created by min on 15/12/25.
 */
public class SQLitePersistence extends SQLiteOpenHelper {

    private static final String dbName  = "fundsflow";
    private static final int dbVersion  = 1;
    private static final IPersistenceInitializer[] persistenceInitializers;

    static {
        persistenceInitializers = new IPersistenceInitializer[] {
                Account.initializer
        };
    }

    public SQLitePersistence(Context ctx) {
        super(ctx, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            for (IPersistenceInitializer initializer : persistenceInitializers) {
                String stmt = initializer.getCreateStatement();
                sqLiteDatabase.execSQL(stmt);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.i("", "Upgrade database from " + oldVersion + " to " + newVersion);
        for (int v = oldVersion; v < newVersion; v++) {
            try {
                for (IPersistenceInitializer initializer : persistenceInitializers) {
                    String stmt = initializer.getUpgradeStatement(v, v + 1);
                    if (Strings.isNullOrEmpty(stmt)) {
                        continue;
                    }
                    sqLiteDatabase.execSQL(stmt);
                }
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
        }
        onCreate(sqLiteDatabase);
    }
}
