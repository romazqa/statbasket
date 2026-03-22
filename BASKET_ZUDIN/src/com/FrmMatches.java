package com;

import com.data.Matches;
import com.gui.GuiHelper;

import javax.swing.*;
import java.util.List;

public class FrmMatches extends javax.swing.JDialog {

    private final ApiClient apiClient = new ApiClient();
    private List<Matches> currentMatches;

    /**
     * Creates new form FrmMatches
     */
    public FrmMatches(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        loadMatchesAsync();
    }

    private void loadMatchesAsync() {
        lblStatus.setText("Загрузка матчей с сервера...");
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        SwingWorker<List<Matches>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Matches> doInBackground() throws Exception {
                return apiClient.getAllMatches();
            }

            @Override
            protected void done() {
                try {
                    currentMatches = get();
                    GuiHelper.addObjectsToTable(tblMatches, currentMatches);
                    lblStatus.setText("Матчи успешно загружены.");
                } catch (Exception e) {
                    e.printStackTrace();
                    lblStatus.setText("Ошибка загрузки матчей!");
                    JOptionPane.showMessageDialog(FrmMatches.this, "Ошибка загрузки: " + e.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
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
        // Предполагается, что EdMatchesDialog сам внутри себя загрузит все необходимые данные (команды, турниры)
        EdMatchesDialog dlg = new EdMatchesDialog(null, true, null);
        dlg.setVisible(true);

        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.saveMatch(dlg.getMatches());
                loadMatchesAsync();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения матча: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblMatches.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите матч для редактирования.");
            return;
        }

        Matches selectedMatch = currentMatches.get(selectedRow);

        EdMatchesDialog dlg = new EdMatchesDialog(null, true, selectedMatch);
        dlg.setVisible(true);

        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.saveMatch(dlg.getMatches());
                loadMatchesAsync();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения матча: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblMatches.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите матч для удаления.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить выбранный матч?", "Подтверждение удаления", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Matches selectedMatch = currentMatches.get(selectedRow);
            try {
                apiClient.deleteMatch(selectedMatch.getId());
                loadMatchesAsync();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка удаления матча: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
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
        tblMatches = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Матчи");

        tblMatches.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                // Заголовки для таблицы матчей
                new String [] {"Дата", "Команда 1", "Счет 1", "Счет 2", "Команда 2", "Площадка"}
        ));
        jScrollPane1.setViewportView(tblMatches);

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
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
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
    private javax.swing.JTable tblMatches;
    // End of variables declaration
}