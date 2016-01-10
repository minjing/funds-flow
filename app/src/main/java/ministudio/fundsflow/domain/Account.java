package ministudio.fundsflow.domain;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ministudio.fundsflow.IPersistenceInitializer;

/**
 * Created by min on 15/12/25.
 */
public class Account {

    public static final IPersistenceInitializer initializer = new AccountInitializer();

    private static final class AccountInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table account (" +
                        "id integer primary key autoincrement, " +
                        "name text not null " +
                        ")";
        private static final String STMT_DROP_TABLE  = "drop table if exist account";

        @Override
        public String[] getCreateStatement() {
            return new String[] { STMT_CREATE_TABLE };
        }

        @Override
        public String[] getUpgradeStatement(int oldVersion, int newVersion) {
            return new String[] { STMT_DROP_TABLE, STMT_CREATE_TABLE };
        }
    }

    private static final String TAB_COL_ID      = "id";
    private static final String TAB_COL_NAME    = "name";

    public static Account getAccount(SQLiteDatabase db, int accountId) {
        if (db == null) {
            throw new IllegalArgumentException("The argument is required - db");
        }
        String stmt = "select id, name from accont where id = ?";
        Cursor cursor = db.rawQuery(stmt, new String[] { String.valueOf(accountId) });
        if (cursor.moveToFirst()) {
            return createAccount(db, cursor);
        } else {
            return null;
        }
    }

    public static boolean isAccountExist(SQLiteDatabase db, String accountName) {
        if (db == null) {
            throw new IllegalArgumentException("The argument is required - db");
        }
        String stmt = "select id, name from account where name = ?";
        Cursor cursor = db.rawQuery(stmt, new String[] { accountName });
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public static Account[] getAccounts(SQLiteDatabase db) {
        if (db == null) {
            throw new IllegalArgumentException("The argument is required - db");
        }
        String stmt = "SELECT id, name FROM account";
        Cursor cursor = db.rawQuery(stmt, new String[0]);
        List<Account> accounts;
        if (cursor.moveToFirst()) {
            accounts = new ArrayList<>();
            do {
                accounts.add(createAccount(db, cursor));
            } while (cursor.moveToNext());
        } else {
            accounts = Collections.emptyList();
        }
        cursor.close();
        db.close();
        return accounts.toArray(new Account[accounts.size()]);
    }

    private static Account createAccount(SQLiteDatabase db, Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(TAB_COL_ID));
        String name = cursor.getString(cursor.getColumnIndex(TAB_COL_NAME));
        return new Account(db, id, name);
    }

    private static final int UNDEFINED_ID   = -1;

    private final SQLiteDatabase _db;

    private int     _id;
    private String  _name = null;

    public Account(SQLiteDatabase db, String name) {
        this(db, UNDEFINED_ID, name);
    }

    public Account(SQLiteDatabase db, int id, String name) {
        if (db == null) {
            throw new IllegalArgumentException("The argument is required - db");
        }
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The argument is required - name");
        }
        this._db = db;
        this._id = id;
        this._name = name;
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

    public void save() {
        if (this._id == UNDEFINED_ID) {
            // create
            String stmt = "insert into account (name) values (?)";
            this._db.execSQL(stmt, new String[] { this._name });
        } else {
            // update
            String stmt  = "update table account (name) values ('?') where id = ?";
            this._db.execSQL(stmt, new Object[] { this._name, this._id });
        }
    }
}
