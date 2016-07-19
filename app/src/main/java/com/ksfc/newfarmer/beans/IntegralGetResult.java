package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * Created by CAI on 2016/6/17.
 */
public class IntegralGetResult extends ResponseResult {

    /**
     * score : 230
     * sign : {"date":"2016-06-16T05:58:41.000Z","consecutiveTimes":1}
     */

    public DatasBean datas;

    public static class DatasBean {
        public int score;
        /**
         * date : 2016-06-16T05:58:41.000Z
         * consecutiveTimes : 1
         */

        public SignBean sign;

        public static class SignBean {
            public String date;
            public int consecutiveTimes;
            public int signed;
            public String large_imgUrl;
        }
    }
}
