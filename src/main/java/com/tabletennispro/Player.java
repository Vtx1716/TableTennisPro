package com.tabletennispro;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a table tennis player with statistics
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private int wins;
    private int losses;
    private int totalPointsScored;
    private int totalPointsConceded;
    
    public Player(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.wins = 0;
        this.losses = 0;
        this.totalPointsScored = 0;
        this.totalPointsConceded = 0;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getTotalPointsScored() { return totalPointsScored; }
    public int getTotalPointsConceded() { return totalPointsConceded; }
    
    public int getTotalMatches() {
        return wins + losses;
    }
    
    public double getWinRate() {
        int total = getTotalMatches();
        return total == 0 ? 0.0 : (double) wins / total * 100;
    }
    
    public double getAveragePointsPerMatch() {
        int total = getTotalMatches();
        return total == 0 ? 0.0 : (double) totalPointsScored / total;
    }
    
    // Setters
    public void setName(String name) { this.name = name; }
    
    public void recordWin(int pointsScored, int pointsConceded) {
        this.wins++;
        this.totalPointsScored += pointsScored;
        this.totalPointsConceded += pointsConceded;
    }
    
    public void recordLoss(int pointsScored, int pointsConceded) {
        this.losses++;
        this.totalPointsScored += pointsScored;
        this.totalPointsConceded += pointsConceded;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return id.equals(player.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
