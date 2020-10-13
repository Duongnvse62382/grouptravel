package com.fpt.gta.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fpt.gta.R;


public class DialogShowErrorMessage {
    public static void showValidationDialog(Context context, String error) {
        try {
            if (ActivityUtil.isRunning((Activity) context)){
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.row_validation_dialog);
                TextView txtError = dialog.findViewById(R.id.txtErrorValidation);
                Button btnOK = dialog.findViewById(R.id.btnOkValidation);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                txtError.setText(error);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showDialogNoInternet(Context context, String error) {
        try {
            if (ActivityUtil.isRunning((Activity) context)){
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.row_validation_dialog);
                TextView txtError = dialog.findViewById(R.id.txtErrorValidation);
                Button btnOK = dialog.findViewById(R.id.btnOkValidation);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                txtError.setText(error);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        ((Activity) context).finish();
                    }
                });
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
