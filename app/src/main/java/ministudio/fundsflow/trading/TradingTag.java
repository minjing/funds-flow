package ministudio.fundsflow.trading;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ministudio.fundsflow.helper.ArgumentValidator;
import ministudio.fundsflow.helper.DomainHelper;
import ministudio.fundsflow.IDomain;
import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;
import ministudio.fundsflow.tag.Tag;

/**
 * Created by xquan on 1/28/2016.
 */
public class TradingTag implements IDomain {

    private static final String TAB_NAME        = "trading_tag";
    private static final String COL_TRADING_ID  = "trading_id";
    private static final String COL_TAG_ID      = "tag_id";

    /*********************
     * Table Initializer *
     *********************/
    public static final TradingTagInitializer initializer = new TradingTagInitializer();

    private static final class TradingTagInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + "(" +
                        COL_TRADING_ID + " integer not null, " +
                        COL_TAG_ID + " integer not null" +
                        ")";
        private static final String STMT_DROP_TABLE = DomainHelper.generateDropTableSql(TAB_NAME);

        @Override
        public String[] getCreateStatement() {
            return new String[] { STMT_CREATE_TABLE };
        }

        @Override
        public String[] getUpgradeStatement(int oldVersion, int newVersion) {
            return new String[] { STMT_DROP_TABLE };
        }
    }

    /******************
     * Domain Creator *
     ******************/
    private static final TradingTagCreator creator = new TradingTagCreator();

    private static final class TradingTagCreator implements IDomainCreator<TradingTag> {

        @Override
        public Class getDomainClass() {
            return TradingTag.class;
        }

        @Override
        public TradingTag create(SQLitePersistence persistence, Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            int tradingId = cursor.getInt(cursor.getColumnIndex(COL_TRADING_ID));
            int tagId = cursor.getInt(cursor.getColumnIndex(COL_TAG_ID));
            return new TradingTag(persistence, id, tradingId, tagId);
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    public static TradingTag findById(SQLitePersistence persistence, int id) {
        ArgumentValidator.checkNull(persistence, "persistence");
        return persistence.findById(TAB_NAME, id, creator);
    }

    public static TradingTag[] findAll(SQLitePersistence persistence) {
        ArgumentValidator.checkNull(persistence, "persistence");
        return persistence.findAll(TAB_NAME, creator);
    }

    public static Tag[] findTags(SQLitePersistence persistence, int tradingId) {
        ArgumentValidator.checkNull(persistence, "persistence");
        String stmt = "select tag." + Tag.COL_ID + ", tag." + Tag.COL_CAT_ID + ", tag." + Tag.COL_TYPE_ID + ", tag." + Tag.COL_NAME +
                " from " + Tag.TAB_NAME + " tag, " + TAB_NAME + " ttag " +
                "where tag." + Tag.COL_ID + " = " + "ttag." + COL_TAG_ID + " and " +
                    "ttag." + COL_TRADING_ID + " = ?";
        SQLiteDatabase db = persistence.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(stmt, new String[] { String.valueOf(tradingId) });
            return DomainHelper.createDomains(persistence, cursor, Tag.creator);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**************************************
     * IDomain related fields and methods *
     **************************************/
    private final SQLitePersistence _persistence;

    private int     _id;
    private int     _tradingId;
    private int     _tagId;

    private TradingTag(SQLitePersistence persistence, int id, int tradingId, int tagId) {
        ArgumentValidator.checkNull(persistence, "persistence");
        this._persistence = persistence;
        this._id = id;
        this._tradingId = tradingId;
        this._tagId = tagId;
    }

    public int getId() {
        return this._id;
    }

    public Trading getTrading() {
        Trading trading = Trading.findById(this._persistence, this._tradingId);
        if (trading == null) {
            throw new IllegalStateException("The trading id does not associated with any trading recording - " + this._tradingId);
        }
        return trading;
    }

    public Tag getTag() {
        Tag tag = Tag.findById(this._persistence, this._tagId);
        if (tag == null) {
            throw new IllegalStateException("The tag id does not associated with any tag recording - " + this._tagId);
        }
        return tag;
    }
}
