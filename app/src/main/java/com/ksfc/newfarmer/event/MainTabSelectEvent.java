package com.ksfc.newfarmer.event;

import com.ksfc.newfarmer.activitys.MainActivity;

/**
 * Created by CAI on 2016/7/29.
 */
public class MainTabSelectEvent {
    public MainTabSelectEvent(MainActivity.Tab tab) {
        this.tab = tab;
    }

    public MainActivity.Tab tab;
}
