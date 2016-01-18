package ministudio.fundsflow.domain;

import android.database.Cursor;

import com.google.common.base.Strings;

import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by min on 16/1/17.
 */
public class TagType implements Domain {

    private static final String TAB_NAME    = "tag_type";
    private static final String COL_NAME    = "name";

    public static final long TYPE_ACCOUNT_ID = 1;
    public static final long TYPE_TRADING_ID = 2;

    /*********************
     * Table Initializer *
     *********************/
    public static final IPersistenceInitializer initializer = new TagTypeInitializer();

    private static final class TagTypeInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + " (" +
                        COL_ID + " integer primary key, " +
                        COL_NAME + " text not null " +
                        ")";
        private static final String STMT_ACCOUNT_DATA =
                "insert into " + TAB_NAME + " (" + COL_ID + ", " + COL_NAME + ") values (" + TYPE_ACCOUNT_ID + ", 'Account')";
        private static final String STMT_TRADING_DATA =
                "insert into " + TAB_NAME + " (" + COL_ID + ", " + COL_NAME + ") values (" + TYPE_TRADING_ID + ", 'Trading')";
        private static final String STMT_DROP_TABLE  = "drop table if exist " + TAB_NAME;

        @Override
        public String[] getCreateStatement() {
            return new String[] { STMT_CREATE_TABLE, STMT_ACCOUNT_DATA, STMT_TRADING_DATA };
        }

        @Override
        public String[] getUpgradeStatement(int oldVersion, int newVersion) {
            return new String[] { STMT_DROP_TABLE };
        }
    }

    private static final TagTypeCreator creator = new TagTypeCreator();

    private static final class TagTypeCreator implements IDomainCreator<TagType> {

        @Override
        public Class<TagType> getDomainClass() {
            return TagType.class;
        }

        @Override
        public TagType create(SQLitePersistence persistence, Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            return new TagType(persistence, id, name);
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    public static TagType findById(SQLitePersistence persistence, int id) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        return persistence.findById(TAB_NAME, id, creator);
    }

    public static TagType[] findAll(SQLitePersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        return persistence.findAll(TAB_NAME, creator);
    }

    /*************************************
     * Domain related fields and methods *
     *************************************/
    private final SQLitePersistence _persistence;
    private int _id;
    private String _name;

    public TagType(SQLitePersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        this._persistence = persistence;
    }

    public TagType(SQLitePersistence persistence, int id, String name) {
        this(persistence);
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The argument is required - name");
        }
        this._id = id;
        this._name = name;
    }

    public int getId() {
        return this._id;
    }

    public String getName() {
        return this._name;
    }
}
