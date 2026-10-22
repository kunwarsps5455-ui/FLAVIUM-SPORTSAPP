package com.flavium.sportsfest.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.flavium.sportsfest.R;

public class RulesWebViewActivity extends AppCompatActivity {

    private WebView webViewRules;
    private Button btnLoadHtml, btnLoadWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_webview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Fest Rules & Guidelines");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        webViewRules = findViewById(R.id.webViewRules);
        btnLoadHtml = findViewById(R.id.btnLoadHtml);
        btnLoadWeb = findViewById(R.id.btnLoadWeb);

        webViewRules.setWebViewClient(new WebViewClient());

        // Syllabus Topic: Displaying HTML Using loadData()
        displayLocalHtml();

        btnLoadHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocalHtml();
            }
        });

        // Syllabus Topic: Loading URLs Using loadUrl()
        btnLoadWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViewRules.loadUrl("https://en.wikipedia.org/wiki/Multi-sport_event");
            }
        });
    }

    private void displayLocalHtml() {
        String htmlContent = "<html><body style='font-family:sans-serif; padding:16px; line-height:1.6; color:#212121;'>"
                + "<h2 style='color:#1A237E;'>FLAVIUM SPORTS FESTIVAL RULES</h2>"
                + "<h3>1. Division Rules</h3>"
                + "<p>Boys and Girls teams compete in separate divisions. Under no circumstances can a boys team be pooled with or matched against a girls team.</p>"
                + "<h3>2. Cricket Tournament Rules</h3>"
                + "<p>Standard matches are 8 overs per side. Teams are divided into round-robin pools with knockout semifinals and finals.</p>"
                + "<h3>3. Knockout Disciplines</h3>"
                + "<p>Single-elimination knockout format applies for football, badminton, and squash brackets.</p>"
                + "<h3>4. Code of Conduct</h3>"
                + "<p>All players must report to the respective ground 15 minutes prior to scheduled match time.</p>"
                + "</body></html>";
        webViewRules.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
