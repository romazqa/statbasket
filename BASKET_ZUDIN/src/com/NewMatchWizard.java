package com;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import com.data.*;
import com.data.Event;

import net.miginfocom.swing.MigLayout;

public class NewMatchWizard extends JRDialog {
    private static final long serialVersionUID = 1L;

    private final ApiClient apiClient = new ApiClient();
    
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton btnNext, btnPrev, btnCancel;

    private JPanel levelPanel, eventPanel, teamPanel, playerPanel, matchPanel;

    private Level newLevel = null;
    private Event newEvent = null;
    private Team newTeam1 = null;
    private Team newTeam2 = null;
    private Matches newMatch = new Matches();

    private List<Player> allPlayersCache;

    private String currentCardName = "levelPanel";

    private JComboBox<Level> levelComboBox;
    private JTextField levelCodField, levelNameField;

    private JComboBox<Event> eventComboBox;
    private JTextField eventCodField, eventNameField, eventLocField, eventYearField;
    private JComboBox<Level> eventLevelComboBox;
    private JLabel eventLevelLabel;

    private JComboBox<Team> team1ComboBox;
    private JTextField team1CodField, team1NameField, team1CityField, team1GenderField;
    private JComboBox<Team> team2ComboBox;
    private JTextField team2CodField, team2NameField, team2CityField, team2GenderField;

    private JList<Player> playerList1;
    private JTextField player1NameField, player1HeieField, player1WeieField, player1RoleField, player1NumField, player1GeedField;
    private JFormattedTextField player1DaterField;
    private JButton btnAddPlayer1;
    
    private JList<Player> playerList2;
    private JTextField player2NameField, player2HeieField, player2WeieField, player2RoleField, player2NumField, player2GeedField;
    private JFormattedTextField player2DaterField;
    private JButton btnAddPlayer2;
    
    private JLabel team1NameLabel, team2NameLabel;

    private JTextField matchPlaygroundField;
    private JFormattedTextField matchDateField;

