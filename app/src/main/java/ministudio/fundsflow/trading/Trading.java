package ministudio.fundsflow.trading;

import android.database.Cursor;

import ministudio.fundsflow.ArgumentValidator;
import ministudio.fundsflow.DomainHelper;
import ministudio.fundsflow.IDomain;
import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;
import ministudio.fundsflow.account.Account;
import ministudio.fundsflow.tag.Tag;

/**
 * Created by xquan on 1/28/2016.
 */
public class Trading implements IDomain {

    private static final String TAB_NAME            = "trading";
    private static final String COL_TIME            = "time";
    private static final String COL_TYPE_ID         = "type_id";
    private static final String COL_IN_ACCOUNT_ID   = "in_account_id";
    private static final String COL_OUT_ACCOUNT_ID  = "out_account_id";
    private static final String COL_AMOUNT          = "amount";

    /*********************
     * Table Initializer *
     *********************/
    public static final TradingTagInitializer initializer = new TradingTagInitializer();

    private static final class TradingTagInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + "(" +
                        COL_ID + " integer primary key autoincrement, " +
                        COL_TIME + " long not null, " +
                        COL_TYPE_ID + " integer not null, " +
                        COL_IN_ACCOUNT_ID + " integer not null, " +
                        COL_OUT_ACCOUNT_ID + " integer not null, " +
                        COL_AMOUNT + " float not null" +
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

    private static final class TradingTagCreator implements IDomainCreator<Trading> {

        @Override
        public Class getDomainClass() {
            return TradingTag.class;
        }

        @Override
        public Trading create(SQLitePersistence persistence, Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            long time = cursor.getLong(cursor.getColumnIndex(COL_TIME));
            int typeId = cursor.getInt(cursor.getColumnIndex(COL_TYPE_ID));
            int inAccountId = cursor.getInt(cursor.getColumnIndex(COL_IN_ACCOUNT_ID));
            int outAccountId = cursor.getInt(cursor.getColumnIndex(COL_OUT_ACCOUNT_ID));
            float amount = cursor.getFloat(cursor.getColumnIndex(COL_AMOUNT));
            return new Trading(persistence, id, time, typeId, inAccountId, outAccountId, amount);
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    public static Trading findById(SQLitePersistence persistence, int id) {
        ArgumentValidator.checkNull(persistence, "persistence");
        return persistence.findById(TAB_NAME, id, creator);
    }

    public static Trading[] findAll(SQLitePersistence persistence) {
        ArgumentValidator.checkNull(persistence, "persistence");
        return persistence.findAll(TAB_NAME, creator);
    }

    /**************************************
     * IDomain related fields and methods *
     **************************************/
    private final SQLitePersistence _persistence;

    private int     _id;
    private long    _time;
    private int     _typeId;
    private int     _inAccountId;
    private int     _outAccountId;
    private float   _amount;

    private Trading(SQLitePersistence persistence, int id, long time, int typeId, int inAccountId, int outAccountId, float amount) {
        ArgumentValidator.checkNull(persistence, "persistence");
        this._persistence = persistence;
        this._id = id;
        this._time = time;
        this._typeId = typeId;
        this._inAccountId = inAccountId;
        this._outAccountId = outAccountId;
        this._amount = amount;
    }

    public int getId() {
        return this._id;
    }

    public long getTime() {
        return this._time;
    }

    public void setTime(long time) {
        this._time = time;
    }

    public TradingType getType() {
        return TradingType.findById(this._persistence, this._typeId);
    }

    public Account getInAccount() {
        if (this._inAccountId == IDomain.UNDEFINED_ID) {
            return null;
        } else {
            return Account.findById(this._persistence, this._inAccountId);
        }
    }

    public Account getOutAccount() {
        if (this._outAccountId == IDomain.UNDEFINED_ID) {
            return null;
        } else {
            return Account.findById(this._persistence, this._outAccountId);
        }
    }

    public float getAmount() {
        return this._amount;
    }

    public Tag[] getTags() {
        return TradingTag.findTags(this._persistence, this._id);
    }

    public void income(Account account, float amount) {
        ArgumentValidator.checkNull(account, "account");
        this._typeId = TradingType.ID_INCOME;
        this._inAccountId = account.getId();
        this._outAccountId = IDomain.UNDEFINED_ID;
        this._amount = amount;
    }

    public void expense(Account account, float amount) {
        ArgumentValidator.checkNull(account, "account");
        this._typeId = TradingType.ID_EXPENSE;
        this._inAccountId = IDomain.UNDEFINED_ID;
        this._outAccountId = account.getId();
        this._amount = amount;
    }

    public void transfer(Account inAccount, Account outAccount, float amount) {
        ArgumentValidator.checkNull(inAccount, "inAccount");
        ArgumentValidator.checkNull(outAccount, "outAccount");
        this._typeId = TradingType.ID_TRANSFER;
        this._inAccountId = inAccount.getId();
        this._outAccountId = outAccount.getId();
        this._amount = amount;
    }
}
