package controllers;

import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Controller;
import play.mvc.Result;
import DBUtils.LifemapDBActions;
import DBUtils.LifemapMySQLInstance;
import Web.ProcessLifemapWebReuqest;
import Web.ProcessWebReuqest;

public class Lifemap extends Controller {

	public static Result index() {
		return ok("Welcome");
	}

	public static Result login() {

		try {

			final Map<String, String[]> values = request().body()
					.asFormUrlEncoded();

			final String user_code = values.get("user_code")[0];
			final String password = values.get("password")[0];
			System.out.println("user_id: " + user_code + "  data type: "
					+ password);

			LifemapMySQLInstance msi = LifemapMySQLInstance.getInstance();
			int userId = LifemapDBActions.checkLogin(msi.getConnection(),
					user_code, password);
			if (userId > 0) {
				session("user_id", "" + userId);
				session("user_code", user_code);
				return ok("OK");
			} else
				return ok("NO");
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return ok("error");
		}

	}

	public static Result home() {
		if (session("user_id") == null) {
			System.out.println("Session Id is null inside home");
			return redirect("/");
		}
		return ok(views.html.home.render(session("user_code"),
				session("user_id")));
	}

	public static Result logout() {
		System.out.println("Logout called");
		session().clear();
		return redirect("/");
	}

	public static Result ajax() {

		JSONObject responseJson = null;
		boolean logoutFlag = false;
		try {
			final Map<String, String[]> values = request().body()
					.asFormUrlEncoded();
			final String jsonStr = values.get("json")[0];
			System.out.println("Requested JSON: " + jsonStr);
			JSONObject requestJson = new JSONObject(jsonStr);
			int user_id = -1;
			if (requestJson.has("user_id"))
				user_id = requestJson.getInt("user_id");
			// System.out.println(user_id + " " + session("user_id"));

			if (session("user_id") == null || !requestJson.has("user_id")
					|| user_id != Integer.parseInt(session("user_id")))
				logoutFlag = true;
			else
				responseJson = ProcessLifemapWebReuqest.processRequest(
						requestJson, user_id);
		} catch (Exception ex) {
			System.out.println(ex.toString());
			responseJson = null;
		}

		if (logoutFlag) {
			System.out.println("Session Id is null inside ajax");
			session().clear();
			return ok("{\"response_code\":-1, \"html\":\"Session Out\"}");
		}

		if (responseJson != null) {
			System.out.println("Response JSON: " + responseJson.toString());
			return ok(responseJson.toString());
		} else {
			System.out.println("Response JSON: "
					+ "{\"response_code\":0, \"html\":\"Error in Server\"}");
			return ok("{\"response_code\":0, \"html\":\"Error in Server\"}");
		}
	}
	
	public static Result external_data() {
		System.out.println("External data called");

		JSONObject responseJson = null;
		String jsonStr=null;
		boolean logoutFlag = false;
		try {
			final Map<String, String[]> values = request().body()
					.asFormUrlEncoded();			
			System.out.println(values.keySet().toString()+"---"+values.containsKey("external_data"));
			jsonStr = values.get("external_data")[0];			
			System.out.println("Requested JSON: " + jsonStr);
			JSONObject requestJson = new JSONObject(jsonStr);
			int user_id = 0;			
			responseJson = ProcessLifemapWebReuqest.processRequest(
						requestJson, user_id);
		} catch (Exception ex) {
			System.out.println(jsonStr);
			System.out.println(ex.toString());
			responseJson = null;
		}
		
		if (responseJson != null) {
			System.out.println("Response JSON: " + responseJson.toString());
			return ok(responseJson.toString());
		} else {
			System.out.println("Response JSON: "
					+ "{\"response_code\":0, \"message\":\"Error\"}");
			return ok("{\"response_code\":0, \"message\":\"Error\"}");
		}
	}
	


}