    public NewMatchWizard(java.awt.Frame parent) {
        super(parent, true);
        setTitle("Новый матч");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        eventLevelLabel = new JLabel("Не выбран");

        createGUI();
        showCard(currentCardName);
        bindListeners();
        loadInitialData();

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    // --- НОВЫЕ МЕТОДЫ ДЛЯ ПЕРЕДАЧИ ИГРОКОВ В МАТЧ ---
    public List<Player> getActivePlayersTeam1() {
        List<Player> list = new ArrayList<>();
        DefaultListModel<Player> model = (DefaultListModel<Player>) playerList1.getModel();
        for (int i = 0; i < model.getSize(); i++) list.add(model.getElementAt(i));
        return list;
    }

    public List<Player> getActivePlayersTeam2() {
        List<Player> list = new ArrayList<>();
        DefaultListModel<Player> model = (DefaultListModel<Player>) playerList2.getModel();
        for (int i = 0; i < model.getSize(); i++) list.add(model.getElementAt(i));
        return list;
    }
    // ------------------------------------------------

    private void loadInitialData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<Level> levels; List<Event> events; List<Team> teams; List<Player> players;
            @Override
            protected Void doInBackground() throws Exception {
                levels = apiClient.getAllLevels(); events = apiClient.getAllEvents();
                teams = apiClient.getAllTeams(); players = apiClient.getAllPlayers();
                return null;
            }
            @Override
            protected void done() {
                try {
                    get(); 
                    for (Level l : levels) { levelComboBox.addItem(l); eventLevelComboBox.addItem(l); }
                    for (Event e : events) eventComboBox.addItem(e);
                    for (Team t : teams) { team1ComboBox.addItem(t); team2ComboBox.addItem(t); }
                    allPlayersCache = players;
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void createGUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        levelPanel = createLevelPanel();
        eventPanel = createEventPanel();
        teamPanel = createTeamPanel();
        playerPanel = createPlayerPanel();
        matchPanel = createMatchPanel();

        cardPanel.add(levelPanel, "levelPanel"); cardPanel.add(eventPanel, "eventPanel");
        cardPanel.add(teamPanel, "teamPanel"); cardPanel.add(playerPanel, "playerPanel");
        cardPanel.add(matchPanel, "matchPanel");

        btnNext = new JButton("Далее"); btnPrev = new JButton("Назад"); btnCancel = new JButton("Отмена");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnPrev); buttonPanel.add(btnNext); buttonPanel.add(btnCancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cardPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createLevelPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        levelComboBox = new JComboBox<>(); 
        panel.add(new JLabel("Выбрать существующий уровень:")); panel.add(levelComboBox, "growx, wrap");
        panel.add(new JLabel("Код (оставьте пустым для нового):")); levelCodField = new JTextField(10); panel.add(levelCodField, "span");
        panel.add(new JLabel("Наименование:")); levelNameField = new JTextField(50); panel.add(levelNameField, "span, wrap");

        JButton btnAddLevel = new JButton("Сохранить/Добавить уровень");
        panel.add(btnAddLevel, "growx, span, wrap");

        btnAddLevel.addActionListener(e -> {
            Level tempLevel = constructLevel(levelCodField, levelNameField);
            if (tempLevel != null) {
                try {
                    newLevel = apiClient.saveLevel(tempLevel); 
                    JOptionPane.showMessageDialog(this, "Уровень сохранен в БД!");
                    levelCodField.setText(String.valueOf(newLevel.getId()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        levelComboBox.addActionListener(e -> {
            Level selected = (Level) levelComboBox.getSelectedItem();
            if (selected != null) {
                levelCodField.setText(String.valueOf(selected.getId()));
                levelNameField.setText(selected.getName());
            }
        });
        return panel;
    }

    private JPanel createEventPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        eventComboBox = new JComboBox<>();
        panel.add(new JLabel("Выбрать соревнование:")); panel.add(eventComboBox, "growx, wrap");
        panel.add(new JLabel("Код:")); eventCodField = new JTextField(6); panel.add(eventCodField, "span");
        panel.add(new JLabel("Название:")); eventNameField = new JTextField(20); panel.add(eventNameField, "span");
        panel.add(new JLabel("Место:")); eventLocField = new JTextField(30); panel.add(eventLocField, "span");
        panel.add(new JLabel("Год:")); eventYearField = new JTextField(10); panel.add(eventYearField, "span, wrap");

        eventLevelComboBox = new JComboBox<>();
        JButton btnAddEvent = new JButton("Сохранить/Добавить соревнование");
        panel.add(btnAddEvent, "growx, span, wrap");

        btnAddEvent.addActionListener(e -> {
            Event tempEvent = constructEvent(eventCodField, eventNameField, eventLocField, eventYearField, newLevel);
            if (tempEvent != null) {
                try {
                    newEvent = apiClient.saveEvent(tempEvent);
                    JOptionPane.showMessageDialog(this, "Соревнование сохранено в БД!");
                    eventCodField.setText(String.valueOf(newEvent.getId()));
                } catch (Exception ex) {}
            }
        });

        eventComboBox.addActionListener(e -> {
            Event selected = (Event) eventComboBox.getSelectedItem();
            if (selected != null) {
                eventCodField.setText(String.valueOf(selected.getId()));
                eventNameField.setText(selected.getName());
                eventLocField.setText(selected.getLocation());
                eventYearField.setText(String.valueOf(selected.getYear()));
            }
        });
        return panel;
    }

    private JPanel createTeamPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[grow][][grow]", "[]5[]10[]"));
        JPanel team1Panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        JPanel team2Panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));

        team1ComboBox = new JComboBox<>();
        team1Panel.add(new JLabel("Выбрать команду 1:")); team1Panel.add(team1ComboBox, "growx, wrap");
        team1Panel.add(new JLabel("Код:")); team1CodField = new JTextField(10); team1Panel.add(team1CodField, "span");
        team1Panel.add(new JLabel("Наименование:")); team1NameField = new JTextField(40); team1Panel.add(team1NameField, "span");
        team1Panel.add(new JLabel("Город:")); team1CityField = new JTextField(10); team1Panel.add(team1CityField, "span");
        team1Panel.add(new JLabel("Пол команды:")); team1GenderField = new JTextField(10); team1Panel.add(team1GenderField, "span, wrap");

        JButton btnAddTeam1 = new JButton("Сохранить/Добавить команду 1");
        team1Panel.add(btnAddTeam1, "growx, span, wrap");
        btnAddTeam1.addActionListener(e -> {
            Team temp = constructTeam(team1CodField, team1NameField, team1CityField, team1GenderField);
            if (temp != null) {
                try { newTeam1 = apiClient.saveTeam(temp); JOptionPane.showMessageDialog(this, "Команда 1 сохранена!"); team1CodField.setText(String.valueOf(newTeam1.getId()));} catch (Exception ex) {}
            }
        });

        team2ComboBox = new JComboBox<>();
        team2Panel.add(new JLabel("Выбрать команду 2:")); team2Panel.add(team2ComboBox, "growx, wrap");
        team2Panel.add(new JLabel("Код:")); team2CodField = new JTextField(10); team2Panel.add(team2CodField, "span");
        team2Panel.add(new JLabel("Наименование:")); team2NameField = new JTextField(40); team2Panel.add(team2NameField, "span");
        team2Panel.add(new JLabel("Город:")); team2CityField = new JTextField(10); team2Panel.add(team2CityField, "span");
        team2Panel.add(new JLabel("Пол команды:")); team2GenderField = new JTextField(10); team2Panel.add(team2GenderField, "span, wrap");

        JButton btnAddTeam2 = new JButton("Сохранить/Добавить команду 2");
        team2Panel.add(btnAddTeam2, "growx, span, wrap");
        btnAddTeam2.addActionListener(e -> {
            Team temp = constructTeam(team2CodField, team2NameField, team2CityField, team2GenderField);
            if (temp != null) {
                try { newTeam2 = apiClient.saveTeam(temp); JOptionPane.showMessageDialog(this, "Команда 2 сохранена!"); team2CodField.setText(String.valueOf(newTeam2.getId()));} catch (Exception ex) {}
            }
        });

        team1ComboBox.addActionListener(e -> {
            Team sel = (Team) team1ComboBox.getSelectedItem();
            if (sel != null) { team1CodField.setText(String.valueOf(sel.getId())); team1NameField.setText(sel.getName()); team1CityField.setText(sel.getCity()); team1GenderField.setText(sel.getGender()); }
        });
        team2ComboBox.addActionListener(e -> {
            Team sel = (Team) team2ComboBox.getSelectedItem();
            if (sel != null) { team2CodField.setText(String.valueOf(sel.getId())); team2NameField.setText(sel.getName()); team2CityField.setText(sel.getCity()); team2GenderField.setText(sel.getGender()); }
        });

        panel.add(team1Panel, "grow"); panel.add(new JLabel(" "), "grow"); panel.add(team2Panel, "grow");
        return panel;
    }

    private JPanel createPlayerPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[grow][][grow]", "[]5[]10[]"));
        JPanel p1 = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        JPanel p2 = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));

        player1NameField = new JTextField(20); player1DaterField = new JFormattedTextField(createFormatter("##-##-####")); player1HeieField = new JTextField(10); player1WeieField = new JTextField(10); player1RoleField = new JTextField(15); player1NumField = new JTextField(5); player1GeedField = new JTextField(5);
        playerList1 = new JList<>();
        team1NameLabel = new JLabel("Не выбрана");
        p1.add(new JLabel("Команда:"), "growx, wrap"); p1.add(team1NameLabel, "growx, wrap");
        p1.add(new JLabel("Участвуют в матче:")); p1.add(new JScrollPane(playerList1), "growx, span, wrap");
        addPlayerFields(p1, player1NameField, player1DaterField, player1HeieField, player1WeieField, player1RoleField, player1NumField, player1GeedField);
        btnAddPlayer1 = new JButton("Создать и добавить игрока"); p1.add(btnAddPlayer1, "growx, span, wrap");
        JButton btnDeletePlayer1 = new JButton("Убрать игрока из матча"); p1.add(btnDeletePlayer1, "growx, span, wrap");

        player2NameField = new JTextField(20); player2DaterField = new JFormattedTextField(createFormatter("##-##-####")); player2HeieField = new JTextField(10); player2WeieField = new JTextField(10); player2RoleField = new JTextField(15); player2NumField = new JTextField(5); player2GeedField = new JTextField(5);
        playerList2 = new JList<>();
        team2NameLabel = new JLabel("Не выбрана");
        p2.add(new JLabel("Команда:"), "growx, wrap"); p2.add(team2NameLabel, "growx, wrap");
        p2.add(new JLabel("Участвуют в матче:")); p2.add(new JScrollPane(playerList2), "growx, span, wrap");
        addPlayerFields(p2, player2NameField, player2DaterField, player2HeieField, player2WeieField, player2RoleField, player2NumField, player2GeedField);
        btnAddPlayer2 = new JButton("Создать и добавить игрока"); p2.add(btnAddPlayer2, "growx, span, wrap");
        JButton btnDeletePlayer2 = new JButton("Убрать игрока из матча"); p2.add(btnDeletePlayer2, "growx, span, wrap");

        // ЛОГИКА ИГРОКОВ: Сохраняем в БД сразу!
        btnAddPlayer1.addActionListener(e -> {
            Player p = constructPlayer(player1NameField, player1DaterField, player1HeieField, player1WeieField, player1RoleField, player1NumField, player1GeedField);
            if (p != null) {
                p.setTeam(newTeam1);
                try {
                    p = apiClient.savePlayer(p); // Сохраняем в БД и получаем ID
                    ((DefaultListModel<Player>) playerList1.getModel()).addElement(p); // Добавляем в текущий матч
                    clearPlayerFields(player1NameField, player1DaterField, player1HeieField, player1WeieField, player1RoleField, player1NumField, player1GeedField);
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage()); }
            }
        });

        btnAddPlayer2.addActionListener(e -> {
            Player p = constructPlayer(player2NameField, player2DaterField, player2HeieField, player2WeieField, player2RoleField, player2NumField, player2GeedField);
            if (p != null) {
                p.setTeam(newTeam2);
                try {
                    p = apiClient.savePlayer(p);
                    ((DefaultListModel<Player>) playerList2.getModel()).addElement(p);
                    clearPlayerFields(player2NameField, player2DaterField, player2HeieField, player2WeieField, player2RoleField, player2NumField, player2GeedField);
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage()); }
            }
        });

