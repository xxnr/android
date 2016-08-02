package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CAI on 2016/7/25.
 */
public class CampaignListResult extends ResponseResult  implements Serializable{

    /**
     * _id : 57959892a91948c547fd699d
     * type : 2
     * title : 测试答题标题
     * online_time : 2016-07-25T02:32:01.459Z
     * start_time : 2016-07-25T03:32:01.459Z
     * campaign_url_name : testcampaign
     * comment : 测试备注2
     * share_points_add : 10
     * image : /images/original/14494789282199y4rhpvi.jpg
     * share_title : 分享活动标题2
     * share_url_name : testcampaignshare
     * share_image : /images/original/14494789282199y4rhpvi.jpg
     * share_abstract : testshareabstract2
     * url : /campaigns/QAs/testcampaign
     * share_url : /campaigns/QAs/testcampaignshare
     * __v : 0
     * share_button : true
     * shareable : true
     * reward_times : 1
     * date_created : 2016-07-25T04:41:54.087Z
     */

    public List<CampaignsBean> campaigns;

    public static class CampaignsBean implements Serializable{
        public String _id;
        public int type;
        public String title;
        public String online_time;
        public String start_time;
        public String campaign_url_name;
        public String comment;
        public int share_points_add;
        public String image;
        public String share_title;
        public String share_url_name;
        public String share_image;
        public String share_abstract;
        public String url;
        public String share_url;
        public boolean share_button;
        public boolean shareable;
        public int reward_times;
        public String date_created;
    }
}
