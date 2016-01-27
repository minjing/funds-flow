package ministudio.fundsflow;

import android.database.Cursor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by min on 16/1/23.
 */
public final class DomainHelper {

    private DomainHelper() { }

    public static <T extends Domain> T[] createDomains(SQLitePersistence persistence, Cursor cursor, IDomainCreator<T> creator) {
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
