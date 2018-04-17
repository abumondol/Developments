package DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.ConstantUtil;
import utils.UtilFunctions;

public class MedicalDBActions {

	public static JSONObject loginCheck(Connection con, JSONObject json)
			throws Exception {

		PreparedStatement preparedStatement = con
				.prepareStatement("select * from user_info where user_code = ? and password = ?");
		preparedStatement.setString(1, json.getString("user_code"));
		preparedStatement.setString(2, json.getString("password"));
		ResultSet resultSet = preparedStatement.executeQuery();
		JSONObject response_json = new JSONObject();

		if (resultSet.next()) {

			// response_code: 100 -> login ok
			response_json.put(ConstantUtil.JSON_RESPONSE_CODE, 100);

			int caregiver_id = resultSet.getInt("id");
			UtilFunctions.close(preparedStatement);
			UtilFunctions.close(resultSet);

			response_json.put("user_info", getUserInfo(con, caregiver_id));
			response_json.put("protocol_list", getProtocolList(con));
		} else {
			// response code: 0 -> user_id/password not matched
			response_json.put(ConstantUtil.JSON_RESPONSE_CODE, 0);
		}

		return response_json;
	}

	public static JSONObject mrnSearch(Connection con, JSONObject json)
			throws Exception {
		String mrn = json.getString("mrn");
		JSONObject response_json = getPatientInfo(con, mrn);

		if (response_json == null) {
			response_json = new JSONObject();
			response_json.put(ConstantUtil.JSON_RESPONSE_CODE, 0);
		} else {
			response_json.put(ConstantUtil.JSON_RESPONSE_CODE, 1);
		}

		return response_json;
	}

	public static JSONArray getProtocolList(Connection con) throws Exception {

		PreparedStatement preparedStatement = con
				.prepareStatement("select * from protocols");
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONArray protocol_list = new JSONArray();

		JSONObject json;
		int protocol_id;
		JSONArray step_list;
		while (resultSet.next()) {
			json = new JSONObject();

			protocol_id = resultSet.getInt("id");
			json.put("id", protocol_id);
			UtilFunctions.putJsonString(json, "name",
					resultSet.getString("name"));
			UtilFunctions.putJsonString(json, "description",
					resultSet.getString("description"));

			step_list = getStepList(con, protocol_id);
			json.put("step_list", step_list);
			protocol_list.put(json);
		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);
		// System.out.println("Total protocols:"+protocol_list.toString());
		return protocol_list;
	}

	public static JSONArray getStepList(Connection con, int protocol_id)
			throws Exception {

		PreparedStatement preparedStatement = con
				.prepareStatement("select * from steps where protocol_id=? order by serial");
		preparedStatement.setInt(1, protocol_id);
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONArray step_list = new JSONArray();
		JSONObject json;
		while (resultSet.next()) {
			json = new JSONObject();

			json.put("id", resultSet.getInt("id"));
			json.put("serial", resultSet.getInt("serial"));
			json.put("protocol_id", resultSet.getInt("protocol_id"));
			UtilFunctions.putJsonString(json, "name",
					resultSet.getString("name"));
			UtilFunctions.putJsonString(json, "description",
					resultSet.getString("description"));
			json.put("interval", resultSet.getInt("interval"));
			json.put("yellow_time", resultSet.getInt("yellow_time"));
			json.put("yellow_alarm_id", resultSet.getInt("yellow_alarm_id"));
			json.put("red_alarm_id", resultSet.getInt("red_alarm_id"));
			step_list.put(json);
		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);
		return step_list;
	}

	public static JSONArray getPatientList(Connection con, int caregiver_id)

	throws Exception {
		PreparedStatement preparedStatement = con
				.prepareStatement("select * from diagnose where caregiver_id = ? ");
		preparedStatement.setInt(1, caregiver_id);
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONArray patient_list = new JSONArray();
		JSONObject json;
		while (resultSet.next()) {
			json = new JSONObject();
			json.put("adm_id", resultSet.getInt("id"));
			json.put("serial", resultSet.getInt("serial"));
			json.put("name", resultSet.getString("name"));
			json.put("description", resultSet.getString("description"));
			json.put("interval", resultSet.getInt("interval"));
			json.put("yellow_time", resultSet.getInt("yellow_time"));
			json.put("yellow_alarm_id", resultSet.getInt("yellow_alarm_id"));
			json.put("red_alarm_id", resultSet.getInt("red_alarm_id"));
			patient_list.put(json);
		}

		if (patient_list.length() == 0)
			return null;

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);
		return patient_list;

	}

	public static JSONObject getPatientInfo(Connection con, String mrn)
			throws Exception {
		PreparedStatement preparedStatement = con
				.prepareStatement("select * from patient_info where mrn = ?");
		preparedStatement.setString(1, mrn);
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONObject json = null;
		if (resultSet.next()) {
			json = new JSONObject();
			json.put("id", resultSet.getInt("id"));
			json.put("first_name", resultSet.getString("first_name"));
			json.put("last_name", resultSet.getString("last_name"));
			json.put("dob", resultSet.getString("dob"));
			json.put("gender", resultSet.getString("gender"));
			json.put("mrn", resultSet.getString("mrn"));
		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);

		return json;
	}

	public static JSONObject getUserInfo(Connection con, int user_id)
			throws Exception {
		PreparedStatement preparedStatement = con
				.prepareStatement("select * from user_info where id = ? ");
		preparedStatement.setInt(1, user_id);
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONObject json = null;
		if (resultSet.next()) {
			json = new JSONObject();
			json.put("id", resultSet.getInt("id"));
			json.put("first_name", resultSet.getString("first_name"));
			json.put("last_name", resultSet.getString("last_name"));
			json.put("gender", resultSet.getString("gender"));
			json.put("designation", resultSet.getString("designation"));
		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);
		return json;
	}

}
