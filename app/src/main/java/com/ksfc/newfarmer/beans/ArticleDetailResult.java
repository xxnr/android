package com.ksfc.newfarmer.beans;

import com.google.gson.annotations.SerializedName;
import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * Created by CAI on 2016/8/3.
 */
public class ArticleDetailResult extends ResponseResult {

   /**
     * title : 新新农人携手中化化肥，助力传统农资行业转型升级；中国“互联网+农业”发展势不可挡，河南省推进“互联网+”行动，引领经济新常态
     * abstract : 中国“互联网+农业”发展势不可挡，河南省推进“互联网+”行动，引领经济新常态，农村“触网”前景十分广阔，BAT各大巨头纷纷布局，农村电商成为中国电商巨头“必争之地“。目前，农资产品、农产品营销体系中存在赊销严重、价格不透明、信息不对称、产业链条冗长等诸多难题，为了加快河南农村互联网发展步伐，推进中原农业现代化发展，河南新农人网络科技有限公司联合中化化肥，通过整合资源，优势互补，打通了供应链各环节，推动营销服务网络的转型升级。
     * image : http://101.200.194.203/images/original/14478291104632hrdlsor.jpg
     * url : http://101.200.194.203/news/2b6ad947d2731b3088655
     * shareurl : http://101.200.194.203/sharenews/2b6ad947d2731b3088655
     */

    public DatasBean datas;

    public static class DatasBean {
        public String title;
        @SerializedName("abstract")
        public String abstractX;
        public String image;
        public String url;
        public String shareurl;
    }
}
