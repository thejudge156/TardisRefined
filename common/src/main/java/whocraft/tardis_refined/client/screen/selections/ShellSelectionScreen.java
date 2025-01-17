package whocraft.tardis_refined.client.screen.selections;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import whocraft.tardis_refined.TardisRefined;
import whocraft.tardis_refined.client.TardisClientData;
import whocraft.tardis_refined.client.model.blockentity.shell.ShellModel;
import whocraft.tardis_refined.client.model.blockentity.shell.ShellModelCollection;
import whocraft.tardis_refined.client.screen.components.GenericMonitorSelectionList;
import whocraft.tardis_refined.client.screen.components.SelectionListEntry;
import whocraft.tardis_refined.common.network.messages.ChangeShellMessage;
import whocraft.tardis_refined.common.tardis.themes.ShellTheme;
import whocraft.tardis_refined.constants.ModMessages;
import whocraft.tardis_refined.patterns.ShellPattern;
import whocraft.tardis_refined.patterns.ShellPatternCollection;
import whocraft.tardis_refined.patterns.ShellPatterns;

import java.util.List;

public class ShellSelectionScreen extends SelectionScreen {

    private final List<ShellTheme> themeList;
    private ShellTheme currentShellTheme;

    protected int imageWidth = 256;
    protected int imageHeight = 173;
    private int leftPos, topPos;


    public static ResourceLocation MONITOR_TEXTURE = new ResourceLocation(TardisRefined.MODID, "textures/ui/shell.png");
    public static ResourceLocation NOISE = new ResourceLocation(TardisRefined.MODID, "textures/ui/noise.png");
    private ShellPattern pattern;

    private ShellPatternCollection patternCollection;
    private Button patternButton;

    public ShellSelectionScreen() {
        super(Component.translatable(ModMessages.UI_SHELL_SELECTION));
        this.themeList = List.of(ShellTheme.values());
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void init() {
        this.setEvents(() -> {
            selectShell(currentShellTheme);
        }, () -> {
            Minecraft.getInstance().setScreen(null);
        });
        this.currentShellTheme = this.themeList.get(0);
        this.patternCollection = ShellPatterns.getPatternCollectionForTheme(this.currentShellTheme);
        this.pattern = this.patternCollection.patterns().get(0);

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        addSubmitButton(width / 2 + 90, (height) / 2 + 35);
        addCancelButton(width / 2 - 11, (height) / 2 + 35);

        patternButton = addRenderableWidget(new Button(width / 2 + 14, (height) / 2 + 34, 70, 20, Component.literal(""), button -> {
            pattern = ShellPatterns.next(this.patternCollection, this.pattern);
            button.setMessage(Component.Serializer.fromJson(new StringReader(this.pattern.name())));
        }));

        patternButton.visible = false; //Hide when initialised. We will only show it when there are more than 1 pattern for a shell (via its {@link PatternCollection} )

        super.init();
    }

    public void selectShell(ShellTheme theme) {
        new ChangeShellMessage(Minecraft.getInstance().player.getLevel().dimension(), theme, pattern).send();
        Minecraft.getInstance().setScreen(null);
    }


    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {

        this.renderBackground(poseStack);

        ClientLevel lvl = Minecraft.getInstance().level;
        RandomSource rand = lvl.random;

        boolean isCrashed = TardisClientData.getInstance(lvl.dimension()).isCrashing();

        if (isCrashed) {
            if (rand.nextInt(10) == 1) {
                for (int i1 = 0; i1 < 3; i1++) {
                    poseStack.translate(rand.nextInt(3) / 100F, rand.nextInt(3) / 100.0f, rand.nextInt(3) / 100.0f);
                }
            }
            if (rand.nextInt(20) == 1) {
                poseStack.scale(1, 1 + rand.nextInt(5) / 100F, 1);
            }
        }


        /*Render Back drop*/
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MONITOR_TEXTURE);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        /*Model*/
        renderShell(poseStack, width / 2- 75, height / 2 - 20, 25F);


        double alpha = (100.0D - this.age * 3.0D) / 100.0D;
        if (isCrashed) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) alpha);
            RenderSystem.setShaderTexture(0, NOISE);
            blit(poseStack, leftPos, topPos, this.noiseX, this.noiseY, imageWidth, imageHeight);
            RenderSystem.disableBlend();
        }


        super.render(poseStack, i, j, f);
    }

    private void renderShell(PoseStack poseStack, int x, int y, float scale) {
        ShellModel model = ShellModelCollection.getInstance().getShellModel(currentShellTheme);
        model.setDoorOpen(false);
        Lighting.setupForEntityInInventory();
        PoseStack pose = poseStack;
        pose.pushPose();
        pose.translate((float) x, y, 100.0F);
        pose.scale(-scale, scale, scale);
        pose.mulPose(Vector3f.XP.rotationDegrees(-15F));
        pose.mulPose(Vector3f.YP.rotationDegrees(System.currentTimeMillis() % 5400L / 15L));
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        VertexConsumer vertexConsumer = bufferSource.getBuffer(model.renderType(model.texture(pattern, false)));
        model.renderToBuffer(pose, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        bufferSource.endBatch();
        pose.popPose();
        Lighting.setupFor3DItems();
    }


    @Override
    public Component getSelectedDisplayName() {
        return currentShellTheme.getDisplayName();
    }

    @Override
    public GenericMonitorSelectionList createSelectionList() {
        int leftPos = width / 2 - 5;
        GenericMonitorSelectionList<SelectionListEntry> selectionList = new GenericMonitorSelectionList<>(this.minecraft, 100, 80, leftPos, this.topPos + 30, this.topPos + this.imageHeight - 60, 12);

        selectionList.setRenderBackground(false);

        for (ShellTheme shellTheme : ShellTheme.values()) {
            selectionList.children().add(new SelectionListEntry(shellTheme.getDisplayName(), (entry) -> {
                this.currentShellTheme = shellTheme;

                for (Object child : selectionList.children()) {
                    if (child instanceof SelectionListEntry current) {
                        current.setChecked(false);
                    }
                }
                this.patternCollection = ShellPatterns.getPatternCollectionForTheme(this.currentShellTheme);
                this.pattern = this.patternCollection.patterns().get(0);

                boolean themeHasPatterns = this.patternCollection.patterns().size() > 1;

                //Hide the pattern button if there is only one pattern available for the shell, else show it. (i.e. The default)
                patternButton.visible = themeHasPatterns;

                if (themeHasPatterns) //Update the button name now that we have confirmed that there is more than one pattern in the shell
                    this.patternButton.setMessage(Component.Serializer.fromJson(new StringReader(pattern.name())));

                age = 0;
                entry.setChecked(true);
            }, leftPos));
        }

        return selectionList;
    }
}
