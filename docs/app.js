// Table Tennis Pro - Web Application

// Utilities
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <span>${message}</span>
    `;

    container.appendChild(toast);

    // Auto remove after 3 seconds
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function showConfirmation(title, message, onConfirm) {
    const modal = document.getElementById('confirmation-modal');
    document.getElementById('confirm-title').textContent = title;
    document.getElementById('confirm-message').textContent = message;

    const confirmBtn = document.getElementById('confirm-ok-btn');
    const newConfirmBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

    newConfirmBtn.addEventListener('click', () => {
        onConfirm();
        hideModal(modal);
    });

    showModal(modal);
}

// Data Storage
class DataManager {
    constructor() {
        this.players = this.loadData('players') || [];
        this.matches = this.loadData('matches') || [];
        this.tournaments = this.loadData('tournaments') || [];
    }

    loadData(key) {
        const data = localStorage.getItem(`ttp_${key}`);
        return data ? JSON.parse(data) : null;
    }

    saveData(key, data) {
        localStorage.setItem(`ttp_${key}`, JSON.stringify(data));
    }

    addPlayer(name) {
        const player = {
            id: this.generateId(),
            name: name,
            wins: 0,
            losses: 0,
            gamesWon: 0,
            gamesLost: 0
        };
        this.players.push(player);
        this.saveData('players', this.players);
        showToast(`Player "${name}" added successfully`, 'success');
        return player;
    }

    deletePlayer(id) {
        this.players = this.players.filter(p => p.id !== id);
        this.saveData('players', this.players);
        showToast('Player deleted', 'success');
    }

    updatePlayerStats(playerId, won, gamesWon, gamesLost) {
        const player = this.players.find(p => p.id === playerId);
        if (player) {
            if (won) player.wins++;
            else player.losses++;
            player.gamesWon += gamesWon;
            player.gamesLost += gamesLost;
            this.saveData('players', this.players);
        }
    }

    addMatch(match) {
        this.matches.push(match);
        this.saveData('matches', this.matches);
    }

    addTournament(tournament) {
        this.tournaments.push(tournament);
        this.saveData('tournaments', this.tournaments);
    }

    updateTournament(tournament) {
        const index = this.tournaments.findIndex(t => t.id === tournament.id);
        if (index !== -1) {
            this.tournaments[index] = tournament;
            this.saveData('tournaments', this.tournaments);
        }
    }

    deleteTournament(id) {
        this.tournaments = this.tournaments.filter(t => t.id !== id);
        this.saveData('tournaments', this.tournaments);
        showToast('Tournament deleted', 'success');
    }

    generateId() {
        return Date.now().toString(36) + Math.random().toString(36).substr(2);
    }
}

// Tournament Logic
class TournamentSystem {
    constructor(dataManager) {
        this.dm = dataManager;
    }

    createTournament(name, format, playerIds) {
        const players = playerIds.map(id => this.dm.players.find(p => p.id === id));
        // Shuffle players
        for (let i = players.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [players[i], players[j]] = [players[j], players[i]];
        }

        const tournament = {
            id: this.dm.generateId(),
            name: name,
            format: format,
            players: players.map(p => ({ id: p.id, name: p.name })), // Store minimal player info
            matches: [], // Array of match objects
            rounds: [], // Array of arrays of match IDs
            completed: false,
            winner: null,
            createdAt: new Date().toISOString()
        };

        this.generateFirstRound(tournament);
        this.dm.addTournament(tournament);
        return tournament;
    }

    generateFirstRound(tournament) {
        const rounds = [];
        const firstRoundMatches = [];
        const players = tournament.players;

        // Simple single elimination
        for (let i = 0; i < players.length; i += 2) {
            if (i + 1 < players.length) {
                const matchId = this.dm.generateId();
                const match = {
                    id: matchId,
                    tournamentId: tournament.id,
                    p1: players[i],
                    p2: players[i + 1],
                    score1: 0,
                    score2: 0,
                    winner: null,
                    completed: false,
                    round: 1,
                    nextMatchId: null // To be filled when next round is generated
                };
                firstRoundMatches.push(match);
                tournament.matches.push(match);
            } else {
                // Odd player out - gets a bye
                const matchId = this.dm.generateId();
                const match = {
                    id: matchId,
                    tournamentId: tournament.id,
                    p1: players[i],
                    p2: { name: 'BYE', id: 'bye' },
                    score1: 0,
                    score2: 0,
                    winner: players[i], // Auto win
                    completed: true,
                    round: 1,
                    nextMatchId: null
                };
                firstRoundMatches.push(match);
                tournament.matches.push(match);
            }
        }

        tournament.rounds.push(firstRoundMatches.map(m => m.id));
        this.generateNextRoundsPlaceholder(tournament, firstRoundMatches);
    }

    generateNextRoundsPlaceholder(tournament, previousRoundMatches) {
        // Recursively generate empty slots for next rounds
        if (previousRoundMatches.length <= 1) return;

        const nextRoundMatches = [];
        let r = previousRoundMatches[0].round + 1;

        for (let i = 0; i < previousRoundMatches.length; i += 2) {
            const matchId = this.dm.generateId();
            const nextMatch = {
                id: matchId,
                tournamentId: tournament.id,
                p1: null, // To be decided
                p2: null, // To be decided
                score1: 0,
                score2: 0,
                winner: null,
                completed: false,
                round: r,
                nextMatchId: null
            };

            // Link previous matches to this one
            previousRoundMatches[i].nextMatchId = matchId;
            if (i + 1 < previousRoundMatches.length) {
                previousRoundMatches[i + 1].nextMatchId = matchId;
            }

            nextRoundMatches.push(nextMatch);
            tournament.matches.push(nextMatch);
        }

        tournament.rounds.push(nextRoundMatches.map(m => m.id));
        this.generateNextRoundsPlaceholder(tournament, nextRoundMatches);
    }

    advanceTournament(tournament, completedMatch) {
        if (!completedMatch.nextMatchId) {
            // This was the final match
            tournament.completed = true;
            tournament.winner = completedMatch.winner.name;
            this.dm.updateTournament(tournament);
            showToast(`Tournament Completed! Winner: ${tournament.winner}`, 'success');
            return;
        }

        const nextMatch = tournament.matches.find(m => m.id === completedMatch.nextMatchId);
        if (!nextMatch) return;

        // Place winner in next match
        if (!nextMatch.p1) {
            nextMatch.p1 = completedMatch.winner;
        } else if (!nextMatch.p2) {
            nextMatch.p2 = completedMatch.winner;
        }

        this.dm.updateTournament(tournament);
    }
}

// Application State
const dataManager = new DataManager();
const tournamentSystem = new TournamentSystem(dataManager);
let currentMatch = null;

// DOM Elements
const tabButtons = document.querySelectorAll('.tab-btn');
const tabPanes = document.querySelectorAll('.tab-pane');

// Tab Navigation
tabButtons.forEach(btn => {
    btn.addEventListener('click', () => {
        const tabName = btn.dataset.tab;
        switchTab(tabName);
    });
});

function switchTab(tabName) {
    // Update active tab button
    tabButtons.forEach(b => {
        b.classList.toggle('active', b.dataset.tab === tabName);
    });

    // Update active tab pane
    tabPanes.forEach(pane => pane.classList.remove('active'));
    document.getElementById(`${tabName}-tab`).classList.add('active');

    // Refresh content
    if (tabName === 'players') renderPlayers();
    if (tabName === 'score') renderScoreTracker();
    if (tabName === 'tournaments') {
        document.getElementById('tournament-detail-view').classList.add('hidden');
        document.getElementById('tournaments-list').classList.remove('hidden');
        document.querySelector('#tournaments-tab .section-header').classList.remove('hidden');
        renderTournaments();
    }
    if (tabName === 'stats') renderStatistics();
}

// Players Tab
function renderPlayers() {
    const container = document.getElementById('players-list');

    if (dataManager.players.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>No players yet</h3>
                <p>Click "Add Player" to get started</p>
            </div>
        `;
        return;
    }

    container.innerHTML = dataManager.players.map(player => `
        <div class="player-card">
            <h3>${player.name}</h3>
            <div class="stats">
                <p>Matches: ${player.wins + player.losses} (${player.wins}W - ${player.losses}L)</p>
                <p>Win Rate: ${player.wins + player.losses > 0 ? ((player.wins / (player.wins + player.losses)) * 100).toFixed(1) : 0}%</p>
            </div>
            <div class="actions">
                <button class="btn btn-danger btn-small" onclick="deletePlayerHandler('${player.id}')">Delete</button>
            </div>
        </div>
    `).join('');
}

