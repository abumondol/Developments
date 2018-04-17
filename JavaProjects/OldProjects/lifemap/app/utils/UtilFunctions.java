package utils;

import org.json.JSONObject;

public class UtilFunctions {

	static public void close(AutoCloseable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (Exception e) {
			System.out.println("Can't close" + c.getClass());
		}
	}

	static public void putJsonString(JSONObject json, String key, String str)
			throws Exception {

		if (str == null)
			json.put(key, JSONObject.NULL);
		else
			json.put(key, str);
	}

	public static int getMaxIndex(int[] data) {

		int max = 0;
		int index = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] > max) {
				index = i;
				max = data[i];
			}
		}

		return max;
	}

	public static void intializeActivityArray(int[] data) {		
		for (int i = 0; i < data.length; i++)
			data[i] = 0;
	}

}
