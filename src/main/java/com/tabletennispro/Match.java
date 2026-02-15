package com.tabletennispro;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a table tennis match between two players
 */
public class Match implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private Player player1;
    private Player player2;
    private int player1Score;
    private int player2Score;
    private LocalDateTime timestamp;
    private boolean completed;
    private int bestOf; // Best of 3, 5, 7, etc.
    
    public Match(Player player1, Player player2, int bestOf) {
        this.id = UUID.randomUUID().toString();
        this.player1 = player1;
        this.player2 = player2;
        this.player1Score = 0;
        this.player2Score = 0;
        this.timestamp = LocalDateTime.now();
        this.completed = false;
        this.bestOf = bestOf;
    }
    
    // Getters
    public String getId() { return id; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public int getPlayer1Score() { return player1Score; }
    public int getPlayer2Score() { return player2Score; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isCompleted() { return completed; }
    public int getBestOf() { return bestOf; }
    
    // Setters
    public void setPlayer1Score(int score) { this.player1Score = score; }
    public void setPlayer2Score(int score) { this.player2Score = score; }
    
    public void incrementPlayer1Score() {
        player1Score++;
        checkMatchCompletion();
    }
    
    public void incrementPlayer2Score() {
        player2Score++;
        checkMatchCompletion();
    }
    
    public void decrementPlayer1Score() {
        if (player1Score > 0) player1Score--;
    }
    
    public void decrementPlayer2Score() {
        if (player2Score > 0) player2Score--;
    }
    
    private void checkMatchCompletion() {
        int gamesNeededToWin = (bestOf / 2) + 1;
        if (player1Score >= gamesNeededToWin || player2Score >= gamesNeededToWin) {
            completed = true;
        }
    }
    
    public Player getWinner() {
        if (!completed) return null;
        return player1Score > player2Score ? player1 : player2;
    }
    
    public Player getLoser() {
        if (!completed) return null;
        return player1Score < player2Score ? player1 : player2;
    }
    
    public void completeMatch() {
        this.completed = true;
        // Update player statistics
        if (player1Score > player2Score) {
            player1.recordWin(player1Score, player2Score);
            player2.recordLoss(player2Score, player1Score);
        } else {
            player2.recordWin(player2Score, player1Score);
            player1.recordLoss(player1Score, player2Score);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s vs %s (%d-%d)%s", 
            player1.getName(), 
            player2.getName(), 
            player1Score, 
            player2Score,
            completed ? " - Completed" : "");
    }
}
