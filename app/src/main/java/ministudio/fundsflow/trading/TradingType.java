package ministudio.fundsflow.trading;

import android.database.Cursor;

import ministudio.fundsflow.helper.ArgumentValidator;
import ministudio.fundsflow.helper.DomainHelper;
import ministudio.fundsflow.IDomain;
import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.R;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by xquan on 1/28/2016.
 */
public class TradingType implements IDomain {

    private static final String TAB_NAME        = "trading_type";
    private static final String COL_NAME_KEY    = "name_key";

    public static final int ID_INCOME           = 1;
    public static final int ID_EXPENSE          = 2;
    public static final int ID_TRANSFER         = 3;

    /*********************
     * Table Initializer *
     *********************/
    public static final TradingTypeInitializer initializer = new TradingTypeInitializer();

    private static final class TradingTypeInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + " (" +
                        COL_ID + " integer primary key autoincrement, " +
                        COL_NAME_KEY + " integer not null " +
                        ")";
        private static final String STMT_INCOME_DATA =
                "insert into " + TAB_NAME + " (" + COL_ID + ", " + COL_NAME_KEY + ") values (" + ID_INCOME + ", " + R.string.trading_type_income + ")";
        private static final String STMT_EXPENSE_DATA =
                "insert into " + TAB_NAME + " (" + COL_ID + ", " + COL_NAME_KEY + ") values (" + ID_EXPENSE + ", " + R.string.trading_type_expense + ")";
        private static final String STMT_TRANSFER_DATA =
                "insert into " + TAB_NAME + " (" + COL_ID + ", " + COL_NAME_KEY + ") values (" + ID_TRANSFER + ", " + R.string.trading_type_transfer + ")";
        private static final String STMT_DROP_TABLE = DomainHelper.generateDropTableSql(TAB_NAME);

        @Override
        public String[] getCreateStatement() {
            return new String[] { STMT_CREATE_TABLE, STMT_INCOME_DATA, STMT_EXPENSE_DATA, STMT_TRANSFER_DATA };
        }

        @Override
        public String[] getUpgradeStatement(int oldVersion, int newVersion) {
            return new String[] { STMT_DROP_TABLE };
        }
    }

    private static final TradingTypeCreator creator = new TradingTypeCreator();

    private static final class TradingTypeCreator implements IDomainCreator<TradingType> {

        @Override
        public Class<TradingType> getDomainClass() {
            return TradingType.class;
        }

        @Override
        public TradingType create(SQLitePersistence persistence, Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            int nameKey = cursor.getInt(cursor.getColumnIndex(COL_NAME_KEY));
            return new TradingType(persistence, id, nameKey);
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    public static TradingType findById(SQLitePersistence persistence, int id) {
        return persistence.findById(TAB_NAME, id, creator);
    }

    public static TradingType[] findAll(SQLitePersistence persistence) {
        return persistence.findAll(TAB_NAME, creator);
    }

    /**************************************
     * IDomain related fields and methods *
     **************************************/
    private final SQLitePersistence _persistence;

    private int _id;
    private int _nameKey;

    TradingType(SQLitePersistence persistence, int id, int nameKey) {
        ArgumentValidator.checkNull(persistence, "persistence");
        this._persistence = persistence;
        this._id = id;
        this._nameKey = nameKey;
    }

    public int getId() {
        return this._id;
    }

    public int getNameKey() {
        return this._nameKey;
    }
}
