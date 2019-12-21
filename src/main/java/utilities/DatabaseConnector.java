package utilities;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;

/**
 * This class is designed to TEST DATABASE CONFIGURATION.
 * A database.properties file with username, password, database, and hostname is required.
 * There must be a tunnel to stargate.cs.usfca.edu running if you are off-campus.
 */
public class DatabaseConnector {

	/**
	 * URI to use when connecting to database. Should be in the format:
	 * jdbc:subprotocol://hostname/database
	 */
	public final String uri;

	/** Properties with username and password for connecting to database. */
	private final Properties dbProperties;

	/**
	 * Default Constructor
	 * Creates a connector from a "database.properties" file located in the
	 * current working directory.
	 *
	 * @throws IOException if unable to properly parse properties file
	 * @throws FileNotFoundException if properties file not found
	 */
	public DatabaseConnector() throws FileNotFoundException, IOException {
		this("database.properties");
	}

	/**
	 * Creates a connector from the provided database properties file.
	 *
	 * @param configPath path to the database properties file
	 * @throws IOException if unable to properly parse properties file
	 * @throws FileNotFoundException if properties file not found
	 */
	public DatabaseConnector(String configPath)
			throws FileNotFoundException, IOException {

		// Try to load the configuration from file
		Properties config = loadConfig(configPath);

		// Create database URI in proper format
		uri = String.format("jdbc:mysql://%s/%s",
				config.getProperty("hostname"),
				config.getProperty("database"))+ "?serverTimezone=UTC";

		System.out.println("uri = " + uri);

		// Create database login properties
		dbProperties = new Properties();

		dbProperties.put("user", config.getProperty("username"));
		dbProperties.put("password", config.getProperty("password"));
	}

	/**
	 * Attempts to load properties file with database configuration. Must
	 * include username, password, database, and hostname.
	 *
	 * @param configPath path to database properties file
	 * @return database properties
	 * @throws IOException if unable to properly parse properties file
	 * @throws FileNotFoundException if properties file not found
	 */
	private Properties loadConfig(String configPath)
			throws FileNotFoundException, IOException {

		// Specify which keys must be in properties file
		Set<String> required = new HashSet<>();
		required.add("hostname");
		required.add("username");
		required.add("password");
		required.add("database");

		// Load properties file
		Properties config = new Properties();
		config.load(new FileReader(configPath));

		System.out.println("config.keySet() "+config.keySet());

		// Check that required keys are present
		if (!config.keySet().containsAll(required)) {
			String error = "Must provide the following in properties file: ";
			throw new InvalidPropertiesFormatException(error + required);
		}

		return config;
	}

	/**
	 * Attempts to connect to database using loaded configuration.
	 *
	 * @return database connection
	 * @throws SQLException if unable to establish database connection
	 */
	public Connection getConnection() throws SQLException {
		Connection dbConnection = DriverManager.getConnection(uri, dbProperties);
		return dbConnection;
	}

	/**
	 * Opens a database connection and returns a set of found tables. Will
	 * return an empty set if there are no results.
	 *
	 * @return set of tables
	 */
	public Set<String> getTables(Connection db) throws SQLException {
		// Set type is used becuase getResultSet() below will give a Set
		Set<String> tableNames = new HashSet<>();

		// Statement createStatement() throws SQLException;
		/*	Creates a Statement object for sending
			SQL statements to the database.
		 */

		try (Statement stmt = db.createStatement();) {
			if (stmt.execute("SHOW TABLES;")) {
				ResultSet results = stmt.getResultSet();

				while (results.next()) {
					tableNames.add(results.getString(1));
				}
			}

		}
		return tableNames;
	}

	/**
	 * Opens a database connection, executes a simple statement, and closes
	 * the database connection.
	 *
	 * @return true if all operations successful
	 */
	public boolean testConnection() {
		boolean tablesExist = false;
		// Using a getter - Not DriverManager.getConnection(url,properties obj)
		// Open database connection and close when done
		try (Connection db = getConnection();) {
			System.out.println("Executing SHOW TABLES...");
			Set<String> tables = getTables(db);

			if (tables != null && tables.size()>5) {
				System.out.print("Found " + tables.size() + " tables: ");
				System.out.println(tables);
				tablesExist = true;
			}
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return tablesExist;
	}

	/**
	 * Tests whether database configuration (including tunnel) is correct. If
	 * you see the message "Connection to database established" then your
	 * settings are correct
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		try {
		    DatabaseConnector connector = new DatabaseConnector("database.properties");
			System.out.println("Connecting to " + connector.uri);

			if (connector.testConnection()) {
				System.out.println("Connection to database established.");

			}
			else {
				System.err.println("Unable to connect properly to database.");
			}
		}
		catch (Exception e) {
			System.err.println("Unable to connect properly to database.");
			System.err.println(e.getMessage());
		}
	}
}