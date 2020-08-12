package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Miner extends MafiaJob {
    public Miner() {
        this.name = "도굴꾼";
        this.colored = "§e도굴꾼";
        this.description = "첫번째 밤에 마피아에게 살해당한 사람의 직업을 얻습니다.";
        this.id = JOB_MINER;
        this.team = TEAM_CITIZEN;
        this.team = TYPE_SPECIALS;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        return false;
    }
}
