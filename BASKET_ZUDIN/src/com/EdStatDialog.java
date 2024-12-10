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

public class EdStatDialog extends JRDialog {

	private static final long serialVersionUID = 1L;
	// ���� ������
	  private DBManager manager;
	  private BigDecimal fk_key;
	  private BigDecimal old_key; // ������� �������� �����
	  // ��� �������� ��������� ����
	  private final static String title_add = 
	    "���������� ����� ����������";
	  private final static String title_ed = 
	     "�������������� ����������";
	   //  ��������� ���������� ��� ������ ����� �����
		private Stat type = null;
	    // ���� ������ ����������� ����� ������
		private boolean isNewRow = false;
	    // �������� ��� ����� ���
		SimpleDateFormat frmt = 
	          new SimpleDateFormat("dd-MM-yyyy");
	    // �������� (���� ��������������) ��� ����� ������
		private JTextField edpointscored;
		
		
		private JTextField edAssistsoim;
		private JTextField edSteal;
		private JTextField ed2, ed3;
		private JTextField edDr, edOr,  edFree;
		private JTextField edTurnover, edBlockedshot, edFoull;
	
		@SuppressWarnings("rawtypes")
		private JComboBox cmbPerso;
		private JTextField edCod_perso;
		//  ������
		private JButton btnOk;
		private JButton btnCancel;
		
		 
		
			
			
