package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;

import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.ActivityEntity;
import utils.ConstantUtil;
import DBUtils.DBActions;
import DBUtils.MobileDBActions;
import DBUtils.MobileMySQLInstance;
import Web.ProcessMobileWebReuqest;

public class Mobile extends Controller {

	/********************** show all activities ************************/
	public static Result show(String adate) {
		ArrayList<ActivityEntity> list = null;
		try {
			MobileMySQLInstance msi = MobileMySQLInstance.getInstance();
			list = DBActions.getActivityMap(msi.getConnection());
		} catch (Exception ex) {
			return ok(ex.toString());
		}

		return ok(views.html.show.render(adate, list));
	}

	/**************************** insertRawData ************************/
	public static Result insertData() {
		boolean flag = true;
		System.out.println("insert called at" + (new Date()).toString());
		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();

		final String userId = values.get(ConstantUtil.USERID)[0];
		final String type = values.get(ConstantUtil.TYPE)[0];
		final String data = values.get(ConstantUtil.DATA)[0];
		System.out.println("user_id: " + userId + "  data type: " + type
				+ "  data length: " + data.length());

		if (flag == true)
			return ok("Hi");
		JSONObject resJson = new JSONObject();

		try {
			resJson.put(ConstantUtil.JSON_RESULT, "Exception");
			MobileMySQLInstance msi = MobileMySQLInstance.getInstance();
			DBActions.insertData(msi.getConnection(), userId, type, data);
		} catch (Exception ex) {
			flag = false;
			System.out.println("Exception calling MySql");
			return ok(resJson.toString());
		}

		try {
			if (flag)
				resJson.put(ConstantUtil.JSON_RESULT, "ok");
			else
				resJson.put(ConstantUtil.JSON_RESULT, "not ok");
		} catch (Exception e) {
			System.out.println("Exception putting res");
			return ok(resJson.toString());
		}

		return ok(resJson.toString());
	}

	public static Result insertJsonData() {
		boolean flag = true;
		System.out.println("insert called at" + (new Date()).toString());
		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();

		final String data = values.get(ConstantUtil.JSON)[0];
		System.out.println("  data length: " + data.length());

		JSONObject resJson = new JSONObject();

		try {
			resJson.put(ConstantUtil.JSON_RESULT, "Exception");
			MobileMySQLInstance msi = MobileMySQLInstance.getInstance();
			if (msi.getConnection() == null) {
				System.out.println("connection null");
				throw new Exception("MySQL connection is null");
			}
			DBActions.insertJsonData(msi.getConnection(), data);

		} catch (Exception ex) {
			flag = false;
			System.out.println("Exception calling MySql " + ex.toString());
			return ok(resJson.toString());
		}

		try {
			if (flag)
				resJson.put(ConstantUtil.JSON_RESULT, "ok");
			else
				resJson.put(ConstantUtil.JSON_RESULT, "not ok");
		} catch (Exception e) {
			System.out.println("Exception putting res");
			return ok(resJson.toString());
		}

		return ok(resJson.toString());
	}

	/********************************** login check ************************/
	public static Result login() {

		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		final String email = values.get("email")[0];
		final String password = values.get("password")[0];
		String config = null;

		try {
			MobileMySQLInstance msi = MobileMySQLInstance.getInstance();
			config = DBActions.loginCheck(msi.getConnection(), email, password);

		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

		JSONObject resJson = new JSONObject();
		try {
			if (config != null)
				resJson.put(ConstantUtil.JSON_CONFIG, config);
			else
				resJson.put(ConstantUtil.JSON_RESULT, "not ok");
		} catch (Exception e) {

		}
		System.out.println(resJson.toString());
		return ok(resJson.toString());
	}

	public static Result connectionTest() {
		System.out
				.println("Connection tested at " + System.currentTimeMillis());
		return ok("connected");
	}

	/******************************* Upload file ****************************/
	public static Result upload() {
		String folder="airsign";

		try {
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart filePart = body.getFile("uploaded_file");
			System.out.println("Upload called");
			if (filePart != null) {
				System.out.println("FilePart is not null");
				String fileName = filePart.getFilename();
				String contentType = filePart.getContentType();
				File file = filePart.getFile();

				//String[] str = fileName.split("_");
				
				FileInputStream fis = new FileInputStream(file);
				
				//folder = "phoneuploads/"+folder+"/"+str[0];
				folder = "phoneuploads/";
				File f = new File(folder);
				if(!f.exists())
					f.mkdirs();
				FileOutputStream fos = new FileOutputStream(folder+"/"+ fileName);

				// int maxBufferSize=1024*1024;
				int bytesAvailable = fis.available();
				System.out.println(bytesAvailable);
				// int bufferSize = Math.min(bytesAvailable, maxBufferSize);

				byte[] buffer = new byte[bytesAvailable];
				int nRead = 0;
				while ((nRead = fis.read(buffer)) != -1) {
					fos.write(buffer);
				}

				fos.flush();
				fis.close();
				fos.close();

				return ok("File uploaded");
			} else {
				return badRequest("error uploading file, filepart is null");
			}
		} catch (Exception ex) {
			System.out.println("Exception in upload: " + ex.toString());
			return badRequest("exception occured");
		}

	}

	/******************************* Web site calls **************************/
	public static Result web_home() {
		if (session("mobile_web_user_id") == null) {
			System.out.println("Session Id is null inside home");
			return redirect("/mobile_web_login");
		}
		return ok(views_mobile.html.mobile_home.render(
				session("mobile_web_user_code"), session("mobile_web_user_id")));
	}

	public static Result web_ajax() {

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

			responseJson = ProcessMobileWebReuqest.processRequest(requestJson,
					user_id);

		} catch (Exception ex) {
			System.out.println(ex.toString());
			responseJson = null;
		}

		if (responseJson != null) {
			// System.out.println("Response JSON: " + responseJson.toString());
			System.out.println("response provided");
			return ok(responseJson.toString());
		} else {
			System.out.println("Response JSON: "
					+ "{\"response_code\":0, \"html\":\"Error in Server\"}");
			return ok("{\"response_code\":0, \"html\":\"Error in Server\"}");
		}
	}

