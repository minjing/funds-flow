package ministudio.fundsflow.domain;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by min on 15/12/25.
 */
public class Account {

    public static final IPersistenceInitializer initializer
            = new AccountInitializer();

    private static final class AccountInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table account (" +
                        "id integer primary key autoincrement, " +
                        "name text not null " +
                        ")";
        private static final String STMT_DROP_TABLE  = "drop table if exist account";

        @Override
        public String getCreateStatement() {
            return STMT_CREATE_TABLE;
        }

        @Override
        public String getUpgradeStatement(int oldVersion, int newVersion) {
            return null;
        }
    }

    private static final String TAB_COL_ID      = "id";
    private static final String TAB_COL_NAME    = "name";

    public static Account getAccount(SQLitePersistence persistence, int accountId) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        String stmt = "SELECT id, name FROM accont WHERE id = ?";
        SQLiteDatabase db = persistence.getReadableDatabase();
        Cursor cursor = db.rawQuery(stmt, new String[] { String.valueOf(accountId) });
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(TAB_COL_ID));
            String name = cursor.getString(cursor.getColumnIndex(TAB_COL_NAME));
            return new Account(persistence, id, name);
        } else {
            return null;
        }
    }

    public static List<Account> getAccounts(SQLitePersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        String stmt = "SELECT id, name FROM account";
        SQLiteDatabase db = persistence.getReadableDatabase();
        Cursor cursor = db.rawQuery(stmt, new String[0]);
        List<Account> accounts;
        if (cursor.moveToFirst()) {
            accounts = new ArrayList<>();
            do {
                int id = cursor.getInt(cursor.getColumnIndex(TAB_COL_ID));
                String name = cursor.getString(cursor.getColumnIndex(TAB_COL_NAME));
                accounts.add(new Account(persistence, id, name));
            } while (cursor.moveToNext());
        } else {
            accounts = Collections.emptyList();
        }
        cursor.close();
        db.close();
        return accounts;
    }

    private final SQLitePersistence _persistence;

    private int     _id;
    private String  _name = null;
    private float   _balance = 0;

    public Account(SQLitePersistence persistence, int id, String name) {
        this(persistence, id, name, 0);
    }

    public Account(SQLitePersistence persistence, int id, String name, float balance) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The argument is required - name");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("The argument must be more than 0 - balance");
        }
        this._persistence = persistence;
        this._id = id;
        this._name = name;
        this._balance = balance;
    }

    public int getId() {
        return this._id;
    }

    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The argument is required - name");
        }
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public float getBalance() {
        return this._balance;
    }

    public void save() {
//        String stmt = "";
//        SQLiteDatabase db = this._persistence.getWritableDatabase();

    }
}
