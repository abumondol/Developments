package Web;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.DateTimeUtil;
import DBUtils.LMWebDBActions_Old;
import DBUtils.LifemapMySQLInstance;

public class ProcessWebReuqest {

	public static JSONObject processRequest(JSONObject json, int user_id) throws Exception {
		JSONObject responseJson = null;
		String request_type;
		try {
			//json = new JSONObject(jsonStr);
			request_type = json.getString("request_type");
			LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();

			if ( request_type.equals("settings")) {
				responseJson = getSettings(user_id);

			} else if ( request_type.equals("daily_data")) {				
				String date = json.getString("date");
				responseJson = LMWebDBActions_Old.getDailyData(msi.getConnection(), user_id, date);
				
			} else if ( request_type.equals("save_daily_data")) {
				String date = json.getString("date");
				String daily_data = json.getJSONObject("daily_data").toString();
				responseJson = LMWebDBActions_Old.saveDailyData(msi.getConnection(), user_id, date, daily_data);

			}else if ( request_type.equals("diary_get")) {
				String date = json.getString("date");
				responseJson = getDiary(date);

			} else if ( request_type.equals("diary_save")) {
				String date = json.getString("date");
				String content = json.getString("content");
				responseJson = saveDiary(date, content);

			} else if ( request_type.equals("diary_delete")) {
				String date = json.getString("date");
				responseJson = deleteDiary(date);

			} else if ( request_type.equals("add_new")) {
				String name = json.getString("name");
				String what= json.getString("what");
				LMWebDBActions_Old.addNew(msi.getConnection(), user_id, name, what);
				responseJson = getSettings(user_id);
				
			} else if ( request_type.equals("add_strand")) {
				String name = json.getString("name");
				String details= json.getJSONObject("details").toString();
				LMWebDBActions_Old.addNewStrand(msi.getConnection(), user_id, name, details);
				responseJson = getSettings(user_id);
				
			} else if ( request_type.equals("delete")) {
				int id= json.getInt("id");				
				String what= json.getString("what");
				LMWebDBActions_Old.delete(msi.getConnection(), id, what);
				responseJson = getSettings(user_id);
				
			}

			if (responseJson != null)
				responseJson.put("response_code", 1);
		} catch (Exception ex) {
			System.out.println("Exception in processRequest: " + ex.toString());
			return null;
		}

		return responseJson;
	}
	
	public static JSONObject getSettings(int user_id) throws Exception {
		JSONObject responseJson = new JSONObject();
		LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();
		responseJson.put("settings",
				LMWebDBActions_Old.getSettings(msi.getConnection(), user_id));
		return responseJson;

	}
	
	
	/******************************************************************************/	
	
	public static JSONObject getActivityTable(String date) throws Exception {
		int year = Integer.parseInt(date.substring(6));
		int month = Integer.parseInt(date.substring(0, 2));
		int day = Integer.parseInt(date.substring(3, 5));
		long startTime = DateTimeUtil.getTimeInMilis(year, month, day, 0, 0, 0);
		long endTime = startTime + 24L * 3600L * 1000L -1;
		
		//System.out.println(DateTimeUtil.getDateTimeString(startTime,3)+" "+DateTimeUtil.getDateTimeString(endTime,3));
		LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();

		JSONArray jsonArr = LMWebDBActions_Old.getActivities(msi.getConnection(),
				startTime, endTime);
		JSONObject ob, ob1, ob2;
		//System.out.println(jsonArr.length());
		int i = 0;
		while (i < jsonArr.length() - 1) {
			ob1 = jsonArr.getJSONObject(i);
			ob2 = jsonArr.getJSONObject(i + 1);
			if (ob1.getInt("activity_id") == ob2.getInt("activity_id")
					&& ob2.getLong("start_time") - ob1.getLong("end_time") <= 60000) {
				ob1.put("end_time", ob2.getLong("end_time"));
				jsonArr.remove(i + 1);
			} else {
				i++;
			}
		}
		
		i=0;
		while(i<jsonArr.length()){
			ob = jsonArr.getJSONObject(i);
			
			if(ob.getLong("end_time")-ob.getLong("start_time")<=60000)
				jsonArr.remove(i);
			else
				i++;
		}

		int l = jsonArr.length();
		for (i = 0; i < l; i++) {
			ob = jsonArr.getJSONObject(i);
			ob.put("start_time", ob.getLong("start_time") - startTime);
			ob.put("end_time", ob.getLong("end_time") - startTime);
			
			ob.put("activity_type_id", 1);
			ob.put("location_id", 1);
			ob.put("done_with_id", 1);
			ob.put("done_for_id", 1);
			ob.put("activity_tag", "");
			ob.put("location_tag", "");
			ob.put("done_with_tag", "");
			ob.put("done_for_tag", "");
			ob.put("comment", "");
		}

		JSONObject responseJson=new JSONObject();
		responseJson.put("start_time", startTime);
		responseJson.put("date", date);
		responseJson.put("list", jsonArr);
		
		return responseJson;

	}	

