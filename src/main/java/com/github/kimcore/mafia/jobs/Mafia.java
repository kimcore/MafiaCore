package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Mafia extends MafiaJob {
    public Mafia() {
        this.name = "마피아";
        this.colored = "§c마피아";
        this.description = "밤마다 플레이어 한 명을 죽일 수 있습니다.";
        this.GUITitle = "§c죽일 사람을 선택하세요";
        this.id = JOB_MAFIA;
        this.team = TEAM_MAFIA;
        this.type = TYPE_MAFIA;
        this.isContacted = true;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        return true;
    }
}
