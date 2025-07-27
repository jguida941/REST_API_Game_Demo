// MatchmakingTicket.java
package com.gamingroom.gameauth.halo.models;

import java.time.LocalDateTime;
import java.util.List;

public class MatchmakingTicket {
    private String ticketId;
    private String playlist;
    private List<Long> playerIds;
    private String status; // "QUEUED", "SEARCHING", "FOUND", "CANCELLED"
    private String serverIp;
    private Integer serverPort;
    private LocalDateTime createdAt;
    
    // Constructor
    public MatchmakingTicket() {
        this.createdAt = LocalDateTime.now();
        this.status = "QUEUED";
    }
    
    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}