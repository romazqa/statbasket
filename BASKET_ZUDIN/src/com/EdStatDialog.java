package com;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import com.data.*;

import net.miginfocom.swing.MigLayout;

public class EdStatDialog extends JRDialog {

    private static final long serialVersionUID = 1L;
    
    // API клиент для работы с сетью
    private final ApiClient apiClient = new ApiClient();
    
    private Integer fk_key; // ID матча
    private Integer old_key; // Прежнее значение ключа статистики
    
    // Заголовки окна
    private final static String title_add = "Добавление новой информации";
    private final static String title_ed = "Редактирование статистики";
    
    // Объект статистики
    private Stat type = null;
    
    // Флаг режима «добавление новой строки»
    private boolean isNewRow = false;
    
    // Элементы UI (поля ввода)
    private JTextField edpointscored;
    private JTextField edAssistsoim;
    private JTextField edSteal;
    private JTextField ed2, ed3;
    private JTextField edDr, edOr, edFree;
    private JTextField edTurnover, edBlockedshot, edFoull;

    private JComboBox<Player> cmbPerso; // Типизированный список игроков
    private JTextField edCod_perso;
    
    // Кнопки управления
    private JButton btnOk;
    private JButton btnCancel;
    
    // Список всех игроков для выпадающего списка
    private List<Player> playerList;

