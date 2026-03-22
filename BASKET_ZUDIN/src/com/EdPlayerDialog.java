package com;

import com.data.Player;
import com.data.Team;
import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EdPlayerDialog extends javax.swing.JDialog {

    private JDialogResult dialogResult;
    private Player player;
    private final ApiClient apiClient = new ApiClient();
    private List<Team> teamList; // Локальный список команд для ComboBox

    /**
     * Creates new form EdPlayerDialog
     * @param parent
     * @param modal
     * @param player Игрок для редактирования или null для создания нового
     * @param teams Список команд, переданный из родительской формы
     */
    public EdPlayerDialog(java.awt.Frame parent, boolean modal, Player player, List<Team> teams) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        this.teamList = teams;

        populateTeamsComboBox();

        if (player == null) {
            this.player = new Player();
            setTitle("Добавление нового игрока");
        } else {
            this.player = player;
            setTitle("Редактирование игрока");
            fillFields();
        }
    }

    public JDialogResult getDialogResult() {
        return dialogResult;
    }

    public Player getPlayer() {
        return player;
    }

    private void populateTeamsComboBox() {
        cbTeam.removeAllItems();
        if (teamList == null || teamList.isEmpty()) {
            cbTeam.addItem("Нет доступных команд");
            btnOk.setEnabled(false); // Нельзя сохранить игрока без команды
            return;
        }
        for (Team team : teamList) {
            cbTeam.addItem(team.getName());
        }
    }

    private void fillFields() {
        tfPlayerName.setText(player.getName());
        tfGameNumber.setText(String.valueOf(player.getGameNumber()));
        tfHeight.setText(String.valueOf(player.getHeight()));
        tfWeight.setText(String.valueOf(player.getWeight()));
        tfRole.setText(player.getRole());
        tfGender.setText(player.getGender());
        
        // --- НОВОЕ: Заполнение даты рождения ---
        if (player.getBirthday() != null) {
            // Стандартный формат LocalDate.toString() - это "ГГГГ-ММ-ДД", что нам и нужно
            tfBirthday.setText(player.getBirthday().toString());
        }
        // --- КОНЕЦ НОВОГО БЛОКА ---

        // Выбираем текущую команду игрока в ComboBox
        if (player.getTeam() != null && teamList != null) {
            for (int i = 0; i < teamList.size(); i++) {
                if (teamList.get(i).getId() == player.getTeam().getId()) {
                    cbTeam.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean checkAndSave() {
        String playerName = tfPlayerName.getText();
        if (playerName == null || playerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Имя игрока не может быть пустым.", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        player.setName(playerName);
        player.setRole(tfRole.getText());
        player.setGender(tfGender.getText());

        try {
            player.setGameNumber(Integer.parseInt(tfGameNumber.getText()));
            player.setHeight(Integer.parseInt(tfHeight.getText()));
            player.setWeight(Integer.parseInt(tfWeight.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Номер, рост и вес должны быть целыми числами.", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // --- НОВОЕ: Считывание и проверка даты рождения ---
        String birthdayText = tfBirthday.getText().trim();
        if (!birthdayText.isEmpty()) {
            try {
                // Пытаемся распарсить строку в дату
                LocalDate birthday = LocalDate.parse(birthdayText);
                player.setBirthday(birthday);
            } catch (DateTimeParseException e) {
                // Если формат неверный, выводим ошибку
                JOptionPane.showMessageDialog(this, "Неверный формат даты рождения. Пожалуйста, используйте формат ГГГГ-ММ-ДД.", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            // Если поле пустое, можно установить null или выдать ошибку, если дата обязательна
            player.setBirthday(null);
        }
        // --- КОНЕЦ НОВОГО БЛОКА ---

        int selectedIndex = cbTeam.getSelectedIndex();
        if (selectedIndex != -1) {
            player.setTeam(teamList.get(selectedIndex));
        } else {
            JOptionPane.showMessageDialog(this, "Необходимо выбрать команду!", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {
        if (checkAndSave()) {
            dialogResult = JDialogResult.OK;
            this.setVisible(false);
        }
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        dialogResult = JDialogResult.Cancel;
        this.setVisible(false);
    }

    //<editor-fold defaultstate="collapsed" desc="Generated Code">
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tfPlayerName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfGameNumber = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfHeight = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfWeight = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tfRole = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbTeam = new javax.swing.JComboBox<>();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        tfGender = new javax.swing.JTextField();
        jLabelBirthday = new javax.swing.JLabel(); // НОВОЕ
        tfBirthday = new javax.swing.JTextField();   // НОВОЕ

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("ФИО игрока:");
        jLabel2.setText("Номер:");
        jLabel3.setText("Рост (см):");
        jLabel4.setText("Вес (кг):");
        jLabel5.setText("Амплуа:");
        jLabel6.setText("Команда:");
        jLabel7.setText("Пол (М/Ж):");
        jLabelBirthday.setText("Дата рождения (ГГГГ-ММ-ДД):"); // НОВОЕ

        btnOk.setText("OK");
        btnOk.addActionListener(evt -> btnOkActionPerformed(evt));

        btnCancel.setText("Отмена");
        btnCancel.addActionListener(evt -> btnCancelActionPerformed(evt));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelBirthday) // НОВОЕ
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfPlayerName)
                            .addComponent(tfGameNumber)
                            .addComponent(tfHeight)
                            .addComponent(tfWeight)
                            .addComponent(tfRole)
                            .addComponent(tfGender)
                            .addComponent(tfBirthday) // НОВОЕ
                            .addComponent(cbTeam, 0, 200, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfPlayerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // НОВЫЙ БЛОК ДЛЯ ДАТЫ РОЖДЕНИЯ
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBirthday)
                    .addComponent(tfBirthday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // КОНЕЦ НОВОГО БЛОКА
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfGameNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tfRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tfGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cbTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JComboBox<String> cbTeam;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelBirthday; // НОВОЕ
    private javax.swing.JTextField tfGameNumber;
    private javax.swing.JTextField tfGender;
    private javax.swing.JTextField tfHeight;
    private javax.swing.JTextField tfPlayerName;
    private javax.swing.JTextField tfRole;
    private javax.swing.JTextField tfWeight;
    private javax.swing.JTextField tfBirthday; // НОВОЕ
    // End of variables declaration
}