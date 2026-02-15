package com.tabletennispro;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for creating a new tournament
 */
public class TournamentCreationDialog extends JDialog {
    private DataManager dataManager;
    private Tournament createdTournament;
    
    public TournamentCreationDialog(Frame parent, DataManager dataManager) {
        super(parent, "Create Tournament", true);
        this.dataManager = dataManager;
        initializeUI();
    }
    
    private void initializeUI() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Create New Tournament", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tournament name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Tournament Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);
        
        // Tournament type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel typeLabel = new JLabel("Tournament Type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] types = {"Single Elimination", "Double Elimination"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(typeCombo, gbc);
        
        // Best of
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel bestOfLabel = new JLabel("Match Format:");
        bestOfLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(bestOfLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] bestOfOptions = {"Best of 3", "Best of 5", "Best of 7"};
        JComboBox<String> bestOfCombo = new JComboBox<>(bestOfOptions);
        bestOfCombo.setSelectedIndex(1);
        bestOfCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(bestOfCombo, gbc);
        
        // Player selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel playerLabel = new JLabel("Select Players:");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(playerLabel, gbc);
        
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        
        DefaultListModel<Player> availablePlayersModel = new DefaultListModel<>();
        for (Player player : dataManager.getAllPlayers()) {
            availablePlayersModel.addElement(player);
        }
        
        JList<Player> playerList = new JList<>(availablePlayersModel);
        playerList.setFont(new Font("Arial", Font.PLAIN, 14));
        playerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(playerList);
        formPanel.add(scrollPane, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton createButton = new JButton("Create Tournament");
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a tournament name.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            java.util.List<Player> selectedPlayers = playerList.getSelectedValuesList();
            if (selectedPlayers.size() < 2) {
                JOptionPane.showMessageDialog(this,
                    "Please select at least 2 players for the tournament.",
                    "Not Enough Players", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Tournament.TournamentType type = typeCombo.getSelectedIndex() == 0 
                ? Tournament.TournamentType.SINGLE_ELIMINATION 
                : Tournament.TournamentType.DOUBLE_ELIMINATION;
            
            int bestOf = Integer.parseInt(bestOfCombo.getSelectedItem().toString().split(" ")[2]);
            
            createdTournament = new Tournament(name, type, bestOf);
            for (Player player : selectedPlayers) {
                createdTournament.addPlayer(player);
            }
            
            dataManager.addTournament(createdTournament);
            
            JOptionPane.showMessageDialog(this,
                "Tournament created successfully!\nPlayers: " + selectedPlayers.size(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public Tournament getCreatedTournament() {
        return createdTournament;
    }
}
