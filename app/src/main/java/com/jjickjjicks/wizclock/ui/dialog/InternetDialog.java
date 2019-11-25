package com.jjickjjicks.wizclock.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.ui.activity.MainActivity;

/**
 * Created by kapil on 03/11/18.
 */
public class InternetDialog {
    private Context context;

    public InternetDialog() {
    }

    public InternetDialog(Context context) {
        this.context = context;
    }

    public void showNoInternetDialog() {
        final Dialog dialog1 = new Dialog(context, R.style.df_dialog);
        dialog1.setContentView(R.layout.dialog_no_internet);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.findViewById(R.id.btnSpinAndWinRedeem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
        dialog1.show();
    }

    public boolean getInternetStatus() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            showNoInternetDialog();
        }
        return isConnected;
    }
}
