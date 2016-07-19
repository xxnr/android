package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HePeng on 2016/1/12.
 */
public class RemainGoodsAttr extends ResponseResult {

    public Data data;

    public class Data implements Serializable {
        public List<Additions> additions;
        public SKU SKU;

        public class Additions implements Serializable {

            public String ref;
            public String price;
            public String name;

        }

        public Price price;
        public Market_price market_price;


        public class Price implements Serializable {

            public String min;
            public String max;
        }

        public class Market_price implements Serializable {

            public String min;
            public String max;
        }

        public List<Attributes> attributes;

        public class Attributes implements Serializable {

            public String name;
            public List<String> values;
        }

        public class SKU implements Serializable {
            public String _id;

        }


    }


}
