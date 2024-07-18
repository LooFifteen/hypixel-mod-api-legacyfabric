package net.hypixel.modapi.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.fabric.event.HypixelModAPICallback;
import net.hypixel.modapi.fabric.payload.ClientboundHypixelPayload;
import net.hypixel.modapi.fabric.payload.ServerboundHypixelPayload;
import net.legacyfabric.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.legacyfabric.fabric.api.logger.v1.Logger;
import net.legacyfabric.fabric.api.networking.v1.PacketByteBufs;
import net.legacyfabric.fabric.api.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PacketByteBuf;

public class FabricModAPI implements ClientModInitializer {
    private static final Logger LOGGER = Logger.get("HypixelModAPI");

    @Override
    public void onInitializeClient() {
        reloadRegistrations();
        registerPacketSender();
    }

    /**
     * Reloads the identifiers that are registered in the Hypixel Mod API and makes sure that the packets are registered.
     * <p>
     * This method is available for internal use by Hypixel to add new packets externally, and is not intended for use by other developers.
     */
    public static void reloadRegistrations() {
        for (String identifier : HypixelModAPI.getInstance().getRegistry().getClientboundIdentifiers()) {
            try {
                registerClientbound(identifier);
                LOGGER.info("Registered clientbound packet with identifier '%s'", identifier);
            } catch (Exception e) {
                LOGGER.error("Failed to register clientbound packet with identifier '%s'", identifier, e);
            }
        }
    }

    private static void registerPacketSender() {
        HypixelModAPI.getInstance().setPacketSender((packet) -> {
            ServerboundHypixelPayload hypixelPayload = new ServerboundHypixelPayload(packet);

            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                PacketByteBuf packetByteBuf = PacketByteBufs.create();
                hypixelPayload.write(packetByteBuf);
                ClientPlayNetworking.send(
                        hypixelPayload.getId(),
                        packetByteBuf
                );
                return true;
            }

            LOGGER.warn("Failed to send a packet as the client is not connected to a server '%s'", packet);
            return false;
        });
    }

    private static void registerClientbound(String identifier) {
        try {
            Identifier clientboundId = new Identifier(identifier);

            ClientPlayNetworking.registerGlobalReceiver(clientboundId, (minecraft, networkHandler, payload, sender) -> {
                LOGGER.debug("Received packet with identifier '%s', during PLAY", identifier);
                handleIncomingPayload(identifier, new ClientboundHypixelPayload(clientboundId, payload));
            });
        } catch (IllegalArgumentException ignored) {
            // Ignored as this is fired when we reload the registrations and the packet is already registered
        }
    }

    private static void handleIncomingPayload(String identifier, ClientboundHypixelPayload payload) {
        if (!payload.isSuccess()) {
            LOGGER.warn("Received an error response for packet %s: %s", identifier, payload.getErrorReason());
            return;
        }

        try {
            HypixelModAPI.getInstance().handle(payload.getPacket());
        } catch (Exception e) {
            LOGGER.error("An error occurred while handling packet %s", identifier, e);
        }

        try {
            HypixelModAPICallback.EVENT.invoker().onPacketReceived(payload.getPacket());
        } catch (Exception e) {
            LOGGER.error("An error occurred while handling packet %s", identifier, e);
        }
    }

}
