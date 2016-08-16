package ministudio.fundsflow.account;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ministudio.fundsflow.R;
import ministudio.fundsflow.SQLitePersistence;

public class AccountActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout accountListLayout;

    private SQLitePersistence persistence;
    private AccountAdapter accountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.accountListLayout = (SwipeRefreshLayout) findViewById(R.id.account_layout);
        this.accountListLayout.setOnRefreshListener(this);

        this.persistence = new SQLitePersistence(this);
        Account[] accounts = Account.getAll(persistence);
        accountAdapter = new AccountAdapter(this, accounts);

        ListView accountListView = (ListView) findViewById(R.id.account_list);
        accountListView.setAdapter(this.accountAdapter);
        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                new AccountEditor().createUI(AccountActivity.this, (Account) AccountActivity.this.accountAdapter.getItem(pos));
            }
        });

        FloatingActionButton btnCreateAccount = (FloatingActionButton) findViewById(R.id.create_account);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AccountEditor().createUI(AccountActivity.this, null);
            }
        });
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

    SQLitePersistence getPersistence() {
        return this.persistence;
    }

    void updateAccountList() {
        this.accountAdapter.update(Account.getAll(persistence));
        this.accountAdapter.notifyDataSetChanged();
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
                convertView = inflater.inflate(R.layout.list_account, null);
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
