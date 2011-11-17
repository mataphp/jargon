package org.irods.jargon.core.pub;

import java.util.List;

import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.ObjStat;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;

/**
 * This interface describes an access object that assists in the searching and
 * listing of collections and data objects, suitable for developing tree
 * depictions of an iRODS file system, and searching that file system on file
 * and collection names.
 * <p/>
 * This access object is being designed to support basic interface functionality
 * for both Swing and web GUI tree models, and basic search boxes for file or
 * collection names. More advanced searching based on metadata or other criteria
 * are available elsewhere in the API.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 * 
 */
public interface CollectionAndDataObjectListAndSearchAO extends IRODSAccessObject {

	/**
	 * This is a method that can support listing and paging of collections,
	 * suitable for creating interfaces that need to handle paging of large
	 * collections. Note that this method returns a simple value object that
	 * contains information about paging for each object. Clients of this method
	 * can inspect the returned results to determine the position of each result
	 * and whether there are more records to display.
	 * <p/>
	 * This method is not recursive, it only lists the collections under the
	 * given parent. The parent is an absolute path, this particular method does
	 * not 'search', rather it just lists.
	 * 
	 * @param absolutePathToParent
	 *            <code>String</code> with the absolute path to the parent. If
	 *            blank, the root is used. If the path is really a file, the
	 *            method will list from the parent of the file.
	 * @param partialStartIndex
	 *            <code>int</code> with the offset from which to start returning
	 *            results.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 * @throws FileNotFoundException
	 *             if the absolutePathToParent does not exist
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> listCollectionsUnderPath(
			final String absolutePathToParent, final int partialStartIndex)
			throws FileNotFoundException, JargonException;

	/**
	 * This is a method that can support listing and paging of files in a
	 * collection, suitable for creating interfaces that need to handle paging
	 * of large collections. Note that this method returns a simple value object
	 * that contains information about paging for each object. Clients of this
	 * method can inspect the returned results to determine the position of each
	 * result and whether there are more records to display.
	 * <p/>
	 * This method is not a search method, it simply lists.
	 * 
	 * @param absolutePathToParent
	 *            <code>String</code> with the absolute path to the parent. If
	 *            blank, the root is used. If the path is really a file, the
	 *            method will list from the parent of the file.
	 * @param partialStartIndex
	 *            <code>int</code> with the offset from which to start returning
	 *            results.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 * @throws FileNotFoundException
	 *             if the absolutePathToParent does not exist
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> listDataObjectsUnderPath(
			final String absolutePathToParent, final int partialStartIndex)
			throws FileNotFoundException, JargonException;

	/**
	 * This method is in support of applications and interfaces that need to
	 * support listing and paging of collections. This method returns a simple
	 * value object that contains information about paging for each object, such
	 * as record count, and whether this is the last record.
	 * <p/>
	 * Note that this collection is composed of a collection of objects for
	 * child collections, and a collection of objects for child data objects
	 * (subdirectories versus files). There are separate counts and
	 * 'isLastEntry' values for each type, discriminated by the
	 * <code>CollectionAndDataObjectListingEntry.objectType</code>. In usage,
	 * this method would be called for the parent directory under which the
	 * subdirectories and files should be listed. The response will include the
	 * sum of both files and subdirectories, and each type may have more
	 * results. Once this result is returned, the
	 * <code>listDataObjectsUnderPath</code> and
	 * <code>listCollectionsUnderPath</code> methods may be called separately
	 * with a partial start index value as appropriate. It is up to the caller
	 * to determine which types need paging.
	 * 
	 * @param absolutePathToParent
	 *            <code>String</code> with the absolute path to the parent. If
	 *            blank, the root is used. If the path is really a file, the
	 *            method will list from the parent of the file.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         containing both files and collections
	 * @throws FileNotFoundException
	 *             if the given path does not exist
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> listDataObjectsAndCollectionsUnderPath(
			final String absolutePathToParent) throws FileNotFoundException,
			JargonException;

	/**
	 * This method is in support of applications and interfaces that need to
	 * support listing and paging of collections. This method returns a simple
	 * count of the children (data objects and collections) underneath this
	 * directory.
	 * 
	 * @param absolutePathToParent
	 *            <code>String</code> with the absolute path to the parent. The
	 *            parent must be a collection or an error is thrown
	 * @return <code>int</code> with a count of the files that are children of
	 *         the parent.
	 * @throws FileNotFoundException
	 *             if the given absolutePathToParent does not exist
	 * @throws JargonException
	 */
	int countDataObjectsAndCollectionsUnderPath(
			final String absolutePathToParent) throws JargonException;

