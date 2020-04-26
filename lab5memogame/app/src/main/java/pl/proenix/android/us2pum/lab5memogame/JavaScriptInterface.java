package pl.proenix.android.us2pum.lab5memogame;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JavaScriptInterface {
    private MainActivity parentActivity;

    public JavaScriptInterface(MainActivity _activity, WebView _webView)  {
        parentActivity = _activity;
    }

    @JavascriptInterface
    public void setResult(int val){
        this.parentActivity.updateAndroidInAppCounter(val);
    }
}
