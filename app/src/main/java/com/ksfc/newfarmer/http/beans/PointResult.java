/**
 *
 */
package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;

import java.io.Serializable;

/**
 * 项目名称：newFarmer 类名称：PointResult 类描述： 签到接口 创建人：周春肖 创建时间：2015-7-10 下午2:50:35
 * 修改备注：
 */
public class PointResult extends ResponseResult implements Serializable{
    public int pointAdded;
    public int consecutiveTimes;
}
