package ministudio.fundsflow;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by min on 16/1/17.
 */
public final class UIHelper {

    public static void showMessage(final View view, final String message) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("Ignore", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        }).show();
    }

    private UIHelper() { }
}
