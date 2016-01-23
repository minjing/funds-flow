package ministudio.fundsflow.tag;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private ViewPagerAdapter _tagTypeListAdapter;
    private TagCategoryListAdapter _tagCatListAdapter;

    private int _selectedType;

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
                TagActivity.this._selectedType = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        this._tabLayout = (TabLayout) findViewById(R.id.tag_tab);
        this._tabLayout.setupWithViewPager(this._tagViewPager);

        TagCategory[] tagCats = TagCategory.findByType(this._persistence, this._tagTypeListAdapter.getItem(this._selectedType).getId());
        this._tagCatListAdapter = new TagCategoryListAdapter(this, tagCats);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View popupView = getLayoutInflater().inflate(R.layout.popup_tag, null);
                ListView tagCatListView = (ListView) popupView.findViewById(R.id.tag_cat_list);
                tagCatListView.setAdapter(TagActivity.this._tagCatListAdapter);
                tagCatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                        TagActivity.this._tagCatListAdapter.select(pos);
                        TagActivity.this._tagCatListAdapter.notifyDataSetChanged();
                    }
                });

                Button btnCreateCat = (Button) popupView.findViewById(R.id.btn_tag_cat_add);
                btnCreateCat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final LinearLayout layout = (LinearLayout) popupView.findViewById(R.id.tag_cat_add_container);
                        final EditText editTxt = new EditText(TagActivity.this);
                        editTxt.setSingleLine();
                        editTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                        editTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        layout.addView(editTxt, 0, params);

                        editTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);

                                    EditText editTxt = (EditText) v;
                                    String newName = editTxt.getText().toString();
                                    TagCategory tagCat = new TagCategory(TagActivity.this._persistence);
                                    tagCat.setName(newName);
                                    int count = tagCat.save();
                                    if (count == 0) {
                                        Snackbar.make(popupView, "Tat category exists", Snackbar.LENGTH_LONG).show();
                                        return true;
                                    }
                                    TagActivity.this._tagCatListAdapter.update(TagCategory.findAll(TagActivity.this._persistence));
                                    TagActivity.this._tagCatListAdapter.notifyDataSetChanged();

                                    // remove text box
                                    layout.removeView(editTxt);
                                }
                                return false;
                            }
                        });
                    }
                });

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

    private ViewPagerAdapter initTagTypeFragments() {
        this._tagTypeListAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        TagType[] types = TagType.findAll(this._persistence);
        for (TagType type : types) {
            TagFragment tagFragment = new TagFragment();
            tagFragment.setTypeId(type.getId());
            tagFragment.setPersistence(this._persistence);
            tagFragment.setActivity(this);
            this._tagTypeListAdapter.addFragment(tagFragment, type.getResourceKey());
        }
        return this._tagTypeListAdapter;
    }

    private static final class TagCategoryListAdapter extends BaseAdapter {

        private TagCategory[] _tagCats;
        private final Context _ctx;
        private int _selectedPos;

        TagCategoryListAdapter(Context context, TagCategory[] tagCategories) {
            this._ctx = context;
            this._tagCats = tagCategories;
        }

        void update(TagCategory[] tagCategories) {
            this._tagCats = tagCategories;
        }

        void select(int pos) {
            this._selectedPos = pos;
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
                holder._imgTagCat = (ImageView) convertView.findViewById(R.id.item_selection);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder._labTagCatName.setText(this._tagCats[position].getName());
            if (position == this._selectedPos) {
                holder._imgTagCat.setImageResource(R.mipmap.ic_check_circle_black_18dp);
            } else {
                holder._imgTagCat.setImageResource(R.mipmap.ic_check_circle_white_18dp);
            }
            return convertView;
        }
    }

    private static final class Holder {
        private TextView _labTagCatName;
        private ImageView _imgTagCat;
    }
}
