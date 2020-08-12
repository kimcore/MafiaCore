package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Terrorist extends MafiaJob {
    public Terrorist() {
        this.name = "테러리스트";
        this.colored = "§5테러리스트";
        this.description = "밤에 한명을 선택해 대상이 마피아일때, 마피아에게 공격을 받으면 함께 폭사합니다. 또한 투표로 처형당할때 자신이 투표한 사람과 함께 폭사할 수 있습니다.";
        this.GUITitle = "§5마피아 같은 사람을 선택하세요";
        this.id = JOB_TERRORIST;
        this.team = TEAM_CITIZEN;
        this.type = TYPE_SPECIALS;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        return true;
    }
}
