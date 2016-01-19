package ministudio.fundsflow;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xquan on 1/19/2016.
 */
public final class ViewPagerAdapter extends FragmentPagerAdapter {

    private final Context _ctx;
    private final List<FragmentInfo> _fragmentInfos = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        this._ctx = context;
    }

    @Override
    public Fragment getItem(int position) {
        return this._fragmentInfos.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return this._fragmentInfos.size();
    }

    public void addFragment(Fragment fragment, String resourceKey) {
        int resId = this._ctx.getResources().getIdentifier(resourceKey, "string", this._ctx.getPackageName());
        String title = this._ctx.getString(resId);
        FragmentInfo fragmentInfo = new FragmentInfo(fragment, title);
        this._fragmentInfos.add(fragmentInfo);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this._fragmentInfos.get(position).getTitle();
    }
}
