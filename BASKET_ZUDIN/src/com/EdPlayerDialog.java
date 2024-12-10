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

public class EdPlayerDialog extends JRDialog {

	private static final long serialVersionUID = 1L;
	// ���� ������
	  private DBManager manager;
	  private BigDecimal old_key; // ������� �������� �����
	  // ��� �������� ��������� ����
	  private final static String title_add = 
	    "���������� ������ ������";
	  private final static String title_ed = 
	     "�������������� ������";
	   //  ��������� ���������� ��� ������ ������ .
		private Player type = null;
	    // ���� ������ ����������� ����� ������
		private boolean isNewRow = false;
	    // �������� ��� ����� ���
		SimpleDateFormat frmt = 
	          new SimpleDateFormat("dd-MM-yyyy");
	    // �������� (���� ��������������) ��� ����� ������
		private JTextField edNum;
		private JTextField edKod_Team;
		private JTextField edWeie;
		private JTextField edHeie;
		private JTextField edName;
		private JTextField edDater;
		private JTextField edRole;
		private JTextField edGeed;
		@SuppressWarnings("rawtypes")
		private JComboBox cmbteaar;
		//  ������
		private JButton btnOk;
		private JButton btnCancel;
	    //  ����������� ������
		public EdPlayerDialog(Window parent,Player type,
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
		     old_key=type.getId();
		     }
		  else
		     this.type = new Player();	// ����� ������
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
				if (!constructPlayer())
					return;
				if (isNewRow) {
	// ����� ������ ��������� �������� ����� ������ ����.
					if (manager.addPlayer(type)) {
					// ��� ������ ������� Ok � �������� ����
						setDialogResult(JDialogResult.OK);
						close();
					}
				} else 
	// ����� ������ ��������� �������� ������
					if (manager.updatePlayer(type, old_key)) {
					setDialogResult(JDialogResult.OK);
					close();
				}
				}
		  });
			// ������� ������ �������� ������
			cmbteaar.addItemListener(new ItemListener() {
			 @Override
			 public void itemStateChanged(ItemEvent e) {
			 // ��������� �������� ���� �������� �����
			 if (e.getStateChange() == ItemEvent.SELECTED) {
			 if (e.getItem() != null) {
			 Team gr = (Team) e.getItem();
			 edKod_Team.setText(gr.getId_team().toString());
			 }
			 }
			 }
			});
			// ������� ������ ������ ����� �����
			edKod_Team.addFocusListener(new FocusListener() {
			 @Override
			 public void focusLost(FocusEvent e) {
			 // ��� ������ ������ �������� ������� ������
				//�������� �� �������-��������� �������
				 if(!(edKod_Team.getText().isEmpty())) {
			 @SuppressWarnings("rawtypes")
			DefaultComboBoxModel model =
			(DefaultComboBoxModel) cmbteaar.getModel();
			 BigDecimal grKod =
			new BigDecimal(edKod_Team.getText());
			 setCmbItem(model, grKod);
			 }
			 }
			 @Override
			 public void focusGained(FocusEvent e) {
			 }
			});
		}
		  // ����� �������� ������������ ����������
		private void createGui() {
			// �������� ������
			JPanel pnl = new JPanel(new MigLayout(
	"insets 5", "[][]","[]5[]10[]"));
			// �������� ����� ��� �������������� ������
	edNum = new JTextField(10);
	edKod_Team = new JTextField(10);
			edWeie = new JTextField(20);
			edHeie = new JTextField(20);
			edRole = new JTextField(20);
			cmbteaar = new JComboBox <> ();
			edName = new JTextField(40);
			edDater = new JFormattedTextField(
	                createFormatter("##-##-####"));
			edDater.setColumns(10);
			edGeed = new JTextField(20);
			//  �������� ������
			btnOk = new JButton("���������");
			btnCancel = new JButton("������");
			// ���������� ��������� �� ������
			pnl.add(new JLabel("�����"));
			pnl.add(edName,"span");
			pnl.add(new JLabel("���� ��������"));
			pnl.add(edDater,"span");
			pnl.add(new JLabel("�������"));
			pnl.add(edKod_Team, "split 2");
			pnl.add(cmbteaar, "growx, wrap");
			pnl.add(new JLabel("����"));
			pnl.add(edHeie,"span");
			pnl.add(new JLabel("���"));
			pnl.add(edWeie,"span");
		
			pnl.add(new JLabel("����"));
			pnl.add(edRole,"span");
			pnl.add(new JLabel("�����"));
			pnl.add(edNum,"span");
			pnl.add(new JLabel("���"));
			pnl.add(edGeed,"span");
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
				edHeie.setText(type.getHeight().toString());
				edNum.setText(type.getNum().toString());
				edName.setText(type.getName().toString());
				edKod_Team.setText(type.getTeam().getId_team().toString());
			edWeie.setText(type.getWeight().toString());
			edGeed.setText(type.getGender());
			edDater.setText(type.getBirthday()==null? 
					   "":frmt.format(type.getBirthday()));

			edRole.setText(type.getRole().toString());
			}
		// �������� ������
					// ��������� ������ � ������
				
					ArrayList<Team> lst = manager.loadTeam();
					if (lst != null) {
					 // �������� ������ ������ �� ���� ������
					 @SuppressWarnings({ "rawtypes" })
					DefaultComboBoxModel model =
					new DefaultComboBoxModel(lst.toArray());
					 // ��������� ������ ��� JComboBox
					 cmbteaar.setModel(model);
					 // ����������� ���� �������� �����
					 BigDecimal grKod = (isNewRow? null :
					type.getTeam().getId_team());
					 // ����� ������ ��������� �������� ������
					 // ���������������� �������� �������� �����
					 setCmbItem(model, grKod);
					}
					} 
					//�������� ����� ��������� �������� ���� ������
					// ��������� �������� ������
					private void setCmbItem(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
					 BigDecimal grKod) {
					cmbteaar.setSelectedItem(null);
					if (grKod != null)
					 // �������� ��������� ������ ��� ���������� ��������
					 // � �������� �����
					 for (int i = 0, c = model.getSize(); i < c; i++)
					 if (((Team) model.getElementAt(i)).
					getId_team().equals(grKod)) {
					 cmbteaar.setSelectedIndex(i);
					 break;
					 }
					} 
		//������������ ������� � �. ����� �����������
		private boolean constructPlayer()	{
		  try {
				type.setNum(edNum.getText().equals("") ? 
	null : new BigDecimal(edNum.getText()));
				
				type.setHeight(edHeie.getText().equals("") ? 
						null : new BigDecimal(edHeie.getText()));
				type.setWeight(edWeie.getText().equals("") ? 
						null : new BigDecimal(edWeie.getText()));
				if (edGeed.getText().toString()!="") 
					type.setGender(edGeed.getText().toString());	
				if (edName.getText().toString()!="") 
					type.setName(edName.getText().toString());	
				type.setBirthday(edDater.getText().substring(0,
					      1).trim().equals("") ? null : 
					      frmt.parse(edDater.getText()));
				type.setRole(edRole.getText());
				Object obj = cmbteaar.getSelectedItem();
				 Team gr = (Team) obj;
				 type.setTeam(gr);
return true;
		  }
		  catch (Exception ex){
			JOptionPane.showMessageDialog(this, 
	            ex.getMessage(), "������ ������",
			   JOptionPane.ERROR_MESSAGE);
			 return false;
		  }
		}
		// ������� ������� ������ ����.
		public Player getPlayer()
		{
			return type;
		}
}
