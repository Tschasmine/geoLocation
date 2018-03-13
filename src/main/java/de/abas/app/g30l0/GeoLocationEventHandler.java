package de.abas.app.g30l0;

import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.SelectableObject;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.schema.vendor.VendorContact;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.Selection;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

import java.util.ArrayList;
import java.util.List;

@EventHandler(head = GeoLocation.class, row = GeoLocation.Row.class)
@RunFopWith(EventHandlerRunner.class)
public class GeoLocationEventHandler {

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	public void startAfter(DbContext ctx, GeoLocation infosystem) {
		infosystem.table().clear();
		List<TradingPartner> tradingPartners = selectBusinessPartners(ctx, infosystem.getCustomersel(), infosystem.getZipcodesel());
		for (TradingPartner tradingPartner : tradingPartners) {
			GeoLocation.Row row = infosystem.table().appendRow();
			row.setCustomer(tradingPartner);
			row.setZipcode(tradingPartner.getZipCode());
			row.setTown(tradingPartner.getTown());
			row.setState(tradingPartner.getStateOfTaxOffice());
		}
	}

	private List<TradingPartner> selectBusinessPartners(DbContext ctx, String swd, String zipCode) {
		List<TradingPartner> tradingPartners = new ArrayList<>();
		tradingPartners.addAll(ctx.createQuery(buildSelection(Customer.class, swd, zipCode)).execute());
		tradingPartners.addAll(ctx.createQuery(buildSelection(CustomerContact.class, swd, zipCode)).execute());
		tradingPartners.addAll(ctx.createQuery(buildSelection(Vendor.class, swd, zipCode)).execute());
		tradingPartners.addAll(ctx.createQuery(buildSelection(VendorContact.class, swd, zipCode)).execute());
		return tradingPartners;
	}

	private <T extends SelectableObject> Selection<T> buildSelection(Class<T> clazz, String swd, String zipCode) {
		SelectionBuilder<T> selectionBuilder = SelectionBuilder.create(clazz);
		if (!swd.isEmpty()) {
			selectionBuilder.add(Conditions.eq("swd", swd));
		}
		if (!zipCode.isEmpty()) {
			selectionBuilder.add(Conditions.eq("zipCode", zipCode));
		}
		return selectionBuilder.build();
	}

}
