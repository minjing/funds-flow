package ministudio.fundsflow.domain;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by min on 15/12/25.
 */
public class Account implements Domain {

    private static final String TAB_NAME    = "account";
    private static final String COL_NAME    = "name";

    public static final long DEFAULT_ACCOUNT_ID = 0;

    /*********************
     * Table Initializer *
     *********************/
    public static final IPersistenceInitializer initializer = new AccountInitializer();

    private static final class AccountInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + " (" +
                        COL_ID + " integer primary key autoincrement, " +
                        COL_NAME + " text not null " +
                ")";
        private static final String STMT_DEFAULT_DATA =
                "insert into " + TAB_NAME + " (" + COL_ID + ", " + COL_NAME + ") values (" + DEFAULT_ACCOUNT_ID + ", 'Crash')";
        private static final String STMT_DROP_TABLE  = "drop table if exist " + TAB_NAME;

        @Override
        public String[] getCreateStatement() {
            return new String[] { STMT_CREATE_TABLE, STMT_DEFAULT_DATA };
        }

        @Override
        public String[] getUpgradeStatement(int oldVersion, int newVersion) {
            return new String[] { STMT_DROP_TABLE, STMT_CREATE_TABLE };
        }
    }

    private static final AccountCreator creator = new AccountCreator();

    private static final class AccountCreator implements IDomainCreator<Account> {

        @Override
        public Class<Account> getDomainClass() {
            return Account.class;
        }

        @Override
        public Account create(SQLitePersistence persistence, Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            return new Account(persistence, id, name);
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    public static Account findById(SQLitePersistence persistence, int accountId) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - db");
        }
        return persistence.findById(TAB_NAME, accountId, creator);
    }

    public static Account[] getAll(SQLitePersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - db");
        }
        return persistence.findAll(TAB_NAME, creator);
    }

    public static void delete(SQLitePersistence persistence, int accountId) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        persistence.delete(TAB_NAME, accountId);
    }

    public static boolean isAccountExist(SQLitePersistence persistence, String accountName) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        SQLiteDatabase db = persistence.getReadableDatabase();
        String stmt = "select id, name from account where name = ?";
        Cursor cursor = db.rawQuery(stmt, new String[] { accountName });
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    private final SQLitePersistence _persistence;

    private int     _id;
    private String  _name = null;

    public Account(SQLitePersistence persistence, String name) {
        this(persistence, UNDEFINED_ID, name);
    }

    public Account(SQLitePersistence persistence, int id, String name) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - db");
        }
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The argument is required - name");
        }
        this._persistence = persistence;
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
        SQLiteDatabase db = this._persistence.getWritableDatabase();
        if (this._id == UNDEFINED_ID) {
            // create
            String stmt = "insert into account (name) values (?)";
            db.execSQL(stmt, new String[] { this._name });
        } else {
            // update
            String stmt  = "update account set name = ? where id = ?";
            db.execSQL(stmt, new Object[] { this._name, this._id });
        }
    }
}
