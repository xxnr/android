package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by CAI on 2016/6/17.
 */
public class PointLogsResult extends ResponseResult {


    /**
     * pointslogs : [{"points":2,"description":"2016-06-16签到","event":{"type":2,"name":"每日签到"},"date":"2016-06-16T05:58:41.356Z"}]
     * total : 1
     * pages : 1
     * page : 0
     */

    public DatasBean datas;

    public static class DatasBean {
        public int total;
        public int pages;
        public int page;
        /**
         * points : 2
         * description : 2016-06-16签到
         * event : {"type":2,"name":"每日签到"}
         * date : 2016-06-16T05:58:41.356Z
         */

        public List<PointslogsBean> pointslogs;

        public static class PointslogsBean {
            public int points;
            public String description;
            /**
             * type : 2
             * name : 每日签到
             */

            public EventBean event;
            public String date;

            public static class EventBean {
                public int type;
                public String name;
            }
        }
    }
}
