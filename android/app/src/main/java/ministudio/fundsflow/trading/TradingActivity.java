package ministudio.fundsflow.trading;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import ministudio.fundsflow.helper.ArgumentValidator;
import ministudio.fundsflow.R;
import ministudio.fundsflow.SQLitePersistence;
import ministudio.fundsflow.helper.DateTimeHelper;

public class TradingActivity extends AppCompatActivity {

    private SQLitePersistence _persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView tradingListView = (RecyclerView) findViewById(R.id.trading_list);
        tradingListView.setItemAnimator(new DefaultItemAnimator());

        this._persistence = new SQLitePersistence(this);
        Trading[] tradings = Trading.findAll(this._persistence);
        LinearLayoutManager layoutMgr = new LinearLayoutManager(this);
        layoutMgr.setOrientation(LinearLayoutManager.VERTICAL);
        tradingListView.setLayoutManager(layoutMgr);
        tradingListView.setAdapter(new TradingListAdapter(tradings));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TradingEditorActivity.class));
            }
        });
    }

    private final class TradingListAdapter extends RecyclerView.Adapter<TradingHolder> {

        private static final int TYPE_NORMAL    = 0;
        private static final int TYPE_HEADER    = 1;

        private Trading[] _tradings;

        TradingListAdapter(Trading[] tradings) {
            ArgumentValidator.checkNull(tradings, "tradings");
            this._tradings = tradings;
        }

        @Override
        public TradingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(TradingActivity.this);
            if (viewType == TYPE_NORMAL) {
                return new TradingHolder(inflater.inflate(R.layout.list_trading, parent, false));
            } else {
                return new TradingHeaderHolder(inflater.inflate(R.layout.list_trading_header, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(TradingHolder holder, int position) {
            Trading trading = this._tradings[position];
            if (holder instanceof TradingHeaderHolder) {
                TradingHeaderHolder headerHolder = (TradingHeaderHolder) holder;
                Date date = new Date(trading.getTime());
                headerHolder._labTradingHeader.setText(DateTimeHelper.getSweetDate(TradingActivity.this, date));
            }
            holder._labAmount.setText(Float.toString(trading.getAmount()));
        }

        @Override
        public int getItemCount() {
            return this._tradings.length;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            if (DateTimeHelper.isSameDate(this._tradings[position].getTime(), this._tradings[position - 1].getTime())) {
                return TYPE_NORMAL;
            } else {
                return TYPE_HEADER;
            }
        }

        @Override
        public long getItemId(int position) {
            return this._tradings[position].getId();
        }
    }

    private class TradingHolder extends RecyclerView.ViewHolder {

        private TextView _labAmount;

        public TradingHolder(View itemView) {
            super(itemView);
            this._labAmount = (TextView) itemView.findViewById(R.id.lab_amount);
        }
    }

    private class TradingHeaderHolder extends TradingHolder {

        private TextView _labTradingHeader;

        public TradingHeaderHolder(View itemView) {
            super(itemView);
            this._labTradingHeader = (TextView) itemView.findViewById(R.id.lab_trading_header);
        }
    }
}
