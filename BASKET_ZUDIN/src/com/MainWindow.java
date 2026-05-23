package com;

import com.gui.LookAndFillUtil;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.data.Matches;

public class MainWindow extends javax.swing.JFrame {

    public MainWindow() {
        initComponents(); // Дизайнер инициализирует меню и пустую форму
        
        setupCustomUI();  // НАШ МЕТОД: добавляем кнопки поверх пустой формы!
        
        LookAndFillUtil.setMyLookAndFill();
        this.setLocationRelativeTo(null);
        this.setTitle("StatBasket: Панель управления");
        this.setSize(700, 500); // Задаем размер окна
    }

    // --- Метод, который дизайнер не удалит ---
    private void setupCustomUI() {
    	JPanel mainPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JButton btnNewMatch = createBigButton("НОВЫЙ МАТЧ", "🆕");
        btnNewMatch.addActionListener(e -> openNewMatchWizard());

        JButton btnMatches = createBigButton("СПИСОК МАТЧЕЙ", "📅");
        btnMatches.addActionListener(e -> openMatches());

        JButton btnTeams = createBigButton("КОМАНДЫ", "🏀");
        btnTeams.addActionListener(e -> openTeams());

        JButton btnPlayers = createBigButton("ИГРОКИ", "👤");
        btnPlayers.addActionListener(e -> openPlayers());
        
        // ----------------------------------

        mainPanel.add(btnNewMatch);
        mainPanel.add(btnMatches);
        mainPanel.add(btnTeams);
        mainPanel.add(btnPlayers);
        
        // 2. ДОБАВЛЯЕМ МЕНЮ "СЕРВИС" В ВЕРХНЮЮ ПАНЕЛЬ
        JMenu menuTools = new JMenu("Сервис");
        JMenuItem miSync = new JMenuItem("Синхронизация данных (Офлайн)");
        miSync.addActionListener(e -> syncOfflineData());
        menuTools.add(miSync);
        jMenuBar1.add(menuTools);
        
        // Перехватываем управление слоем у дизайнера
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        
        // Принудительно перерисовываем окно, чтобы кнопки появились
        this.revalidate();
        this.repaint();
    }

    private JButton createBigButton(String text, String icon) {
        JButton btn = new JButton("<html><center><font size='6'>" + icon + "</font><br>" + text + "</center></html>");
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setName(text); 
        return btn;
    }

    // --- Логика кнопок ---
    private void openNewMatchWizard() {
        NewMatchWizard wizard = new NewMatchWizard(this);
        wizard.setVisible(true);
        if (wizard.getDialogResult() == JDialogResult.OK) {
            com.data.Matches createdMatch = wizard.getNewMatch();
            
            // НОВОЕ: Получаем отфильтрованные списки игроков (только тех, кто остался в JList)
            java.util.List<com.data.Player> roster1 = wizard.getActivePlayersTeam1();
            java.util.List<com.data.Player> roster2 = wizard.getActivePlayersTeam2();
            
            // Передаем эти списки в форму матча
            LiveMatchForm liveForm = new LiveMatchForm(createdMatch, roster1, roster2);
            liveForm.setVisible(true);
        }
    }

    private void openMatches() {
        FrmMatches frm = new FrmMatches(this, true);
        frm.setVisible(true);
    }

    private void openTeams() {
        FrmTeam frm = new FrmTeam(this, true);
        frm.setVisible(true);
    }

    private void openPlayers() {
        FrmPlayer frm = new FrmPlayer(this, true);
        frm.setVisible(true);
    }
    
    private void syncOfflineData() {
        // Ищем папку в домашней директории пользователя
        String userHome = System.getProperty("user.home");
        java.io.File backupDir = new java.io.File(userHome, ".statbasket_backups");
        
        if (!backupDir.exists()) {
            JOptionPane.showMessageDialog(this, "Резервных файлов не найдено.", "Синхронизация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Берем файлы только из нашей папки
        java.io.File[] files = backupDir.listFiles((d, name) -> name.startsWith("backup_match_") && name.endsWith(".json"));

        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(this, "Резервных файлов не найдено.", "Синхронизация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            int successCount = 0;
            int failCount = 0;

            @Override
            protected Void doInBackground() throws Exception {
                ApiClient apiClient = new ApiClient();
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                for (java.io.File file : files) {
                    try {
                        com.data.Matches match = mapper.readValue(file, com.data.Matches.class);
                        apiClient.saveMatch(match);
                        file.delete(); 
                        successCount++;
                    } catch (Exception e) {
                        failCount++; 
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                String message = "Синхронизация завершена!\nУспешно отправлено на сервер: " + successCount + "\nОсталось с ошибкой сети: " + failCount;
                int messageType = failCount == 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
                JOptionPane.showMessageDialog(MainWindow.this, message, "Результат синхронизации", messageType);
            }
        };
        worker.execute();
    }

    //<editor-fold defaultstate="collapsed" desc="Generated Code">
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        miExit = new javax.swing.JMenuItem();
        menuData = new javax.swing.JMenu();
        miLevel = new javax.swing.JMenuItem();
        miEvent = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        menuFile.setText("Файл");
        miExit.setText("Выход");
        miExit.addActionListener(evt -> System.exit(0));
        menuFile.add(miExit);
        jMenuBar1.add(menuFile);

        menuData.setText("Справочники");
        miLevel.setText("Уровни соревнований");
        miLevel.addActionListener(evt -> new FrmLevel(this, true).setVisible(true));
        menuData.add(miLevel);

        miEvent.setText("Соревнования");
        miEvent.addActionListener(evt -> new FrmEvent(this, true).setVisible(true));
        menuData.add(miEvent);

        jMenuBar1.add(menuData);
        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 377, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu menuData;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem miEvent;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miLevel;
    // End of variables declaration
}