        // УДАЛЕНИЕ: Просто убираем из JList (из матча), но НЕ из БД!
        btnDeletePlayer1.addActionListener(e -> {
            int idx = playerList1.getSelectedIndex();
            if (idx != -1) ((DefaultListModel<Player>) playerList1.getModel()).remove(idx);
        });

        btnDeletePlayer2.addActionListener(e -> {
            int idx = playerList2.getSelectedIndex();
            if (idx != -1) ((DefaultListModel<Player>) playerList2.getModel()).remove(idx);
        });

        panel.add(p1, "grow"); panel.add(new JLabel(" "), "grow"); panel.add(p2, "grow");
        return panel;
    }

    private void addPlayerFields(JPanel p, JTextField name, JTextField date, JTextField h, JTextField w, JTextField role, JTextField num, JTextField gen) {
        p.add(new JLabel("ФИО:")); p.add(name, "span");
        p.add(new JLabel("Дата рожд.:")); p.add(date, "span");
        p.add(new JLabel("Рост:")); p.add(h, "span");
        p.add(new JLabel("Вес:")); p.add(w, "span");
        p.add(new JLabel("Роль:")); p.add(role, "span");
        p.add(new JLabel("Номер:")); p.add(num, "span");
        p.add(new JLabel("Пол:")); p.add(gen, "span, wrap");
    }

    private void updatePlayerLists() {
        team1NameLabel.setText(newTeam1 != null ? newTeam1.getName() : "Не выбрана");
        team2NameLabel.setText(newTeam2 != null ? newTeam2.getName() : "Не выбрана");

        if (newTeam1 != null && newTeam1.getPlayers() != null) {
            DefaultListModel<Player> model1 = new DefaultListModel<>();
            for (Player player : newTeam1.getPlayers()) model1.addElement(player);
            playerList1.setModel(model1);
        }

        if (newTeam2 != null && newTeam2.getPlayers() != null) {
            DefaultListModel<Player> model2 = new DefaultListModel<>();
            for (Player player : newTeam2.getPlayers()) model2.addElement(player);
            playerList2.setModel(model2);
        }
    }

    private JPanel createMatchPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        matchPlaygroundField = new JTextField(50);
        matchDateField = new JFormattedTextField(createFormatter("##-##-####"));
        panel.add(new JLabel("Поле проведения:")); panel.add(matchPlaygroundField, "span, wrap");
        panel.add(new JLabel("Дата проведения:")); panel.add(matchDateField, "span, wrap");
        return panel;
    }

    private Level constructLevel(JTextField edCod, JTextField edName) {
        try {
            Level level = new Level();
            level.setId(edCod.getText().equals("") ? null : Integer.valueOf(edCod.getText()));
            level.setName(edName.getText());
            return level;
        } catch (Exception ex) { return null; }
    }

    private Event constructEvent(JTextField edCod, JTextField edName, JTextField edLoc, JTextField edYear, Level level) {
        try {
            Event event = new Event();
            event.setId(edCod.getText().equals("") ? null : Integer.valueOf(edCod.getText()));
            event.setName(edName.getText()); event.setLocation(edLoc.getText());
            event.setYear(edYear.getText().equals("") ? null : Integer.valueOf(edYear.getText()));
            event.setCompetitionLevel(level);
            return event;
        } catch (Exception ex) { return null; }
    }

    private Team constructTeam(JTextField edKod, JTextField edName, JTextField edCity, JTextField edGender) {
        try {
            Team team = new Team();
            team.setId(edKod.getText().equals("") ? null : Integer.valueOf(edKod.getText()));
            team.setName(edName.getText()); team.setCity(edCity.getText()); team.setGender(edGender.getText());
            return team;
        } catch (Exception ex) { return null; }
    }

    private Player constructPlayer(JTextField edName, JFormattedTextField edDater, JTextField edHeie, JTextField edWeie, JTextField edRole, JTextField edNum, JTextField edGeed) {
        try {
            Player player = new Player();
            player.setName(edName.getText());
            String dateText = edDater.getText();
            if (dateText.substring(0, 1).trim().equals("")) player.setBirthday(null);
            else player.setBirthday(LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            player.setHeight(edHeie.getText().equals("") ? null : Integer.valueOf(edHeie.getText()));
            player.setWeight(edWeie.getText().equals("") ? null : Integer.valueOf(edWeie.getText()));
            player.setRole(edRole.getText());
            player.setGameNumber(edNum.getText().equals("") ? null : Integer.valueOf(edNum.getText()));
            player.setGender(edGeed.getText());
            return player;
        } catch (Exception ex) { return null; }
    }

    private void clearPlayerFields(JTextField n, JFormattedTextField d, JTextField h, JTextField w, JTextField r, JTextField num, JTextField g) {
        n.setText(""); d.setText(""); h.setText(""); w.setText(""); r.setText(""); num.setText(""); g.setText("");
    }

    private void bindListeners() {
        btnNext.addActionListener(e -> {
            switch (currentCardName) {
                case "levelPanel":
                    newLevel = (Level) levelComboBox.getSelectedItem();
                    showCard("eventPanel");
                    break;
                case "eventPanel":
                    newEvent = (Event) eventComboBox.getSelectedItem();
                    showCard("teamPanel");
                    break;
                case "teamPanel":
                    newTeam1 = (Team) team1ComboBox.getSelectedItem();
                    newTeam2 = (Team) team2ComboBox.getSelectedItem();
                    updatePlayerLists();
                    showCard("playerPanel");
                    break;
                case "playerPanel":
                    newMatch.setTeam1(newTeam1);
                    newMatch.setTeam2(newTeam2);
                    newMatch.setEvent(newEvent);
                    showCard("matchPanel");
                    break;
                case "matchPanel":
                    newMatch.setPlayground(matchPlaygroundField.getText());
                    newMatch.setTeam1Score(0); newMatch.setTeam2Score(0);
                    try {
                        String dateText = matchDateField.getText();
                        if (dateText.substring(0, 1).trim().equals("")) newMatch.setDate(null);
                        else newMatch.setDate(LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Неверная дата!"); return; }

                    try {
                        newMatch = apiClient.saveMatch(newMatch);
                        setDialogResult(JDialogResult.OK);
                        close();
                    } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Ошибка сети: " + ex.getMessage()); }
                    break;
            }
        });

        btnPrev.addActionListener(e -> {
            switch (currentCardName) {
                case "eventPanel": showCard("levelPanel"); break;
                case "teamPanel": showCard("eventPanel"); break;
                case "playerPanel": showCard("teamPanel"); break;
                case "matchPanel": showCard("playerPanel"); break;
            }
        });

        btnCancel.addActionListener(e -> { setDialogResult(JDialogResult.Cancel); close(); });
    }

    private void showCard(String cardName) {
        cardLayout.show(cardPanel, cardName);
        currentCardName = cardName;
        btnPrev.setEnabled(!currentCardName.equals("levelPanel"));
        btnNext.setText(currentCardName.equals("matchPanel") ? "Завершить" : "Далее");
    }

    protected MaskFormatter createFormatter(String s) {
        try { return new MaskFormatter(s); } catch (Exception exc) { return null; }
    }

    public Matches getNewMatch() { return newMatch; }
}