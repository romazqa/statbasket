package com.example.mycoach;

import java.io.Serializable;
import java.util.List;

public class TrainingPlanItem implements Serializable {
    private String name;
    private String date;
    private String description;
    private List<Exercise> exercises;
    private String status;
    private String comments;
    private String id;


    // Пустой конструктор необходим для Firestore
    public TrainingPlanItem() {}

    // Конструктор
    public TrainingPlanItem(String name, String date, String description, List<Exercise> exercises, String status, String comments) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.exercises = exercises;
        this.status = status;
        this.comments = comments;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public String getStatus() {
        return status;
    }

    public String getComments() {
        return comments;
    }

    public String getId() {
        return id;
    }

    // Сеттеры
    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Вложенный класс для представления упражнения
    public static class Exercise implements Serializable {
        private String name;
        private int sets;
        private int reps;
        private String weight;
        private int restTime;
        private boolean completed;

        // Пустой конструктор
        public Exercise() {}

        // Конструктор
        public Exercise(String name, int sets, int reps, String weight, int restTime) {
            this.name = name;
            this.sets = sets;
            this.reps = reps;
            this.weight = weight;
            this.restTime = restTime;
        }

        // Геттеры и сеттеры
        // ... (аналогично как в TrainingPlanItem)

        public String getName() {
            return name;
        }

        public int getSets() {
            return sets;
        }

        public int getReps() {
            return reps;
        }

        public String getWeight() {
            return weight;
        }

        public int getRestTime() {
            return restTime;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSets(int sets) {
            this.sets = sets;
        }

        public void setReps(int reps) {
            this.reps = reps;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public void setRestTime(int restTime) {
            this.restTime = restTime;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}
