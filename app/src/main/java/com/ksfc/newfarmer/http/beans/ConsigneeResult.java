package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;

import java.util.List;

/**
 * Created by HePeng on 2016/3/18.
 */
public class ConsigneeResult  extends ResponseResult {


    /**
     * total : 2
     * rows : [{"userId":"f4fe7be34b8440ef9a1f8179b5faaad2","consigneeName":"呵呵","consigneePhone":"13271788771","dateCreated":"2016-03-17T10:44:53.627Z"},{"userId":"f4fe7be34b8440ef9a1f8179b5faaad2","consigneeName":"何鹏","consigneePhone":"13271788771","dateCreated":"2016-03-17T08:26:04.096Z"}]
     * page : 1
     * pages : 1
     */

    public DatasEntity datas;

    public static class DatasEntity {
        public int total;
        public int page;
        public int pages;
        /**
         * userId : f4fe7be34b8440ef9a1f8179b5faaad2
         * consigneeName : 呵呵
         * consigneePhone : 13271788771
         * dateCreated : 2016-03-17T10:44:53.627Z
         */

        public List<RowsEntity> rows;


        public static class RowsEntity {
            public String userId;
            public String consigneeName;
            public String consigneePhone;
            public String dateCreated;

        }
    }
}
