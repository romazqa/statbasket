package com;

import com.data.Event;
import com.gui.GuiHelper;

import javax.swing.*;
import java.util.List;

public class FrmEvent extends javax.swing.JDialog {

    private final ApiClient apiClient = new ApiClient();
    private List<Event> currentEvents;

    /**
     * Creates new form FrmEvent
     */
    public FrmEvent(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        // Установка имен для UI-тестов
        tblEvent.setName("tblEvent");
        btnAdd.setName("btnAdd");
        btnEdit.setName("btnEdit");
        btnDelete.setName("btnDelete");
        btnClose.setName("btnClose");
        lblStatus.setName("lblStatus");
        
        this.setLocationRelativeTo(parent);
        loadEventsAsync();
    }

    private void loadEventsAsync() {
        lblStatus.setText("Загрузка соревнований...");
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        SwingWorker<List<Event>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                return apiClient.getAllEvents();
            }

            @Override
            protected void done() {
                try {
                    currentEvents = get();
                    GuiHelper.addObjectsToTable(tblEvent, currentEvents);
                    lblStatus.setText("Соревнования успешно загружены.");
                } catch (Exception e) {
                    e.printStackTrace();
                    lblStatus.setText("Ошибка загрузки!");
                    JOptionPane.showMessageDialog(FrmEvent.this, "Ошибка загрузки: " + e.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
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
        EdEventDialog dlg = new EdEventDialog(null, true, null);
        dlg.setVisible(true);
        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.saveEvent(dlg.getEvent());
                loadEventsAsync();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblEvent.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите соревнование для редактирования.");
            return;
        }

        Event selectedEvent = currentEvents.get(selectedRow);
        EdEventDialog dlg = new EdEventDialog(null, true, selectedEvent);
        dlg.setVisible(true);
        if (dlg.getDialogResult() == JDialogResult.OK) {
            try {
                apiClient.saveEvent(dlg.getEvent());
                loadEventsAsync();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + ex.getMessage(), "Ошибка сети", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblEvent.getSelectedRow();
        if (selectedRow == -1) {
             JOptionPane.showMessageDialog(this, "Выберите соревнование для удаления.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Удалить выбранное соревнование?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Event selectedEvent = currentEvents.get(selectedRow);
            try {
                apiClient.deleteEvent(selectedEvent.getId());
                loadEventsAsync();
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
        tblEvent = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Соревнования");

        tblEvent.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Название", "Место проведения", "Год", "Уровень"}
        ));
        jScrollPane1.setViewportView(tblEvent);

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
    private javax.swing.JTable tblEvent;
}