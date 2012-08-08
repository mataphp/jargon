package org.irods.jargon.userprofile;

import org.irods.jargon.core.exception.DuplicateDataException;
import org.irods.jargon.core.exception.JargonException;

/**
 * Interface for a service to query and maintain user profile information.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 * 
 */
public interface UserProfileService {

	/**
	 * Add a profile for the given user.
	 * 
	 * @param irodsUserName
	 *            <code>String</code> with the name of the iRODS user in the
	 *            given zone for which a profile will be added
	 * @param userProfile
	 *            {@link UserProfile} that will be added
	 * @throws UserProfileValidationException
	 *             if the data in the profile is invalid
	 * @throws DuplicateDataException
	 *             if the profile already exists
	 * @throws JargonException
	 *             general exception
	 */
	void addProfileForUser(final String irodsUserName,
			final UserProfile userProfile)
			throws UserProfileValidationException, DuplicateDataException,
			JargonException;

	/**
	 * Get the configuration for the user profile service
	 * 
	 * @return {@link UserProfileServiceConfiguration}
	 */
	UserProfileServiceConfiguration getUserProfileServiceConfiguration();

	/**
	 * Set the configuration for the user profile service
	 * 
	 * @param userProfileServiceConfiguration
	 *            {@link UserProfileServiceConfiguration}
	 */
	void setUserProfileServiceConfiguration(
			UserProfileServiceConfiguration userProfileServiceConfiguration);

	/**
	 * Remove the public and protected user profile information
	 * 
	 * @param irodsUserName
	 *            <code>String</code> with the name of the iRODS user in the
	 *            given zone for which a profile will be removed
	 * @throws JargonException
	 */
	void removeProfileInformation(String irodsUserName) throws JargonException;

}