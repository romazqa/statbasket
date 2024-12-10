package com;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import com.data.*;
import com.data.Event;

import net.miginfocom.swing.MigLayout;



public class NewMatchWizard extends JRDialog {
    private static final long serialVersionUID = 1L;

    private DBManager manager;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton btnNext, btnPrev, btnCancel;

    // Panels for each step
    private JPanel levelPanel;
    private JPanel eventPanel;
    private JPanel teamPanel;
    private JPanel playerPanel;
    private JPanel matchPanel;

    // Data objects
    private Level newLevel = null;
    private Event newEvent = null;
    private Team newTeam1 = null;
    private Team newTeam2 = null;
    private ArrayList<Player> newPlayersTeam1 = new ArrayList<>();
    private ArrayList<Player> newPlayersTeam2 = new ArrayList<>();
    private Matches newMatch = new Matches();

    private String currentCardName = "levelPanel";

    // Components for levelPanel
    private JComboBox<Level> levelComboBox;
    private JTextField levelCodField;
    private JTextField levelNameField;

    // Components for eventPanel
    private JComboBox<Event> eventComboBox;
    private JTextField eventCodField;
    private JTextField eventNameField;
    private JTextField eventLocField;
    private JTextField eventYearField;
    private JComboBox<Level> eventLevelComboBox;
    private JLabel eventLevelLabel;

    // Components for teamPanel
    private JComboBox<Team> team1ComboBox;
    private JTextField team1CodField;
    private JTextField team1NameField;
    private JTextField team1CityField;
    private JTextField team1GenderField;
    private JComboBox<Team> team2ComboBox;
    private JTextField team2CodField;
    private JTextField team2NameField;
    private JTextField team2CityField;
    private JTextField team2GenderField;

    // Components for playerPanel
    private JList<Player> playerList1;
    private JTextField player1NameField;
    private JFormattedTextField player1DaterField;
    private JTextField player1HeieField;
    private JTextField player1WeieField;
    private JTextField player1RoleField;
    private JTextField player1NumField;
    private JTextField player1GeedField;
    private JButton btnAddPlayer1;
    private JList<Player> playerList2;
    private JTextField player2NameField;
    private JFormattedTextField player2DaterField;
    private JTextField player2HeieField;
    private JTextField player2WeieField;
    private JTextField player2RoleField;
    private JTextField player2NumField;
    private JTextField player2GeedField;
    private JButton btnAddPlayer2;
    private JComboBox<Team> team1PlayerComboBox;
    private JComboBox<Team> team2PlayerComboBox;
    private JLabel team1NameLabel;
    private JLabel team2NameLabel;

    // Components for matchPanel
    private JTextField matchPlaygroundField;
    private JFormattedTextField matchDateField;
    

