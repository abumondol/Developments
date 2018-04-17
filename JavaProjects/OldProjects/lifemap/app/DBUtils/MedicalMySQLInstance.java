package DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import utils.UtilFunctions;

public class MedicalMySQLInstance {
	private static MedicalMySQLInstance instance = null;
	private Connection connect = null;

	protected MedicalMySQLInstance() throws Exception {
		// this will load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		// setup the connection with the DB.
		connect = DriverManager.getConnection("jdbc:mysql://localhost/medical?"
				+ "user=root&password=abCD1234");
	}

	public static MedicalMySQLInstance getInstance() throws Exception {
		if (instance == null) {
			instance = new MedicalMySQLInstance();
		}
		return instance;
	}

	public Connection getConnection() throws Exception{
		if(connect==null || connect.isValid(0)==false)
			connect = DriverManager.getConnection("jdbc:mysql://localhost/medical?"
					+ "user=root&password=abCD1234");	
		return connect;
	}

	public void close() {
		UtilFunctions.close(connect);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			UtilFunctions.close(connect);
		} catch (Throwable t) {
			throw t;
		} finally {
			super.finalize();
		}
	}

}
