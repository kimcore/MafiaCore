package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Soldier extends MafiaJob {
    public Soldier() {
        this.name = "군인";
        this.colored = "§2군인";
        this.description = "마피아의 공격을 한번 막을 수 있고 스파이에게 조사당할 경우 스파이의 존재를 알 수 있습니다.";
        this.id = JOB_SOLDIER;
        this.team = TEAM_MAFIA;
        this.type = TYPE_SPECIALS;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        return false;
    }
}
