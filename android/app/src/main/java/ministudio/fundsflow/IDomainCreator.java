package ministudio.fundsflow;

import android.database.Cursor;

/**
 * Created by min on 16/1/17.
 */
public interface IDomainCreator<T extends IDomain> {

    Class<T> getDomainClass();

    T create(SQLitePersistence persistence, Cursor cursor);
}
