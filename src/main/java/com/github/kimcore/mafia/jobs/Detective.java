package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Detective extends MafiaJob {
    public Detective() {
        this.name = "사립탐정";
        this.colored = "§6사립탐정";
        this.description = "밤에 한 사람을 조사해서 그 사람이 누구에게 능력을 사용했는지 볼 수 있습니다.";
        this.GUITitle = "§6조사할 사람을 선택하세요";
        this.id = JOB_DETECTIVE;
        this.team = TEAM_CITIZEN;
        this.type = TYPE_SPECIALS;
        this.changeable = false;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        Session.getJob(abilityTarget).isInvestigated = true;
        player.sendMessage("§7[§c마피아 §b코어§7] §b" + abilityTarget.getName() + "§e님을 조사합니다.");
        player.sendMessage("§7[§c마피아 §b코어§7] §e시계를 사용해서 대상의 능력 사용을 조사하세요.");
        return true;
    }
}
