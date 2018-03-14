package de.abas.app.g30l0;

import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = CustomerEditor.class)
@RunFopWith(EventHandlerRunner.class)
public class CustomerEventHandler {

	@ButtonEventHandler(field = "yg30l0calculate", type = ButtonEventType.AFTER)
	public void yg30l0calculateAfter(CustomerEditor customerEditor) {
		OpenStreetMapGeolocationResolver geolocationResolver = new OpenStreetMapGeolocationResolver();
		geolocationResolver.resolve(customerEditor);
		customerEditor.setLatitude(geolocationResolver.getLatitude());
		customerEditor.setLongitude(geolocationResolver.getLongitude());

	}

}
