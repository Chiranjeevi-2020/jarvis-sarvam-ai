# ⚡ Jarvis AI — Personal College Assistant (Android)

An **offline-first, semi-online personal assistant app** for college students, powered by **Sarvam AI** for native Indian language speech and intelligence (Tamil, English, and Tanglish).

---

## 🌟 Key Features

1. **WhatsApp-Style Conversational Voice / Chat**:
   - Talk to Jarvis by holding the **Voice Orb Mic** button or typing.
   - Speak in **Tamil, English, or Tanglish** (*"இன்னைக்கு OS assignment deadline இருக்கு, 6 மணிக்கு DSA practice schedule பண்ணு"*).
   - Powered by Sarvam **Saaras v4 STT**, **Sarvam-105B Reasoning**, and **Bulbul v4 TTS** voice responses.

2. **100% Offline-First Architecture (SQLite / Room)**:
   - **Coursework & Deadlines Radar**: Tracks pending assignments, due dates, priority tags, and marks completed items offline.
   - **DSA & Problem Solving Forge**: Topic matrix (Arrays, Two Pointers, Trees, Graphs, DP), problem counters, active streak counter, and 1-tap `+1 Solved` logging.
   - **Subject Study Progress**: Progress bars (% completed) for each semester subject and unit-level syllabus tracking.
   - **Goal Roadmap**: Links your daily routine to your **Long-Term North Star** (e.g. SDE 2027) via **Short-Term Weekly Sprints**.
   - **Offline Alarms**: Triggers exact notifications for study sessions and deadlines even with Wi-Fi/Mobile Data turned off.

3. **Semi-Online Intelligence**:
   - Works fully offline for viewing and logging schedules, deadlines, and DSA problems.
   - Connects to Sarvam AI when voice recognition or smart AI schedule restructuring is needed.

---

## 🚀 How to Build & Install the APK onto Your Phone (Free in 2 Minutes)

Since you don't need to install heavy Android SDKs on your computer, this project comes with a **GitHub Actions cloud build workflow**.

### Step 1: Initialize Git and Push to GitHub
Open PowerShell in this folder (`c:\Users\chira\Downloads\sarvam`) and run:

```powershell
git init
git add .
git commit -m "Initial commit of Jarvis AI Android app"
git branch -M main
```

Create a new repository on [GitHub](https://github.com/new) (e.g., `jarvis-sarvam-ai`), then run:

```powershell
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/jarvis-sarvam-ai.git
git push -u origin main
```

### Step 2: Cloud APK Compilation
1. Go to your GitHub repository in your browser.
2. Click on the **Actions** tab.
3. You will see the **"Build Jarvis Android APK"** workflow running automatically.
4. In about 2 minutes, the build will complete with a green checkmark.

### Step 3: Download & Install on Your Phone
1. Click on the completed workflow run.
2. Scroll to the bottom to find the **Artifacts** section: **`Jarvis-AI-Personal-Assistant`**.
3. Download the zip file directly onto your Android phone (or download on PC and send to phone).
4. Unzip and tap `app-debug.apk` to install!
*(If prompted, allow "Install unknown apps" in Android settings).*

---

## ⚙️ Setting Up Sarvam AI in the App

1. Open the app on your phone.
2. Tap the **Settings (gear)** icon at the top right.
3. Paste your **Sarvam AI Subscription Key** from [dashboard.sarvam.ai](https://dashboard.sarvam.ai).
4. Choose your preferred voice language:
   - `ta-IN` (Tamil)
   - `en-IN` (Indian English)
   - `hi-IN` (Hindi)
5. Tap **Save Settings**.

---

## 🎙️ Sample Voice Commands to Try

- **Tamil / Tanglish**:
  - *"இன்னைக்கு என்ன assignment deadlines இருக்கு?"* (What assignment deadlines are there today?)
  - *"DSA Trees practice session இன்னைக்கு சாயங்காலம் 5 மணிக்கு schedule பண்ணு"* (Schedule DSA Trees practice at 5 PM today)
  - *"OS unit 2 padichuten, progress update pannu"* (Completed OS unit 2, update progress)

- **English**:
  - *"Add an Operating Systems CPU simulator assignment due in 3 days with high priority."*
  - *"I just solved 2 Graph problems, log them to my streak."*
  - *"Give me my daily morning briefing."*
