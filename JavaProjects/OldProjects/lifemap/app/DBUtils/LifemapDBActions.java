package DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import sun.reflect.generics.visitor.Reifier;
import utils.ActivityEntity;
import utils.DateTimeUtil;
import utils.UtilFunctions;

public class LifemapDBActions {

	public static int checkLogin(Connection connection, String user_code,
			String password) throws Exception {

		int result = -1;
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from lifemap_users where user_code = ? and password = ?");
		preparedStatement.setString(1, user_code);
		preparedStatement.setString(2, password);
		ResultSet resultSet = preparedStatement.executeQuery();

		if (resultSet.next()) {
			return resultSet.getInt("id");
		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);

		return result;
	}

	
	
	public static JSONObject getSettings(Connection connection, int user_id)
			throws Exception {
		JSONObject settings = new JSONObject();
		JSONObject userJson = getBlankSettings();

		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from lifemap_settings where user_id=?");
		preparedStatement.setInt(1, user_id);
		ResultSet resultSet = preparedStatement.executeQuery();

		if (resultSet.next())
			userJson = new JSONObject(resultSet.getString("settings"));

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		settings.put("gen", getGeneralSettings());
		settings.put("user", userJson);

		JSONObject responseJSON = new JSONObject();
		responseJSON.put("settings", settings);
		return responseJSON;
	}

	public static JSONObject saveSettings(Connection connection, int user_id,
			String settings) throws Exception {

		PreparedStatement preparedStatement = connection
				.prepareStatement("INSERT INTO lifemap_settings (user_id, settings) VALUES(?, ?) ON DUPLICATE KEY UPDATE settings=?");
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, settings);
		preparedStatement.setString(3, settings);
		preparedStatement.execute();
		UtilFunctions.close(preparedStatement);

