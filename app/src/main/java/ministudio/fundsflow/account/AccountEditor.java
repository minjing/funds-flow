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
import ministudio.fundsflow.UIHelper;
import ministudio.fundsflow.domain.Domain;

/**
 * Created by xquan on 1/27/2016.
 */
final class AccountEditor {

    void createUI(final AccountActivity activity, final int accountId) {
        final View popupView = activity.getLayoutInflater().inflate(R.layout.popup_account, null);

        final PopupWindow popupWin = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWin.setTouchable(true);
        popupWin.setFocusable(true);
        popupWin.setOutsideTouchable(true);
        popupWin.setBackgroundDrawable(new ColorDrawable(0xeeeeee));
        View rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        popupWin.showAtLocation(rootView, Gravity.CENTER, 0, 0);

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
                Account account = Account.findByName(activity.getPersistence(), newAccountName);
                if (accountId == Domain.UNDEFINED_ID) {
                    // create new account
                    if (account != null) {
                        UIHelper.showMessage(v, popupView.getResources().getString(R.string.message_account_name_used));
                        return;
                    }
                    account = new Account(activity.getPersistence(), newAccountName);
                } else {
                    // update existing account
                    if (account != null && account.getId() != accountId) {
                        UIHelper.showMessage(v, popupView.getResources().getString(R.string.message_account_name_used));
                        return;
                    }
                    account.setName(newAccountName);
                }
                account.save();
                popupWin.dismiss();
                activity.updateAccountList();
            }
        });
        Button btnDelete = (Button) popupView.findViewById(R.id.btn_del);
        if (accountId == Domain.UNDEFINED_ID) {
            btnDelete.setEnabled(false);
        } else {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Account.delete(activity.getPersistence(), accountId);
                    popupWin.dismiss();
                    activity.updateAccountList();
                }
            });
        }
        Button btnCancle = (Button) popupView.findViewById(R.id.btn_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWin.dismiss();
            }
        });
    }
}
