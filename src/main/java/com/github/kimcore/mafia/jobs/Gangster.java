package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Gangster extends MafiaJob {
    public Gangster() {
        this.name = "건달";
        this.colored = "§6건달";
        this.description = "밤에 한 사람을 지목해서 대상이 다음 날 투표를 할 수 없도록 협박합니다.";
        this.GUITitle = "§6협박할 사람을 선택하세요";
        this.id = JOB_GANGSTER;
        this.team = TEAM_CITIZEN;
        this.type = TYPE_SPECIALS;
        this.changeable = false;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        inventoryContents.inventory().close(player);
        Session.getJob(this.abilityTarget).isBlackmailed = true;
        player.sendMessage("§7[§c마피아 §b코어§7] §b" + this.abilityTarget.getName() + "§c님에게 위협을 가했습니다.");
        this.abilityTarget.sendMessage("§7[§c마피아 §b코어§7] §c의문의 괴한으로부터 협박을 당했습니다.");
        return true;
    }
}
