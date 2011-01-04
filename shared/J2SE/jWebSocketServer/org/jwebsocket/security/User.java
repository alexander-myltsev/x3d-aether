//	---------------------------------------------------------------------------
//	jWebSocket - User Class
//	Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.security;

import org.apache.log4j.Logger;

/**
 * implements a user with all its data fields and roles.
 * @author aschulze
 */
public class User {

	private static Logger log = Logger.getLogger(User.class);
	/**
	 * The maximum number of login tries until the account gets locked.
	 */
	public static int MAX_PWD_FAIL_COUNT = 3;
	/**
	 * The state of the user is unknown. This state is used only as default
	 * when instantiating a new user. This value should not be saved.
	 */
	public static int ST_UNKNOWN = -1;
	/**
	 * The user is already registered but not activated.
	 * A user needs to get activated to get access to the system.
	 */
	public static int ST_REGISTERED = 0;
	/**
	 * The user is activated and has access to the system according to his 
	 * rights and roles.
	 */
	public static int ST_ACTIVE = 1;
	/**
	 * The user is (temporarily) inactive.
	 * He needs to get (re-)activated to get access to the system.
	 */
	public static int ST_INACTIVE = 2;
	/**
	 * The user is (temporarily) locked, eg due to too much logins 
	 * with wrong credentials.
	 * He needs to gets unlocked again to get access to the system.
	 */
	public static int ST_LOCKED = 3;
	/**
	 * The user is deleted, he can't log in and is not reachable for others.
	 * The row is kept in the database for reference purposes only and
	 * to keep the database consistent (eg for logs, journal or transactions).
	 * He can be activated again to get access to the system.
	 */
	public static int ST_DELETED = 4;
	private Integer userId = null;
	private String loginname = null;
	private String title = null;
	private String company = null;
	private String firstname = null;
	private String lastname = null;
	private String password = null;
	private Integer pwdFailCount = 0;
	private int status = ST_UNKNOWN;
	private String defaultLocale = null;
	private String city = null;
	private String address = null;
	private String zipcode = null;
	private String country_code = null;
	private String email = null;
	private String phone = null;
	private String fax = null;
	private int sessionTimeout = 0;
	private Roles roles = new Roles();

	/**
	 * creates a new user instance by loginname, firstname, lastname, password
	 * and roles.
	 * @param aLoginName
	 * @param aFirstname
	 * @param aLastname
	 * @param aPassword
	 * @param aRoles
	 */
	public User(String aLoginName, String aFirstname, String aLastname, String aPassword, Roles aRoles) {
		loginname = aLoginName;
		firstname = aFirstname;
		lastname = aLastname;
		password = aPassword;
		roles = aRoles;
	}

	/**
	 * returns the id of the user. The id is supposed to be used for storing
	 * users in a database. It's the primary key.
	 * @return id of the user.
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * specifies the id of the user. The id is supposed to be used for storing
	 * users in a database. It's the primary key.
	 * @param userId
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * returns the login name of the user. The login name needs to be unique
	 * for each user. It's the key to identify a user within jWebSocket.
	 * @return
	 */
	public String getLoginname() {
		return loginname;
	}

	/**
	 * specifies the login name of the user. The login name needs to be unique
	 * for each user. It's the key to identify a user within jWebSocket.
	 * @param aLoginName
	 */
	public void setLoginname(String aLoginName) {
		this.loginname = aLoginName;
	}

	/**
	 * returns the title of the user (e.g. mr./mrs.).
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * specifies the title of the user (e.g. mr./mrs.).
	 * @param aTitle
	 */
	public void setTitle(String aTitle) {
		this.title = aTitle;
	}

	/**
	 * returns the company of the user.
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * specifies the company of the user.
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * returns the firstname of the user.
	 * @return
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * specifies the firstname of the user.
	 * @param firstName
	 */
	public void setFirstname(String firstName) {
		this.firstname = firstName;
	}

	/**
	 * returns the lastname of the user.
	 * @return
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * specifies the lastname of the user.
	 * @param lastName
	 */
	public void setLastname(String lastName) {
		this.lastname = lastName;
	}

	/**
	 * checks if the given password matches the user password, it is not
	 * possible to obtain the password for the user. If the password is not
	 * correct the fail counter is incremented. If the fail counter exceeds
	 * the configured maximum the account gets locked. If the password is
	 * correct the password fail counter is resetted.
	 * @param aPassword
	 * @return
	 */
	public boolean checkPassword(String aPassword) {
		boolean lOk = (aPassword != null && aPassword.equals(password));
		if( lOk ) {
			resetPwdFailCount();
		} else {
			incPwdFailCount();
		}
		return lOk;

	}

