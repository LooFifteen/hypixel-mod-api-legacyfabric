package net.hypixel.modapi.fabric.payload;

import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.error.ErrorReason;
import net.hypixel.modapi.packet.ClientboundHypixelPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.legacyfabric.fabric.api.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class ClientboundHypixelPayload {

    private ClientboundHypixelPacket packet;
    private ErrorReason errorReason;

    public ClientboundHypixelPayload(Identifier id, PacketByteBuf buf) {
        PacketSerializer serializer = new PacketSerializer(buf);
        boolean success = serializer.readBoolean();
        if (!success) {
            this.errorReason = ErrorReason.getById(serializer.readVarInt());
            return;
        }

        this.packet = HypixelModAPI.getInstance().getRegistry().createClientboundPacket(id.toString(), serializer);
    }

    public boolean isSuccess() {
        return packet != null;
    }

    public ClientboundHypixelPacket getPacket() {
        return packet;
    }

    public ErrorReason getErrorReason() {
        return errorReason;
    }

}
