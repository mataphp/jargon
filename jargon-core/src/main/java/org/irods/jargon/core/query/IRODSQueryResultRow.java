/**
 *
 */
package org.irods.jargon.core.query;

import java.util.Date;
import java.util.List;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.utils.IRODSDataConversionUtil;

/**
 * Represents a row in a query response, with convenience methods to access
 * attributes
 *
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
public class IRODSQueryResultRow {

	private final List<String> queryResultColumns;
	/**
	 * index of this record in the results
	 */
	private final int recordCount;
	private final boolean lastResult;
	private final List<String> columnNames;

	/**
	 * Build a result row from a column of results produced by an IRODS GenQuery
	 *
	 * @param queryResultColumns
	 *            {@code List} of query result columns
	 * @param columnNames
	 *            {@code List<String>} containing the column names.
	 * @return {@code IRODSQueryResultRow} with the data for this row.
	 * @throws JargonException
	 */
	public static IRODSQueryResultRow instance(
			final List<String> queryResultColumns,
			final List<String> columnNames) throws JargonException {
		return new IRODSQueryResultRow(queryResultColumns, columnNames, 0,
				false);
	}

	/**
	 * Build a result row from a column of results produced by an IRODS
	 * GenQuery. This initializer will add information about the position of the
	 * record to assist in re-query operations
	 *
	 * @param queryResultColumns
	 *            {@code List<String} with the values for each column of
	 *            the query
	 * @param columnNames
	 *            {@code List<String} with the values for each column name
	 * @param recordCount
	 *            {@code int} with the index of the current record
	 * @param lastResult
	 *            {@code boolean} of {@code true} if there are more
	 *            records to page
	 * @return {@link IRODSQueryResultRow}
	 * @throws JargonException
	 */
	public static IRODSQueryResultRow instance(
			final List<String> queryResultColumns,
			final List<String> columnNames, final int recordCount,
			final boolean lastResult) throws JargonException {
		return new IRODSQueryResultRow(queryResultColumns, columnNames,
				recordCount, lastResult);
	}

	/**
	 * Build a result row from a column of results produced by an IRODS
	 * GenQuery. This initializer will add information about the position of the
	 * record to assist in re-query operations
	 *
	 * @param queryResultColumns
	 *            {@code List<String} with the values for each column of
	 *            the query
	 * @param columnNames
	 *            {@code List<String} with the values for each column name
	 * @param recordCount
	 *            {@code int} with the index of the current record
	 * @param lastResult
	 *            {@code boolean} of {@code true} if there are more
	 *            records to page
	 * @param totalRecords
	 *            {@code int} with the total records that will be returned
	 * @return {@link IRODSQueryResultRow}
	 * @throws JargonException
	 */
	public static IRODSQueryResultRow instance(
			final List<String> queryResultColumns,
			final List<String> columnNames, final int recordCount,
			final boolean lastResult, final int totalRecords)
			throws JargonException {
		return new IRODSQueryResultRow(queryResultColumns, columnNames,
				recordCount, lastResult);
	}

	/**
	 * Private constructor
	 *
	 * @param queryResultColumns
	 *            {@code List<String} with the values for each column of
	 *            the query
	 * @param translatedIRODSQuery
	 *            {@link TranslatedIRODSQuery} that produced this result
	 * @param recordCount
	 *            {@code int} with the index of the current record
	 * @param lastResult
	 *            {@code boolean} of {@code true} if there are more
	 *            records to page
	 * @throws JargonException
	 */
	private IRODSQueryResultRow(final List<String> queryResultColumns,
			final List<String> columnNames, final int recordCount,
			final boolean lastResult) throws JargonException {

		if (queryResultColumns == null) {
			throw new JargonException("queryResultColumns is null");
		}

		if (columnNames == null) {
			throw new JargonException("columnNames is null");
		}

		this.queryResultColumns = queryResultColumns;
		this.lastResult = lastResult;
		this.recordCount = recordCount;
		this.columnNames = columnNames;

	}

	/**
	 * Given a columnNumber, return the value of the column in the result set.
	 *
	 * @param columnNumber
	 *            {@code int} with the location of the desired field.
	 * @return {@code String} containing the value of the column. It is up
	 *         to the caller to cast to the appropriate type.
	 * @throws JargonException
	 *             Indicates that the column could not be located in the
	 *             results.
	 */
	public String getColumn(final int columnNumber) throws JargonException {
		if (columnNumber < 0 || columnNumber >= queryResultColumns.size()) {
			throw new JargonException("column out of range");
		}
		return queryResultColumns.get(columnNumber);
	}

	/**
	 * Given a columnName, return the value of the column in the result set.
	 *
	 * @param columnName
	 *            {@code String} with the name of the desired field. The
	 *            search is case-insensitive.
	 * @return {@code String} containing the value of the column. It is up
	 *         to the caller to cast to the appropriate type.
	 * @throws JargonException
	 *             Indicates that the column could not be located in the
	 *             results.
	 */
	public String getColumn(final String columnName) throws JargonException {
		if (columnName == null || columnName.length() == 0) {
			throw new JargonException("columnName is null or empty");
		}

		int idx = getColumnNamePosition(columnName);

		if (idx == -1) {
			throw new JargonException("column name not found in result set:"
					+ columnName);
		}

		return queryResultColumns.get(idx);

	}

	/**
	 * Get the given column as a {@code Date}, or as {@code null}
	 *
	 * @param column
	 *            {@code int} as column position
	 * @return {@code Data} {@link Date} or {@code null}
	 * @throws JargonException
	 */
	public Date getColumnAsDateOrNull(final int column) throws JargonException {
		return IRODSDataConversionUtil.getDateFromIRODSValue(getColumn(column));
	}

	/**
	 * Get the given column as a {@code Date}, or as {@code null}
	 *
	 * @param columnName
	 *            {@code String} as column name
	 * @return {@code Data} {@link Date} or {@code null}
	 * @throws JargonException
	 */
	public Date getColumnAsDateOrNull(final String columnName)
			throws JargonException {
		return IRODSDataConversionUtil
				.getDateFromIRODSValue(getColumn(getColumnNamePosition(columnName)));
	}

	/**
	 * Get the given column as a {@code int}, or as {@code 0}
	 *
	 * @param column
	 *            {@code int} as column position
	 * @return {@code int} or 0 if null
	 * @throws JargonException
	 */
	public int getColumnAsIntOrZero(final int column) throws JargonException {
		return IRODSDataConversionUtil
				.getIntOrZeroFromIRODSValue(getColumn(column));
	}

	/**
	 * Get the given column as a {@code Date}, or as {@code null}
	 *
	 * @param columnName
	 *            {@code String} as column name
	 * @return {@code int} or 0 if null
	 * @throws JargonException
	 */
	public int getColumnAsIntOrZero(final String columnName)
			throws JargonException {
		return IRODSDataConversionUtil
				.getIntOrZeroFromIRODSValue(getColumn(getColumnNamePosition(columnName)));
	}

	/**
	 * Get the given column as a {@code long}, or as {@code 0}
	 *
	 * @param column
	 *            {@code int} as column position
	 * @return {@code long} or 0 if null
	 * @throws JargonException
	 */
	public long getColumnAsLongOrZero(final int column) throws JargonException {
		return IRODSDataConversionUtil
				.getLongOrZeroFromIRODSValue(getColumn(column));
	}

	/**
	 * Get the given column as a {@code long}, or as {@code null}
	 *
	 * @param columnName
	 *            {@code String} as column name
	 * @return {@code long} or 0 if null
	 * @throws JargonException
	 */
	public long getColumnAsLongOrZero(final String columnName)
			throws JargonException {
		return IRODSDataConversionUtil
				.getLongOrZeroFromIRODSValue(getColumn(getColumnNamePosition(columnName)));
	}

	/**
	 * Returns the index of the column with the given name, or -1 if not found.
	 * The match is case-insensitive;
	 *
	 * @param columnName
	 * @return {@code int}
	 */
	protected int getColumnNamePosition(final String columnName) {
		int colPos = -1;
		int i = 0;
		for (String colNameInList : columnNames) {
			if (columnName.equals(colNameInList)) {
				colPos = i;
				break;
			}
			i++;
		}

		return colPos;
	}

	/**
	 * Handy method to just get the results as a {@code List} for use in
	 * forEach and other constructs
	 *
	 * @return {@code List<String>} containing the columns for this row.
	 */
	public List<String> getColumnsAsList() {
		return queryResultColumns;
	}

	public List<String> getQueryResultColumns() {
		return queryResultColumns;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public boolean isLastResult() {
		return lastResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 100;
		StringBuilder builder = new StringBuilder();
		builder.append("IRODSQueryResultRow [");
		if (queryResultColumns != null) {
			builder.append("queryResultColumns=")
					.append(queryResultColumns.subList(0,
							Math.min(queryResultColumns.size(), maxLen)))
					.append(", ");
		}
		builder.append("recordCount=").append(recordCount)
				.append(", lastResult=").append(lastResult).append(", ");
		if (columnNames != null) {
			builder.append("columnNames=")
					.append(columnNames.subList(0,
							Math.min(columnNames.size(), maxLen)));
		}
		builder.append("]");
		return builder.toString();
	}

}
