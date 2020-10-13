package com.fpt.gta.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ProgressDialogUtil {
    ProgressDialog progress;

    public ProgressDialogUtil(Context context) {
        this.progress = new ProgressDialog(context);
    }


    public void showProgressDialog() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage("Vui lòng chờ...");
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (progress.isShowing()) {
                    progress.setCancelable(false);
                }
            }
        }.execute();
    }

    public void hideProgressDialog() {
        progress.dismiss();
    }


}
