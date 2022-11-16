package id.greenrunchly.exambrowser.trikabta;

import static id.greenrunchly.exambrowser.trikabta.DetectConnection.isNetworkStatusAvialable;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    MyTimerTask myTimerTask;
    Timer timer;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setSupportActionBar((Toolbar) findViewById(R.toolbar));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        final String s = getIntent().getStringExtra("url");
        final WebView view = findViewById(R.id.activity_main_webview);
        view.setWebChromeClient(new WebChromeClient() {

        });

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setSupportZoom(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setDisplayZoomControls(false);
        view.setWebViewClient(new ExamWebView());
        view.loadUrl("http://" + s);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(isNetworkStatusAvialable (MainActivity.this)) {
                view.reload();
                view.getSettings().setDomStorageEnabled(true);
            } else {
                Toast.makeText(MainActivity.this, "Url tidak valid/offline", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, InputAddress.class);
                startActivity(intent);
                finish();
                /*view.loadDataWithBaseURL(null, "<html><body><img width=\"100%\" height=\"100%\" src=\"file:///android_res/drawable/no_network.png\"></body></html>", "text/html", "UTF-8", null);
                progressDialogModel.hideProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                Intent i = new Intent(getBaseContext(), InputAddress.class);
                startActivity(i);
                finish();*/
            }
        });
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            bringApplicationToFront();
        }
    }

    private class ExamWebView extends WebViewClient {
        private ExamWebView() {
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(isNetworkStatusAvialable (MainActivity.this)) {
                view.loadUrl(url);
                progressDialogModel.pdMenyiapkanDataLogin(MainActivity.this);
            } else {
                Toast.makeText(MainActivity.this, "Url tidak valid/offline", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, InputAddress.class);
                startActivity(intent);
                finish();
            }
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            progressDialogModel.hideProgressDialog();
            swipeRefreshLayout.setRefreshing(false);
            super.onPageFinished(view, url);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            swipeRefreshLayout.setRefreshing(false);
            Intent i = new Intent(getBaseContext(), InputAddress.class);
            i.putExtra("valid", "offline");
            startActivity(i);
            System.exit(0);
        }
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
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onPause() {
        if (timer == null) {
            myTimerTask = new MyTimerTask();
            timer = new Timer();
            timer.schedule(myTimerTask, 100, 100);
        }
        super.onPause();
    }

    private void bringApplicationToFront() {
        KeyguardManager myKeyManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (myKeyManager.inKeyguardRestrictedInputMode())
            return;
        Log.d("TAG", "==== Bringging Application to Front ====");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


}