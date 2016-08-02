package com.ksfc.newfarmer.event;

import com.ksfc.newfarmer.beans.AddressList;

/**
 * Created by CAI on 2016/7/29.
 */
public class AddressChangeEvent {

    public AddressList.Address address;

    public AddressChangeEvent(AddressList.Address address) {
        this.address = address;
    }
}
