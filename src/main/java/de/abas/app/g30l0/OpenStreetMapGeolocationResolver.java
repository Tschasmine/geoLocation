package de.abas.app.g30l0;

import de.abas.erp.db.schema.referencetypes.TradingPartner;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class OpenStreetMapGeolocationResolver {

	private static final String BASE_URL = "https://nominatim.openstreetmap.org/search?";
	private static final String FORMAT = "&format=json&addressdetails=0&limit=1";
	private static final String LATITUDE = "lat";
	private static final String LONGITUDE = "lon";

	private static final Logger logger = Logger.getLogger(OpenStreetMapGeolocationResolver.class);

	private BigDecimal longitude = new BigDecimal(0);
	private BigDecimal latitude = new BigDecimal(0);

	OpenStreetMapGeolocationResolver() {
		// explicit default constructor
	}

	void resolve(TradingPartner tradingPartner) {
		try {
			JSONObject location = requestLocation(tradingPartner);
			setLatitude(location.getString(LATITUDE));
			setLongitude(location.getString(LONGITUDE));
		} catch (Exception e) {
			logger.error("Invalid address: " + e.getMessage(), e);
		}
	}

	BigDecimal getLongitude() {
		return this.longitude;
	}

	private void setLongitude(String longitude) {
		this.longitude = new BigDecimal(longitude);
	}

	BigDecimal getLatitude() {
		return this.latitude;
	}

	private void setLatitude(String latitude) {
		this.latitude = new BigDecimal(latitude);
	}

	protected JSONObject requestLocation(TradingPartner tradingPartner) throws IOException, JSONException {
		String parameters = "q=" + URLEncoder.encode(tradingPartner.getStreet() + ", " + cutWhitespaceAway(tradingPartner.getZipCode()) + ", " + tradingPartner.getTown() + ", " + tradingPartner.getStateOfTaxOffice().getSwd(), "UTF-8");
		String request = BASE_URL + parameters + FORMAT;
		logger.debug("Request: " + request);
		try (Scanner scanner = new Scanner(new URL(request).openStream(), StandardCharsets.UTF_8.name())) {
			String content = scanner.useDelimiter("\\A").next();
			JSONArray array = new JSONArray(content);
			return array.getJSONObject(0);
		}
	}

	private String cutWhitespaceAway(String zipCode) {
		return zipCode.replaceAll("\\s", "");
	}
}
