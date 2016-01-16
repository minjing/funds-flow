package ministudio.fundsflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.base.Strings;

import ministudio.fundsflow.domain.Account;

public class AccountAddActivity extends AppCompatActivity {

    public static final String ARG_ACCOUNT_ID = "AccountId";

    private Intent _intent;
    private EditText _inputAccountName;
    private Button _btnSave;
    private Button _btnCancel;

    private SQLitePersistence persistence;

    private int _accountId = Account.UNDEFINED_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this._intent = getIntent();
        this._inputAccountName = (EditText) findViewById(R.id.accountAdd_accountName);
        this._btnSave = (Button) findViewById(R.id.btnAccountAdd_save);
        this._btnCancel = (Button) findViewById(R.id.btnAccountAdd_cancel);
        this._accountId = (int) this._intent.getLongExtra(ARG_ACCOUNT_ID, Account.UNDEFINED_ID);

        this.persistence = new SQLitePersistence(AccountAddActivity.this);
        if (this._accountId != Account.UNDEFINED_ID) {
            Account account = Account.findAccount(this.persistence.getReadableDatabase(), this._accountId);
            if (account != null) {
                this._inputAccountName.setText(account.getName());
            }
        }

        this._btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String newName = AccountAddActivity.this._inputAccountName.getText().toString();
                if (Strings.isNullOrEmpty(newName)) {
                    Snackbar.make(AccountAddActivity.this.findViewById(R.id.accountAdd_accountName),
                            "Account name is required", Snackbar.LENGTH_LONG).show();
                    return;
                }

                SQLitePersistence persistence = new SQLitePersistence(AccountAddActivity.this);
                if (_accountId != Account.UNDEFINED_ID) {
                    Account account = Account.findAccount(persistence.getWritableDatabase(), _accountId);
                    if (account == null) {
                        Snackbar.make(AccountAddActivity.this.findViewById(R.id.accountAdd_accountName),
                                "Account does not exists - " + _accountId, Snackbar.LENGTH_LONG).show();
                        setResult(RESULT_CANCELED);
                    } else {
                        account.setName(newName);
                        account.save();
                        setResult(RESULT_OK);
                    }
                } else {
                    boolean isExist = Account.isAccountExist(persistence.getReadableDatabase(), newName);
                    if (isExist) {
                        Snackbar.make(AccountAddActivity.this.findViewById(R.id.accountAdd_accountName),
                                "Account exists", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    Account newAccount = new Account(persistence.getWritableDatabase(), newName);
                    newAccount.save();
                    setResult(RESULT_OK);
                }
                AccountAddActivity.this.finish();
            }
        });

        this._btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                AccountAddActivity.this.finish();
            }
        });
    }
}
