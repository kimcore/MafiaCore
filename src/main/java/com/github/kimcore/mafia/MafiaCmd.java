package com.github.kimcore.mafia;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class MafiaCmd implements CommandExecutor {
    private final MafiaCore Core;

    public MafiaCmd(MafiaCore core) {
        Core = core;
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, Command command, String alias, String[] args) {
        String defaultMessage = "\n§7======== [§c마피아 §b코어§7] ========\n" +
                "§a버전 1 §e[2020.01]\n" +
                "§7/마피아 §f: §e이 도움말을 보여줍니다.\n" +
                "§7/마피아 준비 §f: §e마피아 게임을 준비 합니다.\n" +
                "§7/마피아 참가 §f: §e마피아 게임에 참가 합니다.\n" +
                "§7/마피아 시작 §f: §e마피아 게임을 시작 합니다.\n" +
                "§7/마피아 취소 §f: §e마피아 게임을 취소 합니다.\n"
                ;
        if (args.length == 0) {
            commandSender.sendMessage(defaultMessage);
        } else if (args[0].equals("준비") || args[0].equals("ready")) {
            Core.readyGame(commandSender.getName());
        } else if (args[0].equals("참가") || args[0].equals("join")) {
            Core.joinGame(commandSender.getName());
        } else if (args[0].equals("퇴장") || args[0].equals("leave")) {
            Core.quitGame(commandSender.getName());
        } else if (args[0].equals("시작") || args[0].equals("start")) {
            Core.startGame(commandSender.getName());
        } else if (args[0].equals("취소") || args[0].equals("cancel")) {
            if (commandSender.isOp()) {
                Core.cancelGame(commandSender.getName());
            } else {
                commandSender.sendMessage("§7[§c마피아 §b코어§7] §c관리자 외에는 게임을 취소할 수 없습니다.");
            }
        } else if (args[0].equals("복구") || args[0].equals("restore")) {
            if (commandSender.isOp()) {
                for (Player player : Core.getServer().getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    File InventoryFile = new File(Core.getDataFolder(), "inventory/" + uuid + ".yml");
                    if (InventoryFile.exists()) {
                        Core.restorePlayerState(player);
                        Core.sendMessage("§b" + player.getName() + "§e님의 상태를 복구했습니다.");
                    }
                }
            } else {
                commandSender.sendMessage("§7[§c마피아 §b코어§7] §c사용할 권한이 없습니다.");
            }
        } else { commandSender.sendMessage(defaultMessage); }
        return true;
    }
}
