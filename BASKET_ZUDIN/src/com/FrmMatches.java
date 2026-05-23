package com;

import com.data.Matches;
import com.gui.GuiHelper;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class FrmMatches extends javax.swing.JDialog {

    private final ApiClient apiClient = new ApiClient();
    private List<Matches> currentMatches;
    
    // Переменные для пагинации
    private int currentPage = 0;
    private final int pageSize = 15; // Загружаем по 15 матчей за раз
    private int totalPages = 1;

    public FrmMatches(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tblMatches.setName("tblMatches");
        btnDelete.setName("btnDelete"); // Оставили для тестов

        this.setLocationRelativeTo(parent);
        loadMatchesAsync();
    }

    private void loadMatchesAsync() {
        lblStatus.setText("Загрузка матчей...");
        setButtonsEnabled(false);

        SwingWorker<ApiClient.MatchPage, Void> worker = new SwingWorker<>() {
            @Override
            protected ApiClient.MatchPage doInBackground() throws Exception {
                // Запрашиваем конкретную страницу
                return apiClient.getMatchesPage(currentPage, pageSize);
            }

            @Override
            protected void done() {
                try {
                    ApiClient.MatchPage pageInfo = get();
                    currentMatches = pageInfo.matches;
                    totalPages = pageInfo.totalPages == 0 ? 1 : pageInfo.totalPages; // Защита от 0 страниц
                    
                    GuiHelper.addObjectsToTable(tblMatches, currentMatches);
                    
                    lblStatus.setText("Страница " + (currentPage + 1) + " из " + totalPages);
                    
                    // Управление кнопками страниц
                    btnPrevPage.setEnabled(currentPage > 0);
                    btnNextPage.setEnabled(currentPage < totalPages - 1);
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FrmMatches.this, "Ошибка: " + e.getMessage());
                } finally {
                    setButtonsEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnAdd.setEnabled(enabled);
        btnEdit.setEnabled(enabled);
        btnStats.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
    }

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {
        EdMatchesDialog dlg = new EdMatchesDialog(null, true, null);
        dlg.setVisible(true);
        if (dlg.getDialogResult() == JDialogResult.OK) {
            try { apiClient.saveMatch(dlg.getMatches()); loadMatchesAsync(); } catch (Exception ex) {}
        }
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblMatches.getSelectedRow();
        if (row == -1) return;
        EdMatchesDialog dlg = new EdMatchesDialog(null, true, currentMatches.get(row));
        dlg.setVisible(true);
        if (dlg.getDialogResult() == JDialogResult.OK) {
            try { apiClient.saveMatch(dlg.getMatches()); loadMatchesAsync(); } catch (Exception ex) {}
        }
    }

    private void btnStatsActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblMatches.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите матч."); return;
        }

        setButtonsEnabled(false);
        lblStatus.setText("Загрузка статистики...");

        SwingWorker<Matches, Void> worker = new SwingWorker<>() {
            @Override
            protected Matches doInBackground() throws Exception {
                return apiClient.getMatchById(currentMatches.get(row).getId());
            }
            @Override
            protected void done() {
                try {
                    FrmMatchStats statsForm = new FrmMatchStats(null, get());
                    statsForm.setVisible(true);
                    loadMatchesAsync();
                } catch (Exception e) {} finally { setButtonsEnabled(true); }
            }
        };
        worker.execute();
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblMatches.getSelectedRow();
        if (row == -1) return;
        if (JOptionPane.showConfirmDialog(this, "Удалить матч?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                apiClient.deleteMatch(currentMatches.get(row).getId());
                // Если мы удалили последний элемент на странице, возвращаемся назад
                if (currentMatches.size() == 1 && currentPage > 0) currentPage--;
                loadMatchesAsync();
            } catch (Exception ex) { }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jScrollPane1 = new JScrollPane();
        tblMatches = new JTable();
        
        btnAdd = new JButton("Добавить");
        btnEdit = new JButton("Изменить");
        btnStats = new JButton("Статистика");
        btnDelete = new JButton("Удалить");
        btnClose = new JButton("Закрыть");
        
        btnPrevPage = new JButton("<< Пред.");
        btnNextPage = new JButton("След. >>");
        lblStatus = new JLabel("Статус");

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("История матчей");

        tblMatches.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {}, new String [] {"Дата", "Команда 1", "Счет 1", "Счет 2", "Команда 2", "Площадка"}
        ));
        jScrollPane1.setViewportView(tblMatches);

        btnAdd.addActionListener(evt -> btnAddActionPerformed(evt));
        btnEdit.addActionListener(evt -> btnEditActionPerformed(evt));
        btnStats.addActionListener(evt -> btnStatsActionPerformed(evt));
        btnDelete.addActionListener(evt -> btnDeleteActionPerformed(evt));
        btnClose.addActionListener(evt -> this.dispose());

        // Логика перелистывания страниц
        btnPrevPage.addActionListener(evt -> { if (currentPage > 0) { currentPage--; loadMatchesAsync(); } });
        btnNextPage.addActionListener(evt -> { if (currentPage < totalPages - 1) { currentPage++; loadMatchesAsync(); } });

        // Панель пагинации
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(lblStatus);
        paginationPanel.add(btnNextPage);

        // Панель кнопок действий
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(btnAdd); actionPanel.add(btnEdit); actionPanel.add(btnStats); actionPanel.add(btnDelete);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(paginationPanel, BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.WEST);
        bottomPanel.add(btnClose, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(jScrollPane1, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setSize(800, 400);
    }// </editor-fold>

    private JButton btnAdd, btnClose, btnDelete, btnEdit, btnStats;
    private JButton btnPrevPage, btnNextPage;
    private JScrollPane jScrollPane1;
    private JLabel lblStatus;
    private JTable tblMatches;
}