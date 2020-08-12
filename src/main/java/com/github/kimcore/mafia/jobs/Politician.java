package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Politician extends MafiaJob {
    public Politician() {
        this.name = "정치인";
        this.colored = "§1정치인";
        this.description = "투표로 처형당하지 않고, 두 번 투표할 수 있습니다.";
        this.id = JOB_POLITICIAN;
        this.team = TEAM_CITIZEN;
        this.team = TYPE_SPECIALS;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        return false;
    }
}
