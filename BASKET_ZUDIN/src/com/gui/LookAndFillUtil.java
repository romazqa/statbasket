package com.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Класс для установки внешнего вида приложения
 */
public class LookAndFillUtil {

    /**
     * Установка современного стиля (Nimbus или системный)
     */
    public static void setMyLookAndFill() {
        // Разрешаем темам отрисовывать рамки окон в своем стиле
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        try {
            // 1. Пытаемся установить красивую встроенную тему Nimbus
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    
                    // Немного увеличиваем стандартный шрифт для удобства (по желанию)
                    UIManager.getLookAndFeelDefaults().put("defaultFont", new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
                    return;
                }
            }
            
            // 2. Если Nimbus почему-то не найден, ставим стиль операционной системы (Windows)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (Exception e) {
            System.err.println("Не удалось применить современный стиль окна. Используется стандартный.");
            e.printStackTrace();
        }
    }

    /**
     * Для обратной совместимости, если где-то вызывается setDefault()
     */
    public static void setDefault() {
        setMyLookAndFill();
    }
}