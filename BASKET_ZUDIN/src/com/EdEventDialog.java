package com;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import com.data.*;

import net.miginfocom.swing.MigLayout;

public class EdEventDialog extends JRDialog {

	private static final long serialVersionUID = 1L;
	// ���� ������
	  private DBManager manager;
	  private BigDecimal old_key; // ������� �������� �����
	  // ��� �������� ��������� ����
	  private final static String title_add = 
	    "���������� ������ ������������";
	  private final static String title_ed = 
	     "�������������� ������������";
	   //  ��������� ���������� ��� ������ ������������
		private Event type = null;
	    // ���� ������ ����������� ����� ������
		private boolean isNewRow = false;
	    // �������� ��� ����� ���
		SimpleDateFormat frmt = 
	          new SimpleDateFormat("dd-MM-yyyy");
	    // �������� (���� ��������������) ��� ����� ������
		private JTextField edCod;
		private JTextField edLevelod;
		private JTextField edName;
		private JTextField edLoc;
		private JTextField edYear;
		@SuppressWarnings("rawtypes")
		private JComboBox cmbLevel;
		//  ������
		private JButton btnOk;
		private JButton btnCancel;
	    //  ����������� ������
		public EdEventDialog(Window parent,Event type,
	                    DBManager manager) {
	      this.manager = manager;
	  // ��������� ����� ��� ������ ���������� ����� ������
		  isNewRow = type == null ? true : false;
		  // ����������� ��������� ��� �������� ������./������.
		  setTitle(isNewRow ? title_add : title_ed);
		  // ����������� ������� ������������� ������  
		  if (!isNewRow) {
		     this.type = type; // ������������ ������
		     // ���������� �������� �������� �����
		     old_key=type.getId_event();
		     }
		  else
		     this.type = new Event();	// ����� ������
		  // �������� ������������ ���������� ����
		  createGui();
		  // ���������� ������������ ��� �������� �������
		  bindListeners();
		  //  ��������� ������
		  loadData();
		  pack();
		  // ������� ������ ������������ �������� ����
		  setResizable(false);
		  setLocationRelativeTo(parent);
		}
		//����� ���������� ������������ �������� �������
		  private void bindListeners() {
		   //  ���������� ������� ������ 
		   setKeyListener(this, new KeyAdapter(){
		    @Override
	        // ��������� ������� ������� ESC � �������� ���� 
		    public void keyPressed(KeyEvent e){
		      if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
				setDialogResult(JDialogResult.Cancel);
				close();
				e.consume();
			}
			else
				super.keyPressed(e);
			}
		  });
	      // ��������� ������� ������ �������� ����  
		  addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e)
			{
				close();
			}
		  });
	      //  ���������  ������ �������
		  btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
			  // ������� Cancel � �������� ����
				setDialogResult(JDialogResult.Cancel);
				close();
			}
		  });
	      //  ���������  ������ �����������
		  btnOk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
			  // �������� ������, ����� 
	  //   ��� ������������ ���������� �����
				if (!constructEvent())
					return;
				if (isNewRow) {
	// ����� ������ ��������� �������� ������ ������
					if (manager.addEventt(type)) {
					// ��� ������ ������� Ok � �������� ����
						setDialogResult(JDialogResult.OK);
						close();
					}
				} else 
	// ����� ������ ��������� �������� ������
					if (manager.updateEventt(type, old_key)) {
					setDialogResult(JDialogResult.OK);
					close();
				}
				}
		  });
		// ������� ������ �������� ������
			cmbLevel.addItemListener(new ItemListener() {
			 @Override
			 public void itemStateChanged(ItemEvent e) {
			 // ��������� �������� ���� �������� �����
			 if (e.getStateChange() == ItemEvent.SELECTED) {
			 if (e.getItem() != null) {
			 Level gr = (Level) e.getItem();
			 edLevelod.setText(gr.getKod().toString());
			 }
			 }
			 }
			});
			// ������� ������ ������ ����� �����
			edLevelod.addFocusListener(new FocusListener() {
			 @Override
			 public void focusLost(FocusEvent e) {
			 // ��� ������ ������ �������� ������� ������
				//�������� �� �������-��������� �������
				 if(!(edLevelod.getText().isEmpty())) {
			 @SuppressWarnings("rawtypes")
			DefaultComboBoxModel model =
			(DefaultComboBoxModel) cmbLevel.getModel();
			 BigDecimal grKod =
			new BigDecimal(edLevelod.getText());
			 setCmbItem(model, grKod);
			 }
			 }
			 @Override
			 public void focusGained(FocusEvent e) {
			 }
			});
			} // ����� bindListeners
		  // ����� �������� ������������ ����������
		@SuppressWarnings("rawtypes")
		private void createGui() {
			// �������� ������
			JPanel pnl = new JPanel(new MigLayout(
	"insets 5", "[][]","[]5[]10[]"));
			// �������� ����� ��� �������������� ������
	edCod = new JTextField(6);
	edLevelod = new JTextField(6);
	cmbLevel = new JComboBox ();
			edLoc = new JTextField(30);
			edName = new JTextField(20);
			edYear = new JTextField(10);
			//  �������� ������
			btnOk = new JButton("���������");
			btnCancel = new JButton("������");
			// ���������� ��������� �� ������
			pnl.add(new JLabel("���"));
			pnl.add(edCod,"span");
			pnl.add(new JLabel("��������"));
			pnl.add(edName,"span");
			pnl.add(new JLabel("�����"));
			pnl.add(edLoc,"span");
			pnl.add(new JLabel("�������"));
			pnl.add(edLevelod, "split 2");
			pnl.add(cmbLevel, "growx, wrap");
			
			pnl.add(new JLabel("���"));
			pnl.add(edYear,"span");
			pnl.add(btnOk, "span, split 2, center, sg ");
			pnl.add(btnCancel, "sg 1");
			//  ���������� ������ � ���� ������
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(pnl, BorderLayout.CENTER);
		}
		// ����� ������������ ����� ����� ����
		protected MaskFormatter createFormatter(String s) {
		    MaskFormatter formatter = null;
		    try {
		        formatter = new MaskFormatter(s);
		    } catch (java.text.ParseException exc) {
		        System.err.println("formatter is bad: " 
	              + exc.getMessage());
		        System.exit(-1);
		    }
		    return formatter;
		}
		//  ����� ���������� ��������� ���������� 
	//� ����������� ����
		private void setKeyListener(Component c, KeyListener kl)
		{
		  c.addKeyListener(kl);
		  if (c instanceof Container)
		    for (Component comp:((Container)c).getComponents())
			 setKeyListener(comp, kl);
		}
	    // ����� ������������� ����� ����� (��� ��������������)
		@SuppressWarnings("unchecked")
		private void loadData() {
		  if (!isNewRow){
				edName.setText(type.getName_event().toString());
				edLoc.setText(type.getLocationn().toString());
				edCod.setText(type.getId_event().toString());
				edLevelod.setText(type.getLevel().getKod().toString());
		
			edYear.setText(type.getYear_event().toString());
			}
		// �������� ������
			// ��������� ������ � ������
		
			ArrayList<Level> lst = manager.loadLevel();
			if (lst != null) {
			 // �������� ������ ������ �� ���� ������
			 @SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model =
			new DefaultComboBoxModel(lst.toArray());
			 // ��������� ������ ��� JComboBox
			 cmbLevel.setModel(model);
			 // ����������� ���� �������� �����
			 BigDecimal grKod = (isNewRow? null :
			type.getLevel().getKod());
			 // ����� ������ ��������� �������� ������
			 // ���������������� �������� �������� �����
			 setCmbItem(model, grKod);
			}
			} 
			//�������� ����� ��������� �������� ���� ������
			// ��������� �������� ������
			private void setCmbItem(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
			 BigDecimal grKod) {
			cmbLevel.setSelectedItem(null);
			if (grKod != null)
			 // �������� ��������� ������ ��� ���������� ��������
			 // � �������� �����
			 for (int i = 0, c = model.getSize(); i < c; i++)
			 if (((Level) model.getElementAt(i)).
			getKod().equals(grKod)) {
			 cmbLevel.setSelectedIndex(i);
			 break;
			 }
			} 
		//������������ �������  ����� �����������
		private boolean constructEvent()	{
		  try {
				type.setId_event(edCod.getText().equals("") ? 
	null : new BigDecimal(edCod.getText()));
				
				
				if(!(edName.getText().isEmpty()))
					type.setName_event(edName.getText());
				if(!(edLoc.getText().isEmpty()))
					type.setLocationn(edLoc.getText());
				Object obj = cmbLevel.getSelectedItem();
				Level gr = (Level) obj;
				 type.setLevel(gr);
				
				type.setYear_event(edYear.getText().equals("") ? 
						null : new BigDecimal(edYear.getText()));
return true;
		  }
		  catch (Exception ex){
			JOptionPane.showMessageDialog(this, 
	            ex.getMessage(), "������ ������",
			   JOptionPane.ERROR_MESSAGE);
			 return false;
		  }
		}
		// ������� ������� ������ (������ ���������)
		public Event getEvent()
		{
			return type;
		}
}