	public static Result web_login_check() {

		try {

			final Map<String, String[]> values = request().body()
					.asFormUrlEncoded();

			final String user_code = values.get("user_code")[0];
			final String password = values.get("password")[0];
			System.out.println("user_id: " + user_code + "  data type: "
					+ password);

			MobileMySQLInstance msi = MobileMySQLInstance.getInstance();
			int userId = MobileDBActions.checkLogin(msi.getConnection(),
					user_code, password);
			System.out.println("userId" + userId);
			if (userId > 0) {
				session("mobile_web_user_id", "" + userId);
				session("mobile_web_user_code", user_code);
				return ok("OK");
			} else
				return ok("NO");
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return ok("error");
		}

	}

	public static Result web_logout() {
		System.out.println("Logout called");
		session().remove("mobile_web_user_id");
		session().remove("mobile_web_user_code");
		return redirect("/mobile_web_login");
	}

	public static Result web_login() {
		if (session("mobile_web_user_id") != null) {
			return redirect("/mobile_web");
		}
		return ok(views_mobile.html.mobile_login.render());
	}

	/******************************* Airsign *********************************/

	public static Result airsign_login() {

		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();
		final String phoneNo = values.get("phoneNo")[0];
		final String pin = values.get("pin")[0];

		System.out.println("Phone: " + phoneNo + "  pin: " + pin);

		JSONObject resJson = new JSONObject();
		int user_id = 0;
		try {
			resJson.put(ConstantUtil.JSON_RESULT, "exception");
			MobileMySQLInstance msi = MobileMySQLInstance.getInstance();
			user_id = DBActions.airsignLoginCheck(msi.getConnection(), phoneNo,
					pin);

		} catch (Exception ex) {
			System.out.println(ex.toString());
			return ok(resJson.toString());
		}

		try {
			if (user_id>=0){
				resJson.put(ConstantUtil.JSON_RESULT, "ok");
				JSONObject config = new JSONObject();
				config.put("user_id", user_id);
				config.put("phone", phoneNo);
				config.put("train", false);				
				config.put("tth", 200);				
				resJson.put(ConstantUtil.JSON_CONFIG, config.toString());
			}
			else
				resJson.put(ConstantUtil.JSON_RESULT, "not ok");
		} catch (Exception e) {

		}
		System.out.println(resJson.toString());
		return ok(resJson.toString());
	}

	public static Result airsign_create_account() {

		final Map<String, String[]> values = request().body()
				.asFormUrlEncoded();

		final String phoneNo = values.get("phoneNo")[0];
		final String pin = values.get("pin")[0];
		final String yearOfBirth = values.get("yearOfBirth")[0];
		final String gender = values.get("gender")[0];

		System.out.println("Phone: " + phoneNo + "  pin: " + pin
				+ " yearOfBirth" + yearOfBirth + "  gender: " + gender);

		JSONObject resJson = new JSONObject();
		boolean result = false;

		try {
			resJson.put(ConstantUtil.JSON_RESULT, "exception");
			MobileMySQLInstance msi = MobileMySQLInstance.getInstance();
			result = DBActions.airsignCreateAccount(msi.getConnection(),
					phoneNo, pin, yearOfBirth, gender);
		} catch (Exception ex) {
			System.out.println("Exception calling MySql");
			return ok(resJson.toString());
		}

		try {
			if (result)
				resJson.put(ConstantUtil.JSON_RESULT, "ok");
			else
				resJson.put(ConstantUtil.JSON_RESULT, "not ok");
		} catch (Exception e) {
			System.out.println("Exception putting res");
			return ok(resJson.toString());
		}

		return ok(resJson.toString());
	}

}
