const API_URL = 'http://localhost:8080/data';

let horses = [];
let currentUser = null;
let currentUserId = null;
let selectedHorseId = null;
let balance = 0;
let topupAmount = 0;

const loginScreen = document.getElementById('login-screen');
const appScreen = document.getElementById('app-screen');
const loginBtn = document.getElementById('login-btn');
const loginError = document.getElementById('login-error');
const balanceAmountElem = document.getElementById('balance-amount');
const btnTopup = document.getElementById('btn-topup');
const topupModal = document.getElementById('topup-modal');
const topupAmountInput = document.getElementById('topup-amount');
const topupNextBtn = document.getElementById('topup-next-btn');
const topupCancelBtn = document.getElementById('topup-cancel-btn');
const blikModal = document.getElementById('blik-modal');
const blikCodeInput = document.getElementById('blik-code');
const blikSubmitBtn = document.getElementById('blik-submit-btn');
const cardsContainer = document.getElementById('cards-container');
const startRaceContainer = document.querySelector('.start-race-container');
const startRaceBtn = document.getElementById('start-race-btn');
const resultContainer = document.getElementById('result-container');
const sidebar = document.getElementById('sidebar');
const sidebarToggle = document.getElementById('sidebar-toggle');
const sidebarClose = document.getElementById('sidebar-close');
const betHistoryList = document.getElementById('bet-history-list');
const resultModal = document.getElementById('result-modal');
const resultMessage = document.getElementById('result-message');
const resultOkBtn = document.getElementById('result-ok-btn');

// --- Login ---
loginBtn.onclick = () => {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    if (!username || !password) {
        showLoginError('Please enter username and password.');
        return;
    }

    fetch(`${API_URL}/users/login?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`)
        .then(response => {
            if (!response.ok) throw new Error('Invalid username or password.');
            return response.json();
        })
        .then(user => {
            currentUserId = user.id;
            currentUser = user.username;
            balance = user.balance;
            loginScreen.style.display = 'none';
            appScreen.style.display = 'flex';
            loginError.style.display = 'none';
            renderBalance();
            fetchHorses();
        })
        .catch(error => showLoginError(error.message));
};

function showLoginError(message) {
    loginError.textContent = message;
    loginError.style.display = 'block';
}

// --- Sidebar toggle logic ---
let sidebarOpen = false;

sidebarToggle.onclick = () => {
    sidebarOpen = !sidebarOpen;
    sidebar.style.width = sidebarOpen ? '320px' : '0';
    if (sidebarOpen && currentUserId) fetchAndRenderBetHistory(currentUserId);
};

// --- Fetch and render bet history ---
let betHistorySorted = false;

document.getElementById('sort-toggle').onclick = () => {
    betHistorySorted = !betHistorySorted;
    fetchAndRenderBetHistory(currentUserId);
};

function fetchAndRenderBetHistory(userId) {
    const url = betHistorySorted
        ? `${API_URL}/bet/history/${userId}/sorted`
        : `${API_URL}/bet/history/${userId}`;

    fetch(url)
        .then(res => res.json())
        .then(bets => {
            let html = `<div style="display:flex;font-weight:bold;">
                <div style="flex:1;">Result</div>
                <div style="flex:1;">Amount</div>
                <div style="flex:2;">Betted Horse</div>
            </div>`;
            if (bets.length === 0) {
                html += '<div style="padding:8px;">No bets yet.</div>';
            } else {
                bets.forEach(bet => {
                    html += `<div style="display:flex;">
                        <div style="flex:1;">${bet.won ? "Win" : "Lose"}</div>
                        <div style="flex:1;">${bet.won ? "+" : "-"}${bet.amount}</div>
                        <div style="flex:2;">${bet.horse ? bet.horse.name : ""}</div>
                    </div>`;
                });
            }
            betHistoryList.innerHTML = html;
        })
        .catch(() => {
            betHistoryList.innerHTML = "Error loading bet history.";
        });
}

