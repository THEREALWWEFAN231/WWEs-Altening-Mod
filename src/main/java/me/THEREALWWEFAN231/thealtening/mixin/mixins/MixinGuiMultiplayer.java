package me.THEREALWWEFAN231.thealtening.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.THEREALWWEFAN231.thealtening.GuiWWESAltening;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(MultiplayerScreen.class)
public class MixinGuiMultiplayer extends Screen {

	protected MixinGuiMultiplayer(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("RETURN"))
	public void initGui(CallbackInfo callbackInfo) {
		//36 is a cool number ok
		this.addButton(new ButtonWidget(7, 7, 75, 20, "The Altening", new ButtonWidget.PressAction() {
			
			@Override
			public void onPress(ButtonWidget button) {
				MixinGuiMultiplayer.this.minecraft.openScreen(new GuiWWESAltening(MixinGuiMultiplayer.this));
			}
		}));
	}
	
}
