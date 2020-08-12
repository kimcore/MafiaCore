package com.github.kimcore.mafia;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;

import java.util.Collections;
import java.util.function.Consumer;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MafiaInv {
    private static InventoryManager Manager = new InventoryManager((JavaPlugin) Bukkit.getPluginManager().getPlugin("MafiaCore"));
    public int ABILITY_POS1, ABILITY_POS2, VOTE_POS1, VOTE_POS2;
    public boolean timeUsed = false;

    @SuppressWarnings("ConstantConditions")
    public SmartInventory AbilityGUI(String title, List<Player> players, MafiaSession Session) {
        Manager.init();
        return SmartInventory.builder()
                .manager(Manager)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents inventoryContents) {

                    }

                    @Override
                    public void update(Player player, InventoryContents inventoryContents) {
                        if (Session.getPHASE() != 1) inventoryContents.inventory().close(player);
                        if (Session.getJob(player).isSeduced) {
                            player.sendMessage("§7[§c마피아 §b코어§7] §c마담에게 유혹당해 능력을 사용할 수 없습니다.");
                            inventoryContents.inventory().close(player);
                        } else if (Session.getJob(player).abilityUsed && !Session.getJob(player).changeable) {
                            player.sendMessage("§7[§c마피아 §b코어§7] §c이미 능력을 사용하셨습니다.");
                            inventoryContents.inventory().close(player);
                        }
                        int i = 0;
                        ItemStack BLACK = new ItemStack(Material.GRAY_WOOL, 1);
                        ItemMeta meta = BLACK.getItemMeta();
                        meta.setDisplayName("§a선택함");
                        BLACK.setItemMeta(meta);
                        if (ABILITY_POS1 != 1234 && ABILITY_POS2 != 1234) {
                            inventoryContents.set(ABILITY_POS1, ABILITY_POS2, ClickableItem.empty(BLACK));
                        }
                        for (Player p : players) {
                            if ((Session.getJob(player).id == MafiaJob.JOB_SHAMAN) && !Session.getJob(p).isDead) {
                                continue;
                            } else if (Session.getJob(p).isDead) {
                                continue;
                            }
                            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                            SkullMeta sm = (SkullMeta) skull.getItemMeta();
                            sm.setOwningPlayer(p);
                            sm.setDisplayName("§e" + p.getName());
                            skull.setItemMeta(sm);
                            Consumer<InventoryClickEvent> SkullEvent = e -> {
                                if (!e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) return;
                                Player target = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", ""));
                                if (Session.getJob(player).id == MafiaJob.JOB_COP) {
                                    inventoryContents.inventory().close(player);
                                }
                                if (Session.getJob(player).useAbility(player, target, Session, inventoryContents)) {
                                    if (Session.getJob(player).id == MafiaJob.JOB_DETECTIVE) {
                                        inventoryContents.inventory().close(player);
                                        Session.getInv(target).DetectiveGUI(
                                                "§b" + Session.getJob(player).abilityTarget.getName()
                                                        + "§e님의 능력 사용을 조사합니다",
                                                Session.Players,
                                                Session).open(player);
                                    }
                                    if (e.getSlot() < 9) ABILITY_POS1 = 1;
                                    else ABILITY_POS1 = 3;
                                    ABILITY_POS2 = e.getSlot() % 9;
                                    if (e.getClickedInventory().contains(BLACK)) {
                                        e.getClickedInventory().remove(BLACK);
                                    }
                                    inventoryContents.set(ABILITY_POS1, ABILITY_POS2, ClickableItem.empty(BLACK));
                                }
                            };
                            if (i >= 9) {
                                inventoryContents.set(2, i - 9, ClickableItem.of(skull, SkullEvent));
                            } else {
                                inventoryContents.set(0, i, ClickableItem.of(skull, SkullEvent));
                            }
                            i++;
                        }
                    }
                })
                .size(4, 9)
                .title(title)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    public SmartInventory DetectiveGUI(String title, List<Player> players, MafiaSession Session) {
        Manager.init();
        return SmartInventory.builder()
                .manager(Manager)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents inventoryContents) {

                    }

                    @Override
                    public void update(Player player, InventoryContents inventoryContents) {
                        if (Session.getPHASE() != 1) inventoryContents.inventory().close(player);
                        int i = 0;
                        ItemStack BLACK = new ItemStack(Material.GRAY_WOOL, 1);
                        ItemMeta meta = BLACK.getItemMeta();
                        meta.setDisplayName("§a" + player.getName() + "님이 선택함");
                        BLACK.setItemMeta(meta);
                        if (ABILITY_POS1 != 1234 && ABILITY_POS2 != 1234) {
                            inventoryContents.set(ABILITY_POS1, ABILITY_POS2, ClickableItem.empty(BLACK));
                        }
                        for (Player p : players) {
                            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                            SkullMeta smeta = (SkullMeta) skull.getItemMeta();
                            smeta.setOwningPlayer(p);
                            smeta.setDisplayName("§e" + p.getName());
                            skull.setItemMeta(smeta);
                            if (i >= 9) {
                                inventoryContents.set(2, i - 9, ClickableItem.empty(skull));
                            } else {
                                inventoryContents.set(0, i, ClickableItem.empty(skull));
                            }
                            i++;
                        }
                    }
                })
                .size(4, 9)
                .title(title)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    public SmartInventory TerroristGUI(String title, List<Player> players, MafiaSession Session) {
        Manager.init();
        return SmartInventory.builder()
                .manager(Manager)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents inventoryContents) {

                    }

                    @Override
                    public void update(Player player, InventoryContents inventoryContents) {
                        if (Session.getPHASE() != 1) inventoryContents.inventory().close(player);
                        int i = 0;
                        ItemStack BLACK = new ItemStack(Material.GRAY_WOOL, 1);
                        ItemMeta meta = BLACK.getItemMeta();
                        meta.setDisplayName("§a선택함");
                        BLACK.setItemMeta(meta);
                        if (ABILITY_POS1 != 1234 && ABILITY_POS2 != 1234) {
                            inventoryContents.set(ABILITY_POS1, ABILITY_POS2, ClickableItem.empty(BLACK));
                        }
                        for (Player p : players) {
                            if ((Session.getJob(player).id == MafiaJob.JOB_SHAMAN) && !Session.getJob(p).isDead)
                                continue;
                            if (player == p) continue;
                            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                            SkullMeta sm = (SkullMeta) skull.getItemMeta();
                            sm.setOwningPlayer(p);
                            sm.setDisplayName("§e" + p.getName());
                            skull.setItemMeta(sm);
                            Consumer<InventoryClickEvent> SkullEvent = e -> {
                                if (!e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) return;
                                Player target = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", ""));
                                if (Session.getJob(player).useAbility(player, target, Session, inventoryContents)) {
                                    if (e.getSlot() < 9) ABILITY_POS1 = 1;
                                    else ABILITY_POS1 = 3;
                                    ABILITY_POS2 = e.getSlot() % 9;
                                    if (e.getClickedInventory().contains(BLACK)) {
                                        e.getClickedInventory().remove(BLACK);
                                    }
                                    inventoryContents.set(ABILITY_POS1, ABILITY_POS2, ClickableItem.empty(BLACK));
                                }
                            };
                            if (i >= 9) {
                                inventoryContents.set(2, i - 9, ClickableItem.of(skull, SkullEvent));
                            } else {
                                inventoryContents.set(0, i, ClickableItem.of(skull, SkullEvent));
                            }
                            i++;
                        }
                    }
                })
                .size(4, 9)
                .title(title)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    public SmartInventory TeleportGUI(String title, List<Player> players, MafiaSession Session) {
        Manager.init();
        return SmartInventory.builder()
                .manager(Manager)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents inventoryContents) {

                    }

                    @Override
                    public void update(Player player, InventoryContents inventoryContents) {
                        int i = 0;
                        for (Player p : players) {
                            if (Session.getJob(p).isDead || player == p) continue;
                            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                            SkullMeta sm = (SkullMeta) skull.getItemMeta();
                            sm.setOwningPlayer(p);
                            sm.setDisplayName("§e" + p.getName());
                            skull.setItemMeta(sm);
                            Consumer<InventoryClickEvent> SkullEvent = e -> {
                                if (!e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) return;
                                Player target = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", ""));
                                player.teleport(target.getLocation());
                            };
                            if (i >= 9) {
                                inventoryContents.set(2, i - 9, ClickableItem.of(skull, SkullEvent));
                            } else {
                                inventoryContents.set(0, i, ClickableItem.of(skull, SkullEvent));
                            }
                            i++;
                        }
                    }
                })
                .size(4, 9)
                .title(title)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    public SmartInventory TimeGUI(String title, List<Player> players, MafiaSession Session) {
        Manager.init();
        return SmartInventory.builder()
                .manager(Manager)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents inventoryContents) {
                        if (timeUsed) {
                            player.sendMessage("§7[§c마피아 §b코어§7] §c이미 사용하셨습니다.");
                            inventoryContents.inventory().close(player);
                        }
                        ItemStack EXTEND = new ItemStack(Material.LIME_WOOL, 1);
                        ItemMeta meta = EXTEND.getItemMeta();
                        meta.setDisplayName("§a시간 증가");
                        EXTEND.setItemMeta(meta);
                        ItemStack REDUCE = new ItemStack(Material.RED_WOOL, 1);
                        meta = REDUCE.getItemMeta();
                        meta.setDisplayName("§c시간 단축");
                        REDUCE.setItemMeta(meta);
                        Consumer<InventoryClickEvent> TimeEvent = e -> {
                            inventoryContents.inventory().close(player);
                            if (timeUsed) {
                                player.sendMessage("§7[§c마피아 §b코어§7] §c이미 사용하셨습니다.");
                            } else {
                                if (e.getCurrentItem().equals(EXTEND)) {
                                    Session.E[2] += 15;
                                    Session.sendMessage("§b" + player.getName() + "§e님이 §a시간을 증가하셨습니다.");
                                } else if (e.getCurrentItem().equals(REDUCE)) {
                                    Session.E[2] -= 15;
                                    Session.sendMessage("§b" + player.getName() + "§e님이 §c시간을 단축하셨습니다.");
                                }
                                timeUsed = true;
                            }
                        };
                        inventoryContents.set(0, 0, ClickableItem.of(EXTEND, TimeEvent));
                        inventoryContents.set(0, 1, ClickableItem.of(REDUCE, TimeEvent));
                    }

                    @Override
                    public void update(Player player, InventoryContents inventoryContents) {
                        if (Session.getPHASE() != 2) inventoryContents.inventory().close(player);
                    }
                })
                .size(1, 9)
                .title(title)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    public SmartInventory VoteGUI(String title, List<Player> players, MafiaSession Session) {
        Manager.init();
        return SmartInventory.builder()
                .manager(Manager)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents inventoryContents) {

                    }

                    @Override
                    public void update(Player player, InventoryContents inventoryContents) {
                        if (Session.getPHASE() != 3) inventoryContents.inventory().close(player);
                        int i = 0;
                        for (Player p : players) {
                            if (Session.getJob(p).isDead) continue;
                            int count = Collections.frequency(Session.VoteMap.values(), p);
                            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                            SkullMeta smeta = (SkullMeta) skull.getItemMeta();
                            smeta.setOwningPlayer(p);
                            smeta.setDisplayName("§e" + p.getName());
                            skull.setItemMeta(smeta);
                            ItemStack VOTED = new ItemStack(Material.LIGHT_BLUE_WOOL, count);
                            ItemMeta meta = VOTED.getItemMeta();
                            meta.setDisplayName("§b" + count + "표");
                            VOTED.setItemMeta(meta);
                            Consumer<InventoryClickEvent> VoteEvent = e -> {
                                if (!e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) return;
                                Player target = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", ""));
                                if (e.getSlot() < 9) VOTE_POS1 = 1;
                                else VOTE_POS1 = 3;
                                VOTE_POS2 = e.getSlot() % 9;
                                inventoryContents.set(VOTE_POS1, VOTE_POS2, ClickableItem.empty(VOTED));
                                Session.VoteMap.remove(player);
                                Session.VoteMap.put(player, target);
                            };
                            if (i >= 9) {
                                inventoryContents.set(3, i - 9, ClickableItem.of(VOTED, VoteEvent));
                            } else {
                                inventoryContents.set(1, i, ClickableItem.of(VOTED, VoteEvent));
                            }
                            if (i >= 9) {
                                inventoryContents.set(2, i - 9, ClickableItem.of(skull, VoteEvent));
                            } else {
                                inventoryContents.set(0, i, ClickableItem.of(skull, VoteEvent));
                            }
                            i++;
                        }
                    }
                })
                .size(4, 9)
                .title(title)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    public SmartInventory ConfirmGUI(String title, MafiaSession Session) {
        Manager.init();
        return SmartInventory.builder()
                .manager(Manager)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents inventoryContents) {
                        ItemStack YES = new ItemStack(Material.LIME_WOOL, 1);
                        ItemMeta meta = YES.getItemMeta();
                        meta.setDisplayName("§a살린다");
                        YES.setItemMeta(meta);
                        ItemStack NO = new ItemStack(Material.RED_WOOL, 1);
                        meta = NO.getItemMeta();
                        meta.setDisplayName("§c죽인다");
                        NO.setItemMeta(meta);
                        Consumer<InventoryClickEvent> ConfirmEvent = e -> {
                            if (e.getCurrentItem().equals(YES)) {
                                Session.Yes += 1;
                            } else if (e.getCurrentItem().equals(NO)) {
                                Session.No += 1;
                            }
                            inventoryContents.inventory().setCloseable(true);
                            inventoryContents.inventory().close(player);
                        };
                        inventoryContents.set(0, 0, ClickableItem.of(YES, ConfirmEvent));
                        inventoryContents.set(0, 1, ClickableItem.of(NO, ConfirmEvent));
                    }

                    @Override
                    public void update(Player player, InventoryContents inventoryContents) {

                    }
                })
                .size(1, 9)
                .title(title)
                .build();
    }
}