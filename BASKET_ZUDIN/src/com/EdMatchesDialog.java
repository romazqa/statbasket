package com;

import com.data.Event;
import com.data.Matches;
import com.data.Team;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EdMatchesDialog extends javax.swing.JDialog {

    private JDialogResult dialogResult;
    private Matches match;
    private final ApiClient apiClient = new ApiClient();
    
    // Списки для хранения загруженных данных
    private List<Team> teamList;
    private List<Event> eventList;

    /**
     * Конструктор диалога редактирования матча
     */
    public EdMatchesDialog(java.awt.Frame parent, boolean modal, Matches match) {
        super(parent, modal);
        initComponents();
        
        // Установка имен для UI-тестов
        tfDate.setName("tfDate");
        cbEvent.setName("cbEvent");
        cbTeam1.setName("cbTeam1");
        tfScore1.setName("tfScore1");
        cbTeam2.setName("cbTeam2");
        tfScore2.setName("tfScore2");
        tfPlayground.setName("tfPlayground");
        btnOk.setName("btnOk");
        btnCancel.setName("btnCancel");

        this.setLocationRelativeTo(parent);

        // Блокируем интерфейс до окончания загрузки данных
        btnOk.setEnabled(false);
        cbTeam1.setEnabled(false);
        cbTeam2.setEnabled(false);
        cbEvent.setEnabled(false);

        this.match = match;
        if (this.match == null) {
            this.match = new Matches();
            setTitle("Добавление нового матча");
            tfScore1.setText("0");
            tfScore2.setText("0");
        } else {
            setTitle("Редактирование матча");
        }

        // Асинхронная загрузка справочников
        loadDictionariesAsync();
    }

    public JDialogResult getDialogResult() {
        return dialogResult;
    }

    public Matches getMatches() {
        return match;
    }

    private void loadDictionariesAsync() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                teamList = apiClient.getAllTeams();
                eventList = apiClient.getAllEvents();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); 
                    populateComboBoxes();
                    if (match.getId() != null) {
                        fillFields();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(EdMatchesDialog.this, 
                        "Ошибка загрузки справочников: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnOk.setEnabled(true);
                    cbTeam1.setEnabled(true);
                    cbTeam2.setEnabled(true);
                    cbEvent.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void populateComboBoxes() {
        cbTeam1.removeAllItems();
        cbTeam2.removeAllItems();
        cbEvent.removeAllItems();

        if (teamList != null) {
            for (Team t : teamList) {
                cbTeam1.addItem(t.getName());
                cbTeam2.addItem(t.getName());
            }
        }
        if (eventList != null) {
            for (Event e : eventList) {
                cbEvent.addItem(e.getName());
            }
        }
    }

    private void fillFields() {
        if (match.getDate() != null) {
            tfDate.setText(match.getDate().toString());
        }
        tfScore1.setText(String.valueOf(match.getTeam1Score()));
        tfScore2.setText(String.valueOf(match.getTeam2Score()));
        tfPlayground.setText(match.getPlayground());

        if (match.getEvent() != null && eventList != null) {
            for (int i = 0; i < eventList.size(); i++) {
                if (eventList.get(i).getId().equals(match.getEvent().getId())) {
                    cbEvent.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (match.getTeam1() != null && teamList != null) {
            for (int i = 0; i < teamList.size(); i++) {
                if (teamList.get(i).getId().equals(match.getTeam1().getId())) {
                    cbTeam1.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (match.getTeam2() != null && teamList != null) {
            for (int i = 0; i < teamList.size(); i++) {
                if (teamList.get(i).getId().equals(match.getTeam2().getId())) {
                    cbTeam2.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean checkAndSave() {
        String dateStr = tfDate.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите дату матча!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            match.setDate(LocalDate.parse(dateStr));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты! Используйте ГГГГ-ММ-ДД.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        match.setPlayground(tfPlayground.getText());

        try {
            match.setTeam1Score(Integer.parseInt(tfScore1.getText().trim()));
            match.setTeam2Score(Integer.parseInt(tfScore2.getText().trim()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Счет должен быть целым числом!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int idxEvent = cbEvent.getSelectedIndex();
        int idxTeam1 = cbTeam1.getSelectedIndex();
        int idxTeam2 = cbTeam2.getSelectedIndex();

        if (idxEvent == -1 || idxTeam1 == -1 || idxTeam2 == -1) {
            JOptionPane.showMessageDialog(this, "Необходимо выбрать турнир и обе команды!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (idxTeam1 == idxTeam2) {
            JOptionPane.showMessageDialog(this, "Команда 1 и Команда 2 не могут быть одинаковыми!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        match.setEvent(eventList.get(idxEvent));
        match.setTeam1(teamList.get(idxTeam1));
        match.setTeam2(teamList.get(idxTeam2));

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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tfDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cbEvent = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cbTeam1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        tfScore1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cbTeam2 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        tfScore2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tfPlayground = new javax.swing.JTextField();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Дата (ГГГГ-ММ-ДД):");
        jLabel2.setText("Соревнование:");
        jLabel3.setText("Команда 1:");
        jLabel4.setText("Счет 1:");
        jLabel5.setText("Команда 2:");
        jLabel6.setText("Счет 2:");
        jLabel7.setText("Площадка:");

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
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfDate)
                            .addComponent(cbEvent, 0, 250, Short.MAX_VALUE)
                            .addComponent(cbTeam1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfScore1)
                            .addComponent(cbTeam2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfScore2)
                            .addComponent(tfPlayground)))
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
                    .addComponent(tfDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbTeam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfScore1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbTeam2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tfScore2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tfPlayground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JComboBox<String> cbEvent;
    private javax.swing.JComboBox<String> cbTeam1;
    private javax.swing.JComboBox<String> cbTeam2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField tfDate;
    private javax.swing.JTextField tfPlayground;
    private javax.swing.JTextField tfScore1;
    private javax.swing.JTextField tfScore2;
}