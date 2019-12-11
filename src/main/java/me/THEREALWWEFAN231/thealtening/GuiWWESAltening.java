/*
Copyright (C) 2019 THEREALWWEFAN231

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.THEREALWWEFAN231.thealtening;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import me.THEREALWWEFAN231.thealtening.mixin.mixins.interfacemixin.InterfaceMixinMinecraft;
import me.THEREALWWEFAN231.thealtening.switcher.AlteningServiceType;
import me.THEREALWWEFAN231.thealtening.switcher.ServiceSwitcher;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Session;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class GuiWWESAltening extends Screen {

	public static ArrayList<String> altInformation = new ArrayList<String>();
	public static String paidAPIToken;
	public static ServiceSwitcher serviceSwitcher = new ServiceSwitcher();

	public Screen guiScreen;//old screen

	public String responseMessage;

	public TextFieldWidget apiTokenField;
	public ButtonWidget checkAPITokenButton;

	public TextFieldWidget freeTokenField;
	public ButtonWidget useButton;//for free mode

	public GuiWWESAltening(Screen guiScreen) {
		super(new LiteralText("GuiWWESAltening"));
		this.guiScreen = guiScreen;
	}

	public void init() {

		int widthOfComponents = 200;
		this.apiTokenField = new TextFieldWidget(this.font, this.width / 2 - widthOfComponents / 2, this.height / 2 - this.height / 6, widthOfComponents, 20, "");
		this.freeTokenField = new TextFieldWidget(this.font, this.width / 2 - widthOfComponents / 2, this.height / 2 - this.height / 6 + 40, widthOfComponents, 20, "");

		//before you go on about OMG THIS IS SO BAD YOU DIDENT ADD THESES TO THE BUTTONLIST, its "easier" to do it this way without calling initgui every time theres a "refresh"
		this.checkAPITokenButton = new ButtonWidget(this.width / 2 - widthOfComponents / 2, this.height / 2 + this.height / 6, widthOfComponents, 20, GuiWWESAltening.paidAPIToken == null ? "Check API Token" : "Generate alt", new ButtonWidget.PressAction() {/*no lambda here :eyes */
			@Override
			public void onPress(ButtonWidget button) {
			}
		});
		this.useButton = new ButtonWidget(this.width / 2 - widthOfComponents / 2, this.height / 2 + this.height / 6 + 22, 200, 20, GuiWWESAltening.paidAPIToken == null ? "Use free token" : "Logout", new ButtonWidget.PressAction() {/*no lambda here :eyes */
			@Override
			public void onPress(ButtonWidget button) {
			}
		});

		//this button doesnt need to be "special" although the other dident "need" to be special
		this.addButton(new ButtonWidget(this.width / 2 - widthOfComponents / 2, this.height / 2 + this.height / 6 + 44, 200, 20, "Back", new ButtonWidget.PressAction() {
			@Override
			public void onPress(ButtonWidget button) {
				GuiWWESAltening.this.minecraft.openScreen(GuiWWESAltening.this.guiScreen);
			}
		}));

		super.init();
	}

	public void tick() {

		this.apiTokenField.tick();
		this.freeTokenField.tick();

		super.tick();
	}

	public void render(int mouseX, int mouseY, float partialTicks) {

		this.renderBackground();

		this.font.drawWithShadow("WWEs' altening mod", this.width / 2 - this.font.getStringWidth("WWEs' altening mod") / 2, this.height / 20, -1);
		if (GuiWWESAltening.paidAPIToken == null) {
			//you are not allowed to change this without crediting THEREALWWEFAN231 or WWE Client
			this.font.drawWithShadow("Use code wwe for 20% off the altening", this.width / 2 - this.font.getStringWidth("Use code wwe for 20% off the altening") / 2, this.height / 20 + 10, -1);
		}

		this.font.drawWithShadow("API Token", this.width / 2 - this.font.getStringWidth("API Token") / 2, this.height / 2 - this.height / 6 - 12, -1);
		this.apiTokenField.render(mouseX, mouseY, partialTicks);
		this.font.drawWithShadow("Free account token", this.width / 2 - this.font.getStringWidth("Free account token") / 2, this.height / 2 - this.height / 6 + 28, -1);
		this.freeTokenField.render(mouseX, mouseY, partialTicks);

		this.checkAPITokenButton.render(mouseX, mouseY, partialTicks);
		this.useButton.render(mouseX, mouseY, partialTicks);

		if (this.responseMessage != null) {
			this.font.drawWithShadow(this.responseMessage, this.width / 2 - this.font.getStringWidth(this.responseMessage) / 2, this.height / 6, -1);
		}

		if (!GuiWWESAltening.altInformation.isEmpty()) {
			for (int i = 0; i < GuiWWESAltening.altInformation.size(); i++) {//we will do this loop just incase & and we index control so yeah
				String string = GuiWWESAltening.altInformation.get(i);
				this.font.drawWithShadow(string, this.width / 16, (this.height / 6) + ((i + 1) * 10), -1);
			}
		}

		super.render(mouseX, mouseY, partialTicks);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.minecraft.openScreen(this.guiScreen);
			return false;
		}

		this.apiTokenField.keyPressed(keyCode, scanCode, modifiers);
		this.freeTokenField.keyPressed(keyCode, scanCode, modifiers);

		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	public boolean charTyped(char chr, int keyCode) {
		this.apiTokenField.charTyped(chr, keyCode);
		this.freeTokenField.charTyped(chr, keyCode);
		
		return super.charTyped(chr, keyCode);
	}
	
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		
		this.apiTokenField.mouseClicked(mouseX, mouseY, button);
		this.freeTokenField.mouseClicked(mouseX, mouseY, button);

		if (this.checkAPITokenButton.isHovered()) {
			this.checkAPITokenButton.playDownSound(this.minecraft.getSoundManager());

			if ((this.apiTokenField.getText().isEmpty() || this.apiTokenField.getText().length() == 0) && GuiWWESAltening.paidAPIToken == null) {
				this.responseMessage = Formatting.GOLD + "The API Token field is empty";
			} else {

				this.responseMessage = Formatting.GOLD + "Trying";

				new Thread() {
					public void run() {
						if (GuiWWESAltening.paidAPIToken == null) {
							GuiWWESAltening.this.checkAPIToken();
						} else {
							GuiWWESAltening.this.generateAlt();
						}
					}
				}.start();
			}

		}

		if (this.useButton.isHovered()) {
			this.useButton.playDownSound(this.minecraft.getSoundManager());

			if ((this.freeTokenField.getText().isEmpty() || this.freeTokenField.getText().length() == 0) && GuiWWESAltening.paidAPIToken == null) {
				this.responseMessage = Formatting.GOLD + "Free account field is empty";
			} else {

				this.responseMessage = Formatting.GOLD + "Trying";

				new Thread() {
					public void run() {
						if (GuiWWESAltening.paidAPIToken == null) {
							GuiWWESAltening.this.checkFreeToken();
						} else {
							GuiWWESAltening.paidAPIToken = null;
							GuiWWESAltening.this.apiTokenField.setIsEditable(true);
							GuiWWESAltening.this.checkAPITokenButton.setMessage("Check API Token");
							GuiWWESAltening.this.useButton.setMessage("Use free token");
							GuiWWESAltening.this.responseMessage = null;
						}
					}
				}.start();
			}

		}
		
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public void checkAPIToken() {
		ArrayList<String> urlResponce = this.getLinesFromURL("http://api.thealtening.com/v1/license?token=" + this.apiTokenField.getText());

		String responceLines = "";
		for (String string : urlResponce) {
			responceLines += string;
		}

		if (responceLines.equals("")) {
			return;//most likely a url error and message most likely was shown on screen
		}

		JsonObject jsonObject = new JsonParser().parse(responceLines).getAsJsonObject();
		if (jsonObject.has("premium")) {
			GuiWWESAltening.paidAPIToken = this.apiTokenField.getText();
			this.responseMessage = null;
			this.checkAPITokenButton.setMessage("Generate alt");
			this.apiTokenField.setText("");
			this.apiTokenField.setIsEditable(false);

			this.useButton.setMessage("Logout");
		} else {
			this.responseMessage = Formatting.RED + "Not premium you will have to buy Basic/Premium to use the auto generator or go to the free mode";
			GuiWWESAltening.paidAPIToken = null;//just incase?
		}
	}

	public void generateAlt() {
		ArrayList<String> urlResponce = getLinesFromURL("http://api.thealtening.com/v1/generate?info=true&token=" + GuiWWESAltening.paidAPIToken);

		String responceLines = "";
		for (String s : urlResponce) {
			responceLines += s;
		}

		if (responceLines.equals("")) {//this really really really shouldent happen
			return;//most likely a url error and message most likely was shown on screen
		}

		JsonObject jsonObject = new JsonParser().parse(responceLines).getAsJsonObject();

		GuiWWESAltening.serviceSwitcher.switchToService(AlteningServiceType.THEALTENING);

		YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
		YggdrasilUserAuthentication yggdrasilUserAuthentication = (YggdrasilUserAuthentication) yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
		yggdrasilUserAuthentication.setUsername(jsonObject.get("token").getAsString());
		yggdrasilUserAuthentication.setPassword("UsingWWESAltening");
		try {
			yggdrasilUserAuthentication.logIn();
		} catch (AuthenticationException e) {
			e.printStackTrace();
			this.responseMessage = Formatting.RED + "Failed to login " + e.getMessage();
			return;
		}

		Session session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "LEGACY");
		((InterfaceMixinMinecraft) this.minecraft).setSession(session);

		GuiWWESAltening.altInformation.clear();
		GuiWWESAltening.altInformation.add("Username : " + session.getUsername());
		if (jsonObject.has("info")) {
			JsonObject info = jsonObject.get("info").getAsJsonObject();
			if (info.has("hypixel.lvl")) {
				GuiWWESAltening.altInformation.add("Hypixel level : " + info.get("hypixel.lvl").getAsString());
			}
			if (info.has("hypixel.rank")) {
				GuiWWESAltening.altInformation.add("Hypixel rank : " + info.get("hypixel.rank").getAsString());
			}
			if (info.has("mineplex.lvl")) {
				GuiWWESAltening.altInformation.add("Mineplex level : " + info.get("mineplex.lvl").getAsString());
			}
			if (info.has("mineplex.rank")) {
				GuiWWESAltening.altInformation.add("Mineplex rank : " + info.get("mineplex.rank").getAsString());
			}
			if (info.has("labymod.cape")) {
				GuiWWESAltening.altInformation.add("Labymod cape : " + info.get("labymod.cape").getAsString());
			}
			if (info.has("5zig.cape")) {
				GuiWWESAltening.altInformation.add("5zig cape : " + info.get("5zig.cape").getAsString());
			}
		}
		this.responseMessage = null;
	}

	public void checkFreeToken() {

		GuiWWESAltening.serviceSwitcher.switchToService(AlteningServiceType.THEALTENING);
		YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
		YggdrasilUserAuthentication yggdrasilUserAuthentication = (YggdrasilUserAuthentication) yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
		yggdrasilUserAuthentication.setUsername(this.freeTokenField.getText());
		yggdrasilUserAuthentication.setPassword("UsingWWESAltening");
		try {
			yggdrasilUserAuthentication.logIn();
		} catch (AuthenticationException e) {
			e.printStackTrace();
			this.responseMessage = Formatting.RED + "Failed to login " + e.getMessage();
			return;
		}

		Session session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "LEGACY");
		((InterfaceMixinMinecraft) this.minecraft).setSession(session);

		GuiWWESAltening.altInformation.clear();
		GuiWWESAltening.altInformation.add("Username : " + session.getUsername());

		this.responseMessage = null;
	}

	public ArrayList<String> getLinesFromURL(String urlString) {
		ArrayList<String> lines = new ArrayList<String>();

		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			this.responseMessage = Formatting.RED + "Error " + e.getMessage();//i dont really know about this
		}

		return lines;
	}

}
