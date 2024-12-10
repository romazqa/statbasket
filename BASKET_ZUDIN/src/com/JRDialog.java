package com;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
public class JRDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	// ����������-������������� ��� �������� �������� �������� 
	private JDialogResult dialogResult = JDialogResult.None;
	

	public JDialogResult getDialogResult() {
		return dialogResult;
	}
	
	public void setDialogResult(JDialogResult dialogResult) {
		this.dialogResult = dialogResult;
	}
	/**
	 * ���������� ������ ��� ��������� ���������� ����.
	 * @return ��������� ������ ����������� ����
	 */
	public JDialogResult showDialog()
	{
		setLocationRelativeTo(JRDialog.this.getParent());
           // ����-��������� 
		setModal(true);
           // ���������� ��������� ���� �� ������� �������� ����
           //   ��������� - Cancel
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				setDialogResult(JDialogResult.Cancel);
				dispose();
			}
		});
           //  ����������� ����
		setVisible(true);
           //  ������� �������������� � ������� ��������
		return getDialogResult();
	}
	/**
	 * ��������� ������ � ����������� �������
	 */
	public void close()
	{
		dispose();
	} 
 }
