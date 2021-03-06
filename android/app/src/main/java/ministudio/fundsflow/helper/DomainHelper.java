package ministudio.fundsflow.helper;

import android.database.Cursor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ministudio.fundsflow.IDomain;
import ministudio.fundsflow.IDomainCreator;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by min on 16/1/23.
 */
public final class DomainHelper {

    private DomainHelper() { }

    public static String generateDropTableSql(String tableName) {
        return "drop table if exist " + tableName;
    }

    public static <T extends IDomain> T[] createDomains(SQLitePersistence persistence, Cursor cursor, IDomainCreator<T> creator) {
        List<T> tagCats;
        if (cursor.moveToFirst()) {
            tagCats = new ArrayList<>();
            do {
                tagCats.add(creator.create(persistence, cursor));
            } while (cursor.moveToNext());
        } else {
            tagCats = Collections.EMPTY_LIST;
        }
        return tagCats.toArray((T[]) Array.newInstance(creator.getDomainClass(), tagCats.size()));
    }
}
