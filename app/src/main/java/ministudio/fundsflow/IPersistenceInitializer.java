package ministudio.fundsflow;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by min on 15/12/28.
 */
public interface IPersistenceInitializer {

    String[] getCreateStatement();

    String[] getUpgradeStatement(int oldVersion, int newVersion);
}
