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

    // ���� ������
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
    private Team selectedPlayerTeam = null; // ���� ��� ������� ���������� ������
    private JButton selectedPlayerButton = null;
    private JTextArea eventsTextArea;
    private HashMap<BigDecimal, Stat> playerStatsMap = new HashMap<>();

    private JLabel scoreLabel; // ��� ����������� �����
    private int scoreTeamA = 0;
    private int scoreTeamB = 0;
    private int[] quarterScoresTeamA = new int[4]; // ������ ��� �������� ����� ������� A �� ���������
    private int[] quarterScoresTeamB = new int[4]; // ������ ��� �������� ����� ������� B �� ���������
    private int currentQuarter = 0; // ������� �������� (0 - ���� �� �����)
    private boolean quarterStarted = false; // ����, �����������, ������ �� ��������
    private JButton startQuarterButton;
    private JButton endQuarterButton;
    private JComboBox<String> quarterComboBox;
    private JButton finishMatchButton;
    private Timer quarterTimer;
    private int quarterTimeSeconds = 600; // ����� �������� � ��������
    private JLabel quarterTimerLabel; // ����� ��� ����������� ������� ��������
    private HashMap<BigDecimal, Integer> playerTimeOnCourt = new HashMap<>(); // ����� ������� ������ �� �������� (� ��������)
    private boolean timerPaused = false; // ����, �����������, �� ����� �� ������
    private JButton pauseTimerButton; // ������ ��� ����� �������
    private JButton resumeTimerButton; // ������ ��� ������������� �������
    
    private HashMap<BigDecimal, Integer> playerTimeMap = new HashMap<>();
    private HashMap<BigDecimal, Integer> playerTwoPointMisses = new HashMap<>();
    private HashMap<BigDecimal, Integer> playerThreePointMisses = new HashMap<>();
    private HashMap<BigDecimal, Integer> playerFreeThrowMisses = new HashMap<>();
    
    private ImageIcon basketballIcon;
    // ������ �������� ���������� ������
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
        
        // !!!  ������������� ������ �������� ����
        URL iconUrl = MainWindow.class.getResource("/images/icon-basketballapp.png");  //  
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        } else {
            System.err.println("�� ������� ��������� ������.");
        }

        
     // �������� �������� �������������� ����
        URL basketballImageUrl = MainWindow.class.getResource("/images/ball2.png"); 
        if (basketballImageUrl != null) {
            try {
                BufferedImage originalImage = ImageIO.read(basketballImageUrl);

                //  ��������� ������ �����������
                int newWidth = 40;  
                int newHeight = 40; 
                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                // ������� BufferedImage � �������������
                BufferedImage transparentImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = transparentImage.createGraphics();
                g2d.drawImage(scaledImage, 0, 0, null);
                g2d.dispose();

                basketballIcon = new ImageIcon(transparentImage);
            } catch (IOException ex) {
                System.err.println("�� ������� ��������� �������� basketball.png: " + ex.getMessage());
            }
        } else {
            System.err.println("�� ������� ����� ���� basketball.png");
        }
        

        // ����� ������ - ������� �
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints leftGbc = new GridBagConstraints();
        for (int i = 0; i < 5; i++) {
            playerButtonsA[i] = createPlayerButton("����� " + (i + 1), 110, 70);
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

        // ����������� ������ - ���� � �������
        JPanel middlePanel = new JPanel(new BorderLayout());
        JPanel scorePanel = new JPanel(new FlowLayout());
        scoreLabel = new JLabel("����: ������� A - 0 : 0 - ������� B");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel);
        middlePanel.add(scorePanel, BorderLayout.NORTH);
        

        eventsTextArea = new JTextArea(20, 30);
        eventsTextArea.setFont(new Font("Arial", Font.BOLD, 12));
        eventsTextArea.setEditable(false);
        JPanel eventsPanel = new JPanel(new BorderLayout()); // !!! ���������� BorderLayout ��� eventsPanel
        eventsPanel.setBorder(BorderFactory.createTitledBorder("���� ����������� �������"));
        eventsPanel.add(new JScrollPane(eventsTextArea), BorderLayout.CENTER); // !!! ��������� JScrollPane � CENTER
        middlePanel.add(eventsPanel, BorderLayout.CENTER);
        eventsPanel.setPreferredSize(eventsTextArea.getPreferredSize()); 
        
       

        // ������ �� ��������������� ��������
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        String[] buttonLabels = {"1 ����", "1 ����\n����", "2 ����", "2 ����\n����", "3 ����", "3 ����\n����"};
        for (String label : buttonLabels) {
            statsPanel.add(createStatButton(label, 90, 90));
            
        }
        
        middlePanel.add(statsPanel, BorderLayout.WEST);

        // ������ � ��������������� ��������
        JPanel extraButtonsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        String[] extraButtonLabels = {"������\n� ������", "������\n� �����", "�������", "���", "������", "��������"};
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

        // ������ ������ - ������� �
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rightGbc = new GridBagConstraints();
        for (int i = 0; i < 5; i++) {
            playerButtonsB[i] = createPlayerButton("����� " + (i + 1), 110, 70);
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
        
        

        // ������ ������
        JPanel bottomPanel = new JPanel(new MigLayout("insets 5, flowy", "[grow][]", "[]5[]"));
        bottomPanel.add(quarterComboBox, "cell 0 1, width 60!, align center");
        JLabel quarterLabel = new JLabel("��������"); 
        quarterLabel.setFont(new Font("Arial", Font.BOLD, 14)); // ������ �����
        bottomPanel.add(quarterLabel, "cell 0 0, align center");
        bottomPanel.add(startQuarterButton, "cell 0 1, width 140!, growx 0, align center");
        bottomPanel.add(endQuarterButton, "cell 0 1, growx 0, align center");
        bottomPanel.add(finishMatchButton, "cell 1 0, growx"); // ��������� ������ �� ������
        bottomPanel.add(quarterTimerLabel, "cell 0 1, align center");
        bottomPanel.add(pauseTimerButton, "cell 0 1, width 140!, growx 0, align center");
        bottomPanel.add(resumeTimerButton, "cell 0 1, width 140!, growx 0, align center");
        pauseTimerButton.setEnabled(false); // ��������� ������ ����� 
        resumeTimerButton.setEnabled(false); // ��������� ������ ����������
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottomPanel, gbc);

        setVisible(true);
        
        
     // ������� ����������� ������ � GridBagLayout
        JPanel centralPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cgbc = new GridBagConstraints(); //  GridBagConstraints ��� centralPanel

        // ��������� ������ � centralPanel
        cgbc.gridx = 0;
        cgbc.gridy = 0;
        cgbc.weightx = 0.3; //  ����� ������ �������� 20% ������
        cgbc.fill = GridBagConstraints.BOTH;
        centralPanel.add(leftPanel, cgbc);

        cgbc.gridx = 1;
        cgbc.weightx = 0.4; //  ������� ������ �������� 60% ������
        centralPanel.add(middlePanel, cgbc);

        cgbc.gridx = 2;
        cgbc.weightx = 0.3; //  ������ ������ �������� 20% ������
        centralPanel.add(rightPanel, cgbc);
        

        // ��������� centralPanel � JFrame
        gbc.gridx = 0; 
        gbc.gridy = 0; //  !!! �������� gridy 
        gbc.gridwidth = 3;
        gbc.weightx = 1.0; // !!!  centralPanel  �������� ��� ��������� ������������ �� �����������
        gbc.weighty = 1.0; // !!!  centralPanel  �������� ��� ��������� ������������ �� ���������
        gbc.fill = GridBagConstraints.BOTH;
        add(centralPanel, gbc); 
    }

    
     
    private JButton createStatButton(String text, int width, int height) {
        JButton button = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        button.setPreferredSize(new Dimension(width, height)); 
        if (text.contains("����")) {
            button.setBorder(new RoundedLineBorder(new Color(253, 118, 118), 3, 12)); // ������-�������, ������������ ����
        } else {
            button.setBorder(new RoundedLineBorder(new Color(102, 204, 102), 3, 12)); // ������-�������, ������������ ����
        }

        // !!! ��������� ����������� ������� ����
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // �������� ��� ������ ��� ��������� �������
                button.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // ���������� �������� ��� ������
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
                        case "1 ����":
                            playerStats.setPointScored(playerStats.getPointScored() + 1);
                            playerStats.setFreeThrow(playerStats.getFreeThrow() + 1);
                            // ��������� ������� ���������� ������
                            if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team())) {
                                scoreTeamA++;
                            } else if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam2().getId_team())) {
                                scoreTeamB++;
                            }
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ����� 1 ����");
                            break;
                        case "1 ����\n����":
                            // ������ ��������� �����������, �� �������� � ��
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ����������� 1 ����");
                            playerFreeThrowMisses.put(selectedPlayer.getId(), 
                                    playerFreeThrowMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                            break;
                        case "2 ����":
                            playerStats.setPointScored(playerStats.getPointScored() + 2);
                            playerStats.setDoubleDouble(playerStats.getDoubleDouble() + 1);
                            // ��������� ������� ���������� ������
                            if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team())) {
                                scoreTeamA += 2;
                            } else if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam2().getId_team())) {
                                scoreTeamB += 2;
                            }
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ����� 2 ����");
                            break;
                        case "2 ����\n����":
                            // ������ ��������� �����������, �� �������� � ��
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ����������� 2 ����");
                            playerTwoPointMisses.put(selectedPlayer.getId(), 
                                    playerTwoPointMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                            break;
                        case "3 ����":
                            playerStats.setPointScored(playerStats.getPointScored() + 3);
                            playerStats.setTriple(playerStats.getTriple() + 1);
                            // ��������� ������� ���������� ������
                            if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team())) {
                                scoreTeamA += 3;
                            } else if (selectedPlayerTeam != null && selectedPlayerTeam.getId_team().equals(currentMatch.getTeam2().getId_team())) {
                                scoreTeamB += 3;
                            }
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ����� 3 ����");
                            break;
                        case "3 ����\n����":
                            // ������ ��������� �����������, �� �������� � ��
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ����������� 3 ����");
                            playerThreePointMisses.put(selectedPlayer.getId(), 
                                    playerThreePointMisses.getOrDefault(selectedPlayer.getId(), 0) + 1);
                            break;
                        case "������\n� ������":
                            playerStats.setDr(playerStats.getDr() + 1);
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ������ ������ � ������");
                            break;
                        case "������\n� �����":
                            playerStats.setOr(playerStats.getOr() + 1);
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ������ ������ � �����");
                            break;
                        case "�������":
                            playerStats.setBlockedShot(playerStats.getBlockedShot() + 1);
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ������ �������");
                            break;
                        case "���":
                            playerStats.setFoul(playerStats.getFoul() + 1);
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ������� ���");
                            break;
                        case "������":
                            playerStats.setAssists(playerStats.getAssists() + 1);
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ������ ������");
                            break;
                        case "��������":
                            playerStats.setSteal(playerStats.getSteal() + 1);
                            updateEventsTextArea("����� " + selectedPlayer.getName() + " ������ ��������");

                            // ��������� ������ ��� ������ ������ ��������������� �������
                            showOpponentPlayerDialog();
                            break;
                    }
                    // ��������� ���� �� �����
                    updateScoreLabel();
                }
            }
        });
        return button;
    }
    
    // ����� ��� ����������� ������� � �������� ��������������� �������
    private void showOpponentPlayerDialog() {
        // ���������� ������� ���������
        BigDecimal opponentTeamId = selectedPlayerTeam.getId_team().equals(currentMatch.getTeam1().getId_team()) ? 
                                     currentMatch.getTeam2().getId_team() : currentMatch.getTeam1().getId_team();

        // ������� ������ ������� ��������� �� ��������
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

        // ������� JList � �������� ���������
        JList<Player> opponentPlayerList = new JList<>(opponentPlayersModel);
        opponentPlayerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ������� ������
        JDialog dialog = new JDialog(this, "�������� ������, ������� ������� ���", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(opponentPlayerList), BorderLayout.CENTER);

        // ������ "OK"
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
                updateEventsTextArea("����� " + opponentPlayer.getName() + " ������� ���");
            }
            dialog.dispose();
        });
        dialog.add(okButton, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    // ����� ��� �������� ������ ������
    private JButton createPlayerButton(String text, int width, int height) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height)); 
        // !!! ��������� ����������� ������� ����
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // �������� ��� ������ ��� ��������� �������
                button.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // ���������� �������� ��� ������
                button.setBackground(null); 
            }
        });
        button.addActionListener(e -> {
            JButton btn = (JButton) e.getSource(); // �������� ������� ������
            selectedPlayerButton = btn; // ��������� ������ �� ������� ������
            String playerIdStr = btn.getActionCommand();
            if (playerIdStr != null && !playerIdStr.isEmpty()) {
                BigDecimal playerId = new BigDecimal(playerIdStr);
                for (Player player : manager.loadPlayer()) {
                    if (player.getId().equals(playerId)) {
                        selectedPlayer = player;
                        selectedPlayerTeam = player.getTeam();
                        updateEventsTextArea("������ �����: " + selectedPlayer.getName());
                        break;
                    }
                }
            }
            // !!!  �������������/������� ������ ���� 
            if (basketballIcon != null) {
                if (currentlySelectedPlayerButton != null) {
                    currentlySelectedPlayerButton.setIcon(null); //  ������� ������ � ���������� ������
                }
                btn.setIcon(basketballIcon); //  ������������� ������ �� ����� ������
                currentlySelectedPlayerButton = btn; //  ��������� currentlySelectedPlayerButton
            }
        });
        return button;
    }


    // ����� ��� �������� ������ ������
    private JButton createSubstitutionButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(new Color(128, 128, 128));
        // !!! ��������� ����������� ������� ����
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // �������� ��� ������ ��� ��������� �������
                button.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // ���������� �������� ��� ������
                button.setBackground(new Color(128, 128, 128)); 
            }
        });
        return button;
    }

    private JMenuBar createMenu() {
        mainMenu = new JMenuBar();
        mSprav = new JMenu("�����������");
        miExit = new JMenuItem("�����");
        mile = new JMenuItem("������ ������������");
        miSor = new JMenuItem("������������");
        miTe = new JMenuItem("�������");
        miNewMatch = new JMenuItem("����� ����");
        miMatch = new JMenuItem("�������� � �������������� ������");
        miPla = new JMenuItem("������");
        mKn = new JMenu("������� ������");

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
                resetQuarterScores(); // ����� ����� �� ���������
                currentQuarter = 0; // ����� ������� ��������
                quarterStarted = false; // ����� ����� ������ ��������
            }
        });

     // ��������� ��� ������ ������
        substitutionButtonA.addActionListener(e -> {
            if (currentMatch != null) {
                // !!! ��������, ������� �� ������ ������ ������� A
                if (selectedPlayerButton != null && Arrays.asList(playerButtonsA).contains(selectedPlayerButton)) {
                    showSubstitutionDialog(currentMatch.getTeam1().getId_team(), playerButtonsA);
                } else {
                    JOptionPane.showMessageDialog(this, "�������� ������ ������� A ��� ������.", "������", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        substitutionButtonB.addActionListener(e -> {
            if (currentMatch != null) {
                // !!! ��������, ������� �� ������ ������ ������� B
                if (selectedPlayerButton != null && Arrays.asList(playerButtonsB).contains(selectedPlayerButton)) {
                    showSubstitutionDialog(currentMatch.getTeam2().getId_team(), playerButtonsB);
                } else {
                    JOptionPane.showMessageDialog(this, "�������� ������ ������� B ��� ������.", "������", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        startQuarterButton.addActionListener(e -> {
            if (currentMatch != null) {
                int selectedQuarter = quarterComboBox.getSelectedIndex() + 1;
                if (selectedQuarter > currentQuarter && !quarterStarted) {
                    currentQuarter = selectedQuarter;
                    quarterStarted = true;
                    updateEventsTextArea("������ " + currentQuarter + " ��������");

                    startQuarterTimer(); // ��������� ������
                    resetQuarterTimer(); // ���������� ������ ��������
                    quarterTimer.start();

                    // !!! �������� ������ �����, ��������� ������ ����������
                    pauseTimerButton.setEnabled(true);
                    resumeTimerButton.setEnabled(false);
                } else if (quarterStarted) {
                    JOptionPane.showMessageDialog(this, "�������� ��� ������!", "������", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "������ ������ ��� ��������, ���� �� ��������� ����������.", "������", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        endQuarterButton.addActionListener(e -> {
            if (currentMatch != null && quarterStarted) {
                quarterScoresTeamA[currentQuarter - 1] = scoreTeamA;
                quarterScoresTeamB[currentQuarter - 1] = scoreTeamB;
                quarterStarted = false;
                updateEventsTextArea("����� " + currentQuarter + " ��������. ����: " + scoreTeamA + " : " + scoreTeamB);

                // ��������� playerTimeMap
                for (Map.Entry<BigDecimal, Integer> entry : playerTimeOnCourt.entrySet()) {
                    BigDecimal playerId = entry.getKey();
                    int timeOnCourt = entry.getValue();
                    playerTimeMap.put(playerId, playerTimeMap.getOrDefault(playerId, 0) + timeOnCourt);
                }

                resetQuarterTimer(); // ���������� ������ ��������
            } else if (!quarterStarted) {
                JOptionPane.showMessageDialog(this, "�������� ��� �� ������!", "������", JOptionPane.ERROR_MESSAGE);
            }
        });
        // ��������� ��� ������ "��������� ����"
        finishMatchButton.addActionListener(e -> {
            if (currentQuarter == 4 && quarterStarted == false) {
                // ��������� ���������� ���� �������
                for (Stat stats : playerStatsMap.values()) {
                    manager.saveStat(stats);
                }
                if (currentMatch != null) {
                    currentMatch.setTeam1score(BigDecimal.valueOf(scoreTeamA));
                    currentMatch.setTeam2score(BigDecimal.valueOf(scoreTeamB));
                    manager.updateMatches(currentMatch, currentMatch.getId_matches()); // ��������� ���� � ���� ������
                }


                // ������� ����� � �����
                createMatchReport();


                // ���������� ������� ���� � ������� ����������
                currentMatch = null;
                selectedPlayer = null;
                selectedPlayerTeam = null;
                selectedPlayerButton = null;
                resetScore();
                resetQuarterScores();
                currentQuarter = 0;
                quarterStarted = false;
                eventsTextArea.setText(""); // ������� ��������� ���� �������
                updatePlayerButtons(); 
            } else {
                JOptionPane.showMessageDialog(this, "��������� ������� �������� ��� ����!", "������", JOptionPane.ERROR_MESSAGE);
            }
        });
     // ��������� ��� ������ "�����"
        pauseTimerButton.addActionListener(e -> {
            if (quarterStarted && !timerPaused) {
                quarterTimer.stop();
                timerPaused = true;
                updateEventsTextArea("������ ����������");

                // !!! �������� ������ ����������, ��������� ������ �����
                resumeTimerButton.setEnabled(true);
                pauseTimerButton.setEnabled(false); 
            }
        });


        // ��������� ��� ������ "����������"
        resumeTimerButton.addActionListener(e -> {
            if (quarterStarted && timerPaused) {
                quarterTimer.start();
                timerPaused = false;
                updateEventsTextArea("������ �������");

                // !!! �������� ������ �����, ��������� ������ ����������
                pauseTimerButton.setEnabled(true); 
                resumeTimerButton.setEnabled(false);
            }
        });
        
        
    }
    
    
    
 // ����� ��� ������ ����� �� ���������
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
                playerButtonsA[i].setText("<html><center>����� " + player.getNum() + "<br>" + player.getName() + "</center></html>");
                playerButtonsA[i].setActionCommand(player.getId().toString());
            }

            for (int i = 0; i < 5 && i < playersTeam2.size(); i++) {
                Player player = playersTeam2.get(i);
                playerButtonsB[i].setText("<html><center>����� " + player.getNum() + "<br>" + player.getName() + "</center></html>");
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
                // ��������� ����� ������� �� ��������
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

                // �������� �� ��������� ��������
                if (quarterTimeSeconds == 0) {
                    endQuarterButton.doClick(); // ���������� ������� ������ "��������� ��������"
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
        quarterTimeSeconds = 600; // ���������� ����� �� 10 �����
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
        substitutionButtonA = createSubstitutionButton("������ ������� �", 150, 70);
        substitutionButtonB = createSubstitutionButton("������ ������� B", 150, 70);
        startQuarterButton = new JButton("������ ��������");
        endQuarterButton = new JButton("��������� ��������");
     // ��������� ��� ������ ��������
        quarterComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        finishMatchButton = new JButton("��������� ����");
     // ����� ��� ������� ��������
        quarterTimerLabel = new JLabel("00:00");
        quarterTimerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // ������ ��� ���������� ��������
        pauseTimerButton = new JButton("�����");
        resumeTimerButton = new JButton("����������");
        
        addButtonHighlight(startQuarterButton);
        addButtonHighlight(endQuarterButton);
        addButtonHighlight(pauseTimerButton);
        addButtonHighlight(resumeTimerButton);
        addButtonHighlight(finishMatchButton);
        
        
        
        startQuarterButton.setBackground(new Color(102, 204, 102)); //  ������-������� ���
        endQuarterButton.setBackground(new Color(255, 153, 51)); //  ������-��������� ���      
        pauseTimerButton.setBackground(new Color(255, 153, 51)); //  ������-��������� ���     
        resumeTimerButton.setBackground(new Color(102, 204, 102)); //  ������-������� ���      
        finishMatchButton.setBackground(new Color(253, 51, 51)); //  ������-������� ��� 
        
        
        
        
  
    }
    
    private void addButtonHighlight(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Color originalColor = button.getBackground();

                // !!! ����������� RGB � HSL
                float[] hsl = Color.RGBtoHSB(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), null);

                // !!! ����������� ������� (L) �� 20%
                float newLightness = Math.min(hsl[2] + 0.2f, 1.0f); //  �� ��������� ������������ ������� (1.0)

                // !!! ����������� HSL ������� � RGB
                Color brighterColor = Color.getHSBColor(hsl[0], hsl[1], newLightness);

                button.setBackground(brighterColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Color brighterColor = button.getBackground();

                // !!! ����������� RGB � HSL
                float[] hsl = Color.RGBtoHSB(brighterColor.getRed(), brighterColor.getGreen(), brighterColor.getBlue(), null);

                // !!! ��������� ������� (L) �� 20%
                float originalLightness = Math.max(hsl[2] - 0.2f, 0.0f); //  �� ���������� ���� ����������� ������� (0.0)

                // !!! ����������� HSL ������� � RGB
                Color originalColor = Color.getHSBColor(hsl[0], hsl[1], originalLightness);

                button.setBackground(originalColor);
            }
        });
    }

    private void showSubstitutionDialog(BigDecimal teamId, JButton[] playerButtons) {
        ArrayList<Player> teamPlayers = getPlayersForTeam(teamId);
        if (teamPlayers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "� ������� ��� �������!", "������", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // !!! ������ �������, ������� ��� �� ��������
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

        // !!! ������ �������, ������� �� �� ��������
        ArrayList<Player> availablePlayers = new ArrayList<>(teamPlayers);
        availablePlayers.removeAll(playersOnCourt);

        // !!! ������� JList ������ � ���������� ��������
        JList<Player> playerList = new JList<>(availablePlayers.toArray(new Player[0])); 
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ������ ���������� ����
        JDialog substitutionDialog = new JDialog(this, "������ ������", true);
        substitutionDialog.setLayout(new BorderLayout());
        substitutionDialog.add(new JLabel("�������� ������ ��� ������:"), BorderLayout.NORTH);
        substitutionDialog.add(new JScrollPane(playerList), BorderLayout.CENTER);

        // ������ "OK"
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            Player newPlayer = playerList.getSelectedValue();
            if (newPlayer != null) {
                if (selectedPlayerButton != null) {
                    int playerIndex = Arrays.asList(playerButtons).indexOf(selectedPlayerButton);

                    // ��������� ���������� ����������� ������
                    if (playerStatsMap.containsKey(selectedPlayer.getId())) {
                        Stat stats = playerStatsMap.get(selectedPlayer.getId());
                        manager.saveStat(stats);
                    }
                    
                    if (selectedPlayer != null) { // ��� ����������� ������
                        BigDecimal playerId = selectedPlayer.getId();
                        int timeOnCourt = playerTimeOnCourt.getOrDefault(playerId, 0);
                        playerTimeMap.put(playerId, playerTimeMap.getOrDefault(playerId, 0) + timeOnCourt);
                        playerTimeOnCourt.remove(playerId); // ������� ������ �� playerTimeOnCourt
                    }
                    playerTimeOnCourt.put(newPlayer.getId(), 0); // ��������� ������ ������ � playerTimeOnCourt

                    // ����������� ������ � ������ ������
                    playerButtons[playerIndex].setText("<html><center>����� " + newPlayer.getNum() + "<br>" + newPlayer.getName() + "</center></html>");
                    playerButtons[playerIndex].setActionCommand(newPlayer.getId().toString());
                    selectedPlayer = newPlayer;
                    selectedPlayerTeam = newPlayer.getTeam();
                    updateEventsTextArea("����� " + selectedPlayer.getName() + " ����� �� ����");
                } else {
                    JOptionPane.showMessageDialog(this, "������: �� ������ ����� ��� ������.", "������", JOptionPane.ERROR_MESSAGE);
                }
                substitutionDialog.dispose();
            }
        });
        substitutionDialog.add(okButton, BorderLayout.SOUTH);

        // ���������� ������
        substitutionDialog.pack();
        substitutionDialog.setLocationRelativeTo(this); 
        substitutionDialog.setVisible(true);
    }

    // ����� ��� ���������� ���������� ���� �������
    private void updateEventsTextArea(String event) {
        eventsTextArea.append(event + "\n");
        eventsTextArea.setCaretPosition(eventsTextArea.getDocument().getLength());
    }

    // ����� ��� ���������� ����� �� �����
    private void updateScoreLabel() {
        if (currentMatch != null) {
            scoreLabel.setText("����: " + currentMatch.getTeam1().getTeam_name() + " - " + scoreTeamA +
                    " : " + scoreTeamB + " - " + currentMatch.getTeam2().getTeam_name());
        }
    }

    // ����� ��� ������ �����
    private void resetScore() {
        scoreTeamA = 0;
        scoreTeamB = 0;
        updateScoreLabel();
    }
    
    private void createMatchReport() {
        if (currentMatch == null) {
            JOptionPane.showMessageDialog(this, "���� �� ������!", "������", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ������� ������ ��� ������
        JDialog reportDialog = new JDialog(this, "����� � �����", true);
        reportDialog.setLayout(new BorderLayout());

        // ��������� ���������� � �����
        JPanel matchInfoPanel = new JPanel(new MigLayout("insets 5, wrap 2", "[][grow]", ""));
        matchInfoPanel.add(new JLabel("����:"));
        matchInfoPanel.add(new JLabel(currentMatch.getTeam1().getTeam_name() + " - " + currentMatch.getTeam2().getTeam_name()));
        matchInfoPanel.add(new JLabel("����:"));
        matchInfoPanel.add(new JLabel(scoreTeamA + " : " + scoreTeamB));

        // ��������� ���������� � ����� �� ���������
        for (int i = 0; i < 4; i++) {
            matchInfoPanel.add(new JLabel("�������� " + (i + 1) + ":"));
            matchInfoPanel.add(new JLabel(quarterScoresTeamA[i] + " : " + quarterScoresTeamB[i]));
        }
        reportDialog.add(matchInfoPanel, BorderLayout.NORTH);

        // ������� ������ ��� ������ ������
        JPanel teamsPanel = new JPanel(new GridLayout(1, 2)); 

        // ������� ������� ��� ������� A
        JPanel teamAPanel = new JPanel(new BorderLayout());
        teamAPanel.add(new JLabel("������� A: " + currentMatch.getTeam1().getTeam_name()), BorderLayout.NORTH);
        JTable teamATable = createTeamTable(currentMatch.getTeam1().getId_team());
        teamAPanel.add(new JScrollPane(teamATable), BorderLayout.CENTER);
        teamsPanel.add(teamAPanel);

        // ������� ������� ��� ������� B
        JPanel teamBPanel = new JPanel(new BorderLayout());
        teamBPanel.add(new JLabel("������� B: " + currentMatch.getTeam2().getTeam_name()), BorderLayout.NORTH);
        JTable teamBTable = createTeamTable(currentMatch.getTeam2().getId_team());
        teamBPanel.add(new JScrollPane(teamBTable), BorderLayout.CENTER);
        teamsPanel.add(teamBPanel);

        reportDialog.add(teamsPanel, BorderLayout.CENTER);

        
     // ������ ��� ������
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // ��������� ������ "��������� �����"
        JButton saveButton = new JButton("��������� �����");
        saveButton.addActionListener(e -> saveMatchReportToWordFile());
        buttonPanel.add(saveButton);

        // ��������� ������ "�������"
        JButton closeButton = new JButton("�������");
        closeButton.addActionListener(e -> reportDialog.dispose());
        buttonPanel.add(closeButton);

        reportDialog.add(buttonPanel, BorderLayout.SOUTH); // ��������� ������ � ��������
        
     

        reportDialog.pack();
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setVisible(true);
    }

    private JTable createTeamTable(BigDecimal teamId) {
    	
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("�����");
        model.addColumn("��");
        model.addColumn("����");
        model.addColumn("2-���");
        model.addColumn("3-���");
        model.addColumn("��");
        model.addColumn("��");
        model.addColumn("��");
        model.addColumn("��");
        model.addColumn("�");
        model.addColumn("���������"); 
        model.addColumn("������");  

        // ��������� ������� ������� � �������
        for (Player player : manager.loadPlayer()) {
            if (player.getTeam().getId_team().equals(teamId)) {
                Stat playerStats = manager.loadStatForPlayerAndMatch(player.getId(), currentMatch.getId_matches());

                String playingTime = "00:00"; // ����� �� �������� �� ���������

                if (playerStats != null) {
                    // �������� ����� ���� �� playerTimeMap
                    Integer playerTimeSeconds = playerTimeMap.get(player.getId());
                    if (playerTimeSeconds != null) {
                        playingTime = String.format("%02d:%02d", playerTimeSeconds / 60, playerTimeSeconds % 60);
                    }

                 // �������� ���������� �������� ������
                    int twoPointMisses = playerTwoPointMisses.getOrDefault(player.getId(), 0);
                    int threePointMisses = playerThreePointMisses.getOrDefault(player.getId(), 0);
                    int freeThrowMisses = playerFreeThrowMisses.getOrDefault(player.getId(), 0);

                    // ��������� ����� ���������� �������
                    int twoPointAttempts = playerStats.getDoubleDouble() + twoPointMisses;
                    int threePointAttempts = playerStats.getTriple() + threePointMisses;
                    int freeThrowAttempts = playerStats.getFreeThrow() + freeThrowMisses;

                    model.addRow(new Object[]{
                            player.getName(),
                            playingTime, // ������� ����� ����
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
                    // ����� �� ������� �� �������� - ��������� ������� ��������
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

        // ������� ������ ������ �����
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("��������� ����� � ����� � Word");
        fileChooser.setSelectedFile(new java.io.File("match_report.docx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (XWPFDocument document = new XWPFDocument();
                 FileOutputStream out = new FileOutputStream(fileToSave)) {

                // ���������� ���������� � �����
                XWPFParagraph matchInfo = document.createParagraph();
                matchInfo.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun matchInfoRun = matchInfo.createRun();
                matchInfoRun.setText("����: " + currentMatch.getTeam1().getTeam_name() + " - " + 
                                     currentMatch.getTeam2().getTeam_name());
                matchInfoRun.addBreak();
                matchInfoRun.setText("����: " + scoreTeamA + " : " + scoreTeamB);
                matchInfoRun.addBreak();

                // ���������� ���������� � ����� �� ���������
                XWPFParagraph quarterScores = document.createParagraph();
                quarterScores.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun quarterScoresRun = quarterScores.createRun();
                quarterScoresRun.setText("���� �� ���������:");
                quarterScoresRun.addBreak();
                for (int i = 0; i < 4; i++) {
                    quarterScoresRun.setText("�������� " + (i + 1) + ": " + quarterScoresTeamA[i] + " : " + 
                                            quarterScoresTeamB[i]);
                    quarterScoresRun.addBreak();
                }

                // ���������� ������ ������� ������� A
                writeTeamDataToWordTable(document, currentMatch.getTeam1().getId_team(), "������� A: " + currentMatch.getTeam1().getTeam_name());

                // ���������� ������ ������� ������� B
                writeTeamDataToWordTable(document, currentMatch.getTeam2().getId_team(), "������� B: " + currentMatch.getTeam2().getTeam_name());

                document.write(out);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "������ ���������� ������: " + ex.getMessage(), "������", 
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void writeTeamDataToWordTable(XWPFDocument document, BigDecimal teamId, String teamName) {
    	// ��������� �������� ������� ����� ��������
        XWPFParagraph teamParagraph = document.createParagraph();
        teamParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun teamParagraphRun = teamParagraph.createRun();
        teamParagraphRun.setBold(true); 
        teamParagraphRun.setText(teamName); 
        teamParagraphRun.addBreak();
        
    	// ������� �������
        XWPFTable table = document.createTable();
        table.setWidth("100%");

        // ������� ��������� �������
        XWPFTableRow headerRow = table.getRow(0); // �������� ������ ������ (���������)
        headerRow.getCell(0).setText("�����");
        headerRow.addNewTableCell().setText("��");
        headerRow.addNewTableCell().setText("����");
        headerRow.addNewTableCell().setText("2-���");
        headerRow.addNewTableCell().setText("3-���");
        headerRow.addNewTableCell().setText("��");
        headerRow.addNewTableCell().setText("��");
        headerRow.addNewTableCell().setText("��");
        headerRow.addNewTableCell().setText("��");
        headerRow.addNewTableCell().setText("�");
        headerRow.addNewTableCell().setText("���������"); 
        headerRow.addNewTableCell().setText("������");   

        // ��������� ������� ������� � �������
        for (Player player : manager.loadPlayer()) {
            if (player.getTeam().getId_team().equals(teamId)) {
                Stat playerStats = manager.loadStatForPlayerAndMatch(player.getId(), currentMatch.getId_matches());
                String playingTime = "00:00";

                XWPFTableRow row = table.createRow(); // ������� ����� ������ ��� ������

                if (playerStats != null) {
                    Integer playerTimeSeconds = playerTimeMap.get(player.getId());
                    if (playerTimeSeconds != null) {
                        playingTime = String.format("%02d:%02d", playerTimeSeconds / 60, playerTimeSeconds % 60);
                    }

                    int twoPointMisses = playerTwoPointMisses.getOrDefault(player.getId(), 0);
                    int threePointMisses = playerThreePointMisses.getOrDefault(player.getId(), 0);
                    int freeThrowMisses = playerFreeThrowMisses.getOrDefault(player.getId(), 0);

                    // !!!  ��������� ����� ���������� �������
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
                    // ����� �� ������� �� �������� - ��������� ������� ��������
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
        viewer.setTitle("����� � ��������� ����� � ������� ��������");
        viewer.setVisible(false);
    }

    public void initialize() {
        Locale.setDefault(new Locale("ru"));
    }

    public static void main(String[] args) {
    	try {
	        // ������������� ���� Nimbus
	        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 13)); // �������� ����� ������
			UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 12)); // �������� ����� �����
			
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
        PgAppBasket app = new PgAppBasket();
        app.run();
    }
}