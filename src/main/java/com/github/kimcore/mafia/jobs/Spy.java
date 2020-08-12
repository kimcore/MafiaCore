package com.github.kimcore.mafia.jobs;

import fr.minuskube.inv.content.InventoryContents;
import com.github.kimcore.mafia.MafiaJob;
import com.github.kimcore.mafia.MafiaSession;
import org.bukkit.entity.Player;

public class Spy extends MafiaJob {
    public Spy() {
        this.name = "스파이";
        this.colored = "§c스파이";
        this.description = "밤에 한명을 선택해 대상의 직업을 알아낼 수 있고 대상이 마피아일 경우 접선합니다.";
        this.GUITitle = "§c직업을 알아낼 사람을 선택하세요";
        this.id = JOB_SPY;
        this.team = TEAM_MAFIA;
        this.type = TYPE_MAFIA_TEAM;
        this.changeable = false;
    }

    @Override
    protected boolean ability(Player player, MafiaSession Session, InventoryContents inventoryContents) {
        inventoryContents.inventory().close(player);
        player.sendMessage("§7[§c마피아 §b코어§7] §b" + abilityTarget.getName() + "§e님은 " + Session.getJob(this.abilityTarget).colored + "§e입니다.");
        if (Session.getJob(abilityTarget).id == MafiaJob.JOB_SOLDIER) {
            player.sendMessage("§7[§c마피아 §b코어§7] §2군인§ce에게 §c정체가 탄로났습니다.");
            abilityTarget.sendMessage("§7[§c마피아 §b코어§7] §c스파이§e인 §b" + player.getName() + "§e님에게 §c조사당했습니다.");
        } else if (Session.getJob(abilityTarget).id == MafiaJob.JOB_MAFIA) {
            this.isContacted = true;
            player.sendMessage("§7[§c마피아 §b코어§7] §c마피아와 접선했습니다.");
        }
        return true;
    }
}
