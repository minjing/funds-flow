package ministudio.fundsflow.tag;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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

import com.google.common.base.Strings;

import ministudio.fundsflow.R;
import ministudio.fundsflow.UIHelper;

/**
 * Created by min on 16/1/24.
 */
final class TagEditor {

    void createUI(final TagActivity activity, final int typeId) {
        final View popupView = activity.getLayoutInflater().inflate(R.layout.popup_tag, null);
        ListView tagCatListView = (ListView) popupView.findViewById(R.id.tag_cat_list);
        TagCategory[] tagCats = TagCategory.findByType(activity.getPersistence(), typeId);
        final TagCategoryListAdapter tagCatListAdapter = new TagCategoryListAdapter(activity, tagCats);
        tagCatListView.setAdapter(tagCatListAdapter);
        tagCatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                tagCatListAdapter.select(pos);
                tagCatListAdapter.notifyDataSetChanged();
            }
        });

        Button btnCreateCat = (Button) popupView.findViewById(R.id.btn_tag_cat_add);
        btnCreateCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout layout = (LinearLayout) popupView.findViewById(R.id.tag_cat_add_container);
                final EditText editTxt = new EditText(activity);
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
                            if (Strings.isNullOrEmpty(newName)) {
                                UIHelper.showMessage(editTxt, "Tag Category Name must be specified");
                                return true;
                            }

                            //int selectedType = (int) TagActivity.this._tagTypeListAdapter.getItemId(TagActivity.this._selectedType);
                            TagCategory tagCat = new TagCategory(activity.getPersistence());
                            tagCat.setName(newName);
                            tagCat.setType(typeId);
                            int count = tagCat.save();
                            if (count == 0) {
                                UIHelper.showMessage(popupView, "Tat category exists");
                                return true;
                            }
                            tagCatListAdapter.update(TagCategory.findByType(activity.getPersistence(), typeId));
                            tagCatListAdapter.notifyDataSetChanged();

                            // remove text box
                            layout.removeView(editTxt);
                        }
                        return false;
                    }
                });
                editTxt.setFocusable(true);
                editTxt.setFocusableInTouchMode(true);
                editTxt.requestFocus();
                editTxt.requestFocusFromTouch();
            }
        });

        final PopupWindow popupWin = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWin.setTouchable(true);
        popupWin.setFocusable(true);
        popupWin.setOutsideTouchable(true);
        popupWin.setBackgroundDrawable(new ColorDrawable(0xeeeeee));
        View rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        popupWin.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        Button btnSave = (Button) popupView.findViewById(R.id.btn_save_tag);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txt = (EditText) popupView.findViewById(R.id.txt_tag_name);
                String newTagName = txt.getText().toString();
                if (Strings.isNullOrEmpty(newTagName)) {
                    UIHelper.showMessage(v, "The tag name is required");
                    return;
                }
                if (Tag.find(activity.getPersistence(), typeId, newTagName) != null) {
                    UIHelper.showMessage(v, "The new tag name exist");
                    return;
                }
                TagCategory tagCat = tagCatListAdapter.getSelectedCategory();
                Tag tag = new Tag(activity.getPersistence(), newTagName);
                tag.setType(typeId);
                tag.setCategory(tagCat);
                tag.save();

                popupWin.dismiss();
                // Refresh tag fragment
                activity.getCurrentFragment().updateTagList();
            }
        });
        Button btnDelete = (Button) popupView.findViewById(R.id.btn_del_tag);
        Button btnCancel = (Button) popupView.findViewById(R.id.btn_cancel_tag);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWin.dismiss();
            }
        });
    }

    private static final class TagCategoryListAdapter extends BaseAdapter {

        private TagCategory[] _tagCats;
        private final Context _ctx;
        private int _selectedPos = -1;

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

        TagCategory getSelectedCategory() {
            if (this._selectedPos == -1) {
                return null;
            } else {
                return this._tagCats[this._selectedPos];
            }
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
