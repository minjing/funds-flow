package ministudio.fundsflow.tag;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Strings;

import ministudio.fundsflow.helper.ArgumentValidator;
import ministudio.fundsflow.helper.DomainHelper;
import ministudio.fundsflow.IDomain;
import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by min on 15/12/25.
 */
public class Tag implements IDomain {

    public static final String TAB_NAME     = "tag";
    public static final String COL_TYPE_ID  = "type_id";
    public static final String COL_CAT_ID   = "cat_id";
    public static final String COL_NAME     = "name";

    /*********************
     * Table Initializer *
     *********************/
    public static final TagInitializer initializer = new TagInitializer();

    private static final class TagInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + "(" +
                        COL_ID + " integer primary key autoincrement, " +
                        COL_TYPE_ID + " integer not null, " +
                        COL_CAT_ID + " integer not null, " +
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

    public static final TagCreator creator = new TagCreator();

    private static final class TagCreator implements IDomainCreator<Tag> {

        @Override
        public Class<Tag> getDomainClass() {
            return Tag.class;
        }

        @Override
        public Tag create(SQLitePersistence persistence, Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            int typeId = cursor.getInt(cursor.getColumnIndex(COL_TYPE_ID));
            int catId = cursor.getInt(cursor.getColumnIndex(COL_CAT_ID));
            return new Tag(persistence, id, typeId, catId, name);
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

    public static Tag[] findByType(SQLitePersistence persistence, int typeId) {
        ArgumentValidator.checkNull(persistence, "persistence");

        SQLiteDatabase db = persistence.getReadableDatabase();
        String stmt = "select * from " + TAB_NAME + " where " + COL_TYPE_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(stmt, new String[] { String.valueOf(typeId) });
            return DomainHelper.createDomains(persistence, cursor, creator);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Tag find(SQLitePersistence persistence, int typeId, String name) {
        ArgumentValidator.checkNull(persistence, "persistence");

        SQLiteDatabase db = persistence.getReadableDatabase();
        String stmt = "select * from " + TAB_NAME + " where " + COL_TYPE_ID + " = ? and " + COL_NAME + " = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(stmt, new String[] { String.valueOf(typeId), name });
            if (cursor.moveToFirst()) {
                return creator.create(persistence, cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void delete(SQLitePersistence persistence, int id) {
        if (persistence == null) {
            throw new IllegalArgumentException("The argument is required - persistence");
        }
        persistence.delete(TAB_NAME, id);
    }

    /*************************************
     * IDomain related fields and methods *
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

    public void setType(int typeId) {
        this._typeId = typeId;
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
            String stmt = "insert into " + TAB_NAME + " (" + COL_TYPE_ID + ", " + COL_CAT_ID + ", " + COL_NAME + ") values (?, ?, ?)";
            db.execSQL(stmt, new Object[] { this._typeId, this._catId, this._name });
        } else {
            // update
            String stmt  = "update " + TAB_NAME + " set " +
                    COL_TYPE_ID + " = ?, " +
                    COL_CAT_ID + " = ?, " +
                    COL_NAME + " = ? where id = ?";
            db.execSQL(stmt, new Object[] { this._typeId, this._catId, this._name, this._id });
        }
    }
}
