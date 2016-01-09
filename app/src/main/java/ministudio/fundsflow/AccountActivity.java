package ministudio.fundsflow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ministudio.fundsflow.domain.Account;

public class AccountActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView accountListView;
    private SwipeRefreshLayout accountListLayout;

    private SQLitePersistence persistence;
    private ArrayAdapter accountAdapter;

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
        Account[] accounts = Account.getAccounts(persistence.getReadableDatabase());
        this.accountAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, accounts);
        this.accountListView.setAdapter(this.accountAdapter);

        FloatingActionButton btnCreateAccount = (FloatingActionButton) findViewById(R.id.create_account);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountAddActivity.class));
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                accountListLayout.setRefreshing(false);
                accountAdapter.clear();
                accountAdapter.addAll(Account.getAccounts(persistence.getReadableDatabase()));
                accountAdapter.notifyDataSetChanged();
            }
        }, 500);
    }
}
