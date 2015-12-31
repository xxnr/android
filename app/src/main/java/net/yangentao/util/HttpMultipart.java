package net.yangentao.util;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpMultipart {
	private MultipartEntity entities = new MultipartEntity();
	private int timeout = 30 * 1000;// 上传文件, 超时大点

	public HttpMultipart() {

	}

	/**
	 * 设置超时时间
	 * 
	 * @param millSeconds
	 *            毫秒, 最小10秒(10000毫秒)
	 */
	public void setTimeout(int millSeconds) {
		this.timeout = millSeconds;
		if (this.timeout < 10 * 1000) {
			this.timeout = 10 * 1000;
		}
	}

	// UTF-8, text/plain
	public void addStringBody(String key, String content) {
		addStringBody(key, content, Charset.forName(HttpUtil.UTF8));
	}

	public void addStringBody(String key, String content, Charset charset) {
		addStringBody(key, content, Charset.forName(HttpUtil.UTF8),
				"text/plain");
	}

	public void addStringBody(String key, String content, Charset charset,
			String mimeType) {
		try {
			StringBody body = new StringBody(content, mimeType, charset);
			entities.addPart(key, body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void addFileBody(String key, File file) {
		addFileBody(key, file, "application/octet-stream", file.getName());
	}

	public void addFileBody(String key, File file, String mimeType,
			String filename) {
		FileBody body = new FileBody(file, filename, mimeType, null);
		entities.addPart(key, body);
	}

	public void addByteArrayBody(String key, byte[] data) {
		addByteArrayBody(key, data, "application/octet-stream", "tempfile");
	}

	public void addByteArrayBody(String key, byte[] data, String mimeType,
			String filename) {
		ByteArrayBody body = new ByteArrayBody(data, mimeType, filename);
		entities.addPart(key, body);
	}

	public InputStream post(String url) {
		HttpClient client = getHttpClient(this.timeout);
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setEntity(entities);
		XLog.d("POST Multipart ", url);

		try {
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

	private static HttpClient getHttpClient(int timeoutMillSeconds) {
		if (timeoutMillSeconds < 10 * 1000) {
			timeoutMillSeconds = 10 * 1000;
		}
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		HttpConnectionParams.setSoTimeout(params, timeoutMillSeconds);
		HttpClient httpClient = new DefaultHttpClient(params);
		return httpClient;
	}

	public String postTextUtf8(String url) {
		try {
			InputStream is = post(url);
			if (is == null) {
				return null;
			}
			return Util.saveStreamToString(is, HttpUtil.UTF8);
		} catch (Exception e) {
			XLog.e(e);
		}
		return null;
	}
}
