package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Shaman extends MafiaJob {
    public Shaman() {
        this.name = "영매";
        this.colored = "§d영매";
        this.description = "죽은 사람과 대화를 할 수 있으며, 밤마다 죽은 사람을 선택하여 직업을 알아내고 대상을 성불시킵니다.";
        this.GUITitle = "§d성불할 사람을 선택하세요";
        this.changeable = false;
        this.id = JOB_SHAMAN;
        this.team = TEAM_CITIZEN;
        this.type = TYPE_SPECIALS;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        Session.getJob(this.abilityTarget).isBlessed = true;
        player.sendMessage("§7[§c마피아 §b코어§7] §b" + this.abilityTarget.getName() + "§e님을 §d성불했습니다.");
        player.sendMessage("§7[§c마피아 §b코어§7] §b" + this.abilityTarget.getName() + "§e님의 직업은 " + Session.getJob(abilityTarget).colored + " §e입니다.");
        return true;
    }
}
