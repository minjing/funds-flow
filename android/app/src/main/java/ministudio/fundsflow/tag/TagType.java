package ministudio.fundsflow.tag;

import android.database.Cursor;

import com.google.common.base.Strings;

import ministudio.fundsflow.IDomain;
import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by min on 16/1/17.
 */
public class TagType implements IDomain {

    private static final String TAB_NAME    = "tag_type";
    private static final String COL_RES_KEY = "res_key";

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
                        COL_RES_KEY + " text not null " +
                        ")";
        private static final String STMT_ACCOUNT_DATA =
                "insert into " + TAB_NAME + " (" + COL_ID + ", " + COL_RES_KEY + ") values (" + TYPE_ACCOUNT_ID + ", 'title_tag_type_account')";
        private static final String STMT_TRADING_DATA =
                "insert into " + TAB_NAME + " (" + COL_ID + ", " + COL_RES_KEY + ") values (" + TYPE_TRADING_ID + ", 'title_tag_type_trading')";
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
            String resKey = cursor.getString(cursor.getColumnIndex(COL_RES_KEY));
            return new TagType(persistence, id, resKey);
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
     * IDomain related fields and methods *
     *************************************/
    private final SQLitePersistence _persistence;
    private int _id;
    private String _resKey;

    public TagType(SQLitePersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        this._persistence = persistence;
    }

    public TagType(SQLitePersistence persistence, int id, String resKey) {
        this(persistence);
        if (Strings.isNullOrEmpty(resKey)) {
            throw new IllegalArgumentException("The argument is required - resKey");
        }
        this._id = id;
        this._resKey = resKey;
    }

    public int getId() {
        return this._id;
    }

    public String getResourceKey() {
        return this._resKey;
    }
}
