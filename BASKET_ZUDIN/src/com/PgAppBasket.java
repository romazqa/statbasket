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


	// ����� ������ ����������
	public void run() {
		// ����� ���� �����������
		if (showLoginWnd()) {
			// ��� �������� ���������� ����������� ��������� ����
			showMainWnd();
		} else {
			// ����� - �������� ����������
			closeApplication();
		}
		
	}

	// ����� ���� �����������
	private boolean showLoginWnd() {
		try {
			manager = new DBManager();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// �������� ����
		LoginDialog dlg = new LoginDialog(manager);
		// ����� ����
		JDialogResult res = dlg.showDialog();
		// ������� true ��� ��������� ����������
		return (res == JDialogResult.OK);
	}

	// ����� ��������� ���� ����������
	private void showMainWnd() {
		// ����� ���� ��������
		// � ��������� ������
		WaitingDialog wd = new WaitingDialog("����������� ���������...");
		wd.setVisible(true);
		// ���������� ��������� ���������� ������ ��������� ����
		MainWindow mainWindow = null;
		try {
			// ����� ������ �������� ��������� ����
			mainWindow = createMainWindow(manager);
			// ���������� ��������� �� �������� ����
			mainWindow.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					closeApplication();
				}
			});
			// ������������� ����
			mainWindow.initialize();
			// �������� � �������� ����-��������
			wd.setVisible(false);
			wd.dispose();
			// ����������� ��������� ����
			mainWindow.setVisible(true);
		} catch (Exception ex) {
			// ��������� ������ ��������� ����������:
			// ����� ���������
			// � �������� � �������� ����-��������
			String s = ex.getMessage();
			JOptionPane.showMessageDialog(null, s, "������", JOptionPane.ERROR_MESSAGE);
			wd.setVisible(false);
			wd.dispose();
		}
	}

	// �������� ��������� ����
	private MainWindow createMainWindow(DBManager manager) {
		return new MainWindow(manager);
	}

	// �������� ����������
	private void closeApplication() {
		System.exit(0);
	}

	// ����� ����� � ���������� � ����� main
	public static void main(String[] args) {
		try {
	        // ������������� ���� Nimbus
	        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 13)); // �������� ����� ������
			UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 12)); // �������� ����� �����
			
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		PgAppBasket app = new PgAppBasket();
		// ����� ��� ������ ������
		app.run();
	}
}
