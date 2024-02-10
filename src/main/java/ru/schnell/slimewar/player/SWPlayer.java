package ru.schnell.slimewar.player;

import org.bukkit.plugin.Plugin;
import ru.schnell.slimewar.SlimeWarGame;
import ru.schnell.slimewar.scoreboard.SWBoard;

import java.util.UUID;

public class SWPlayer {

    private UUID id;
    private boolean living;
    private int lvl = 1;
    private int maxWave = 0;
    private int kills = 0;
    private double damage = 0.0;
    private SWBoard swBoard;
    protected SWPlayer(UUID id) {
        this.id = id;
    }

    public int getLvl() {
        return lvl;
    }

    public void incLvl() {
        lvl++;
    }

    public boolean isLiving() {
        return living;
    }

    public void setLiving(boolean living) {
        this.living = living;
    }

    public int getMaxWave() {
        return maxWave;
    }

    public int getKills() {
        return kills;
    }

    public double getDamage() {
        return damage;
    }

    public void createSWBoard(Plugin plugin, SlimeWarGame game) {
        if (this.swBoard == null) this.swBoard = new SWBoard(this, plugin, game);
    }

    public SWBoard getSwBoard() {
        return swBoard;
    }

    public void setMaxWave(int maxWave) {
        this.maxWave = maxWave;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
