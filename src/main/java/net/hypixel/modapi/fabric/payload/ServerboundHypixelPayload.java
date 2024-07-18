package net.hypixel.modapi.fabric.payload;

import net.hypixel.modapi.packet.HypixelPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.legacyfabric.fabric.api.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class ServerboundHypixelPayload {

    private final Identifier id;
    private final HypixelPacket packet;

    public ServerboundHypixelPayload(HypixelPacket packet) {
        this.id = new Identifier(packet.getIdentifier());
        this.packet = packet;
    }

    public void write(PacketByteBuf buf) {
        PacketSerializer serializer = new PacketSerializer(buf);
        packet.write(serializer);
    }

    public Identifier getId() {
        return id;
    }

}
