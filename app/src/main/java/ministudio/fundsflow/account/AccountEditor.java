package ministudio.fundsflow.account;

import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.common.base.Strings;

import ministudio.fundsflow.R;
import ministudio.fundsflow.helper.UIHelper;

/**
 * Created by xquan on 1/27/2016.
 */
final class AccountEditor {

    void createUI(final AccountActivity activity, final Account account) {
        final View popupView = activity.getLayoutInflater().inflate(R.layout.popup_account, null);

        final PopupWindow popupWin = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWin.setTouchable(true);
        popupWin.setFocusable(true);
        popupWin.setOutsideTouchable(true);
        popupWin.setBackgroundDrawable(new ColorDrawable(0xeeeeee));
        View rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        popupWin.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        EditText txtAccountName = (EditText) popupView.findViewById(R.id.txt_account_name);
        if (account != null) {
            txtAccountName.setText(account.getName());
        }

        Button btnSave = (Button) popupView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtAccountName = (EditText) popupView.findViewById(R.id.txt_account_name);
                String newAccountName = txtAccountName.getText().toString();
                if (Strings.isNullOrEmpty(newAccountName)) {
                    UIHelper.showMessage(v, popupView.getResources().getString(R.string.message_account_name_empty));
                    return;
                }
                Account newAccount = Account.findByName(activity.getPersistence(), newAccountName);
                if (account == null) {
                    // create new account
                    if (newAccount != null) {
                        UIHelper.showMessage(v, popupView.getResources().getString(R.string.message_account_name_used));
                        return;
                    }
                    newAccount = new Account(activity.getPersistence(), newAccountName);
                } else {
                    // update existing account
                    if (newAccount != null && newAccount.getId() != account.getId()) {
                        UIHelper.showMessage(v, popupView.getResources().getString(R.string.message_account_name_used));
                        return;
                    }
                    newAccount = account;
                    newAccount.setName(newAccountName);
                }
                newAccount.save();
                popupWin.dismiss();
                activity.updateAccountList();
            }
        });
        Button btnDelete = (Button) popupView.findViewById(R.id.btn_del);
        if (account == null || account.getId() == Account.DEFAULT_ACCOUNT_ID) {
            btnDelete.setEnabled(false);
        } else {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Account.delete(activity.getPersistence(), account.getId());
                    popupWin.dismiss();
                    activity.updateAccountList();
                }
            });
        }
        Button btnCancel = (Button) popupView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWin.dismiss();
            }
        });
    }
}
