package ministudio.fundsflow.domain;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Strings;

import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by min on 15/12/25.
 */
public class Tag implements Domain {

    private static final String TAB_NAME    = "tag";
    private static final String COL_TYPE_ID = "type_id";
    private static final String COL_NAME    = "name";

    /*********************
     * Table Initializer *
     *********************/
    private static final class TagInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + "(" +
                        COL_ID + " integer primary key autoincrement, " +
                        COL_TYPE_ID + " integer no null " +
                        COL_NAME + " text not null " +
                        ")";
        private static final String STMT_DROP_TABLE = "drop table if exist " + TAB_NAME;

        @Override
        public String[] getCreateStatement() {
            return new String[] { STMT_CREATE_TABLE };
        }

        @Override
        public String[] getUpgradeStatement(int oldVersion, int newVersion) {
            return new String[] { STMT_DROP_TABLE };
        }
    }

    private static final TagCreator creator = new TagCreator();

    private static final class TagCreator implements IDomainCreator<Tag> {

        @Override
        public Tag create(SQLitePersistence persistence, Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            int catId = cursor.getInt(cursor.getColumnIndex(COL_TYPE_ID));
            return new Tag(persistence, id, catId, name);
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    public static Tag findById(SQLitePersistence persistence, int id) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        return persistence.findById(TAB_NAME, id, creator);
    }

    public static Tag[] findAll(SQLitePersistence persistence) {
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
    private int _typeId;
    private int _catId;
    private String _name;

    public Tag(SQLitePersistence persistence, String name) {
        this(persistence, UNDEFINED_ID, UNDEFINED_ID, UNDEFINED_ID, name);
    }

    public Tag(SQLitePersistence persistence, int id, int typeId, int catId, String name) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        this._persistence = persistence;
        this._id = id;
        this._typeId = typeId;
        this._catId = catId;
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

    public TagType getType() {
        if (this._typeId == UNDEFINED_ID) {
            return null;
        } else {
            return TagType.findById(this._persistence, this._typeId);
        }
    }

    public void setType(TagType type) {
        if (type == null) {
            this._typeId = UNDEFINED_ID;
        } else {
            this._typeId = type.getId();
        }
    }

    public TagCategory getCategory() {
        if (this._catId == UNDEFINED_ID) {
            return null;
        } else {
            return TagCategory.findById(this._persistence, this._catId);
        }
    }

    public void setCategory(TagCategory category) {
        if (category == null) {
            this._catId = UNDEFINED_ID;
        } else {
            this._catId = category.getId();
        }
    }

    public void save() {
        SQLiteDatabase db = this._persistence.getWritableDatabase();
        if (this._id == UNDEFINED_ID) {
            // create
            String stmt = "insert into " + TAB_NAME + " (" + COL_TYPE_ID + ", " + COL_NAME + ") values (?, ?)";
            db.execSQL(stmt, new String[] { String.valueOf(this._typeId), this._name });
        } else {
            // update
            String stmt  = "update " + TAB_NAME + " set " + COL_TYPE_ID + " = ?, " + COL_NAME + " = ? where id = ?";
            db.execSQL(stmt, new Object[] { String.valueOf(this._typeId), this._name, this._id });
        }
    }
}