	/**
	 * changes the password of the user, to change it the caller needs to know
	 * the original password. For initialization purposes e.g. during the
	 * startup process the original password is null.
	 * The password cannot be reset to null.
	 * @param aOldPW original password or null of password is set first time.
	 * @param aNewPW new password, nust not be <tt>null</tt>.
	 * @return true if the password was changed successfully otherwise false.
	 */
	public boolean changePassword(String aOldPW, String aNewPW) {
		if (aOldPW != null
			&& aNewPW != null
			&& password.equals(aOldPW)) {
			password = aNewPW;
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return loginname + ": " + firstname + " " + lastname;
	}

	/**
	 * returns the roles of the user.
	 * @return
	 */
	public Roles getRoles() {
		return roles;
	}

	// TODO: potential security hole: don't allow to change roles w/o a special permission!
	/**
	 * specifies the roles of the user.
	 * @param aRoles
	 */
	public void setRoles(Roles aRoles) {
		this.roles = aRoles;
	}

	/**
	 * returns the user's current status (one of the ST_XXX constants).
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	// TODO: potential security hole: don't allow to e.g. unlock a user w/o a special permission!
	/**
	 * specifies the user's current status (one of the ST_XXX constants).
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * returns the user's current password fail counter. Please consider that
	 * since currently the users are stored in memory only the fail counter
	 * is reset after an application restart.
	 * @return
	 */
	public Integer getPwdFailCount() {
		return pwdFailCount;
	}

	// TODO: potential security hole: don't allow to reset password fail counter w/o a special permission!
	/**
	 * explicitly sets the password fail counter for the user.
	 * @param aPwdFailCount
	 */
	public void setPwdFailCount(Integer aPwdFailCount) {
		pwdFailCount = aPwdFailCount;
	}

	// TODO: potential security hole: don't allow to roll over password fail counter!
	/**
	 * increments the password fail counter. If the password fail counter
	 * exceeds the maximum value the user gets locked.
	 * This is called after the application passed an incorrect password to
	 * the checkPassword method.
	 * @return 
	 */
	private Integer incPwdFailCount() {
		setPwdFailCount(pwdFailCount + 1);
		if (pwdFailCount >= MAX_PWD_FAIL_COUNT) {
			lock();
		}
		return pwdFailCount;
	}

	// TODO: potential security hole: don't allow to reset password fail counter w/o a special permission!
	/**
	 * resets the password fail counter and saves the user back to the database.
	 * This is called after a successful authentication.
	 */
	private void resetPwdFailCount() {
		setPwdFailCount(0);
	}

	/**
	 * returns the default locale for the user.For future use in an
	 * internationalized environment.
	 * @return
	 */
	public String getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * specifies the default locale for the user. For future use in an
	 * internationalized environment.
	 * @param default_locale
	 */
	public void setDefaultLocale(String default_locale) {
		this.defaultLocale = default_locale;
	}

	/**
	 * returns the city of the user.
	 * @return
	 */
	public String getCity() {
		return city;
	}

	/**
	 * specifies the city of the user.
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * returns the address of the user.
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * specifies the address of the user.
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * returns the zip code of the user.
	 * @return
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * specifies the zip code of the user.
	 * @param zipcode
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * returns the country code of the user, either 2 digits like "DE" or
	 * 5 digits like "EN-US".
	 * @return
	 */
	public String getCountryCode() {
		return country_code;
	}

	/**
	 * specifies the country code of the user, either 2 digits like "DE" or
	 * 5 digits like "EN-US".
	 * @param country_code
	 */
	public void setCountryCode(String country_code) {
		this.country_code = country_code;
	}

	/**
	 * returns the phone number of the user.
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * specifies the phone number of the user.
	 * @param aPhone
	 */
	public void setPhone(String aPhone) {
		this.phone = aPhone;
	}

	/**
	 * returns the email address of the user.
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * specifies the email address of the user.
	 * @param aEmail
	 */
	public void setEmail(String aEmail) {
		this.email = aEmail;
	}

	/**
	 * returns the fax number of the user.
	 * @return
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * specifies the fax number of the user.
	 * @param aFax
	 */
	public void setFax(String aFax) {
		this.fax = aFax;
	}

	/**
	 * returns the individual session timeout for the user -
	 * not yet supported.
	 * @return
	 */
	public int getSessionTimeout() {
		return sessionTimeout;
	}

	/**
	 * specifies the individual session timeout for the user -
	 * not yet supported.
	 * @param session_timeout
	 */
	public void setSessionTimeout(int session_timeout) {
		this.sessionTimeout = session_timeout;
	}

	/**
	 * sets the user to locked state. He cannot login anymore after that.
	 */
	public void lock() {
		this.setStatus(ST_LOCKED);
	}

	// TODO: potential security hole: don't allow to unlock account w/o a special permission!
	/**
	 * Releases the user's locked state. He cannot login again after that.
	 */
	public void unlock() {
		this.setStatus(ST_ACTIVE);
	}

	/**
	 * checks if the user has a certain right. The right is passed as a string
	 * which associates the key of the right.
	 * @param aRight
	 * @return
	 */
	public boolean hasRight(String aRight) {
		return roles.hasRight(aRight);
	}
}
