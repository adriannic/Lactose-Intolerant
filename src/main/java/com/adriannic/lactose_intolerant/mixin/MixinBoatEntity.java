package com.adriannic.lactose_intolerant.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BoatEntity.class)
public abstract class MixinBoatEntity extends Entity {
    public MixinBoatEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected abstract int getMaxPassengers();

    public boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < this.getMaxPassengers() && !this.isSubmergedIn(FluidTags.WATER) && !(passenger instanceof HostileEntity);
    }
}
