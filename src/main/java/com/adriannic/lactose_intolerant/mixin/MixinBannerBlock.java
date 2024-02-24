package com.adriannic.lactose_intolerant.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BannerBlock.class)
public abstract class MixinBannerBlock extends AbstractBannerBlock implements Waterloggable {

    protected MixinBannerBlock(DyeColor color, Settings settings) {
        super(color, settings);
        this.setDefaultState(getStateManager().getDefaultState().with(Properties.WATERLOGGED, false));
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    public void appendWaterlogged(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(Properties.WATERLOGGED);
    }

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BannerBlock;getDefaultState()Lnet/minecraft/block/BlockState;"))
    public BlockState getDefaultStateWithWaterlogged(BannerBlock instance, @Local ItemPlacementContext ctx) {
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