    public NewMatchWizard(Window parent, DBManager manager) {
        this.manager = manager;
        setTitle("Новый матч");
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        eventLevelLabel = new JLabel("Не выбран"); 

        // Create the GUI
        createGUI();
        // Initialize the first step
        showCard(currentCardName);
        // Bind listeners
        bindListeners();

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void createGUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        levelPanel = createLevelPanel();
        eventPanel = createEventPanel();
        teamPanel = createTeamPanel();
        playerPanel = createPlayerPanel();
        matchPanel = createMatchPanel();

        cardPanel.add(levelPanel, "levelPanel");
        cardPanel.add(eventPanel, "eventPanel");
        cardPanel.add(teamPanel, "teamPanel");
        cardPanel.add(playerPanel, "playerPanel");
        cardPanel.add(matchPanel, "matchPanel");

        btnNext = new JButton("Далее");
        btnPrev = new JButton("Назад");
        btnCancel = new JButton("Отмена");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnPrev);
        buttonPanel.add(btnNext);
        buttonPanel.add(btnCancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cardPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    
    private JPanel createLevelPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));

        levelComboBox = new JComboBox<>(manager.loadLevelForCmb().toArray(new Level[0])); // Сначала JComboBox
        panel.add(new JLabel("Выбрать существующий уровень:"));
        panel.add(levelComboBox, "growx, wrap");

        panel.add(new JLabel("Или создать новый:"));
        panel.add(new JLabel("Код:"));
        levelCodField = new JTextField(10);
        panel.add(levelCodField, "span");
        panel.add(new JLabel("Наименование:"));
        levelNameField = new JTextField(50); 
        panel.add(levelNameField, "span, wrap");

     // Кнопка "Добавить уровень"
        JButton btnAddLevel = new JButton("Добавить уровень");
        panel.add(btnAddLevel, "growx, span, wrap");

        // Обработчик для кнопки "Добавить уровень"
        btnAddLevel.addActionListener(e -> {
            newLevel = constructLevel(levelCodField, levelNameField);
            if (newLevel != null && manager.addLevel(newLevel)) {
                System.out.println("Новый уровень добавлен в БД");
                levelComboBox.addItem(newLevel);
                levelComboBox.setSelectedItem(newLevel);
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении уровня в БД", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик для ComboBox "Уровень"
        levelComboBox.addActionListener(e -> {
            Level selectedLevel = (Level) levelComboBox.getSelectedItem();
            if (selectedLevel != null) {
                levelCodField.setText(selectedLevel.getKod().toString());
                levelNameField.setText(selectedLevel.getName());
            }
        });

        return panel;
    }

    private JPanel createEventPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));

        eventComboBox = new JComboBox<>(manager.loadEventtForCmb().toArray(new Event[0]));
        panel.add(new JLabel("Выбрать существующее соревнование:"));
        panel.add(eventComboBox, "growx, wrap");

        panel.add(new JLabel("Или создать новое:"));
        panel.add(new JLabel("Код:"));
        eventCodField = new JTextField(6);
        panel.add(eventCodField, "span");
        panel.add(new JLabel("Название:"));
        eventNameField = new JTextField(20);
        panel.add(eventNameField, "span");
        panel.add(new JLabel("Место:"));
        eventLocField = new JTextField(30);
        panel.add(eventLocField, "span");
        //panel.add(new JLabel("Уровень соревнования:"));
        //eventLevelLabel = new JLabel(newLevel != null ? newLevel.getName() : "Не выбран");
        //panel.add(eventLevelLabel, "growx, wrap");
        panel.add(new JLabel("Год:"));
        eventYearField = new JTextField(10);
        panel.add(eventYearField, "span, wrap");
        
        eventLevelComboBox = new JComboBox<>(); 
        updateEventLevelComboBox(eventLevelComboBox);

        // Кнопка "Добавить соревнование"
        JButton btnAddEvent = new JButton("Добавить соревнование");
        panel.add(btnAddEvent, "growx, span, wrap");

        // Обработчик для кнопки "Добавить соревнование"
        btnAddEvent.addActionListener(e -> {
            newEvent = constructEvent(eventCodField, eventNameField, eventLocField, eventYearField, newLevel);
            if (newEvent != null && manager.addEventt(newEvent)) {
                System.out.println("Новое событие добавлено в БД");
                eventComboBox.addItem(newEvent);
                eventComboBox.setSelectedItem(newEvent);
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении события в БД", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        eventComboBox.addActionListener(e -> {
            Event selectedEvent = (Event) eventComboBox.getSelectedItem();
            if (selectedEvent != null) {
                eventCodField.setText(selectedEvent.getId_event().toString());
                eventNameField.setText(selectedEvent.getName_event());
                eventLocField.setText(selectedEvent.getLocationn());
                eventYearField.setText(selectedEvent.getYear_event().toString());
                eventLevelComboBox.setSelectedItem(selectedEvent.getLevel());
            }
        });

        return panel;
    }

    private void updateEventLevelComboBox(JComboBox<Level> cmbLevel) {
        if (newLevel != null) {
            cmbLevel.addItem(newLevel);
            cmbLevel.setSelectedItem(newLevel);
        } else {
            cmbLevel.setModel(new DefaultComboBoxModel<>(manager.loadLevelForCmb().toArray(new Level[0])));
        }
    }

    private JPanel createTeamPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[grow][][grow]", "[]5[]10[]"));
        JPanel team1Panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        JPanel team2Panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));

        // Team 1 Components
        team1ComboBox = new JComboBox<>(manager.loadTeamForCmb().toArray(new Team[0]));
        team1Panel.add(new JLabel("Выбрать существующую команду 1:"));
        team1Panel.add(team1ComboBox, "growx, wrap");

        team1Panel.add(new JLabel("Или создать новую команду 1:"));
        team1Panel.add(new JLabel("Код:"));
        team1CodField = new JTextField(10);
        team1Panel.add(team1CodField, "span");
        team1Panel.add(new JLabel("Наименование:"));
        team1NameField = new JTextField(40);
        team1Panel.add(team1NameField, "span");
        team1Panel.add(new JLabel("Город:"));
        team1CityField = new JTextField(10);
        team1Panel.add(team1CityField, "span");
        team1Panel.add(new JLabel("Пол команды:"));
        team1GenderField = new JTextField(10);
        team1Panel.add(team1GenderField, "span, wrap");
        
     // Кнопка "Добавить команду 1"
        JButton btnAddTeam1 = new JButton("Добавить команду 1");
        team1Panel.add(btnAddTeam1, "growx, span, wrap");

        // Обработчик для кнопки "Добавить команду 1"
        btnAddTeam1.addActionListener(e -> {
            newTeam1 = constructTeam(team1CodField, team1NameField, team1CityField, team1GenderField);
            if (newTeam1 != null && manager.addTeam(newTeam1)) {
                System.out.println("Новая команда 1 добавлена в БД");
                team1ComboBox.addItem(newTeam1);
                team1ComboBox.setSelectedItem(newTeam1);
                team2ComboBox.addItem(newTeam1);
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении команды 1 в БД", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Team 2 Components
        team2ComboBox = new JComboBox<>(manager.loadTeamForCmb().toArray(new Team[0]));
        team2Panel.add(new JLabel("Выбрать существующую команду 2:"));
        team2Panel.add(team2ComboBox, "growx, wrap");

        team2Panel.add(new JLabel("Или создать новую команду 2:"));
        team2Panel.add(new JLabel("Код:"));
        team2CodField = new JTextField(10);
        team2Panel.add(team2CodField, "span");
        team2Panel.add(new JLabel("Наименование:"));
        team2NameField = new JTextField(40);
        team2Panel.add(team2NameField, "span");
        team2Panel.add(new JLabel("Город:"));
        team2CityField = new JTextField(10);
        team2Panel.add(team2CityField, "span");
        team2Panel.add(new JLabel("Пол команды:"));
        team2GenderField = new JTextField(10);
        team2Panel.add(team2GenderField, "span, wrap");
        
     // Кнопка "Добавить команду 2"
        JButton btnAddTeam2 = new JButton("Добавить команду 2");
        team2Panel.add(btnAddTeam2, "growx, span, wrap");

        // Обработчик для кнопки "Добавить команду 2"
        btnAddTeam2.addActionListener(e -> {
            newTeam2 = constructTeam(team2CodField, team2NameField, team2CityField, team2GenderField);
            if (newTeam2 != null && manager.addTeam(newTeam2)) {
                System.out.println("Новая команда 2 добавлена в БД");
                team1ComboBox.addItem(newTeam2);
                team2ComboBox.addItem(newTeam2);
                team2ComboBox.setSelectedItem(newTeam2);
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении команды 2 в БД", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        team1ComboBox.addActionListener(e -> {
            Team selectedTeam = (Team) team1ComboBox.getSelectedItem();
            if (selectedTeam != null) {
                team1CodField.setText(selectedTeam.getId_team().toString());
                team1NameField.setText(selectedTeam.getTeam_name());
                team1CityField.setText(selectedTeam.getCity());
                team1GenderField.setText(selectedTeam.getGender_team());
            }
        });

        team2ComboBox.addActionListener(e -> {
            Team selectedTeam = (Team) team2ComboBox.getSelectedItem();
            if (selectedTeam != null) {
                team2CodField.setText(selectedTeam.getId_team().toString());
                team2NameField.setText(selectedTeam.getTeam_name());
                team2CityField.setText(selectedTeam.getCity());
                team2GenderField.setText(selectedTeam.getGender_team());
            }
        });

        panel.add(team1Panel, "grow");
        panel.add(new JLabel(" "), "grow");
        panel.add(team2Panel, "grow");
        return panel;
    }

    private JPanel createPlayerPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[grow][][grow]", "[]5[]10[]"));
        JPanel team1PlayerPanel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        JPanel team2PlayerPanel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));

        // Team 1 Player Components
        player1NameField = new JTextField(40);
        player1DaterField = new JFormattedTextField(createFormatter("##-##-####"));
        player1DaterField.setColumns(10);
        player1HeieField = new JTextField(20);
        player1WeieField = new JTextField(20);
        player1RoleField = new JTextField(20);
        player1NumField = new JTextField(10);
        player1GeedField = new JTextField(20);
        btnAddPlayer1 = new JButton("Добавить игрока в команду 1");
        playerList1 = new JList<>();

        JLabel team1Label = new JLabel("Команда:");
        team1PlayerPanel.add(team1Label, "growx, wrap");

        team1NameLabel = new JLabel(newTeam1 != null ? newTeam1.getTeam_name() : "Не выбрана");
        team1PlayerPanel.add(team1NameLabel, "growx, wrap");
        

        team1PlayerPanel.add(new JLabel("Игроки команды 1:"));
        team1PlayerPanel.add(new JScrollPane(playerList1), "growx, span, wrap");

        team1PlayerPanel.add(new JLabel("Добавить нового игрока в команду 1:"));
        team1PlayerPanel.add(new JLabel("Игрок:"));
        team1PlayerPanel.add(player1NameField, "span");
        team1PlayerPanel.add(new JLabel("Дата рождения:"));
        team1PlayerPanel.add(player1DaterField, "span");
        team1PlayerPanel.add(new JLabel("Рост:"));
        team1PlayerPanel.add(player1HeieField, "span");
        team1PlayerPanel.add(new JLabel("Вес:"));
        team1PlayerPanel.add(player1WeieField, "span");
        team1PlayerPanel.add(new JLabel("Роль:"));
        team1PlayerPanel.add(player1RoleField, "span");
        team1PlayerPanel.add(new JLabel("Номер:"));
        team1PlayerPanel.add(player1NumField, "span");
        team1PlayerPanel.add(new JLabel("Пол:"));
        team1PlayerPanel.add(player1GeedField, "span, wrap");
        team1PlayerPanel.add(btnAddPlayer1, "growx, span, wrap");
        JButton btnDeletePlayer1 = new JButton("Удалить игрока из команды 1"); 
        team1PlayerPanel.add(btnDeletePlayer1, "growx, span, wrap");

        // Team 2 Player Components (similar to Team 1)
        player2NameField = new JTextField(40);
        player2DaterField = new JFormattedTextField(createFormatter("##-##-####"));
        player2DaterField.setColumns(10);
        player2HeieField = new JTextField(20);
        player2WeieField = new JTextField(20);
        player2RoleField = new JTextField(20);
        player2NumField = new JTextField(10);
        player2GeedField = new JTextField(20);
        btnAddPlayer2 = new JButton("Добавить игрока в команду 2");
        playerList2 = new JList<>();

        JLabel team2Label = new JLabel("Команда:");
        team2PlayerPanel.add(team2Label, "growx, wrap");

        team2NameLabel = new JLabel(newTeam2 != null ? newTeam2.getTeam_name() : "Не выбрана");
        team2PlayerPanel.add(team2NameLabel, "growx, wrap");

        team2PlayerPanel.add(new JLabel("Игроки команды 2:"));
        team2PlayerPanel.add(new JScrollPane(playerList2), "growx, span, wrap");

        team2PlayerPanel.add(new JLabel("Добавить нового игрока в команду 2:"));
        team2PlayerPanel.add(new JLabel("Игрок:"));
        team2PlayerPanel.add(player2NameField, "span");
        team2PlayerPanel.add(new JLabel("Дата рождения:"));
        team2PlayerPanel.add(player2DaterField, "span");
        team2PlayerPanel.add(new JLabel("Рост:"));
        team2PlayerPanel.add(player2HeieField, "span");
        team2PlayerPanel.add(new JLabel("Вес:"));
        team2PlayerPanel.add(player2WeieField, "span");
        team2PlayerPanel.add(new JLabel("Роль:"));
        team2PlayerPanel.add(player2RoleField, "span");
        team2PlayerPanel.add(new JLabel("Номер:"));
        team2PlayerPanel.add(player2NumField, "span");
        team2PlayerPanel.add(new JLabel("Пол:"));
        team2PlayerPanel.add(player2GeedField, "span, wrap");
        team2PlayerPanel.add(btnAddPlayer2, "growx, span, wrap");
        JButton btnDeletePlayer2 = new JButton("Удалить игрока из команды 2");
        team2PlayerPanel.add(btnDeletePlayer2, "growx, span, wrap");

        // Update player lists based on selected/created teams
        updatePlayerLists(playerList1, playerList2); 

        btnAddPlayer1.addActionListener(e -> {
            Player newPlayer = constructPlayer(player1NameField, player1DaterField, player1HeieField, player1WeieField, 
                    player1RoleField, player1NumField, player1GeedField);
            if (newPlayer != null) {
                newPlayersTeam1.add(newPlayer);
                ((DefaultListModel<Player>) playerList1.getModel()).addElement(newPlayer);
                clearPlayerFields(player1NameField, player1DaterField, player1HeieField, player1WeieField, 
                        player1RoleField, player1NumField, player1GeedField);
            }
        });

        btnAddPlayer2.addActionListener(e -> {
            Player newPlayer = constructPlayer(player2NameField, player2DaterField, player2HeieField, player2WeieField, 
                    player2RoleField, player2NumField, player2GeedField);
            if (newPlayer != null) {
                newPlayersTeam2.add(newPlayer);
                ((DefaultListModel<Player>) playerList2.getModel()).addElement(newPlayer);
                clearPlayerFields(player2NameField, player2DaterField, player2HeieField, player2WeieField, 
                        player2RoleField, player2NumField, player2GeedField);
            }
        });
        
     // Обработчик для кнопки удаления игрока из команды 1
        btnDeletePlayer1.addActionListener(e -> {
            int selectedIndex = playerList1.getSelectedIndex();
            if (selectedIndex != -1) {
                Player playerToRemove = ((DefaultListModel<Player>) playerList1.getModel()).getElementAt(selectedIndex);
                
                // Удаляем из списка игроков команды
                newPlayersTeam1.remove(playerToRemove); 
                
                // Удаляем из JList
                ((DefaultListModel<Player>) playerList1.getModel()).remove(selectedIndex);
            }
        });

        // Обработчик для кнопки удаления игрока из команды 2 (аналогично команде 1)
        btnDeletePlayer2.addActionListener(e -> {
            int selectedIndex = playerList2.getSelectedIndex();
            if (selectedIndex != -1) {
                Player playerToRemove = ((DefaultListModel<Player>) playerList2.getModel()).getElementAt(selectedIndex);
                
                // Удаляем из списка игроков команды
                newPlayersTeam2.remove(playerToRemove); 
                
                // Удаляем из JList
                ((DefaultListModel<Player>) playerList2.getModel()).remove(selectedIndex);
            }
        });

        panel.add(team1PlayerPanel, "grow");
        panel.add(new JLabel(" "), "grow");
        panel.add(team2PlayerPanel, "grow");
        return panel;
    }

    private void updatePlayerLists(JList<Player> playerList1, JList<Player> playerList2) {
    	team1NameLabel.setText(newTeam1 != null ? newTeam1.getTeam_name() : "Не выбрана");
        team2NameLabel.setText(newTeam2 != null ? newTeam2.getTeam_name() : "Не выбрана");
        
        if (newTeam1 != null) {
            DefaultListModel<Player> model1 = new DefaultListModel<>();
            // Add existing players from newTeam1 to model1
            for (Player player : manager.loadPlayer()) {
                if (player.getTeam() != null && player.getTeam().getId_team().equals(newTeam1.getId_team())) {
                    model1.addElement(player);
                }
            }
            playerList1.setModel(model1);
        }
        if (newTeam2 != null) {
            DefaultListModel<Player> model2 = new DefaultListModel<>();
            // Add existing players from newTeam2 to model2
            for (Player player : manager.loadPlayer()) {
                if (player.getTeam() != null && player.getTeam().getId_team().equals(newTeam2.getId_team())) {
                    model2.addElement(player);
                }
            }
            playerList2.setModel(model2);
        }
    }


    private JPanel createMatchPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        matchPlaygroundField = new JTextField(50);
        matchDateField = new JFormattedTextField(createFormatter("##-##-####"));
        matchDateField.setColumns(10);

        //panel.add(new JLabel("Уровень соревнования:"));
        //panel.add(new JLabel(newLevel != null ? newLevel.getName() : "Не выбран"), "wrap");

        //panel.add(new JLabel("Соревнование:"));
        //panel.add(new JLabel(newEvent != null ? newEvent.getName_event() : "Не выбрано"), "wrap");

        //panel.add(new JLabel("Команда 1:"));
        //panel.add(new JLabel(newTeam1 != null ? newTeam1.getTeam_name() : "Не выбрана"), "wrap");

        //panel.add(new JLabel("Команда 2:"));
        //panel.add(new JLabel(newTeam2 != null ? newTeam2.getTeam_name() : "Не выбрана"), "wrap");

        panel.add(new JLabel("Поле проведения:"));
        panel.add(matchPlaygroundField, "span, wrap");

        panel.add(new JLabel("Дата проведения:"));
        panel.add(matchDateField, "span, wrap");

        return panel;
    }

    // Helper methods for constructing data objects and clearing fields
    private Level constructLevel(JTextField edCod, JTextField edName) {
        try {
            Level level = new Level();
            level.setKod(edCod.getText().equals("") ? null : new BigDecimal(edCod.getText()));
            level.setName(edName.getText());
            return level;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка данных", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private Event constructEvent(JTextField edCod, JTextField edName, JTextField edLoc, JTextField edYear, Level level) {
        try {
            Event event = new Event();
            event.setId_event(edCod.getText().equals("") ? null : new BigDecimal(edCod.getText()));
            event.setName_event(edName.getText());
            event.setLocationn(edLoc.getText());
            event.setYear_event(edYear.getText().equals("") ? null : new BigDecimal(edYear.getText()));
            event.setLevel(level);
            return event;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка данных", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private Team constructTeam(JTextField edKod, JTextField edName, JTextField edCity, JTextField edGender) {
        try {
            Team team = new Team();
            team.setId_team(edKod.getText().equals("") ? null : new BigDecimal(edKod.getText()));
            team.setTeam_name(edName.getText());
            team.setCity(edCity.getText());
            team.setGender_team(edGender.getText());
            return team;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка данных", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private SimpleDateFormat frmt = new SimpleDateFormat("dd-MM-yyyy"); 
    private Player constructPlayer(JTextField edName, JFormattedTextField edDater,
                                  JTextField edHeie, JTextField edWeie, JTextField edRole,
                                  JTextField edNum, JTextField edGeed) {
        try {
            Player player = new Player();
            player.setName(edName.getText());
            player.setBirthday(edDater.getText().substring(0, 1).trim().equals("") ?
                    null : frmt.parse(edDater.getText()));
            player.setHeight(edHeie.getText().equals("") ? null : new BigDecimal(edHeie.getText()));
            player.setWeight(edWeie.getText().equals("") ? null : new BigDecimal(edWeie.getText()));
            player.setRole(edRole.getText());
            player.setNum(edNum.getText().equals("") ? null : new BigDecimal(edNum.getText()));
            player.setGender(edGeed.getText());
            return player;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка данных", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void clearPlayerFields(JTextField edName, JFormattedTextField edDater,
                                   JTextField edHeie, JTextField edWeie, JTextField edRole,
                                   JTextField edNum, JTextField edGeed) {
        edName.setText("");
        edDater.setText("");
        edHeie.setText("");
        edWeie.setText("");
        edRole.setText("");
        edNum.setText("");
        edGeed.setText("");
    }

    private void bindListeners() {
    	btnNext.addActionListener(e -> {
    	    switch (currentCardName) {
    	        case "levelPanel":
    	            // Выбираем уровень из ComboBox
    	            newLevel = (Level) levelComboBox.getSelectedItem();
    	            showCard("eventPanel");
    	            break;

    	        case "eventPanel":
    	            eventLevelLabel.setText(newLevel != null ? newLevel.getName() : "Не выбран");
    	            // Выбираем событие из ComboBox
    	            newEvent = (Event) eventComboBox.getSelectedItem();
    	            showCard("teamPanel");
    	            break;

    	        case "teamPanel":
    	            // Выбираем команды из ComboBox
    	            newTeam1 = (Team) team1ComboBox.getSelectedItem();
    	            newTeam2 = (Team) team2ComboBox.getSelectedItem();
    	            updatePlayerLists(playerList1, playerList2);
    	            showCard("playerPanel");
    	            break;

    	        case "playerPanel":
    	            // Add newPlayersTeam1 and newPlayersTeam2 to DB
    	            for (Player player : newPlayersTeam1) {
    	                player.setTeam(newTeam1);
    	                if (!manager.addPlayer(player)) {
    	                    JOptionPane.showMessageDialog(this, "Ошибка при добавлении игрока команды 1 в БД", "Ошибка", JOptionPane.ERROR_MESSAGE);
    	                    return;
    	                }
    	            }

    	            for (Player player : newPlayersTeam2) {
    	                player.setTeam(newTeam2);
    	                if (!manager.addPlayer(player)) {
    	                    JOptionPane.showMessageDialog(this, "Ошибка при добавлении игрока команды 2 в БД", "Ошибка", JOptionPane.ERROR_MESSAGE);
    	                    return;
    	                }
    	            }

    	            // Set teams and event in newMatch
    	            newMatch.setTeam1(newTeam1);
    	            newMatch.setTeam2(newTeam2);
    	            newMatch.setEvent(newEvent);

    	            showCard("matchPanel");
    	            btnNext.setText("Завершить");
    	            break;

    	        case "matchPanel":
    	            // Construct newMatch, add to DB
    	            newMatch.setPlayground(matchPlaygroundField.getText());
    	            newMatch.setTeam1score(BigDecimal.ZERO);
    	            newMatch.setTeam2score(BigDecimal.ZERO);
    	            try {
    	                newMatch.setDate_matches(matchDateField.getText().substring(0, 1).trim().equals("") ?
    	                        null : frmt.parse(matchDateField.getText()));
    	            } catch (java.text.ParseException ex) {
    	                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка данных", JOptionPane.ERROR_MESSAGE);
    	                return;
    	            }

    	            if (manager.addMatches(newMatch)) {
    	                System.out.println("Новый матч добавлен в БД");
    	            } else {
    	                JOptionPane.showMessageDialog(this, "Ошибка при добавлении матча в БД", "Ошибка", JOptionPane.ERROR_MESSAGE);
    	                return;
    	            }

    	            setDialogResult(JDialogResult.OK);
    	            close();
    	            break;
    	    }
    	    manageButtons();
    	});

        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (currentCardName) {
                    case "eventPanel":
                        showCard("levelPanel");
                        break;
                    case "teamPanel":
                        showCard("eventPanel");
                        break;
                    case "playerPanel":
                        showCard("teamPanel");
                        break;
                    case "matchPanel":
                        showCard("playerPanel");
                        btnNext.setText("Далее");
                        break;
                }
                manageButtons();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDialogResult(JDialogResult.Cancel);
                close();
            }
        });
    }

    // Метод для обновления списка уровней в панели событий
    private void updateEventLevelComboBox1(JComboBox<Level> eventLevelComboBox) {
        eventLevelComboBox.setModel(new DefaultComboBoxModel<>(manager.loadLevelForCmb().toArray(new Level[0])));
        if (newLevel != null) {
            eventLevelComboBox.setSelectedItem(newLevel);
        }
    }

    

    private void showCard(String cardName) {
        cardLayout.show(cardPanel, cardName);
        currentCardName = cardName;
        manageButtons();
    }

    private void manageButtons() {
        btnPrev.setEnabled(!currentCardName.equals("levelPanel"));
        btnNext.setEnabled(true); 
        if (currentCardName.equals("matchPanel")) {
            btnNext.setText("Завершить");
        } else {
            btnNext.setText("Далее");
        }
    }

    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    //  Метод добавление слушателя клавиатуры
    // к компонентам окна
    private void setKeyListener(Component c, KeyListener kl) {
        c.addKeyListener(kl);
        if (c instanceof Container)
            for (Component comp : ((Container) c).getComponents())
                setKeyListener(comp, kl);
    }
    
    public Matches getNewMatch() {
        return newMatch;
    }

}