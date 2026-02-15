package com.tabletennispro;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog for viewing and managing tournament brackets
 */
public class TournamentViewDialog extends JDialog {
    private Tournament tournament;
    private DataManager dataManager;
    private JPanel bracketPanel;
    
    public TournamentViewDialog(Frame parent, Tournament tournament, DataManager dataManager) {
        super(parent, "Tournament: " + tournament.getName(), true);
        this.tournament = tournament;
        this.dataManager = dataManager;
        initializeUI();
    }
    
    private void initializeUI() {
        setSize(900, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel(tournament.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel infoLabel = new JLabel(
            String.format("Type: %s | Players: %d | Best of %d", 
                tournament.getType(), 
                tournament.getPlayers().size(),
                tournament.getBestOf()),
            SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titlePanel.add(infoLabel, BorderLayout.CENTER);
        
        if (tournament.isCompleted()) {
            JLabel winnerLabel = new JLabel(
                "🏆 Winner: " + tournament.getWinner().getName() + " 🏆",
                SwingConstants.CENTER);
            winnerLabel.setFont(new Font("Arial", Font.BOLD, 18));
            winnerLabel.setForeground(new Color(0, 150, 0));
            titlePanel.add(winnerLabel, BorderLayout.SOUTH);
        } else if (tournament.isStarted()) {
            JLabel statusLabel = new JLabel(
                "Round " + tournament.getCurrentRound() + " - In Progress",
                SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
            statusLabel.setForeground(new Color(0, 100, 200));
            titlePanel.add(statusLabel, BorderLayout.SOUTH);
        }
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Bracket display
        bracketPanel = new JPanel();
        bracketPanel.setLayout(new BoxLayout(bracketPanel, BoxLayout.Y_AXIS));
        bracketPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        updateBracketDisplay();
        
        JScrollPane scrollPane = new JScrollPane(bracketPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        if (!tournament.isStarted()) {
            JButton startButton = new JButton("Start Tournament");
            startButton.setFont(new Font("Arial", Font.BOLD, 14));
            startButton.addActionListener(e -> {
                if (tournament.getPlayers().size() < 2) {
                    JOptionPane.showMessageDialog(this,
                        "Need at least 2 players to start tournament!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                tournament.startTournament();
                dataManager.updateData();
                updateBracketDisplay();
                dispose();
                // Reopen to show updated state
                TournamentViewDialog newDialog = new TournamentViewDialog(
                    (Frame) getParent(), tournament, dataManager);
                newDialog.setVisible(true);
            });
            controlPanel.add(startButton);
        } else if (!tournament.isCompleted()) {
            JButton advanceButton = new JButton("Advance to Next Round");
            advanceButton.setFont(new Font("Arial", Font.BOLD, 14));
            advanceButton.addActionListener(e -> {
                tournament.advanceTournament();
                dataManager.updateData();
                updateBracketDisplay();
                
                if (tournament.isCompleted()) {
                    JOptionPane.showMessageDialog(this,
                        "Tournament Complete!\nWinner: " + tournament.getWinner().getName(),
                        "Tournament Finished", JOptionPane.INFORMATION_MESSAGE);
                }
                
                dispose();
                TournamentViewDialog newDialog = new TournamentViewDialog(
                    (Frame) getParent(), tournament, dataManager);
                newDialog.setVisible(true);
            });
            controlPanel.add(advanceButton);
        }
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.addActionListener(e -> dispose());
        controlPanel.add(closeButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void updateBracketDisplay() {
        bracketPanel.removeAll();
        
        if (!tournament.isStarted()) {
            // Show player list
            JLabel playersLabel = new JLabel("Tournament Players:");
            playersLabel.setFont(new Font("Arial", Font.BOLD, 16));
            playersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            bracketPanel.add(playersLabel);
            bracketPanel.add(Box.createVerticalStrut(10));
            
            for (Player player : tournament.getPlayers()) {
                JLabel playerLabel = new JLabel("• " + player.getName());
                playerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                playerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                bracketPanel.add(playerLabel);
            }
        } else {
            // Show matches grouped by round
            List<Match> matches = tournament.getMatches();
            
            if (matches.isEmpty()) {
                JLabel noMatchesLabel = new JLabel("No matches yet");
                noMatchesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                bracketPanel.add(noMatchesLabel);
            } else {
                // Group matches by completion status to show rounds
                int roundNumber = 1;
                int matchesPerRound = tournament.getPlayers().size() / 2;
                int matchIndex = 0;
                
                while (matchIndex < matches.size()) {
                    JLabel roundLabel = new JLabel("Round " + roundNumber);
                    roundLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    roundLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    bracketPanel.add(roundLabel);
                    bracketPanel.add(Box.createVerticalStrut(10));
                    
                    int matchesInThisRound = Math.min(matchesPerRound, matches.size() - matchIndex);
                    
                    for (int i = 0; i < matchesInThisRound && matchIndex < matches.size(); i++, matchIndex++) {
                        Match match = matches.get(matchIndex);
                        JPanel matchPanel = createMatchPanel(match);
                        matchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        bracketPanel.add(matchPanel);
                        bracketPanel.add(Box.createVerticalStrut(5));
                    }
                    
                    bracketPanel.add(Box.createVerticalStrut(15));
                    roundNumber++;
                    matchesPerRound = matchesPerRound / 2;
                    if (matchesPerRound < 1) matchesPerRound = 1;
                }
            }
        }
        
        bracketPanel.revalidate();
        bracketPanel.repaint();
    }
    
    private JPanel createMatchPanel(Match match) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.setMaximumSize(new Dimension(600, 60));
        
        JLabel matchLabel = new JLabel(String.format("%s vs %s",
            match.getPlayer1().getName(),
            match.getPlayer2().getName()));
        matchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel scoreLabel = new JLabel(String.format("%d - %d",
            match.getPlayer1Score(),
            match.getPlayer2Score()));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        if (match.isCompleted()) {
            panel.setBackground(new Color(230, 255, 230));
            JLabel winnerLabel = new JLabel("Winner: " + match.getWinner().getName());
            winnerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            winnerLabel.setForeground(new Color(0, 120, 0));
            
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setOpaque(false);
            infoPanel.add(matchLabel);
            infoPanel.add(winnerLabel);
            
            panel.add(infoPanel, BorderLayout.CENTER);
            panel.add(scoreLabel, BorderLayout.EAST);
        } else {
            panel.setBackground(new Color(255, 255, 230));
            
            JButton playButton = new JButton("Play Match");
            playButton.setFont(new Font("Arial", Font.PLAIN, 12));
            playButton.addActionListener(e -> {
                ScoreTrackerDialog dialog = new ScoreTrackerDialog((Frame) getParent(), dataManager, match);
                dialog.setVisible(true);
                updateBracketDisplay();
            });
            
            panel.add(matchLabel, BorderLayout.CENTER);
            panel.add(playButton, BorderLayout.EAST);
        }
        
        return panel;
    }
}
