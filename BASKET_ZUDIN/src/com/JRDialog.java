package com;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
public class JRDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	// Переменная-идентификатор для возврата значений диалогом 
	private JDialogResult dialogResult = JDialogResult.None;
	

	public JDialogResult getDialogResult() {
		return dialogResult;
	}
	
	public void setDialogResult(JDialogResult dialogResult) {
		this.dialogResult = dialogResult;
	}
	/**
	 * Отображает диалог как модальное диалоговое окно.
	 * @return Результат вызова диалогового окна
	 */
	public JDialogResult showDialog()
	{
		setLocationRelativeTo(JRDialog.this.getParent());
           // Окно-модальное 
		setModal(true);
           // Добавление слушателя окна на событие закрытия окна
           //   Результат - Cancel
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				setDialogResult(JDialogResult.Cancel);
				dispose();
			}
		});
           //  Отображение окна
		setVisible(true);
           //  Возврат установленного в диалоге значения
		return getDialogResult();
	}
	/**
	 * Закрывает диалог и освобождает ресурсы
	 */
	public void close()
	{
		dispose();
	} 
 }
