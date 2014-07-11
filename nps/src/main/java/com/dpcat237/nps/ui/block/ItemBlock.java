package com.dpcat237.nps.ui.block;

import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dpcat237.nps.helper.NumbersHelper;

public class ItemBlock {
    private static final int CACHE_MAX_SIZE = 5000;

    public static void prepareWebView(WebView webView, String textSize, String itemLink, String itemTitle, String feedTitle, String itemContent, Integer dateAdd) {
        webView.setFocusable(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        WebSettings ws = webView.getSettings();
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(false);
        ws.setTextZoom(Integer.parseInt(textSize));

        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setAppCacheMaxSize(CACHE_MAX_SIZE);
        ws.setAppCacheEnabled(true);

        Integer timestamp = dateAdd * 1000;
        String date = NumbersHelper.getDate(timestamp);

        String contentHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
                "<div style='border-bottom:1px solid #d3d3d3; padding-bottom:4px; font-weight: bold; font-size:1em;'>" +
                "<a style='text-decoration: none; color:#12c;' href='"+itemLink+"'>"+itemTitle+"</a>" +
                "</div>" +
                "<p style='margin-top:1px; font-size:1em;'><font style='color:#12c;'>"+feedTitle+"</font>" +
                " <font style='color:#d3d3d3;'>on "+date+"</font></p>";

        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        String content = "<div style='padding:0px 3px 0px 2px;'>"+contentHeader+itemContent+"</div>";
        webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
    }
}
