package io.icker.factions.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.icker.factions.database.Database;
import io.icker.factions.database.Member;
import io.icker.factions.util.FactionsUtil;

import com.mojang.brigadier.Command;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class DisbandCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();

		Member member = Database.Members.get(player.getUuid());
        member.getFaction().remove();

		PlayerManager manager = context.getSource().getMinecraftServer().getPlayerManager();
		for (ServerPlayerEntity p : manager.getPlayerList()) {
			manager.sendCommandTree(p);
		}

		FactionsUtil.Message.sendSuccess(player, "Success! Faction disbanded");
		return 1;
	}
}