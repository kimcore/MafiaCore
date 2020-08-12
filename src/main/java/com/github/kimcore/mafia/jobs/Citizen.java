package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Citizen extends MafiaJob {
    public Citizen() {
        this.name = "시민";
        this.colored = "§b시민";
        this.description = "투표로 마피아팀을 모두 제거할 경우 승리합니다.";
        this.id = JOB_CITIZEN;
        this.team = TEAM_CITIZEN;
        this.type = TYPE_CITIZEN;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        return false;
    }
}