	/**
	 * Provides a search capability to search for any collections that have a
	 * match on the search term. The typical case would be a search box on a
	 * form to find all collections that have the given string.
	 * <p/>
	 * Note that this will do a genquery like:
	 * 
	 * <pre>
	 * COL_COLL_NAME like '%thepathyougiveforsearch%'
	 * </pre>
	 * 
	 * @param searchTerm
	 *            <code>String</code> that is the path search term, note that
	 *            the "%" is added in the method and should not be provided as a
	 *            parameter.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         containing collections that match the search term * @throws
	 *         JargonException
	 */
	List<CollectionAndDataObjectListingEntry> searchCollectionsBasedOnName(
			String searchTerm) throws JargonException;

	/**
	 * Provides a search capability to search for any collections that have a
	 * match on the search term. The typical case would be a search box on a
	 * form to find all collections that have the given string.
	 * <p/>
	 * Note that this will do a genquery like:
	 * <p/>
	 * 
	 * <pre>
	 * COL_COLL_NAME like '%thepathyougiveforsearch%'
	 * </pre>
	 * 
	 * @param searchTerm
	 *            <code>String</code> that is the path search term, note that
	 *            the "%" is added in the method and should not be provided as a
	 *            parameter.
	 * @param <code>int</code> with a partial start index of 0 or greater that
	 *        indicates the offset into the returned results, suitable for
	 *        paging.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         containing collections that match the search term * @throws
	 *         JargonException
	 */
	List<CollectionAndDataObjectListingEntry> searchCollectionsBasedOnName(
			String searchTerm, int partialStartIndex) throws JargonException;

	/**
	 * Provides a search capability to search for any data objects that have a
	 * match on the given search term. The typical case would be a search box on
	 * a form to find all data objects that have the given string in the name.
	 * <p/>
	 * Note that this will do a genquery like:
	 * <p/>
	 * 
	 * <pre>
	 * WHERE DATA_NAME LIKE '%searchTerm%'
	 * </pre>
	 * 
	 * @param searchTerm
	 *            <code>String</code> that is the path search term, note that
	 *            the "%" is added in the method and should not be provided as a
	 *            parameter.
	 * @param partialStartIndex
	 *            <code>int</code> with an offset into the results.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         containing data objects that match the search term
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> searchDataObjectsBasedOnName(
			String searchTerm, int partialStartIndex) throws JargonException;

	/**
	 * Provides a search capability to search for any data objects that have a
	 * match on the given search term. The typical case would be a search box on
	 * a form to find all data objects that have the given string in the name.
	 * <p/>
	 * Note that this will do a genquery like:
	 * <p/>
	 * 
	 * <pre>
	 * WHERE DATA_NAME LIKE '%searchTerm%'
	 * </pre>
	 * 
	 * @param searchTerm
	 *            <code>String</code> that is the path search term, note that
	 *            the "%" is added in the method and should not be provided as a
	 *            parameter.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         containing data objects that match the search term
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> searchDataObjectsBasedOnName(
			String searchTerm) throws JargonException;

	/**
	 * Provides a search capability to search for any collections and data
	 * objects that have a match on the given search term. The typical case
	 * would be a search box on a form to find all data objects that have the
	 * given string in the name. This method creates the union of a search on
	 * both data objects and collections. Note that for data objects and
	 * collections, there might separately be further paging available. In
	 * typical usage, this method can be called. The result collections can be
	 * inspected using the methods defined in
	 * {@link org.irods.jargon.core.pub.domain.IRODSDomainObject} to see if more
	 * results are available.
	 * <p/>
	 * Note that this will do a genquery like:
	 * <p/>
	 * 
	 * <pre>
	 * WHERE DATA_NAME LIKE '%searchTerm%'
	 * </pre>
	 * 
	 * @param searchTerm
	 *            <code>String</code> that is the path search term, note that
	 *            the "%" is added in the method and should not be provided as a
	 *            parameter.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         containing data objects that match the search term
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> searchCollectionsAndDataObjectsBasedOnName(
			String searchTerm) throws JargonException;

	/**
	 * Handy method will get the full domain object, {@link DataObject} or
	 * {@link Collection}, based on the given absolute path. This can be handy
	 * for display in interfaces or other applications that are concerned with
	 * retrieving 'info' about a given path.
	 * 
	 * @param objectAbsolutePath
	 *            <code>String</code> with the absolute path to the given data
	 *            object or collection.
	 * @return <code>Object</code> that will be either a <code>DataObject</code>
	 *         or <code>Collection</code> object based on the object at the
	 *         given absolute path in iRODS.
	 * @throws FileNotFoundException
	 *             , if no data object or collection found for the given path.
	 *             The method does not return null in this case
	 * @throws FileNotFoundException
	 *             if the given objectAbsolutePath does not exist
	 * @throws JargonException
	 */
	Object getFullObjectForType(String objectAbsolutePath)
			throws FileNotFoundException, JargonException;

