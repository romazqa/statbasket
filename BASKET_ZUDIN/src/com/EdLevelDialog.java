package com;

import com.data.Level;
import javax.swing.JOptionPane;

public class EdLevelDialog extends javax.swing.JDialog {

    private JDialogResult dialogResult;
    private Level level;

    public EdLevelDialog(java.awt.Frame parent, boolean modal, Level level) {
        super(parent, modal);
        initComponents();
        
        // Установка имени для UI-тестов
        tfLevelName.setName("tfLevelName");
        
        this.setLocationRelativeTo(parent);

        if (level == null) {
            this.level = new Level();
            setTitle("Добавление нового уровня");
        } else {
            this.level = level;
            setTitle("Редактирование уровня");
            fillFields();
        }
    }

    public JDialogResult getDialogResult() {
        return dialogResult;
    }

    public Level getLevel() {
        return level;
    }

    private void fillFields() {
        tfLevelName.setText(level.getName());
    }

    private boolean checkAndSave() {
        String levelName = tfLevelName.getText();
        if (levelName == null || levelName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Название уровня не может быть пустым.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        level.setName(levelName);
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
        tfLevelName = new javax.swing.JTextField();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Наименование уровня:");

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
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(tfLevelName, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
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
                    .addComponent(tfLevelName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField tfLevelName;
    // End of variables declaration
}