	public static JSONObject getActivityTableRandom(String date)
			throws Exception {
		int year = Integer.parseInt(date.substring(6));
		int month = Integer.parseInt(date.substring(0, 2));
		int day = Integer.parseInt(date.substring(3, 5));
		// long startTime = DateTimeUtil.getTimeInMilis(year, month, day,
		// 0, 0, 0);
		// long endTime = startTime + 24L * 3600L * 1000L;

		JSONObject responseJson = new JSONObject();
		String[] strandTypes = { "Location", "People", "Activity" };
		String[] activities = { "", "Walk", "Run", "Exercise", "Drive",
				"Shower", "Make BreakFast", "Eat", "Work", "Sleep", "Others" };
		String[] activity_types = { "", "Continuing", "Repeating", "Unique" };
		String[] locations = { "", "Home", "Office", "Location 1", "Others" };
		String[] doneWith = { "", "Self", "Parents", "Family", "Friends",
				"Colleagues", "Others" };
		String[] doneFor = { "", "Self", "Parents", "Family", "Friends",
				"Colleagues", "Others" };
		int[] durationMin = { 5, 1, 1, 5, 2, 5, 5, 2, 10, 100 };
		int[] durationMax = { 500, 100, 40, 100, 600, 30, 60, 45, 400, 600 };

		int startTime = 0, duration, activityIndex, prevActivityIndex = 0;
		JSONObject ob;
		JSONArray jsonArray = new JSONArray();

		while (startTime < 24 * 60) {
			activityIndex = (int) ((activities.length - 1) * Math.random());
			if (activityIndex == prevActivityIndex)
				continue;

			duration = (int) (durationMax[activityIndex] * Math.random());
			if (duration < durationMin[activityIndex])
				duration += durationMin[activityIndex];

			ob = new JSONObject();
			ob.put("start_time", startTime);
			ob.put("end_time", startTime + duration);
			ob.put("activity_index", activityIndex);
			ob.put("activity_type_index", 0);
			ob.put("location_index", 0);
			ob.put("done_with_index", 0);
			ob.put("done_for_index", 0);
			ob.put("activity_tag", "");
			ob.put("location_tag", "");
			ob.put("done_with_tag", "");
			ob.put("done_for_tag", "");
			ob.put("comment", "");
			jsonArray.put(ob);

			if (startTime + duration >= 24 * 60) {
				ob.put("end_time", 24 * 60);
				break;
			}

			prevActivityIndex = activityIndex;
			System.out.println(startTime + "  -  " + (startTime + duration)
					+ " : " + activities[activityIndex]);
			startTime += duration;

		}

		// int[] strandIndices={2,3};
		JSONArray strands = new JSONArray();
		ob = new JSONObject();
		ob.put("id", 0);
		ob.put("name", "Parents");
		ob.put("type_index", 1);
		// ob.put("indices",strandIndices);
		strands.put(ob);

		ob = new JSONObject();
		ob.put("id", 1);
		ob.put("name", "Home");
		ob.put("type_index", 0);
		// ob.put("indices",{});
		strands.put(ob);

		responseJson.put("strand_types", strandTypes);
		responseJson.put("activities", activities);
		responseJson.put("activity_types", activity_types);
		responseJson.put("locations", locations);
		responseJson.put("done_with", doneWith);
		responseJson.put("done_for", doneFor);
		responseJson.put("list", jsonArray);
		responseJson.put("strands", strands);
		return responseJson;

	}

	public static JSONObject getDiary(String date) throws Exception {
		JSONObject responseJson = new JSONObject();
		LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();
		responseJson.put("content",
				LMWebDBActions_Old.getDiary(msi.getConnection(), date));
		return responseJson;

	}

	public static JSONObject saveDiary(String date, String content)
			throws Exception {
		JSONObject responseJson = new JSONObject();
		LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();
		LMWebDBActions_Old.saveDiary(msi.getConnection(), date, content);
		return responseJson;

	}

	public static JSONObject deleteDiary(String date) throws Exception {
		JSONObject responseJson = new JSONObject();
		LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();
		LMWebDBActions_Old.deleteDiary(msi.getConnection(), date);
		return responseJson;

	}

	
	
	/***************************** SWAD ********************************/
	
	public static JSONObject processRequestSwad(JSONObject json) throws Exception {
		JSONObject responseJson = null;
		String request_type;
		try {
			//json = new JSONObject(jsonStr);
			request_type = json.getString("request_type");
			LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();

			if ( request_type.equals("settings")) {
				responseJson = new JSONObject();				
				responseJson.put("settings",
						LMWebDBActions_Old.getSettingsSwad(msi.getConnection()));

			} else if ( request_type.equals("data")) {				
				String date = json.getString("date");
				int subject = json.getInt("subject");
				int activity = json.getInt("activity");
				responseJson = LMWebDBActions_Old.getDataSwad(msi.getConnection(),  subject, activity, date);
				
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
