package utilities;

/**
 * Creates a Status enum type for tracking errors. Each Status enum type
 * will use the ordinal as the error code, and store a message describing
 * the error.
 */
public enum Status {
	/*
	 * Creates several Status enum types. The Status name and message is
	 * given in the NAME(message) format below. The Status ordinal is
	 * determined by its position in the list. (For example, OK is the
	 * first element, and will have ordinal 0.)
	 */

	// ordinal - Serial Number
	// Starts from 0 - 0,1,2,3,..

	OK("No errors occured."),
	ERROR("Unknown error occurred."),
	MISSING_CONFIG("Unable to find configuration file."),
	MISSING_VALUES("Missing values in configuration file."),
	CONNECTION_FAILED("Failed to establish a database connection."),
	CREATE_FAILED("Failed to create necessary table(s)."),
	INVALID_LOGIN("Invalid username and/or password."),
	INVALID_USER("User does not exist."),
	DUPLICATE_USER("User with that username already exists."),
	SQL_EXCEPTION("Unable to execute SQL statement."),
	PWD_NOT_MATCH("Passwords don't match"),
	PWD_PATTERN_MISMATCH("Password don't match the required format");

	private final String message;

	private Status(String message) {
		this.message = message;
	}

	/**
	 * Return Message for specific error
	 * @return message
	 */
	public String message() {
		return message;
	}

	/**
	 * Returns the message in string format
	 * @return message string
	 */
	@Override
	public String toString() {
		return this.message;
	}
}