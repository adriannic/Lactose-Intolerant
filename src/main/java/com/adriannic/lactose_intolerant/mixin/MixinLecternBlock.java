package com.adriannic.lactose_intolerant.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternBlock.class)
public abstract class MixinLecternBlock extends BlockWithEntity implements Waterloggable {

    public MixinLecternBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getStateManager().getDefaultState().with(Properties.WATERLOGGED, false));
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    public void appendWaterlogged(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(Properties.WATERLOGGED);
    }

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LecternBlock;getDefaultState()Lnet/minecraft/block/BlockState;"))
    public BlockState getDefaultStateWithWaterlogged(LecternBlock instance, @Local ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        return getDefaultState().with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}
