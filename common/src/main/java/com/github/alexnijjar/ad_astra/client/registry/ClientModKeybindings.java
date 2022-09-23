package com.github.alexnijjar.ad_astra.client.registry;

import com.github.alexnijjar.ad_astra.entities.vehicles.RocketEntity;

import com.github.alexnijjar.ad_astra.networking.NetworkHandling;
import com.github.alexnijjar.ad_astra.networking.packets.client.KeybindPacket;
import com.github.alexnijjar.ad_astra.networking.packets.client.LaunchRocketPacket;
import com.github.alexnijjar.ad_astra.networking.packets.server.StartRocketPacket;
import dev.architectury.event.events.client.ClientTickEvent;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ClientModKeybindings {

	public static boolean clickingJump;
	public static boolean clickingSprint;
	public static boolean clickingForward;
	public static boolean clickingBack;
	public static boolean clickingLeft;
	public static boolean clickingRight;

	private static boolean sentJumpPacket;
	private static boolean sentSprintPacket;
	private static boolean sentForwardPacket;
	private static boolean sentBackPacket;
	private static boolean sentLeftPacket;
	private static boolean sentRightPacket;

	public static void register() {
		ClientTickEvent.CLIENT_POST.register(client -> {

			clickingJump = client.options.jumpKey.isPressed();
			clickingSprint = client.options.sprintKey.isPressed();
			clickingForward = client.options.forwardKey.isPressed();
			clickingBack = client.options.backKey.isPressed();
			clickingLeft = client.options.leftKey.isPressed();
			clickingRight = client.options.rightKey.isPressed();

			if (client.world != null) {
				if (client.player != null) {
					if (client.options.jumpKey.isPressed()) {
						if (client.player.getVehicle() instanceof RocketEntity rocket) {
							if (!rocket.isFlying()) {
								if (rocket.getFluidAmount() >= RocketEntity.getRequiredAmountForLaunch(rocket.getFluidHolder())) {
									NetworkHandling.CHANNEL.sendToServer(new LaunchRocketPacket());
								} else if (sentJumpPacket) {
									client.player.sendMessage(Text.translatable("message.ad_astra.no_fuel"), false);
								}
							}
						}
					}
				}

				if (clickingJump && sentJumpPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.JUMP, true));
					sentJumpPacket = false;
				}

				if (clickingSprint && sentSprintPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.SPRINT, true));
					sentSprintPacket = false;
				}

				if (clickingForward && sentForwardPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.FORWARD, true));
					sentForwardPacket = false;
				}

				if (clickingBack && sentBackPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.BACK, true));
					sentBackPacket = false;
				}

				if (clickingLeft && sentLeftPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.LEFT, true));
					sentLeftPacket = false;
				}

				if (clickingRight && sentRightPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.RIGHT, true));
					sentRightPacket = false;
				}

				if (!clickingJump && !sentJumpPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.JUMP, false));
					sentJumpPacket = true;
				}

				if (!clickingSprint && !sentSprintPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.SPRINT, false));
					sentSprintPacket = true;
				}

				if (!clickingForward && !sentForwardPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.FORWARD, false));
					sentForwardPacket = true;
				}

				if (!clickingBack && !sentBackPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.BACK, false));
					sentBackPacket = true;
				}

				if (!clickingLeft && !sentLeftPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.LEFT, false));
					sentLeftPacket = true;
				}

				if (!clickingRight && !sentRightPacket) {
					NetworkHandling.CHANNEL.sendToServer(new KeybindPacket(KeybindPacket.Keybind.RIGHT, false));
					sentRightPacket = true;
				}
			}
		});
	}

	private static PacketByteBuf createKeyDownBuf() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		MinecraftClient client = MinecraftClient.getInstance();
		buf.writeUuid(client.player.getUuid());
		buf.writeBoolean(true);
		return buf;
	}

	private static PacketByteBuf createKeyUpBuf() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		MinecraftClient client = MinecraftClient.getInstance();
		buf.writeUuid(client.player.getUuid());
		buf.writeBoolean(false);
		return buf;
	}
}