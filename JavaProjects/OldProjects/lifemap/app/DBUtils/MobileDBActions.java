package DBUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.virginia.cs.mooncake.mobiledatacollector.DataPacket;
import edu.virginia.cs.mooncake.mobiledatacollector.SensorSample;
import utils.DateTimeUtil;
import utils.UtilFunctions;

public class MobileDBActions {

	public static int checkLogin(Connection connection, String user_code,
			String password) throws Exception {

		int result = -1;
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from user_info where email = ? and password = ?");
		preparedStatement.setString(1, user_code);
		preparedStatement.setString(2, password);
		ResultSet resultSet = preparedStatement.executeQuery();

		if (resultSet.next()) {
			return resultSet.getInt("user_id");
		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);

		return result;
	}
	
	public static JSONObject getSettings(Connection connection, int user_id)
			throws Exception {
		JSONObject settings = new JSONObject();
		JSONArray sub_list = new JSONArray();
		JSONArray act_list = new JSONArray();
		JSONArray pos_list = new JSONArray();
		JSONObject json;

		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from subject_list where listed='Y'");
		ResultSet resultSet = preparedStatement.executeQuery();

//		json = new JSONObject();
//		json.put("id", 0);
//		json.put("name", "All");
//		sub_list.put(json);
		while (resultSet.next()) {
			json = new JSONObject();
			json.put("id", resultSet.getInt("id"));
			json.put("name", resultSet.getString("name"));
			sub_list.put(json);
		}
		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		preparedStatement = connection
				.prepareStatement("select * from activity_list where listed='Y'");
		resultSet = preparedStatement.executeQuery();

		json = new JSONObject();
		json.put("id", 0);
		json.put("name", "All");
		act_list.put(json);
		while (resultSet.next()) {
			json = new JSONObject();
			json.put("id", resultSet.getInt("id"));
			json.put("name", resultSet.getString("activity"));
			act_list.put(json);
		}
		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		preparedStatement = connection
				.prepareStatement("select * from position_list where listed='Y'");
		resultSet = preparedStatement.executeQuery();

		json = new JSONObject();
		json.put("id", 0);
		json.put("name", "All");
		pos_list.put(json);
		while (resultSet.next()) {
			json = new JSONObject();
			json.put("id", resultSet.getInt("id"));
			json.put("name", resultSet.getString("position"));
			pos_list.put(json);
		}
		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		settings.put("sub_list", sub_list);
		settings.put("act_list", act_list);
		settings.put("pos_list", pos_list);
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("settings", settings);

		return responseJSON;
	}
	
	public static JSONObject getSettingsAdmin(Connection connection, int user_id)
			throws Exception {
		JSONObject settings = new JSONObject();
		JSONArray sub_list = new JSONArray();
		JSONArray act_list = new JSONArray();
		JSONArray pos_list = new JSONArray();
		JSONObject json;

		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from subject_list where listed='H'");
		ResultSet resultSet = preparedStatement.executeQuery();

//		json = new JSONObject();
//		json.put("id", 0);
//		json.put("name", "All");
//		sub_list.put(json);
		while (resultSet.next()) {
			json = new JSONObject();
			json.put("id", resultSet.getInt("id"));
			json.put("name", resultSet.getString("name"));
			sub_list.put(json);
		}
		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		preparedStatement = connection
				.prepareStatement("select * from activity_list where listed='H'");
		resultSet = preparedStatement.executeQuery();

		json = new JSONObject();
		json.put("id", 0);
		json.put("name", "All");
		act_list.put(json);
		while (resultSet.next()) {
			json = new JSONObject();
			json.put("id", resultSet.getInt("id"));
			json.put("name", resultSet.getString("activity"));
			act_list.put(json);
		}
		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		preparedStatement = connection
				.prepareStatement("select * from position_list where listed='Y'");
		resultSet = preparedStatement.executeQuery();

		json = new JSONObject();
		json.put("id", 0);
		json.put("name", "All");
		pos_list.put(json);
		while (resultSet.next()) {
			json = new JSONObject();
			json.put("id", resultSet.getInt("id"));
			json.put("name", resultSet.getString("position"));
			pos_list.put(json);
		}
		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		settings.put("sub_list", sub_list);
		settings.put("act_list", act_list);
		settings.put("pos_list", pos_list);
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("settings", settings);

		return responseJSON;
	}

