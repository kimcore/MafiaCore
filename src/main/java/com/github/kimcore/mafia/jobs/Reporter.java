package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Reporter extends MafiaJob {
    public Reporter() {
        this.name = "기자";
        this.colored = "§7기자";
        this.description = "밤에 한 명을 선택해서 취재해 대상의 직업을 모두에게 공개합니다. 단, 한번만 사용할 수 있고, 두번째 밤부터 사용할 수 있습니다.";
        this.GUITitle = "§7취재할 사람을 선택하세요";
        this.id = JOB_REPORTER;
        this.team = TEAM_CITIZEN;
        this.type = TYPE_SPECIALS;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        if (Session.currentDay == 1) {
            player.sendMessage("§7[§c마피아 §b코어§7] §c1번째 밤에는 능력을 사용할 수 없습니다.");
            return false;
        } else if (oneTimeUsed) {
            inventoryContents.inventory().close(player);
            player.sendMessage("§7[§c마피아 §b코어§7] §c이미 능력을 사용하셨습니다.");
            return false;
        } else {
            return true;
        }
    }
}
