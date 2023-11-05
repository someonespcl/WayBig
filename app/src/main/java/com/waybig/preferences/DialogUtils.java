package com.waybig.preferences;
import android.app.Dialog;
import android.content.Context;
import com.waybig.R;

public class DialogUtils {
    public static Dialog showCustomDialog(Context context) {
        Dialog customDialog = new Dialog(context);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setCanceledOnTouchOutside(false);
        //customDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        customDialog.show();

        return customDialog;
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