	    //  ����������� ������
		public EdStatDialog(Window parent,Stat type,
	                    DBManager manager, BigDecimal fk_key) {
	      this.manager = manager;
	  // ��������� ����� ��� ������ ���������� ����� ������
		  isNewRow = type == null ? true : false;
		  // ����������� ��������� ��� �������� ������./������.
		  setTitle(isNewRow ? title_add : title_ed);
		     this.fk_key=fk_key;
		  // ����������� ������� ������������� ������  
		  if (!isNewRow) {
		     this.type = type; // ������������ ������
		     // ���������� �������� �������� �����
		     old_key=type.getIdPlayerStats();
		
		
		     }
		  else {
		     this.type = new Stat();	// ����� ������
		    
		  }
		  // �������� ������������ ���������� ����
		  createGui();
		  // ���������� ������������ ��� �������� �������
		  bindListeners();
		  //  ��������� ������
		  loadData();
		  setSize(900,500);
		  pack();
		  // ������� ������ ������������ �������� ����
		  setResizable(false);
		  
		  setLocationRelativeTo(parent);
		}
		// ����� ��������� ������ �����
		
	
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
		  btnOk.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
				  // �������� ������, ����� 
		  //   ��� ������������ ���������� �����
					if (!constructStat())
						return;
					if (isNewRow) {
		// ����� ������ ��������� �������� ����� st
						if (manager.addStat(type)) {
						// ��� ������ ������� Ok � �������� ����
							setDialogResult(JDialogResult.OK);
							close();
						}
					} else 
		// ����� ������ ��������� �������� ������
						if (manager.updateStat(type, old_key)) {
						setDialogResult(JDialogResult.OK);
						close();
					}
					}
			  });
		  
		  //  ���������  ������ �����������
		 
		 
			
				// ������� ������ �������� ������
				  cmbPerso.addItemListener(new ItemListener() {
				  @Override
				  public void itemStateChanged(ItemEvent e) {
				  // ��������� �������� ���� �������� �����
				  if (e.getStateChange() == ItemEvent.SELECTED) {
				  if (e.getItem() != null) {
				  Player c4 = (Player) e.getItem();
				  edCod_perso.setText(c4.getId().toString());
				  }
				  }
				  }
				  });
				  // ������� ������ ������ ����� �����
				  edCod_perso.addFocusListener(new FocusListener() {
				  @Override
				  public void focusLost(FocusEvent e) {
				  // ��� ������ ������ �������� ������� ������
						//�������� �� �������-��������� �������
						 if(!(edCod_perso.getText().isEmpty())) {
				  @SuppressWarnings("rawtypes")
				DefaultComboBoxModel model5 =
				  (DefaultComboBoxModel) cmbPerso.getModel();
				  BigDecimal grKod =
				  new BigDecimal(edCod_perso.getText());
				  setCmbItem6(model5, grKod);
				  }
				  }

				@Override
				public void focusGained(FocusEvent e) {
			
					
				}
				  });
				
				} // ����� 
		
		  // ����� �������� ������������ ����������
		@SuppressWarnings("rawtypes")
		private void createGui() {
			// �������� ������
			JPanel pnl = new JPanel(new MigLayout(
	"insets 5", "[][]","[]5[]10[]"));
			// �������� ����� ��� �������������� ������
	edpointscored = new JTextField(10);
	
	
	cmbPerso = new JComboBox();
	
			edCod_perso = new JTextField(10);
			edBlockedshot = new JTextField(20);
			
			edFoull = new JTextField(20);
			ed2 = new JTextField(20);
			ed3 = new JTextField(20);
			ed2 = new JTextField(20);
			ed3 = new JTextField(20);
			edAssistsoim = new JTextField(20);
			edTurnover = new JTextField(20);
			edSteal = new JTextField(20);
			edDr = new JTextField(20);
			edOr = new JTextField(20);
			edFree = new JTextField(20);
			
			//  �������� ������
			btnOk = new JButton("���������");
			btnCancel = new JButton("������");
			// ���������� ��������� �� ������
			//pnl.add(new JLabel("�� ����� �����"));
		//	pnl.add(edCod,"span");
			
			pnl.add(new JLabel("�����"));
			pnl.add(edCod_perso, "split 2");
			pnl.add(cmbPerso, "growx, wrap");
			pnl.add(new JLabel("�����"));
			pnl.add(edpointscored,"span");
			pnl.add(new JLabel("���-�� ��������"));
			pnl.add(edAssistsoim,"span");
			pnl.add(new JLabel("���-�� ����������"));
			pnl.add(edSteal,"span");

			pnl.add(new JLabel("���-�� ������ ����"));
			pnl.add(edTurnover,"span");
			pnl.add(new JLabel("���-�� ����-�����"));
			pnl.add(edBlockedshot, "span");
			
			pnl.add(new JLabel("���-�� �����"));
		
			pnl.add(edFoull, "span");
			
			
			pnl.add(new JLabel("���-�� 2-� �������"));
			pnl.add(ed2,"span");

			pnl.add(new JLabel("���-�� 3-� �������"));
			pnl.add(ed3,"span");

			pnl.add(new JLabel("���-�� ��������"));
			pnl.add(edFree,"span");
			pnl.add(new JLabel("���-�� �������� �� ����� ����"));
			pnl.add(edDr,"span");
			pnl.add(new JLabel("���-�� �������� �� ����� ����"));
			pnl.add(edOr,"span");
		
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
				edAssistsoim.setText(type.getAssists().toString());
				ed2.setText(type.getDoubleDouble().toString());
				ed3.setText(type.getTriple().toString());
				edpointscored.setText(type.getPointScored().toString());
		//		edId_zayav.setText(type.getId_zayav().toString());
			edCod_perso.setText(type.getPlayer().getId().toString());
			edBlockedshot.setText(type.getBlockedShot().toString());
			edFoull.setText(type.getFoul().toString());
			edSteal.setText(type.getSteal().toString());
			edTurnover.setText(type.getTurnover().toString());
			edFree.setText(type.getFreeThrow().toString());
			edDr.setText(type.getDr().toString());
			edOr.setText(type.getOr().toString());
			}
		  else 
		  {
			
			 
		  }
		  ArrayList<Player> lst6 = manager.loadPlayerForCmb();
			if (lst6 != null) {
			// �������� ������ ������ �� ���� ������
			@SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model5 =
			new DefaultComboBoxModel(lst6.toArray());
			// ��������� ������ ��� JComboBox
			cmbPerso.setModel(model5);
			BigDecimal grKod;
			// ����������� ���� �������� �����
			 //����� ������ ����������� ���� �������� ����� 
			 grKod = (isNewRow? null :
				 type.getPlayer()==null? null :
					 type.getPlayer().getId());
			// ����� ������ ��������� �������� ������
			// ���������������� �������� �������� �����
			setCmbItem6(model5, grKod);
			}
		// �������� ������
			// ��������� ������ � ������
		
			
			} 
			
			// ��������� �������� ������
			private void setCmbItem6(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
			BigDecimal grKod) {
			cmbPerso.setSelectedItem(null);
			if (grKod != null)
			// �������� ��������� ������ ��� ���������� ��������
			// � �������� �����
			for (int i = 0, c = model.getSize(); i < c; i++)
			if (((Player) model.getElementAt(i)).
			getId().equals(grKod)) {
				cmbPerso.setSelectedIndex(i);
				break;
				}
			}
			
		//������������ ������� ����� ����� ����� �����������
		private boolean constructStat()	{
		  try {
			//System.out.print(fk_key);
				type.setIdMatch(fk_key);
				type.setAssists(edAssistsoim.getText().equals("") ? 
						null : Integer.parseInt(edAssistsoim.getText()));
				type.setPointScored(edpointscored.getText().equals("") ? 
						null : Integer.parseInt(edpointscored.getText()));
				type.setFoul(edFoull.getText().equals("") ? 
						null : Integer.parseInt(edFoull.getText()));
				type.setBlockedShot(edBlockedshot.getText().equals("") ? 
						null : Integer.parseInt(edBlockedshot.getText()));
			type.setTurnover(edTurnover.getText().equals("") ? 
						null : Integer.parseInt(edTurnover.getText()));
				type.setSteal(edSteal.getText().equals("") ? 
						null : Integer.parseInt(edSteal.getText()));
				type.setDoubleDouble(ed2.getText().equals("") ? 
						null : Integer.parseInt(ed2.getText()));
				type.setTriple(ed3.getText().equals("") ? 
						null : Integer.parseInt(ed3.getText()));
				type.setFreeThrow(edFree.getText().equals("") ? 
						null : Integer.parseInt(edFree.getText()));
				type.setDr(edDr.getText().equals("") ? 
						null : Integer.parseInt(edDr.getText()));
				type.setOr(edOr.getText().equals("") ? 
						null : Integer.parseInt(edOr.getText()));
					Object obj2 = cmbPerso.getSelectedItem();
					Player gr2 = (Player) obj2;
					type.setPlayer(gr2);
					
return true;
		  }
		  catch (Exception ex){
			JOptionPane.showMessageDialog(this, 
	            ex.getMessage(), "������ ������",
			   JOptionPane.ERROR_MESSAGE);
			 return false;
		  }
		}
		// ������� ������� ����� �����
		public Stat getStat()
		{
			return type;
		}
	
		 
		
		
		
			
		
		}

