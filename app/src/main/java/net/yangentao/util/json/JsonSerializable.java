package net.yangentao.util.json;

import org.json.JSONObject;

public interface JsonSerializable {
	public String toJson();

	public void fromJson(String json);

	public void fromJson(JSONObject json);
}
