package com;

import com.data.Player;
import com.data.Team;
import com.gui.GuiHelper;

import javax.swing.*;
import java.util.List;

public class FrmPlayer extends javax.swing.JDialog {

    private final ApiClient apiClient = new ApiClient();
    private List<Player> currentPlayers;
    private List<Team> allTeams; // Команды необходимы для диалога добавления/редактирования игрока

    /**
     * Creates new form FrmPlayer
     */
    public FrmPlayer(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        // Установка имен для UI-тестов
        tblPlayer.setName("tblPlayer");
        btnAdd.setName("btnAdd");
        btnEdit.setName("btnEdit");
        btnDelete.setName("btnDelete");
        btnClose.setName("btnClose");
        lblStatus.setName("lblStatus");
        
        this.setLocationRelativeTo(parent);
        loadDataAsync();
    }

    private void loadDataAsync() {
        lblStatus.setText("Загрузка данных с сервера...");
        // Блокируем кнопки на время загрузки
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        SwingWorker<List<Player>, Void> worker = new SwingWorker<List<Player>, Void>() {
            @Override
            protected List<Player> doInBackground() throws Exception {
                // В фоновом потоке загружаем и команды, и игроков
                allTeams = apiClient.getAllTeams();
                return apiClient.getAllPlayers();
            }

            @Override
            protected void done() {
                try {
                    currentPlayers = get();
                    GuiHelper.addObjectsToTable(tblPlayer, currentPlayers);
                    lblStatus.setText("Данные успешно загружены.");
                } catch (Exception e) {
                    e.printStackTrace();
                    lblStatus.setText("Ошибка загрузки данных!");
                    JOptionPane.showMessageDialog(FrmPlayer.this, "Ошибка загрузки: " + e.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Разблокируем кнопки
                    btnAdd.setEnabled(true);
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {
        if (allTeams == null || allTeams.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Данные команд еще не загружены или отсутствуют. Невозможно добавить игрока.");
            return;
        }

        EdPlayerDialog dlg = new EdPlayerDialog(null, true, null, allTeams);
        dlg.setVisible(true);

        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.savePlayer(dlg.getPlayer());
                loadDataAsync(); // Обновляем список после добавления
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения игрока: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblPlayer.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите игрока для редактирования.");
            return;
        }
        if (allTeams == null || allTeams.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Данные команд еще не загружены. Невозможно редактировать игрока.");
            return;
        }

        Player selectedPlayer = currentPlayers.get(selectedRow);

        EdPlayerDialog dlg = new EdPlayerDialog(null, true, selectedPlayer, allTeams);
        dlg.setVisible(true);

        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.savePlayer(dlg.getPlayer());
                loadDataAsync(); // Обновляем список после редактирования
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения игрока: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblPlayer.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите игрока для удаления.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить выбранного игрока?", "Подтверждение удаления", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Player selectedPlayer = currentPlayers.get(selectedRow);
            try {
                apiClient.deletePlayer(selectedPlayer.getId());
                loadDataAsync(); // Обновляем список после удаления
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка удаления игрока: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    //<editor-fold defaultstate="collapsed" desc="Generated Code">
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblPlayer = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Игроки");

        tblPlayer.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"ФИО", "Команда", "Номер", "Рост", "Вес"}
        ));
        jScrollPane1.setViewportView(tblPlayer);

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
    }
    //</editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblPlayer;
}