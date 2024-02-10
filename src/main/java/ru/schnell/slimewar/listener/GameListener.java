package ru.schnell.slimewar.listener;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jdbi.v3.core.async.JdbiExecutor;
import ru.schnell.slimewar.SlimeWarGame;
import ru.schnell.slimewar.configuration.EnchantConfig;
import ru.schnell.slimewar.configuration.GameConfig;
import ru.schnell.slimewar.configuration.SlimeConfig;
import ru.schnell.slimewar.database.SlimeWarPlayerDao;
import ru.schnell.slimewar.player.SWPlayer;
import ru.schnell.slimewar.player.SWPlayerManager;
import ru.schnell.util.RandomCollection;

import java.util.Comparator;


public class GameListener implements Listener {

    private Plugin plugin;
    private JdbiExecutor executor;
    private SWPlayerManager playerManager;
    private double upgradeChance;
    private RandomCollection<Enchantment> enchantments;
    private SlimeWarGame game;
    private double slimeDamagePerLvl;


    public GameListener(Plugin plugin, JdbiExecutor executor, SWPlayerManager playerManager, SlimeWarGame game, GameConfig gameConfig, EnchantConfig enchantConfig, SlimeConfig slimeConfig) {
        this.plugin = plugin;
        this.executor = executor;
        this.playerManager = playerManager;
        this.upgradeChance = gameConfig.getUpgradeChance();
        this.enchantments = enchantConfig.getEnchantments();
        this.game = game;
        this.slimeDamagePerLvl = slimeConfig.getDamagePerLvl();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        SWPlayer swPlayer = playerManager.register(e.getPlayer());
        swPlayer.createSWBoard(plugin, game);
        e.getPlayer().setScoreboard(swPlayer.getSwBoard().getScoreboard());

        game.checkStart();

        if (game.getState() == SlimeWarGame.GameState.WAITING || game.getState() == SlimeWarGame.GameState.STARTING) {
            e.getPlayer().teleport(game.getLobby().getSpawnLocation());
            swPlayer.setLiving(true);
        }else {
            e.getPlayer().teleport(game.getArena().getSpawnLocation());
        }

        game.getBossBar().addPlayer(e.getPlayer());
    }

    private void onLeave(Player player) {
        game.checkEnd();

        if (game.getState() == SlimeWarGame.GameState.WAITING) return;

        SWPlayer swPlayer = playerManager.getPlayer(player);
        swPlayer.setLiving(false);
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        onLeave(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        onLeave(e.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (game.getState() == SlimeWarGame.GameState.RUNNING) {
            SWPlayer swPlayer = playerManager.getPlayer(e.getEntity());
            swPlayer.setLiving(false);
            e.getEntity().setGameMode(GameMode.SPECTATOR);
            if (game.getWave() > swPlayer.getMaxWave()) swPlayer.setMaxWave(game.getWave());
            executor.useExtension(SlimeWarPlayerDao.class, dao -> {
                dao.addValue(e.getEntity().getUniqueId(), 0, 0, game.getWave());
            });
        }

        game.checkEnd();
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() == EntityType.SLIME && e.getDamager().getType() == EntityType.PLAYER){

            executor.useExtension(SlimeWarPlayerDao.class, dao -> {
                dao.addValue(e.getDamager().getUniqueId(), e.getDamage(), 0, 0);
            });
            SWPlayer swPlayer = playerManager.getPlayer(e.getDamager().getUniqueId());
            swPlayer.setDamage(swPlayer.getDamage() + e.getDamage());

            if (((Slime) e.getEntity()).getHealth() > e.getFinalDamage()) return;
            swPlayer.setKills(swPlayer.getKills() + 1);
            executor.useExtension(SlimeWarPlayerDao.class, dao -> {
                dao.addValue(e.getDamager().getUniqueId(), 0, 1, 0);
            });

            if (Math.random() * 100.0 > upgradeChance) return;

            Player player = (Player) e.getDamager();

            Pair<Integer, ItemStack> swordPair = firstSword(player);
            if (swordPair == null) return;

            ItemStack sword = swordPair.getValue();
            ItemMeta meta = sword.getItemMeta();
            Enchantment enchantment = enchantments.next();
            if (meta.hasEnchant(enchantment)) {
                int lvl = meta.getEnchantLevel(enchantment) + 1;
                meta.addEnchant(enchantment, lvl, true);
            }else {
                meta.addEnchant(enchantment, 1, true);
            }
            sword.setItemMeta(meta);

            player.getInventory().setItem(swordPair.getKey(), sword);
            swPlayer.incLvl();
            player.sendMessage("Уровень повышен");
        }

        if (e.getDamager().getType() == EntityType.SLIME) {

            SWPlayer highest = Bukkit.getOnlinePlayers().stream()
                            .map(player -> playerManager.getPlayer(player))
                            .filter(swPlayer -> swPlayer.isLiving())
                            .sorted(Comparator.comparing(SWPlayer::getLvl, Comparator.reverseOrder()))
                            .findFirst()
                            .orElse(null);

            double damage = highest == null ? 0 : slimeDamagePerLvl * highest.getLvl();
            e.setDamage(damage);
        }

    }

    private Pair<Integer, ItemStack> firstSword(Player player) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null || item.getType() == Material.AIR) continue;

            if (item.getType().name().contains("SWORD")) return Pair.of(i, item);
        }

        return null;
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

}
