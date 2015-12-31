package net.yangentao.util.json;

import org.json.JSONObject;

public class JsonModel implements JsonSerializable {

	@Override
	public void fromJson(String json) {
		try {
			Json.Obj.fromJ(json, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fromJson(JSONObject json) {
		try {
			Json.Obj.fromJ(json, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toJson() {
		try {
			return Json.Obj.toJ(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
