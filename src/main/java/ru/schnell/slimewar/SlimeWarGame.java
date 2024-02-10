package ru.schnell.slimewar;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.schnell.slimewar.area.Arena;
import ru.schnell.slimewar.area.Lobby;
import ru.schnell.slimewar.configuration.ArenaConfig;
import ru.schnell.slimewar.configuration.GameConfig;
import ru.schnell.slimewar.configuration.LobbyConfig;
import ru.schnell.slimewar.configuration.SlimeConfig;
import ru.schnell.slimewar.player.SWPlayer;
import ru.schnell.slimewar.player.SWPlayerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class SlimeWarGame {

    private Plugin plugin;
    private SWPlayerManager playerManager;
    private Lobby lobby;
    private Arena arena;
    private GameState state;
    private int playersToStart;
    private BossBar bossBar;
    private int wave = 0;
    private Collection<Slime> slimes;

    public SlimeWarGame(Plugin plugin, GameConfig gameConfig, LobbyConfig lobbyConfig, ArenaConfig arenaConfig, SlimeConfig slimeConfig, SWPlayerManager playerManager) {
        this.plugin = plugin;
        this.lobby = new Lobby(lobbyConfig);
        this.arena = new Arena(arenaConfig, slimeConfig);
        this.state = GameState.WAITING;
        this.playersToStart = gameConfig.getPlayersToStart();
        this.playerManager = playerManager;
        this.bossBar = Bukkit.createBossBar("Ожидание игроков", BarColor.RED, BarStyle.SOLID);
    }

    public void checkStart() {
        if (state != GameState.WAITING) return;
        if (Bukkit.getOnlinePlayers().size() < playersToStart) return;

        state = GameState.STARTING;

        new BukkitRunnable() {
            int counter = 10;
            @Override
            public void run() {
                if (counter < 1) {
                    start();
                    this.cancel();
                    return;
                }

                bossBar.setProgress(counter / 10.0);
                bossBar.setTitle("Начало через: " + counter);
                counter--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public GameState getState() {
        return state;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void start() {
        state = GameState.RUNNING;
        gameWorker();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setHealth(player.getMaxHealth());
            player.setGameMode(GameMode.SURVIVAL);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.getInventory().clear();
            player.getEquipment().clear();
            player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
            player.teleport(arena.getSpawnLocation());
        });
    }

    public void checkEnd() {
        if (state != GameState.RUNNING) return;

        int livingSize = (int) Bukkit.getOnlinePlayers().stream()
                .map(player -> playerManager.getPlayer(player))
                .filter(swPlayer -> swPlayer.isLiving())
                .count();

        if (livingSize != 0) return;

        state = GameState.STOPPING;

        new BukkitRunnable() {

            int stopTimer = 10;

            @Override
            public void run() {
                if (stopTimer == 0) {
                    Bukkit.getServer().shutdown();
                    this.cancel();
                    return;
                }

                bossBar.setTitle("Перезагрузка через: " + stopTimer);
                bossBar.setProgress(stopTimer / 10.0);

                stopTimer--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void gameWorker() {

        new BukkitRunnable() {

            boolean startWave = true;
            int delayTimer = 10;
            boolean delayTimerStarted = false;
            @Override
            public void run() {
                if (state != GameState.RUNNING) {
                    this.cancel();
                    return;
                }

                if (startWave) {
                    startWave = false;
                    wave++;
                    slimes = spawnSlimes();
                    Collection<Slime> livingSlimes = slimes.stream().filter(slime -> !slime.isDead()).collect(Collectors.toList());
                    bossBar.setTitle("Волна: " + wave + " | Живых слаймов: " + livingSlimes.size());
                    bossBar.setProgress(Double.valueOf(livingSlimes.size()) / Double.valueOf(arena.spawnSlimeCount()));
                }else {
                    Collection<Slime> livingSlimes = slimes.stream().filter(slime -> !slime.isDead()).collect(Collectors.toList());

                    if (livingSlimes.size() > 0) {
                        bossBar.setTitle("Волна: " + wave + " | Живых слаймов: " + livingSlimes.size());
                        bossBar.setProgress(Double.valueOf(livingSlimes.size()) / Double.valueOf(arena.spawnSlimeCount()));
                    }else {
                        if (delayTimerStarted) {
                            bossBar.setTitle("Новая волна через: " + delayTimer);
                            bossBar.setProgress(delayTimer / 10.0);
                            delayTimer--;
                            if (delayTimer < 1) {
                                startWave = true;
                                delayTimer = 10;
                            }
                        }else {
                            delayTimerStarted = true;

                            bossBar.setTitle("Новая волна через: " + delayTimer);
                            bossBar.setProgress(delayTimer / 10.0);

                            delayTimer--;
                        }
                    }
                }

            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public Collection<Slime> spawnSlimes() {
        SWPlayer swPlayer = playerManager.getPlayers().stream()
                .filter(SWPlayer::isLiving)
                .sorted(Comparator.comparing(SWPlayer::getLvl, Comparator.reverseOrder()))
                .findFirst()
                .orElse(null);

        if (swPlayer == null) return new ArrayList<>();

        return arena.spawnSlimes(swPlayer.getLvl());
    }

    public Lobby getLobby() {
        return lobby;
    }

    public Arena getArena() {
        return arena;
    }

    public int getWave() {
        return wave;
    }

    public enum GameState {
        WAITING, STARTING, RUNNING, STOPPING
    }

}
