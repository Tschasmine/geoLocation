package de.abas.app.g30l0;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.util.ContextHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ClientSideInfosystemTest {

	private static DbContext ctx;
	private GeoLocation geoLocation = ctx.openInfosystem(GeoLocation.class);

	@BeforeClass
	public static void setup() {
		ctx = getClientContext();
	}

	@AfterClass
	public static void cleanup() {
		ctx.close();
	}

	@Test
	public void helloTestingWorld() {
		ctx.out().println("Hello, Testing World!");
	}

	private static DbContext getClientContext() {
		final Properties pr = new Properties();
		final File configFile = new File("gradle.properties");
		try {
			pr.load(new FileReader(configFile));
			String hostname = pr.getProperty("EDP_HOST");
			String client = pr.getProperty("EDP_CLIENT");
			int port = Integer.parseInt(pr.getProperty("EDP_PORT", "6550"));
			String password = pr.getProperty("EDP_PASSWORD");
			return ContextHelper.createClientContext(hostname, port, client, password, ClientSideInfosystemTest.class.getSimpleName());
		} catch (final FileNotFoundException e) {
			throw new RuntimeException("Could not find configuration file " + configFile.getAbsolutePath());
		} catch (final IOException e) {
			throw new RuntimeException("Could not load configuration file " + configFile.getAbsolutePath());
		}
	}

}
