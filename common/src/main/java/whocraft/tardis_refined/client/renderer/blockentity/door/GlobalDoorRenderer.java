package whocraft.tardis_refined.client.renderer.blockentity.door;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import whocraft.tardis_refined.client.ModelRegistry;
import whocraft.tardis_refined.client.model.blockentity.door.*;
import whocraft.tardis_refined.client.model.blockentity.shell.ShellModel;
import whocraft.tardis_refined.common.block.door.GlobalDoorBlock;
import whocraft.tardis_refined.common.blockentity.door.GlobalDoorBlockEntity;
import whocraft.tardis_refined.common.tardis.themes.ShellTheme;

public class GlobalDoorRenderer implements BlockEntityRenderer<GlobalDoorBlockEntity>, BlockEntityRendererProvider<GlobalDoorBlockEntity> {

    private static ShellModel currentModel, factoryDoorModel, policeBoxModel, phoneBoothDoorModel, mysticDoor, drifterModel, presentModel, vendingModel, briefcaseModel, groeningModel, bigBenModel;

    public GlobalDoorRenderer(BlockEntityRendererProvider.Context context) {
        factoryDoorModel = new FactoryDoorModel(context.bakeLayer((ModelRegistry.FACTORY_DOOR)));
        policeBoxModel = new PoliceBoxDoorModel(context.bakeLayer((ModelRegistry.POLICE_BOX_DOOR)));
        phoneBoothDoorModel = new PhoneBoothDoorModel(context.bakeLayer((ModelRegistry.PHONE_BOOTH_DOOR)));
        mysticDoor = new MysticDoorModel(context.bakeLayer((ModelRegistry.MYSTIC_DOOR)));
        drifterModel = new DrifterDoorModel(context.bakeLayer((ModelRegistry.DRIFTER_DOOR)));
        presentModel = new PresentDoorModel(context.bakeLayer((ModelRegistry.PRESENT_DOOR)));
        vendingModel = new VendingMachineDoorModel(context.bakeLayer((ModelRegistry.VENDING_DOOR)));
        briefcaseModel = new BriefcaseDoorModel(context.bakeLayer((ModelRegistry.BRIEFCASE_DOOR)));
        groeningModel = new GroeningDoorModel(context.bakeLayer((ModelRegistry.GROENING_DOOR)));
        bigBenModel = new BigBenDoorModel(context.bakeLayer((ModelRegistry.BIG_BEN_DOOR)));
    }

    @Override
    public void render(GlobalDoorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
        BlockState blockstate = blockEntity.getBlockState();
        float rotation = blockstate.getValue(GlobalDoorBlock.FACING).toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        ShellTheme theme = blockstate.getValue(GlobalDoorBlock.SHELL);
        boolean isOpen = blockstate.getValue(GlobalDoorBlock.OPEN);

        // Render slightly off the wall to prevent z-fighting.
        poseStack.translate(0, 0, -0.01);

        switch (theme) {
            default:
            case FACTORY:
                currentModel = factoryDoorModel;
                break;
            case POLICE_BOX:
                currentModel = policeBoxModel;
                poseStack.scale(1.05f, 1.05f, 1.05f);
                poseStack.translate(0, -0.07, 0);
                break;
            case PHONE_BOOTH:
                currentModel = phoneBoothDoorModel;
                break;
            case MYSTIC:
                currentModel = mysticDoor;
                break;
            case DRIFTER:
                currentModel = drifterModel;
                break;
            case PRESENT:
                currentModel = presentModel;
                break;
            case VENDING:
                currentModel = vendingModel;
                break;
            case BRIEFCASE:
                currentModel = briefcaseModel;
                break;
             case GROENING:
                currentModel = groeningModel;
                break;
             case BIG_BEN:
                currentModel = bigBenModel;
                break;
        }

        currentModel.setDoorPosition(isOpen);
        currentModel.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(currentModel.texture())), packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(GlobalDoorBlockEntity blockEntity) {
        return true;
    }

    @Override
    public BlockEntityRenderer<GlobalDoorBlockEntity> create(Context context) {
        return new GlobalDoorRenderer(context);
    }
}
