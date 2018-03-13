package de.abas.app.g30l0;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.schema.vendor.VendorContact;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.Selection;
import de.abas.erp.db.selection.SelectionBuilder;

import java.util.ArrayList;
import java.util.List;

public class TradingPartnerSelection {

	private final DbContext ctx;

	TradingPartnerSelection(DbContext ctx) {
		this.ctx = ctx;
	}

	List<TradingPartner> selectTradingPartners(String swd, String zipCode) {
		List<TradingPartner> tradingPartners = new ArrayList<>();
		tradingPartners.addAll(ctx.createQuery(buildSelection(Customer.class, swd, zipCode)).execute());
		tradingPartners.addAll(ctx.createQuery(buildSelection(CustomerContact.class, swd, zipCode)).execute());
		tradingPartners.addAll(ctx.createQuery(buildSelection(Vendor.class, swd, zipCode)).execute());
		tradingPartners.addAll(ctx.createQuery(buildSelection(VendorContact.class, swd, zipCode)).execute());
		return tradingPartners;
	}

	private <T extends TradingPartner> Selection<T> buildSelection(Class<T> clazz, String swd, String zipCode) {
		SelectionBuilder<T> selectionBuilder = SelectionBuilder.create(clazz);
		if (!swd.isEmpty()) {
			selectionBuilder.add(Conditions.eq(TradingPartner.META.swd, swd));
		}
		if (!zipCode.isEmpty()) {
			selectionBuilder.add(Conditions.eq(TradingPartner.META.zipCode, zipCode));
		}
		return selectionBuilder.build();
	}

}