window.deletePlayerHandler = function (id) {
    showConfirmation('Delete Player', 'Are you sure you want to delete this player? This cannot be undone.', () => {
        dataManager.deletePlayer(id);
        renderPlayers();
        updatePlayerSelects();
    });
};

// Add Player Modal
const addPlayerModal = document.getElementById('add-player-modal');
const addPlayerBtn = document.getElementById('add-player-btn');
const savePlayerBtn = document.getElementById('save-player-btn');
const newPlayerNameInput = document.getElementById('new-player-name');

addPlayerBtn.addEventListener('click', () => {
    newPlayerNameInput.value = '';
    showModal(addPlayerModal);
});

savePlayerBtn.addEventListener('click', () => {
    const name = newPlayerNameInput.value.trim();
    if (name) {
        dataManager.addPlayer(name);
        hideModal(addPlayerModal);
        renderPlayers();
        updatePlayerSelects();
    } else {
        showToast('Please enter a player name', 'error');
    }
});

// Score Tracker Tab
function renderScoreTracker() {
    updatePlayerSelects();
}

function updatePlayerSelects() {
    const player1Select = document.getElementById('player1-select');
    const player2Select = document.getElementById('player2-select');

    const options = dataManager.players.map(p =>
        `<option value="${p.id}">${p.name}</option>`
    ).join('');

    player1Select.innerHTML = '<option value="">Select Player 1</option>' + options;
    player2Select.innerHTML = '<option value="">Select Player 2</option>' + options;
}

