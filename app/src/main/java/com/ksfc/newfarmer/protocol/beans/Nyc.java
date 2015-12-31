/**
 * 
 */
package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.GetGoodsData.SingleGood;

/**
 * 项目名称：newFarmer 类名称：Nyc 类描述： 创建人：尚前琛 创建时间：2015-6-30 下午1:08:06 修改备注：
 */
public class Nyc extends ResponseResult {
	public List<SingleGood> list;
	public String type;
}
