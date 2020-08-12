package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Cop extends MafiaJob {
    public Cop() {
        this.name = "경찰";
        this.colored = "§9경찰";
        this.description = "밤마다 한 사람을 조사하여 그 사람이 마피아인지 아닌지 알 수 있습니다.";
        this.GUITitle = "§9조사할 사람을 선택하세요";
        this.id = JOB_COP;
        this.team = TEAM_CITIZEN;
        this.type = TYPE_COP;
        this.changeable = false;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        inventoryContents.inventory().close(player);
        if (Session.getJob(abilityTarget).id == MafiaJob.JOB_MAFIA) {
            player.sendMessage("§7[§c마피아 §b코어§7] §b" + abilityTarget.getName() + "§e님은 §c마피아입니다.");
        } else {
            player.sendMessage("§7[§c마피아 §b코어§7] §b" + abilityTarget.getName() + "§e님은 §c마피아가 아닙니다.");
        }
        return true;
    }
}
