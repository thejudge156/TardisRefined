package whocraft.tardis_refined.common.network.messages;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import whocraft.tardis_refined.client.screen.CancelDesktopScreen;
import whocraft.tardis_refined.client.screen.MonitorScreen;
import whocraft.tardis_refined.common.network.MessageContext;
import whocraft.tardis_refined.common.network.MessageS2C;
import whocraft.tardis_refined.common.network.MessageType;
import whocraft.tardis_refined.common.network.TardisNetwork;
import whocraft.tardis_refined.common.tardis.TardisNavLocation;


public class OpenMonitorMessage extends MessageS2C {

    private final boolean desktopGenerating;
    private TardisNavLocation currentLocation, targetLocation;

    public OpenMonitorMessage(boolean desktopGenerating, TardisNavLocation currentLocation, TardisNavLocation targetLocation) {
        this.desktopGenerating = desktopGenerating;
        this.currentLocation = currentLocation;
        this.targetLocation = targetLocation;
    }

    public OpenMonitorMessage(FriendlyByteBuf friendlyByteBuf) {
        this.desktopGenerating = friendlyByteBuf.readBoolean();
        this.currentLocation = TardisNavLocation.deserialise(friendlyByteBuf.readNbt());
        this.targetLocation = TardisNavLocation.deserialise(friendlyByteBuf.readNbt());
    }

    @NotNull
    @Override
    public MessageType getType() {
        return TardisNetwork.OPEN_MONITOR;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.desktopGenerating);
        buf.writeNbt(currentLocation.serialise());
        buf.writeNbt(targetLocation.serialise());
    }


    @Override
    public void handle(MessageContext context) {
        handleScreens();
    }

    @Environment(EnvType.CLIENT)
    private void handleScreens() {
        // Open the monitor.
        if (this.desktopGenerating) {
            Minecraft.getInstance().setScreen(new CancelDesktopScreen());
        } else {
            Minecraft.getInstance().setScreen(new MonitorScreen(currentLocation, targetLocation));
        }
    }
}
