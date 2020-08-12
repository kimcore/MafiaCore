package com.github.kimcore.mafia;

import com.coloredcarrot.api.sidebar.Sidebar;
import com.coloredcarrot.api.sidebar.SidebarString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class MafiaSidebar extends Sidebar {
    public MafiaSidebar() {
        super("§7======== §c마피아 §b코어 §7========", Bukkit.getPluginManager().getPlugin("MafiaCore"), 10);
    }

    public void clear() {
        List<SidebarString> empty = this.getEntries();
        empty.clear();
        this.setEntries(empty);
    }

    public void showTo(String player) {
        this.showTo(Bukkit.getPlayer(player));
    }

    void showToEveryone() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.showTo(player);
        }
    }

    void setStatus(String status) {
        this.addEntry(new SidebarString("§6" + status));
    }

    void addText(String text) {
        this.addEntry(new SidebarString(text));
    }

    void removeText(String text) {
        this.removeEntry(new SidebarString(text));
    }
}
