package com.ksfc.newfarmer.event;

/**
 * Created by CAI on 2016/7/29.
 */
public class OrderListSwipeEvent {
    //订单刷新的position
    public int position;

    public OrderListSwipeEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
