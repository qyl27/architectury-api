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

package dev.architectury.test.networking;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class ButtonClickedMessage extends BaseC2SMessage {
    private final int buttonId;
    
    /**
     * To send this message from client to server, call new ButtonClickedMessage(id).sendToServer()
     */
    public ButtonClickedMessage(int id) {
        buttonId = id;
    }
    
    public ButtonClickedMessage(RegistryFriendlyByteBuf buf) {
        buttonId = buf.readVarInt();
    }
    
    @Override
    public MessageType getType() {
        return TestModNet.BUTTON_CLICKED;
    }
    
    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(buttonId);
    }
    
    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.getPlayer().sendSystemMessage(Component.literal("You clicked button #" + buttonId));
    }
}