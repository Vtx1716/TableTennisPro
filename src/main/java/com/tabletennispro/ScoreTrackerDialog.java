package com.tabletennispro;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for tracking scores during a match
 */
public class ScoreTrackerDialog extends JDialog {
    private DataManager dataManager;
    private Match currentMatch;

    private JLabel player1NameLabel;
    private JLabel player2NameLabel;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private JLabel statusLabel;

    public ScoreTrackerDialog(Frame parent, DataManager dataManager) {
        super(parent, "Score Tracker", true);
        this.dataManager = dataManager;

        if (dataManager.getAllPlayers().size() < 2) {
            JOptionPane.showMessageDialog(parent,
                    "You need at least 2 players to start a match!\nPlease add players first.",
                    "Not Enough Players", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        setupMatch();
        if (currentMatch != null) {
            initializeUI();
        }
    }

    // Constructor for tournament matches
    public ScoreTrackerDialog(Frame parent, DataManager dataManager, Match match) {
        super(parent, "Score Tracker", true);
        this.dataManager = dataManager;
        this.currentMatch = match;
        initializeUI();
    }

    private void setupMatch() {
        // Player selection dialog
        JPanel selectionPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<Player> player1Combo = new JComboBox<>();
        JComboBox<Player> player2Combo = new JComboBox<>();

        for (Player player : dataManager.getAllPlayers()) {
            player1Combo.addItem(player);
            player2Combo.addItem(player);
        }

        if (player2Combo.getItemCount() > 1) {
            player2Combo.setSelectedIndex(1);
        }

        String[] bestOfOptions = { "Best of 3", "Best of 5", "Best of 7" };
        JComboBox<String> bestOfCombo = new JComboBox<>(bestOfOptions);
        bestOfCombo.setSelectedIndex(1); // Default to Best of 5

        selectionPanel.add(new JLabel("Player 1:"));
        selectionPanel.add(player1Combo);
        selectionPanel.add(new JLabel("Player 2:"));
        selectionPanel.add(player2Combo);
        selectionPanel.add(new JLabel("Match Format:"));
        selectionPanel.add(bestOfCombo);

        int result = JOptionPane.showConfirmDialog(getParent(), selectionPanel,
                "Match Setup", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Player p1 = (Player) player1Combo.getSelectedItem();
            Player p2 = (Player) player2Combo.getSelectedItem();

            if (p1.equals(p2)) {
                JOptionPane.showMessageDialog(getParent(),
                        "Please select two different players!",
                        "Invalid Selection", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            int bestOf = Integer.parseInt(bestOfCombo.getSelectedItem().toString().split(" ")[2]);
            currentMatch = new Match(p1, p2, bestOf);
        } else {
            dispose();
        }
    }

    private void initializeUI() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Live Match", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Score panel
        JPanel scorePanel = new JPanel(new GridLayout(2, 2, 20, 20));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Player 1
        JPanel player1Panel = new JPanel(new BorderLayout());
        player1Panel.setBackground(new Color(100, 150, 255));
        player1Panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        player1NameLabel = new JLabel(currentMatch.getPlayer1().getName(), SwingConstants.CENTER);
        player1NameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        player1NameLabel.setForeground(Color.WHITE);

        player1ScoreLabel = new JLabel("0", SwingConstants.CENTER);
        player1ScoreLabel.setFont(new Font("Arial", Font.BOLD, 72));
        player1ScoreLabel.setForeground(Color.WHITE);

        player1Panel.add(player1NameLabel, BorderLayout.NORTH);
        player1Panel.add(player1ScoreLabel, BorderLayout.CENTER);

        // Player 2
        JPanel player2Panel = new JPanel(new BorderLayout());
        player2Panel.setBackground(new Color(255, 100, 100));
        player2Panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        player2NameLabel = new JLabel(currentMatch.getPlayer2().getName(), SwingConstants.CENTER);
        player2NameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        player2NameLabel.setForeground(Color.WHITE);

        player2ScoreLabel = new JLabel("0", SwingConstants.CENTER);
        player2ScoreLabel.setFont(new Font("Arial", Font.BOLD, 72));
        player2ScoreLabel.setForeground(Color.WHITE);

        player2Panel.add(player2NameLabel, BorderLayout.NORTH);
        player2Panel.add(player2ScoreLabel, BorderLayout.CENTER);

        // Buttons for player 1
        JPanel player1ButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton p1PlusButton = new JButton("+1 Game");
        p1PlusButton.setFont(new Font("Arial", Font.BOLD, 16));
        p1PlusButton.addActionListener(e -> incrementPlayer1Score());

        JButton p1MinusButton = new JButton("-1 Game");
        p1MinusButton.setFont(new Font("Arial", Font.PLAIN, 14));
        p1MinusButton.addActionListener(e -> decrementPlayer1Score());

        player1ButtonPanel.add(p1PlusButton);
        player1ButtonPanel.add(p1MinusButton);

        // Buttons for player 2
        JPanel player2ButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton p2PlusButton = new JButton("+1 Game");
        p2PlusButton.setFont(new Font("Arial", Font.BOLD, 16));
        p2PlusButton.addActionListener(e -> incrementPlayer2Score());

        JButton p2MinusButton = new JButton("-1 Game");
        p2MinusButton.setFont(new Font("Arial", Font.PLAIN, 14));
        p2MinusButton.addActionListener(e -> decrementPlayer2Score());

        player2ButtonPanel.add(p2PlusButton);
        player2ButtonPanel.add(p2MinusButton);

        scorePanel.add(player1Panel);
        scorePanel.add(player2Panel);
        scorePanel.add(player1ButtonPanel);
        scorePanel.add(player2ButtonPanel);

        add(scorePanel, BorderLayout.CENTER);

        // Status and control panel
        JPanel bottomPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel("Best of " + currentMatch.getBestOf() + " - Match in Progress",
                SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout());

        JButton finishButton = new JButton("Finish Match");
        finishButton.setFont(new Font("Arial", Font.BOLD, 14));
        finishButton.addActionListener(e -> finishMatch());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel this match?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        controlPanel.add(finishButton);
        controlPanel.add(cancelButton);

        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void incrementPlayer1Score() {
        currentMatch.incrementPlayer1Score();
        updateDisplay();
    }

    private void incrementPlayer2Score() {
        currentMatch.incrementPlayer2Score();
        updateDisplay();
    }

    private void decrementPlayer1Score() {
        currentMatch.decrementPlayer1Score();
        updateDisplay();
    }

    private void decrementPlayer2Score() {
        currentMatch.decrementPlayer2Score();
        updateDisplay();
    }

    private void updateDisplay() {
        player1ScoreLabel.setText(String.valueOf(currentMatch.getPlayer1Score()));
        player2ScoreLabel.setText(String.valueOf(currentMatch.getPlayer2Score()));

        if (currentMatch.isCompleted()) {
            Player winner = currentMatch.getWinner();
            statusLabel.setText("MATCH COMPLETE - " + winner.getName() + " WINS!");
            statusLabel.setForeground(new Color(0, 150, 0));
        }
    }

    private void finishMatch() {
        if (currentMatch.getPlayer1Score() == 0 && currentMatch.getPlayer2Score() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please record at least one game before finishing the match.",
                    "No Score Recorded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentMatch.completeMatch();
        dataManager.addMatch(currentMatch);

        Player winner = currentMatch.getWinner();
        JOptionPane.showMessageDialog(this,
                String.format("Match Complete!\n\n%s defeats %s\nScore: %d-%d",
                        winner.getName(),
                        currentMatch.getLoser().getName(),
                        currentMatch.getPlayer1Score(),
                        currentMatch.getPlayer2Score()),
                "Match Result", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}