const startMatchBtn = document.getElementById('start-match-btn');
const matchSetup = document.getElementById('match-setup');
const scoreTracker = document.getElementById('score-tracker');

// Function to start a match (freestyle or tournament)
function startMatch(p1, p2, format, tournamentMatchId = null) {
    currentMatch = {
        player1: p1,
        player2: p2,
        score1: 0,
        score2: 0,
        bestOf: format,
        firstTo: Math.ceil(format / 2),
        tournamentMatchId: tournamentMatchId
    };

    // Switch to score tab if not already there
    switchTab('score');

    matchSetup.classList.add('hidden');
    scoreTracker.classList.remove('hidden');
    updateScoreDisplay();
}

startMatchBtn.addEventListener('click', () => {
    const p1Id = document.getElementById('player1-select').value;
    const p2Id = document.getElementById('player2-select').value;
    const format = parseInt(document.getElementById('match-format').value);

    if (!p1Id || !p2Id) {
        showToast('Please select both players', 'error');
        return;
    }

    if (p1Id === p2Id) {
        showToast('Please select different players', 'error');
        return;
    }

    const player1 = dataManager.players.find(p => p.id === p1Id);
    const player2 = dataManager.players.find(p => p.id === p2Id);

    startMatch(player1, player2, format);
});

function updateScoreDisplay() {
    if (!currentMatch) return;

    document.getElementById('p1-name').textContent = currentMatch.player1.name;
    document.getElementById('p2-name').textContent = currentMatch.player2.name;
    document.getElementById('p1-score').textContent = currentMatch.score1;
    document.getElementById('p2-score').textContent = currentMatch.score2;
    document.getElementById('match-best-of').textContent = currentMatch.bestOf;
    document.getElementById('match-first-to').textContent = currentMatch.firstTo;
}

document.getElementById('p1-plus').addEventListener('click', () => {
    if (currentMatch) {
        currentMatch.score1++;
        updateScoreDisplay();
        checkMatchComplete();
    }
});

document.getElementById('p1-minus').addEventListener('click', () => {
    if (currentMatch && currentMatch.score1 > 0) {
        currentMatch.score1--;
        updateScoreDisplay();
    }
});

