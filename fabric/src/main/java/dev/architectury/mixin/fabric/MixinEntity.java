/*
 * This file is part of architectury.
 * Copyright (C) 2020, 2021, 2022 architectury
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package dev.architectury.mixin.fabric;

import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.hooks.level.entity.fabric.EntityHooksImpl;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(method = "hurtClient", at = @At("HEAD"), cancellable = true)
    private void hurtClient(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof LivingEntity)) return;
        if ((Object) this instanceof Player) return;
        if (EntityEvent.LIVING_HURT.invoker().hurt((LivingEntity) (Object) this, damageSource, 0).isFalse()) {
            cir.setReturnValue(false);
        }
    }
    
    @ModifyVariable(method = "setLevelCallback", argsOnly = true, ordinal = 0, at = @At("HEAD"))
    public EntityInLevelCallback modifyLevelCallback_setLevelCallback(EntityInLevelCallback callback) {
        return EntityHooksImpl.wrapEntityInLevelCallback((Entity) (Object) this, callback);
    }
}