	/**
	 * Retrieve a list of collections (not data objects) underneath a given
	 * parent path, with the user ACL permissions displayed. This is equivalent
	 * to the ls -la results. The returned
	 * <code>CollectionAndDataObjectListingEntry</code> objects will have a
	 * collection of <code>UserFilePermission</code> objects that detail the
	 * permissions.
	 * 
	 * @param absolutePathToParent
	 *            <code>String</code> with the absolute path to the parent. If
	 *            blank, the root is used. If the path is really a file, the
	 *            method will list from the parent of the file.
	 * @param partialStartIndex
	 *            <code>int</code> with the offset from which to start returning
	 *            results.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         including file permissions
	 * @throws FileNotFoundException
	 *             if the given absolutePathToParent does not exist
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> listCollectionsUnderPathWithPermissions(
			String absolutePathToParent, int partialStartIndex)
			throws FileNotFoundException, JargonException;

	/**
	 * This is a method that can support listing and paging of data objects in a
	 * collection, including ACL information. This is suitable for creating
	 * interfaces that need to handle paging of large collections. Note that
	 * this method returns a simple value object that contains information about
	 * paging for each object. Clients of this method can inspect the returned
	 * results to determine the position of each result and whether there are
	 * more records to display.
	 * <p/>
	 * This method is not a search method, it simply lists.
	 * 
	 * @param absolutePathToParent
	 *            <code>String</code> with the absolute path to the parent. If
	 *            blank, the root is used. If the path is really a file, the
	 *            method will list from the parent of the file.
	 * @param partialStartIndex
	 *            <code>int</code> with the offset from which to start returning
	 *            results.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         with included per-user ACL information
	 * @throws FileNotFound
	 *             exception if the given absolutePathToParent does not exist
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> listDataObjectsUnderPathWithPermissions(
			String absolutePathToParent, int partialStartIndex)
			throws FileNotFoundException, JargonException;

	/**
	 * This method is in support of applications and interfaces that need to
	 * support listing and paging of collections. This method returns a simple
	 * value object that contains information about paging for each object, such
	 * as record count, and whether this is the last record. This method adds
	 * the user ACL information, which is derived from an extended query.
	 * <p/>
	 * This method is meant for listings, or building trees. As such, it does
	 * not show any information about replicas, rather, it groups the data by
	 * data object path for all replicas.
	 * <p/>
	 * Note that this collection is composed of a collection of objects for
	 * child collections, and a collection of objects for child data objects
	 * (subdirectories versus files). There are separate counts and
	 * 'isLastEntry' values for each type, discriminated by the
	 * <code>CollectionAndDataObjectListingEntry.objectType</code>. In usage,
	 * this method would be called for the parent directory under which the
	 * subdirectories and files should be listed. The response will include the
	 * sum of both files and subdirectories, and each type may have more
	 * results. Once this result is returned, the
	 * <code>listDataObjectsUnderPath</code> and
	 * <code>listCollectionsUnderPath</code> methods may be called separately
	 * with a partial start index value as appropriate. It is up to the caller
	 * to determine which types need paging.
	 * 
	 * @param absolutePathToParent
	 *            <code>String</code> with the absolute path to the parent. If
	 *            blank, the root is used. If the path is really a file, the
	 *            method will list from the parent of the file.
	 * @return <code>List</code> of
	 *         {@link org.irods.jargon.core.query.CollectionAndDataObjectListingEntry}
	 *         containing both files and collections
	 * @throws FileNotFoundException
	 *             if the given absolutePathToParent does not exist
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> listDataObjectsAndCollectionsUnderPathWithPermissions(
			String absolutePathToParent) throws FileNotFoundException,
			JargonException;

	/**
	 * TODO: work in progress
	 * 
	 * @param absolutePathToParent
	 * @param userName
	 * @param partialStartIndex
	 * @return
	 * @throws JargonException
	 */
	List<CollectionAndDataObjectListingEntry> listDataObjectsSharedWithAGivenUser(
			String absolutePathToParent, String userName, int partialStartIndex)
			throws JargonException;

	/**
	 * Retrieve the <code>ObjStat</code> for a collection or data object at the
	 * given absolute path in iRODS. This is the result of a call to rsObjStat.
	 * 
	 * @param irodsAbsolutePath
	 *            <code>String</code> with the absolute path to an iRODS
	 *            collection or data object.
	 * @return {@link ObjStat} with object data, or <code>null</code> if no data
	 *         is found.
	 * @throws JargonException
	 */
	ObjStat retrieveObjectStatForPath(String irodsAbsolutePath)
			throws JargonException;

}