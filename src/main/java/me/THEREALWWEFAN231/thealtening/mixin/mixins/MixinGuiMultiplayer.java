package me.THEREALWWEFAN231.thealtening.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.THEREALWWEFAN231.thealtening.GuiWWESAltening;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiMultiplayer.class)
public class MixinGuiMultiplayer extends GuiScreen {

	@Inject(method = "initGui", at = @At("RETURN"))
	public void initGui(CallbackInfo callbackInfo) {
		//36 is a cool number ok
		this.buttonList.add(new GuiButton(36, 7, 7, 75, 20, "The Altening"));
	}
	
	@Inject(method = "actionPerformed", at = @At("RETURN"))
	protected void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
		if(button.id == 36) {
			this.mc.displayGuiScreen(new GuiWWESAltening(this));
		}
	}

}
