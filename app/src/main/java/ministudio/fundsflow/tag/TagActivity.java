package ministudio.fundsflow.tag;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

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

    private ListView _tagCatListView;
    private TagCategoryListAdapter _tagCatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this._persistence = new SQLitePersistence(this);

        this._tagViewPager = (ViewPager) findViewById(R.id.tag_viewpager);
        this._tagViewPager.setAdapter(initTagFragments());

        this._tabLayout = (TabLayout) findViewById(R.id.tag_tab);
        this._tabLayout.setupWithViewPager(this._tagViewPager);

        TagCategory[] tagCats = TagCategory.findAll(this._persistence);
        this._tagCatListAdapter = new TagCategoryListAdapter(this, tagCats);

//        this._tagCatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
//                Intent intent = new Intent(getApplicationContext(), AccountAddActivity.class);
//                intent.putExtra(AccountAddActivity.ARG_ACCOUNT_ID, id);
//                startActivityForResult(intent, RESULT_CANCELED);
//            }
//        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = getLayoutInflater().inflate(R.layout.popup_tag, null);
                ListView tagCatListView = (ListView) popupView.findViewById(R.id.tag_cat_list);
                tagCatListView.setAdapter(TagActivity.this._tagCatListAdapter);
//                ViewGroup.LayoutParams params = tagCatListView.getLayoutParams();
//                params.height = getWindowManager().getDefaultDisplay().getHeight() - 30;
//                tagCatListView.setLayoutParams(params);

                PopupWindow popupWin = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                popupWin.setTouchable(true);
                popupWin.setFocusable(true);
                popupWin.setOutsideTouchable(true);
                popupWin.setBackgroundDrawable(new ColorDrawable(0xeeeeee));
                View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                popupWin.showAtLocation(rootView, Gravity.CENTER, 0, 0);
            }
        });
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

    private static final class TagCategoryListAdapter extends BaseAdapter {

        private TagCategory[] _tagCats;
        private final Context _ctx;

        TagCategoryListAdapter(Context context, TagCategory[] tagCategories) {
            this._ctx = context;
            this._tagCats = tagCategories;
        }

        void update(TagCategory[] tagCategories) {
            this._tagCats = tagCategories;
        }

        @Override
        public int getCount() {
            return this._tagCats.length;
        }

        @Override
        public Object getItem(int i) {
            return this._tagCats[i];
        }

        @Override
        public long getItemId(int i) {
            return this._tagCats[i].getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(this._ctx);
                convertView = inflater.inflate(R.layout.tag_cat_list, null);
                holder = new Holder();
                holder._labTagCatName = (TextView) convertView.findViewById(R.id.tag_cat_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder._labTagCatName.setText(this._tagCats[position].getName());
            return convertView;
        }
    }

    private static final class Holder {
        private TextView _labTagCatName;
    }
}
