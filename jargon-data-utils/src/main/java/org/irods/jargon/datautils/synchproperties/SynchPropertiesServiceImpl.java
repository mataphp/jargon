package org.irods.jargon.datautils.synchproperties;

import java.util.ArrayList;
import java.util.List;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.DataNotFoundException;
import org.irods.jargon.core.exception.DuplicateDataException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.domain.AvuData;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.AVUQueryElement;
import org.irods.jargon.core.query.AVUQueryElement.AVUQueryPart;
import org.irods.jargon.core.query.AVUQueryOperatorEnum;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to maintain synchronization properties for client->iRODS data
 * synchronization
 * 
 * @author Mike Conway - DICE (www.irods.org)
 * 
 */
public class SynchPropertiesServiceImpl implements SynchPropertiesService {

	public static final Logger log = LoggerFactory
			.getLogger(SynchPropertiesServiceImpl.class);

	/**
	 * Factory to create necessary Jargon access objects, which interact with
	 * the iRODS server
	 */
	private IRODSAccessObjectFactory irodsAccessObjectFactory;

	/**
	 * Describes iRODS server and account information
	 */
	private IRODSAccount irodsAccount;

	/**
	 * Default (no-values) constructor.  The account and <code>IRODSFileSystem</code> need to be initialized va the setter methods.
	 */
	public SynchPropertiesServiceImpl() {
		super();
	}
	/**
	 * Constructor initializes dependencies.  These can also be set after using the default constructor.
	 * @param irodsAccessObjectFactory <code>IRODSAccessObjectFactory</code> that can create various access objects to interact with iRODS
	 * @param irodsAccount <code>IRODSAccount</code> that describes the user and server to connect to
	 */

	public SynchPropertiesServiceImpl(
			IRODSAccessObjectFactory irodsAccessObjectFactory,
			IRODSAccount irodsAccount) {
		super();
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
		this.irodsAccount = irodsAccount;
	}

	@Override
	public IRODSAccessObjectFactory getIrodsAccessObjectFactory() {
		return irodsAccessObjectFactory;
	}

	@Override
	public void setIrodsAccessObjectFactory(
			final IRODSAccessObjectFactory irodsAccessObjectFactory) {
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
	}

	@Override
	public IRODSAccount getIrodsAccount() {
		return irodsAccount;
	}

	@Override
	public void setIrodsAccount(final IRODSAccount irodsAccount) {
		this.irodsAccount = irodsAccount;
	}

	/*
	 * attrib | value | unit
	 * 
	 * in home dir
	 * 
	 * [device name] | [user name] | iRODSSynch:userDevice
	 * 
	 * in synch root dir
	 * 
	 * [device name]:[user name] | [lastLocalSynch | lastIrodsSynch |
	 * localAbsPath] | iRODSSynch:userSynchDir
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.irods.jargon.datautils.synchproperties.SynchPropetiesService#
	 * getUserSynchTargetForUserAndAbsolutePath(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public UserSynchTarget getUserSynchTargetForUserAndAbsolutePath(
			final String userName, final String deviceName,
			final String irodsAbsolutePath) throws DataNotFoundException,
			JargonException {

		if (userName == null || userName.isEmpty()) {
			throw new IllegalArgumentException("null or empty userName");
		}

		if (deviceName == null || deviceName.isEmpty()) {
			throw new IllegalArgumentException("null or empty deviceName");
		}

		if (irodsAbsolutePath == null || irodsAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException(
					"null or empty irodsAbsolutePath");
		}

		checkDependencies();

		log.info("getUserSynchTargetForUserAndAbsolutePath for user:{}",
				userName);
		log.info("    for absPath:{}", irodsAbsolutePath);

		CollectionAO collectionAO = irodsAccessObjectFactory
				.getCollectionAO(irodsAccount);

		String userDevAttrib = buildAvuAttribForSynchUtilTarget(userName,
				deviceName);

		List<AVUQueryElement> avuQuery = new ArrayList<AVUQueryElement>();
		List<MetaDataAndDomainData> queryResults;

		log.debug("building avu query");

		try {
			AVUQueryElement avuQueryElement = AVUQueryElement
					.instanceForValueQuery(AVUQueryPart.UNITS,
							AVUQueryOperatorEnum.EQUAL, USER_SYNCH_DIR_TAG);
			avuQuery.add(avuQueryElement);
			avuQueryElement = AVUQueryElement.instanceForValueQuery(
					AVUQueryPart.ATTRIBUTE, AVUQueryOperatorEnum.EQUAL,
					userDevAttrib);
			avuQuery.add(avuQueryElement);
			queryResults = collectionAO
					.findMetadataValuesByMetadataQueryForCollection(avuQuery,
							irodsAbsolutePath);
		} catch (JargonQueryException e) {
			log.error(
					"error creating query for lookup of user synch target data",
					e);
			throw new JargonException(e);
		}

		log.debug("result of query for synch target data:{}", queryResults);

		// there should be only one synch device. If there's more than one,
		// treat as an error for now
		if (queryResults.isEmpty()) {
			throw new DataNotFoundException(
					"no synch data for given user and device");
		} else if (queryResults.size() > 1) {
			throw new JargonException(
					"more than one synch entry found for given user and device");
		}

		MetaDataAndDomainData deviceInfo = queryResults.get(0);
		// split the data out of the avu data value

		String[] valueComponents = deviceInfo.getAvuValue().split("[~]");
		if (valueComponents.length != 3) {
			log.error("did not find 3 expected values in the avu value: {}",
					deviceInfo);
			throw new JargonException(
					"unexpected data in AVU value for UserSynchTarget");
		}

		long lastIrodsSynch;
		long lastLocalSynch;
		String localSynchAbsolutePath;

		try {
			lastIrodsSynch = Long.valueOf(valueComponents[0]);
			lastLocalSynch = Long.valueOf(valueComponents[1]);
		} catch (NumberFormatException nfe) {
			log.error(
					"unable to parse out timestamps from synch data in AVU value:{}",
					deviceInfo);
			throw new JargonException("UserSynchTarget parse error");
		}

		localSynchAbsolutePath = valueComponents[2];

		if (localSynchAbsolutePath == null || localSynchAbsolutePath.isEmpty()) {
			log.error("no local synch absolute path found in:{}", deviceInfo);
			throw new JargonException("no local synch absolute path");
		}

		UserSynchTarget userSynchTarget = new UserSynchTarget();
		userSynchTarget.setDeviceName(deviceName);
		userSynchTarget.setIrodsSynchRootAbsolutePath(irodsAbsolutePath);
		userSynchTarget.setLocalSynchRootAbsolutePath(localSynchAbsolutePath);
		userSynchTarget.setLastIRODSSynchTimestamp(lastIrodsSynch);
		userSynchTarget.setLastLocalSynchTimestamp(lastLocalSynch);
		userSynchTarget.setUserName(userName);

		log.info("build UserSynchTarget:{}", userSynchTarget);

		return userSynchTarget;

	}

	
	/**
	 * Ensure whether the required dependencies have been set
	 */
	private void checkDependencies() throws JargonException {

		if (irodsAccessObjectFactory == null) {
			throw new JargonException(
					"irodsAccessObjectFactory was not initialized");
		}

		if (irodsAccount == null) {
			throw new JargonException("irodsAccount was not initialized");
		}

	}

