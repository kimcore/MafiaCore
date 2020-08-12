package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Beastman extends MafiaJob {
    public Beastman() {
        this.name = "짐승인간";
        this.colored = "§c짐승인간";
        this.description = "마피아와 접선한 후 마피아가 모두 사망할 경우 밤마다 사람들을 죽일 수 있습니다.";
        this.GUITitle = "§c죽일 사람을 선택하세요";
        this.id = JOB_BEASTMAN;
        this.team = TEAM_MAFIA;
        this.type = TYPE_MAFIA_TEAM;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        if (!this.isContacted && !Session.AllMafiaDead) {
            player.sendMessage("§7[§c마피아 §b코어§7] §c접선 후 모든 마피아가 죽어야 사용할 수 있습니다.");
            return false;
        } else {
            return true;
        }
    }
}
