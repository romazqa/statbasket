package com;

import com.data.Level;
import com.gui.GuiHelper;

import javax.swing.*;
import java.util.List;

public class FrmLevel extends javax.swing.JDialog {

    private final ApiClient apiClient = new ApiClient();
    private List<Level> currentLevels;

    /**
     * Creates new form FrmLevel
     */
    public FrmLevel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        // Установка имен для UI-тестов
        tblLevel.setName("tblLevel");
        btnAdd.setName("btnAdd");
        btnEdit.setName("btnEdit");
        btnDelete.setName("btnDelete");
        btnClose.setName("btnClose");
        lblStatus.setName("lblStatus");
        
        this.setLocationRelativeTo(parent);
        loadLevelsAsync();
    }

    private void loadLevelsAsync() {
        lblStatus.setText("Загрузка уровней соревнований...");
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        SwingWorker<List<Level>, Void> worker = new SwingWorker<List<Level>, Void>() {
            @Override
            protected List<Level> doInBackground() throws Exception {
                return apiClient.getAllLevels();
            }

            @Override
            protected void done() {
                try {
                    currentLevels = get();
                    GuiHelper.addObjectsToTable(tblLevel, currentLevels);
                    lblStatus.setText("Данные успешно загружены.");
                } catch (Exception e) {
                    e.printStackTrace();
                    lblStatus.setText("Ошибка загрузки!");
                    JOptionPane.showMessageDialog(FrmLevel.this, "Ошибка загрузки: " + e.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnAdd.setEnabled(true);
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {
        EdLevelDialog dlg = new EdLevelDialog(null, true, null);
        dlg.setVisible(true);
        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.saveLevel(dlg.getLevel());
                loadLevelsAsync();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblLevel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите уровень для редактирования.");
            return;
        }

        Level selectedLevel = currentLevels.get(selectedRow);
        EdLevelDialog dlg = new EdLevelDialog(null, true, selectedLevel);
        dlg.setVisible(true);
        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.saveLevel(dlg.getLevel());
                loadLevelsAsync();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblLevel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите уровень для удаления.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить выбранный уровень?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Level selectedLevel = currentLevels.get(selectedRow);
            try {
                apiClient.deleteLevel(selectedLevel.getId());
                loadLevelsAsync();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка удаления: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblLevel = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Уровни соревнований");

        tblLevel.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Наименование уровня"}
        ));
        jScrollPane1.setViewportView(tblLevel);

        btnAdd.setText("Добавить");
        btnAdd.addActionListener(evt -> btnAddActionPerformed(evt));

        btnEdit.setText("Изменить");
        btnEdit.addActionListener(evt -> btnEditActionPerformed(evt));

        btnDelete.setText("Удалить");
        btnDelete.addActionListener(evt -> btnDeleteActionPerformed(evt));
        
        btnClose.setText("Закрыть");
        btnClose.addActionListener(evt -> btnCloseActionPerformed(evt));

        lblStatus.setText("Статус");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnAdd)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnEdit)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDelete)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnClose))
                                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAdd)
                                        .addComponent(btnEdit)
                                        .addComponent(btnDelete)
                                        .addComponent(btnClose))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblLevel;
}