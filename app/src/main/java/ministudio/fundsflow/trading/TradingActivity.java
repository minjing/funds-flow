package ministudio.fundsflow.trading;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ministudio.fundsflow.ArgumentValidator;
import ministudio.fundsflow.R;
import ministudio.fundsflow.SQLitePersistence;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
                headerHolder._labTradingHeader.setText(FriendlyDate(date));
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
            if (isSameDate(this._tradings[position].getTime(), this._tradings[position - 1].getTime())) {
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

    public static int daysOfTwo(Date originalDate, Date compareDateDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(originalDate);
        int originalDay = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(compareDateDate);
        int compareDay = aCalendar.get(Calendar.DAY_OF_YEAR);

        return originalDay - compareDay;
    }

    public static boolean isSameDate(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
            return false;
        }
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)) {
            return false;
        }
        if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        return true;
    }

    public static String FriendlyDate(Date compareDate) {
        Date nowDate = new Date();
        int dayDiff = daysOfTwo(nowDate, compareDate);

        if (dayDiff <= 0)
            return "今日";
        else if (dayDiff == 1)
            return "昨日";
        else if (dayDiff == 2)
            return "前日";
        else
            return new SimpleDateFormat("M月d日 E").format(compareDate);
    }
}
