package id.greenrunchly.exambrowser.trikabta;

import android.app.ProgressDialog;
import android.content.Context;

public class progressDialogModel {

    static ProgressDialog progressDialog;

    public static void pdMenyiapkanDataLogin(Context context){
        progressDialog=new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Memuat...");
        progressDialog.show();
    }

    public static void hideProgressDialog(){
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
