# Table Tennis Pro - Web Version 🏓

A responsive web application for tracking table tennis matches, managing players, organizing tournaments, and viewing statistics. **Optimized for iPad and mobile devices!**

## ✨ Features

- **Player Management**: Add, view, and delete players
- **Live Score Tracking**: Real-time match scoring with Best of 3/5/7 formats
- **Tournament Creation**: Organize tournaments with multiple players
- **Statistics Dashboard**: View player rankings, win rates, and match history
- **Offline Support**: All data stored locally in your browser
- **Touch-Friendly**: Optimized for iPad and mobile devices
- **Responsive Design**: Works on all screen sizes

## 🚀 How to Use

### On Your Computer

1. Simply open `index.html` in any modern web browser
2. Or use a local server (recommended):
   ```bash
   # Using Python 3
   python -m http.server 8000
   
   # Using Node.js (if you have http-server installed)
   npx http-server
   ```
3. Navigate to `http://localhost:8000` in your browser

### On Your iPad

**Option 1: Using a Local Server (Recommended)**
1. Start a local server on your computer (see above)
2. Find your computer's IP address:
   - Windows: Run `ipconfig` in Command Prompt, look for IPv4 Address
   - Mac/Linux: Run `ifconfig` or `ip addr`
3. On your iPad, open Safari and go to: `http://YOUR_COMPUTER_IP:8000`
   - Example: `http://192.168.1.100:8000`

**Option 2: Using iCloud Drive**
1. Copy the entire `web` folder to iCloud Drive
2. On your iPad, open the Files app
3. Navigate to the folder and tap `index.html`
4. Choose "Open in Safari"

**Option 3: Deploy Online (Best for Long-term Use)**
- Deploy to GitHub Pages, Netlify, or Vercel (free)
- Access from anywhere with the URL

## 📱 iPad Tips

- **Add to Home Screen**: In Safari, tap the Share button → "Add to Home Screen" for app-like experience
- **Full Screen**: The app will run in full-screen mode when added to home screen
- **Touch Controls**: All buttons are optimized for touch input
- **Landscape Mode**: Works great in both portrait and landscape orientations

## 🎮 Quick Start Guide

1. **Add Players**: Go to the "Players" tab and click "+ Add Player"
2. **Start a Match**: 
   - Go to "Score Tracker" tab
   - Select two players
   - Choose match format (Best of 3, 5, or 7)
   - Click "Start Match"
   - Use +1/-1 buttons to track scores
   - Click "Finish Match" when done
3. **Create Tournament**: Go to "Tournaments" tab and click "+ Create Tournament"
4. **View Stats**: Check the "Statistics" tab to see player rankings

## 💾 Data Storage

- All data is stored in your browser's localStorage
- Data persists between sessions
- Each device/browser has its own separate data
- To backup data: Use browser's developer tools to export localStorage
- To clear data: Clear browser data/cache

## 🎨 Features

### Player Management
- Add unlimited players
- Track wins, losses, and win rates
- Delete players when needed

### Score Tracker
- Beautiful color-coded player panels (Blue vs Red)
- Large, easy-to-read scores
- Touch-friendly +1/-1 buttons
- Automatic match completion detection
- Stats automatically updated after each match

### Tournaments
- Create tournaments with custom names
- Select multiple players
- Choose match format
- Track tournament progress

### Statistics
- Sortable player rankings
- Win/Loss records
- Win rate percentages
- Games won/lost tracking
- Comprehensive match history

## 🌐 Browser Compatibility

- ✅ Safari (iOS/iPadOS)
- ✅ Chrome
- ✅ Firefox
- ✅ Edge
- ✅ Any modern browser with localStorage support

## 🔧 Technical Details

- **Pure HTML/CSS/JavaScript** - No frameworks required
- **localStorage API** for data persistence
- **Responsive CSS Grid & Flexbox** layouts
- **Touch-optimized** interface
- **No backend required** - runs entirely in the browser

## 📝 Notes

- This is a client-side only application
- Data is stored locally on each device
- For multi-device sync, consider deploying online and using a backend service
- The app works offline once loaded

## 🎯 Differences from Java Version

- ✅ Works on iPad/mobile devices
- ✅ No installation required
- ✅ Cross-platform (any device with a browser)
- ✅ Modern, touch-friendly interface
- ⚠️ Tournament bracket visualization simplified
- ⚠️ Data stored per-device (not centralized)

## 🚀 Deployment Options

### GitHub Pages (Free)
1. Create a GitHub repository
2. Upload the `web` folder contents
3. Enable GitHub Pages in repository settings
4. Access via `https://yourusername.github.io/repository-name`

### Netlify (Free)
1. Drag and drop the `web` folder to Netlify
2. Get instant URL
3. Custom domain support

### Vercel (Free)
1. Connect your GitHub repository
2. Auto-deploy on every commit
3. Custom domain support

---

**Enjoy tracking your table tennis matches! 🏓**

*Built with ❤️ for table tennis enthusiasts*
