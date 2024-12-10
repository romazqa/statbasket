package com;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import javax.swing.table.TableCellRenderer;

import com.data.*;
import com.data.Event;

import net.miginfocom.swing.MigLayout;

public class EdMatchesDialog extends JRDialog {

	private static final long serialVersionUID = 1L;
	private String userLogin;
	// ���� ������
	  private DBManager manager;
	  private BigDecimal old_key; // ������� �������� �����
	  // ��� �������� ��������� ����
	  private final static String title_add = 
	    "���������� ������ �����";
	  private final static String title_ed = 
	     "�������������� �����";
	   //  ��������� ���������� ��� ������ ����
		private Matches type = null;
	    // ���� ������ ����������� ����� ������
		private boolean isNewRow = false;
	    // �������� ��� ����� ���
		SimpleDateFormat frmt = 
	          new SimpleDateFormat("dd-MM-yyyy");
	    // �������� (���� ��������������) ��� ����� ������
		private JTextField edCod_event;
		private JTextField edt1;
		private JTextField edt2;
		private JTextField edPlr;
		private JTextField edDate_zaver;
		private JTextField edkteam1, edkteam2;
		@SuppressWarnings("rawtypes")
		private JComboBox cmbEver, cmbTeam1, cmbTeam2;
		
		//  ������
		private JButton btnOk;
		private JButton btnCancel;
		//��� ����. �������
		 private JTable tblStat;
		 
			private JButton btnClose;
			// ������ �������������� 
				private JButton btnEdit;
				// ������ ���������� 
				private JButton btnNew;
				// ������ �������� 
				private JButton btnDelete;
				//���� ������
			private TKTableModel tblModel;
			 private ArrayList<Stat> kls;
			 
