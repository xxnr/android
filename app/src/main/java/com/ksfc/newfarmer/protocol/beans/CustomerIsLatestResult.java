package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * Created by CAI on 2016/4/11.
 */
public class CustomerIsLatestResult extends ResponseResult {

    /**
     * count : 5   报备人数
     * countLeftToday : 15 今日可添加人数
     * needUpdate : 1  是否需要更新，1需要 0不需要
     */

    public int count;
    public int countLeftToday;
    public int needUpdate;

}
