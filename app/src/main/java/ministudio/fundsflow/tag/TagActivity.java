package ministudio.fundsflow.tag;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

    private ViewPagerAdapter _tagTypeListAdapter;

    private int _tabPos;

    SQLitePersistence getPersistence() {
        return this._persistence;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this._persistence = new SQLitePersistence(this);

        this._tagViewPager = (ViewPager) findViewById(R.id.tag_viewpager);
        this._tagViewPager.setAdapter(initTagTypeFragments());
        this._tagViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                TagActivity.this._tabPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        this._tabLayout = (TabLayout) findViewById(R.id.tag_tab);
        this._tabLayout.setupWithViewPager(this._tagViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TagEditor().createUI(TagActivity.this,
                        (int) TagActivity.this._tagTypeListAdapter.getItemId(TagActivity.this._tabPos));
            }
        });
    }

    TagFragment getCurrentFragment() {
        return (TagFragment) this._tagTypeListAdapter.getItem(this._tabPos);
    }

    private ViewPagerAdapter initTagTypeFragments() {
        this._tagTypeListAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        TagType[] types = TagType.findAll(this._persistence);
        for (TagType type : types) {
            TagFragment tagFragment = new TagFragment();
            tagFragment.setTypeId(type.getId());
            tagFragment.setPersistence(this._persistence);
            tagFragment.setActivity(this);
            this._tagTypeListAdapter.addFragment(tagFragment, type.getId(), type.getResourceKey());
        }
        return this._tagTypeListAdapter;
    }
}
