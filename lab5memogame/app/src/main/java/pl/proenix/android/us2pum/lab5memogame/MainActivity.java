package pl.proenix.android.us2pum.lab5memogame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.numberOfMoves);

        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        WebView.setWebContentsDebuggingEnabled(true);
        webView.loadUrl("file:///android_asset/memo-game/dist/index.html");

        webView.addJavascriptInterface(new JavaScriptInterface(this, webView), "MyHandler");
    }

    public void updateAndroidInAppCounter(final int val) {
        runOnUiThread(new Runnable() {
            public void run() {
                textView.setText("Liczba ruchów: " + val);
            }
        });
    }
}
