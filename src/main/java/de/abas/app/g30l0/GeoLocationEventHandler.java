package de.abas.app.g30l0;

import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.Query;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = GeoLocation.class, row = GeoLocation.Row.class)
@RunFopWith(EventHandlerRunner.class)
public class GeoLocationEventHandler {

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	public void startAfter(DbContext ctx, GeoLocation infosystem) {
		infosystem.table().clear();
		Query<Customer> query = ctx.createQuery(SelectionBuilder.create(Customer.class).add(Conditions.eq(Customer.META.swd, infosystem.getCustomersel())).build());
		for (Customer customer : query) {
			GeoLocation.Row row = infosystem.table().appendRow();
			row.setCustomer(customer);
			row.setZipcode(customer.getZipCode());
			row.setTown(customer.getTown());
			row.setState(customer.getStateOfTaxOffice());
		}
	}

}
