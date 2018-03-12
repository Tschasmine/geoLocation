package de.abas.app.g30l0;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.EditorAction;
import de.abas.erp.db.exception.CommandException;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.db.util.ContextHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClientSideInfosystemTest {

	private static final String CUSTOMER_SWD = "FISCHERFORESDK";
	private static DbContext ctx;
	private GeoLocation geoLocation = ctx.openInfosystem(GeoLocation.class);

	@BeforeClass
	public static void setup() {
		ctx = getClientContext();
		createTestData();
	}

	@AfterClass
	public static void cleanup() {
		deleteTestData();
		ctx.close();
	}

	@Test
	public void canDisplayCustomerInfo() {
		geoLocation.setCustomersel(CUSTOMER_SWD);
		geoLocation.invokeStart();
		assertThat(geoLocation.table().getRowCount(), is(1));
		GeoLocation.Row row = geoLocation.table().getRow(1);
		assertThat(row.getZipcode(), is("39638"));
		assertThat(row.getTown(), is("Gardelegen"));
		assertThat(row.getState().getSwd(), is("DEUTSCHLAND"));
	}

	@After
	public void tidyUp() {
		geoLocation.abort();
	}

	private static void createTestData() {
		CustomerEditor customerEditor = ctx.newObject(CustomerEditor.class);
		customerEditor.setSwd(CUSTOMER_SWD);
		customerEditor.setAddr("Simone Fischer");
		customerEditor.setStreet("Sandstraße 55");
		customerEditor.setZipCode("39638");
		customerEditor.setTown("Gardelegen");
		customerEditor.setDescr(customerEditor.getAddr() + ", " + customerEditor.getTown());
		customerEditor.commit();
	}

	private static void deleteTestData() {
		List<Customer> customers = ctx.createQuery(SelectionBuilder.create(Customer.class).add(Conditions.eq(Customer.META.swd, CUSTOMER_SWD)).build()).execute();
		for (Customer customer : customers) {
			CustomerEditor customerEditor = customer.createEditor();
			try {
				customerEditor.open(EditorAction.DELETE);
				customerEditor.commit();
			} catch (CommandException e) {
				if (customerEditor.active()) {
					customerEditor.abort();
				}
				System.err.println("Error while deleting customer " + customer.getIdno() + ": " + e.getMessage());
			}

		}
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
