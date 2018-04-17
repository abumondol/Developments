package DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.ActivityEntity;
import utils.ConstantUtil;
import utils.DateTimeUtil;
import utils.UtilFunctions;

public class DBActions {

	/****************************** all activities ****************************/
	public static ArrayList<ActivityEntity> getActivityMap(Connection connect)
			throws Exception {

		Statement statement = connect.createStatement();
		ResultSet resultSet = statement
				.executeQuery("select * from activity_data order by time");

		ArrayList<ActivityEntity> list = new ArrayList<ActivityEntity>();
		long time;
		long duration;
		int activity_id;
		String activity;
		while (resultSet.next()) {
			time = resultSet.getLong("time");
			duration = resultSet.getLong("duration");
			activity_id = resultSet.getInt("activity_id");
			activity = getActivityName(activity_id);
			duration /= 1000;
			String d = "" + duration / 60 + " min " + duration % 60 + " sec";

			// list.add(new ActivityEntity(DateTimeUtil.getTimeString(time, 2),
			// d, activity));
		}

		UtilFunctions.close(statement);
		UtilFunctions.close(resultSet);

		return list;
	}

	public static String getActivityName(int aid) {
		String str = "Undefined";
		switch (aid) {
		case 0:
			str = "Sedentary";
			break;
		case 2:
			str = "Walk";
			break;
		case 3:
			str = "Run";
			break;
		case 4:
			str = "Stair Up";
			break;
		case 5:
			str = "Stair Down";
			break;

		}
		return str;

	}

	/****************************** login check ****************************/
	public static String loginCheck(Connection connect, String email,
			String password) throws Exception {
		PreparedStatement preparedStatement = connect
				.prepareStatement("select * from user_info where email = ? and password = ?");
		preparedStatement.setString(1, email);
		preparedStatement.setString(2, password);
		ResultSet resultSet = preparedStatement.executeQuery();

		if (resultSet.next()) {
			int user_id = resultSet.getInt("user_id");

			UtilFunctions.close(preparedStatement);
			UtilFunctions.close(resultSet);

			JSONArray activityArray = new JSONArray();
			JSONArray positionArray = new JSONArray();
			JSONArray subjectArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			JSONObject jsonTemp;

			preparedStatement = connect
					.prepareStatement("select * from activity_list where listed='Y' order by id");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				jsonTemp = new JSONObject();
				jsonTemp.put(ConstantUtil.JSON_ID, resultSet.getInt("id"));
				jsonTemp.put(ConstantUtil.JSON_NAME,
						resultSet.getString("activity"));
				activityArray.put(jsonTemp);
			}

			UtilFunctions.close(preparedStatement);
			UtilFunctions.close(resultSet);

			preparedStatement = connect
					.prepareStatement("select * from position_list where listed='Y' order by id");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				jsonTemp = new JSONObject();
				jsonTemp.put(ConstantUtil.JSON_ID, resultSet.getInt("id"));
				jsonTemp.put(ConstantUtil.JSON_NAME,
						resultSet.getString("position"));
				positionArray.put(jsonTemp);
			}

			UtilFunctions.close(preparedStatement);
			UtilFunctions.close(resultSet);

			preparedStatement = connect
					.prepareStatement("select * from subject_list where listed='Y' order by id");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				jsonTemp = new JSONObject();
				jsonTemp.put(ConstantUtil.JSON_ID, resultSet.getInt("id"));
				jsonTemp.put(ConstantUtil.JSON_NAME,
						resultSet.getString("name"));
				subjectArray.put(jsonTemp);
			}

			UtilFunctions.close(preparedStatement);
			UtilFunctions.close(resultSet);

			jsonObject.put(ConstantUtil.JSON_USERID, user_id);
			jsonObject.put(ConstantUtil.JSON_ACTIVITY_LIST, activityArray);
			jsonObject.put(ConstantUtil.JSON_POSITION_LIST, positionArray);
			jsonObject.put(ConstantUtil.JSON_SUBJECT_LIST, subjectArray);

			return jsonObject.toString();

		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);

		return null;
	}
	
	/**************************** insert Raw Data ******************************/
	public static void insertData(Connection connect, String userId,
			String type, String data) throws Exception {

		PreparedStatement preparedStatement = connect
				.prepareStatement("insert into  raw_data (user_id, type, data) values (?, ?, ?)");
		preparedStatement.setInt(1, Integer.parseInt(userId));
		preparedStatement.setString(2, type);
		preparedStatement.setString(3, data);
		preparedStatement.execute();
		UtilFunctions.close(preparedStatement);
	}

	public static void insertJsonDataRaw(Connection connect, String data)
			throws Exception {

		PreparedStatement preparedStatement = connect
				.prepareStatement("insert into  raw_data (json_data) values (?)");
		preparedStatement.setString(1, data);
		preparedStatement.execute();
		UtilFunctions.close(preparedStatement);
	}

	public static void insertJsonData(Connection connect, String data)
			throws Exception {
		JSONObject json = new JSONObject(data);
		if (json.has("train"))
			insertTrainData(connect, json.getJSONArray("train"));

	}

	public static void insertTrainData(Connection con, JSONArray jsonArray)
			throws Exception {
		PreparedStatement prepStmt;
		JSONObject jsonObj;

		long start_time, end_time;
		int activity_id, position_id, subject_id;
		String data_type;
		for (int i = 0; i < jsonArray.length(); i++) {
			jsonObj = jsonArray.getJSONObject(i);

			start_time = jsonObj.getLong("start_time");
			end_time = jsonObj.getLong("end_time");
			activity_id = jsonObj.getInt("activity_id");
			position_id = jsonObj.getInt("position_id");

			if (jsonObj.has("subject_id"))
				subject_id = jsonObj.getInt("subject_id");
			else
				subject_id = 0;

			if (jsonObj.has("data_type"))
				data_type = jsonObj.getString("data_type");
			else
				data_type = "NA";

			prepStmt = con
					.prepareStatement("insert into train_data (start_time, end_time, activity_id, position_id, subject_id, data_type) values(?, ?, ?, ?, ?, ?)");
			prepStmt.setLong(1, start_time);
			prepStmt.setLong(2, end_time);
			prepStmt.setInt(3, activity_id);
			prepStmt.setInt(4, position_id);
			prepStmt.setInt(5, subject_id);
			prepStmt.setString(6, data_type);
			prepStmt.execute();
			UtilFunctions.close(prepStmt);
			// System.out.println("Train Data Inserted");
		}

	}

	/********************* AirSign Database actions **************************/

	public static int airsignLoginCheck(Connection connect, String phoneNo,
			String pin) throws Exception {
		PreparedStatement preparedStatement = connect
				.prepareStatement("select * from airsign_users where phone_no = ? and pin = ?");
		preparedStatement.setString(1, phoneNo);
		preparedStatement.setString(2, pin);
		ResultSet resultSet = preparedStatement.executeQuery();

		if (resultSet.next()){
			return resultSet.getInt("id");
		}			
			
		return -1;
	}

	public static boolean airsignCreateAccount(Connection connect, String phoneNo,
			String pin, String yearOfBirth, String gender) throws Exception {

		PreparedStatement preparedStatement = connect
				.prepareStatement("insert into  airsign_users (phone_no, pin, year_of_birth, gender) values (?, ?, ?, ?)");
		preparedStatement.setString(1, phoneNo);
		preparedStatement.setString(2, pin);
		preparedStatement.setString(3, yearOfBirth);
		preparedStatement.setString(4, gender);
		boolean result = preparedStatement.execute();
		UtilFunctions.close(preparedStatement);
		return result;
	}
	
	
	
	
}
