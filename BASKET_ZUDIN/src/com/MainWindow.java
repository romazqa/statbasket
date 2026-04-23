package com;

import com.gui.LookAndFillUtil;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

        mainPanel.add(btnNewMatch);
        mainPanel.add(btnMatches);
        mainPanel.add(btnTeams);
        mainPanel.add(btnPlayers);

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