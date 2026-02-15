package com.tabletennispro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Main GUI window for Table Tennis Pro
 */
public class MainWindow extends JFrame {
    private DataManager dataManager;
    private JTabbedPane tabbedPane;

    // Panels
    private JPanel playersPanel;
    private JPanel scoreTrackerPanel;
    private JPanel tournamentPanel;
    private JPanel statsPanel;

    public MainWindow() {
        dataManager = new DataManager();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Table Tennis Pro");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Create panels
        createPlayersPanel();
        createScoreTrackerPanel();
        createTournamentPanel();
        createStatsPanel();

        // Add panels to tabbed pane
        tabbedPane.addTab("Players", new ImageIcon(), playersPanel, "Manage Players");
        tabbedPane.addTab("Score Tracker", new ImageIcon(), scoreTrackerPanel, "Track Match Scores");
        tabbedPane.addTab("Tournaments", new ImageIcon(), tournamentPanel, "Manage Tournaments");
        tabbedPane.addTab("Statistics", new ImageIcon(), statsPanel, "View Player Statistics");

        add(tabbedPane);

        // Apply modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPlayersPanel() {
        playersPanel = new JPanel(new BorderLayout(10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Player Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        playersPanel.add(titleLabel, BorderLayout.NORTH);

        // Player list
        DefaultListModel<Player> playerListModel = new DefaultListModel<>();
        JList<Player> playerList = new JList<>(playerListModel);
        playerList.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(playerList);

        // Load players
        for (Player player : dataManager.getAllPlayers()) {
            playerListModel.addElement(player);
        }

        playersPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add Player");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter player name:");
            if (name != null && !name.trim().isEmpty()) {
                Player player = new Player(name.trim());
                dataManager.addPlayer(player);
                playerListModel.addElement(player);
            }
        });

        JButton removeButton = new JButton("Remove Player");
        removeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        removeButton.addActionListener(e -> {
            Player selected = playerList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to remove " + selected.getName() + "?",
                        "Confirm Removal", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dataManager.removePlayer(selected);
                    playerListModel.removeElement(selected);
                }
            }
        });

        JButton editButton = new JButton("Edit Player");
        editButton.setFont(new Font("Arial", Font.PLAIN, 14));
        editButton.addActionListener(e -> {
            Player selected = playerList.getSelectedValue();
            if (selected != null) {
                String newName = JOptionPane.showInputDialog(this,
                        "Enter new name:", selected.getName());
                if (newName != null && !newName.trim().isEmpty()) {
                    selected.setName(newName.trim());
                    dataManager.updateData();
                    playerList.repaint();
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(editButton);

        playersPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createScoreTrackerPanel() {
        scoreTrackerPanel = new JPanel(new BorderLayout(10, 10));
        scoreTrackerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Score Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreTrackerPanel.add(titleLabel, BorderLayout.NORTH);

        JButton newMatchButton = new JButton("Start New Match");
        newMatchButton.setFont(new Font("Arial", Font.BOLD, 16));
        newMatchButton.setPreferredSize(new Dimension(200, 50));
        newMatchButton.addActionListener(e -> openScoreTracker());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(newMatchButton);

        scoreTrackerPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void openScoreTracker() {
        ScoreTrackerDialog dialog = new ScoreTrackerDialog(this, dataManager);
        dialog.setVisible(true);
    }

    private void createTournamentPanel() {
        tournamentPanel = new JPanel(new BorderLayout(10, 10));
        tournamentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Tournament Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tournamentPanel.add(titleLabel, BorderLayout.NORTH);

        // Tournament list
        DefaultListModel<Tournament> tournamentListModel = new DefaultListModel<>();
        JList<Tournament> tournamentList = new JList<>(tournamentListModel);
        tournamentList.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(tournamentList);

        // Load tournaments
        for (Tournament tournament : dataManager.getAllTournaments()) {
            tournamentListModel.addElement(tournament);
        }

        tournamentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton createButton = new JButton("Create Tournament");
        createButton.setFont(new Font("Arial", Font.PLAIN, 14));
        createButton.addActionListener(e -> {
            TournamentCreationDialog dialog = new TournamentCreationDialog(this, dataManager);
            dialog.setVisible(true);
            // Refresh list
            tournamentListModel.clear();
            for (Tournament tournament : dataManager.getAllTournaments()) {
                tournamentListModel.addElement(tournament);
            }
        });

        JButton viewButton = new JButton("View/Manage");
        viewButton.setFont(new Font("Arial", Font.PLAIN, 14));
        viewButton.addActionListener(e -> {
            Tournament selected = tournamentList.getSelectedValue();
            if (selected != null) {
                TournamentViewDialog dialog = new TournamentViewDialog(this, selected, dataManager);
                dialog.setVisible(true);
                tournamentList.repaint();
            }
        });

        JButton deleteButton = new JButton("Delete Tournament");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> {
            Tournament selected = tournamentList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this tournament?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dataManager.removeTournament(selected);
                    tournamentListModel.removeElement(selected);
                }
            }
        });

        buttonPanel.add(createButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        tournamentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createStatsPanel() {
        statsPanel = new JPanel(new BorderLayout(10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Player Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statsPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = { "Player", "Matches", "Wins", "Losses", "Win Rate", "Avg Points/Match" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        statsTable.setRowHeight(25);
        statsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Populate table
        for (Player player : dataManager.getAllPlayers()) {
            Object[] row = {
                    player.getName(),
                    player.getTotalMatches(),
                    player.getWins(),
                    player.getLosses(),
                    String.format("%.1f%%", player.getWinRate()),
                    String.format("%.1f", player.getAveragePointsPerMatch())
            };
            tableModel.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(statsTable);
        statsPanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Statistics");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            for (Player player : dataManager.getAllPlayers()) {
                Object[] row = {
                        player.getName(),
                        player.getTotalMatches(),
                        player.getWins(),
                        player.getLosses(),
                        String.format("%.1f%%", player.getWinRate()),
                        String.format("%.1f", player.getAveragePointsPerMatch())
                };
                tableModel.addRow(row);
            }
        });

        statsPanel.add(refreshButton, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
