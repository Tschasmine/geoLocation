package de.abas.app.g30l0;

import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.regions.RegionCountryEconomicArea;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class OpenStreetMapGeolocationResolverTest {

	private final static JSONObject JSON_OBJECT = new JSONArray("[{\"lat\":\"49.0042323\",\"lon\":\"8.3848872\"}]").getJSONObject(0);
	private final OpenStreetMapGeolocationResolver resolver = spy(OpenStreetMapGeolocationResolver.class);

	@Test
	public void canResolveLocation() throws IOException {
		TradingPartner tradingPartner = mock(TradingPartner.class);
		RegionCountryEconomicArea germany = mock(RegionCountryEconomicArea.class);
		when(germany.getSwd()).thenReturn("DEUTSCHLAND");
		when(tradingPartner.getStreet()).thenReturn("Sandstraße 55");
		when(tradingPartner.getZipCode()).thenReturn("39638");
		when(tradingPartner.getStateOfTaxOffice()).thenReturn(germany);
		doReturn(JSON_OBJECT).when(resolver).requestLocation(tradingPartner);
		resolver.resolve(tradingPartner);
		assertThat(resolver.getLatitude(), closeTo(BigDecimal.valueOf(49.0042323), BigDecimal.valueOf(0.000001)));
		assertThat(resolver.getLongitude(), closeTo(BigDecimal.valueOf(8.3848872), BigDecimal.valueOf(0.000001)));
	}

}