document.getElementById('p2-plus').addEventListener('click', () => {
    if (currentMatch) {
        currentMatch.score2++;
        updateScoreDisplay();
        checkMatchComplete();
    }
});

document.getElementById('p2-minus').addEventListener('click', () => {
    if (currentMatch && currentMatch.score2 > 0) {
        currentMatch.score2--;
        updateScoreDisplay();
    }
});

function checkMatchComplete() {
    if (!currentMatch) return;

    if (currentMatch.score1 >= currentMatch.firstTo || currentMatch.score2 >= currentMatch.firstTo) {
        const winner = currentMatch.score1 >= currentMatch.firstTo ? currentMatch.player1 : currentMatch.player2;
        showToast(`🏆 ${winner.name} wins the match to point! Press Finish to save.`, 'success');
    }
}

document.getElementById('finish-match-btn').addEventListener('click', () => {
    if (!currentMatch) return;

    // Validate winner
    if (currentMatch.score1 < currentMatch.firstTo && currentMatch.score2 < currentMatch.firstTo) {
        showConfirmation('Match Not Finished', 'The match score has not reached the winning condition yet. Finish anyway?', () => {
            completeMatch();
        });
        return;
    }
    completeMatch();
});

function completeMatch() {
    const winner = currentMatch.score1 > currentMatch.score2 ? currentMatch.player1 : currentMatch.player2;
    const loser = currentMatch.score1 > currentMatch.score2 ? currentMatch.player2 : currentMatch.player1;
    const winnerScore = Math.max(currentMatch.score1, currentMatch.score2);
    const loserScore = Math.min(currentMatch.score1, currentMatch.score2);

    // Update player stats
    dataManager.updatePlayerStats(winner.id, true, winnerScore, loserScore);
    dataManager.updatePlayerStats(loser.id, false, loserScore, winnerScore);

    // Save match
    const match = {
        id: dataManager.generateId(),
        player1: currentMatch.player1.name,
        player2: currentMatch.player2.name,
        score1: currentMatch.score1,
        score2: currentMatch.score2,
        date: new Date().toISOString()
    };
    dataManager.addMatch(match);

    // Handle Tournament Callback
    if (currentMatch.tournamentMatchId) {
        // Find tournament and update match
        const tournament = dataManager.tournaments.find(t =>
            t.matches.some(m => m.id === currentMatch.tournamentMatchId)
        );

        if (tournament) {
            const tMatch = tournament.matches.find(m => m.id === currentMatch.tournamentMatchId);
            tMatch.score1 = currentMatch.score1;
            tMatch.score2 = currentMatch.score2;
            tMatch.winner = winner;
            tMatch.completed = true;

            tournamentSystem.advanceTournament(tournament, tMatch);
            showToast('Tournament match updated!', 'success');

            // Return to tournament view
            currentMatch = null;
            scoreTracker.classList.add('hidden');
            matchSetup.classList.remove('hidden');
            renderTournamentDetail(tournament.id); // Go back to t-view
            switchTab('tournaments');
            return;
        }
    }

    showToast(`Match completed! ${winner.name} wins ${winnerScore}-${loserScore}`, 'success');

    // Reset
    currentMatch = null;
    scoreTracker.classList.add('hidden');
    matchSetup.classList.remove('hidden');
    renderPlayers();
}

document.getElementById('cancel-match-btn').addEventListener('click', () => {
    showConfirmation('Cancel Match', 'Are you sure you want to cancel this match? Progress will be lost.', () => {
        if (currentMatch.tournamentMatchId) {
            const tournament = dataManager.tournaments.find(t =>
                t.matches.some(m => m.id === currentMatch.tournamentMatchId)
            );
            renderTournamentDetail(tournament.id);
            switchTab('tournaments');
        }
        currentMatch = null;
        scoreTracker.classList.add('hidden');
        matchSetup.classList.remove('hidden');
    });
});