	    //  ����������� ������
		public EdMatchesDialog(Window parent,Matches type,
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
		     old_key=type.getId_matches();
		     loadData2(); //�������� � ������� 2
		     }
		  else {
		     this.type = new Matches();	// ����� ������
		     kls=new ArrayList <Stat>();
		  }
		  // �������� ������������ ���������� ����
		  createGui();
		  // ���������� ������������ ��� �������� �������
		  bindListeners();
		  //  ��������� ������
		  loadData();
		  setSize(1200,600);
		  // ������� ������ ������������ �������� ����
		  setResizable(true);
		  setButton(); // ��������� ����������� ������ �����
		  setLocationRelativeTo(parent);
		}
		// ����� ��������� ������ �����
		private void setButton() {
		if (btnOk.getText().equals("���������")) {
		btnCancel.setText("������");
		btnNew.setEnabled(false);
		btnEdit.setEnabled(false);
		btnDelete.setEnabled(false);
		} else {
		btnCancel.setText("�����");
		btnNew.setEnabled(true);
		btnEdit.setEnabled(true);
		btnDelete.setEnabled(true);
		}
		}
		 // ��������� ������ � ������� 2
		private void loadData2() {
		 // ��������� ������ ����� ��������
		 kls=manager.loadStat(type.getId_matches());

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
		  btnOk.addActionListener(new ActionListener() {
			  @Override
			  public void actionPerformed(ActionEvent e) {
			  if (((AbstractButton) e.getSource()).getText().equals("���������")) {
			  // �������� ������, ������� Ok � �������� ����
			  if (!constructMatches()) return;
			  //����� ������ ���������� 313
			  else {if (isNewRow)
			  			{
				  if (manager.addMatches(type) == true)
				  			{
				  					setDialogResult(JDialogResult.OK);
				  					((AbstractButton) e.getSource()).setText("�������������");
				  					setButton();
				  			}
			  			}
			  else 
				  if (manager.updateMatches(type, old_key)==true)
				  {
					  setDialogResult(JDialogResult.OK);
				  ((AbstractButton) e.getSource()).setText("�������������");
				  setButton();
				  }	} 
			  }
			  else {
			  ((AbstractButton) e.getSource()).setText("���������");
			  setButton();
			  }
			  }
			  });
		  // ��� ������ �������
			 btnClose.addActionListener(new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
			 // ��������� ����
			 dispose();
			  }
			  });
			//  ��� ������ ����������
							btnNew.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									addStat();
								}
							});
							//  ��� ������ ��������������
							btnEdit.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									editStat();
								}
							});
							//  ��� ������ ��������
							btnDelete.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
								deleteStat();
								}
							});
							// ������� ������ �������� ������
							  cmbEver.addItemListener(new ItemListener() {
							  @Override
							  public void itemStateChanged(ItemEvent e) {
							  // ��������� �������� ���� �������� �����
							  if (e.getStateChange() == ItemEvent.SELECTED) {
							  if (e.getItem() != null) {
							  Event c4 = (Event) e.getItem();
							  edCod_event.setText(c4.getId_event().toString());
							  }
							  }
							  }
							  });
							  // ������� ������ ������ ����� �����
							  edCod_event.addFocusListener(new FocusListener() {
							  @Override
							  public void focusLost(FocusEvent e) {
							  // ��� ������ ������ �������� ������� ������
									//�������� �� �������-��������� �������
									 if(!(edCod_event.getText().isEmpty())) {
							  @SuppressWarnings("rawtypes")
							DefaultComboBoxModel model5 =
							  (DefaultComboBoxModel) cmbEver.getModel();
							  BigDecimal grKod =
							  new BigDecimal(edCod_event.getText());
							  setCmbItem6(model5, grKod);
							  }
							  }
							  
							  
							  @Override
							  public void focusGained(FocusEvent e) {
							  }
							  });
							//������� ������ �������� ������
								cmbTeam1.addItemListener(new ItemListener() {
									 @Override
									 public void itemStateChanged(ItemEvent e) {
									 // ��������� �������� ���� �������� �����
									 if (e.getStateChange() == ItemEvent.SELECTED) {
									 if (e.getItem() != null) {
									 Team gr = (Team) e.getItem();
									 edkteam1.setText(gr.getId_team().toString());
									 }
									 }
									 }
									});
									// ������� ������ ������ ����� �����
								edkteam1.addFocusListener(new FocusListener() {
									 @Override
									 public void focusLost(FocusEvent e) {
									 // ��� ������ ������ �������� ������� ������
										//�������� �� �������-��������� �������
										 if(!(edkteam1.getText().isEmpty())) {
									 @SuppressWarnings("rawtypes")
									DefaultComboBoxModel model1 =
									(DefaultComboBoxModel) cmbTeam1.getModel();
									 BigDecimal grKod =
									new BigDecimal(edkteam1.getText());
									 setCmbItem1(model1, grKod);
									 }
									 }
									 @Override
									 public void focusGained(FocusEvent e) {
									 }
									});
								//������� ������ �������� ������
								cmbTeam2.addItemListener(new ItemListener() {
									 @Override
									 public void itemStateChanged(ItemEvent e) {
									 // ��������� �������� ���� �������� �����
									 if (e.getStateChange() == ItemEvent.SELECTED) {
									 if (e.getItem() != null) {
									 Team gr = (Team) e.getItem();
									 edkteam2.setText(gr.getId_team().toString());
									 }
									 }
									 }
									});
									// ������� ������ ������ ����� �����
								edkteam2.addFocusListener(new FocusListener() {
									 @Override
									 public void focusLost(FocusEvent e) {
									 // ��� ������ ������ �������� ������� ������
										//�������� �� �������-��������� �������
										 if(!(edkteam2.getText().isEmpty())) {
									 @SuppressWarnings("rawtypes")
									DefaultComboBoxModel model2 =
									(DefaultComboBoxModel) cmbTeam2.getModel();
									 BigDecimal grKod2 =
									new BigDecimal(edkteam1.getText());
									 setCmbItem2(model2, grKod2);
									 }
									 }
									 @Override
									 public void focusGained(FocusEvent e) {
									 }
									});
							
			 }
			 
		  // ����� �������� ������������ ����������
		@SuppressWarnings("rawtypes")
		private void createGui() {
			// �������� ������
			JPanel pnl = new JPanel(new MigLayout(
	"insets 5", "[][]","[]5[]10[]"));
			// �������� ����� ��� �������������� ������
	edCod_event = new JTextField(10);
	edt1 = new JTextField(7);
	cmbEver = new JComboBox();
	cmbTeam1 = new JComboBox();
	cmbTeam2 = new JComboBox();
			edt2 = new JTextField(7);
			edPlr = new JTextField(50);
			edkteam1 = new JTextField(10);
			edkteam2 = new JTextField(10);
			edDate_zaver = new JFormattedTextField(
	                createFormatter("##-##-####"));
			edDate_zaver.setColumns(10);
			//  �������� ������
			btnOk = new JButton("���������");
			btnCancel = new JButton("������");
			// ���������� ��������� �� ������
			pnl.add(new JLabel("�����������"));
			pnl.add(edCod_event, "span, split 2,  left, sg 2");
			pnl.add(cmbEver, "wrap");
			pnl.add(new JLabel("���� ����������"));
			pnl.add(edDate_zaver,"span");
			pnl.add(new JLabel("����"));
			pnl.add(edt1,"span, split 3");
			pnl.add(new JLabel(":"));
			pnl.add(edt2);
			pnl.add(new JLabel("���� ����������"));
			pnl.add(edPlr,"span");
			pnl.add(new JLabel("������� 1"));
			pnl.add(edkteam1,"split 2");
			pnl.add(cmbTeam1, "growx, wrap");
			pnl.add(new JLabel("������� 2"));
			pnl.add(edkteam2,"split 2");
			pnl.add(cmbTeam2, "growx, wrap");
			
			
		
			
			
			pnl.add(btnOk, "span, split 2, center, sg ");
			pnl.add(btnCancel, "sg 1");
			//  ���������� ������ � ���� ������
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(pnl, BorderLayout.NORTH);
			 JPanel pnfl = new JPanel(new MigLayout("insets 3, gapy 4",
						//	JPanel pnl = new JPanel(new MigLayout("insets 3, gap 90! 90!",
						 "[grow, fill]", "[]5[grow, fill]10[]"));
						 // �������� ������� �������
						 tblStat = new JTable();
						 // �������� ������� ��������� ������ �� ����
						 // ��������������� ������
						 tblStat.setModel(tblModel = new TKTableModel(kls));
						 // �������� ������� ���������� ��� ��������� ������
						 RowSorter<TKTableModel> sorter = new
						 TableRowSorter<TKTableModel>(tblModel);
						 // ���������� ������� ���������� �������
						 tblStat.setRowSorter(sorter);
						 // ������ ��������� �������� ���� �������
						 // ��������� ������� ���� ������� ������
						 tblStat.setRowSelectionAllowed(true);
						 // ������ ���������� ����� ��������
						 tblStat.setIntercellSpacing(new Dimension(0, 1)); 
						 // ������ ���� �����
						 tblStat.setGridColor(new Color(170, 170, 255).darker());
						 // �������������� ����������� ������ ��������� �������
						 tblStat.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
						 // ����������� ��������� ������ 1 ������
						 tblStat.getSelectionModel().setSelectionMode(
						 ListSelectionModel.SINGLE_SELECTION);
						 // �������� ������� ��������� � ������� � ��� �������
						 JScrollPane scrlPane = new JScrollPane(tblStat);
						 scrlPane.getViewport().setBackground(Color.white);
						 scrlPane.setBorder(BorderFactory.createCompoundBorder
						(new EmptyBorder(3,0,3,0),scrlPane.getBorder()));
						// tblStat.getColumnModel().getColumn(0).setMaxWidth(100);
						 //tblStat.getColumnModel().getColumn(0).setMinWidth(100);
						 // �������� ������ ��� �������� �����
						 btnClose = new JButton("�������");
						 pnfl.add(getToolBar(),"growx,wrap");//��������  ������
						 //���������� �� ������: �����, ������� � �������� � ������
						 pnfl.add(scrlPane, "grow, span");
						 pnfl.add(btnClose, "growx 0, right");
						
						 TableCellRenderer centerRenderer = (TableCellRenderer) new CenterAlignedTableCellRenderer();  // !!!  ���������� ����
						    for (int i = 0; i < tblStat.getColumnModel().getColumnCount(); i++) {
						        tblStat.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
						    }
						 
						 	getContentPane().add(pnfl, BorderLayout.CENTER);
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
				edPlr.setText(type.getPlayground().toString());
		//		edCod_sotrud.setText(type.getId_Matches().toString());
				edt1.setText(type.getTeam1score().toString());
			edt2.setText(type.getTeam2score().toString());
			edDate_zaver.setText(type.getDate_matches()==null? 
					   "":frmt.format(type.getDate_matches()));
			if (type.getTeam1()!=null)
			edkteam1.setText(type.getTeam1().getId_team().toString());
			if (type.getTeam2()!=null)
			edkteam2.setText(type.getTeam2().getId_team().toString());
			}
		  ArrayList<Event> lst6 = manager.loadEventtForCmb();
			if (lst6 != null) {
			// �������� ������ ������ �� ���� ������
			@SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model5 =
			new DefaultComboBoxModel(lst6.toArray());
			// ��������� ������ ��� JComboBox
			cmbEver.setModel(model5);
			BigDecimal grKod2;
			// ����������� ���� �������� �����
			 //����� ������ ����������� ���
			 grKod2 = (isNewRow? null :
				 type.getEvent()==null? null :
					 type.getEvent().getId_event());
			// ����� ������ ��������� �������� ������
			// ���������������� �������� �������� �����
			setCmbItem6(model5, grKod2);
			}
			
			ArrayList<Team> lst1 = manager.loadTeamForCmb();
			if (lst1 != null) {
			 // �������� ������ ������ �� ���� ������
			 @SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model1 =
			new DefaultComboBoxModel(lst1.toArray());
			 // ��������� ������ ��� JComboBox
			 cmbTeam1.setModel(model1);
			 BigDecimal grKod1;
			 // ����������� ���� �������� �����
			 grKod1 = (isNewRow? null :
				 type.getTeam2()==null? null :
					 type.getTeam2().getId_team());
			 
			 
			 // ����� ������ ��������� �������� ������
			 // ���������������� �������� �������� �����
			 setCmbItem1(model1, grKod1);
			}
			ArrayList<Team> lst2 = manager.loadTeamForCmb();
			if (lst2 != null) {
			 // �������� ������ ������ �� ���� ������
			 @SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model2 =
			new DefaultComboBoxModel(lst2.toArray());
			 // ��������� ������ ��� JComboBox
			 cmbTeam2.setModel(model2);
			 // ����������� ���� �������� �����
			 BigDecimal grKod = (isNewRow? null :
			type.getTeam1().getId_team());
			 // ����� ������ ��������� �������� ������
			 // ���������������� �������� �������� �����
			 setCmbItem2(model2, grKod);
			}
		}
		// ��������� �������� ������
		private void setCmbItem6(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
		BigDecimal grKod) {
		cmbEver.setSelectedItem(null);
		if (grKod != null)
		// �������� ��������� ������ ��� ���������� ��������
		// � �������� �����
		for (int i = 0, c = model.getSize(); i < c; i++)
		if (((Event) model.getElementAt(i)).
		getId_event().equals(grKod)) {
			cmbEver.setSelectedIndex(i);
			break;
			}
		}
		private void setCmbItem1(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
				 BigDecimal grKod) {
				cmbTeam1.setSelectedItem(null);
				if (grKod != null)
				 // �������� ��������� ������ ��� ���������� ��������
				 // � �������� �����
				 for (int i = 0, c = model.getSize(); i < c; i++)
				 if (((Team) model.getElementAt(i)).
				getId_team().equals(grKod)) {
				 cmbTeam1.setSelectedIndex(i);
				 break;
				 }
				} 
		private void setCmbItem2(@SuppressWarnings("rawtypes") DefaultComboBoxModel model1,
				 BigDecimal grKod) {
				cmbTeam2.setSelectedItem(null);
				if (grKod != null)
				 // �������� ��������� ������ ��� ���������� ��������
				 // � �������� �����
				 for (int i = 0, c = model1.getSize(); i < c; i++)
				 if (((Team) model1.getElementAt(i)).
				getId_team().equals(grKod)) {
				 cmbTeam2.setSelectedIndex(i);
				 break;
				 }
				} 
		
		
		//������������ ������� m ����� �����������
		private boolean constructMatches()	{
		  try {
		//		type.setId_Matches(edCod_sotrud.getText().equals("") ? 
//	null : new BigDecimal(edCod_sotrud.getText()));
				type.setTeam1score(edt1.getText().equals("") ? 
						null : new BigDecimal(edt1.getText()));
				type.setTeam2score(edt2.getText().equals("") ? 
						null : new BigDecimal(edt2.getText()));
				if(!(edPlr.getText().isEmpty()))
					type.setPlayground(edPlr.getText());
			
				type.setDate_matches(edDate_zaver.getText().substring(0,
					      1).trim().equals("") ? null : 
					      frmt.parse(edDate_zaver.getText()));
				Object obj = cmbEver.getSelectedItem();
				Event gr = (Event) obj;
				type.setEvent(gr);
				
				 Object obj2 = cmbTeam1.getSelectedItem();
				 Team gr2 = (Team) obj2;
				 type.setTeam1(gr2);
				 Object obj3 = cmbTeam2.getSelectedItem();
				 Team gr3 = (Team) obj3;
				 type.setTeam2(gr3);
return true;
		  }
		  catch (Exception ex){
			JOptionPane.showMessageDialog(this, 
	            ex.getMessage(), "������ ������",
			   JOptionPane.ERROR_MESSAGE);
			 return false;
		  }
		}
		// ������� ������� ������
		public Matches getMatches()
		{
			return type;
		}
		 private JToolBar getToolBar() {
		     // �������� ������
			JToolBar res = new JToolBar();
			// ������������ ��������� ������
			res.setFloatable(false);
			// ���������� ������ ����������
			// ����������� �������������� ����������� ��� ������
			URL url = FrmTeam.class.getResource("/images/add.png");
			// �������� ������ � ������������
			btnNew = new JButton(new ImageIcon(url));
			// �� ������ �� ��������������� �����
			btnNew.setFocusable(false);
			//  ���������� ����������� ��������� ��� ������
			btnNew.setToolTipText("�������� ����� ����������");
			// ���������� ������ ���������
			url = FrmTeam.class.getResource("/images/delete.png");
			btnDelete = new JButton(new ImageIcon(url));
			btnDelete.setFocusable(false);
			btnDelete.setToolTipText("������� ������");
			// ���������� ������ ���������������
			url = FrmTeam.class.getResource("/images/edit.png");
			btnEdit = new JButton(new ImageIcon(url));
			btnEdit.setFocusable(false);
			btnEdit.setToolTipText("�������� ������ ������");
			//  ���������� ������ �� ������
			res.add(btnNew);
			res.add(btnEdit);
			res.add(btnDelete);
			// ������� ������ � �������� ����������
			return res;
		   }
		 
		//�������������� ������� ������
			private void editStat() {			
				int index = tblStat.getSelectedRow();
				if (index == -1)
					return;
				// �������������� ������� ������� � ������ ������
				int modelRow = tblStat.convertRowIndexToModel(index);
				// �������� ������ �� ������ �� �������
				Stat prod = kls.get(modelRow);
				
				
				// �������� ������� ���� ��������������
				EdStatDialog dlg = new EdStatDialog(this,
		    prod,manager, type.getId_matches());
				// ����� ���� � �������� ���� ��������
				if (dlg.showDialog() == JDialogResult.OK) {
		               // ����� ������ ���������� ������ ������ ��������� ������
					tblModel.updateRow(modelRow);
					System.out.println("���������� OK");
					}
				
		
			}
			// �������� ����� ������
				private void addStat() {

					// �������� ������� ���� ��������������
					EdStatDialog dlg = new EdStatDialog(this, 
						null,manager,type.getId_matches());
					dlg.showDialog();
					Stat prod = dlg.getStat();
					if (prod!=null&&prod.getIdPlayerStats()!=null) {
						// ���������� ��� � ��������� ������
						tblModel.addRow(prod);
					}
			
				}
				// �������� ������� ������
				private void deleteStat() {
					// ���������� ������ ������� ������.
					int index = tblStat.getSelectedRow();
					// ���� ��� ���������� ������, �� �����
					if (index == -1)
						return;
					// ����� ������� �� ��������. ��� ������ - �����
					if (JOptionPane.showConfirmDialog(this, 
			  "������� ����������?", "�������������", 
			  JOptionPane.YES_NO_OPTION,
					  JOptionPane.QUESTION_MESSAGE) != 
					  JOptionPane.YES_OPTION)
					  return;
					// �������������� ������� ������������� � ������ ������
					int modelRow = tblStat.convertRowIndexToModel(index);
					// �������� ������� ��� ���������� ������
					Stat prod = kls.get(modelRow);
					try {
				// ����������� ���� (���������� �����) ���������� ������
					  BigDecimal kod = prod.getIdPlayerStats();
					  // ����� ������ ��������� ��� �������� ������
					  if (manager.deleteStat(kod)) {
					  // ����� ������ �������� ������ �� ��������� ������
					    tblModel.deleteRow(modelRow);
					    System.out.println("�������� OK");
					  } else
						JOptionPane.showMessageDialog(this, 
			"������ �������� ������", "������", 
			JOptionPane.ERROR_MESSAGE);
			
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(this, 
			ex.getMessage(), "������ ��������", 
			JOptionPane.ERROR_MESSAGE);
					}
			
				}
		
		 // ������ ������ ��� �������
		private class TKTableModel extends AbstractTableModel {

			private static final long serialVersionUID = 1L;
		private ArrayList<Stat> prods;
		 // ����������� ������� ������
		 public TKTableModel(ArrayList<Stat> prods) {
		 this.prods = prods;
		 }
		 // ���������� ������� � �������
		 @Override
		 public int getColumnCount() {
		 return 13;
		 }
		 // ���������� ����� � ������� = ������� ������
		 @Override
		 public int getRowCount() {
		 return (prods==null?0:prods.size());
		 }
		 // ����������� ����������� �����
		 @Override 
		 public Object getValueAt(int rowIndex, int columnIndex) {
		 // �������� ������ �� ������ �� �������� �������
			 Stat playerStats = prods.get(rowIndex);
		 // ������ ������� ������������ ���� �������
		 switch (columnIndex) {
		 case 0: // !!!  ������� �������� ������� ��� ������ ������, ���� ������� ���
	            if (playerStats.getPlayer().getTeam() != null) {
	                return playerStats.getPlayer().getTeam().getTeam_name();
	            } else {
	                return "����������� �������"; //  
	            }
		
		 case 1:
			 return playerStats.getPlayer().getName();	
			
		 case 2:
			 return playerStats.getPointScored();
		 case 3:
			 return playerStats.getAssists();
		 case 4:
			 return playerStats.getSteal();
		 case 5:
			 return playerStats.getTurnover();
		 case 6:
			 return playerStats.getBlockedShot();
		 case 7:
			 return playerStats.getFoul();
		 case 8:
			 return playerStats.getDoubleDouble();
		 case 9:
			 return playerStats.getTriple();
		 case 10:
			 return playerStats.getFreeThrow();
		 case 11:
			 return playerStats.getDr();
		 case 12:
			 return playerStats.getOr();
		 
		 
		 default:
		 return null;
		 }
		 }
		 // ����������� �������� �������
		 @Override
		 public String getColumnName(int column) {
		 switch (column) {
		 case 0:
		 return "�������";
		
		 case 1:
			 return "�����";
		 case 2:
			 return "�����";
		 case 3:
			 return "��������";
		 case 4:
			 return "����������";
		 case 5:
			 return "������ ����";
		 case 6:
			 return "���������";
		 case 7:
			 return "�����";
		 case 8:
			 return "2-� �������";
		 case 9:
			 return "3-� �������";
		 case 10:
			 return "��������";
		 case 11:
			 return "������� �� ����� ����";
		 case 12:
			 return "������� �� ����� ����";
		 
		 default:
		 return null;
		 }
		 }
		 // ���� ����� ������������ ��� ����������� �����������
		 // ����� ������� � ����������� �� ���� ������
		 @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
		 if (c==0) // ������ �� null � ������� 4
		 return java.lang.Number.class;
		 else if (c == 1) // ������ �� null � ������� 5
	//return Date.class;
		return java.lang.Number.class;
		 else
		 return java.lang.Number.class;
		 }


	//������� ���������� ������
				public void addRow(Stat prod) {
					//  ���������� ��������� ����������� ������
					int len = prods.size();
					// ���������� � ����� ������ � ������ ������
					prods.add(prod);
					// ���������� ����������� ������ � ������ �������
					fireTableRowsInserted(len, len);
				}
				// ������� ��������������
				public void updateRow(int index) {
					// ���������� ����������� ���������� ������
					fireTableRowsUpdated(index, index);
				}
				// ������� ��������
				public void deleteRow(int index) {
					//  ���� ��������� ������ �� ����� �������
					if (index != prods.size() - 1)
						fireTableRowsUpdated(index + 1, prods.size() - 1);
						// �������� ������ �� ������ ������
						prods.remove(index);
						// ���������� ����������� ����� ��������
						fireTableRowsDeleted(index, index);
				}
		 }
		}


