package Web;

public class HTMLGenerator {

	public static String activityTable(int[] data) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < data.length; i++) {			
			builder.append("<div class='divActivityBar' style='background-color:");
			builder.append(getColor(data[i]));
			builder.append(";'>");
			builder.append("</div>");
		}
		
//		builder.append("<table id='tableActivity'>");
//		builder.append("<tr id='trTableActivity'>");
//
//		for (int i = 0; i < data.length; i++) {
//			builder.append("<td id='tdTableActivity" + (i + 1)
//					+ "' class='tdTableActivity' bgcolor='" + getColor(data[i])
//					+ "'>");
//			builder.append("</td>");
//		}
//
//		builder.append("</tr>");
//		builder.append("</table>");

		return builder.toString();
	}

	public static String getColor(int code) {
		
		//System.out.print(" "+code+ " ");

		if (code == 1)
			return "#ABABAB";
		else if (code == 2)
			return "#123456";
		else if (code == 3)
			return "#00FF00";
		else if (code == 4)
			return "#FF0000";
		else if (code == 5)
			return "#0000FF";
		else if (code == 6)
			return "#ABCDEF";
		else
			return "#FFFFFF";
	}
}