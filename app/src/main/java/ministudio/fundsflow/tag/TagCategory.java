package ministudio.fundsflow.tag;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Strings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ministudio.fundsflow.ArgumentValidator;
import ministudio.fundsflow.DomainHelper;
import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.IPersistenceInitializer;
import ministudio.fundsflow.SQLitePersistence;
import ministudio.fundsflow.domain.Domain;

/**
 * Created by min on 16/1/18.
 */
public class TagCategory implements Domain {

    private static final String TAB_NAME    = "tag_category";
    private static final String COL_NAME    = "name";
    private static final String COL_TYPE_ID = "type_id";

    /************************
     * DB Table Initializer *
     ************************/
    public static final TagCategoryInitializer initializer = new TagCategoryInitializer();

    private static final class TagCategoryInitializer implements IPersistenceInitializer {

        private static final String STMT_CREATE_TABLE =
                "create table " + TAB_NAME + " (" +
                    COL_ID + " integer primary key, " +
                    COL_NAME + " text not null, " +
                    COL_TYPE_ID + " integer not null " +
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
            int typeId = cursor.getInt(cursor.getColumnIndex(COL_TYPE_ID));
            return new TagCategory(persistence, id, typeId, name);
        }
    }

    /*********************************************************
     * Static methods for finding and deleting functionality *
     *********************************************************/
    public static TagCategory findById(SQLitePersistence persistence, int id) {
        ArgumentValidator.checkNull(persistence, "persistence");
        return persistence.findById(TAB_NAME, id, creator);
    }

    public static TagCategory[] findByType(SQLitePersistence persistence, int typeId) {
        ArgumentValidator.checkNull(persistence, "persistence");

        SQLiteDatabase db = persistence.getReadableDatabase();
        Cursor cursor = null;
        String stmt = "select * from " + TAB_NAME + " where " + COL_TYPE_ID + " = ?";
        try {
            cursor = db.rawQuery(stmt, new String[] { String.valueOf(typeId) });
            return DomainHelper.createDomains(persistence, cursor, creator);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static TagCategory findByName(SQLitePersistence persistence, String name) {
        ArgumentValidator.checkNull(persistence, "persistence");
        ArgumentValidator.checkBlank(name, "name");

        SQLiteDatabase db = persistence.getReadableDatabase();
        Cursor cursor = null;
        String stmt = "select * from " + TAB_NAME + " where " + COL_NAME + " = ?";
        try {
            cursor = db.rawQuery(stmt, new String[] { name });
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

    public static TagCategory[] findAll(SQLitePersistence persistence) {
        ArgumentValidator.checkNull(persistence, "persistence");
        return persistence.findAll(TAB_NAME, creator);
    }

    public static void delete(SQLitePersistence persistence, int id) {
        ArgumentValidator.checkNull(persistence, "persistence");
        persistence.delete(TAB_NAME, id);
    }

    /*************************************
     * Domain related fields and methods *
     *************************************/
    private final SQLitePersistence _persistence;
    private int _id = UNDEFINED_ID;
    private String _name;
    private int _typeId;

    public TagCategory(SQLitePersistence persistence) {
        ArgumentValidator.checkNull(persistence, "persistence");
        this._persistence = persistence;
    }

    private TagCategory(SQLitePersistence persistence, int id, int typeId, String name) {
        this(persistence);

        ArgumentValidator.checkBlank(name, "name");

        this._id = id;
        this._typeId = typeId;
        this._name = name;
    }

    public int getId() {
        return this._id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        ArgumentValidator.checkBlank(name, "name");
        this._name = name;
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

    public int save() {
        if (findByName(this._persistence, this._name) != null) {
            return 0;
        }
        SQLiteDatabase db = this._persistence.getWritableDatabase();
        if (this._id == UNDEFINED_ID) {
            // create
            String stmt = "insert into " + TAB_NAME + " (" + COL_TYPE_ID + ", " + COL_NAME + ") values (?, ?)";
            db.execSQL(stmt, new Object[] { this._typeId, this._name });
        } else {
            // update
            String stmt  = "update " + TAB_NAME + " set " +
                    COL_TYPE_ID + " = ?, " +
                    COL_NAME + " = ? where id = ?";
            db.execSQL(stmt, new Object[] { this._typeId, this._name, this._id });
        }
        return 1;
    }
}
