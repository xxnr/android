package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by HePeng on 2016/3/15.
 */
public class RSCAddressListResult extends ResponseResult {
    public List<ProvinceListEntity> provinceList;
    public List<CityListEntity> cityList;
    public List<CountyListEntity> countyList;

    public static class ProvinceListEntity {
        public String _id;
        public String name;
    }


    public static class CityListEntity {
        public String _id;
        public String name;
    }

    public static class CountyListEntity {
        public String _id;
        public String name;
    }


}
