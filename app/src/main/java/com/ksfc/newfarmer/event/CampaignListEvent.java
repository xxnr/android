package com.ksfc.newfarmer.event;

import com.ksfc.newfarmer.beans.CampaignListResult;

import java.util.List;

/**
 * Created by CAI on 2016/8/1.
 */
public class CampaignListEvent {

   public List<CampaignListResult.CampaignsBean> campaigns;

    public CampaignListEvent(List<CampaignListResult.CampaignsBean> campaigns) {
        this.campaigns = campaigns;
    }
}
