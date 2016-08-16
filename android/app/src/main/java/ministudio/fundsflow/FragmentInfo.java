package ministudio.fundsflow;

import android.support.v4.app.Fragment;

import com.google.common.base.Strings;

/**
 * Created by xquan on 1/19/2016.
 */
public final class FragmentInfo {

    private final Fragment _fragment;
    private final String _title;
    private final int _id;

    public FragmentInfo(Fragment fragment, int id, String title) {
        if (fragment == null) {
            throw new IllegalArgumentException("The argument is required - fragment");
        }
        if (Strings.isNullOrEmpty(title)) {
            throw new IllegalArgumentException("The argument is required - title");
        }

        this._fragment = fragment;
        this._id = id;
        this._title = title;
    }

    public Fragment getFragment() {
        return this._fragment;
    }

    public int getId() {
        return this._id;
    }

    public String getTitle() {
        return this._title;
    }
}
