package ministudio.fundsflow;

import android.database.Cursor;

import ministudio.fundsflow.domain.Domain;

/**
 * Created by min on 16/1/17.
 */
public interface IDomainCreator<T extends Domain> {

    T create(SQLitePersistence persistence, Cursor cursor);
}
