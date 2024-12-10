package com.example.mycoach;

public class ClientRequest {
    private String requestId; // ID документа запроса
    private String trainerId;
    private String clientId;
    private String clientName;

    // ... (конструктор, геттеры, сеттеры)
    public ClientRequest(String requestId, String trainerId, String clientId, String clientName) {
        this.requestId = requestId;
        this.trainerId = trainerId;
        this.clientId = clientId;
        this.clientName = clientName;
    }

    // Геттеры
    public String getRequestId() {
        return requestId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }
}