package ministudio.fundsflow.tag;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ministudio.fundsflow.R;
import ministudio.fundsflow.SQLitePersistence;

/**
 * Created by xquan on 1/19/2016.
 */
public class TagFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SQLitePersistence _persistence;
    private TagActivity _activity;
    private int _tagTypeId;

    private SwipeRefreshLayout _tagListLayout;
    private ListView _tagListView;
    private TagAdapter _tagAdapter;

    void setActivity(TagActivity activity) {
        this._activity = activity;
    }

    void setPersistence(SQLitePersistence persistence) {
        this._persistence = persistence;
    }

    void setTypeId(int tagTypeId) {
        this._tagTypeId = tagTypeId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tag, container, false);

        this._tagListView = (ListView) rootView.findViewById(R.id.tag_list);
        this._tagListLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.tag_layout);
        this._tagListLayout.setOnRefreshListener(this);

        Tag[] tags = Tag.findByType(this._persistence, this._tagTypeId);
        this._tagAdapter = new TagAdapter(this._activity, tags);
        this._tagListView.setAdapter(this._tagAdapter);
        this._tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Tag tag = (Tag) TagFragment.this._tagAdapter.getItem(pos);
                new TagEditor().createUI(TagFragment.this._activity, TagFragment.this._tagTypeId, tag);
            }
        });

        return rootView;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                TagFragment.this._tagListLayout.setRefreshing(false);
                TagFragment.this._tagAdapter.update(Tag.findByType(TagFragment.this._persistence, TagFragment.this._tagTypeId));
                TagFragment.this._tagAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        this._tagAdapter.update(Tag.findByType(TagFragment.this._persistence, TagFragment.this._tagTypeId));
        this._tagAdapter.notifyDataSetChanged();
    }

    void updateTagList() {
        Tag[] tags = Tag.findByType(this._persistence, this._tagTypeId);
        this._tagAdapter.update(tags);
        this._tagAdapter.notifyDataSetChanged();
    }

    private static final class TagAdapter extends BaseAdapter {

        private Tag[] _tags;
        private final Context _ctx;

        private TagAdapter(Context context, Tag[] tags) {
            this._ctx = context;
            this._tags = tags;
        }

        void update(Tag[] tags) {
            this._tags = tags;
        }

        @Override
        public int getCount() {
            return this._tags.length;
        }

        @Override
        public Object getItem(int i) {
            return this._tags[i];
        }

        @Override
        public long getItemId(int i) {
            return this._tags[i].getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(this._ctx);
                convertView = inflater.inflate(R.layout.list_tag, null);
                holder = new Holder();
                holder._labTagName = (TextView) convertView.findViewById(R.id.tag_name);
                holder._labTagCat = (TextView) convertView.findViewById(R.id.tag_cat_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder._labTagName.setText(this._tags[position].getName());
            TagCategory tagCat = this._tags[position].getCategory();
            if (tagCat != null) {
                holder._labTagCat.setVisibility(View.VISIBLE);
                holder._labTagCat.setText(tagCat.getName());
            } else {
                holder._labTagCat.setVisibility(View.INVISIBLE);
                holder._labTagCat.setText(null);
            }
            return convertView;
        }
    }

    private static final class Holder {
        private TextView _labTagName;
        private TextView _labTagCat;
    }
}
