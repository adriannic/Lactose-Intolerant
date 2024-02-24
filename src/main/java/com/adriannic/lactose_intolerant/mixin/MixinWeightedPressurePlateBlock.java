package com.adriannic.lactose_intolerant.mixin;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeightedPressurePlateBlock.class)
public abstract class MixinWeightedPressurePlateBlock extends AbstractPressurePlateBlock implements Waterloggable {

    protected MixinWeightedPressurePlateBlock(Settings settings, BlockSetType blockSetType) {
        super(settings, blockSetType);
        this.setDefaultState(getStateManager().getDefaultState().with(Properties.WATERLOGGED, false));
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    public void appendWaterlogged(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(Properties.WATERLOGGED);
    }

}