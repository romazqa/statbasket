package com;

import com.data.Matches;
import com.data.Player;
import com.data.Stat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrmMatchStats extends JDialog {

    private final ApiClient apiClient = new ApiClient();
    private Matches match;

    // Списки для хранения статистики, чтобы знать, кого мы редактируем
    private List<Stat> statsTeam1 = new ArrayList<>();
    private List<Stat> statsTeam2 = new ArrayList<>();

    public FrmMatchStats(Frame parent, Matches match) {
        super(parent, true);
        this.match = match;
        
        initComponents();
        this.setLocationRelativeTo(parent);
        updateTables();
    }

    private void normalizePlayerStats() {
        if (match.getPlayerStats() == null) match.setPlayerStats(new ArrayList<>());

        Map<Integer, Stat> existingStats = new HashMap<>();
        for (Stat s : match.getPlayerStats()) {
            if (s.getPlayer() != null) existingStats.put(s.getPlayer().getId(), s);
        }

        if (match.getTeam1() != null && match.getTeam1().getPlayers() != null) {
            for (Player p : match.getTeam1().getPlayers()) {
                if (!existingStats.containsKey(p.getId())) {
                    Stat s = new Stat(); s.setPlayer(p); s.setIdPlayer(p.getId()); s.setIdMatch(match.getId());
                    match.getPlayerStats().add(s); existingStats.put(p.getId(), s);
                }
            }
        }
        if (match.getTeam2() != null && match.getTeam2().getPlayers() != null) {
            for (Player p : match.getTeam2().getPlayers()) {
                if (!existingStats.containsKey(p.getId())) {
                    Stat s = new Stat(); s.setPlayer(p); s.setIdPlayer(p.getId()); s.setIdMatch(match.getId());
                    match.getPlayerStats().add(s); existingStats.put(p.getId(), s);
                }
            }
        }
    }

    private void updateTables() {
        normalizePlayerStats();
        statsTeam1.clear();
        statsTeam2.clear();

        DefaultTableModel model1 = (DefaultTableModel) tblTeam1.getModel();
        DefaultTableModel model2 = (DefaultTableModel) tblTeam2.getModel();
        model1.setRowCount(0);
        model2.setRowCount(0);

        Integer team1Id = match.getTeam1().getId();

        for (Stat s : match.getPlayerStats()) {
            if (s.getPlayer() != null && s.getPlayer().getTeam() != null) {
                Object[] row = createRow(s);
                if (s.getPlayer().getTeam().getId().equals(team1Id)) {
                    model1.addRow(row);
                    statsTeam1.add(s);
                } else {
                    model2.addRow(row);
                    statsTeam2.add(s);
                }
            }
        }
    }

    private Object[] createRow(Stat s) {
        return new Object[]{
            s.getPlayer().getName(),
            getSafeInt(s.getPointScored()),
            getSafeInt(s.getAssists()),
            getSafeInt(s.getSteal()),
            getSafeInt(s.getTurnover()),
            getSafeInt(s.getBlockedShot()),
            getSafeInt(s.getFoul()),
            getSafeInt(s.getDoubleDouble()),
            getSafeInt(s.getTriple()),
            getSafeInt(s.getFreeThrow()),
            getSafeInt(s.getDr()),
            getSafeInt(s.getOr())
        };
    }

    private int getSafeInt(Integer val) { return val == null ? 0 : val; }

    private void saveMatchAsync() {
        lblStatus.setText("Сохранение...");
        btnEdit.setEnabled(false);

        SwingWorker<Matches, Void> worker = new SwingWorker<>() {
            @Override protected Matches doInBackground() throws Exception { return apiClient.saveMatch(match); }
            @Override protected void done() {
                try {
                    match = get();
                    updateTables();
                    lblStatus.setText("Успешно сохранено.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FrmMatchStats.this, "Ошибка: " + e.getMessage());
                } finally { btnEdit.setEnabled(true); }
            }
        };
        worker.execute();
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        int row1 = tblTeam1.getSelectedRow();
        int row2 = tblTeam2.getSelectedRow();

        Stat selectedStat = null;
        if (row1 != -1) {
            selectedStat = statsTeam1.get(row1);
        } else if (row2 != -1) {
            selectedStat = statsTeam2.get(row2);
        } else {
            JOptionPane.showMessageDialog(this, "Выберите игрока в одной из таблиц.");
            return;
        }

        EdStatDialog dlg = new EdStatDialog(null, selectedStat, match.getId());
        dlg.setVisible(true);

        if (dlg.getDialogResult() == JDialogResult.OK) {
            saveMatchAsync();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        tblTeam1 = new JTable();
        tblTeam2 = new JTable();
        btnEdit = new JButton("Изменить");
        btnClose = new JButton("Закрыть");
        lblStatus = new JLabel(" ");

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Статистика матча: " + match.getTeam1().getName() + " vs " + match.getTeam2().getName());

        String[] columns = {"Игрок", "Очки", "АС", "ПХ", "ПТ", "БШ", "Ф", "2-очк", "3-очк", "ШТ", "ПБ-З", "ПБ-А"};
        tblTeam1.setModel(new DefaultTableModel(new Object[][]{}, columns));
        tblTeam2.setModel(new DefaultTableModel(new Object[][]{}, columns));

        // Добавляем слушатели: если кликнули в одной таблице, сбрасываем выделение в другой
        tblTeam1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblTeam1.getSelectedRow() != -1) tblTeam2.clearSelection();
        });
        tblTeam2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblTeam2.getSelectedRow() != -1) tblTeam1.clearSelection();
        });

        btnEdit.addActionListener(this::btnEditActionPerformed);
        btnClose.addActionListener(e -> this.dispose());

        // Создаем панели для каждой команды
        JPanel pnlTeam1 = new JPanel(new BorderLayout());
        pnlTeam1.setBorder(BorderFactory.createTitledBorder("Команда: " + match.getTeam1().getName()));
        pnlTeam1.add(new JScrollPane(tblTeam1), BorderLayout.CENTER);

        JPanel pnlTeam2 = new JPanel(new BorderLayout());
        pnlTeam2.setBorder(BorderFactory.createTitledBorder("Команда: " + match.getTeam2().getName()));
        pnlTeam2.add(new JScrollPane(tblTeam2), BorderLayout.CENTER);

        // Размещаем таблицы рядом
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tablesPanel.add(pnlTeam1);
        tablesPanel.add(pnlTeam2);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(btnEdit);
        bottomPanel.add(lblStatus, BorderLayout.WEST);
        bottomPanel.add(buttonsPanel, BorderLayout.CENTER);
        bottomPanel.add(btnClose, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(tablesPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setSize(1200, 500); // Сделали окно шире для двух таблиц
    }// </editor-fold>

    private JButton btnClose, btnEdit;
    private JLabel lblStatus;
    private JTable tblTeam1, tblTeam2;
}