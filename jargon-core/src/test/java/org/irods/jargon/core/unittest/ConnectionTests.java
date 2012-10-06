package org.irods.jargon.core.unittest;

import org.irods.jargon.core.connection.ConnectionProgressStatusTest;
import org.irods.jargon.core.connection.DefaultPropertiesJargonConfigTest;
import org.irods.jargon.core.connection.EnvironmentalInfoAccessorTest;
import org.irods.jargon.core.connection.IRODSServerPropertiesTest;
import org.irods.jargon.core.connection.IRODSSessionTest;
import org.irods.jargon.core.connection.IRODSSimpleConnectionTest;
import org.irods.jargon.core.connection.IRODSSimpleProtocolManagerTest;
import org.irods.jargon.core.connection.PAMAuthTest;
import org.irods.jargon.core.connection.PipelineConfigurationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ IRODSSimpleConnectionTest.class,
		EnvironmentalInfoAccessorTest.class,
		IRODSSimpleProtocolManagerTest.class,
 IRODSSessionTest.class,
		IRODSServerPropertiesTest.class,
		DefaultPropertiesJargonConfigTest.class,
		ConnectionProgressStatusTest.class,
 PipelineConfigurationTest.class,
		PAMAuthTest.class })
public class ConnectionTests {

}
