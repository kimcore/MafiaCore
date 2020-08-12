package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Doctor extends MafiaJob {
    public Doctor() {
        this.name = "의사";
        this.colored = "§a의사";
        this.description = "밤에 한 사람을 지목해서 대상이 마피아에게 공격받을 경우 대상을 치료합니다.";
        this.GUITitle = "§a치료할 사람을 선택하세요";
        this.id = JOB_DOCTOR;
        this.team = TEAM_CITIZEN;
        this.type = TYPE_DOCTOR;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        return true;
    }
}
