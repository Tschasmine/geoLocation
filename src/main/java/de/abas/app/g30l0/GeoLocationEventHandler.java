package de.abas.app.g30l0;

import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

import java.util.List;

@EventHandler(head = GeoLocation.class, row = GeoLocation.Row.class)
@RunFopWith(EventHandlerRunner.class)
public class GeoLocationEventHandler {

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	public void startAfter(DbContext ctx, GeoLocation infosystem) {
		infosystem.table().clear();
		List<TradingPartner> tradingPartners = new TradingPartnerSelection(ctx).selectTradingPartners(infosystem.getCustomersel(), infosystem.getZipcodesel());
		OpenStreetMapGeolocationResolver geolocationResolver = new OpenStreetMapGeolocationResolver();
		for (TradingPartner tradingPartner : tradingPartners) {
			GeoLocation.Row row = infosystem.table().appendRow();
			row.setCustomer(tradingPartner);
			row.setZipcode(tradingPartner.getZipCode());
			row.setTown(tradingPartner.getTown());
			row.setState(tradingPartner.getStateOfTaxOffice());
			geolocationResolver.resolve(tradingPartner);
			row.setLatitude(geolocationResolver.getLatitude());
			row.setLongitude(geolocationResolver.getLongitude());
		}
	}

}
