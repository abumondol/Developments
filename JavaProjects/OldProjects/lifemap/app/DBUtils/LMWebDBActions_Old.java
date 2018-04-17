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

public class LMWebDBActions_Old {
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
		JSONObject json = new JSONObject();
		JSONArray locationArray = new JSONArray();
		JSONArray peopleArray = new JSONArray();
		JSONArray activityArray = new JSONArray();
		JSONArray activityTypeArray = new JSONArray();
		JSONArray strandArray = new JSONArray();

		JSONObject ob;

		/************** Activity Types ****************/
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from lifemap_list_activity_type order by id");
		ResultSet resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			ob = new JSONObject();
			ob.put("id", resultSet.getInt("id"));
			ob.put("name", resultSet.getString("name"));
			activityTypeArray.put(ob);
		}

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		/************** Activity List ****************/
		preparedStatement = connection
				.prepareStatement("select * from lifemap_user_activities where user_id=? or user_id=0 order by user_id, sl");
		preparedStatement.setInt(1, user_id);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			ob = new JSONObject();
			ob.put("id", resultSet.getInt("id"));
			ob.put("name", resultSet.getString("name"));
			ob.put("uid", resultSet.getInt("user_id"));
			activityArray.put(ob);
		}

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		/************** Location List ****************/
		preparedStatement = connection
				.prepareStatement("select * from lifemap_user_locations where user_id=? or user_id =0 order by user_id, sl");
		preparedStatement.setInt(1, user_id);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			ob = new JSONObject();
			ob.put("id", resultSet.getInt("id"));
			ob.put("name", resultSet.getString("name"));
			ob.put("uid", resultSet.getInt("user_id"));
			locationArray.put(ob);
		}

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		/************** People List ****************/
		preparedStatement = connection
				.prepareStatement("select * from lifemap_user_people where user_id=? or user_id =0 order by user_id, sl");
		preparedStatement.setInt(1, user_id);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			ob = new JSONObject();
			ob.put("id", resultSet.getInt("id"));
			ob.put("name", resultSet.getString("name"));
			ob.put("uid", resultSet.getInt("user_id"));
			peopleArray.put(ob);
		}

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		/************** Strand List ****************/
		preparedStatement = connection
				.prepareStatement("select * from lifemap_user_strands where user_id=? or user_id =0  order by user_id");
		preparedStatement.setInt(1, user_id);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			ob = new JSONObject();
			ob.put("id", resultSet.getInt("id"));
			ob.put("name", resultSet.getString("name"));
			ob.put("uid", resultSet.getInt("user_id"));
			ob.put("details", new JSONObject(resultSet.getString("details")));
			strandArray.put(ob);
		}

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		json.put("activity_type_list", activityTypeArray);
		json.put("user_activity_list", activityArray);
		json.put("user_location_list", locationArray);
		json.put("user_people_list", peopleArray);

		if (strandArray.length() > 0)
			json.put("user_strand_list", strandArray);

		return json;
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
		}

		responseJson.put("date", date);

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);
		return responseJson;
	}

	public static JSONObject saveDailyData(Connection connection, int user_id,
			String date, String daily_data) throws Exception {
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from lifemap_daily_data where user_id = ? and date = ?");
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, date);
		ResultSet resultSet = preparedStatement.executeQuery();

		String sql;
		if (resultSet.next())
			sql = "update lifemap_daily_data set daily_data = ? where user_id = ? and date = ?";
		else
			sql = "insert into lifemap_daily_data (daily_data, user_id, date) values(?, ?, ?)";

		UtilFunctions.close(resultSet);
		UtilFunctions.close(preparedStatement);

		preparedStatement = connection.prepareStatement(sql);

		preparedStatement.setString(1, daily_data);
		preparedStatement.setInt(2, user_id);
		preparedStatement.setString(3, date);

		preparedStatement.execute();

		UtilFunctions.close(preparedStatement);

		return new JSONObject();
	}

	public static void addNew(Connection connection, int user_id, String name,
			String what) throws Exception {
		String sql = "";

		if (what.equals("act"))
			sql = "insert into lifemap_user_activities (user_id, name) values(?, ?)";
		else if (what.equals("loc"))
			sql = "insert into lifemap_user_locations (user_id, name) values(?, ?)";
		else if (what.equals("ppl"))
			sql = "insert into lifemap_user_people (user_id, name) values(?, ?)";
		else
			return;
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, name);
		preparedStatement.execute();
	}

	public static void addNewStrand(Connection connection, int user_id,
			String name, String details) throws Exception {
		String sql = "insert into lifemap_user_strands (user_id, name, details) values(?, ?, ?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, name);
		preparedStatement.setString(3, details);
		preparedStatement.execute();
	}

	public static void delete(Connection connection, int id, String what)
			throws Exception {
		String sql = "";
		if (what.equals("strand"))
			sql = "delete from lifemap_user_strands where id=?";
		else
			return;

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		preparedStatement.execute();
	}

	public static JSONArray getActivities(Connection connection,
			long startTime, long endTime) throws Exception {
		PreparedStatement preparedStatement = connection
				.prepareStatement("Select * from activity_data where start_time>=? and start_time<? or end_time>? and end_time<=? order by start_time");
		preparedStatement.setLong(1, startTime);
		preparedStatement.setLong(2, endTime);
		preparedStatement.setLong(3, startTime);
		preparedStatement.setLong(4, endTime);
		ResultSet resultSet = preparedStatement.executeQuery();

		JSONArray jsonArr = new JSONArray();
		JSONObject ob;
		int aid;
		long st, et;
		while (resultSet.next()) {
			aid = resultSet.getInt("activity_id");
			st = resultSet.getLong("start_time");
			et = resultSet.getLong("end_time");
			System.out.println(st + " " + et + " " + (et - st) + " " + aid);
			if (aid == 1 || et - st <= 30000)
				continue;
			ob = new JSONObject();
			ob.put("start_time", st);
			ob.put("end_time", et);
			ob.put("activity_id", aid);
			jsonArr.put(ob);
		}

		return jsonArr;
	}

	/**********************************************************************/
	public static int[] getActivityData(Connection connection, long startTime,
			long endTime) throws Exception {

		int total = 288;
		int i = 0;
		int[] activities = new int[total];
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from activity_raw where start_time < ? and end_time > ? order by start_time");
		preparedStatement.setLong(1, endTime);
		preparedStatement.setLong(2, startTime);
		ResultSet resultSet = preparedStatement.executeQuery();

		int rowCount = 0;
		if (resultSet.last()) {
			rowCount = resultSet.getRow();
			resultSet.beforeFirst(); // not rs.first() because the rs.next()
										// below will move on, missing the first
										// element
		}

		if (rowCount == 0) {
			return activities;
		}

		long[] time1 = new long[rowCount];
		long[] time2 = new long[rowCount];
		int[] aid = new int[rowCount];

		i = 0;
		while (resultSet.next()) {
			time1[i] = resultSet.getLong("start_time");
			time2[i] = resultSet.getLong("end_time");
			aid[i] = resultSet.getInt("activity_id");
			i++;
		}

		if (time1[0] < startTime) {
			time1[0] = startTime;
		}

		if (time2[rowCount - 1] > endTime) {
			time2[rowCount - 1] = endTime;
		}

		System.out.println(" Row Count: " + rowCount);

		/******************** Fill No Data ***************************/

		ArrayList<ActivityEntity> list = new ArrayList<ActivityEntity>();

		ActivityEntity ae, prev, next;
		if (time1[0] > startTime) {
			ae = new ActivityEntity(startTime, time1[0], 0);
			list.add(ae);
		}

		ae = new ActivityEntity(time1[0], time2[0], aid[0]);
		list.add(ae);

		for (i = 1; i < rowCount; i++) {
			if (time1[i] > time2[i - 1]) {
				ae = new ActivityEntity(time2[i - 1], time1[i], 0);
				list.add(ae);
			}
			ae = new ActivityEntity(time1[i], time2[i], aid[i]);
			list.add(ae);
		}

		if (time2[i - 1] < endTime) {
			ae = new ActivityEntity(time2[i - 1], endTime, 0);
			list.add(ae);
		}

		System.out.println(" Filled Row Count: " + list.size());

		/******************** Smoothing ***************************/
		i = 1;
		while (i < list.size() - 1) {
			ae = list.get(i);
			prev = list.get(i - 1);
			next = list.get(i + 1);
			if (ae.duration() < 30 * 1000) {
				if (prev.duration() >= next.duration()) {
					prev.setEndTime(ae.getEndTime());
				} else {
					next.setStartTime(ae.getStartTime());
				}

				list.remove(i);
			} else
				i++;
		}

		System.out.println(" Smoothed Row Count: " + list.size());

		for (i = 0; i < list.size(); i++)
			System.out.println(DateTimeUtil.getDateTimeString(list.get(i)
					.getStartTime(), 3)
					+ " "
					+ DateTimeUtil.getDateTimeString(list.get(i).getEndTime(),
							3) + " " + list.get(i).getActivityId());

		/*********************** Convert to activity Bar *****************/
		int j = 0;
		int duration = 5 * 60 * 1000;
		int[] activityLengths = new int[7];
		i = 0;

		while (i < list.size()) {
			// System.out.println("i:"+i+" ,j:"+j);
			if (j >= total)
				break;
			ae = list.get(i);
			if (ae.getEndTime() >= ae.getStartTime() + duration) {
				// System.out.println("hi");
				activities[j] = ae.getActivityId();
				j++;

				if (ae.getEndTime() == ae.getStartTime() + duration)
					i++;
				else
					ae.setStartTime(ae.getStartTime() + duration);
			} else {
				// System.out.println("hello");
				long cet = ae.getStartTime() + duration;
				UtilFunctions.intializeActivityArray(activityLengths);

				while (ae.getEndTime() < cet) {
					activityLengths[ae.getActivityId()] += (int) ae.duration();
					i++;
					if (i < list.size())
						ae = list.get(i);
				}

				if (ae.getEndTime() > cet) {
					activityLengths[ae.getActivityId()] += (int) (cet - ae
							.getStartTime());
					ae.setStartTime(cet);
				} else {
					activityLengths[ae.getActivityId()] += ae.duration();
				}

				activities[j] = UtilFunctions.getMaxIndex(activityLengths);
				j++;
			}

		}

		System.out.println("i:" + i + " ,j:" + j);

		return activities;
	}

	public static int[] getUserAnnotationData(Connection connection,
			long startTime, long endTime) throws Exception {

		int total = 288;
		int i = 0;
		int[] activities = new int[total];
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from user_annotation where start_time < ? and end_time > ? order by start_time");
		preparedStatement.setLong(1, endTime);
		preparedStatement.setLong(2, startTime);
		ResultSet resultSet = preparedStatement.executeQuery();

		int rowCount = 0;
		if (resultSet.last()) {
			rowCount = resultSet.getRow();
			resultSet.beforeFirst(); // not rs.first() because the rs.next()
										// below will move on, missing the first
										// element
		}

		if (rowCount == 0) {
			return activities;
		}

		long[] time1 = new long[rowCount];
		long[] time2 = new long[rowCount];
		int[] aid = new int[rowCount];

		i = 0;
		while (resultSet.next()) {
			time1[i] = resultSet.getLong("start_time");
			time2[i] = resultSet.getLong("end_time");
			aid[i] = resultSet.getInt("activity_id");
			i++;
		}

		if (time1[0] < startTime) {
			time1[0] = startTime;
		}

		if (time2[rowCount - 1] > endTime) {
			time2[rowCount - 1] = endTime;
		}

		// long t;
		// int duration = 5 * 60 * 1000;
		// i=0;
		// int j=0;
		// for (t = startTime; t < endTime; t += duration) {
		// if(startTime == time1[i]){
		// activities[i]
		//
		// }else{
		//
		// }
		// }

		System.out.println(" Row Count: " + rowCount);
		return activities;
	}

	public static String getDiary(Connection connection, String date)
			throws Exception {
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from diary where date=?");
		preparedStatement.setString(1, date);
		ResultSet resultSet = preparedStatement.executeQuery();

		if (resultSet.next()) {
			return resultSet.getString("content");
		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);
		return "";

	}

	public static void saveDiary(Connection connection, String date,
			String content) throws Exception {
		PreparedStatement preparedStatement = connection
				.prepareStatement("select * from diary where date=?");
		preparedStatement.setString(1, date);
		ResultSet resultSet = preparedStatement.executeQuery();

		PreparedStatement pst;
		if (resultSet.next()) {
			pst = connection
					.prepareStatement("update diary set content=? where date=?");
			pst.setString(1, content);
			pst.setString(2, date);
			pst.execute();
			UtilFunctions.close(pst);
		} else {
			pst = connection
					.prepareStatement("insert into diary (date, content) values (?,?)");

			pst.setString(1, date);
			pst.setString(2, content);
			pst.execute();
			UtilFunctions.close(pst);
		}

		UtilFunctions.close(preparedStatement);
		UtilFunctions.close(resultSet);

	}

	public static boolean deleteDiary(Connection connection, String date)
			throws Exception {
		PreparedStatement preparedStatement = connection
				.prepareStatement("delete from diary where date=?");
		preparedStatement.setString(1, date);
		boolean flag = preparedStatement.execute();
		UtilFunctions.close(preparedStatement);
		return flag;

	}

	/***************************SWAD*****************************/
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

	public static JSONObject getDataSwad(Connection connection, int subject, int activity,
			String date) throws Exception {

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
