package Web;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.DateTimeUtil;
import DBUtils.LifemapDBActions;
import DBUtils.LifemapMySQLInstance;

public class ProcessLifemapWebReuqest {

	public static JSONObject processRequest(JSONObject json, int user_id)
			throws Exception {
		JSONObject responseJson = null;
		String request_type;
		try {
			// json = new JSONObject(jsonStr);
			request_type = json.getString("request_type");
			LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();

			if (request_type.equals("settings")) {
				responseJson = LifemapDBActions.getSettings(msi.getConnection(),
						user_id);

			} else if (request_type.equals("save_settings")) {
				String settings = json.getJSONObject("settings").toString();
				responseJson = LifemapDBActions.saveSettings(msi.getConnection(),
						user_id, settings);

			} else if (request_type.equals("daily_data")) {
				String date = json.getString("date");
				responseJson = LifemapDBActions.getDailyData(msi.getConnection(),
						user_id, date);

			} else if (request_type.equals("save_daily_data")) {
				String date = json.getString("date");
				String daily_data = json.getJSONObject("daily_data").toString();
				responseJson = LifemapDBActions.saveDailyData(
						msi.getConnection(), user_id, date, daily_data);

			}else if(request_type.equals("data_push")){				
				responseJson = LifemapDBActions.saveDailyDataOther(
						msi.getConnection(), json);
			}else if(request_type.equals("data_pull")){				
				responseJson = LifemapDBActions.getDailyDataOther(
						msi.getConnection(), json);
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
