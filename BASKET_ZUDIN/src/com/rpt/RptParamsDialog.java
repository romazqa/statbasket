package com.rpt;

import javax.swing.JButton;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import com.JDialogResult;
import com.JRDialog;
import net.miginfocom.swing.MigLayout;

public class RptParamsDialog extends JRDialog {

	private static final long serialVersionUID = 1L;
	// компонент "Календарик"
	private JDateChooser startDate;
	private JDateChooser endDate;
	private JButton btnOk, btnCancel;
	private RptParams params = null;

	public RptParamsDialog(Window parent) throws ParseException {
		setTitle("Параметры отчета");
		createGUI();
		loadData();
		bindListeners();
		pack();
// запрет изменения размеров
		setResizable(false);
	}

	private void createGUI() {
		// метод создания графич. интерфейса
		MigLayout layout = new MigLayout("insets 7,gapy 5,wrap 2", "[]10[]", "[]7[]5[]10[]");
		JPanel pnl = new JPanel(layout);
		startDate = new JDateChooser();
		endDate = new JDateChooser();
		// добавляем на панель метку
		pnl.add(new JLabel("Дата начала:"));
		pnl.add(startDate, "w 120::,grow, sg 1");
		pnl.add(new JLabel("Дата окончания:"));
		pnl.add(endDate, "grow, sg 1");
		btnOk = new JButton("Принять");
		btnCancel = new JButton("Отмена");
		pnl.add(btnOk, "span, split 2, right, sg");
		pnl.add(btnCancel, "sg");
		getContentPane().add(pnl);
	}

	private void loadData() throws ParseException {
// заполняем компоненты 
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		startDate.setDate(formatter.parse("07-12-2020"));
		endDate.setDate(formatter.parse("03-04-2024"));
	}

	private void bindListeners() {
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (buildParams()) {
					setDialogResult(JDialogResult.OK);
					close();
				}
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDialogResult(JDialogResult.Cancel);
				close();
			}
		});
	}

	public RptParams getParams() {
		return params;
	}

	/**
	 * * Формирование параметров отчета
	 * 
	 * @return True, если параметры удачно сформированы
	 */
	protected boolean buildParams() {
		if (!validateData())
			return false;
		RptParams params = new RptParams();
		params.StartDate = startDate.getDate();
		params.EndDate = endDate.getDate();
		this.params = params;
		return true;
	}

	/**
	 * Проверка правильности ввода данных
	 * 
	 * @return True, если данные введены правильно
	 */
	private boolean validateData() {
		try {
			Date start = startDate.getDate();
			Date end = endDate.getDate();
			if (start == null)
				// ошибка
				throw new Exception("Неверно указана дата начала");
			if (end == null)
				throw new Exception("Неверно указана дата окончания");
			if (start.after(end))
				throw new Exception("Временной диапазон задан неверно");
			return true;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка ввода данных", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}