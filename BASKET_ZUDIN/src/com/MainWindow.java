package com;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.data.*;
import com.gui.LookAndFillUtil;
import com.rpt.RptData;
import com.rpt.RptParams;
import com.rpt.RptParamsDialog;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import java.io.FileOutputStream;


public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    // Поля класса
    private DBManager manager;
    JMenuBar mainMenu;
    JMenuItem miExit;
    JMenu mSprav;
    JMenuItem miNewMatch;
    JMenuItem mile;
    JMenuItem miSor;
    JMenuItem miTe;
    JMenuItem miPla;

    JMenuItem miMatch;

    JMenu mKn;

    private JButton[] playerButtonsA = new JButton[5];
    private JButton[] playerButtonsB = new JButton[5];
    private JButton substitutionButtonA;
    private JButton substitutionButtonB;

    private Matches currentMatch = null;
    private Player selectedPlayer = null;
    private Team selectedPlayerTeam = null; // Поле для команды выбранного игрока
    private JButton selectedPlayerButton = null;
    private JTextArea eventsTextArea;
    private HashMap<BigDecimal, Stat> playerStatsMap = new HashMap<>();

    private JLabel scoreLabel; // Для отображения счета
    private int scoreTeamA = 0;
    private int scoreTeamB = 0;
    private int[] quarterScoresTeamA = new int[4]; // Массив для хранения счета команды A по четвертям
    private int[] quarterScoresTeamB = new int[4]; // Массив для хранения счета команды B по четвертям
    private int currentQuarter = 0; // Текущая четверть (0 - матч не начат)
    private boolean quarterStarted = false; // Флаг, указывающий, начата ли четверть
    private JButton startQuarterButton;
    private JButton endQuarterButton;
    private JComboBox<String> quarterComboBox;
    private JButton finishMatchButton;
    private Timer quarterTimer;
    private int quarterTimeSeconds = 600; // Время четверти в секундах
    private JLabel quarterTimerLabel; // Лейбл для отображения времени четверти
    private HashMap<BigDecimal, Integer> playerTimeOnCourt = new HashMap<>(); // Время каждого игрока на площадке (в секундах)
    private boolean timerPaused = false; // Флаг, указывающий, на паузе ли таймер
    private JButton pauseTimerButton; // Кнопка для паузы таймера
    private JButton resumeTimerButton; // Кнопка для возобновления таймера
    
    private HashMap<BigDecimal, Integer> playerTimeMap = new HashMap<>();
    private HashMap<BigDecimal, Integer> playerTwoPointMisses = new HashMap<>();
    private HashMap<BigDecimal, Integer> playerThreePointMisses = new HashMap<>();
    private HashMap<BigDecimal, Integer> playerFreeThrowMisses = new HashMap<>();
    
    private ImageIcon basketballIcon;
    // Кнопка текущего выбранного игрока
    private JButton currentlySelectedPlayerButton = null;

    public MainWindow(DBManager manager) {
        this.manager = manager;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        manager.setPath();
        testConn();
        createGUI();
        bindListeners();
        setSize(1320, 830);
        setTitle("StatBasket");
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        playerTimeMap = new HashMap<>();
        
        // !!!  Устанавливаем иконку главного окна
        URL iconUrl = MainWindow.class.getResource("/images/icon-basketballapp.png");  //  
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        } else {
            System.err.println("Не удалось загрузить иконку.");
        }

        
     // Загрузка картинки баскетбольного мяча
        URL basketballImageUrl = MainWindow.class.getResource("/images/ball2.png"); 
        if (basketballImageUrl != null) {
            try {
                BufferedImage originalImage = ImageIO.read(basketballImageUrl);

                //  Уменьшаем размер изображения
                int newWidth = 40;  
                int newHeight = 40; 
                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                // Создаем BufferedImage с прозрачностью
                BufferedImage transparentImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = transparentImage.createGraphics();
                g2d.drawImage(scaledImage, 0, 0, null);
                g2d.dispose();

                basketballIcon = new ImageIcon(transparentImage);
            } catch (IOException ex) {
                System.err.println("Не удалось загрузить картинку basketball.png: " + ex.getMessage());
            }
        } else {
            System.err.println("Не удалось найти файл basketball.png");
        }
        

        // Левая панель - команда А
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints leftGbc = new GridBagConstraints();
        for (int i = 0; i < 5; i++) {
            playerButtonsA[i] = createPlayerButton("Игрок " + (i + 1), 110, 70);
            leftGbc.gridx = 0;
            leftGbc.gridy = i;
            leftGbc.fill = GridBagConstraints.BOTH;
            leftGbc.weightx = 1.0;
            leftPanel.add(playerButtonsA[i], leftGbc);
        }
        
        leftGbc.gridx = 0;
        leftGbc.gridy = 6;
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.weightx = 1.0;
        leftGbc.insets = new Insets(20, 0, 0, 0);
        leftPanel.add(substitutionButtonA, leftGbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(leftPanel, gbc);

        // Центральная панель - счет и события
        JPanel middlePanel = new JPanel(new BorderLayout());
        JPanel scorePanel = new JPanel(new FlowLayout());
        scoreLabel = new JLabel("Счет: Команда A - 0 : 0 - Команда B");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel);
        middlePanel.add(scorePanel, BorderLayout.NORTH);
        

        eventsTextArea = new JTextArea(20, 30);
        eventsTextArea.setFont(new Font("Arial", Font.BOLD, 12));
        eventsTextArea.setEditable(false);
        JPanel eventsPanel = new JPanel(new BorderLayout()); // !!! Используем BorderLayout для eventsPanel
        eventsPanel.setBorder(BorderFactory.createTitledBorder("Окно отображения событий"));
        eventsPanel.add(new JScrollPane(eventsTextArea), BorderLayout.CENTER); // !!! Добавляем JScrollPane в CENTER
        middlePanel.add(eventsPanel, BorderLayout.CENTER);
        eventsPanel.setPreferredSize(eventsTextArea.getPreferredSize()); 
        
       

        // Панель со статистическими кнопками
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        String[] buttonLabels = {"1 очко", "1 очко\nмимо", "2 очка", "2 очка\nмимо", "3 очка", "3 очка\nмимо"};
        for (String label : buttonLabels) {
            statsPanel.add(createStatButton(label, 90, 90));
            
        }
        
        middlePanel.add(statsPanel, BorderLayout.WEST);

        // Панель с дополнительными кнопками
        JPanel extraButtonsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        String[] extraButtonLabels = {"подбор\nв защите", "подбор\nв атаке", "блокшот", "фол", "ассист", "перехват"};
        for (String label : extraButtonLabels) {
            extraButtonsPanel.add(createStatButton(label, 90, 90));
        }
        middlePanel.add(extraButtonsPanel, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(middlePanel, gbc);

        // Правая панель - команда Б
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rightGbc = new GridBagConstraints();
        for (int i = 0; i < 5; i++) {
            playerButtonsB[i] = createPlayerButton("Игрок " + (i + 1), 110, 70);
            rightGbc.gridx = 0;
            rightGbc.gridy = i;
            rightGbc.fill = GridBagConstraints.BOTH;
            rightGbc.weightx = 1.0;
            rightPanel.add(playerButtonsB[i], rightGbc);
        }

        rightGbc.gridx = 0;
        rightGbc.gridy = 6;
        rightGbc.fill = GridBagConstraints.HORIZONTAL;
        rightGbc.weightx = 1.0;
        rightGbc.insets = new Insets(20, 0, 0, 0);
        rightPanel.add(substitutionButtonB, rightGbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel, gbc);
        
        

        // Нижняя панель
        JPanel bottomPanel = new JPanel(new MigLayout("insets 5, flowy", "[grow][]", "[]5[]"));
        bottomPanel.add(quarterComboBox, "cell 0 1, width 60!, align center");
        JLabel quarterLabel = new JLabel("ЧЕТВЕРТЬ"); 
        quarterLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Жирный шрифт
        bottomPanel.add(quarterLabel, "cell 0 0, align center");
        bottomPanel.add(startQuarterButton, "cell 0 1, width 140!, growx 0, align center");
        bottomPanel.add(endQuarterButton, "cell 0 1, growx 0, align center");
        bottomPanel.add(finishMatchButton, "cell 1 0, growx"); // Добавляем кнопку на панель
        bottomPanel.add(quarterTimerLabel, "cell 0 1, align center");
        bottomPanel.add(pauseTimerButton, "cell 0 1, width 140!, growx 0, align center");
        bottomPanel.add(resumeTimerButton, "cell 0 1, width 140!, growx 0, align center");
        pauseTimerButton.setEnabled(false); // Отключаем кнопку паузы 
        resumeTimerButton.setEnabled(false); // Отключаем кнопку продолжить
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottomPanel, gbc);

        setVisible(true);
        
        
     // Создаем центральную панель с GridBagLayout
        JPanel centralPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cgbc = new GridBagConstraints(); //  GridBagConstraints для centralPanel

        // Добавляем панели в centralPanel
        cgbc.gridx = 0;
        cgbc.gridy = 0;
        cgbc.weightx = 0.3; //  Левая панель занимает 20% ширины
        cgbc.fill = GridBagConstraints.BOTH;
        centralPanel.add(leftPanel, cgbc);

        cgbc.gridx = 1;
        cgbc.weightx = 0.4; //  Средняя панель занимает 60% ширины
        centralPanel.add(middlePanel, cgbc);

        cgbc.gridx = 2;
        cgbc.weightx = 0.3; //  Правая панель занимает 20% ширины
        centralPanel.add(rightPanel, cgbc);
        

        // Добавляем centralPanel в JFrame
        gbc.gridx = 0; 
        gbc.gridy = 0; //  !!! Изменяем gridy 
        gbc.gridwidth = 3;
        gbc.weightx = 1.0; // !!!  centralPanel  занимает все доступное пространство по горизонтали
        gbc.weighty = 1.0; // !!!  centralPanel  занимает все доступное пространство по вертикали
        gbc.fill = GridBagConstraints.BOTH;
        add(centralPanel, gbc); 
    }

    
     
    private JButton createStatButton(String text, int width, int height) {
        JButton button = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        button.setPreferredSize(new Dimension(width, height)); 
        if (text.contains("мимо")) {
            button.setBorder(new RoundedLineBorder(new Color(253, 118, 118), 3, 12)); // Светло-красный, закругленные края
        } else {
            button.setBorder(new RoundedLineBorder(new Color(102, 204, 102), 3, 12)); // Светло-зеленый, закругленные края
        }

        // !!! Добавляем обработчики событий мыши
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Изменяем фон кнопки при наведении курсора
                button.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Возвращаем исходный фон кнопки
                button.setBackground(null); 
            }
        });
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedPlayer != null && currentMatch != null) {
                    Stat playerStats = playerStatsMap.get(selectedPlayer.getId());
                    if (playerStats == null) {
                        playerStats = new Stat();
                        playerStats.setIdPlayer(selectedPlayer.getId().intValue());
                        playerStats.setIdMatch(currentMatch.getId_matches());
                        playerStatsMap.put(selectedPlayer.getId(), playerStats);
                    }

                    switch (text) {
                        case "1 очко":
                            playerStats.setPointScored(playerStats.getPointScored() + 1);
                            playerStats.setFreeThrow(playerStats.getFreeThrow() + 1);
                            // Проверяем команду выбранного игрока
                            if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team())) {
                                scoreTeamA++;
                            } else if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam2().getId_team())) {
                                scoreTeamB++;
                            }
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " забил 1 очко");
                            break;
                        case "1 очко\nмимо":
                            // Просто обновляем отображение, не сохраняя в БД
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " промахнулся 1 очко");
                            playerFreeThrowMisses.put(selectedPlayer.getId(), 
                                    playerFreeThrowMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                            break;
                        case "2 очка":
                            playerStats.setPointScored(playerStats.getPointScored() + 2);
                            playerStats.setDoubleDouble(playerStats.getDoubleDouble() + 1);
                            // Проверяем команду выбранного игрока
                            if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team())) {
                                scoreTeamA += 2;
                            } else if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam2().getId_team())) {
                                scoreTeamB += 2;
                            }
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " забил 2 очка");
                            break;
                        case "2 очка\nмимо":
                            // Просто обновляем отображение, не сохраняя в БД
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " промахнулся 2 очка");
                            playerTwoPointMisses.put(selectedPlayer.getId(), 
                                    playerTwoPointMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                            break;
                        case "3 очка":
                            playerStats.setPointScored(playerStats.getPointScored() + 3);
                            playerStats.setTriple(playerStats.getTriple() + 1);
                            // Проверяем команду выбранного игрока
                            if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team())) {
                                scoreTeamA += 3;
                            } else if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam2().getId_team())) {
                                scoreTeamB += 3;
                            }
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " забил 3 очка");
                            break;
                        case "3 очка\nмимо":
                            // Просто обновляем отображение, не сохраняя в БД
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " промахнулся 3 очка");
                            playerThreePointMisses.put(selectedPlayer.getId(), 
                                    playerThreePointMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                            break;
                        case "подбор\nв защите":
                            playerStats.setDr(playerStats.getDr() + 1);
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " сделал подбор в защите");
                            break;
                        case "подбор\nв атаке":
                            playerStats.setOr(playerStats.getOr() + 1);
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " сделал подбор в атаке");
                            break;
                        case "блокшот":
                            playerStats.setBlockedShot(playerStats.getBlockedShot() + 1);
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " сделал блокшот");
                            break;
                        case "фол":
                            playerStats.setFoul(playerStats.getFoul() + 1);
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " получил фол");
                            break;
                        case "ассист":
                            playerStats.setAssists(playerStats.getAssists() + 1);
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " сделал ассист");
                            break;
                        case "перехват":
                            playerStats.setSteal(playerStats.getSteal() + 1);
                            updateEventsTextArea("Игрок " + selectedPlayer.getName() + " сделал перехват");

                            // Открываем диалог для выбора игрока противоположной команды
                            showOpponentPlayerDialog();
                            break;
                    }
                    // Обновляем счет на табло
                    updateScoreLabel();
                }
            }
        });
        return button;
    }
    
    // Метод для отображения диалога с игроками противоположной команды
    private void showOpponentPlayerDialog() {
        // Определяем команду соперника
        BigDecimal opponentTeamId = selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team()) ? 
                                     currentMatch.getTeam2().getId_team() : currentMatch.getTeam1().getId_team();

        // Создаем список игроков соперника на площадке
        DefaultListModel<Player> opponentPlayersModel = new DefaultListModel<>();
        for (int i = 0; i < 5; i++) {
            JButton[] opponentButtons = selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team()) ? 
                                          playerButtonsB : playerButtonsA;
            String playerIdStr = opponentButtons[i].getActionCommand();
            if (playerIdStr != null && !playerIdStr.isEmpty()) {
                BigDecimal playerId = new BigDecimal(playerIdStr);
                for (Player player : manager.loadPlayer()) {
                    if (player.getId().equals(playerId) && player.getTeam().getId_team().equals(opponentTeamId)) {
                        opponentPlayersModel.addElement(player);
                        break;
                    }
                }
            }
        }

        // Создаем JList с игроками соперника
        JList<Player> opponentPlayerList = new JList<>(opponentPlayersModel);
        opponentPlayerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Создаем диалог
        JDialog dialog = new JDialog(this, "Выберите игрока, который потерял мяч", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(opponentPlayerList), BorderLayout.CENTER);

        // Кнопка "OK"
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            Player opponentPlayer = opponentPlayerList.getSelectedValue();
            if (opponentPlayer != null) {
                Stat opponentStats = playerStatsMap.get(opponentPlayer.getId());
                if (opponentStats == null) {
                    opponentStats = new Stat();
                    opponentStats.setIdPlayer(opponentPlayer.getId().intValue());
                    opponentStats.setIdMatch(currentMatch.getId_matches());
                    playerStatsMap.put(opponentPlayer.getId(), opponentStats);
                }
                opponentStats.setTurnover(opponentStats.getTurnover() + 1);
                updateEventsTextArea("Игрок " + opponentPlayer.getName() + " потерял мяч");
            }
            dialog.dispose();
        });
        dialog.add(okButton, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    // Метод для создания кнопки игрока
    private JButton createPlayerButton(String text, int width, int height) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height)); 
        // !!! Добавляем обработчики событий мыши
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Изменяем фон кнопки при наведении курсора
                button.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Возвращаем исходный фон кнопки
                button.setBackground(null); 
            }
        });
        button.addActionListener(e -> {
            JButton btn = (JButton) e.getSource(); // Получаем нажатую кнопку
            selectedPlayerButton = btn; // Сохраняем ссылку на нажатую кнопку
            String playerIdStr = btn.getActionCommand();
            if (playerIdStr != null && !playerIdStr.isEmpty()) {
                BigDecimal playerId = new BigDecimal(playerIdStr);
                for (Player player : manager.loadPlayer()) {
                    if (player.getId().equals(playerId)) {
                        selectedPlayer = player;
                        selectedPlayerTeam = player.getTeam();
                        updateEventsTextArea("Выбран игрок: " + selectedPlayer.getName());
                        break;
                    }
                }
            }
            // !!!  Устанавливаем/удаляем иконку мяча 
            if (basketballIcon != null) {
                if (currentlySelectedPlayerButton != null) {
                    currentlySelectedPlayerButton.setIcon(null); //  Удаляем иконку с предыдущей кнопки
                }
                btn.setIcon(basketballIcon); //  Устанавливаем иконку на новую кнопку
                currentlySelectedPlayerButton = btn; //  Обновляем currentlySelectedPlayerButton
            }
        });
        return button;
    }


    // Метод для создания кнопки замены
    private JButton createSubstitutionButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(new Color(128, 128, 128));
        // !!! Добавляем обработчики событий мыши
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Изменяем фон кнопки при наведении курсора
                button.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Возвращаем исходный фон кнопки
                button.setBackground(new Color(128, 128, 128)); 
            }
        });
        return button;
    }

    private JMenuBar createMenu() {
        mainMenu = new JMenuBar();
        mSprav = new JMenu("Справочники");
        miExit = new JMenuItem("Выход");
        mile = new JMenuItem("Уровни соревнований");
        miSor = new JMenuItem("Соревнования");
        miTe = new JMenuItem("Команды");
        miNewMatch = new JMenuItem("Новый матч");
        miMatch = new JMenuItem("Просмотр и редактирование матчей");
        miPla = new JMenuItem("Игроки");
        mKn = new JMenu("История матчей");

        mSprav.add(mile);
        mSprav.add(miTe);
        mSprav.add(miSor);
        mKn.add(miMatch);
        mSprav.add(miPla);

        //mainMenu.add(mSprav);
        mainMenu.add(mKn);
        mainMenu.add(miNewMatch);
        mainMenu.add(miExit);
        return mainMenu;
    }

    private void bindListeners() {
        miExit.addActionListener(e -> System.exit(0));

        miMatch.addActionListener(e -> ShowZayav());
        miTe.addActionListener(e -> ShowStreet());
        miSor.addActionListener(e -> ShowZdan());
        mile.addActionListener(e -> ShowGorod());
        miPla.addActionListener(e -> ShowObj_nedvij());
        miNewMatch.addActionListener(e -> {
        	NewMatchWizard wizard = new NewMatchWizard(MainWindow.this, manager);
            if (wizard.showDialog() == JDialogResult.OK) {
                currentMatch = wizard.getNewMatch();
                updatePlayerButtons();
                resetScore(); 
                playerStatsMap.clear(); 
                resetQuarterScores(); // Сброс счета по четвертям
                currentQuarter = 0; // Сброс текущей четверти
                quarterStarted = false; // Сброс флага начала четверти
            }
        });

     // Слушатели для кнопок замены
        substitutionButtonA.addActionListener(e -> {
            if (currentMatch != null) {
                // !!! Проверка, выбрана ли кнопка игрока команды A
                if (selectedPlayerButton != null && Arrays.asList(playerButtonsA).contains(selectedPlayerButton)) {
                    showSubstitutionDialog(currentMatch.getTeam1().getId_team(), playerButtonsA);
                } else {
                    JOptionPane.showMessageDialog(this, "Выберите игрока команды A для замены.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        substitutionButtonB.addActionListener(e -> {
            if (currentMatch != null) {
                // !!! Проверка, выбрана ли кнопка игрока команды B
                if (selectedPlayerButton != null && Arrays.asList(playerButtonsB).contains(selectedPlayerButton)) {
                    showSubstitutionDialog(currentMatch.getTeam2().getId_team(), playerButtonsB);
                } else {
                    JOptionPane.showMessageDialog(this, "Выберите игрока команды B для замены.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        startQuarterButton.addActionListener(e -> {
            if (currentMatch != null) {
                int selectedQuarter = quarterComboBox.getSelectedIndex() + 1;
                if (selectedQuarter > currentQuarter && !quarterStarted) {
                    currentQuarter = selectedQuarter;
                    quarterStarted = true;
                    updateEventsTextArea("Начало " + currentQuarter + " четверти");

                    startQuarterTimer(); // Запускаем таймер
                    resetQuarterTimer(); // Сбрасываем таймер четверти
                    quarterTimer.start();

                    // !!! Включаем кнопку паузы, отключаем кнопку продолжить
                    pauseTimerButton.setEnabled(true);
                    resumeTimerButton.setEnabled(false);
                } else if (quarterStarted) {
                    JOptionPane.showMessageDialog(this, "Четверть уже начата!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Нельзя начать эту четверть, пока не завершена предыдущая.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        endQuarterButton.addActionListener(e -> {
            if (currentMatch != null && quarterStarted) {
                quarterScoresTeamA[currentQuarter - 1] = scoreTeamA;
                quarterScoresTeamB[currentQuarter - 1] = scoreTeamB;
                quarterStarted = false;
                updateEventsTextArea("Конец " + currentQuarter + " четверти. Счет: " + scoreTeamA + " : " + scoreTeamB);

                // Обновляем playerTimeMap
                for (Map.Entry<BigDecimal, Integer> entry : playerTimeOnCourt.entrySet()) {
                    BigDecimal playerId = entry.getKey();
                    int timeOnCourt = entry.getValue();
                    playerTimeMap.put(playerId, playerTimeMap.getOrDefault(playerId, 0) + timeOnCourt);
                }

                resetQuarterTimer(); // Сбрасываем таймер четверти
            } else if (!quarterStarted) {
                JOptionPane.showMessageDialog(this, "Четверть еще не начата!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Слушатель для кнопки "Завершить матч"
        finishMatchButton.addActionListener(e -> {
            if (currentQuarter == 4 && quarterStarted == false) {
                // Сохраняем статистику всех игроков
                for (Stat stats : playerStatsMap.values()) {
                    manager.saveStat(stats);
                }
                if (currentMatch != null) {
                    currentMatch.setTeam1score(BigDecimal.valueOf(scoreTeamA));
                    currentMatch.setTeam2score(BigDecimal.valueOf(scoreTeamB));
                    manager.updateMatches(currentMatch, currentMatch.getId_matches()); // Обновляем матч в базе данных
                }


                // Создаем отчет о матче
                createMatchReport();


                // Сбрасываем текущий матч и очищаем статистику
                currentMatch = null;
                selectedPlayer = null;
                selectedPlayerTeam = null;
                selectedPlayerButton = null;
                resetScore();
                resetQuarterScores();
                currentQuarter = 0;
                quarterStarted = false;
                eventsTextArea.setText(""); // Очищаем текстовое поле событий
                updatePlayerButtons(); 
            } else {
                JOptionPane.showMessageDialog(this, "Завершите текущую четверть или матч!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
     // Слушатель для кнопки "Пауза"
        pauseTimerButton.addActionListener(e -> {
            if (quarterStarted && !timerPaused) {
                quarterTimer.stop();
                timerPaused = true;
                updateEventsTextArea("Таймер остановлен");

                // !!! Включаем кнопку продолжить, отключаем кнопку паузы
                resumeTimerButton.setEnabled(true);
                pauseTimerButton.setEnabled(false); 
            }
        });


        // Слушатель для кнопки "Продолжить"
        resumeTimerButton.addActionListener(e -> {
            if (quarterStarted && timerPaused) {
                quarterTimer.start();
                timerPaused = false;
                updateEventsTextArea("Таймер запущен");

                // !!! Включаем кнопку паузы, отключаем кнопку продолжить
                pauseTimerButton.setEnabled(true); 
                resumeTimerButton.setEnabled(false);
            }
        });
        
        
    }
    
    
    
 // Метод для сброса счета по четвертям
    private void resetQuarterScores() {
        for (int i = 0; i < 4; i++) {
            quarterScoresTeamA[i] = 0;
            quarterScoresTeamB[i] = 0;
        }
    }

    private void updatePlayerButtons() {
        if (currentMatch != null) {
            ArrayList<Player> playersTeam1 = getPlayersForTeam(currentMatch.getTeam1().getId_team());
            ArrayList<Player> playersTeam2 = getPlayersForTeam(currentMatch.getTeam2().getId_team());

            for (int i = 0; i < 5 && i < playersTeam1.size(); i++) {
                Player player = playersTeam1.get(i);
                playerButtonsA[i].setText("<html><center>Игрок " + player.getNum() + "<br>" + player.getName() + "</center></html>");
                playerButtonsA[i].setActionCommand(player.getId().toString());
            }

            for (int i = 0; i < 5 && i < playersTeam2.size(); i++) {
                Player player = playersTeam2.get(i);
                playerButtonsB[i].setText("<html><center>Игрок " + player.getNum() + "<br>" + player.getName() + "</center></html>");
                playerButtonsB[i].setActionCommand(player.getId().toString());
            }
        }
    }

    private ArrayList<Player> getPlayersForTeam(BigDecimal teamId) {
        ArrayList<Player> players = new ArrayList<>();
        for (Player player : manager.loadPlayer()) {
            if (player.getTeam().getId_team().equals(teamId)) {
                players.add(player);
            }
        }
        return players;
    }
    
    private void startQuarterTimer() {
        quarterTimer = new Timer(1000, e -> {
            if (!timerPaused) {
                quarterTimeSeconds--;
                updateQuarterTimerLabel();
                // Обновляем время игроков на площадке
                for (int i = 0; i < 5; i++) {
                    if (playerButtonsA[i].getActionCommand() != null) {
                        BigDecimal playerId = new BigDecimal(playerButtonsA[i].getActionCommand());
                        playerTimeOnCourt.put(playerId, playerTimeOnCourt.getOrDefault(playerId, 0) + 1);
                    }
                    if (playerButtonsB[i].getActionCommand() != null) {
                        BigDecimal playerId = new BigDecimal(playerButtonsB[i].getActionCommand());
                        playerTimeOnCourt.put(playerId, playerTimeOnCourt.getOrDefault(playerId, 0) + 1);
                    }
                }

                // Проверка на окончание четверти
                if (quarterTimeSeconds == 0) {
                    endQuarterButton.doClick(); // Симулируем нажатие кнопки "Завершить четверть"
                }
            }
        });
        quarterTimer.start();
    }


    private void stopQuarterTimer() {
        if (quarterTimer != null) {
            quarterTimer.stop();
        }
    }


    private void resetQuarterTimer() {
        stopQuarterTimer();
        quarterTimeSeconds = 600; // Сбрасываем время на 10 минут
        updateQuarterTimerLabel();
        playerTimeOnCourt.clear();
    }

    private void updateQuarterTimerLabel() {
        int minutes = quarterTimeSeconds / 60;
        int seconds = quarterTimeSeconds % 60;
        quarterTimerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    protected void ShowZayav() {
    	FrmMatches frm = new FrmMatches(manager);
        frm.setVisible(true);

    }

    protected void ShowStreet() {
        FrmTeam frm = new FrmTeam(manager);
        frm.setVisible(true);
    }

    protected void ShowObj_nedvij() {
        FrmPlayer frm = new FrmPlayer(manager);
        frm.setVisible(true);
    }

    protected void ShowGorod() {
        FrmLevel frm = new FrmLevel(manager);
        frm.setVisible(true);
    }

    protected void ShowZdan() {
        FrmEvent frm = new FrmEvent(manager);
        frm.setVisible(true);
    }

    private void testConn() {
        String ver = manager.getVersion();
        System.out.println(ver);
    }

    private void createGUI() {
        setJMenuBar(createMenu());
        substitutionButtonA = createSubstitutionButton("Замена команды А", 150, 70);
        substitutionButtonB = createSubstitutionButton("Замена команды B", 150, 70);
        startQuarterButton = new JButton("Начать четверть");
        endQuarterButton = new JButton("Завершить четверть");
     // Комбобокс для выбора четверти
        quarterComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        finishMatchButton = new JButton("Завершить матч");
     // Лейбл для таймера четверти
        quarterTimerLabel = new JLabel("00:00");
        quarterTimerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Кнопки для управления таймером
        pauseTimerButton = new JButton("Пауза");
        resumeTimerButton = new JButton("Продолжить");
        
        addButtonHighlight(startQuarterButton);
        addButtonHighlight(endQuarterButton);
        addButtonHighlight(pauseTimerButton);
        addButtonHighlight(resumeTimerButton);
        addButtonHighlight(finishMatchButton);
        
        
        
        startQuarterButton.setBackground(new Color(102, 204, 102)); //  Светло-зеленый фон
        endQuarterButton.setBackground(new Color(255, 153, 51)); //  Светло-оранжевый фон      
        pauseTimerButton.setBackground(new Color(255, 153, 51)); //  Светло-оранжевый фон     
        resumeTimerButton.setBackground(new Color(102, 204, 102)); //  Светло-зеленый фон      
        finishMatchButton.setBackground(new Color(253, 51, 51)); //  Светло-красный фон 
        
        
        
        
  
    }
    
    private void addButtonHighlight(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Color originalColor = button.getBackground();

                // !!! Преобразуем RGB в HSL
                float[] hsl = Color.RGBtoHSB(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), null);

                // !!! Увеличиваем яркость (L) на 20%
                float newLightness = Math.min(hsl[2] + 0.2f, 1.0f); //  Не превышаем максимальную яркость (1.0)

                // !!! Преобразуем HSL обратно в RGB
                Color brighterColor = Color.getHSBColor(hsl[0], hsl[1], newLightness);

                button.setBackground(brighterColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Color brighterColor = button.getBackground();

                // !!! Преобразуем RGB в HSL
                float[] hsl = Color.RGBtoHSB(brighterColor.getRed(), brighterColor.getGreen(), brighterColor.getBlue(), null);

                // !!! Уменьшаем яркость (L) на 20%
                float originalLightness = Math.max(hsl[2] - 0.2f, 0.0f); //  Не опускаемся ниже минимальной яркости (0.0)

                // !!! Преобразуем HSL обратно в RGB
                Color originalColor = Color.getHSBColor(hsl[0], hsl[1], originalLightness);

                button.setBackground(originalColor);
            }
        });
    }

    private void showSubstitutionDialog(BigDecimal teamId, JButton[] playerButtons) {
        ArrayList<Player> teamPlayers = getPlayersForTeam(teamId);
        if (teamPlayers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "В команде нет игроков!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // !!! Список игроков, которые УЖЕ на площадке
        ArrayList<Player> playersOnCourt = new ArrayList<>();
        for (JButton button : playerButtons) {
            String playerIdStr = button.getActionCommand();
            if (playerIdStr != null && !playerIdStr.isEmpty()) {
                BigDecimal playerId = new BigDecimal(playerIdStr);
                for (Player player : teamPlayers) {
                    if (player.getId().equals(playerId)) {
                        playersOnCourt.add(player);
                        break;
                    }
                }
            }
        }

        // !!! Список игроков, которые НЕ на площадке
        ArrayList<Player> availablePlayers = new ArrayList<>(teamPlayers);
        availablePlayers.removeAll(playersOnCourt);

        // !!! Создаем JList только с доступными игроками
        JList<Player> playerList = new JList<>(availablePlayers.toArray(new Player[0])); 
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Создаём диалоговое окно
        JDialog substitutionDialog = new JDialog(this, "Замена игрока", true);
        substitutionDialog.setLayout(new BorderLayout());
        substitutionDialog.add(new JLabel("Выберите игрока для замены:"), BorderLayout.NORTH);
        substitutionDialog.add(new JScrollPane(playerList), BorderLayout.CENTER);

        // Кнопка "OK"
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            Player newPlayer = playerList.getSelectedValue();
            if (newPlayer != null) {
                if (selectedPlayerButton != null) {
                    int playerIndex = Arrays.asList(playerButtons).indexOf(selectedPlayerButton);

                    // Сохраняем статистику предыдущего игрока
                    if (playerStatsMap.containsKey(selectedPlayer.getId())) {
                        Stat stats = playerStatsMap.get(selectedPlayer.getId());
                        manager.saveStat(stats);
                    }
                    
                    if (selectedPlayer != null) { // Для замененного игрока
                        BigDecimal playerId = selectedPlayer.getId();
                        int timeOnCourt = playerTimeOnCourt.getOrDefault(playerId, 0);
                        playerTimeMap.put(playerId, playerTimeMap.getOrDefault(playerId, 0) + timeOnCourt);
                        playerTimeOnCourt.remove(playerId); // Удаляем игрока из playerTimeOnCourt
                    }
                    playerTimeOnCourt.put(newPlayer.getId(), 0); // Добавляем нового игрока в playerTimeOnCourt

                    // Привязываем кнопку к новому игроку
                    playerButtons[playerIndex].setText("<html><center>Игрок " + newPlayer.getNum() + "<br>" + newPlayer.getName() + "</center></html>");
                    playerButtons[playerIndex].setActionCommand(newPlayer.getId().toString());
                    selectedPlayer = newPlayer;
                    selectedPlayerTeam = newPlayer.getTeam();
                    updateEventsTextArea("Игрок " + selectedPlayer.getName() + " вышел на поле");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка: не выбран игрок для замены.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                substitutionDialog.dispose();
            }
        });
        substitutionDialog.add(okButton, BorderLayout.SOUTH);

        // Отображаем диалог
        substitutionDialog.pack();
        substitutionDialog.setLocationRelativeTo(this); 
        substitutionDialog.setVisible(true);
    }

    // Метод для обновления текстового поля событий
    private void updateEventsTextArea(String event) {
        eventsTextArea.append(event + "\n");
        eventsTextArea.setCaretPosition(eventsTextArea.getDocument().getLength());
    }

    // Метод для обновления счета на табло
    private void updateScoreLabel() {
        if (currentMatch != null) {
            scoreLabel.setText("Счет: " + currentMatch.getTeam1().getTeam_name() + " - " + scoreTeamA +
                    " : " + scoreTeamB + " - " + currentMatch.getTeam2().getTeam_name());
        }
    }

    // Метод для сброса счета
    private void resetScore() {
        scoreTeamA = 0;
        scoreTeamB = 0;
        updateScoreLabel();
    }
    
    private void createMatchReport() {
        if (currentMatch == null) {
            JOptionPane.showMessageDialog(this, "Матч не выбран!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Создаем диалог для отчета
        JDialog reportDialog = new JDialog(this, "Отчет о матче", true);
        reportDialog.setLayout(new BorderLayout());

        // Добавляем информацию о матче
        JPanel matchInfoPanel = new JPanel(new MigLayout("insets 5, wrap 2", "[][grow]", ""));
        matchInfoPanel.add(new JLabel("Матч:"));
        matchInfoPanel.add(new JLabel(currentMatch.getTeam1().getTeam_name() + " - " + currentMatch.getTeam2().getTeam_name()));
        matchInfoPanel.add(new JLabel("Счет:"));
        matchInfoPanel.add(new JLabel(scoreTeamA + " : " + scoreTeamB));

        // Добавляем информацию о счете по четвертям
        for (int i = 0; i < 4; i++) {
            matchInfoPanel.add(new JLabel("Четверть " + (i + 1) + ":"));
            matchInfoPanel.add(new JLabel(quarterScoresTeamA[i] + " : " + quarterScoresTeamB[i]));
        }
        reportDialog.add(matchInfoPanel, BorderLayout.NORTH);

        // Создаем панель для таблиц команд
        JPanel teamsPanel = new JPanel(new GridLayout(1, 2)); 

        // Создаем таблицу для команды A
        JPanel teamAPanel = new JPanel(new BorderLayout());
        teamAPanel.add(new JLabel("Команда A: " + currentMatch.getTeam1().getTeam_name()), BorderLayout.NORTH);
        JTable teamATable = createTeamTable(currentMatch.getTeam1().getId_team());
        teamAPanel.add(new JScrollPane(teamATable), BorderLayout.CENTER);
        teamsPanel.add(teamAPanel);

        // Создаем таблицу для команды B
        JPanel teamBPanel = new JPanel(new BorderLayout());
        teamBPanel.add(new JLabel("Команда B: " + currentMatch.getTeam2().getTeam_name()), BorderLayout.NORTH);
        JTable teamBTable = createTeamTable(currentMatch.getTeam2().getId_team());
        teamBPanel.add(new JScrollPane(teamBTable), BorderLayout.CENTER);
        teamsPanel.add(teamBPanel);

        reportDialog.add(teamsPanel, BorderLayout.CENTER);

        
     // Панель для кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Добавляем кнопку "Сохранить отчет"
        JButton saveButton = new JButton("Сохранить отчет");
        saveButton.addActionListener(e -> saveMatchReportToWordFile());
        buttonPanel.add(saveButton);

        // Добавляем кнопку "Закрыть"
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> reportDialog.dispose());
        buttonPanel.add(closeButton);

        reportDialog.add(buttonPanel, BorderLayout.SOUTH); // Добавляем панель с кнопками
        
     

        reportDialog.pack();
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setVisible(true);
    }

    private JTable createTeamTable(BigDecimal teamId) {
    	
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Игрок");
        model.addColumn("СВ");
        model.addColumn("Очки");
        model.addColumn("2-очк");
        model.addColumn("3-очк");
        model.addColumn("ШБ");
        model.addColumn("СЩ");
        model.addColumn("ЧЩ");
        model.addColumn("ВС");
        model.addColumn("Ф");
        model.addColumn("Перехваты"); 
        model.addColumn("Потери");  

        // Добавляем игроков команды в таблицу
        for (Player player : manager.loadPlayer()) {
            if (player.getTeam().getId_team().equals(teamId)) {
                Stat playerStats = manager.loadStatForPlayerAndMatch(player.getId(), currentMatch.getId_matches());

                String playingTime = "00:00"; // Время на площадке по умолчанию

                if (playerStats != null) {
                    // Получаем время игры из playerTimeMap
                    Integer playerTimeSeconds = playerTimeMap.get(player.getId());
                    if (playerTimeSeconds != null) {
                        playingTime = String.format("%02d:%02d", playerTimeSeconds / 60, playerTimeSeconds % 60);
                    }

                 // Получаем количество промахов игрока
                    int twoPointMisses = playerTwoPointMisses.getOrDefault(player.getId(), 0);
                    int threePointMisses = playerThreePointMisses.getOrDefault(player.getId(), 0);
                    int freeThrowMisses = playerFreeThrowMisses.getOrDefault(player.getId(), 0);

                    // Вычисляем общее количество бросков
                    int twoPointAttempts = playerStats.getDoubleDouble() + twoPointMisses;
                    int threePointAttempts = playerStats.getTriple() + threePointMisses;
                    int freeThrowAttempts = playerStats.getFreeThrow() + freeThrowMisses;

                    model.addRow(new Object[]{
                            player.getName(),
                            playingTime, // Выводим время игры
                            playerStats.getPointScored(),
                            playerStats.getDoubleDouble() + "/" + twoPointAttempts,
                            playerStats.getTriple() + "/" + threePointAttempts,
                            playerStats.getFreeThrow() + "/" + freeThrowAttempts,
                            playerStats.getDr(),
                            playerStats.getOr(),
                            playerStats.getDr() + playerStats.getOr(),
                            playerStats.getFoul(),
                            playerStats.getSteal(),
                            playerStats.getTurnover()  
                    });
                } else {
                    // Игрок не выходил на площадку - добавляем нулевые значения
                    model.addRow(new Object[]{
                            player.getName(), playingTime, 0, "0/0", "0/0", "0/0", 0, 0, 0, 0, 0, 0
                    });
                }
            }
        }

        return new JTable(model);
    }
    private void saveMatchReportToWordFile() {
        if (currentMatch == null) {
            return;
        }

        // Создаем диалог выбора файла
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить отчет о матче в Word");
        fileChooser.setSelectedFile(new java.io.File("match_report.docx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (XWPFDocument document = new XWPFDocument();
                 FileOutputStream out = new FileOutputStream(fileToSave)) {

                // Записываем информацию о матче
                XWPFParagraph matchInfo = document.createParagraph();
                matchInfo.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun matchInfoRun = matchInfo.createRun();
                matchInfoRun.setText("Матч: " + currentMatch.getTeam1().getTeam_name() + " - " + 
                                     currentMatch.getTeam2().getTeam_name());
                matchInfoRun.addBreak();
                matchInfoRun.setText("Счет: " + scoreTeamA + " : " + scoreTeamB);
                matchInfoRun.addBreak();

                // Записываем информацию о счете по четвертям
                XWPFParagraph quarterScores = document.createParagraph();
                quarterScores.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun quarterScoresRun = quarterScores.createRun();
                quarterScoresRun.setText("Счет по четвертям:");
                quarterScoresRun.addBreak();
                for (int i = 0; i < 4; i++) {
                    quarterScoresRun.setText("Четверть " + (i + 1) + ": " + quarterScoresTeamA[i] + " : " + 
                                            quarterScoresTeamB[i]);
                    quarterScoresRun.addBreak();
                }

                // Записываем данные игроков команды A
                writeTeamDataToWordTable(document, currentMatch.getTeam1().getId_team(), "Команда A: " + currentMatch.getTeam1().getTeam_name());

                // Записываем данные игроков команды B
                writeTeamDataToWordTable(document, currentMatch.getTeam2().getId_team(), "Команда B: " + currentMatch.getTeam2().getTeam_name());

                document.write(out);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения отчета: " + ex.getMessage(), "Ошибка", 
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void writeTeamDataToWordTable(XWPFDocument document, BigDecimal teamId, String teamName) {
    	// Добавляем название команды перед таблицей
        XWPFParagraph teamParagraph = document.createParagraph();
        teamParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun teamParagraphRun = teamParagraph.createRun();
        teamParagraphRun.setBold(true); 
        teamParagraphRun.setText(teamName); 
        teamParagraphRun.addBreak();
        
    	// Создаем таблицу
        XWPFTable table = document.createTable();
        table.setWidth("100%");

        // Создаем заголовок таблицы
        XWPFTableRow headerRow = table.getRow(0); // Получаем первую строку (заголовок)
        headerRow.getCell(0).setText("Игрок");
        headerRow.addNewTableCell().setText("СВ");
        headerRow.addNewTableCell().setText("Очки");
        headerRow.addNewTableCell().setText("2-очк");
        headerRow.addNewTableCell().setText("3-очк");
        headerRow.addNewTableCell().setText("ШБ");
        headerRow.addNewTableCell().setText("СЩ");
        headerRow.addNewTableCell().setText("ЧЩ");
        headerRow.addNewTableCell().setText("ВС");
        headerRow.addNewTableCell().setText("Ф");
        headerRow.addNewTableCell().setText("Перехваты"); 
        headerRow.addNewTableCell().setText("Потери");   

        // Добавляем игроков команды в таблицу
        for (Player player : manager.loadPlayer()) {
            if (player.getTeam().getId_team().equals(teamId)) {
                Stat playerStats = manager.loadStatForPlayerAndMatch(player.getId(), currentMatch.getId_matches());
                String playingTime = "00:00";

                XWPFTableRow row = table.createRow(); // Создаем новую строку для игрока

                if (playerStats != null) {
                    Integer playerTimeSeconds = playerTimeMap.get(player.getId());
                    if (playerTimeSeconds != null) {
                        playingTime = String.format("%02d:%02d", playerTimeSeconds / 60, playerTimeSeconds % 60);
                    }

                    int twoPointMisses = playerTwoPointMisses.getOrDefault(player.getId(), 0);
                    int threePointMisses = playerThreePointMisses.getOrDefault(player.getId(), 0);
                    int freeThrowMisses = playerFreeThrowMisses.getOrDefault(player.getId(), 0);

                    // !!!  Вычисляем общее количество бросков
                    int twoPointAttempts = playerStats.getDoubleDouble() + twoPointMisses;
                    int threePointAttempts = playerStats.getTriple() + threePointMisses;
                    int freeThrowAttempts = playerStats.getFreeThrow() + freeThrowMisses;

                    row.getCell(0).setText(player.getName());
                    row.getCell(1).setText(playingTime);
                    row.getCell(2).setText(String.valueOf(playerStats.getPointScored()));
                    row.getCell(3).setText(playerStats.getDoubleDouble() + "/" + twoPointAttempts);
                    row.getCell(4).setText(playerStats.getTriple() + "/" + threePointAttempts);
                    row.getCell(5).setText(playerStats.getFreeThrow() + "/" + freeThrowAttempts);
                    row.getCell(6).setText(String.valueOf(playerStats.getDr()));
                    row.getCell(7).setText(String.valueOf(playerStats.getOr()));
                    row.getCell(8).setText(String.valueOf(playerStats.getDr() + playerStats.getOr()));
                    row.getCell(9).setText(String.valueOf(playerStats.getFoul()));
                    row.getCell(10).setText(String.valueOf(playerStats.getSteal())); 
                    row.getCell(11).setText(String.valueOf(playerStats.getTurnover())); 
                } else {
                    // Игрок не выходил на площадку - добавляем нулевые значения
                    row.getCell(0).setText(player.getName());
                    row.getCell(1).setText(playingTime);
                    row.getCell(2).setText("0");
                    row.getCell(3).setText("0/0");
                    row.getCell(4).setText("0/0");
                    row.getCell(5).setText("0/0");
                    row.getCell(6).setText("0");
                    row.getCell(7).setText("0");
                    row.getCell(8).setText("0");
                    row.getCell(9).setText("0");
                    row.getCell(10).setText("0");
                    row.getCell(11).setText("0");
                }
            }
        }

         
    }

    protected void ShowRpt() throws ParseException {
        final RptParamsDialog dlg = new RptParamsDialog(this);
        if (dlg.showDialog() != JDialogResult.OK)
            return;
        RptParams params = dlg.getParams();
        JasperPrint jp = null;
        ArrayList<RptData> reportData = manager.getDataReport(params);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(reportData);
        HashMap<String, Object> map = new HashMap<>();
        map.put("p_startDate", params.StartDate);
        map.put("p_endDate", params.EndDate);
        String reportName = "reports/" + "rptKurs_RIALSRielt.jasper";
        try {
            jp = JasperFillManager.fillReport(reportName, map, ds);
        } catch (JRException e) {
            e.printStackTrace();
        }
        JasperViewer viewer = new JasperViewer(jp, false);
        viewer.setTitle("Отчет о стоимости аренд и покупок клиентов");
        viewer.setVisible(false);
    }

    public void initialize() {
        Locale.setDefault(new Locale("ru"));
    }

    public static void main(String[] args) {
    	try {
	        // Устанавливаем тему Nimbus
	        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 13)); // Изменяем шрифт кнопок
			UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 12)); // Изменяем шрифт меток
			
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
        PgAppBasket app = new PgAppBasket();
        app.run();
    }
}