package com.github.kimcore.mafia;

import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.entity.Player;

public abstract class MafiaJob {
    public static int TEAM_CITIZEN = 0;
    public static int TEAM_MAFIA = 1;
    public static int TYPE_MAFIA = 0;
    public static int TYPE_MAFIA_TEAM = 1;
    public static int TYPE_COP = 2;
    public static int TYPE_DOCTOR = 3;
    public static int TYPE_SPECIALS = 4;
    public static int TYPE_CITIZEN = 5;
    public static int JOB_BEASTMAN = 0;
    public static int JOB_CITIZEN = 1;
    public static int JOB_COP = 2;
    public static int JOB_DETECTIVE = 3;
    public static int JOB_DOCTOR = 4;
    public static int JOB_GANGSTER = 5;
    public static int JOB_MADAME = 6;
    public static int JOB_MAFIA = 7;
    public static int JOB_MINER = 8;
    public static int JOB_POLITICIAN = 9;
    public static int JOB_REPORTER = 10;
    public static int JOB_SHAMAN  = 11;
    public static int JOB_SOLDIER = 12;
    public static int JOB_SPY = 13;
    public static int JOB_TERRORIST = 14;

    public String name;
    public String colored;
    public String description;
    public String GUITitle;
    public int id;
    public int team;
    public int type;
    public boolean changeable = true;
    public boolean abilityUsed = false;
    public boolean isDead = false;
    public boolean isBlessed = false;
    public boolean isSeduced = false;
    public boolean isBlackmailed = false;
    public boolean isInvestigated = false;
    public boolean isContacted = false;
    public boolean oneTimeUsed = false;
    public boolean wasMiner = false;
    public MafiaJob minedJob = null;
    public Player abilityTarget = null;

    public boolean useAbility(Player player, Player target, MafiaSession Session, InventoryContents inventoryContents) {
        if (isSeduced) {
            inventoryContents.inventory().close(player);
            player.sendMessage("§7[§c마피아 §b코어§7] §c마담에게 유혹당해 능력을 사용할 수 없습니다.");
            return false;
        } else if (abilityUsed) {
            if (changeable) {
                abilityTarget = target;
                abilityUsed = ability(player, Session, inventoryContents);
                return abilityUsed;
            } else {
                inventoryContents.inventory().close(player);
                player.sendMessage("§7[§c마피아 §b코어§7] §c이미 능력을 사용하셨습니다.");
                return false;
            }
        } else {
            abilityTarget = target;
            abilityUsed = ability(player, Session, inventoryContents);
            return abilityUsed;
        }
    }

    protected abstract boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents);
}