    /**
     * Конструктор диалога редактирования статистики
     */
    public EdStatDialog(Frame parent, Stat type, Integer fk_key) {
        super(parent, true); // Вызываем конструктор JRDialog(Frame, boolean)
        
        // Определение режима работы (создание или редактирование)
        isNewRow = type == null;
        setTitle(isNewRow ? title_add : title_ed);
        this.fk_key = fk_key;
        
        if (!isNewRow) {
            this.type = type; 
            old_key = type.getIdPlayerStats();
        } else {
            this.type = new Stat(); 
        }
        
        // Инициализация интерфейса
        createGui();
        
        // Назначение слушателей событий
        bindListeners();
        
        // Асинхронная загрузка списка игроков с сервера
        loadDataAsync();
        
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void loadDataAsync() {
        // Блокируем выбор игрока и кнопку сохранения на время загрузки
        cmbPerso.setEnabled(false);
        btnOk.setEnabled(false);
        
        SwingWorker<List<Player>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Player> doInBackground() throws Exception {
                return apiClient.getAllPlayers();
            }

            @Override
            protected void done() {
                try {
                    playerList = get();
                    populateComboBox();
                    
                    if (!isNewRow) {
                        fillFields();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(EdStatDialog.this, 
                        "Ошибка загрузки списка игроков: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                } finally {
                    cmbPerso.setEnabled(true);
                    btnOk.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void populateComboBox() {
        cmbPerso.removeAllItems();
        if (playerList != null) {
            for (Player p : playerList) {
                cmbPerso.addItem(p);
            }
        }
    }

    private void fillFields() {
        // Установка значений в текстовые поля
        setTextOrEmpty(edAssistsoim, type.getAssists());
        setTextOrEmpty(ed2, type.getDoubleDouble());
        setTextOrEmpty(ed3, type.getTriple());
        setTextOrEmpty(edpointscored, type.getPointScored());
        
        if (type.getPlayer() != null) {
            edCod_perso.setText(String.valueOf(type.getPlayer().getId()));
            // Поиск и выбор игрока в ComboBox по ID
            for (int i = 0; i < cmbPerso.getItemCount(); i++) {
                Player item = cmbPerso.getItemAt(i);
                if (item.getId().equals(type.getPlayer().getId())) {
                    cmbPerso.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        setTextOrEmpty(edBlockedshot, type.getBlockedShot());
        setTextOrEmpty(edFoull, type.getFoul());
        setTextOrEmpty(edSteal, type.getSteal());
        setTextOrEmpty(edTurnover, type.getTurnover());
        setTextOrEmpty(edFree, type.getFreeThrow());
        setTextOrEmpty(edDr, type.getDr());
        setTextOrEmpty(edOr, type.getOr());
    }
    
    private void setTextOrEmpty(JTextField field, Object value) {
        field.setText(value != null ? value.toString() : "");
    }

    private void bindListeners() {
        // Закрытие окна по ESC
        setKeyListener(this, new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setDialogResult(JDialogResult.Cancel);
                    close();
                    e.consume();
                }
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        
        btnCancel.addActionListener(e -> {
            setDialogResult(JDialogResult.Cancel);
            close();
        });
        
        btnOk.addActionListener(e -> {
            if (!constructStat()) return;
            // Возвращаем результат OK вызывающей форме
            setDialogResult(JDialogResult.OK);
            close();
        });

        // Синхронизация ComboBox и текстового поля с ID игрока
        cmbPerso.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
                Player selected = (Player) e.getItem();
                edCod_perso.setText(String.valueOf(selected.getId()));
            }
        });

        edCod_perso.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String text = edCod_perso.getText().trim();
                if (!text.isEmpty()) {
                    try {
                        Integer id = Integer.valueOf(text);
                        for (int i = 0; i < cmbPerso.getItemCount(); i++) {
                            if (cmbPerso.getItemAt(i).getId().equals(id)) {
                                cmbPerso.setSelectedIndex(i);
                                break;
                            }
                        }
                    } catch (NumberFormatException ex) { }
                }
            }
        });
    }

    private void createGui() {
        JPanel pnl = new JPanel(new MigLayout("insets 5", "[][]", "[]5[]10[]"));
        
        edpointscored = new JTextField(10);
        cmbPerso = new JComboBox<>();
        edCod_perso = new JTextField(10);
        edBlockedshot = new JTextField(20);
        edFoull = new JTextField(20);
        ed2 = new JTextField(20);
        ed3 = new JTextField(20);
        edAssistsoim = new JTextField(20);
        edTurnover = new JTextField(20);
        edSteal = new JTextField(20);
        edDr = new JTextField(20);
        edOr = new JTextField(20);
        edFree = new JTextField(20);

        btnOk = new JButton("Сохранить");
        btnCancel = new JButton("Отмена");

        // Добавление компонентов на панель
        pnl.add(new JLabel("Игрок"));
        pnl.add(edCod_perso, "split 2");
        pnl.add(cmbPerso, "growx, wrap");
        
        pnl.add(new JLabel("Очков"));
        pnl.add(edpointscored, "span");
        
        pnl.add(new JLabel("Кол-во ассистов"));
        pnl.add(edAssistsoim, "span");
        
        pnl.add(new JLabel("Кол-во перехватов"));
        pnl.add(edSteal, "span");

        pnl.add(new JLabel("Кол-во потерь мяча"));
        pnl.add(edTurnover, "span");
        
        pnl.add(new JLabel("Кол-во блок-шотов"));
        pnl.add(edBlockedshot, "span");

        pnl.add(new JLabel("Кол-во фолов"));
        pnl.add(edFoull, "span");

        pnl.add(new JLabel("Кол-во 2-х очковых"));
        pnl.add(ed2, "span");

        pnl.add(new JLabel("Кол-во 3-х очковых"));
        pnl.add(ed3, "span");

        pnl.add(new JLabel("Кол-во штрафных"));
        pnl.add(edFree, "span");
        
        pnl.add(new JLabel("Подборы (защита)"));
        pnl.add(edDr, "span");
        
        pnl.add(new JLabel("Подборы (атака)"));
        pnl.add(edOr, "span");

        pnl.add(btnOk, "span, split 2, center, sg ");
        pnl.add(btnCancel, "sg 1");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnl, BorderLayout.CENTER);
    }

    private boolean constructStat() {
        try {
            type.setIdMatch(fk_key);
            type.setAssists(parseIntOrNull(edAssistsoim.getText()));
            type.setPointScored(parseIntOrNull(edpointscored.getText()));
            type.setFoul(parseIntOrNull(edFoull.getText()));
            type.setBlockedShot(parseIntOrNull(edBlockedshot.getText()));
            type.setTurnover(parseIntOrNull(edTurnover.getText()));
            type.setSteal(parseIntOrNull(edSteal.getText()));
            type.setDoubleDouble(parseIntOrNull(ed2.getText()));
            type.setTriple(parseIntOrNull(ed3.getText()));
            type.setFreeThrow(parseIntOrNull(edFree.getText()));
            type.setDr(parseIntOrNull(edDr.getText()));
            type.setOr(parseIntOrNull(edOr.getText()));

            Player selected = (Player) cmbPerso.getSelectedItem();
            if (selected != null) {
                type.setPlayer(selected);
                type.setIdPlayer(selected.getId());
            } else {
                JOptionPane.showMessageDialog(this, "Пожалуйста, выберите игрока!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка формата данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private Integer parseIntOrNull(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return Integer.parseInt(text.trim());
    }

    public Stat getStat() {
        return type;
    }

    private void setKeyListener(Component c, KeyListener kl) {
        c.addKeyListener(kl);
        if (c instanceof Container)
            for (Component comp : ((Container) c).getComponents())
                setKeyListener(comp, kl);
    }
}