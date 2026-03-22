package com;

import com.data.Event;
import com.data.Level;
import javax.swing.*;
import java.util.List;

public class EdEventDialog extends javax.swing.JDialog {

    private JDialogResult dialogResult;
    private Event event;
    private final ApiClient apiClient = new ApiClient();
    private List<Level> levelList;

    /**
     * Creates new form EdEventDialog
     */
    public EdEventDialog(java.awt.Frame parent, boolean modal, Event event) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);

        loadLevelsForComboBox();

        if (event == null) {
            this.event = new Event();
            setTitle("Добавление нового соревнования");
        } else {
            this.event = event;
            setTitle("Редактирование соревнования");
            fillFields();
        }
    }

    public JDialogResult getDialogResult() {
        return dialogResult;
    }

    public Event getEvent() {
        return event;
    }

    private void loadLevelsForComboBox() {
        cbLevel.setEnabled(false);
        cbLevel.removeAllItems();
        cbLevel.addItem("Загрузка уровней...");

        SwingWorker<List<Level>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Level> doInBackground() throws Exception {
                return apiClient.getAllLevels();
            }

            @Override
            protected void done() {
                try {
                    levelList = get();
                    cbLevel.removeAllItems();
                    if (levelList != null && !levelList.isEmpty()) {
                        for (Level level : levelList) {
                            cbLevel.addItem(level.getName());
                        }
                        selectCurrentLevel(); // Выбираем текущий уровень после загрузки
                    } else {
                        cbLevel.addItem("Нет доступных уровней");
                        btnOk.setEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    cbLevel.addItem("Ошибка загрузки");
                    btnOk.setEnabled(false);
                } finally {
                    cbLevel.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void fillFields() {
        tfEventName.setText(event.getName());
        tfLocation.setText(event.getLocation());
        tfYear.setText(String.valueOf(event.getYear() != 0 ? event.getYear() : ""));
        selectCurrentLevel();
    }

    private void selectCurrentLevel() {
        if (event.getCompetitionLevel() != null && levelList != null) {
            for (int i = 0; i < levelList.size(); i++) {
                if (levelList.get(i).getId() == event.getCompetitionLevel().getId()) {
                    cbLevel.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean checkAndSave() {
        String eventName = tfEventName.getText();
        if (eventName == null || eventName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Название соревнования не может быть пустым.", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        event.setName(eventName);
        event.setLocation(tfLocation.getText());
        try {
            event.setYear(Integer.parseInt(tfYear.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Год должен быть числом.", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int selectedIndex = cbLevel.getSelectedIndex();
        if (selectedIndex != -1) {
            event.setCompetitionLevel(levelList.get(selectedIndex));
        } else {
            JOptionPane.showMessageDialog(this, "Необходимо выбрать уровень соревнования!", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
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
        tfEventName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfLocation = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfYear = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cbLevel = new javax.swing.JComboBox<>();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Название:");
        jLabel2.setText("Место проведения:");
        jLabel3.setText("Год:");
        jLabel4.setText("Уровень соревнования:");

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
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfEventName)
                            .addComponent(tfLocation)
                            .addComponent(tfYear)
                            .addComponent(cbLevel, 0, 240, Short.MAX_VALUE)))
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
                    .addComponent(tfEventName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JComboBox<String> cbLevel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField tfEventName;
    private javax.swing.JTextField tfLocation;
    private javax.swing.JTextField tfYear;
    // End of variables declaration
}