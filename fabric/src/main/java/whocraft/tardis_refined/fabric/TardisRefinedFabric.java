package whocraft.tardis_refined.fabric;

import net.fabricmc.api.ModInitializer;
import whocraft.tardis_refined.TardisRefined;
import whocraft.tardis_refined.common.util.fabric.PlatformImpl;
import whocraft.tardis_refined.fabric.events.LevelEvents;

public class TardisRefinedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PlatformImpl.init();
        LevelEvents.addEvents();
        TardisRefined.init();
    }
}