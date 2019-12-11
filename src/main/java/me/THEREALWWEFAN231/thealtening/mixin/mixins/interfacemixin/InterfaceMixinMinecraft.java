package me.THEREALWWEFAN231.thealtening.mixin.mixins.interfacemixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

@Mixin(MinecraftClient.class)
public interface InterfaceMixinMinecraft {
	
	@Accessor("session")
	void setSession(Session session);
	
}
