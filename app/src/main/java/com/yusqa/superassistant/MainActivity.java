package com.yusqa.superassistant;

import android.app.Activity;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.graphics.Color;
import android.util.Log;

import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import org.json.JSONObject;

public class MainActivity extends Activity {
    private WebView web;
    private CredentialManager credentialManager;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(11,16,32));
        getWindow().setNavigationBarColor(Color.rgb(11,16,32));
        credentialManager = CredentialManager.create(this);

        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(11,16,32));
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.addJavascriptInterface(new AccountBridge(), "Android");
        web.loadUrl("file:///android_asset/index.html");
        setContentView(web);
    }

    private class AccountBridge {
        @JavascriptInterface
        public void googleSignIn() {
            runOnUiThread(() -> {
                try {
                    String clientId = getString(R.string.default_web_client_id);
                    if (clientId.startsWith("WEB_CLIENT_ID_BURAYA")) {
                        sendError("Google Cloud Console Web Client ID ayarlanmalı.");
                        return;
                    }
                    GetGoogleIdOption option = new GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(clientId)
                        .setAutoSelectEnabled(false)
                        .build();

                    GetCredentialRequest request = new GetCredentialRequest.Builder()
                        .addCredentialOption(option)
                        .build();

                    credentialManager.getCredentialAsync(
                        MainActivity.this,
                        request,
                        new CancellationSignal(),
                        getMainExecutor(),
                        new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                            @Override public void onResult(GetCredentialResponse response) {
                                try {
                                    Credential credential = response.getCredential();
                                    if (!GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                                        sendError("Google hesabı seçilemedi.");
                                        return;
                                    }
                                    GoogleIdTokenCredential google =
                                        GoogleIdTokenCredential.createFrom(credential.getData());
                                    JSONObject profile = new JSONObject();
                                    profile.put("name", google.getDisplayName() == null ? "Google kullanıcısı" : google.getDisplayName());
                                    profile.put("email", google.getId());
                                    String json = profile.toString();
                                    web.evaluateJavascript("window.onGoogleSignIn(" + JSONObject.quote(json) + ")", null);
                                } catch (Exception e) {
                                    sendError("Hesap bilgisi okunamadı.");
                                }
                            }

                            @Override public void onError(GetCredentialException e) {
                                Log.e("SuperAssistant", "Google sign-in error", e);
                                sendError("Giriş iptal edildi veya OAuth ayarları eksik.");
                            }
                        }
                    );
                } catch (Exception e) {
                    sendError("Google giriş ayarı kontrol edilmeli.");
                }
            });
        }
    }

    private void sendError(String message) {
        if (web != null) {
            web.post(() -> web.evaluateJavascript(
                "window.onGoogleSignInError(" + JSONObject.quote(message) + ")", null
            ));
        }
    }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
