package com;

import com.gui.LookAndFillUtil;

import javax.swing.SwingUtilities;

public class PgAppBasket {

    /**
     * Точка входа в приложение.
     */
    public static void main(String[] args) {
        // Запускаем интерфейс в специальном потоке Swing (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Устанавливаем красивый внешний вид (если у вас есть этот класс)
                    LookAndFillUtil.setDefault();
                    
                    // ЕСЛИ У ВАС БЫЛО ОКНО ЛОГИНА (LoginDialog):
                    // Пока мы не настроили авторизацию на сервере, 
                    // давайте запускать сразу главное окно, чтобы проверить работу.
                    
                    // Создаем и показываем главное окно
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}