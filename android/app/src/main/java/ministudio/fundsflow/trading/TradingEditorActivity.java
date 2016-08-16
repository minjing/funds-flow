package ministudio.fundsflow.trading;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import ministudio.fundsflow.R;
import ministudio.fundsflow.helper.DateTimeHelper;

/**
 * Created by min on 16/1/30.
 */
public class TradingEditorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText _txtDate;
    private EditText _txtTime;
    private Calendar _selectedCalender = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this._selectedCalender.setTimeInMillis(System.currentTimeMillis());

        this._txtDate = (EditText) findViewById(R.id.txt_date);
        this._txtDate.setInputType(InputType.TYPE_NULL);
        this._txtDate.setText(DateTimeHelper.getDate(this, this._selectedCalender.getTime()));
        this._txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TradingEditorActivity.this, TradingEditorActivity.this,
                        TradingEditorActivity.this._selectedCalender.get(Calendar.YEAR),
                        TradingEditorActivity.this._selectedCalender.get(Calendar.MONTH),
                        TradingEditorActivity.this._selectedCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        this._txtTime = (EditText) findViewById(R.id.txt_time);
        this._txtTime.setInputType(InputType.TYPE_NULL);
        this._txtTime.setText(DateTimeHelper.getTime(this, this._selectedCalender.getTime()));
        this._txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(TradingEditorActivity.this, TradingEditorActivity.this,
                        TradingEditorActivity.this._selectedCalender.get(Calendar.HOUR_OF_DAY),
                        TradingEditorActivity.this._selectedCalender.get(Calendar.MINUTE), true).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this._selectedCalender.set(Calendar.YEAR, year);
        this._selectedCalender.set(Calendar.MONTH, monthOfYear);
        this._selectedCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        this._txtDate.setText(DateTimeHelper.getDate(this, this._selectedCalender.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this._selectedCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
        this._selectedCalender.set(Calendar.MINUTE, minute);
        this._txtDate.setText(DateTimeHelper.getDate(this, this._selectedCalender.getTime()));
    }
}
