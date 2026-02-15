package com.tabletennispro;

import java.io.*;
import java.util.*;

/**
 * Manages all data for the Table Tennis Pro application
 */
public class DataManager {
    private static final String DATA_FILE = "tabletennis_data.dat";
    
    private List<Player> players;
    private List<Match> matches;
    private List<Tournament> tournaments;
    
    public DataManager() {
        this.players = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.tournaments = new ArrayList<>();
        loadData();
    }
    
    // Player management
    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            saveData();
        }
    }
    
    public void removePlayer(Player player) {
        players.remove(player);
        saveData();
    }
    
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }
    
    public Player getPlayerByName(String name) {
        return players.stream()
            .filter(p -> p.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
    
    // Match management
    public void addMatch(Match match) {
        matches.add(match);
        saveData();
    }
    
    public List<Match> getAllMatches() {
        return new ArrayList<>(matches);
    }
    
    public List<Match> getMatchesForPlayer(Player player) {
        List<Match> playerMatches = new ArrayList<>();
        for (Match match : matches) {
            if (match.getPlayer1().equals(player) || match.getPlayer2().equals(player)) {
                playerMatches.add(match);
            }
        }
        return playerMatches;
    }
    
    // Tournament management
    public void addTournament(Tournament tournament) {
        tournaments.add(tournament);
        saveData();
    }
    
    public void removeTournament(Tournament tournament) {
        tournaments.remove(tournament);
        saveData();
    }
    
    public List<Tournament> getAllTournaments() {
        return new ArrayList<>(tournaments);
    }
    
    public List<Tournament> getActiveTournaments() {
        List<Tournament> active = new ArrayList<>();
        for (Tournament t : tournaments) {
            if (t.isStarted() && !t.isCompleted()) {
                active.add(t);
            }
        }
        return active;
    }
    
    // Data persistence
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(players);
            oos.writeObject(matches);
            oos.writeObject(tournaments);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            players = (List<Player>) ois.readObject();
            matches = (List<Match>) ois.readObject();
            tournaments = (List<Tournament>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
            // Initialize with empty lists if loading fails
            players = new ArrayList<>();
            matches = new ArrayList<>();
            tournaments = new ArrayList<>();
        }
    }
    
    public void updateData() {
        saveData();
    }
}
