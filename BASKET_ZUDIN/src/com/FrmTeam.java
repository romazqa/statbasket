package com;

import com.data.Team;
import com.gui.GuiHelper;

import javax.swing.*;
import java.util.List;

public class FrmTeam extends javax.swing.JDialog {

    private final ApiClient apiClient = new ApiClient();
    private List<Team> currentTeams;

    /**
     * Creates new form FrmTeam
     */
    public FrmTeam(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        loadTeamsAsync();
    }

    private void loadTeamsAsync() {
        lblStatus.setText("Загрузка команд с сервера...");
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        SwingWorker<List<Team>, Void> worker = new SwingWorker<List<Team>, Void>() {
            @Override
            protected List<Team> doInBackground() throws Exception {
                return apiClient.getAllTeams();
            }

            @Override
            protected void done() {
                try {
                    currentTeams = get();
                    GuiHelper.addObjectsToTable(tblTeam, currentTeams);
                    lblStatus.setText("Команды успешно загружены.");
                } catch (Exception e) {
                    e.printStackTrace();
                    lblStatus.setText("Ошибка загрузки команд!");
                    JOptionPane.showMessageDialog(FrmTeam.this, "Ошибка загрузки: " + e.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
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
        // Вызываем НОВЫЙ конструктор: parent=null, modal=true, team=null
        EdTeamDialog dlg = new EdTeamDialog(null, true, null);
        dlg.setVisible(true);

        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.saveTeam(dlg.getTeam());
                loadTeamsAsync(); // Обновляем список
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения команды: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblTeam.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите команду для редактирования.");
            return;
        }

        Team selectedTeam = currentTeams.get(selectedRow);

        // Вызываем НОВЫЙ конструктор: parent=null, modal=true, team=selectedTeam
        EdTeamDialog dlg = new EdTeamDialog(null, true, selectedTeam);
        dlg.setVisible(true);

        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.saveTeam(dlg.getTeam());
                loadTeamsAsync(); // Обновляем список
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения команды: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblTeam.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите команду для удаления.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить выбранную команду?", "Подтверждение удаления", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Team selectedTeam = currentTeams.get(selectedRow);
            try {
                apiClient.deleteTeam(selectedTeam.getId().intValue());
                loadTeamsAsync(); // Обновляем список
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка удаления команды: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
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
        tblTeam = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Команды");

        tblTeam.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Название", "Город", "Пол"}
        ));
        jScrollPane1.setViewportView(tblTeam);

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
    private javax.swing.JTable tblTeam;
    // End of variables declaration
}