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

package dev.architectury.core.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ArchitecturySpawnEggItem extends SpawnEggItem {
    private static final Logger LOGGER = LogManager.getLogger(ArchitecturySpawnEggItem.class);
    
    private final RegistrySupplier<? extends EntityType<? extends Mob>> entityType;
    
    protected static DispenseItemBehavior createDispenseItemBehavior() {
        return new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource source, ItemStack stack) {
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getType(source.level().registryAccess(), stack);
                
                try {
                    entityType.spawn(source.level(), stack, null, source.pos().relative(direction), EntitySpawnReason.DISPENSER, direction != Direction.UP, false);
                } catch (Exception var6) {
                    LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.pos(), var6);
                    return ItemStack.EMPTY;
                }
                
                stack.shrink(1);
                source.level().gameEvent(null, GameEvent.ENTITY_PLACE, source.pos());
                return stack;
            }
        };
    }
    
    public ArchitecturySpawnEggItem(RegistrySupplier<? extends EntityType<? extends Mob>> entityType, Properties properties) {
        this(entityType, properties, createDispenseItemBehavior());
    }
    
    public ArchitecturySpawnEggItem(RegistrySupplier<? extends EntityType<? extends Mob>> entityType, Properties properties,
                                    @Nullable DispenseItemBehavior dispenseItemBehavior) {
        super(null, properties);
        this.entityType = Objects.requireNonNull(entityType, "entityType");
        SpawnEggItem.BY_ID.remove(null);
        entityType.listen(type -> {
            LOGGER.debug("Registering spawn egg {} for {}", toString(),
                    Objects.toString(type.arch$registryName()));
            SpawnEggItem.BY_ID.put(type, this);
            this.defaultType = type;
            
            if (dispenseItemBehavior != null) {
                DispenserBlock.registerBehavior(this, dispenseItemBehavior);
            }
        });
    }
    
    @Override
    public EntityType<?> getType(HolderLookup.Provider provider, ItemStack itemStack) {
        EntityType<?> type = super.getType(provider, itemStack);
        return type == null ? entityType.get() : type;
    }
}
