package com.github.kimcore.mafia;

import com.connorlinfoot.titleapi.TitleAPI;
import com.github.kimcore.mafia.jobs.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MafiaSession {
    public MafiaCore Core;
    public List<Player> Players;
    private List<MafiaJob> MafiaTeam, Specials;
    private Map<Player, MafiaJob> PlayerJobs;
    public Map<Player, MafiaSidebar> Sidebars;
    private Map<Player, MafiaInv> PlayersInv;
    public Map<Player, Player> VoteMap;
    public int Yes, No;
    public boolean AllMafiaDead;
    public int currentDay, PHASE;
    public int[] E;

    public MafiaSession(MafiaCore core, List<Player> players) {
        Core = core;
        Players = new ArrayList<>(players);
        PlayerJobs = new HashMap<>();
        MafiaTeam = new ArrayList<>();
        Specials = new ArrayList<>();
        MafiaTeam.add(new Spy());
        MafiaTeam.add(new Beastman());
        MafiaTeam.add(new Madame());
        Specials.add(new Shaman());
        Specials.add(new Soldier());
        Specials.add(new Politician());
        Specials.add(new Reporter());
        Specials.add(new Gangster());
        Specials.add(new Detective());
        Specials.add(new Miner());
        Specials.add(new Terrorist());
    }

    public MafiaJob getJob(Player player) {
        return PlayerJobs.get(player);
    }

    public int getPHASE() {
        return PHASE;
    }

    public MafiaInv getInv(Player player) {
        return PlayersInv.get(player);
    }

    public void setPHASE(int PHASE) {
        this.PHASE = PHASE;
    }

    public void assignJobs() {
        List<Player> p = new ArrayList<>(Players);
        int MAFIA = 1, MAFIA_TEAM = 0, SPECIALS = 1, CITIZEN = 0;
        switch (p.size()) {
            case 4:
                break;
            case 5:
                SPECIALS = 2;
                break;
            case 6:
                MAFIA_TEAM = 1;
                SPECIALS = 2;
                break;
            case 7:
                MAFIA_TEAM = 1;
                SPECIALS = 3;
                break;
            case 8:
                MAFIA = 2;
                MAFIA_TEAM = 1;
                SPECIALS = 3;
                break;
            case 9:
                MAFIA = 2;
                MAFIA_TEAM = 1;
                SPECIALS = 3;
                CITIZEN = 1;
                break;
            case 10:
                MAFIA = 2;
                MAFIA_TEAM = 1;
                SPECIALS = 4;
                CITIZEN = 1;
                break;
            case 11:
                MAFIA = 3;
                MAFIA_TEAM = 1;
                SPECIALS = 4;
                CITIZEN = 1;
                break;
            case 12:
                MAFIA = 3;
                MAFIA_TEAM = 1;
                SPECIALS = 5;
                CITIZEN = 1;
                break;
        }
        Player player;
        MafiaJob job;
        for (int i = 0; i < MAFIA; i++) {
            player = p.get(new Random().nextInt(p.size()));
            job = new Mafia();
            PlayerJobs.put(player, job);
            p.remove(player);
        }
        if (MAFIA_TEAM == 1) {
            player = p.get(new Random().nextInt(p.size()));
            job = MafiaTeam.get(new Random().nextInt(MafiaTeam.size()));
            PlayerJobs.put(player, job);
            p.remove(player);
        }
        player = p.get(new Random().nextInt(p.size()));
        job = new Cop();
        PlayerJobs.put(player, job);
        p.remove(player);
        player = p.get(new Random().nextInt(p.size()));
        job = new Doctor();
        PlayerJobs.put(player, job);
        p.remove(player);
        for (int i = 0; i < SPECIALS; i++) {
            player = p.get(new Random().nextInt(p.size()));
            int index = new Random().nextInt(Specials.size());
            PlayerJobs.put(player, Specials.get(index));
            Specials.remove(index);
            p.remove(player);
        }
        if (CITIZEN == 1) {
            player = p.get(new Random().nextInt(p.size()));
            job = new Citizen();
            PlayerJobs.put(player, job);
            p.remove(player);
        }
    }

    public void sendMessage(String message) {
        for (Player p : Players) {
            p.sendMessage("§7[§c마피아 §b코어§7] " + message);
        }
    }

    public void showSidebar(Player player, int time, String desc1, String desc2) {
        MafiaSidebar Sidebar = Sidebars.get(player);
        Sidebar.clear();
        Sidebar.addEmpty();
        Sidebar.setStatus(Core.getStatus());
        Sidebar.addText("§e" + time + "초 남음");
        Sidebar.addText("§f당신의 직업은");
        Sidebar.addText(this.getJob(player).colored + " §f입니다.");
        Sidebar.addEmpty();
        Sidebar.addText(desc1);
        Sidebar.addText(desc2);
        Sidebar.showTo(player);
    }

    @SuppressWarnings("ConstantConditions")
    public void start() {
        this.assignJobs();
        ItemStack item = new ItemStack(Material.CLOCK, 1);
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName("§b마피아 게임 시계");
        item.setItemMeta(itemmeta);
        for (Player player : Players) {
            Core.savePlayerState(player);
            TitleAPI.sendTitle(player, 10, 200, 10,
                    "§e당신은 " + this.getJob(player).colored + " §e입니다", "");
            player.sendMessage("§7[§c마피아 §b코어§7] " + this.getJob(player).colored + " §e:");
            player.sendMessage("§7[§c마피아 §b코어§7] " + this.getJob(player).description);
            player.teleport(Core.gameLocation);
            player.getInventory().addItem(item);
        }
        this.sendMessage("§a게임이 시작됩니다.");
        Phase1();
    }

    public void end(int TEAM) {
        if (TEAM == MafiaJob.TEAM_MAFIA) {
            this.sendMessage("§c마피아팀이 승리했습니다!");
        } else {
            this.sendMessage("§b시민팀이 승리했습니다!");
        }
        for (Player player : Players) {
            if (TEAM == MafiaJob.TEAM_MAFIA) {
                TitleAPI.sendTitle(player, 10, 200, 10,
                        "§c마피아팀 승리!", "");
            } else {
                TitleAPI.sendTitle(player, 10, 200, 10,
                        "§b시민팀 승리!", "");
            }
            if (this.getJob(player).wasMiner) {
                this.sendMessage("§b" + player.getName() + "§e님은 "
                        + KoreanUtil.get(this.getJob(player).colored, "§e을", "§e를") + " 도굴한 도굴꾼이었습니다.");
            } else if (this.getJob(player).minedJob != null) {
                this.sendMessage("§b" + player.getName() + "§e님은 "
                        + KoreanUtil.get(this.getJob(player).minedJob.colored, "§e을", "§e를") + " 도굴당한 §b시민§e이었습니다.");
            } else {
                this.sendMessage("§b" + player.getName() + "§e님은 "
                        + KoreanUtil.get(this.getJob(player).colored, "§e이었", "§e였") + "습니다.");
            }
        }
        Core.clearGame();
    }

    public void markDead(Player target) {
        target.setHealth(0.0D);
        this.getJob(target).isDead = true;
        target.setAllowFlight(true);
        target.setFlying(true);
        target.hidePlayer(Core, target);
    }

    @SuppressWarnings("ConstantConditions")
    private void Phase1() {
        if (!Core.isGaming()) {
            Core.getServer().getScheduler().cancelTask(E[0]);
            return;
        }
        this.setPHASE(1);
        currentDay++;
        E = new int[]{0, 30, 0, 30, 20, 20};
        Sidebars = new HashMap<>();
        VoteMap = new HashMap<>();
        PlayersInv = new HashMap<>();
        AllMafiaDead = true;
        for (Player player : Players) {
            if (!this.getJob(player).isDead) {
                E[2] += 15;
            }
            PlayersInv.put(player, new MafiaInv());
            PlayersInv.get(player).ABILITY_POS1 = 1234;
            PlayersInv.get(player).ABILITY_POS2 = 1234;
            PlayersInv.get(player).VOTE_POS1 = 1234;
            PlayersInv.get(player).VOTE_POS2 = 1234;
            PlayersInv.get(player).timeUsed = false;
            Sidebars.put(player, new MafiaSidebar());
            this.getJob(player).abilityUsed = false;
            this.getJob(player).isSeduced = false;
            this.getJob(player).abilityTarget = null;
            if (this.getJob(player).id == MafiaJob.JOB_MAFIA) {
                AllMafiaDead = false;
            }
        }
        Core.getServer().getWorld("mafiaworld").setTime(15000);
        this.sendMessage("§e밤이 되었습니다.");
        Core.setStatus(currentDay + "번째 밤");
        E[0] = Core.getServer().getScheduler().scheduleSyncRepeatingTask(Core, () -> {
            if (!Core.isGaming()) {
                Core.getServer().getScheduler().cancelTask(E[0]);
                return;
            }
            for (Player player : Players) {
                showSidebar(player, E[1], "§b시계§e를 사용해서", "§e능력을 사용하세요");
                if (E[1] == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 10, 1);
                } else if (E[1] <= 5) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 10, 1);
                }
            }

            if (E[1] <= 0) {
                Core.getServer().getScheduler().cancelTask(E[0]);
                Phase2();
            } else {
                E[1]--;
            }

        }, 0, 20L);
    }

    @SuppressWarnings("ConstantConditions")
    public void Phase2() {
        if (!Core.isGaming()) {
            Core.getServer().getScheduler().cancelTask(E[0]);
            return;
        }
        this.setPHASE(2);
        this.sendMessage("§e낮이 밝았습니다.");
        Core.setStatus(currentDay + "번째 낮");
        Core.getServer().getWorld("mafiaworld").setTime(1000);
        for (Player player : Players) {
            MafiaJob job = this.getJob(player);
            if (job.id == MafiaJob.JOB_MAFIA && job.abilityTarget != null) {
                boolean HEALED = false;
                for (Player p : Players) {
                    if (this.getJob(p).id == MafiaJob.JOB_DOCTOR && this.getJob(p).abilityTarget == job.abilityTarget) {
                        HEALED = true;
                    }
                }
                if (HEALED) {
                    this.sendMessage("§b" + job.abilityTarget.getName() + "§e님이 마피아의 공격을 받았지만");
                    this.sendMessage("§a의사의 치료를 받고 살아났습니다!");
                } else {
                    if (this.getJob(job.abilityTarget).id == MafiaJob.JOB_TERRORIST && this.getJob(job.abilityTarget).abilityTarget == player) {
                        this.markDead(job.abilityTarget);
                        this.markDead(player);
                        this.sendMessage("§b" + job.abilityTarget.getName() + "§e님이 §c마피아§e인 §b" + player.getName() + "§e님과 §5함께 폭사했습니다!");
                    } else if (this.getJob(job.abilityTarget).id == MafiaJob.JOB_SOLDIER && !this.getJob(job.abilityTarget).isSeduced) {
                        this.getJob(job.abilityTarget).oneTimeUsed = true;
                        this.sendMessage("§b" + job.abilityTarget.getName() + "§e님이 §c마피아의 공격을 받았지만");
                        this.sendMessage("§a의사의 치료를 받고 살아났습니다!");
                    } else {
                        this.markDead(job.abilityTarget);
                        this.sendMessage("§b" + job.abilityTarget.getName() + "§e님이 §c마피아의 공격을 받아 사망했습니다.");
                        for (Player p : Players) {
                            if (p != job.abilityTarget && this.getJob(p).id == MafiaJob.JOB_MINER && this.currentDay == 1) {
                                p.sendMessage("§7[§c마피아 §b코어§7] §b" + job.abilityTarget.getName() + "§e님의 직업인 " +
                                        KoreanUtil.get(this.getJob(job.abilityTarget).colored, "§e을", "§e를") +
                                        " 도굴했습니다.");
                                this.PlayerJobs.put(p, this.getJob(job.abilityTarget));
                                this.PlayerJobs.put(job.abilityTarget, new Citizen());
                                this.getJob(job.abilityTarget).isDead = true;
                                this.getJob(job.abilityTarget).minedJob = this.getJob(p);
                                this.getJob(p).isDead = false;
                                this.getJob(p).wasMiner = true;
                            }
                        }
                    }
                }
            } else if (job.id == MafiaJob.JOB_BEASTMAN && job.abilityTarget != null && job.isContacted && this.AllMafiaDead) {
                this.markDead(job.abilityTarget);
                this.sendMessage("§b" + job.abilityTarget.getName() + "§e님이 §c짐승인간의 공격을 받아 사망했습니다.");
            } else if (job.id == MafiaJob.JOB_REPORTER && this.currentDay != 1 && job.abilityTarget != null && !job.oneTimeUsed) {
                job.oneTimeUsed = true;
                this.sendMessage("§e속보입니다!");
                this.sendMessage("§b" + job.abilityTarget.getName() + "§e님이 " + KoreanUtil.get(this.getJob(job.abilityTarget).colored, "§e이", "§e") +
                        "라는 소식입니다!");
            }
        }
        MafiaJob job;
        int MT = 0, CT = 0;
        for (Player player : Players) {
            job = this.getJob(player);
            if (job.isDead) continue;
//            if (job.isBlackmailed) continue;
            if (job.team == MafiaJob.TEAM_MAFIA) {
                if (job.type == MafiaJob.TYPE_MAFIA_TEAM && job.isContacted) MT += 1;
                else if (job.type == MafiaJob.TYPE_MAFIA) MT += 1;
            } else {
                if (job.id == MafiaJob.JOB_POLITICIAN) {
                    if (job.isSeduced) CT += 1;
                    else CT += 2;
                } else if (job.id == MafiaJob.JOB_GANGSTER && !job.abilityUsed) CT += 2;
                else {
                    CT += 1;
                }
            }
        }
        if (MT >= CT) {
            this.end(MafiaJob.TEAM_MAFIA);
        } else if (MT == 0) {
            this.end(MafiaJob.TEAM_CITIZEN);
        } else {
            E[0] = Core.getServer().getScheduler().scheduleSyncRepeatingTask(Core, () -> {
                if (!Core.isGaming()) {
                    Core.getServer().getScheduler().cancelTask(E[0]);
                    return;
                }
                for (Player player : Players) {
                    showSidebar(player, E[2], "§e상의를 통해", "§e처형할 사람을 정하세요");
                }
                if (E[2] <= 0) {
                    Core.getServer().getScheduler().cancelTask(E[0]);
                    Phase3();
                } else {
                    E[2]--;
                }
            }, 0, 20L);
        }
    }

    public void Phase3() {
        if (!Core.isGaming()) {
            Core.getServer().getScheduler().cancelTask(E[0]);
            return;
        }
        this.setPHASE(3);
        this.sendMessage("§e투표시간이 되었습니다.");
        E[0] = Core.getServer().getScheduler().scheduleSyncRepeatingTask(Core, () -> {
            if (!Core.isGaming()) {
                Core.getServer().getScheduler().cancelTask(E[0]);
                return;
            }
            for (Player player : Players) {
                showSidebar(player, E[3], "§b시계§e를 사용해서", "§e처형할 사람을 투표하세요");
                if (E[3] == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 10, 1);
                } else if (E[3] <= 5) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 10, 1);
                }
            }
            if (E[3] <= 0) {
                Core.getServer().getScheduler().cancelTask(E[0]);
                if (VoteMap.isEmpty()) {
                    Phase6();
                } else {
                    Map<Player, Integer> Votes = new HashMap<>();
                    Player target = null;
                    int max = 0, i = 0;
                    for (Player p : Players) {
                        Votes.put(p, 0);
                    }
                    for (Player p : VoteMap.keySet()) {
                        if (this.getJob(p).id == MafiaJob.JOB_POLITICIAN && !this.getJob(p).isSeduced) {
                            Votes.put(VoteMap.get(p), 1);
                        } else if (this.getJob(p).type == MafiaJob.TYPE_MAFIA_TEAM) {
                            if (this.getJob(VoteMap.get(p)).id == MafiaJob.JOB_MAFIA && this.getJob(p).id != MafiaJob.JOB_MAFIA) {
                                this.getJob(p).isContacted = true;
                                p.sendMessage("§7[§c마피아 §b코어§7] §c마피아와 접선했습니다.");
                            } else if (this.getJob(p).id == MafiaJob.JOB_MADAME) {
                                this.getJob(VoteMap.get(p)).isSeduced = true;
                                p.sendMessage("§7[§c마피아 §b코어§7] §b" + VoteMap.get(p).getName() + "§e님을 §c유혹했습니다.");
                            }
                        }
                    }
                    for (Player p : VoteMap.values()) {
                        Votes.put(p, Votes.get(p) + 1);
                    }
                    for (Player p : Votes.keySet()) {
                        i++;
                        if (Votes.get(p) > max) {
                            target = p;
                            max = Votes.get(p);
                        } else if (Votes.get(p) == max) {
                            if (i == Votes.size()) {
                                this.sendMessage("§c투표로 대상이 정해지지 않았습니다.");
                                Phase6();
                            }
                        }
                    }
                    Phase4(target);
                }
            } else {
                E[3]--;
            }
        }, 0, 20L);
    }

    public void Phase4(Player target) {
        if (!Core.isGaming()) {
            Core.getServer().getScheduler().cancelTask(E[0]);
            return;
        }
        this.setPHASE(4);
        this.sendMessage("§b" + target.getName() + "§e님의 최후의 반론");
        if (this.getJob(target).id == MafiaJob.JOB_TERRORIST) {
            PlayersInv.get(target).TerroristGUI(
                    "§c함께 폭사할 대상을 선택하세요",
                    Players,
                    this
            ).open(target);
        }
        E[0] = Core.getServer().getScheduler().scheduleSyncRepeatingTask(Core, () -> {
            if (!Core.isGaming()) {
                Core.getServer().getScheduler().cancelTask(E[0]);
                return;
            }
            for (Player player : Players) {
                showSidebar(player, E[4], "§b" + target.getName() + "§e님의", "§e최후의 반론");
            }
            if (E[4] <= 0) {
                Core.getServer().getScheduler().cancelTask(E[0]);
                Phase5(target);
            } else {
                E[4]--;
            }
        }, 0, 20L);
    }

    public void Phase5(Player target) {
        if (!Core.isGaming()) {
            Core.getServer().getScheduler().cancelTask(E[0]);
            return;
        }
        this.setPHASE(5);
        Yes = 0;
        No = 0;
        this.sendMessage("§b" + target.getName() + "§e님의 §c생사를 결정하세요");
        for (Player player : Players) {
            if (this.getJob(player).isBlackmailed) {
                No += 1;
                continue;
            } else if (this.getJob(player).isDead) {
                continue;
            }
            PlayersInv.get(player).ConfirmGUI("§b" + target.getName() + "§e님의 §c생사를 결정하세요", this).open(player);
        }
        E[0] = Core.getServer().getScheduler().scheduleSyncRepeatingTask(Core, () -> {
            if (!Core.isGaming()) {
                Core.getServer().getScheduler().cancelTask(E[0]);
                return;
            }
            for (Player player : Players) {
                showSidebar(player, E[5], "§b" + target.getName() + "§e님의", "§c생사를 결정하세요");
                if (E[5] == 1) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 10, 1);
                } else if (E[5] <= 5) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 10, 1);
                }
            }
            if (E[5] <= 0) {
                Core.getServer().getScheduler().cancelTask(E[0]);
                if (Yes > No || (Yes == 0 && No == 0)) {
                    this.sendMessage("§a살린다 " + Yes + "표 §c죽인다 " + No + "표로 §b" + target.getName() + "§e님은 §a처형당하지 않았습니다.");
                } else if (this.getJob(target).id == MafiaJob.JOB_POLITICIAN && !this.getJob(target).isSeduced) {
                    this.sendMessage("§a살린다 " + Yes + "표 §c죽인다 " + No + "표로 §b" + target.getName() + "§e님은 처형당해야 하지만");
                    this.sendMessage("§c정치인은 투표로 처형당하지 않습니다.");
                } else {
                    this.sendMessage("§a살린다 " + Yes + "표 §c죽인다 " + No + "표로 §b" + target.getName() + "§e님은 §c처형당했습니다.");
                    this.markDead(target);
                    if (this.getJob(target).id == MafiaJob.JOB_TERRORIST && this.getJob(target).abilityTarget != null && !this.getJob(target).isSeduced) {
                        this.markDead(this.getJob(target).abilityTarget);
                        this.sendMessage("§b" + target.getName() + "§e님이 §b" + this.getJob(target).abilityTarget.getName() + "§e님과 §5함께 폭사했습니다!");
                    }
                }
                Phase6();
            } else {
                E[5]--;
            }
        }, 0, 20L);
    }

    public void Phase6() {
        if (!Core.isGaming()) {
            Core.getServer().getScheduler().cancelTask(E[0]);
            return;
        }
        this.setPHASE(6);
        MafiaJob job;
        int MT = 0, CT = 0;
        for (Player player : Players) {
            job = this.getJob(player);
            if (job.isDead) continue;
//            if (job.isBlackmailed) continue;
            if (job.team == MafiaJob.TEAM_MAFIA) {
                if (job.type == MafiaJob.TYPE_MAFIA_TEAM && job.isContacted) MT += 1;
                else if (job.type == MafiaJob.TYPE_MAFIA) MT += 1;
            } else {
                if (job.id == MafiaJob.JOB_POLITICIAN) {
                    if (job.isSeduced) CT += 1;
                    else CT += 2;
                } else if (job.id == MafiaJob.JOB_GANGSTER && !job.abilityUsed) CT += 2;
                else {
                    CT += 1;
                }
            }
        }
        if (MT >= CT) {
            this.end(MafiaJob.TEAM_MAFIA);
        } else if (MT == 0) {
            this.end(MafiaJob.TEAM_CITIZEN);
        } else {
            Phase1();
        }
    }
}

