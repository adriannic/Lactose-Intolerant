package com.adriannic.lactose_intolerant.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartEntity.class)
public abstract class MixinAbstractMinecartEntity extends Entity {

    public MixinAbstractMinecartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;hasPassengers()Z"))
    public boolean hasPassengers(AbstractMinecartEntity instance, @Local Entity entity) {
        return !this.getPassengerList().isEmpty() && (entity instanceof HostileEntity);
    }
}
