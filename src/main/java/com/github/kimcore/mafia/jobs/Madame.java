package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Madame extends MafiaJob {
    public Madame() {
        this.name = "마담";
        this.colored = "§c마담";
        this.description = "낮에 투표한 대상이 하루 동안 능력을 사용하지 못하도록 합니다.";
        this.id = JOB_MADAME;
        this.team = TEAM_MAFIA;
        this.type = TYPE_MAFIA_TEAM;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        return false;
    }
}
