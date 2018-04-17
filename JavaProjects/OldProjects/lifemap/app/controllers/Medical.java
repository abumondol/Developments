package controllers;

import java.util.Map;

import org.json.JSONObject;

import play.mvc.Controller;
import play.mvc.Result;
import DBUtils.MedicalDBActions;
import DBUtils.MedicalMySQLInstance;

public class Medical extends Controller {

	public static Result phone() {
		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		final String jsonStr = values.get("json")[0];
		System.out.println(jsonStr);
		String response = phoneJsonAction(jsonStr);
		System.out.println(response);
		return ok(response);
	}

	public static Result image(Integer id) {
		try {
			return ok(new java.io.File("images/" + id + ".jpg"));
		} catch (Exception ex) {
			return ok("exception");
		}
	}

	private static String phoneJsonAction(String jsonStr) {

		MedicalMySQLInstance msi;
		JSONObject json = null;
		JSONObject response_json = null;
		String json_type = null;

		try {
			json = new JSONObject(jsonStr);
			json_type = json.getString("json_type");
			msi = MedicalMySQLInstance.getInstance();
			System.out.println("Request JSON Type:" + json_type);
			if (json_type == null)
				response_json = null;
			else if (json_type.equals("login"))
				response_json = MedicalDBActions.loginCheck(
						msi.getConnection(), json);
			else if (json_type.equals("mrn_search"))
				response_json = MedicalDBActions.mrnSearch(msi.getConnection(),
						json);
			else
				response_json = null;
			
		} catch (Exception ex) {
			System.out.println(ex.toString());
			response_json = null;
		}

		if (response_json == null)
			return "{\"response_code\":-1}"; // -1 -> Exception occured

		return response_json.toString();
	}

}
