package Web;

import org.json.JSONObject;

import DBUtils.MobileDBActions;
import DBUtils.MobileMySQLInstance;

public class ProcessMobileWebReuqest {

	public static JSONObject processRequest(JSONObject json, int user_id)
			throws Exception {
		JSONObject responseJson = null;
		String request_type;
		try {			
			request_type = json.getString("request_type");
			MobileMySQLInstance msi = MobileMySQLInstance.getInstance();

			if (request_type.equals("settings")) {
				if (user_id != 100)
					responseJson = MobileDBActions.getSettings(
							msi.getConnection(), user_id);
				else
					responseJson = MobileDBActions.getSettingsAdmin(
							msi.getConnection(), user_id);

			} else if (request_type.equals("data")) {
				int sub_id = json.getInt("sub_id");
				int act_id = json.getInt("act_id");
				int pos_id = json.getInt("pos_id");
				JSONObject settings = json.getJSONObject("settings");
				responseJson = MobileDBActions.getData(msi.getConnection(),
						user_id, sub_id, act_id, pos_id, settings);

			} else if (request_type.equals("delete")) {
				JSONObject obj = json.getJSONObject("obj");
				responseJson = MobileDBActions.deleteData(msi.getConnection(),
						user_id, obj);

			}

			if (responseJson != null)
				responseJson.put("response_code", 1);
		} catch (Exception ex) {
			System.out.println("Exception in processRequest: " + ex.toString());
			return null;
		}

		return responseJson;
	}

}
