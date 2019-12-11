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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import me.THEREALWWEFAN231.thealtening.mixin.mixins.interfacemixin.InterfaceMixinMinecraft;
import me.THEREALWWEFAN231.thealtening.switcher.AlteningServiceType;
import me.THEREALWWEFAN231.thealtening.switcher.ServiceSwitcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public class GuiWWESAltening extends GuiScreen {

	public static ArrayList<String> altInformation = new ArrayList<String>();
	public static String paidAPIToken;
	public static ServiceSwitcher serviceSwitcher = new ServiceSwitcher();

	public GuiScreen guiScreen;//old screen

	public String responseMessage;

	public GuiTextField apiTokenField;
	public GuiButton checkAPITokenButton;

	public GuiTextField freeTokenField;
	public GuiButton useButton;//for free mode

	public GuiWWESAltening(GuiScreen guiScreen) {
		this.guiScreen = guiScreen;
	}

	public void initGui() {

		int widthOfComponents = 200;
		this.apiTokenField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - widthOfComponents / 2, this.height / 2 - this.height / 6, widthOfComponents, 20);
		this.freeTokenField = new GuiTextField(1, this.fontRendererObj, this.width / 2 - widthOfComponents / 2, this.height / 2 - this.height / 6 + 40, widthOfComponents, 20);

		//before you go on about OMG THIS IS SO BAD YOU DIDENT ADD THESES TO THE BUTTONLIST, its "easier" to do it this way without calling initgui every time theres a "refresh"
		this.checkAPITokenButton = new GuiButton(0, this.width / 2 - widthOfComponents / 2, this.height / 2 + this.height / 6, GuiWWESAltening.paidAPIToken == null ? "Check API Token" : "Generate alt");
		this.useButton = new GuiButton(2, this.width / 2 - widthOfComponents / 2, this.height / 2 + this.height / 6 + 22, GuiWWESAltening.paidAPIToken == null ? "Use free token" : "Logout");

		//this button doesnt need to be "special" although the other dident "need" to be special
		this.buttonList.add(new GuiButton(3, this.width / 2 - widthOfComponents / 2, this.height / 2 + this.height / 6 + 44, "Back"));

		super.initGui();
	}

	public void updateScreen() {

		this.apiTokenField.updateCursorCounter();
		this.freeTokenField.updateCursorCounter();

		super.updateScreen();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		this.drawDefaultBackground();

		this.fontRendererObj.drawStringWithShadow("WWEs' altening mod", this.width / 2 - this.fontRendererObj.getStringWidth("WWEs' altening mod") / 2, this.height / 20, -1);
		if (GuiWWESAltening.paidAPIToken == null) {
			//you are not allowed to change this without crediting THEREALWWEFAN231 or WWE Client
			this.fontRendererObj.drawStringWithShadow("Use code wwe for 20% off the altening", this.width / 2 - this.fontRendererObj.getStringWidth("Use code wwe for 20% off the altening") / 2, this.height / 20 + 10, -1);
		}
		
		this.fontRendererObj.drawStringWithShadow("API Token", this.width / 2 - this.fontRendererObj.getStringWidth("API Token") / 2, this.height / 2 - this.height / 6 - 12, -1);
		this.apiTokenField.drawTextBox();
		this.fontRendererObj.drawStringWithShadow("Free account token", this.width / 2 - this.fontRendererObj.getStringWidth("Free account token") / 2, this.height / 2 - this.height / 6 + 28, -1);
		this.freeTokenField.drawTextBox();

		this.checkAPITokenButton.drawButton(this.mc, mouseX, mouseY);
		this.useButton.drawButton(this.mc, mouseX, mouseY);

		if (this.responseMessage != null) {
			this.fontRendererObj.drawStringWithShadow(this.responseMessage, this.width / 2 - this.fontRendererObj.getStringWidth(this.responseMessage) / 2, this.height / 6, -1);
		}

		if (!GuiWWESAltening.altInformation.isEmpty()) {
			for (int i = 0; i < GuiWWESAltening.altInformation.size(); i++) {//we will do this loop just incase & and we index control so yeah
				String string = GuiWWESAltening.altInformation.get(i);
				this.fontRendererObj.drawStringWithShadow(string, this.width / 16, (this.height / 6) + ((i + 1) * 10), -1);
			}
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {

		if (keyCode == Keyboard.KEY_ESCAPE) {
			this.mc.displayGuiScreen(this.guiScreen);
			return;
		}

		this.apiTokenField.textboxKeyTyped(typedChar, keyCode);
		this.freeTokenField.textboxKeyTyped(typedChar, keyCode);

		super.keyTyped(typedChar, keyCode);
	}

	protected void actionPerformed(GuiButton button) throws IOException {

		if (button.id == 3) {
			this.mc.displayGuiScreen(this.guiScreen);
			return;
		}

		super.actionPerformed(button);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

		this.apiTokenField.mouseClicked(mouseX, mouseY, mouseButton);
		this.freeTokenField.mouseClicked(mouseX, mouseY, mouseButton);

		if (this.checkAPITokenButton.isMouseOver()) {
			this.checkAPITokenButton.playPressSound(this.mc.getSoundHandler());

			if ((this.apiTokenField.getText().isEmpty() || this.apiTokenField.getText().length() == 0) && GuiWWESAltening.paidAPIToken == null) {
				this.responseMessage = EnumChatFormatting.GOLD + "The API Token field is empty";
			} else {

				this.responseMessage = EnumChatFormatting.GOLD + "Trying";

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

		if (this.useButton.isMouseOver()) {
			this.useButton.playPressSound(this.mc.getSoundHandler());

			if ((this.freeTokenField.getText().isEmpty() || this.freeTokenField.getText().length() == 0) && GuiWWESAltening.paidAPIToken == null) {
				this.responseMessage = EnumChatFormatting.GOLD + "Free account field is empty";
			} else {

				this.responseMessage = EnumChatFormatting.GOLD + "Trying";

				new Thread() {
					public void run() {
						if (GuiWWESAltening.paidAPIToken == null) {
							GuiWWESAltening.this.checkFreeToken();
						} else {
							GuiWWESAltening.paidAPIToken = null;
							GuiWWESAltening.this.apiTokenField.setEnabled(true);
							GuiWWESAltening.this.checkAPITokenButton.displayString = "Check API Token";
							GuiWWESAltening.this.useButton.displayString = "Use free token";
							GuiWWESAltening.this.responseMessage = null;
						}
					}
				}.start();
			}

		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
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
			this.checkAPITokenButton.displayString = "Generate alt";
			this.apiTokenField.setText("");
			this.apiTokenField.setEnabled(false);

			this.useButton.displayString = "Logout";
		} else {
			this.responseMessage = EnumChatFormatting.RED + "Not premium you will have to buy Basic/Premium to use the auto generator or go to the free mode";
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
			this.responseMessage = EnumChatFormatting.RED + "Failed to login " + e.getMessage();
			return;
		}

		Session session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "LEGACY");
		((InterfaceMixinMinecraft) this.mc).setSession(session);

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
			this.responseMessage = EnumChatFormatting.RED + "Failed to login " + e.getMessage();
			return;
		}

		Session session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "LEGACY");
		((InterfaceMixinMinecraft) this.mc).setSession(session);

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
			this.responseMessage = EnumChatFormatting.RED + "Error " + e.getMessage();//i dont really know about this
		}

		return lines;
	}

}
