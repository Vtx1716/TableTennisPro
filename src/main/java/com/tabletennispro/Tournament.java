package com.tabletennispro;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a tournament bracket with single or double elimination
 */
public class Tournament implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private List<Player> players;
    private List<Match> matches;
    private TournamentType type;
    private int bestOf;
    private boolean started;
    private boolean completed;

    public enum TournamentType {
        SINGLE_ELIMINATION,
        DOUBLE_ELIMINATION
    }

    public Tournament(String name, TournamentType type, int bestOf) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.bestOf = bestOf;
        this.players = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.started = false;
        this.completed = false;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public List<Match> getMatches() {
        return new ArrayList<>(matches);
    }

    public TournamentType getType() {
        return type;
    }

    public int getBestOf() {
        return bestOf;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void addPlayer(Player player) {
        if (!started && !players.contains(player)) {
            players.add(player);
        }
    }

    public void removePlayer(Player player) {
        if (!started) {
            players.remove(player);
        }
    }

    public void startTournament() {
        if (started || players.size() < 2) {
            return;
        }

        // Shuffle players for random seeding
        Collections.shuffle(players);

        // Generate first round matches
        generateFirstRound();
        started = true;
    }

    private void generateFirstRound() {
        // For single elimination, pair up players
        for (int i = 0; i < players.size() - 1; i += 2) {
            Match match = new Match(players.get(i), players.get(i + 1), bestOf);
            matches.add(match);
        }

        // If odd number of players, one gets a bye (automatically advances)
        if (players.size() % 2 != 0) {
            // The last player gets a bye - we'll handle this in the next round
        }
    }

    public void advanceTournament() {
        // Get all completed matches from current round
        List<Match> currentRound = getCurrentRoundMatches();
        boolean allCompleted = currentRound.stream().allMatch(Match::isCompleted);

        if (!allCompleted) {
            return; // Can't advance until all matches are done
        }

        // Get winners from current round
        List<Player> winners = new ArrayList<>();
        for (Match match : currentRound) {
            winners.add(match.getWinner());
        }

        // Handle bye if odd number of players
        if (players.size() % 2 != 0 && matches.size() == currentRound.size()) {
            winners.add(players.get(players.size() - 1));
        }

        // Check if tournament is complete
        if (winners.size() == 1) {
            completed = true;
            return;
        }

        // Generate next round matches
        for (int i = 0; i < winners.size() - 1; i += 2) {
            Match match = new Match(winners.get(i), winners.get(i + 1), bestOf);
            matches.add(match);
        }
    }

    private List<Match> getCurrentRoundMatches() {
        List<Match> currentRound = new ArrayList<>();
        for (int i = matches.size() - 1; i >= 0; i--) {
            Match match = matches.get(i);
            if (!match.isCompleted()) {
                currentRound.add(0, match);
            } else if (!currentRound.isEmpty()) {
                break; // Found the start of current round
            }
        }

        // If all matches are completed, return the last set
        if (currentRound.isEmpty() && !matches.isEmpty()) {
            int roundSize = 1;
            for (int i = matches.size() - 1; i >= 0 && roundSize > 0; i--) {
                currentRound.add(0, matches.get(i));
                roundSize--;
            }
        }

        return currentRound;
    }

    public Player getWinner() {
        if (!completed)
            return null;
        Match finalMatch = matches.get(matches.size() - 1);
        return finalMatch.getWinner();
    }

    public int getCurrentRound() {
        if (!started)
            return 0;

        int playersCount = players.size();

        // Calculate which round we're in based on completed matches
        int completedMatches = (int) matches.stream().filter(Match::isCompleted).count();

        // Simple calculation for single elimination
        int round = 1;
        int matchesInRound = playersCount / 2;
        int matchesCounted = 0;

        while (matchesCounted + matchesInRound <= completedMatches) {
            matchesCounted += matchesInRound;
            matchesInRound = matchesInRound / 2;
            round++;
        }

        return round;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %d players)%s",
                name,
                type,
                players.size(),
                completed ? " - Completed" : started ? " - In Progress" : "");
    }
}
