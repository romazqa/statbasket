package com;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gui.LookAndFillUtil;

public class PgAppBasket {
	private DBManager manager = null;


	// Метод старта приложения
	public void run() {
		// Вывод окна регистрации
		if (showLoginWnd()) {
			// При успешном соединении отображение основного окна
			showMainWnd();
		} else {
			// иначе - закрытие приложения
			closeApplication();
		}
		
	}

	// Вывод окна регистрации
	private boolean showLoginWnd() {
		try {
			manager = new DBManager();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// создание окна
		LoginDialog dlg = new LoginDialog(manager);
		// вывод окна
		JDialogResult res = dlg.showDialog();
		// Возврат true при установке соединения
		return (res == JDialogResult.OK);
	}

	// Вывод основного окна приложения
	private void showMainWnd() {
		// вывод окна заставки
		// в отдельном потоке
		WaitingDialog wd = new WaitingDialog("Загружается программа...");
		wd.setVisible(true);
		// объявление объектной переменной класса основного окна
		MainWindow mainWindow = null;
		try {
			// Вызов метода создания основного окна
			mainWindow = createMainWindow(manager);
			// Добавление слушателя на закрытие окна
			mainWindow.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					closeApplication();
				}
			});
			// Инициализация окна
			mainWindow.initialize();
			// Закрытие и удаление окна-заставки
			wd.setVisible(false);
			wd.dispose();
			// отображение основного окна
			mainWindow.setVisible(true);
		} catch (Exception ex) {
			// обработка ошибки установки соединения:
			// вывод сообщения
			// и закрытие и удаление окна-заставки
			String s = ex.getMessage();
			JOptionPane.showMessageDialog(null, s, "Ошибка", JOptionPane.ERROR_MESSAGE);
			wd.setVisible(false);
			wd.dispose();
		}
	}

	// Создание основного окна
	private MainWindow createMainWindow(DBManager manager) {
		return new MainWindow(manager);
	}

	// Закрытие приложения
	private void closeApplication() {
		System.exit(0);
	}

	// Точка входа в приложение – метод main
	public static void main(String[] args) {
		try {
	        // Устанавливаем тему Nimbus
	        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 13)); // Изменяем шрифт кнопок
			UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 12)); // Изменяем шрифт меток
			
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		PgAppBasket app = new PgAppBasket();
		// вызов его метода старта
		app.run();
	}
}
