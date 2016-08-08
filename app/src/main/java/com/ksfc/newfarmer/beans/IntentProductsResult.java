package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by CAI on 2016/8/5.
 */
public class IntentProductsResult extends ResponseResult{


    /**
     * brand : 江淮
     * products : [{"name":"和悦A30","_id":"56a72696c1813a7c109138c3"},{"name":"全新和悦","_id":"56a72696c1813a7c109138c4"},{"name":"瑞风S3","_id":"56a72696c1813a7c109138c5"},{"name":"瑞风S2","_id":"56a72696c1813a7c109138c6"},{"name":"瑞风S5","_id":"56a72696c1813a7c109138c7"},{"name":"瑞风M2","_id":"56a72696c1813a7c109138c8"},{"name":"瑞风M3","_id":"56a72696c1813a7c109138c9"}]
     */

    public List<IntentionProductsBean> intentionProducts;

    public static class IntentionProductsBean {
        public String brand;
        /**
         * name : 和悦A30
         * _id : 56a72696c1813a7c109138c3
         */

        public List<ProductsBean> products;

        public static class ProductsBean {
            public String name;
            public String _id;
        }
    }
}
