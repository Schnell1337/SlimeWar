package ru.schnell.slimewar.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.schnell.slimewar.SlimeWarGame;
import ru.schnell.slimewar.player.SWPlayer;

import java.text.DecimalFormat;

public class SWBoard {

    private static final DecimalFormat format;

    private SWPlayer owner;
    private Scoreboard scoreboard;
    private Objective o;
    private SlimeWarGame game;

    static {
        format = new DecimalFormat("##.##");
    }

    public SWBoard(SWPlayer owner, Plugin plugin, SlimeWarGame game) {
        this.game = game;
        this.owner = owner;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.o = scoreboard.registerNewObjective("SlimeWar", "");
        this.o.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.o.setDisplayName("§cСтатистика");

        new BukkitRunnable() {

            @Override
            public void run() {
                updateScoreboard();
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public SWPlayer getOwner() {
        return owner;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void updateScoreboard() {
        for (String s : this.scoreboard.getEntries()) {
            this.scoreboard.resetScores(s);
        }

        Objective o = this.o;
        int i = 0;

        o.getScore("§6Макс. волна: §7" + owner.getMaxWave()).setScore(i++);
        o.getScore("§6Текущая волна: §7" + game.getWave()).setScore(i++);
        o.getScore("§6Урон: §7" + format.format(owner.getDamage())).setScore(i++);
        o.getScore("§6Убийств: §7" + owner.getKills()).setScore(i++);
    }

}
