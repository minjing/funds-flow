package ministudio.fundsflow.tag;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import ministudio.fundsflow.R;
import ministudio.fundsflow.SQLitePersistence;
import ministudio.fundsflow.ViewPagerAdapter;

/**
 * Created by xquan on 1/19/2016.
 */
public class TagActivity extends AppCompatActivity {

    private TabLayout _tabLayout;
    private ViewPager _tagViewPager;

    private SQLitePersistence _persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = getLayoutInflater().inflate(R.layout.popup_tag, null);
                PopupWindow popupWin = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWin.setTouchable(true);
                popupWin.setFocusable(true);
                popupWin.setOutsideTouchable(true);
                popupWin.setBackgroundDrawable(new ColorDrawable(0xeeeeee));
                View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                popupWin.showAtLocation(rootView, Gravity.CENTER, 0, 0);
            }
        });

        this._persistence = new SQLitePersistence(this);

        this._tagViewPager = (ViewPager) findViewById(R.id.tag_viewpager);
        this._tagViewPager.setAdapter(initTagFragments());

        this._tabLayout = (TabLayout) findViewById(R.id.tag_tab);
        this._tabLayout.setupWithViewPager(this._tagViewPager);
    }

    private ViewPagerAdapter initTagFragments() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        TagType[] types = TagType.findAll(this._persistence);
        for (TagType type : types) {
            TagFragment tagFragment = new TagFragment();
            tagFragment.setTypeId(type.getId());
            tagFragment.setPersistence(this._persistence);
            tagFragment.setActivity(this);
            adapter.addFragment(tagFragment, type.getResourceKey());
        }
        return adapter;
    }
}
