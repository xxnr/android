package net.yangentao.util;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class HttpUtil {
	public static final String UTF8 = "UTF-8";

	public static final int TIMEOUT_DEFAULT = 10000;// 10 seconds

	public static interface HttpGetProgress {
		public void onProgress(int percent, String url, Object tag);
	}

	public static boolean httpGetSaveToFile(String url, File file) {
		InputStream is = httpGetStream(url);
		XLog.i("调用" + "httpGetSaveToFile()" + is);
		return Util.saveStreamToFile(is, file);
	}

	public static String httpGetTextUtf8(String url) {
		return httpGetText(url, UTF8);
	}

	public static String httpGetTextUtf8(String url, String argName,
			String argValue) {
		List<NameValuePair> args = new ArrayList<>();
		args.add(new BasicNameValuePair(argName, argValue));
		return httpGetTextUtf8(url, args);
	}

	public static String httpGetTextUtf8(String url, List<NameValuePair> args) {
		if (Util.notEmpty(args)) {
			StrBuilder sb = new StrBuilder(args.size() * 32);
			for (NameValuePair p : args) {
				try {
					String name = URLEncoder.encode(p.getName(), UTF8);
					String val = URLEncoder.encode(p.getValue(), UTF8);
					if (sb.isEmpty()) {
						sb.append("?");
					} else {
						sb.append("&");
					}
					sb.append(name, "=", val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			url = url + sb.toString();
		}
		return httpGetTextUtf8(url);
	}

	public static String httpGetText(String url, String encoding) {
		InputStream is = null;
		try {
			is = httpGetStream(url);
			return Util.saveStreamToString(is, encoding);
		} catch (Exception e1) {
			XLog.e(e1);
		}
		return null;
	}

	public static byte[] httpGetBytes(String url) {
		InputStream is = null;
		try {
			is = httpGetStream(url);
			return Util.saveStreamToBytes(is);
		} catch (Exception e1) {
			XLog.e(e1);
		}
		return null;
	}

	public static InputStream httpGetStream(String url) {
		try {
			HttpResponse r = httpGetResponseOK(url);
			if (r != null) {
				return r.getEntity().getContent();
			}
		} catch (Exception e1) {
			XLog.e(e1);
		}
		return null;
	}

	/**
	 * @param url
	 * @return code在[200,299]以内时返回相应, 否则返回null
	 */
	public static HttpResponse httpGetResponseOK(String url) {
		HttpClient client = getHttpClient();
		HttpGet req = new HttpGet(url);
		XLog.d("HTTP GET: " + url);
		try {
			HttpResponse response = client.execute(req);
			int code = response.getStatusLine().getStatusCode();
			XLog.d("code:" + response.getStatusLine().getStatusCode() + " "
					+ response.getStatusLine().getReasonPhrase());
			if (code >= 200 && code <= 299) {
				return response;
			}
		} catch (Exception e1) {
			XLog.e(e1);
		}
		return null;

	}

	public static InputStream httpPostStream(String url,
			List<NameValuePair> nameValuePairs) {
		HttpClient client = getHttpClient();
		HttpPost httpRequest = new HttpPost(url);
		XLog.d("HTTP POST: " + url);
		try {
			if (nameValuePairs != null) {
				for (NameValuePair p : nameValuePairs) {
					XLog.d(p.getName(), "=", p.getValue());
				}

				httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						HTTP.UTF_8));
			}
			HttpResponse response = client.execute(httpRequest);
			int code = response.getStatusLine().getStatusCode();
			XLog.d("code:" + response.getStatusLine().getStatusCode() + " "
					+ response.getStatusLine().getReasonPhrase());
			if (code >= 200 && code <= 299) {
				return response.getEntity().getContent();
			}
		} catch (Exception e) {
			XLog.e(e);
		}
		return null;
	}

	/**
	 * post数据到服务器
	 */
	public static byte[] httpPostBytes(String url,
			List<NameValuePair> nameValuePairs) {
		try {
			InputStream is = httpPostStream(url, nameValuePairs);
			return Util.saveStreamToBytes(is);
		} catch (Exception e) {
			XLog.e(e);
		}
		return null;
	}

	public static String httpPostText(String url,
			List<NameValuePair> nameValuePairs, String encoding) {
		try {
			InputStream is = httpPostStream(url, nameValuePairs);
			if (is == null) {
				return null;
			}
			return Util.saveStreamToString(is, encoding);
		} catch (Exception e) {
			XLog.e(e);
		}
		return null;
	}

	public static String httpPostTextUtf8(String url,
			List<NameValuePair> nameValuePairs) {
		return httpPostText(url, nameValuePairs, UTF8);
	}

	public static String httpPostTextUtf8(String url, String argName,
			String argValue) {
		List<NameValuePair> args = new ArrayList<>();
		args.add(new BasicNameValuePair(argName, argValue));
		return httpPostTextUtf8(url, args);
	}

	public static String httpPostFileUTF8(String url, File file) {
		HttpClient client = getHttpClient();
		HttpPost httpRequest = new HttpPost(url);
		XLog.d("HTTP POST: " + url);
		try {
			FileEntity fe = new FileEntity(file, "binary/octet-stream");
			httpRequest.setEntity(fe);
			HttpResponse response = client.execute(httpRequest);
			int code = response.getStatusLine().getStatusCode();
			XLog.d("code:" + response.getStatusLine().getStatusCode() + " "
					+ response.getStatusLine().getReasonPhrase());
			if (code >= 200 && code <= 299) {
				InputStream is = response.getEntity().getContent();
				return Util.saveStreamToString(is, UTF8);
			}
		} catch (Exception e) {
			XLog.e(e);
		}
		return null;
	}

	private static HttpClient httpClient;

	private static int timeout = TIMEOUT_DEFAULT;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int millSeconds) {
		timeout = millSeconds;
		if (timeout < 10000) {
			timeout = 10000;
		}
	}

	private static HttpClient getHttpClient() {
		if (httpClient == null) {
			synchronized (HttpUtil.class) {
				if (httpClient == null) {
					HttpParams params = new BasicHttpParams();
					HttpConnectionParams
							.setConnectionTimeout(params, 10 * 1000);
					HttpConnectionParams.setSoTimeout(params, timeout);
					httpClient = new DefaultHttpClient(params);
				}
			}
		}

		return httpClient;
	}
}
