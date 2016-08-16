package ministudio.fundsflow.tag;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ministudio.fundsflow.DividerItemDecoration;
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
    private RecyclerView _tagListView;
    private TagListAdapter _tagAdapter;

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

        this._tagListLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.tag_layout);
        this._tagListLayout.setOnRefreshListener(this);

        this._tagListView = (RecyclerView) rootView.findViewById(R.id.tag_list);
        this._tagListView.setHasFixedSize(true);
        this._tagListView.setItemAnimator(new DefaultItemAnimator());
        this._tagListView.addItemDecoration(new DividerItemDecoration(this._activity, DividerItemDecoration.VERTICAL_LIST));

        Tag[] tags = Tag.findByType(this._persistence, this._tagTypeId);
        this._tagAdapter = new TagListAdapter(tags);

        LinearLayoutManager layoutMgr = new LinearLayoutManager(this._activity);
        layoutMgr.setOrientation(LinearLayoutManager.VERTICAL);
        this._tagListView.setLayoutManager(layoutMgr);
        this._tagListView.setAdapter(this._tagAdapter);

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

    private final class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.TagListViewHolder> {

        private Tag[] _tags;

        private TagListAdapter(Tag[] tags) {
            this._tags = tags;
        }

        @Override
        public TagListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tag, parent, false);
            TagListViewHolder viewHolder = new TagListViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TagListViewHolder holder, int position) {
            holder._pos = position;
            Tag tag = this._tags[position];
            holder._labTagName.setText(tag.getName());
            TagCategory tagCat = this._tags[position].getCategory();
            if (tagCat != null) {
                holder._labTagCatName.setVisibility(View.VISIBLE);
                holder._labTagCatName.setText(tag.getCategory().getName());
            } else {
                holder._labTagCatName.setVisibility(View.INVISIBLE);
                holder._labTagCatName.setText(null);
            }
        }

        @Override
        public int getItemCount() {
            return this._tags.length;
        }

        void update(Tag[] tags) {
            this._tags = tags;
        }

        public final class TagListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView _labTagName;
            private TextView _labTagCatName;
            private int _pos;

            private TagListViewHolder(View rootView) {
                super(rootView);
                this._labTagName = (TextView) rootView.findViewById(R.id.lab_tag_name);
                this._labTagCatName = (TextView) rootView.findViewById(R.id.lab_tag_cat_name);
                rootView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                new TagEditor().createUI(TagFragment.this._activity, TagFragment.this._tagTypeId, TagListAdapter.this._tags[this._pos]);
            }
        }
    }
}
