package ministudio.fundsflow.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import ministudio.fundsflow.R;
import ministudio.fundsflow.SQLitePersistence;
import ministudio.fundsflow.UIHelper;

public class AccountActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView accountListView;
    private SwipeRefreshLayout accountListLayout;

    private SQLitePersistence persistence;
    private AccountAdapter accountAdapter;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountListView = (ListView) findViewById(R.id.account_list);
        this.accountListLayout = (SwipeRefreshLayout) findViewById(R.id.account_layout);
        this.accountListLayout.setOnRefreshListener(this);

        this.persistence = new SQLitePersistence(this);
        Account[] accounts = Account.getAll(persistence);
        this.accountAdapter = new AccountAdapter(this, accounts);
        this.accountListView.setAdapter(this.accountAdapter);

        this.accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(getApplicationContext(), AccountAddActivity.class);
                intent.putExtra(AccountAddActivity.ARG_ACCOUNT_ID, id);
                startActivityForResult(intent, RESULT_CANCELED);
            }
        });

        FloatingActionButton btnCreateAccount = (FloatingActionButton) findViewById(R.id.create_account);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccountAddActivity.class);
                startActivityForResult(intent, RESULT_CANCELED);
            }
        });

        registerForContextMenu(this.accountListView);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                accountListLayout.setRefreshing(false);
                accountAdapter.update(Account.getAll(persistence));
                accountAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.accountAdapter.update(Account.getAll(this.persistence));
        this.accountAdapter.notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                this.accountAdapter.update(Account.getAll(this.persistence));
                this.accountAdapter.notifyDataSetChanged();
                break;
            case RESULT_CANCELED:
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.remove_account:
                if (menuInfo.id == Account.DEFAULT_ACCOUNT_ID) {
                    UIHelper.showMessage(findViewById(R.id.account_list), "Default account can't be deleted.");
                    return true;
                }
                Account.delete(this.persistence, (int) menuInfo.id);
                this.accountAdapter.update(Account.getAll(this.persistence));
                this.accountAdapter.notifyDataSetChanged();
                return true;
        }
        return false;
    }

    private static final class AccountAdapter extends BaseAdapter {

        private Account[] accounts;
        private final Context ctx;

        private AccountAdapter(Context context, Account[] accounts) {
            this.ctx = context;
            this.accounts = accounts;
        }

        void update(Account[] accounts) {
            this.accounts = accounts;
        }

        @Override
        public int getCount() {
            return this.accounts.length;
        }

        @Override
        public Object getItem(int i) {
            return this.accounts[i];
        }

        @Override
        public long getItemId(int i) {
            return this.accounts[i].getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(this.ctx);
                convertView = inflater.inflate(R.layout.account_list, null);
                holder = new Holder();
                holder.labelAccountName = (TextView) convertView.findViewById(R.id.account_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.labelAccountName.setText(this.accounts[position].getName());
            return convertView;
        }
    }

    private static final class Holder {
        private TextView labelAccountName;
    }
}
