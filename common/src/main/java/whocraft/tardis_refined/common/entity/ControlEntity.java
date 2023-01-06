package whocraft.tardis_refined.common.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import whocraft.tardis_refined.common.blockentity.console.GlobalConsoleBlockEntity;
import whocraft.tardis_refined.common.capability.TardisLevelOperator;
import whocraft.tardis_refined.common.tardis.control.ControlSpecification;
import whocraft.tardis_refined.common.tardis.control.ship.MonitorControl;
import whocraft.tardis_refined.common.util.MiscHelper;
import whocraft.tardis_refined.registry.EntityRegistry;

public class ControlEntity extends PathfinderMob {

    private ControlSpecification controlSpecification;
    private BlockPos consoleBlockPos;

    public ControlEntity(Level level) {
        super(EntityRegistry.CONTROL_ENTITY.get(), level);
    }

    @Override
    public Component getName() {
        if(controlSpecification == null){
            return super.getName();
        }
        return Component.translatable(controlSpecification.control().getTranslationKey());
    }

    public ControlSpecification controlSpecification() {
        return controlSpecification;
    }

    public void assignControlData(ControlSpecification consoleControl, BlockPos entityPosition) {
        this.consoleBlockPos = entityPosition;
        this.controlSpecification = consoleControl;
        this.setBoundingBox(new AABB(new BlockPos(consoleControl.scale())));
        this.refreshDimensions();
        this.setCustomName(Component.translatable(consoleControl.control().getTranslationKey()));
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().
                add(Attributes.FOLLOW_RANGE, 35D).
                add(Attributes.MOVEMENT_SPEED, 0.23F).
                add(Attributes.ATTACK_DAMAGE, 3F).
                add(Attributes.MAX_HEALTH, 20000000000D).
                add(Attributes.ARMOR, 2000000000.0D);
    }

    @Override
    public boolean save(CompoundTag compound) {
        compound.put("CONSOLE_POS",NbtUtils.writeBlockPos(this.consoleBlockPos));
        return super.save(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        var consolePos = (CompoundTag) compound.get("CONSOLE_POS");
        if (consolePos != null) {
            this.consoleBlockPos = NbtUtils.readBlockPos(consolePos);
        }

    }


    @Override
    protected AABB makeBoundingBox() {
        if (controlSpecification != null) {
            return new AABB(new BlockPos(controlSpecification.scale()));
        }
        return super.makeBoundingBox();
    }

    @Override
    public AttributeMap getAttributes() {
        return new AttributeMap(createAttributes().build());
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return MiscHelper.spawnPacket(this);
    }

    @Override
    public boolean mayInteract(Level level, BlockPos blockPos) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.getDirectEntity() instanceof Player player) {
            if (getLevel() instanceof ServerLevel serverLevel) {

                if (player.getMainHandItem().getItem() == Items.DEBUG_STICK) {
                    setPos(position().add(player.isCrouching() ? -0.1 : 0.1, 0, 0));
                    return false;
                }

                TardisLevelOperator.get(serverLevel).ifPresent(cap -> {

                    if (!(this.controlSpecification.control().getControl() instanceof MonitorControl)) {
                        if (cap.getInteriorManager().isWaitingToGenerate()) {
                            serverLevel.playSound(null, this.blockPosition(), SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.BLOCKS, 100, (float)(0.1 + (serverLevel.getRandom().nextFloat() * 0.5)) );
                            return;
                        }
                    }
                    this.controlSpecification.control().getControl().onLeftClick(cap, this, player);
                });

                return true;
            }
        }
        return super.hurt(damageSource, f);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
        if (interactionHand == InteractionHand.MAIN_HAND) {
            if (getLevel() instanceof ServerLevel serverLevel) {

                if (player.getMainHandItem().getItem() == Items.DEBUG_STICK) {
                    setPos(position().add(0, 0, player.isCrouching() ? -0.1 : 0.1));
                    return InteractionResult.SUCCESS;
                }

                TardisLevelOperator.get(serverLevel).ifPresent(cap -> {

                    if (!(this.controlSpecification.control().getControl() instanceof MonitorControl)) {
                        if (cap.getInteriorManager().isWaitingToGenerate()) {
                            serverLevel.playSound(null, this.blockPosition(), SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.BLOCKS, 100, (float)(0.1 + (serverLevel.getRandom().nextFloat() * 0.5)) );
                            return;
                        }
                    }

                    this.controlSpecification.control().getControl().onRightClick(cap, this, player);

                });
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
    }

    @Override
    public void tick() {
        setNoAi(true);

        if (this.controlSpecification == null) {
            if (this.consoleBlockPos != null) {
                if (level.getBlockEntity(this.consoleBlockPos) instanceof GlobalConsoleBlockEntity globalConsoleBlockEntity) {
                    kill();

                    globalConsoleBlockEntity.markDirty();

                }
            } else {
                kill();
            }
        }

        super.tick();
    }

}
