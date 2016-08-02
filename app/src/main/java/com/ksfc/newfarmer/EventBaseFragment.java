package com.ksfc.newfarmer;

import android.os.Bundle;
import android.support.annotation.Nullable;


import com.ksfc.newfarmer.event.EmptySubscribe;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by CAI on 2016/7/28.
 */
public abstract class EventBaseFragment extends BaseFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Subscribe
    public void emptySubscribr(EmptySubscribe subscribe) {
        // TODO: 2016/7/28
        //empty
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
