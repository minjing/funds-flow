package ministudio.fundsflow;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.base.Strings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ministudio.fundsflow.account.Account;
import ministudio.fundsflow.tag.Tag;
import ministudio.fundsflow.tag.TagCategory;
import ministudio.fundsflow.tag.TagType;
import ministudio.fundsflow.trading.Trading;
import ministudio.fundsflow.trading.TradingTag;
import ministudio.fundsflow.trading.TradingType;

/**
 * Created by min on 15/12/25.
 */
public class SQLitePersistence extends SQLiteOpenHelper {

    private static final String dbName  = "fundsflow.db";
    private static final int dbVersion  = 1;
    private static final IPersistenceInitializer[] persistenceInitializers =
            new IPersistenceInitializer[] {
                Account.initializer,
                TagType.initializer,
                TagCategory.initializer,
                Tag.initializer,
                TradingType.initializer,
                Trading.initializer,
                TradingTag.initializer
            };

    private SQLiteDatabase _readableDb;
    private SQLiteDatabase _writableDb;

    public SQLitePersistence(Context ctx) {
        super(ctx, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            for (IPersistenceInitializer initializer : persistenceInitializers) {
                String[] stmts = initializer.getCreateStatement();
                for (String stmt : stmts) {
                    sqLiteDatabase.execSQL(stmt);
                }
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
                    String[] stmts = initializer.getUpgradeStatement(v, v + 1);
                    if (stmts == null || stmts.length == 0) {
                        continue;
                    }
                    for (String stmt : stmts) {
                        sqLiteDatabase.execSQL(stmt);
                    }
                }
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
        }
        onCreate(sqLiteDatabase);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (this._readableDb == null) {
            this._readableDb = super.getReadableDatabase();
        }
        return this._readableDb;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (this._writableDb == null) {
            this._writableDb = super.getWritableDatabase();
        }
        return this._writableDb;
    }

    public void close() {
        if (this._readableDb != null) {
            this._readableDb.close();
        }
        this._readableDb = null;
        if (this._writableDb != null) {
            this._writableDb.close();
        }
        this._writableDb = null;
    }

    public <T extends IDomain> T findById(String tableName, int id, IDomainCreator<T> creator) {
        if (Strings.isNullOrEmpty(tableName)) {
            throw new IllegalArgumentException("The argument is required - tableName");
        }
        if (creator == null) {
            throw new IllegalArgumentException("The argument is required - creator");
        }
        SQLiteDatabase db = getReadableDatabase();
        String stmt = "select * from " + tableName + " where id = ?";
        Cursor cursor = db.rawQuery(stmt, new String[] { String.valueOf(id) });
        T domain = null;
        if (cursor.moveToFirst()) {
            domain = creator.create(this, cursor);
        }
        cursor.close();
        return domain;
    }

    public <T extends IDomain> T[] findAll(String tableName, IDomainCreator<T> creator) {
        if (Strings.isNullOrEmpty(tableName)) {
            throw new IllegalArgumentException("The argument is required - tableName");
        }
        if (creator == null) {
            throw new IllegalArgumentException("The argument is required - creator");
        }
        SQLiteDatabase db = getReadableDatabase();
        String stmt = "select * from " + tableName;
        Cursor cursor = null;
        List<T> domains;
        try {
            cursor = db.rawQuery(stmt, new String[0]);
            if (cursor.moveToFirst()) {
                domains = new ArrayList<>();
                do {
                    domains.add(creator.create(this, cursor));
                } while (cursor.moveToNext());
            } else {
                domains = Collections.emptyList();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return domains.toArray((T[]) Array.newInstance(creator.getDomainClass(), domains.size()));
    }

    public void delete(String tableName, int id) {
        SQLiteDatabase db = getWritableDatabase();
        String stmt = "delete from " + tableName + " where " + IDomain.COL_ID + " = ?";
        db.execSQL(stmt, new Object[]{id});
    }
}
