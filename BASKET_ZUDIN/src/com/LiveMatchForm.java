package com;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.data.*;
import net.miginfocom.swing.MigLayout;
import org.apache.poi.xwpf.usermodel.*;

public class LiveMatchForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private final ApiClient apiClient = new ApiClient();

    private JButton[] playerButtonsA = new JButton[5];
    private JButton[] playerButtonsB = new JButton[5];
    private JButton substitutionButtonA;
    private JButton substitutionButtonB;

    private Matches currentMatch;
    private List<Player> activePlayersTeamA;
    private List<Player> activePlayersTeamB;
    private Player selectedPlayer = null;
    
    // УДАЛЕНО: private Team selectedPlayerTeam; - это поле ненадежно
    private JButton selectedPlayerButton = null;
    private JTextArea eventsTextArea;
    
    private HashMap<Integer, Stat> playerStatsMap = new HashMap<>();

    private JLabel scoreLabel;
    private int scoreTeamA = 0;
    private int scoreTeamB = 0;
    private int[] quarterScoresTeamA = new int[4];
    private int[] quarterScoresTeamB = new int[4];
    private int currentQuarter = 0;
    private boolean quarterStarted = false;
    private JButton startQuarterButton, endQuarterButton, finishMatchButton;
    private JComboBox<String> quarterComboBox;
    
    private Timer quarterTimer;
    private int quarterTimeSeconds = 600;
    private JLabel quarterTimerLabel;
    private HashMap<Integer, Integer> playerTimeOnCourt = new HashMap<>();
    private boolean timerPaused = false;
    private JButton pauseTimerButton, resumeTimerButton;
    
    private HashMap<Integer, Integer> playerTimeMap = new HashMap<>();
    private HashMap<Integer, Integer> playerTwoPointMisses = new HashMap<>();
    private HashMap<Integer, Integer> playerThreePointMisses = new HashMap<>();
    private HashMap<Integer, Integer> playerFreeThrowMisses = new HashMap<>();
    
    private ImageIcon basketballIcon;
    private JButton currentlySelectedPlayerButton = null;

    public LiveMatchForm(Matches match, List<Player> rosterA, List<Player> rosterB) {
        this.currentMatch = match;
        this.activePlayersTeamA = rosterA;
        this.activePlayersTeamB = rosterB;
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1320, 830);
        setTitle("StatBasket - Ведение матча: " + match.getTeam1().getName() + " vs " + match.getTeam2().getName());
        setLocationRelativeTo(null);

        this.scoreTeamA = match.getTeam1Score() != null ? match.getTeam1Score() : 0;
        this.scoreTeamB = match.getTeam2Score() != null ? match.getTeam2Score() : 0;

        loadIcon();
        createGUI();
        bindListeners();
        
        updatePlayerButtons();
        updateScoreLabel();
    }

    // ИЗМЕНЕН: Теперь проверка идет по отфильтрованному списку
    private boolean isPlayerInTeam1(Integer playerId) {
        for(Player p : activePlayersTeamA) {
            if(p.getId().equals(playerId)) return true;
        }
        return false;
    }

    private void loadIcon() {
        URL basketballImageUrl = MainWindow.class.getResource("/images/ball2.png"); 
        if (basketballImageUrl != null) {
            try {
                BufferedImage originalImage = ImageIO.read(basketballImageUrl);
                Image scaledImage = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                BufferedImage transparentImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = transparentImage.createGraphics();
                g2d.drawImage(scaledImage, 0, 0, null);
                g2d.dispose();
                basketballIcon = new ImageIcon(transparentImage);
            } catch (IOException ex) {
                System.err.println("Не удалось загрузить картинку basketball.png");
            }
        }
    }

    private void createGUI() {
        setLayout(new GridBagLayout());
        // ... (Код создания панелей остался тем же, для краткости не дублирую полностью стандартную разметку) ...
        // ВАЖНО: Вставьте сюда код создания панелей из предыдущего ответа, 
        // или если он у вас сохранился, оставьте как есть. 
        // Главное изменение - в методе createStatButton ниже.
        
        // --- ВОССТАНОВЛЕНИЕ GUI (Кратко) ---
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints leftGbc = new GridBagConstraints();
        for (int i = 0; i < 5; i++) {
            playerButtonsA[i] = createPlayerButton("Игрок " + (i + 1), 110, 70);
            leftGbc.gridx = 0; leftGbc.gridy = i; leftGbc.fill = GridBagConstraints.BOTH; leftGbc.weightx = 1.0;
            leftPanel.add(playerButtonsA[i], leftGbc);
        }
        substitutionButtonA = createSubstitutionButton("Замена " + currentMatch.getTeam1().getName(), 150, 70);
        leftGbc.gridy = 6; leftGbc.insets = new Insets(20, 0, 0, 0);
        leftPanel.add(substitutionButtonA, leftGbc);

        JPanel middlePanel = new JPanel(new BorderLayout());
        JPanel scorePanel = new JPanel(new FlowLayout());
        scoreLabel = new JLabel("Счет: 0 : 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel);
        middlePanel.add(scorePanel, BorderLayout.NORTH);

        eventsTextArea = new JTextArea(20, 30);
        eventsTextArea.setFont(new Font("Arial", Font.BOLD, 12));
        eventsTextArea.setEditable(false);
        middlePanel.add(new JScrollPane(eventsTextArea), BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        String[] buttonLabels = {"1 очко", "1 очко\nмимо", "2 очка", "2 очка\nмимо", "3 очка", "3 очка\nмимо"};
        for (String label : buttonLabels) statsPanel.add(createStatButton(label, 90, 90));
        middlePanel.add(statsPanel, BorderLayout.WEST);

        JPanel extraButtonsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        String[] extraButtonLabels = {"подбор\nв защите", "подбор\nв атаке", "блокшот", "фол", "ассист", "перехват"};
        for (String label : extraButtonLabels) extraButtonsPanel.add(createStatButton(label, 90, 90));
        middlePanel.add(extraButtonsPanel, BorderLayout.EAST);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rightGbc = new GridBagConstraints();
        for (int i = 0; i < 5; i++) {
            playerButtonsB[i] = createPlayerButton("Игрок " + (i + 1), 110, 70);
            rightGbc.gridx = 0; rightGbc.gridy = i; rightGbc.fill = GridBagConstraints.BOTH; rightGbc.weightx = 1.0;
            rightPanel.add(playerButtonsB[i], rightGbc);
        }
        substitutionButtonB = createSubstitutionButton("Замена " + currentMatch.getTeam2().getName(), 150, 70);
        rightGbc.gridy = 6; rightGbc.insets = new Insets(20, 0, 0, 0);
        rightPanel.add(substitutionButtonB, rightGbc);

        JPanel bottomPanel = new JPanel(new MigLayout("insets 5, flowy", "[grow][]", "[]5[]"));
        quarterComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        startQuarterButton = new JButton("Начать четверть");
        endQuarterButton = new JButton("Завершить четверть");
        finishMatchButton = new JButton("Завершить матч");
        quarterTimerLabel = new JLabel("10:00");
        quarterTimerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        pauseTimerButton = new JButton("Пауза");
        resumeTimerButton = new JButton("Продолжить");
        pauseTimerButton.setEnabled(false); resumeTimerButton.setEnabled(false);

        // Цвета
        startQuarterButton.setBackground(new Color(102, 204, 102));
        finishMatchButton.setBackground(new Color(253, 51, 51));

        bottomPanel.add(new JLabel("ЧЕТВЕРТЬ"), "cell 0 0, align center");
        bottomPanel.add(quarterComboBox, "cell 0 1, width 60!, align center");
        bottomPanel.add(startQuarterButton, "cell 0 1, width 140!, growx 0, align center");
        bottomPanel.add(endQuarterButton, "cell 0 1, growx 0, align center");
        bottomPanel.add(finishMatchButton, "cell 1 0, growx");
        bottomPanel.add(quarterTimerLabel, "cell 0 1, align center");
        bottomPanel.add(pauseTimerButton, "cell 0 1, width 140!, growx 0, align center");
        bottomPanel.add(resumeTimerButton, "cell 0 1, width 140!, growx 0, align center");

        JPanel centralPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.gridx = 0; cgbc.gridy = 0; cgbc.weightx = 0.3; cgbc.fill = GridBagConstraints.BOTH;
        centralPanel.add(leftPanel, cgbc);
        cgbc.gridx = 1; cgbc.weightx = 0.4;
        centralPanel.add(middlePanel, cgbc);
        cgbc.gridx = 2; cgbc.weightx = 0.3;
        centralPanel.add(rightPanel, cgbc);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        add(centralPanel, gbc); 
        gbc.gridy = 3; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottomPanel, gbc);
    }

    // --- ИСПРАВЛЕННАЯ ЛОГИКА КНОПКИ ИГРОКА ---
    private JButton createPlayerButton(String text, int width, int height) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setPreferredSize(new Dimension(width, height));
        button.addActionListener(e -> {
            JButton btn = (JButton) e.getSource();
            String playerIdStr = btn.getActionCommand();
            
            if (playerIdStr != null && !playerIdStr.isEmpty()) {
                Integer playerId = Integer.valueOf(playerIdStr);
                selectedPlayer = findPlayerInMatch(playerId);
                
                if (selectedPlayer != null) {
                    selectedPlayerButton = btn;
                    updateEventsTextArea("Выбран игрок: " + selectedPlayer.getName());
                    
                    if (basketballIcon != null) {
                        if (currentlySelectedPlayerButton != null) currentlySelectedPlayerButton.setIcon(null);
                        btn.setIcon(basketballIcon);
                        currentlySelectedPlayerButton = btn;
                    }
                }
            }
        });
        return button;
    }

    // --- ИСПРАВЛЕННАЯ ЛОГИКА КНОПКИ СТАТИСТИКИ ---
    private JButton createStatButton(String text, int width, int height) {
        JButton button = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        button.setPreferredSize(new Dimension(width, height)); 
        button.addActionListener(e -> {
            if (selectedPlayer == null) {
                JOptionPane.showMessageDialog(this, "Сначала выберите игрока!", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!quarterStarted) {
                JOptionPane.showMessageDialog(this, "Сначала начните четверть!", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Получаем статистику из кэша или создаем новую
            Stat playerStats = playerStatsMap.computeIfAbsent(selectedPlayer.getId(), k -> {
                Stat s = new Stat();
                s.setPlayer(selectedPlayer);
                s.setIdPlayer(selectedPlayer.getId());
                s.setIdMatch(currentMatch.getId());
                return s;
            });

            // ИСПРАВЛЕНО: Определяем команду надежным способом
            boolean isTeamA = isPlayerInTeam1(selectedPlayer.getId());

            switch (text) {
                case "1 очко":
                    playerStats.setPointScored(getSafeInt(playerStats.getPointScored()) + 1);
                    playerStats.setFreeThrow(getSafeInt(playerStats.getFreeThrow()) + 1);
                    if (isTeamA) scoreTeamA++; else scoreTeamB++;
                    updateEventsTextArea(selectedPlayer.getName() + ": +1 очко");
                    break;
                case "1 очко\nмимо":
                    playerFreeThrowMisses.put(selectedPlayer.getId(), playerFreeThrowMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": промах 1 очко");
                    break;
                case "2 очка":
                    playerStats.setPointScored(getSafeInt(playerStats.getPointScored()) + 2);
                    playerStats.setDoubleDouble(getSafeInt(playerStats.getDoubleDouble()) + 1);
                    if (isTeamA) scoreTeamA += 2; else scoreTeamB += 2;
                    updateEventsTextArea(selectedPlayer.getName() + ": +2 очка");
                    break;
                case "2 очка\nмимо":
                    playerTwoPointMisses.put(selectedPlayer.getId(), playerTwoPointMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": промах 2 очка");
                    break;
                case "3 очка":
                    playerStats.setPointScored(getSafeInt(playerStats.getPointScored()) + 3);
                    playerStats.setTriple(getSafeInt(playerStats.getTriple()) + 1);
                    if (isTeamA) scoreTeamA += 3; else scoreTeamB += 3;
                    updateEventsTextArea(selectedPlayer.getName() + ": +3 очка");
                    break;
                case "3 очка\nмимо":
                    playerThreePointMisses.put(selectedPlayer.getId(), playerThreePointMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": промах 3 очка");
                    break;
                case "подбор\nв защите":
                    playerStats.setDr(getSafeInt(playerStats.getDr()) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": подбор (защ)");
                    break;
                case "подбор\nв атаке":
                    playerStats.setOr(getSafeInt(playerStats.getOr()) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": подбор (атк)");
                    break;
                case "блокшот":
                    playerStats.setBlockedShot(getSafeInt(playerStats.getBlockedShot()) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": блокшот");
                    break;
                case "фол":
                    playerStats.setFoul(getSafeInt(playerStats.getFoul()) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": фол");
                    break;
                case "ассист":
                    playerStats.setAssists(getSafeInt(playerStats.getAssists()) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": ассист");
                    break;
                case "перехват":
                    playerStats.setSteal(getSafeInt(playerStats.getSteal()) + 1);
                    updateEventsTextArea(selectedPlayer.getName() + ": перехват");
                    showOpponentPlayerDialog();
                    break;
            }
            updateScoreLabel();
        });
        return button;
    }

    // --- ОСТАЛЬНЫЕ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ---

    private int getSafeInt(Integer val) { return val == null ? 0 : val; }

    private Player findPlayerInMatch(Integer playerId) {
        for (Player p : currentMatch.getTeam1().getPlayers()) if (p.getId().equals(playerId)) return p;
        for (Player p : currentMatch.getTeam2().getPlayers()) if (p.getId().equals(playerId)) return p;
        return null;
    }

    private void updatePlayerButtons() {
        if (currentMatch != null) {
            for (int i = 0; i < 5; i++) {
                if (i < activePlayersTeamA.size()) {
                    playerButtonsA[i].setText("<html><center>Игрок " + activePlayersTeamA.get(i).getGameNumber() + "<br>" + activePlayersTeamA.get(i).getName() + "</center></html>");
                    playerButtonsA[i].setActionCommand(activePlayersTeamA.get(i).getId().toString());
                } else {
                    playerButtonsA[i].setText("...");
                    playerButtonsA[i].setActionCommand("");
                }
                
                if (i < activePlayersTeamB.size()) {
                    playerButtonsB[i].setText("<html><center>Игрок " + activePlayersTeamB.get(i).getGameNumber() + "<br>" + activePlayersTeamB.get(i).getName() + "</center></html>");
                    playerButtonsB[i].setActionCommand(activePlayersTeamB.get(i).getId().toString());
                } else {
                    playerButtonsB[i].setText("...");
                    playerButtonsB[i].setActionCommand("");
                }
            }
        }
    }

    private void showOpponentPlayerDialog() {
        boolean isSelectedTeamA = isPlayerInTeam1(selectedPlayer.getId());
        List<Player> opponentTeamPlayers = isSelectedTeamA ? activePlayersTeamB : activePlayersTeamA;
        
        // Фильтруем тех, кто на кнопках (на поле)
        JButton[] opponentButtons = isSelectedTeamA ? playerButtonsB : playerButtonsA;
        DefaultListModel<Player> opponentPlayersModel = new DefaultListModel<>();
        
        for (int i = 0; i < 5; i++) {
            String pidStr = opponentButtons[i].getActionCommand();
            if (pidStr != null && !pidStr.isEmpty()) {
                Integer pid = Integer.valueOf(pidStr);
                opponentTeamPlayers.stream().filter(p -> p.getId().equals(pid)).findFirst().ifPresent(opponentPlayersModel::addElement);
            }
        }

        JList<Player> list = new JList<>(opponentPlayersModel);
        JDialog d = new JDialog(this, "Кто потерял мяч?", true);
        d.add(new JScrollPane(list));
        JButton ok = new JButton("OK");
        ok.addActionListener(ev -> {
            Player opp = list.getSelectedValue();
            if (opp != null) {
                Stat s = playerStatsMap.computeIfAbsent(opp.getId(), k -> {
                    Stat stat = new Stat(); stat.setPlayer(opp); stat.setIdPlayer(opp.getId()); stat.setIdMatch(currentMatch.getId()); return stat;
                });
                s.setTurnover(getSafeInt(s.getTurnover()) + 1);
                updateEventsTextArea(opp.getName() + ": потеря");
            }
            d.dispose();
        });
        d.add(ok, BorderLayout.SOUTH);
        d.pack(); d.setLocationRelativeTo(this); d.setVisible(true);
    }

    private JButton createSubstitutionButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(new Color(128, 128, 128));
        return button;
    }

    private void bindListeners() {
        substitutionButtonA.addActionListener(e -> handleSubstitution(currentMatch.getTeam1(), playerButtonsA));
        substitutionButtonB.addActionListener(e -> handleSubstitution(currentMatch.getTeam2(), playerButtonsB));

        startQuarterButton.addActionListener(e -> {
            int selectedQuarter = quarterComboBox.getSelectedIndex() + 1;
            if (selectedQuarter > currentQuarter && !quarterStarted) {
                currentQuarter = selectedQuarter;
                quarterStarted = true;
                updateEventsTextArea("=== НАЧАЛО " + currentQuarter + " ЧЕТВЕРТИ ===");
                startQuarterTimer();
                resetQuarterTimer();
                quarterTimer.start();
                pauseTimerButton.setEnabled(true);
                resumeTimerButton.setEnabled(false);
            }
        });

        endQuarterButton.addActionListener(e -> {
            if (quarterStarted) {
                quarterScoresTeamA[currentQuarter - 1] = scoreTeamA;
                quarterScoresTeamB[currentQuarter - 1] = scoreTeamB;
                quarterStarted = false;
                updateEventsTextArea("=== КОНЕЦ ЧЕТВЕРТИ. СЧЕТ: " + scoreTeamA + " : " + scoreTeamB + " ===");
                // Обновляем время на площадке
                for (Map.Entry<Integer, Integer> entry : playerTimeOnCourt.entrySet()) {
                    playerTimeMap.put(entry.getKey(), playerTimeMap.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
                resetQuarterTimer();
            }
        });

        finishMatchButton.addActionListener(e -> {
            // Разрешаем завершать после любой четверти для тестов, но лучше проверять currentQuarter == 4
            int confirm = JOptionPane.showConfirmDialog(this, "Завершить матч и отправить данные на сервер?", "Завершение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (quarterStarted) {
                    JOptionPane.showMessageDialog(this, "Сначала завершите текущую четверть!");
                    return;
                }
                
                currentMatch.setTeam1Score(scoreTeamA);
                currentMatch.setTeam2Score(scoreTeamB);
                currentMatch.setPlayerStats(new ArrayList<>(playerStatsMap.values()));

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        apiClient.saveMatch(currentMatch);
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            get();
                            JOptionPane.showMessageDialog(LiveMatchForm.this, "Матч успешно сохранен!");
                            createMatchReport(); // Показываем отчет
                            dispose();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(LiveMatchForm.this, "Ошибка сохранения: " + ex.getMessage());
                        }
                    }
                };
                worker.execute();
            }
        });

        pauseTimerButton.addActionListener(e -> {
            if (quarterStarted && !timerPaused) {
                quarterTimer.stop();
                timerPaused = true;
                updateEventsTextArea("Таймер остановлен");
                resumeTimerButton.setEnabled(true);
                pauseTimerButton.setEnabled(false); 
            }
        });

        resumeTimerButton.addActionListener(e -> {
            if (quarterStarted && timerPaused) {
                quarterTimer.start();
                timerPaused = false;
                updateEventsTextArea("Таймер запущен");
                pauseTimerButton.setEnabled(true); 
                resumeTimerButton.setEnabled(false);
            }
        });
    }

    private void handleSubstitution(Team team, JButton[] playerButtons) {
        if (selectedPlayerButton == null || !Arrays.asList(playerButtons).contains(selectedPlayerButton)) {
            JOptionPane.showMessageDialog(this, "Выберите игрока на площадке (нажмите кнопку игрока), которого хотите заменить.", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Player> teamPlayers = team.getId().equals(currentMatch.getTeam1().getId()) ? activePlayersTeamA : activePlayersTeamB;
        List<Player> playersOnCourt = new ArrayList<>();
        
        for (JButton button : playerButtons) {
            String pidStr = button.getActionCommand();
            if (pidStr != null && !pidStr.isEmpty()) {
                Integer pid = Integer.valueOf(pidStr);
                teamPlayers.stream().filter(p -> p.getId().equals(pid)).findFirst().ifPresent(playersOnCourt::add);
            }
        }

        List<Player> availablePlayers = new ArrayList<>(teamPlayers);
        availablePlayers.removeAll(playersOnCourt);

        DefaultListModel<Player> model = new DefaultListModel<>();
        for(Player p : availablePlayers) model.addElement(p);
        
        JList<Player> list = new JList<>(model);
        JDialog d = new JDialog(this, "Замена", true);
        d.add(new JScrollPane(list));
        JButton ok = new JButton("OK");
        ok.addActionListener(ev -> {
            Player newP = list.getSelectedValue();
            if (newP != null) {
                // Сохраняем время уходящего
                if (selectedPlayer != null) {
                    int time = playerTimeOnCourt.getOrDefault(selectedPlayer.getId(), 0);
                    playerTimeMap.put(selectedPlayer.getId(), playerTimeMap.getOrDefault(selectedPlayer.getId(), 0) + time);
                    playerTimeOnCourt.remove(selectedPlayer.getId());
                }
                playerTimeOnCourt.put(newP.getId(), 0);

                selectedPlayerButton.setText("<html><center>" + newP.getGameNumber() + "<br>" + newP.getName() + "</center></html>");
                selectedPlayerButton.setActionCommand(newP.getId().toString());
                selectedPlayer = newP;
                updateEventsTextArea("Замена: вышел " + newP.getName());
                d.dispose();
            }
        });
        d.add(ok, BorderLayout.SOUTH);
        d.pack(); d.setLocationRelativeTo(this); d.setVisible(true);
    }

    private void startQuarterTimer() {
        quarterTimer = new Timer(1000, e -> {
            if (!timerPaused) {
                quarterTimeSeconds--;
                updateQuarterTimerLabel();
                for (int i = 0; i < 5; i++) {
                    String pidA = playerButtonsA[i].getActionCommand();
                    if (pidA != null && !pidA.isEmpty()) {
                        int id = Integer.valueOf(pidA);
                        playerTimeOnCourt.put(id, playerTimeOnCourt.getOrDefault(id, 0) + 1);
                    }
                    String pidB = playerButtonsB[i].getActionCommand();
                    if (pidB != null && !pidB.isEmpty()) {
                        int id = Integer.valueOf(pidB);
                        playerTimeOnCourt.put(id, playerTimeOnCourt.getOrDefault(id, 0) + 1);
                    }
                }
                if (quarterTimeSeconds == 0) endQuarterButton.doClick(); 
            }
        });
        quarterTimer.start();
    }

    private void stopQuarterTimer() { if (quarterTimer != null) quarterTimer.stop(); }
    
    private void resetQuarterTimer() {
        stopQuarterTimer();
        quarterTimeSeconds = 600; 
        updateQuarterTimerLabel();
        playerTimeOnCourt.clear();
    }

    private void updateQuarterTimerLabel() {
        quarterTimerLabel.setText(String.format("%02d:%02d", quarterTimeSeconds / 60, quarterTimeSeconds % 60));
    }

    private void updateEventsTextArea(String event) {
        eventsTextArea.append(event + "\n");
        eventsTextArea.setCaretPosition(eventsTextArea.getDocument().getLength());
    }

    private void updateScoreLabel() {
        if (currentMatch != null) {
            scoreLabel.setText("Счет: " + currentMatch.getTeam1().getName() + " - " + scoreTeamA +
                    " : " + scoreTeamB + " - " + currentMatch.getTeam2().getName());
        }
    }
    
    // --- ЛОГИКА ОТЧЕТОВ (Использует локальные данные) ---
    private void createMatchReport() {
        JDialog reportDialog = new JDialog(this, "Отчет о матче", true);
        reportDialog.setLayout(new BorderLayout());

        JPanel matchInfoPanel = new JPanel(new MigLayout("insets 5, wrap 2", "[][grow]", ""));
        matchInfoPanel.add(new JLabel("Матч:"));
        matchInfoPanel.add(new JLabel(currentMatch.getTeam1().getName() + " - " + currentMatch.getTeam2().getName()));
        matchInfoPanel.add(new JLabel("Счет:"));
        matchInfoPanel.add(new JLabel(scoreTeamA + " : " + scoreTeamB));

        for (int i = 0; i < 4; i++) {
            matchInfoPanel.add(new JLabel("Четверть " + (i + 1) + ":"));
            matchInfoPanel.add(new JLabel(quarterScoresTeamA[i] + " : " + quarterScoresTeamB[i]));
        }
        reportDialog.add(matchInfoPanel, BorderLayout.NORTH);

        JPanel teamsPanel = new JPanel(new GridLayout(1, 2)); 
        JPanel teamAPanel = new JPanel(new BorderLayout());
        teamAPanel.add(new JLabel("Команда A: " + currentMatch.getTeam1().getName()), BorderLayout.NORTH);
        teamAPanel.add(new JScrollPane(createTeamTable(currentMatch.getTeam1())), BorderLayout.CENTER);
        teamsPanel.add(teamAPanel);

        JPanel teamBPanel = new JPanel(new BorderLayout());
        teamBPanel.add(new JLabel("Команда B: " + currentMatch.getTeam2().getName()), BorderLayout.NORTH);
        teamBPanel.add(new JScrollPane(createTeamTable(currentMatch.getTeam2())), BorderLayout.CENTER);
        teamsPanel.add(teamBPanel);

        reportDialog.add(teamsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить отчет");
        saveButton.addActionListener(e -> saveMatchReportToWordFile());
        buttonPanel.add(saveButton);
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> reportDialog.dispose());
        buttonPanel.add(closeButton);
        reportDialog.add(buttonPanel, BorderLayout.SOUTH); 
        
        reportDialog.pack();
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setVisible(true);
    }

    private JTable createTeamTable(Team team) {
        DefaultTableModel model = new DefaultTableModel();
        String[] columns = {"Игрок", "СВ", "Очки", "2-очк", "3-очк", "ШБ", "СЩ", "ЧЩ", "ВС", "Ф", "Перехваты", "Потери"};
        for (String c : columns) model.addColumn(c);

        for (Player player : team.getPlayers()) {
            Stat playerStats = playerStatsMap.get(player.getId());
            String playingTime = "00:00";

            if (playerStats != null) {
                Integer pt = playerTimeMap.get(player.getId());
                if (pt != null) playingTime = String.format("%02d:%02d", pt / 60, pt % 60);

                int miss2 = playerTwoPointMisses.getOrDefault(player.getId(), 0);
                int miss3 = playerThreePointMisses.getOrDefault(player.getId(), 0);
                int missF = playerFreeThrowMisses.getOrDefault(player.getId(), 0);

                int att2 = getSafeInt(playerStats.getDoubleDouble()) + miss2;
                int att3 = getSafeInt(playerStats.getTriple()) + miss3;
                int attF = getSafeInt(playerStats.getFreeThrow()) + missF;

                model.addRow(new Object[]{
                        player.getName(), playingTime, getSafeInt(playerStats.getPointScored()),
                        getSafeInt(playerStats.getDoubleDouble()) + "/" + att2,
                        getSafeInt(playerStats.getTriple()) + "/" + att3,
                        getSafeInt(playerStats.getFreeThrow()) + "/" + attF,
                        getSafeInt(playerStats.getDr()), getSafeInt(playerStats.getOr()),
                        getSafeInt(playerStats.getDr()) + getSafeInt(playerStats.getOr()),
                        getSafeInt(playerStats.getFoul()), getSafeInt(playerStats.getSteal()), getSafeInt(playerStats.getTurnover())  
                });
            } else {
                model.addRow(new Object[]{player.getName(), playingTime, 0, "0/0", "0/0", "0/0", 0, 0, 0, 0, 0, 0});
            }
        }
        return new JTable(model);
    }

    private void saveMatchReportToWordFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить отчет о матче в Word");
        fileChooser.setSelectedFile(new java.io.File("match_report.docx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (XWPFDocument document = new XWPFDocument();
                 FileOutputStream out = new FileOutputStream(fileChooser.getSelectedFile())) {

                XWPFParagraph matchInfo = document.createParagraph();
                matchInfo.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = matchInfo.createRun();
                run.setText("Матч: " + currentMatch.getTeam1().getName() + " - " + currentMatch.getTeam2().getName());
                run.addBreak();
                run.setText("Счет: " + scoreTeamA + " : " + scoreTeamB);
                run.addBreak();

                writeTeamDataToWordTable(document, currentMatch.getTeam1());
                writeTeamDataToWordTable(document, currentMatch.getTeam2());

                document.write(out);
                JOptionPane.showMessageDialog(this, "Отчет сохранен!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения отчета: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void writeTeamDataToWordTable(XWPFDocument document, Team team) {
        XWPFParagraph tp = document.createParagraph();
        XWPFRun tr = tp.createRun();
        tr.setBold(true); 
        tr.setText("Команда: " + team.getName()); 
        
        XWPFTable table = document.createTable();
        table.setWidth("100%");
        XWPFTableRow headerRow = table.getRow(0);
        String[] cols = {"Игрок", "СВ", "Очки", "2-очк", "3-очк", "ШБ", "СЩ", "ЧЩ", "ВС", "Ф", "Перехваты", "Потери"};
        headerRow.getCell(0).setText(cols[0]);
        for(int i=1; i<cols.length; i++) headerRow.addNewTableCell().setText(cols[i]);

        for (Player player : team.getPlayers()) {
            Stat s = playerStatsMap.get(player.getId());
            XWPFTableRow row = table.createRow();
            if (s != null) {
                Integer pt = playerTimeMap.getOrDefault(player.getId(), 0);
                int att2 = getSafeInt(s.getDoubleDouble()) + playerTwoPointMisses.getOrDefault(player.getId(), 0);
                int att3 = getSafeInt(s.getTriple()) + playerThreePointMisses.getOrDefault(player.getId(), 0);
                int attF = getSafeInt(s.getFreeThrow()) + playerFreeThrowMisses.getOrDefault(player.getId(), 0);

                row.getCell(0).setText(player.getName());
                row.getCell(1).setText(String.format("%02d:%02d", pt / 60, pt % 60));
                row.getCell(2).setText(String.valueOf(getSafeInt(s.getPointScored())));
                row.getCell(3).setText(getSafeInt(s.getDoubleDouble()) + "/" + att2);
                row.getCell(4).setText(getSafeInt(s.getTriple()) + "/" + att3);
                row.getCell(5).setText(getSafeInt(s.getFreeThrow()) + "/" + attF);
                row.getCell(6).setText(String.valueOf(getSafeInt(s.getDr())));
                row.getCell(7).setText(String.valueOf(getSafeInt(s.getOr())));
                row.getCell(8).setText(String.valueOf(getSafeInt(s.getDr()) + getSafeInt(s.getOr())));
                row.getCell(9).setText(String.valueOf(getSafeInt(s.getFoul())));
                row.getCell(10).setText(String.valueOf(getSafeInt(s.getSteal()))); 
                row.getCell(11).setText(String.valueOf(getSafeInt(s.getTurnover()))); 
            } else {
                row.getCell(0).setText(player.getName());
                for(int i=1; i<12; i++) row.getCell(i).setText("0");
            }
        }
    }
}