package io.icker.factions.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.Command;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import io.icker.factions.database.PlayerConfig;
import io.icker.factions.util.Message;

public class ZoneMsgCommand implements Command<ServerCommandSource> {
  @Override
  public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
      ServerCommandSource source = context.getSource();
      ServerPlayerEntity player = source.getPlayer();

      PlayerConfig config = PlayerConfig.get(player.getUuid());
      boolean zoneMsg = !config.currentZoneMessage;
      config.setZoneMsg(zoneMsg);

      new Message("Successfully toggled zone messages")
      .filler("·")
      .add(
          new Message(zoneMsg ? "ON" : "OFF")
          .format(zoneMsg ? Formatting.GREEN : Formatting.RED)
      )
      .send(player, false);

      return 1;
  }
}
