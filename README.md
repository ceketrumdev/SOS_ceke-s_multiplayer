# SOS — ceke’s multiplayer (Songs of Syx)

A **co-op multiplayer** mod for *Songs of Syx* (WIP).

> ⚠️ **Status: not functional in-game yet**
>
> The repository already contains a technical foundation (multiplayer menu, client/server, network packets, co-op rules), but the actual **game state synchronization is still not implemented** (e.g. the server tick currently sends an empty payload / TODO).  
> Progress is happening **little by little**, but it’s **not playable as a real multiplayer mod yet**.  
> (Example: server tick uses `new byte[0]` + TODO.) 

---

## Goal
Build a **co-op** experience where one player hosts and others join a shared world (no PvP for now). 

---

## What’s already in the repo (code-side)
### Mod base + game mode
- Mod entry point: `MultiplayerMod` (loads a **Co-op** mode by default). 
- A “Co-op” mode that registers multiple rules (mouse sync, speed sync, etc.). 

### Multiplayer menu (lobby)
- A dedicated menu with **Host** / **Join**, save selection, and “New Game”. 

### Networking (client/server skeleton)
- A **HostServer** (authoritative server) that ticks and broadcasts packets. 
- A **GameClient** that connects, sends some packets, and receives updates. 

### Co-op sync pieces (present, but not playable yet)
- Speed sync logic (detects changes and sends `PacketSpeedChange`). 
- Cursor position syncing (send/receive mouse position). 
- Additional registered rules (ping/resources) inside the Co-op module. 

### Inputs & chat: included, but **not functional in-game**
- Packet/classes exist (`PacketPlayerInput`, `PacketChat`) and the client can send them. 
- But in-game integration is not finished (e.g. chat display is marked TODO on the client side, and core state sync is still stubbed). 

---

## What’s missing (high level)
- A real **game state synchronization** layer (instead of an empty `PacketGameState`).
- Completing authoritative handling of actions on the host + proper client updates (inputs, UI, etc.).  
   

---
