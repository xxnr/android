package com.ksfc.newfarmer.event;

/**
 * Created by CAI on 2016/7/29.
 */
public class WebShareUrlEvent {
    public String shareUrl;
    public boolean isRefresh;

    public WebShareUrlEvent(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public WebShareUrlEvent( String shareUrl,boolean isRefresh) {
        this.shareUrl = shareUrl;
        this.isRefresh = isRefresh;

    }
}
