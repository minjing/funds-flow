package ministudio.fundsflow;

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

public class AccountActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView accountListView;
    private SwipeRefreshLayout accountListLayout;

    private ArrayAdapter mAdapter;

    String[] data = { "a", "b", "c" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountListView = (ListView) findViewById(R.id.account_list);
        this.accountListLayout = (SwipeRefreshLayout) findViewById(R.id.account_layout);
        this.accountListLayout.setOnRefreshListener(this);

        this.mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        this.accountListView.setAdapter(this.mAdapter);

        FloatingActionButton btnCreateAccount = (FloatingActionButton) findViewById(R.id.create_account);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                accountListLayout.setRefreshing(false);
//                ItemInfo info = new ItemInfo();
//                info.setName("coin-refresh");
//                infoList.add(info);
//                adapter.notifyDataSetChanged();
            }
        }, 500);
    }
}