// Tournaments Tab
function renderTournaments() {
    const container = document.getElementById('tournaments-list');

    if (dataManager.tournaments.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>No tournaments yet</h3>
                <p>Click "Create Tournament" to get started</p>
            </div>
        `;
        return;
    }

    container.innerHTML = dataManager.tournaments.map(tournament => `
        <div class="tournament-card" onclick="openTournamentDetail('${tournament.id}')">
            <h3>${tournament.name}</h3>
            <div class="info">
                <p>Format: Best of ${tournament.format}</p>
                <p>Players: ${tournament.players.length}</p>
                <p>Status: <span style="font-weight:bold; color:${tournament.completed ? '#28a745' : '#667eea'}">${tournament.completed ? 'Completed' : 'In Progress'}</span></p>
                ${tournament.winner ? `<p>🏆 Winner: <strong>${tournament.winner}</strong></p>` : ''}
            </div>
            <div class="actions">
                <button class="btn btn-danger btn-small" onclick="event.stopPropagation(); deleteTournamentHandler('${tournament.id}')">Delete</button>
            </div>
        </div>
    `).join('');
}

window.openTournamentDetail = function (id) {
    renderTournamentDetail(id);
};

window.deleteTournamentHandler = function (id) {
    showConfirmation('Delete Tournament', 'Are you sure you want to delete this tournament?', () => {
        dataManager.deleteTournament(id);
        renderTournaments();
    });
};

// Tournament Detail View
const tournamentDetailView = document.getElementById('tournament-detail-view');
const tournamentBracket = document.getElementById('tournament-bracket');
const backToTournamentsBtn = document.getElementById('back-to-tournaments-btn');

backToTournamentsBtn.addEventListener('click', () => {
    tournamentDetailView.classList.add('hidden');
    document.getElementById('tournaments-list').classList.remove('hidden');
    document.querySelector('#tournaments-tab .section-header').classList.remove('hidden');
});

function renderTournamentDetail(id) {
    const tournament = dataManager.tournaments.find(t => t.id === id);
    if (!tournament) return;

    document.getElementById('tournaments-list').classList.add('hidden');
    document.querySelector('#tournaments-tab .section-header').classList.add('hidden');
    tournamentDetailView.classList.remove('hidden');

    document.getElementById('tournament-detail-title').textContent = tournament.name;

    // Render Bracket
    tournamentBracket.innerHTML = '';

    tournament.rounds.forEach((roundMatches, index) => {
        const roundColumn = document.createElement('div');
        roundColumn.className = 'bracket-round';

        let roundName = `Round ${index + 1}`;
        if (index === tournament.rounds.length - 1) roundName = 'Finals';
        if (index === tournament.rounds.length - 2) roundName = 'Semi-Finals';

        const roundTitle = document.createElement('h4');
        roundTitle.textContent = roundName;
        roundColumn.appendChild(roundTitle);

        roundMatches.forEach(matchId => {
            const match = tournament.matches.find(m => m.id === matchId);
            const matchNode = document.createElement('div');
            matchNode.className = `match-node ${match.completed ? 'completed' : ''} ${(!match.completed && match.p1 && match.p2) ? 'active' : ''}`;

            const p1 = match.p1 ? match.p1.name : 'TBD';
            const p2 = match.p2 ? match.p2.name : 'TBD';
            const p1Class = (match.completed && match.winner && match.winner.id === match.p1.id) ? 'winner' : '';
            const p2Class = (match.completed && match.winner && match.winner.id === match.p2.id) ? 'winner' : '';

            matchNode.innerHTML = `
                <div class="match-player ${p1Class}">
                    <span>${p1}</span>
                    <span>${match.score1}</span>
                </div>
                <div class="match-player ${p2Class}" style="border-top:1px solid #eee">
                    <span>${p2}</span>
                    <span>${match.score2}</span>
                </div>
            `;

            if (!match.completed && match.p1 && match.p2 && match.p2.id !== 'bye') {
                matchNode.title = "Click to play match";
                matchNode.addEventListener('click', () => {
                    playTournamentMatch(match, tournament);
                });
            }

            roundColumn.appendChild(matchNode);
        });

        tournamentBracket.appendChild(roundColumn);
    });
}

function playTournamentMatch(match, tournament) {
    showConfirmation('Play Match', `Start match: ${match.p1.name} vs ${match.p2.name}?`, () => {
        // Need to reconstruct full player objects for logic
        const p1 = dataManager.players.find(p => p.id === match.p1.id);
        const p2 = dataManager.players.find(p => p.id === match.p2.id);

        if (!p1 || !p2) {
            showToast('Error loading players for match', 'error');
            return;
        }

        startMatch(p1, p2, tournament.format, match.id);
    });
}

// Create Tournament Modal
const createTournamentModal = document.getElementById('create-tournament-modal');
const createTournamentBtn = document.getElementById('create-tournament-btn');
const saveTournamentBtn = document.getElementById('save-tournament-btn');

createTournamentBtn.addEventListener('click', () => {
    renderTournamentPlayersList();
    showModal(createTournamentModal);
});

function renderTournamentPlayersList() {
    const container = document.getElementById('tournament-players-list');

    if (dataManager.players.length === 0) {
        container.innerHTML = '<p class="text-center">No players available. Add players first.</p>';
        return;
    }

    container.innerHTML = dataManager.players.map(player => `
        <label class="checkbox-item">
            <input type="checkbox" value="${player.id}">
            <span>${player.name}</span>
        </label>
    `).join('');
}

saveTournamentBtn.addEventListener('click', () => {
    const name = document.getElementById('tournament-name').value.trim();
    const format = parseInt(document.getElementById('tournament-format').value);
    const checkboxes = document.querySelectorAll('#tournament-players-list input:checked');
    const selectedPlayerIds = Array.from(checkboxes).map(cb => cb.value);

    if (!name) {
        showToast('Please enter a tournament name', 'error');
        return;
    }

    if (selectedPlayerIds.length < 2) {
        showToast('Please select at least 2 players', 'error');
        return;
    }

    // Check power of 2 for better brackets (optional, but good for UI)
    // For now we allow any, but logic handles it simply

    tournamentSystem.createTournament(name, format, selectedPlayerIds);
    hideModal(createTournamentModal);
    renderTournaments();

    showToast(`Tournament "${name}" created!`, 'success');
});

// Statistics Tab
function renderStatistics() {
    const container = document.getElementById('stats-table-container');

    if (dataManager.players.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>No statistics yet</h3>
                <p>Add players and play matches to see statistics</p>
            </div>
        `;
        return;
    }

    // Sort by wins
    const sortedPlayers = [...dataManager.players].sort((a, b) => b.wins - a.wins);

    container.innerHTML = `
        <table class="stats-table">
            <thead>
                <tr>
                    <th>Rank</th>
                    <th>Player</th>
                    <th>Matches</th>
                    <th>Wins</th>
                    <th>Losses</th>
                    <th>Win Rate</th>
                    <th>Games Won</th>
                    <th>Games Lost</th>
                </tr>
            </thead>
            <tbody>
                ${sortedPlayers.map((player, index) => {
        const totalMatches = player.wins + player.losses;
        const winRate = totalMatches > 0 ? ((player.wins / totalMatches) * 100).toFixed(1) : 0;

        return `
                        <tr>
                            <td>${index + 1}</td>
                            <td><strong>${player.name}</strong></td>
                            <td>${totalMatches}</td>
                            <td>${player.wins}</td>
                            <td>${player.losses}</td>
                            <td>${winRate}%</td>
                            <td>${player.gamesWon}</td>
                            <td>${player.gamesLost}</td>
                        </tr>
                    `;
    }).join('')}
            </tbody>
        </table>
    `;
}

// Modal Functions
function showModal(modal) {
    modal.classList.add('active');
}

function hideModal(modal) {
    modal.classList.remove('active');
}

// Close modal on X button or Cancel
document.querySelectorAll('.close-btn, .cancel-modal').forEach(btn => {
    btn.addEventListener('click', (e) => {
        const modal = e.target.closest('.modal');
        hideModal(modal);
    });
});

// Close modal on outside click
document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            hideModal(modal);
        }
    });
});

// Initialize app
renderPlayers();
updatePlayerSelects();
