package de.abas.app.g30l0;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.Deletable;
import de.abas.erp.db.Query;
import de.abas.erp.db.SelectableObject;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.db.schema.regions.RegionCountryEconomicArea;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.schema.vendor.VendorEditor;
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
import java.math.BigDecimal;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.junit.Assert.assertThat;

public class ClientSideInfosystemTest {

	private static final String CUSTOMER_SWD = "FISCHERFORESDK";
	private static final String VENDOR_SWD = "HELLSTROEMFORESDK";
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

	@Test
	public void canDisplayVendorInfo() {
		geoLocation.setCustomersel(VENDOR_SWD);
		geoLocation.invokeStart();
		assertThat(geoLocation.table().getRowCount(), is(1));
		GeoLocation.Row row = geoLocation.table().getRow(1);
		assertThat(row.getZipcode(), is("111 52"));
		assertThat(row.getTown(), is("Stockholm"));
		assertThat(row.getState().getSwd(), is("SCHWEDEN"));
	}

	@Test
	public void canDisplayGeoLocation() {
		geoLocation.setCustomersel(VENDOR_SWD);
		geoLocation.invokeStart();
		assertThat(geoLocation.table().getRowCount(), is(1));
		GeoLocation.Row row = geoLocation.table().getRow(1);
		assertThat(row.getLongitude(), closeTo(BigDecimal.valueOf(18.0644393), BigDecimal.valueOf(0.000001)));
		assertThat(row.getLatitude(), closeTo(BigDecimal.valueOf(59.3286884), BigDecimal.valueOf(0.000001)));
	}

	@Test
	public void canSelectBasedOnZipCode() {
		geoLocation.setZipcodesel("39638");
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
		createTestCustomer();
		createTestVendor();
	}

	private static void createTestCustomer() {
		CustomerEditor customerEditor = ctx.newObject(CustomerEditor.class);
		customerEditor.setSwd(CUSTOMER_SWD);
		customerEditor.setAddr("Simone Fischer");
		customerEditor.setStreet("Sandstraße 55");
		customerEditor.setZipCode("39638");
		customerEditor.setTown("Gardelegen");
		customerEditor.setDescr(customerEditor.getAddr() + ", " + customerEditor.getTown());
		customerEditor.commit();
	}

	private static void createTestVendor() {
		VendorEditor vendorEditor = ctx.newObject(VendorEditor.class);
		vendorEditor.setSwd(VENDOR_SWD);
		vendorEditor.setAddr("Isolde Hellström");
		vendorEditor.setStreet("Fredsgatan 12");
		vendorEditor.setZipCode("111 52");
		vendorEditor.setTown("Stockholm");
		RegionCountryEconomicArea sweden = ctx.createQuery(SelectionBuilder.create(RegionCountryEconomicArea.class).add(Conditions.eq(RegionCountryEconomicArea.META.swd, "SCHWEDEN")).build()).iterator().next();
		vendorEditor.setStateOfTaxOffice(sweden);
		vendorEditor.setDescr(vendorEditor.getAddr() + ", " + vendorEditor.getTown());
		vendorEditor.commit();
	}

	private static void deleteTestData() {
		deleteTestData(Customer.class, CUSTOMER_SWD);
		deleteTestData(Vendor.class, VENDOR_SWD);
	}

	private static <T extends SelectableObject & Deletable> void deleteTestData(Class<T> clazz, String swd) {
		Query<T> query = ctx.createQuery(SelectionBuilder.create(clazz)
				.add(Conditions.starts("swd", swd))
				.build());
		for (T object : query) {
			object.delete();
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
