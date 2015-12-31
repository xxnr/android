package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

public class InformationResult extends ResponseResult {

	/**
	 * code : 1000 message : success datas : {"count":8,"items":[{"image":
	 * "http://192.168.1.15/images/original/1446616617612m3d6lxr.jpg"
	 * ,"category":
	 * "农业新闻","title":"测试上图片","datecreated":"2015-11-01T11:30:23.625Z"
	 * ,"url":"http://192.168.1.15/article/ec091d8bf4aaa52c44378"},{"image":
	 * "http://192.168.1.15/images/original/144637713009953a0pb9.jpg"
	 * ,"category":
	 * "公司动态","title":"测试资讯修改","datecreated":"2015-11-01T11:26:29.933Z"
	 * ,"url":"http://192.168.1.15/article/2b6ad947d2731b3088655"},{"image":
	 * "http://192.168.1.15/images/original/1446376511122vc102j4i.jpg"
	 * ,"category"
	 * :"公司动态","title":"测试资讯内容1","datecreated":"2015-11-01T11:19:42.845Z"
	 * ,"url":"http://192.168.1.15/article/4f2975821fa34a5eb118b"},{"image":
	 * "http://192.168.1.15/images/original/1446630315168sg4lsor.jpg"
	 * ,"category":"新农活动","title":"【北京新新农人研究院】信息周报 2015.10.19\u20142015.10.25",
	 * "datecreated":"2015-11-04T09:46:04.598Z","url":
	 * "http://192.168.1.15/article/505360e970375c4b49195"},{"image":
	 * "http://192.168.1.15/images/original/144662981834747gjsjor.jpg"
	 * ,"category"
	 * :"新农活动","title":"【北京新新农人研究院】郑州共青团\u201c互联网+农业\u201d主题公益培训精彩内容回放"
	 * ,"datecreated":"2015-11-04T09:42:46.951Z","url":
	 * "http://192.168.1.15/article/736bea418daeb536c82ec"},{"image":
	 * "http://192.168.1.15/images/original/14466296276793gadobt9.jpg"
	 * ,"category":"公司动态","title":"河南日报：新农人网络科技有限公司打造新新农人网络平台","datecreated":
	 * "2015-11-04T09:34:20.387Z"
	 * ,"url":"http://192.168.1.15/article/ba7bffac1735bd1d36d8d"},{"image":
	 * "http://192.168.1.15/images/original/14466295568686snstt9.jpg"
	 * ,"category":"农业新闻","title":"【营销策略】应对赊销有对策，此后再不宽容","datecreated":
	 * "2015-11-04T09:32:56.382Z"
	 * ,"url":"http://192.168.1.15/article/206f6ec3f2b62cac7cae3"},{"image":
	 * "http://192.168.1.15/images/original/1446629414810mv5f80k9.jpg"
	 * ,"category":"农业新闻","title":"【市场行情】尿素行情再现内忧外患","datecreated":
	 * "2015-11-04T09:30:48.451Z"
	 * ,"url":"http://192.168.1.15/article/2295a06592e923e130f4b"
	 * }],"pages":1,"page":1}
	 */

	private String code;
	private String message;
	/**
	 * count : 8 items : [{"image":
	 * "http://192.168.1.15/images/original/1446616617612m3d6lxr.jpg"
	 * ,"category":
	 * "农业新闻","title":"测试上图片","datecreated":"2015-11-01T11:30:23.625Z"
	 * ,"url":"http://192.168.1.15/article/ec091d8bf4aaa52c44378"},{"image":
	 * "http://192.168.1.15/images/original/144637713009953a0pb9.jpg"
	 * ,"category":
	 * "公司动态","title":"测试资讯修改","datecreated":"2015-11-01T11:26:29.933Z"
	 * ,"url":"http://192.168.1.15/article/2b6ad947d2731b3088655"},{"image":
	 * "http://192.168.1.15/images/original/1446376511122vc102j4i.jpg"
	 * ,"category"
	 * :"公司动态","title":"测试资讯内容1","datecreated":"2015-11-01T11:19:42.845Z"
	 * ,"url":"http://192.168.1.15/article/4f2975821fa34a5eb118b"},{"image":
	 * "http://192.168.1.15/images/original/1446630315168sg4lsor.jpg"
	 * ,"category":"新农活动","title":"【北京新新农人研究院】信息周报 2015.10.19\u20142015.10.25",
	 * "datecreated":"2015-11-04T09:46:04.598Z","url":
	 * "http://192.168.1.15/article/505360e970375c4b49195"},{"image":
	 * "http://192.168.1.15/images/original/144662981834747gjsjor.jpg"
	 * ,"category"
	 * :"新农活动","title":"【北京新新农人研究院】郑州共青团\u201c互联网+农业\u201d主题公益培训精彩内容回放"
	 * ,"datecreated":"2015-11-04T09:42:46.951Z","url":
	 * "http://192.168.1.15/article/736bea418daeb536c82ec"},{"image":
	 * "http://192.168.1.15/images/original/14466296276793gadobt9.jpg"
	 * ,"category":"公司动态","title":"河南日报：新农人网络科技有限公司打造新新农人网络平台","datecreated":
	 * "2015-11-04T09:34:20.387Z"
	 * ,"url":"http://192.168.1.15/article/ba7bffac1735bd1d36d8d"},{"image":
	 * "http://192.168.1.15/images/original/14466295568686snstt9.jpg"
	 * ,"category":"农业新闻","title":"【营销策略】应对赊销有对策，此后再不宽容","datecreated":
	 * "2015-11-04T09:32:56.382Z"
	 * ,"url":"http://192.168.1.15/article/206f6ec3f2b62cac7cae3"},{"image":
	 * "http://192.168.1.15/images/original/1446629414810mv5f80k9.jpg"
	 * ,"category":"农业新闻","title":"【市场行情】尿素行情再现内忧外患","datecreated":
	 * "2015-11-04T09:30:48.451Z"
	 * ,"url":"http://192.168.1.15/article/2295a06592e923e130f4b"}] pages : 1
	 * page : 1
	 */

	private DatasEntity datas;

	public void setCode(String code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDatas(DatasEntity datas) {
		this.datas = datas;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public DatasEntity getDatas() {
		return datas;
	}

	public static class DatasEntity {
		private int count;
		private int pages;
		private int page;
		/**
		 * image : http://192.168.1.15/images/original/1446616617612m3d6lxr.jpg
		 * category : 农业新闻 title : 测试上图片 datecreated : 2015-11-01T11:30:23.625Z
		 * url : http://192.168.1.15/article/ec091d8bf4aaa52c44378
		 */

		private List<ItemsEntity> items;

		public void setCount(int count) {
			this.count = count;
		}

		public void setPages(int pages) {
			this.pages = pages;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public void setItems(List<ItemsEntity> items) {
			this.items = items;
		}

		public int getCount() {
			return count;
		}

		public int getPages() {
			return pages;
		}

		public int getPage() {
			return page;
		}

		public List<ItemsEntity> getItems() {
			return items;
		}

		public static class ItemsEntity {
			private String image;
			private String category;
			private String title;
			private String datecreated;
			private String url;

			public void setImage(String image) {
				this.image = image;
			}

			public void setCategory(String category) {
				this.category = category;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public void setDatecreated(String datecreated) {
				this.datecreated = datecreated;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getImage() {
				return image;
			}

			public String getCategory() {
				return category;
			}

			public String getTitle() {
				return title;
			}

			public String getDatecreated() {
				return datecreated;
			}

			public String getUrl() {
				return url;
			}
		}
	}
}