// --- Delete bet history ---
const clearHistoryBtn = document.getElementById('clear-history-btn');
clearHistoryBtn.onclick = () => {
    if (!currentUserId) return;
    if (!confirm("Are you sure you want to delete your entire bet history?")) return;
    fetch(`${API_URL}/bet/history/${currentUserId}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error('Failed to clear history');
            betHistoryList.innerHTML = '<div style="padding:8px;">No bets yet.</div>';
        })
        .catch(error => {
            alert('Failed to clear history: ' + error.message);
        });
};

// --- Balance render ---
function renderBalance() {
    balanceAmountElem.textContent = balance.toFixed(2);
}

// --- Render horse cards ---
function fetchHorses() {
    fetch(`${API_URL}/horse`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to load horse data.');
            return response.json();
        })
        .then(data => {
            horses = getRandomHorsesFrom(data);
            renderHorseCards();
        })
        .catch(err => {
            console.error('Error fetching horses:', err);
            alert('Failed to load horses from server.');
        });
}

function getRandomHorsesFrom(data) {
    const shuffled = [...data].sort(() => 0.5 - Math.random());
    return shuffled.slice(0, 8);
}

function getRandomHorseImage() {
    const index = Math.floor(Math.random() * 8) + 1;
    return `assets/horse${index}.jpg`;
}

function renderHorseCards() {
    cardsContainer.innerHTML = '';
    horses.forEach(h => {
        const card = document.createElement('div');
        card.classList.add('horse-card');
        card.dataset.id = h.id;
        card.innerHTML = `
            <div class="horse-image">
                <img src="${getRandomHorseImage()}" alt="${h.name}" />
            </div>
            <div class="horse-name">${h.name}</div>
            <div class="horse-info">
                Age: ${h.age} <br/>
                Weight: ${h.weight} kg<br/>
                Speed: ${h.speed} <br/>
                Stamina: ${h.temperament || h.stamina}
            </div>`;

        card.onclick = () => {
            document.querySelectorAll('.horse-card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
            selectedHorseId = h.id;
            startRaceContainer.style.display = 'block';
            resultContainer.textContent = '';
        };

        cardsContainer.appendChild(card);
    });
}

// --- Top-up modal logic ---
btnTopup.onclick = () => {
    topupAmountInput.value = '';
    topupModal.style.display = 'flex';
};

topupCancelBtn.onclick = () => {
    topupModal.style.display = 'none';
};

topupNextBtn.onclick = () => {
    const val = parseFloat(topupAmountInput.value);
    if (isNaN(val) || val <= 0) {
        alert('Please enter a valid positive amount.');
        return;
    }
    topupAmount = val;
    topupModal.style.display = 'none';
    blikCodeInput.value = '';
    blikModal.style.display = 'flex';
};

blikSubmitBtn.onclick = () => {
    const blikCode = blikCodeInput.value.trim();
    if (blikCode.length !== 6 || !/^\d{6}$/.test(blikCode)) {
        alert('Please enter a 6-digit BLIK code.');
        return;
    }
    const newBalance = balance + topupAmount;
    const updatedUser = {
        username: currentUser,
        balance: newBalance
    };
    fetch(`${API_URL}/users/${currentUserId}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(updatedUser)
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to update balance');
            return response.json();
        })
        .then(user => {
            balance = user.balance;
            renderBalance();
            blikModal.style.display = 'none';
            alert(`Top-up successful! ${topupAmount.toFixed(2)} PLN added.`);
        })
        .catch(err => {
            alert('Top-up failed: ' + err.message);
        });
};

// --- Race logic ---
startRaceBtn.onclick = () => {
    if (!selectedHorseId) return alert('Please select a horse first.');
    if (balance < 100) return alert('You need at least 100 PLN to place a bet.');
    sidebar.style.width = '0';

    fetch(`${API_URL}/bet/request`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            userId: currentUserId,
            horseId: selectedHorseId,
            amount: 100
        })
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to place bet');
            return fetch(`${API_URL}/race/start`, { method: 'POST' });
        })
        .then(response => {
            if (!response.ok) throw new Error('Race failed');
            return response.json();
        })
        .then(result => {
            const winnerId = result.winningHorseId;
            const winnerName = result.winner;
            balance -= 100;
            if (winnerId === selectedHorseId) balance += 200;

            const updatedUser = {
                username: currentUser,
                balance: balance
            };
            return fetch(`${API_URL}/users/${currentUserId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedUser)
            })
                .then(res => {
                    if (!res.ok) throw new Error('Failed to update balance');
                    return res.json();
                })
                .then(user => {
                    renderBalance();
                    if (winnerId === selectedHorseId) {
                        resultMessage.textContent = `Congratulations! Your horse "${winnerName}" won! You earned 200 PLN.`;
                        resultMessage.style.color = '#4CAF50';
                    } else {
                        resultMessage.textContent = `Sorry, your horse lost. Winner is "${winnerName}". Better luck next time!`;
                        resultMessage.style.color = '#ff4f4f';
                    }
                    resultModal.style.display = 'flex';
                });
        })
        .catch(error => {
            alert('Error during race: ' + error.message);
        })
        .finally(() => {
            selectedHorseId = null;
            document.querySelectorAll('.horse-card').forEach(c => c.classList.remove('selected'));
            startRaceContainer.style.display = 'none';
            fetchHorses();
        });
};

// --- Result modal logic ---
resultOkBtn.onclick = () => {
    resultModal.style.display = 'none';
    resultContainer.textContent = '';
    selectedHorseId = null;
    document.querySelectorAll('.horse-card.selected').forEach(c => c.classList.remove('selected'));
    startRaceContainer.style.display = 'none';
};
