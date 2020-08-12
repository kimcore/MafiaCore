package com.github.kimcore.mafia;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MafiaCore extends JavaPlugin implements Listener {
    public MafiaSidebar Sidebar;
    public List<Player> Players;
    public Location gameLocation;
    private MafiaSession Session;
    private String MafiaStatus;
    private boolean isGaming = false, enableTPBlock = false;

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        Sidebar = new MafiaSidebar();
        Players = new ArrayList<>();
        boolean hasMafiaWorld = false;
        for (World world : Bukkit.getWorlds()) if (world.getName().equals("mafiaworld")) hasMafiaWorld = true;
        if (!hasMafiaWorld) {
            this.sendMessage("§a플러그인 설치 후 처음 1회 마피아 게임용 월드를 생성합니다.");
            this.sendMessage("§a잠시 서버렉이 있을 수 있습니다.");
            buildWorld();
            this.sendMessage("§a마피아 월드 생성 완료.");
        }
        this.setStatus("시작 안됨");
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("마피아").setExecutor(new MafiaCmd(this));
        Bukkit.getConsoleSender().sendMessage("§7[§c마피아 §b코어§7] §a플러그인이 활성화 되었습니다.");
        if (getDataFolder().mkdirs()) {
            Bukkit.getConsoleSender().sendMessage("§7[§c마피아 §b코어§7] §a플러그인 데이터 폴더를 생성했습니다.");
        }
        World MafiaWorld = Bukkit.getWorld("mafiaworld");
        MafiaWorld.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        MafiaWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        MafiaWorld.setGameRule(GameRule.KEEP_INVENTORY, true);
        MafiaWorld.setDifficulty(Difficulty.PEACEFUL);
        MafiaWorld.setPVP(false);
        MafiaWorld.setTime(0L);
        gameLocation = new Location(MafiaWorld, -300, 64, -148);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§7[§c마피아 §b코어§7] §c플러그인이 비활성화 되었습니다.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.getStatus().equals("준비 시간")) {
            Sidebar.showToEveryone();
            player.sendMessage("§7[§c마피아 §b코어§7] §a현재 준비중인 마피아 게임이 있습니다.");
            player.sendMessage("§7[§c마피아 §b코어§7] §a'/마피아 참가' 를 사용해서 참가하세요.");
            Sidebar.showToEveryone();
        }
        UUID uuid = player.getUniqueId();
        File InventoryFile = new File(getDataFolder(), "inventory/" + uuid + ".yml");
        if (InventoryFile.exists()) {
            restorePlayerState(player);
            sendMessage("§b" + player.getName() + "§e님의 상태를 복구했습니다.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.getStatus().equals("준비 시간") && Players.contains(player)) {
            Players.remove(player);
            this.sendMessage(
                    "§e" + player.getName() + "§c 님이 게임 준비중 떠나셨습니다.");
            Sidebar.clear();
            Sidebar.addEmpty();
            Sidebar.setStatus(this.getStatus());
            Sidebar.addEmpty();
            Sidebar.addText("§e현재 참여 인원 :");
            Sidebar.addText("§a" + Players.size() + " 명");
            if (Players.size() >= 4) {
                Sidebar.addText("§a 최소 인원이 충족되었습니다.");
                Sidebar.addText("§a'/마피아 참가' 를 사용해서 참가하거나");
                Sidebar.addText("§a'/마피아 시작' 을 사용해서 시작하세요");
            } else {
                Sidebar.addEmpty();
                Sidebar.addText("§a'/마피아 참가' 를 사용해서 참가하세요");
                Sidebar.addEmpty();
            }
            Sidebar.showToEveryone();
        } else if (this.isGaming() && Players.contains(player)) {
            Players.remove(player);
            this.sendMessage(
                    "§e" + player.getName() + "§c 님이 게임중 떠나셨습니다.");
            clearGame();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("mafiaworld")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("mafiaworld")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (this.isGaming() && Players.contains(player)) {
            event.setCancelled(true);
            if (Session.getPHASE() == 1) {
                if (Session.getJob(player).isDead) {
                    if (Session.getJob(player).isBlessed) {
                        player.sendMessage("§7[§c마피아 §b코어§7] §c성불당한 플레이어는 채팅을 할 수 없습니다!");
                        return;
                    }
                    for (Player p : Players) {
                        if ((Session.getJob(p).isDead && !Session.getJob(p).isBlessed) || Session.getJob(p).id == MafiaJob.JOB_SHAMAN) {
                            p.sendMessage("§7[§4사망한 플레이어§7] §e" + player.getName() + " : §f" + message);
                        }
                    }
                } else if (Session.getJob(player).team == MafiaJob.TEAM_MAFIA) {
                    for (Player p : Players) {
                        if ((Session.getJob(p).team == MafiaJob.TEAM_MAFIA && Session.getJob(player).isContacted && Session.getJob(p).isContacted) ||
                                Session.getJob(p).isDead) {
                            p.sendMessage("§7[§c마피아팀§7] §e" + player.getName() + " : §f" + message);
                        }
                    }
                } else if (Session.getJob(player).id == MafiaJob.JOB_SHAMAN) {
                    player.sendMessage("§7[§d영매§7] §e" + player.getName() + " : §f" + message);
                    for (Player p : Players) {
                        if (Session.getJob(p).isDead) {
                            p.sendMessage("§7[§d영매§7] §e" + player.getName() + " : §f" + message);
                        }
                    }
                }
            } else {
                if (Session.getJob(player).isDead) {
                    if (Session.getJob(player).isBlessed) {
                        player.sendMessage("§7[§c마피아 §b코어§7] §c성불당한 플레이어는 채팅을 할 수 없습니다!");
                    } else {
                        for (Player p : Players) {
                            if (Session.getJob(p).isDead || Session.getJob(p).id == MafiaJob.JOB_SHAMAN) {
                                p.sendMessage("§7[§4사망한 플레이어§7] §e" + player.getName() + " : §f" + message);
                            }
                        }
                    }

                } else {
                    for (Player p : Players) {
                        p.sendMessage("§7[§b채팅§7] §e" + player.getName() + " : §f" + message);
                    }
                }
            }
        }
    }

    @EventHandler
    @SuppressWarnings("ConstantConditions")
    public void onPlayerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.isGaming() && Players.contains(player)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
                ItemStack item = new ItemStack(Material.CLOCK);
                ItemMeta itemmeta = item.getItemMeta();
                itemmeta.setDisplayName("§b마피아 게임 시계");
                item.setItemMeta(itemmeta);
                if (player.getInventory().getItemInMainHand().equals(item)) {
                    if (Session.getJob(player).isDead) {
                        Session.getInv(player).TeleportGUI(
                                "§e텔레포트할 플레이어를 선택하세요",
                                Players,
                                Session
                        ).open(player);
                        return;
                    }
                    MafiaJob job = Session.getJob(player);
                    switch (Session.getPHASE()) {
                        case 1:
                            if (job.id == MafiaJob.JOB_DETECTIVE && job.abilityUsed) {
                                Session.getInv(job.abilityTarget).DetectiveGUI(
                                        "§b" + job.abilityTarget.getName()
                                                + "§e님의 능력 사용을 조사합니다",
                                        Players,
                                        Session
                                ).open(player);
                                break;
                            } else if (job.id == MafiaJob.JOB_BEASTMAN && !job.isContacted && !Session.AllMafiaDead) {
                                player.sendMessage("§7[§c마피아 §b코어§7] §c접선 후 모든 마피아가 죽어야 사용할 수 있습니다.");
                                break;
                            } else if (job.id == MafiaJob.JOB_CITIZEN || job.id == MafiaJob.JOB_MADAME ||
                                    job.id == MafiaJob.JOB_MINER || job.id == MafiaJob.JOB_SOLDIER) {
                                player.sendMessage("§7[§c마피아 §b코어§7] " + KoreanUtil.get(job.colored, "§e은", "§e는") +
                                        " 사용할 수 있는 능력이 없습니다.");
                                break;
                            } else if (job.id == MafiaJob.JOB_SHAMAN) {
                                boolean deadPresent = false;
                                for (Player p : Players) {
                                    if (Session.getJob(p).isDead) {
                                        deadPresent = true;
                                    }
                                }
                                if (!deadPresent) {
                                    player.sendMessage("§7[§c마피아 §b코어§7] §c사망한 플레이어가 없습니다.");
                                    break;
                                }
                            }
                            Session.getInv(player).AbilityGUI(
                                    Session.getJob(player).GUITitle,
                                    Players,
                                    Session
                            ).open(player);
                            break;
                        case 2:
                            Session.getInv(player).TimeGUI(
                                    "§e시간을 §a증가§e하거나 §c단축§e하세요",
                                    Players,
                                    Session
                            ).open(player);
                            break;
                        case 3:
                            if (job.isBlackmailed) {
                                player.sendMessage("§7[§c마피아 §b코어§7] §c협박당한 플레이어는 투표를 할 수 없습니다!");
                            } else {
                                Session.getInv(player).VoteGUI(
                                        "§b투표할 사람을 선택하세요",
                                        Players,
                                        Session
                                ).open(player);
                                break;
                            }
                        default:
                            player.sendMessage("§7[§c마피아 §b코어§7] §c지금은 사용할 수 없습니다!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (this.enableTPBlock && Players.contains(event.getPlayer())) {
            event.setRespawnLocation(gameLocation);
        }
    }

    @EventHandler
    public void onTPWorld(PlayerChangedWorldEvent event) {
        if (enableTPBlock && event.getFrom().getName().equals("mafiaworld")) {
            event.getPlayer().teleport(gameLocation);
            event.getPlayer().sendMessage("§7[§c마피아 §b코어§7] §c어 딜 도 망 가 ?");
        }
    }

    @SuppressWarnings("all")
    private void buildWorld() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equals("mafiaworld")) Bukkit.unloadWorld(world, false);
            new File("mafiaworld").delete();
        }
        String FILE_URL = "https://cdn.haru.im/MafiaCore/world.zip";
        try (BufferedInputStream in = new BufferedInputStream(new URL(FILE_URL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("world.zip")) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException ignored) {
        }
        try {
            new ZipFile("world.zip").extractAll("./");
        } catch (ZipException e) {
            e.printStackTrace();
        }
        new File("world.zip").delete();
        WorldCreator wc = new WorldCreator("mafiaworld");
        wc.createWorld();
        Bukkit.createWorld(wc);
        World MafiaWorld = Bukkit.getWorld("mafiaworld");
        MafiaWorld.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        MafiaWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        MafiaWorld.setGameRule(GameRule.KEEP_INVENTORY, true);
        MafiaWorld.setDifficulty(Difficulty.PEACEFUL);
        MafiaWorld.setPVP(false);
        MafiaWorld.setTime(0L);
        gameLocation = new Location(MafiaWorld, -300, 64, -148);
    }

    public void sendMessage(String message) {
        Bukkit.getServer().broadcastMessage("§7[§c마피아 §b코어§7] " + message);
    }

    public void sendMessageRaw(String message) {
        Bukkit.getServer().broadcastMessage(message);
    }

    public void setStatus(String NewStatus) {
        MafiaStatus = NewStatus;
    }

    public String getStatus() {
        return MafiaStatus;
    }

    public boolean isGaming() {
        return isGaming;
    }

    public void savePlayerState(Player player) {
        UUID uuid = player.getUniqueId();
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        if (new File(getDataFolder(), "inventory").mkdirs()) {
            Bukkit.getConsoleSender().sendMessage("§7[§c마피아 §b코어§7] §a인벤토리 폴더를 생성했습니다.");
        }
        File ItemsFile = new File(getDataFolder(), "inventory/" + uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(ItemsFile);
        config.set("items", contents);
        config.set("armors", armorContents);
        config.set("location", player.getLocation());
        config.set("exp", player.getExp());
        config.set("respawn", player.getBedSpawnLocation());
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        try {
            config.save(ItemsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public void restorePlayerState(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            File InventoryFile = new File(getDataFolder(), "inventory/" + uuid + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(InventoryFile);
            List<ItemStack> content = (List<ItemStack>) config.get("items");
            List<ItemStack> armor = (List<ItemStack>) config.get("armors");
            ItemStack[] contents = content.toArray(new ItemStack[0]);
            ItemStack[] armorContents = armor.toArray(new ItemStack[0]);
            Location location = (Location) config.get("location");
            Location spawn = (Location) config.get("respawn");
            float exp = player.getExp();
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.getInventory().setContents(contents);
            player.getInventory().setArmorContents(armorContents);
            player.teleport(location);
            player.setExp(exp);
            player.setBedSpawnLocation(spawn);
            InventoryFile.delete();
        } catch (NullPointerException ignored) {
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void readyGame(String player) {
        if (this.getStatus().equals("준비 시간")) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c이미 준비 시간입니다.");
        } else if (this.isGaming()) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c현재 진행중인 게임이 있습니다.");
        } else {
            this.setStatus("준비 시간");
            Sidebar.clear();
            Sidebar.addEmpty();
            Sidebar.setStatus(this.getStatus());
            Sidebar.addEmpty();
            Sidebar.addText("§e현재 참여 인원 :");
            Sidebar.addText("§a0 명");
            Sidebar.addEmpty();
            Sidebar.addText("§a'/마피아 참가' 를 사용해서 참가하세요");
            Sidebar.addEmpty();
            Sidebar.showToEveryone();
            this.sendMessage("§b" + player + "§e님이 §c마피아 게임§c을 시작하셨습니다.");
            this.sendMessage("§a'/마피아 참가' 를 사용해서 참가하세요.");
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void joinGame(String player) {
        if (this.isGaming()) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c이미 게임이 시작되었습니다.");
            return;
        }
        if (!this.getStatus().equals("준비 시간")) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c먼저 §a'/마피아 준비' §c명령어를 사용해서 준비를 해주세요.");
            return;
        }
        if (Players.contains(Bukkit.getPlayer(player))) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c이미 게임에 참가하셨습니다.");
            return;
        }
        if (Players.size() == 12) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c인원이 꽉차서 참가할 수 없습니다.");
            return;
        }
        this.sendMessage("§b" + player + "§e 님이 마피아 게임에 §a참가하셨습니다.");
        Sidebar.clear();
        Players.add(Bukkit.getPlayer(player));
        Sidebar.addEmpty();
        Sidebar.setStatus(this.getStatus());
        Sidebar.addEmpty();
        Sidebar.addText("§e현재 참여 인원 :");
        Sidebar.addText("§a" + Players.size() + " 명");
        if (Players.size() == 12) {
            Sidebar.addText("§a 최대 인원이 충족되었습니다.");
            Sidebar.addEmpty();
            Sidebar.addText("§a'/마피아 시작' 을 사용해서 시작하세요");
        } else if (Players.size() >= 4) {
            Sidebar.addText("§a 최소 인원이 충족되었습니다.");
            Sidebar.addText("§a'/마피아 참가' 를 사용해서 참가하거나");
            Sidebar.addText("§a'/마피아 시작' 을 사용해서 시작하세요");
        } else {
            Sidebar.addEmpty();
            Sidebar.addText("§a'/마피아 참가' 를 사용해서 참가하세요");
            Sidebar.addEmpty();
        }
        Sidebar.showToEveryone();
    }

    @SuppressWarnings("ConstantConditions")
    public void quitGame(String player) {
        if (this.isGaming()) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c이미 게임이 시작되었습니다.");
            return;
        }
        if (!this.getStatus().equals("준비 시간")) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c먼저 §a'/마피아 준비' §c명령어를 사용해서 준비를 해주세요.");
            return;
        }
        if (!Players.contains(Bukkit.getPlayer(player))) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c게임에 참가하지 않으셨습니다.");
            return;
        }
        Players.remove(Bukkit.getPlayer(player));
        this.sendMessage("§b" + player + "§e 님이 마피아 게임에서 §c퇴장하셨습니다.");
        Sidebar.clear();
        Sidebar.addEmpty();
        Sidebar.setStatus(this.getStatus());
        Sidebar.addEmpty();
        Sidebar.addText("§e현재 참여 인원 :");
        Sidebar.addText("§a" + Players.size() + " 명");
        if (Players.size() == 12) {
            Sidebar.addText("§a 최대 인원이 충족되었습니다.");
            Sidebar.addEmpty();
            Sidebar.addText("§a'/마피아 시작' 을 사용해서 시작하세요");
        } else if (Players.size() >= 4) {
            Sidebar.addText("§a 최소 인원이 충족되었습니다.");
            Sidebar.addText("§a'/마피아 참가' 를 사용해서 참가하거나");
            Sidebar.addText("§a'/마피아 시작' 을 사용해서 시작하세요");
        } else {
            Sidebar.addEmpty();
            Sidebar.addText("§a'/마피아 참가' 를 사용해서 참가하세요");
            Sidebar.addEmpty();
        }
        Sidebar.showToEveryone();
    }

    @SuppressWarnings("ConstantConditions")
    public void startGame(String player) {
        if (this.isGaming()) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c이미 진행중인 게임이 있습니다.");
            return;
        }
        if (this.getStatus().equals("시작 안됨")) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c먼저 §a'/마피아 준비' §c명령어를 사용해서 준비를 해주세요.");
            return;
        }
        if (Players.size() < 4) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c최소 인원 (4명)이 채워지지 않아 게임을 시작할 수 없습니다.");
            return;
        }
        this.isGaming = true;
        this.enableTPBlock = true;
        Sidebar.clear();
        Session = new MafiaSession(this, Players);
        Session.start();
    }

    @SuppressWarnings("ConstantConditions")
    public void cancelGame(String player) {
        if (this.getStatus().equals("시작 안됨")) {
            Bukkit.getPlayer(player).sendMessage("§7[§c마피아 §b코어§7] §c진행중인 게임이 없습니다.");
        } else {
            clearGame();
            this.sendMessage("§b" + player + "§e님이 §c마피아 게임을 취소하셨습니다.");

        }
    }

    public void clearGame() {
        this.setStatus("시작 안됨");
        this.getServer().getScheduler().cancelTasks(this);
        this.enableTPBlock = false;
        if (this.isGaming()) {
            for (Player player : Players) {
                restorePlayerState(player);
                Session.Sidebars.get(player).clear();
                Session.Sidebars.get(player).showTo(player);
                if (Session.getJob(player).isDead) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.showPlayer(this, player);
                }
            }
            this.Session.Sidebars = new HashMap<>();
            this.isGaming = false;
        }
        this.Players = new ArrayList<>();
        this.Session = null;
        this.Sidebar.clear();
        this.Sidebar.showToEveryone();
    }
}