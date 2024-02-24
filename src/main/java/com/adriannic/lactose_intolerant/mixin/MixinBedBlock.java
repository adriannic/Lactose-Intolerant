package com.adriannic.lactose_intolerant.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public abstract class MixinBedBlock extends HorizontalFacingBlock implements Waterloggable {

    public MixinBedBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(getStateManager().getDefaultState().with(Properties.WATERLOGGED, false));
    }

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;Lnet/minecraft/util/math/Vec3d;FZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"))
    private Explosion noExplosion(World instance, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, Vec3d pos, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, @Local PlayerEntity player) {
        player.sendMessage(Text.translatable("block.minecraft.bed.obstructed"), true);
        return null;
    }

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private boolean removeBlock(World instance, BlockPos pos, boolean move) {
        return false;
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    public void appendWaterlogged(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(Properties.WATERLOGGED);
    }

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BedBlock;getDefaultState()Lnet/minecraft/block/BlockState;"))
    public BlockState getDefaultStateWithWaterlogged(BedBlock instance, @Local ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        return getDefaultState().with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
    public void fluidTick(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
    }
}
