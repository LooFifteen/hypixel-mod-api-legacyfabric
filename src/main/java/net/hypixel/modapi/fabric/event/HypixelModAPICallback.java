package net.hypixel.modapi.fabric.event;

import net.hypixel.modapi.packet.ClientboundHypixelPacket;
import net.legacyfabric.fabric.api.event.Event;
import net.legacyfabric.fabric.api.event.EventFactory;

/**
 * Callback for when a Hypixel Mod API packet is received.
 */
public interface HypixelModAPICallback {

    Event<HypixelModAPICallback> EVENT = EventFactory.createArrayBacked(HypixelModAPICallback.class, callbacks -> packet -> {
        for (HypixelModAPICallback callback : callbacks) {
            callback.onPacketReceived(packet);
        }
    });

    void onPacketReceived(ClientboundHypixelPacket packet);

}
