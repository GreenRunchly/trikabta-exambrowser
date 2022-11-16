package id.greenrunchly.exambrowser.trikabta;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputAddress extends Activity {
    private static final String FLAVOR = "";
    private EditText inputAddress;
    private TextInputLayout inputLayoutAddress;

    private class MyTextWatcher implements TextWatcher {
        private final View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            if (this.view.getId() == R.id.input_address) {
                InputAddress.this.validateAddress();
            }
        }
    }

    @SuppressLint({"NonConstantResourceId", "PrivateResource"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_address);
        try {
            String s = getIntent().getStringExtra("valid");
            if (s.equals("offline")){
                Toast.makeText(InputAddress.this, "Anda sedang offline atau situs tidak bisa di jangkau!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(InputAddress.this, s, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        this.inputLayoutAddress = findViewById(R.id.input_layout_address);
        this.inputAddress = findViewById(R.id.input_address);
        this.inputAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputAddress.this.inputAddress.setSelection(InputAddress.this.inputAddress.getText().length());
            }
        });
        Button btnLanjut = findViewById(R.id.btn_lanjut);
        this.inputAddress.addTextChangedListener(new MyTextWatcher(this.inputAddress));
        btnLanjut.setOnClickListener(view -> InputAddress.this.submitForm());
        if (!isNetworkAvailable()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Peringatan");
            alertDialogBuilder.setMessage("Tidak ada konektifitas dari WiFi maupun Data, yakin ingin melanjutkan?").setCancelable(false).setPositiveButton("Lanjutkan", (dialog, id) -> dialog.cancel()).setNegativeButton("Keluar", (dialog, id) -> {
                InputAddress.this.finish();
                System.exit(0);
            });
            alertDialogBuilder.create().show();
        }
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.inputAddress.setText(prefs.getString("autoSave", FLAVOR));
        this.inputAddress.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                prefs.edit().putString("autoSave", s.toString()).apply();
            }
        });
        this.inputAddress.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == 0) {
                switch (keyCode) {
                    case R.styleable.Toolbar_titleMarginEnd /*23*/:
                    case R.styleable.AppCompatTheme_editTextColor /*66*/:
                        InputAddress.this.submitForm();
                        return true;
                }
            }
            return false;
        });
    }

    private void submitForm() {
        if (validateAddress()) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("url", this.inputAddress.getText().toString());
            startActivity(intent);
            finish();
        }
    }

    private boolean validateAddress() {
        if (this.inputAddress.getText().toString().trim().isEmpty()) {
            this.inputLayoutAddress.setError(getString(R.string.err_msg_name));
            requestFocus(this.inputAddress);
            return false;
        }
        this.inputLayoutAddress.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(5);
        }
    }

    public boolean isNetworkAvailable() {
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void onBackPressed() {
        ///Memperlihatkan pesan
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Keluar");
        alertDialogBuilder.setMessage("Ingin keluar dari aplikasi?").setCancelable(false).setPositiveButton("Iya", (dialog, id) -> System.exit(0)).setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());
        alertDialogBuilder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOverlayPermission();
        Log.d("TAG", "==== On Resume Input ====");
    }

    // method to ask user to grant the Overlay permission
    public void checkOverlayPermission(){
        Log.d("TAG", "==== Mencoba Overlay ====");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Log.d("TAG", "==== Meminta Settings Overlay ====");
                ///Memperlihatkan pesan
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Satu langkah lagi!");
                alertDialogBuilder.setMessage("Izin Overlay diperlukan untuk meminimalisir kecurangan para pengguna Android 6.0+, mohon aktifkan untuk melanjutkan!").setCancelable(false).setPositiveButton("Ayo ke Pengaturan!", (dialog, id) -> {
                    dialog.cancel();
                    // send user to the device settings
                    Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(myIntent);
                    Log.d("TAG", "==== Membuka Settings Overlay ====");
                }).setNegativeButton("Keluar", (dialog, id) -> {
                    InputAddress.this.finish();
                    System.exit(0);
                });
                alertDialogBuilder.create().show();
            }
        }
    }
}