	public static JSONObject getData(Connection connection, int user_id,
			int sub_id, int act_id, int pos_id, JSONObject settings)
			throws Exception {

		JSONArray arr = new JSONArray();
		JSONObject json;

		String stmt = "Select * from train_data where subject_id in (";

		if (sub_id == 0)
			stmt += getInClause(settings.getJSONArray("sub_list"));
		else
			stmt += sub_id;

		stmt += ") and activity_id in (";

		if (act_id == 0)
			stmt += getInClause(settings.getJSONArray("act_list"));
		else
			stmt += act_id;

		stmt += ") and position_id in (";

		if (pos_id == 0)
			stmt += getInClause(settings.getJSONArray("pos_list"));
		else
			stmt += pos_id;

		stmt += ") order by start_time desc";
		// System.out.println("\n\n"+stmt+"\n\n");

		PreparedStatement preparedStatement = connection.prepareStatement(stmt);
		ResultSet resultSet = preparedStatement.executeQuery();

		String[] file_parts;
		long start_time, end_time, current_time, t1, t2;
		int activity_id, position_id, subject_id, id, index, size, valueLength, i;
		ArrayList<SensorSample> list = new ArrayList<SensorSample>();
		SensorSample sample;
		DataPacket dataPacket;
		String path = "C:\\ASM\\developments\\play\\myprojects\\lifemap\\phoneuploads";
		String[] files = getFileList(path);
		boolean[] flags = new boolean[files.length];

		while (resultSet.next()) {
			json = new JSONObject();
			id = resultSet.getInt("id");
			start_time = resultSet.getLong("start_time");
			end_time = resultSet.getLong("end_time");
			activity_id = resultSet.getInt("activity_id");
			position_id = resultSet.getInt("position_id");
			subject_id = resultSet.getInt("subject_id");

			json.put("id", id);
			json.put("subject", subject_id);
			json.put("activity", activity_id);
			json.put("position", position_id);
			json.put("time", DateTimeUtil.getDateTimeString(start_time, 3));
			json.put("duration1", duration(end_time - start_time));
			json.put("duration2", "-");
			json.put("condition", "File Missing");
			json.put("diff_start", 0);
			json.put("diff_end", 0);

			for (i = 0; i < files.length; i++) {
				file_parts = files[i].split("_");
				if (file_parts.length < 2)
					continue;
				t1 = Long.parseLong(file_parts[1]);

				if (t1 == start_time) {
					flags[i] = true;
					json.put("file_name", files[i]);
					json.put("start_time", start_time);
					try {
						File file = new File(path + "\\" + files[i]);
						FileInputStream fileIn = new FileInputStream(file);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						dataPacket = (DataPacket) in.readObject();
						list = dataPacket.list;
						in.close();
						fileIn.close();
					} catch (Exception ex) {
						json.put("condition", "Error in file");
						continue;
					}

					int sz = list.size();
					long st = list.get(0).timeStamp;
					long et = list.get(sz - 1).timeStamp;
					json.put("duration2", duration(et - st));
					
					//json.put("diff_end", et-end_time);

					if (end_time - start_time > et - st + 15000
							|| end_time - start_time > et - st + 15000)
						json.put("condition", "Duration problem");
					else
						json.put("condition", "OK");
					break;
				}

			}
			arr.put(json);

		}

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		/************** Adding standalone files *********************/
		if (sub_id == 0 && user_id==100) {
			for (i = 0; i < files.length; i++) {
				if (flags[i] == false) {
					file_parts = files[i].split("_");
					if (file_parts.length < 2)
						continue;
					t1 = Long.parseLong(file_parts[1]);

					json = new JSONObject();
					json.put("id", 0);
					json.put("subject", -1);
					json.put("activity", -1);
					json.put("position", -1);
					json.put("time", DateTimeUtil.getDateTimeString(t1, 3));
					json.put("duration1", "-");
					json.put("duration2", "-");
					json.put("condition", "Error in file");
					json.put("file_name", files[i]);

					try {
						File file = new File(path + "\\" + files[i]);
						FileInputStream fileIn = new FileInputStream(file);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						dataPacket = (DataPacket) in.readObject();
						list = dataPacket.list;
						in.close();
						fileIn.close();
					} catch (Exception ex) {
						json.put("condition", "Error in file");
						continue;
					}

					int sz = list.size();
					long st = list.get(0).timeStamp;
					long et = list.get(sz - 1).timeStamp;
					json.put("duration2", duration(et - st));
					json.put("condition", "Annotation Missing");
					arr.put(json);
				}
			}
		}

		JSONObject responseJSON = new JSONObject();
		responseJSON.put("data", arr);
		responseJSON.put("sub_id", sub_id);
		responseJSON.put("act_id", act_id);
		responseJSON.put("pos_id", pos_id);
		return responseJSON;

	}

	public static JSONObject deleteData(Connection connection, int user_id,
			JSONObject obj) throws Exception {

		int id = obj.getInt("id");
		if (id > 0) {
			String stmt = "delete from train_data where id=?";
			PreparedStatement preparedStatement = connection
					.prepareStatement(stmt);
			preparedStatement.setInt(1, id);
			preparedStatement.execute();
			UtilFunctions.close(preparedStatement);
		}

		if (obj.has("file_name")) {
			try {
				String path = "C:\\ASM\\developments\\play\\myprojects\\lifemap\\phoneuploads\\";
				String file_name = path + obj.getString("file_name");
				File file = new File(file_name);
				file.delete();
			} catch (Exception ex) {
				System.out.println("File deleteion error");
			}
		}

		JSONObject responseJSON = new JSONObject();
		responseJSON.put("id", id);
		if (obj.has("file_name"))
			responseJSON.put("file_name", obj.getString("file_name"));
		return responseJSON;

	}

	public static String getInClause(JSONArray arr) throws Exception {
		int l = arr.length();
		String str = "" + arr.getJSONObject(0).getInt("id");

		for (int i = 1; i < l; i++) {
			str += "," + arr.getJSONObject(i).getInt("id");
		}

		return str;
	}

	public static String[] getFileList(String path) {
		File folder = new File(path);
		String[] files = folder.list();
		Arrays.sort(files);
		return files;
	}

	public static String duration(long ms) {
		long s = ms / 1000;
		ms = ms % 1000;
		long min = s / 60;
		s = s % 60;
		return min + " m " + s + " s " + ms + " ms";
	}

}
