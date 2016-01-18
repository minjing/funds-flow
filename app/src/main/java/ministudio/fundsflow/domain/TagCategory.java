package ministudio.fundsflow.domain;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Strings;

import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by min on 16/1/18.
 */
public class TagCategory implements Domain {

    private static final String TAB_NAME    = "tag_category";
    private static final String COL_NAME    = "name";

    /************************
     * DB Table Initializer *
     ************************/
    public static final TagCategoryInitializer initializer = new TagCategoryInitializer();

    private static final class TagCategoryInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + " (" +
                    COL_ID + " integer primary key, " +
                    COL_NAME + " text not null " +
                ")";
        private static final String STMT_DROP_TABLE  = "drop table if exist " + TAB_NAME;

        private TagCategoryInitializer() { }

        @Override
        public String[] getCreateStatement() {
            return new String[] { STMT_CREATE_TABLE };
        }

        @Override
        public String[] getUpgradeStatement(int oldVersion, int newVersion) {
            return new String[] { STMT_DROP_TABLE };
        }
    }

    private static final TagCategoryCreator creator = new TagCategoryCreator();

    private static final class TagCategoryCreator implements IDomainCreator<TagCategory> {

        @Override
        public Class<TagCategory> getDomainClass() {
            return TagCategory.class;
        }

        @Override
        public TagCategory create(SQLitePersistence persistence, Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            return new TagCategory(persistence, id, name);
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    public static TagCategory findById(SQLitePersistence persistence, int id) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        return persistence.findById(TAB_NAME, id, creator);
    }

    public static TagCategory[] findAll(SQLitePersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        return persistence.findAll(TAB_NAME, creator);
    }

    public static void delete(SQLitePersistence persistence, int id) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        persistence.delete(TAB_NAME, id);
    }

    /*************************************
     * Domain related fields and methods *
     *************************************/
    private final SQLitePersistence _persistence;
    private int _id;
    private String _name;

    public TagCategory(SQLitePersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        this._persistence = persistence;
    }

    public TagCategory(SQLitePersistence persistence, int id, String name) {
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

    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The argument is required - name");
        }
        this._name = name;
    }

    public void save() {
        SQLiteDatabase db = this._persistence.getWritableDatabase();
        if (this._id == UNDEFINED_ID) {
            // create
            String stmt = "insert into " + TAB_NAME + " (" + COL_NAME + ") values (?)";
            db.execSQL(stmt, new String[] { this._name });
        } else {
            // update
            String stmt  = "update " + TAB_NAME + " set " + COL_NAME + " = ? where id = ?";
            db.execSQL(stmt, new Object[] { this._name, this._id });
        }
    }
}
