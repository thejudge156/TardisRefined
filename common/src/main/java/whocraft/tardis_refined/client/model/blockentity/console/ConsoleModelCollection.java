package whocraft.tardis_refined.client.model.blockentity.console;

import net.minecraft.client.Minecraft;
import whocraft.tardis_refined.client.ModelRegistry;
import whocraft.tardis_refined.common.tardis.themes.ConsoleTheme;

/***
 * A collection of models for rendering the console unit.
 */
public class ConsoleModelCollection {

    ConsoleUnit toyotaConsoleModel, coralConsoleModel, copperConsoleModel, nukaConsoleModel, factoryConsoleModel, crystalConsoleModel, victorianConsoleModel, mystConsoleModel, initiativeConsoleModel;

    public ConsoleModelCollection() {
        factoryConsoleModel = new FactoryConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.FACTORY_CONSOLE)));
        nukaConsoleModel = new NukaConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.NUKA_CONSOLE)));
        copperConsoleModel = new CopperConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.COPPER_CONSOLE)));
        coralConsoleModel = new CoralConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.CORAL_CONSOLE)));
        toyotaConsoleModel = new ToyotaConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.TOYOTA_CONSOLE)));
        crystalConsoleModel = new CrystalConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.CRYSTAL_CONSOLE)));
        victorianConsoleModel = new VictorianConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.VICTORIAN_CONSOLE)));
        mystConsoleModel = new MystConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.MYST_CONSOLE)));
        initiativeConsoleModel = new InitiativeConsoleModel(Minecraft.getInstance().getEntityModels().bakeLayer((ModelRegistry.INITIATIVE_CONSOLE)));
    }

    /**
     * Get the associated console model from a console theme.
     *
     * @param theme The Console theme.
     * @return Console unit model tied with the console theme.
     **/
    public ConsoleUnit getConsoleModel(ConsoleTheme theme) {
        switch (theme) {
            case FACTORY:
                return factoryConsoleModel;
            case COPPER:
                return copperConsoleModel;
            case CORAL:
                return coralConsoleModel;
            case TOYOTA:
                return toyotaConsoleModel;
            case NUKA:
                return nukaConsoleModel;
            case CRYSTAL:
                return crystalConsoleModel;
            case VICTORIAN:
                return victorianConsoleModel;
            case MYST:
                return mystConsoleModel;
            case INITIATIVE:
                return initiativeConsoleModel;
            default:
                throw new RuntimeException("Renderer has accessed theme that isn't accounted for! - " + theme.getSerializedName());
        }
    }

    private static ConsoleModelCollection instance = null;

    public static ConsoleModelCollection getInstance() {
        if (ConsoleModelCollection.instance == null) {
            ConsoleModelCollection.instance = new ConsoleModelCollection();
        }

        return instance;
    }
}
