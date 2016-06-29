package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;

import java.util.List;

public class ClassIDResult extends ResponseResult {
    /**
     * id : 531680A5
     * name : 化肥
     */

    public List<CategoriesEntity> categories;

    public static class CategoriesEntity {
        public String id;
        public String name;
    }
}
