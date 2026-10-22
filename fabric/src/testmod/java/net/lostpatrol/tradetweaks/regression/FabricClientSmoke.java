/** Installs client-only startup smoke for explicitly selected test launches. */
package net.lostpatrol.tradetweaks.regression;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.lostpatrol.tradetweaks.regression.client.ClientSmoke;

public final class FabricClientSmoke implements ClientModInitializer {
    /** Avoids loading client smoke support on dedicated GameTest servers. */
    @Override
    public void onInitializeClient() {
        if (Boolean.getBoolean("tradetweaks.clientSmoke")) {
            ClientTickEvents.END_CLIENT_TICK.register(ClientSmoke::tick);
        }
    }
}
