package com;
import javax.swing.JDialog;
import java.awt.Color;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
public class WaitingDialog extends JDialog
	{
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MSG = 
	           "Ожидание загрузки...";
		public WaitingDialog(JDialog parent, String Message)
		{
			super(parent);
			setUndecorated(true);
			setFocusableWindowState(false);
			JPanel pnlContent = 
	           new JPanel(new MigLayout("insets 7", "", ""));
			JLabel lblContent = new JLabel();
			lblContent.setText(Message == 
	               null ? DEFAULT_MSG : Message);
			pnlContent.add(lblContent);
	pnlContent.setBorder(BorderFactory.createLineBorder
	(Color.BLACK));
			getContentPane().add(pnlContent);
			pack();
			setModal(true);
			setLocationRelativeTo(this.getParent());
		}

		public WaitingDialog(String Message)
		{
			this(null, Message);
		}

		public WaitingDialog()
		{
			this(null);
		}
	   //  Вывод окна в отдельном потоке 
	   //  (чтобы не мешало загрузке приложения)
		@Override
		public void setVisible(boolean b)
		{
			if (b)
			  new Thread()
			  {
				public void run(){
					WaitingDialog.super.setVisible(true);
				};
			  }.start();
			else
			{
				if (SwingUtilities.isEventDispatchThread())
				{
					super.setVisible(false);
					dispose();
				}
				else
					try
					{
					SwingUtilities.invokeAndWait(new Runnable()
						{
							@Override
							public void run()
							{
						WaitingDialog.super.setVisible(false);
								dispose();
							}
						});
					}
					catch (Exception e)
					{
						//
					}
			}
		}
	}
