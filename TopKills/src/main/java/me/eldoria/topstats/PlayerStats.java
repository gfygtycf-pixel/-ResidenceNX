package me.eldoria.topstats;

public class PlayerStats {

    private final String name;

    private int kills;
    private int deaths;

    public PlayerStats(String name) {
        this.name = name;
    }

    public PlayerStats(String name, int kills, int deaths) {
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
    }

    public String getName() {
        return name;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addKill() {
        kills++;
    }

    public void addDeath() {
        deaths++;
    }

    public double getKd() {

        if (deaths == 0) {
            return kills;
        }

        return (double) kills / deaths;
    }
}