	/* (non-Javadoc)
	 * @see org.irods.jargon.datautils.synchproperties.SynchPropertiesService#addSynchDeviceForUserAndIrodsAbsolutePath(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addSynchDeviceForUserAndIrodsAbsolutePath(
			final String userName, final String deviceName,
			final String irodsAbsolutePath, final String localAbsolutePath)
			throws DuplicateDataException, JargonException {

		checkDependencies();

		if (userName == null || userName.isEmpty()) {
			throw new IllegalArgumentException("null or empty userName");
		}

		if (deviceName == null || deviceName.isEmpty()) {
			throw new IllegalArgumentException("null or empty deviceName");
		}

		if (irodsAbsolutePath == null || irodsAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException(
					"null or empty irodsAbsolutePath");
		}

		if (localAbsolutePath == null || localAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException(
					"null or empty localAbsolutePath");
		}

		log.info("addSynchDeviceForUserAndIrodsAbsolutePath");
		log.info("   userName:{}", userName);
		log.info("   deviceName:{}", deviceName);
		log.info("   irodsAbsolutePath:{}", irodsAbsolutePath);
		log.info("   localAbsolutepath:{}", localAbsolutePath);

		log.info("checking for already-added synch device");

		try {
			UserSynchTarget userSynchTarget = getUserSynchTargetForUserAndAbsolutePath(
					userName, deviceName, irodsAbsolutePath);
			log.error("userSynchTarget already defined:{}", userSynchTarget);
			throw new DuplicateDataException(
					"cannot add userSyncTarget, already defined");
		} catch (DataNotFoundException dnf) {
			log.debug("no duplicate found, this is normal");
		}
		
		log.info("checking to see if irods collection exists, if it does not, then create it and tag with AVU data, path:{}", irodsAbsolutePath);
		
		// see if the irods directory exists, create if it does not
		IRODSFile irodsCollection = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(irodsAbsolutePath);
		
		if (!irodsCollection.exists()) {
			log.info("creating collection, does not exist");
			irodsCollection.mkdirs();
		}
		
		// TODO: what timestamp vals here?  I'm thinking 0 as it hasn't been synched yet, this would be updated at synch completes
		log.info("adding marker avu for target collection");
		AvuData marker = AvuData.instance(buildAvuAttribForSynchUtilTarget(userName, deviceName),
				buildAvuValueForSynchUtilTarget(0L, 0L, localAbsolutePath), 
				USER_SYNCH_DIR_TAG);
		
		CollectionAO collectionAO = irodsAccessObjectFactory
		.getCollectionAO(irodsAccount);
		
		collectionAO.addAVUMetadata(irodsAbsolutePath, marker);
		log.info("marker added:{}", marker);

	}
	
	/**
	 * @param userName
	 * @param deviceName
	 * @return
	 */
	private String buildAvuAttribForSynchUtilTarget(
			final String userName, final String deviceName) {
		StringBuilder userDevAttrib = new StringBuilder();
		userDevAttrib.append(userName);
		userDevAttrib.append(":");
		userDevAttrib.append(deviceName);
		return userDevAttrib.toString();
	}
	
	private String buildAvuValueForSynchUtilTarget(final long localLastSynchTimestamp, final long irodsLastSynchTimestamp, final String localAbsolutePath) {
		StringBuilder sb = new StringBuilder();
		sb.append(localLastSynchTimestamp);
		sb.append(SEPARATOR);
		sb.append(irodsLastSynchTimestamp);
		sb.append(SEPARATOR);
		sb.append(localAbsolutePath);
		return sb.toString();
	}


}