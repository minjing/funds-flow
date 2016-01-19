package ministudio.fundsflow;

import android.support.v4.app.Fragment;

import com.google.common.base.Strings;

/**
 * Created by xquan on 1/19/2016.
 */
public final class FragmentInfo {

    private final Fragment _fragment;
    private final String _title;

    public FragmentInfo(Fragment fragment, String title) {
        if (fragment == null) {
            throw new IllegalArgumentException("The argument is required - fragment");
        }
        if (Strings.isNullOrEmpty(title)) {
            throw new IllegalArgumentException("The argument is required - title");
        }

        this._fragment = fragment;
        this._title = title;
    }

    public Fragment getFragment() {
        return this._fragment;
    }

    public String getTitle() {
        return this._title;
    }
}
