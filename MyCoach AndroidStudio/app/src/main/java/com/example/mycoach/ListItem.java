package com.example.mycoach;

import java.io.Serializable;

public class ListItem implements Serializable {
    private String title;
    private String description;
    private String date; // Добавьте другие поля, если необходимо
    private String status; // Например, статус задачи
    // ... другие поля


    // Пустой конструктор необходим для Firestore
    public ListItem() {}

    // Конструктор с параметрами
    public ListItem(String title, String description, String date, String status /* , ... другие параметры */) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.status = status;
        // ... инициализация других полей
    }

    // Геттеры и сеттеры для всех полей
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // ... геттеры и сеттеры для других полей

}