package ministudio.fundsflow;

import dagger.Module;
import dagger.Provides;

/**
 * Created by min on 16/2/1.
 */
@Module
public class DomainModule {

    @Provides
    SQLitePersistence getPesistence() {
        return null;
    }
}
