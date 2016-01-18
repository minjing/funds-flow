package ministudio.fundsflow;

import android.database.Cursor;

import ministudio.fundsflow.domain.Domain;

/**
 * Created by min on 16/1/17.
 */
public interface IDomainCreator<T extends Domain> {

    Class<T> getDomainClass();

    T create(SQLitePersistence persistence, Cursor cursor);
}