		return new JSONObject();

	}

	public static JSONObject getDailyData(Connection connection, int user_id,
			String date) throws Exception {

		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from lifemap_daily_data where user_id = ? and date = ?");
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, date);
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONObject responseJson = new JSONObject();

		if (resultSet.next()) {
			responseJson.put("daily_data",
					new JSONObject(resultSet.getString("daily_data")));
		} else {
			responseJson.put("daily_data", getBlankDailyData());
		}

		responseJson.put("date", date);
		responseJson.put("user_id", user_id);

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);
		return responseJson;
	}
	
	public static JSONObject getDailyDataOther(Connection connection, JSONObject json) throws Exception {

		int user_id = checkLogin(connection, json.getString("user_code"), json.getString("password"));
		System.out.println("User_id: "+user_id);
		if(user_id <0)
			return null;
		
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from lifemap_daily_data_other where user_id = ? and date = ? and data_type=?");
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, json.getString("date"));
		preparedStatement.setString(3, json.getString("data_type"));
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONObject responseJson = new JSONObject();
		responseJson.put("user_code", json.getString("user_code"));
		responseJson.put("date", json.getString("date"));
		responseJson.put("data_type", json.getString("data_type"));
		JSONArray jsonArray = new JSONArray();

		while (resultSet.next()) {
			jsonArray.put(new JSONObject(resultSet.getString("data")));			
		} 
		responseJson.put("count", jsonArray.length());
		responseJson.put("data", jsonArray);
		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);
		return responseJson;
	}

	public static JSONObject saveDailyData(Connection connection, int user_id,
			String date, String daily_data) throws Exception {

		PreparedStatement preparedStatement;
		/*preparedStatement = connection
				.prepareStatement("insert into lifemap_daily_data_all (user_id, date, daily_data) select * from lifemap_daily_data where user_id=? and date=?");
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, date);
		preparedStatement.execute();
		UtilFunctions.close(preparedStatement);*/

		preparedStatement = connection
				.prepareStatement("INSERT INTO lifemap_daily_data (user_id, date,  daily_data) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE daily_data=?");
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, daily_data);
		preparedStatement.setString(4, daily_data);
		preparedStatement.execute();
		UtilFunctions.close(preparedStatement);

		return new JSONObject();
	}

	public static JSONObject saveDailyDataOther(Connection connection, JSONObject json) throws Exception {

		PreparedStatement preparedStatement;
		
		int user_id = checkLogin(connection, json.getString("user_code"), json.getString("password"));
		System.out.println("User_id: "+user_id);
		if(user_id <0)
			return null;

		preparedStatement = connection
				.prepareStatement("INSERT INTO lifemap_daily_data_other (user_id, date,  data_type, data) VALUES(?, ?, ?, ?)");
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, json.getString("date"));
		preparedStatement.setString(3, json.getString("data_type"));
		preparedStatement.setString(4, json.getJSONObject("data").toString());
		preparedStatement.execute();
		UtilFunctions.close(preparedStatement);
		
		return new JSONObject();
	}
	
	
	public static JSONObject getGeneralSettings() throws Exception {
		JSONObject gen = new JSONObject();
		JSONArray activityTypeArray = new JSONArray();
		JSONArray activityArray = new JSONArray();
		JSONArray locationArray = new JSONArray();
		JSONArray peopleArray = new JSONArray();
		JSONArray strandArray = new JSONArray();

		activityTypeArray.put(" ");
		activityTypeArray.put("Continuing");
		activityTypeArray.put("Repeating");
		activityTypeArray.put("Unique");
		activityTypeArray.put("Travel");

		activityArray.put("Wake up");
		activityArray.put("Lunch");
		activityArray.put("Dinner");
		activityArray.put("Study");

		locationArray.put("Home");
		
		// peopleArray.put("Self");

		gen.put("type", activityTypeArray);
		gen.put("activity", activityArray);
		gen.put("location", locationArray);
		gen.put("people", peopleArray);
		gen.put("strand", strandArray);

		return gen;
	}

	public static JSONObject getBlankSettings() throws Exception {
		JSONObject json = new JSONObject();
		JSONArray activityArray = new JSONArray();
		JSONArray locationArray = new JSONArray();
		JSONArray peopleArray = new JSONArray();
		JSONArray strandArray = new JSONArray();		

		json.put("activity", activityArray);
		json.put("location", locationArray);
		json.put("people", peopleArray);
		json.put("strand", strandArray);

		return json;
	}

	public static JSONObject getBlankDailyData() throws Exception {
		JSONObject daily_data = new JSONObject();

		JSONArray actArray = new JSONArray();
		JSONArray diaryArray = new JSONArray();		
		
		//JSONArray strandArray = new JSONArray();
		//strandArray.put("");

		JSONObject actItem = new JSONObject();
		actItem.put("start_time", 9999);
		actItem.put("end_time", 0);
		actItem.put("activity_type", "");
		actItem.put("activity", "Wake up");
		actItem.put("location", "");
		actItem.put("people", "");
		actItem.put("strand", "");
		actArray.put(actItem);

		JSONObject diaryItem = new JSONObject();
		diaryItem.put("description", "");
		diaryArray.put(diaryItem);

		daily_data.put("table_activity_list", actArray);
		daily_data.put("table_diary_list", diaryArray);

		return daily_data;
	}

	/*************************** SWAD *****************************/
	public static JSONObject getSettingsSwad(Connection connection)
			throws Exception {
		JSONObject json = new JSONObject();
		JSONArray subjectArray = new JSONArray();
		JSONArray activityArray = new JSONArray();

		JSONObject ob;

		/************** Subjects ****************/
		ob = new JSONObject();
		ob.put("id", 0);
		ob.put("name", "All");
		subjectArray.put(ob);

		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from subject_list where listed='Y' order by serial");
		ResultSet resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			ob = new JSONObject();
			ob.put("id", resultSet.getInt("id"));
			ob.put("name", resultSet.getString("name"));
			subjectArray.put(ob);
		}

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		/************** Activity List ****************/
		ob = new JSONObject();
		ob.put("id", 0);
		ob.put("name", "All");
		activityArray.put(ob);
		preparedStatement = connection
				.prepareStatement("select * from activity_list where listed='Y' order by serial");
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			ob = new JSONObject();
			ob.put("id", resultSet.getInt("id"));
			ob.put("name", resultSet.getString("activity"));
			activityArray.put(ob);
		}

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		json.put("subject_list", subjectArray);
		json.put("activity_list", activityArray);

		return json;
	}

	public static JSONObject getDataSwad(Connection connection, int subject,
			int activity, String date) throws Exception {

		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from lifemap_daily_data where user_id = ? and date = ?");
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONObject responseJson = new JSONObject();

		if (resultSet.next()) {
			responseJson.put("daily_data",
					new JSONObject(resultSet.getString("daily_data")));
		}

		responseJson.put("date", date);

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);
		return responseJson;
	}

}
