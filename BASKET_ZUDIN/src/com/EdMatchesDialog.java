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
	// Поля класса
	  private DBManager manager;
	  private BigDecimal old_key; // прежнее значение ключа
	  // Два варианта заголовка окна
	  private final static String title_add = 
	    "Добавление нового матча";
	  private final static String title_ed = 
	     "Редактирование матча";
	   //  Объектная переменная для класса Матч
		private Matches type = null;
	    // Флаг режима «добавление новой строки»
		private boolean isNewRow = false;
	    // Форматер для полей дат
		SimpleDateFormat frmt = 
	          new SimpleDateFormat("dd-MM-yyyy");
	    // Элементы (поля редактирования) для полей записи
		private JTextField edCod_event;
		private JTextField edt1;
		private JTextField edt2;
		private JTextField edPlr;
		private JTextField edDate_zaver;
		private JTextField edkteam1, edkteam2;
		@SuppressWarnings("rawtypes")
		private JComboBox cmbEver, cmbTeam1, cmbTeam2;
		
		//  кнопки
		private JButton btnOk;
		private JButton btnCancel;
		//ДЛЯ ПОДЧ. ТАБЛИЦЫ
		 private JTable tblStat;
		 
			private JButton btnClose;
			// кнопка редактирования 
				private JButton btnEdit;
				// кнопка добавления 
				private JButton btnNew;
				// кнопка удаления 
				private JButton btnDelete;
				//табл модель
			private TKTableModel tblModel;
			 private ArrayList<Stat> kls;
			 
	    //  Конструктор класса
		public EdMatchesDialog(Window parent,Matches type,
	                    DBManager manager) {
	      this.manager = manager;
	  // Установка флага для режима добавления новой строки
		  isNewRow = type == null ? true : false;
		  // Определение заголовка для операций добавл./редакт.
		  setTitle(isNewRow ? title_add : title_ed);
		  // Определение объекта редактируемой строки  
		  if (!isNewRow) {
		     this.type = type; // Существующий объект
		     // Сохранение прежнего значения ключа
		     old_key=type.getId_matches();
		     loadData2(); //Загрузка в таблицу 2
		     }
		  else {
		     this.type = new Matches();	// Новый объект
		     kls=new ArrayList <Stat>();
		  }
		  // Создание графического интерфейса окна
		  createGui();
		  // Назначение обработчиков для основных событий
		  bindListeners();
		  //  Получение данных
		  loadData();
		  setSize(1200,600);
		  // Задание режима неизменяемых размеров окна
		  setResizable(true);
		  setButton(); // Настройка отображения кнопок формы
		  setLocationRelativeTo(parent);
		}
		// Метод настройки кнопок формы
		private void setButton() {
		if (btnOk.getText().equals("Сохранить")) {
		btnCancel.setText("Отмена");
		btnNew.setEnabled(false);
		btnEdit.setEnabled(false);
		btnDelete.setEnabled(false);
		} else {
		btnCancel.setText("Выход");
		btnNew.setEnabled(true);
		btnEdit.setEnabled(true);
		btnDelete.setEnabled(true);
		}
		}
		 // Получение данных в таблицу 2
		private void loadData2() {
		 // Получение данных через менеджер
		 kls=manager.loadStat(type.getId_matches());

		 }
		//Метод назначения обработчиков основных событий
		  private void bindListeners() {
		   //  Обработчик нажатия клавиш 
		   setKeyListener(this, new KeyAdapter(){
		    @Override
	        // Обработка нажатия клавиши ESC – закрытие окна 
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
	      // Обработка нажатия кнопки закрытия окна  
		  addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e)
			{
				close();
			}
		  });
	      //  Обработка  кнопки «Отмена»
		  btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
			  // Возврат Cancel и закрытие окна
				setDialogResult(JDialogResult.Cancel);
				close();
			}
		  });
		  //  Обработка  кнопки «Сохранить»
		  btnOk.addActionListener(new ActionListener() {
			  @Override
			  public void actionPerformed(ActionEvent e) {
			  if (((AbstractButton) e.getSource()).getText().equals("Сохранить")) {
			  // Проверка данных, возврат Ok и закрытие окна
			  if (!constructMatches()) return;
			  //новая версия построения 313
			  else {if (isNewRow)
			  			{
				  if (manager.addMatches(type) == true)
				  			{
				  					setDialogResult(JDialogResult.OK);
				  					((AbstractButton) e.getSource()).setText("Редактировать");
				  					setButton();
				  			}
			  			}
			  else 
				  if (manager.updateMatches(type, old_key)==true)
				  {
					  setDialogResult(JDialogResult.OK);
				  ((AbstractButton) e.getSource()).setText("Редактировать");
				  setButton();
				  }	} 
			  }
			  else {
			  ((AbstractButton) e.getSource()).setText("Сохранить");
			  setButton();
			  }
			  }
			  });
		  // Для кнопки Закрыть
			 btnClose.addActionListener(new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
			 // Закрываем окно
			 dispose();
			  }
			  });
			//  Для кнопки добавления
							btnNew.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									addStat();
								}
							});
							//  Для кнопки редактирования
							btnEdit.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									editStat();
								}
							});
							//  Для кнопки удаления
							btnDelete.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
								deleteStat();
								}
							});
							// Событие выбора элемента списка
							  cmbEver.addItemListener(new ItemListener() {
							  @Override
							  public void itemStateChanged(ItemEvent e) {
							  // установка значения поля внешнего ключа
							  if (e.getStateChange() == ItemEvent.SELECTED) {
							  if (e.getItem() != null) {
							  Event c4 = (Event) e.getItem();
							  edCod_event.setText(c4.getId_event().toString());
							  }
							  }
							  }
							  });
							  // Событие потери фокуса полем ввода
							  edCod_event.addFocusListener(new FocusListener() {
							  @Override
							  public void focusLost(FocusEvent e) {
							  // При потере фокуса изменяем элемент списка
									//Проверка на пустоту-добавлена Романом
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
							//Событие выбора элемента списка
								cmbTeam1.addItemListener(new ItemListener() {
									 @Override
									 public void itemStateChanged(ItemEvent e) {
									 // установка значения поля внешнего ключа
									 if (e.getStateChange() == ItemEvent.SELECTED) {
									 if (e.getItem() != null) {
									 Team gr = (Team) e.getItem();
									 edkteam1.setText(gr.getId_team().toString());
									 }
									 }
									 }
									});
									// Событие потери фокуса полем ввода
								edkteam1.addFocusListener(new FocusListener() {
									 @Override
									 public void focusLost(FocusEvent e) {
									 // При потере фокуса изменяем элемент списка
										//Проверка на пустоту-добавлена Романом
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
								//Событие выбора элемента списка
								cmbTeam2.addItemListener(new ItemListener() {
									 @Override
									 public void itemStateChanged(ItemEvent e) {
									 // установка значения поля внешнего ключа
									 if (e.getStateChange() == ItemEvent.SELECTED) {
									 if (e.getItem() != null) {
									 Team gr = (Team) e.getItem();
									 edkteam2.setText(gr.getId_team().toString());
									 }
									 }
									 }
									});
									// Событие потери фокуса полем ввода
								edkteam2.addFocusListener(new FocusListener() {
									 @Override
									 public void focusLost(FocusEvent e) {
									 // При потере фокуса изменяем элемент списка
										//Проверка на пустоту-добавлена Романом
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
			 
		  // Метод создания графического интерфейса
		@SuppressWarnings("rawtypes")
		private void createGui() {
			// Создание панели
			JPanel pnl = new JPanel(new MigLayout(
	"insets 5", "[][]","[]5[]10[]"));
			// Создание полей для редактирования данных
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
			//  Создание кнопок
			btnOk = new JButton("Сохранить");
			btnCancel = new JButton("Отмена");
			// Добавление элементов на панель
			pnl.add(new JLabel("Мероприятие"));
			pnl.add(edCod_event, "span, split 2,  left, sg 2");
			pnl.add(cmbEver, "wrap");
			pnl.add(new JLabel("Дата проведения"));
			pnl.add(edDate_zaver,"span");
			pnl.add(new JLabel("Счёт"));
			pnl.add(edt1,"span, split 3");
			pnl.add(new JLabel(":"));
			pnl.add(edt2);
			pnl.add(new JLabel("Поле проведения"));
			pnl.add(edPlr,"span");
			pnl.add(new JLabel("Команда 1"));
			pnl.add(edkteam1,"split 2");
			pnl.add(cmbTeam1, "growx, wrap");
			pnl.add(new JLabel("Команда 2"));
			pnl.add(edkteam2,"split 2");
			pnl.add(cmbTeam2, "growx, wrap");
			
			
		
			
			
			pnl.add(btnOk, "span, split 2, center, sg ");
			pnl.add(btnCancel, "sg 1");
			//  Добавление панели в окно фрейма
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(pnl, BorderLayout.NORTH);
			 JPanel pnfl = new JPanel(new MigLayout("insets 3, gapy 4",
						//	JPanel pnl = new JPanel(new MigLayout("insets 3, gap 90! 90!",
						 "[grow, fill]", "[]5[grow, fill]10[]"));
						 // Создание объекта таблицы
						 tblStat = new JTable();
						 // Создание объекта табличной модели на базе
						 // сформированного списка
						 tblStat.setModel(tblModel = new TKTableModel(kls));
						 // Создание объекта сортировки для табличной модели
						 RowSorter<TKTableModel> sorter = new
						 TableRowSorter<TKTableModel>(tblModel);
						 // Назначение объекта сортировки таблице
						 tblStat.setRowSorter(sorter);
						 // Задаем параметры внешнего вида таблицы
						 // Выделение полосой всей текущей строки
						 tblStat.setRowSelectionAllowed(true);
						 // Задаем промежутки между ячейками
						 tblStat.setIntercellSpacing(new Dimension(0, 1)); 
						 // Задаем цвет сетки
						 tblStat.setGridColor(new Color(170, 170, 255).darker());
						 // Автоматическое определение ширины последней колонки
						 tblStat.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
						 // Возможность выделения только 1 строки
						 tblStat.getSelectionModel().setSelectionMode(
						 ListSelectionModel.SINGLE_SELECTION);
						 // Создание области прокрутки и вставка в нее таблицы
						 JScrollPane scrlPane = new JScrollPane(tblStat);
						 scrlPane.getViewport().setBackground(Color.white);
						 scrlPane.setBorder(BorderFactory.createCompoundBorder
						(new EmptyBorder(3,0,3,0),scrlPane.getBorder()));
						// tblStat.getColumnModel().getColumn(0).setMaxWidth(100);
						 //tblStat.getColumnModel().getColumn(0).setMinWidth(100);
						 // Создание кнопки для закрытия формы
						 btnClose = new JButton("Закрыть");
						 pnfl.add(getToolBar(),"growx,wrap");//Создание  панели
						 //Добавление на панель: метки, области с таблицей и кнопки
						 pnfl.add(scrlPane, "grow, span");
						 pnfl.add(btnClose, "growx 0, right");
						
						 TableCellRenderer centerRenderer = (TableCellRenderer) new CenterAlignedTableCellRenderer();  // !!!  Приведение типа
						    for (int i = 0; i < tblStat.getColumnModel().getColumnCount(); i++) {
						        tblStat.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
						    }
						 
						 	getContentPane().add(pnfl, BorderLayout.CENTER);
		}
		// Метод формирования маски ввода даты
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
		//  Метод добавление слушателя клавиатуры 
	//к компонентам окна
		private void setKeyListener(Component c, KeyListener kl)
		{
		  c.addKeyListener(kl);
		  if (c instanceof Container)
		    for (Component comp:((Container)c).getComponents())
			 setKeyListener(comp, kl);
		}
	    // Метод инициализации полей формы (при редактировании)
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
			// Создание модели данных на базе списка
			@SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model5 =
			new DefaultComboBoxModel(lst6.toArray());
			// Установка модели для JComboBox
			cmbEver.setModel(model5);
			BigDecimal grKod2;
			// Определение поля внешнего ключа
			 //НОВАЯ ВЕРСИЯ ОПРЕДЕЛЕНИЯ ПОЛ
			 grKod2 = (isNewRow? null :
				 type.getEvent()==null? null :
					 type.getEvent().getId_event());
			// Вызов метода установки элемента списка
			// соответствующего значению внешнего ключа
			setCmbItem6(model5, grKod2);
			}
			
			ArrayList<Team> lst1 = manager.loadTeamForCmb();
			if (lst1 != null) {
			 // Создание модели данных на базе списка
			 @SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model1 =
			new DefaultComboBoxModel(lst1.toArray());
			 // Установка модели для JComboBox
			 cmbTeam1.setModel(model1);
			 BigDecimal grKod1;
			 // Определение поля внешнего ключа
			 grKod1 = (isNewRow? null :
				 type.getTeam2()==null? null :
					 type.getTeam2().getId_team());
			 
			 
			 // Вызов метода установки элемента списка
			 // соответствующего значению внешнего ключа
			 setCmbItem1(model1, grKod1);
			}
			ArrayList<Team> lst2 = manager.loadTeamForCmb();
			if (lst2 != null) {
			 // Создание модели данных на базе списка
			 @SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model2 =
			new DefaultComboBoxModel(lst2.toArray());
			 // Установка модели для JComboBox
			 cmbTeam2.setModel(model2);
			 // Определение поля внешнего ключа
			 BigDecimal grKod = (isNewRow? null :
			type.getTeam1().getId_team());
			 // Вызов метода установки элемента списка
			 // соответствующего значению внешнего ключа
			 setCmbItem2(model2, grKod);
			}
		}
		// Установка элемента списка
		private void setCmbItem6(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
		BigDecimal grKod) {
		cmbEver.setSelectedItem(null);
		if (grKod != null)
		// Просмотр элементов списка для нахождения элемента
		// с заданным кодом
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
				 // Просмотр элементов списка для нахождения элемента
				 // с заданным кодом
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
				 // Просмотр элементов списка для нахождения элемента
				 // с заданным кодом
				 for (int i = 0, c = model1.getSize(); i < c; i++)
				 if (((Team) model1.getElementAt(i)).
				getId_team().equals(grKod)) {
				 cmbTeam2.setSelectedIndex(i);
				 break;
				 }
				} 
		
		
		//Формирование объекта m перед сохранением
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
	            ex.getMessage(), "Ошибка данных",
			   JOptionPane.ERROR_MESSAGE);
			 return false;
		  }
		}
		// Возврат объекта Заявка
		public Matches getMatches()
		{
			return type;
		}
		 private JToolBar getToolBar() {
		     // Создание панели
			JToolBar res = new JToolBar();
			// Неизменяемое положение панели
			res.setFloatable(false);
			// Добавление кнопки «Добавить»
			// Определение местоположения изображения для кнопки
			URL url = FrmTeam.class.getResource("/images/add.png");
			// Создание кнопки с изображением
			btnNew = new JButton(new ImageIcon(url));
			// На кнопку не устанавливается фокус
			btnNew.setFocusable(false);
			//  Добавление всплывающей подсказки для кнопки
			btnNew.setToolTipText("Добавить новую информацию");
			// Добавление кнопки «Удалить»
			url = FrmTeam.class.getResource("/images/delete.png");
			btnDelete = new JButton(new ImageIcon(url));
			btnDelete.setFocusable(false);
			btnDelete.setToolTipText("Удалить запись");
			// Добавление кнопки «Редактировать»
			url = FrmTeam.class.getResource("/images/edit.png");
			btnEdit = new JButton(new ImageIcon(url));
			btnEdit.setFocusable(false);
			btnEdit.setToolTipText("Изменить данные записи");
			//  Добавление кнопок на панель
			res.add(btnNew);
			res.add(btnEdit);
			res.add(btnDelete);
			// Возврат панели в качестве результата
			return res;
		   }
		 
		//Редактирование текущей записи
			private void editStat() {			
				int index = tblStat.getSelectedRow();
				if (index == -1)
					return;
				// Преобразование индекса таблицы в индекс модели
				int modelRow = tblStat.convertRowIndexToModel(index);
				// Получаем объект из модели по индексу
				Stat prod = kls.get(modelRow);
				
				
				// Создание объекта окна редактирования
				EdStatDialog dlg = new EdStatDialog(this,
		    prod,manager, type.getId_matches());
				// Вызов окна и проверка кода возврата
				if (dlg.showDialog() == JDialogResult.OK) {
		               // Вызов метода обновления строки данных табличной модели
					tblModel.updateRow(modelRow);
					System.out.println("Обновление OK");
					}
				
		
			}
			// Создание новой записи
				private void addStat() {

					// Создание объекта окна редактирования
					EdStatDialog dlg = new EdStatDialog(this, 
						null,manager,type.getId_matches());
					dlg.showDialog();
					Stat prod = dlg.getStat();
					if (prod!=null&&prod.getIdPlayerStats()!=null) {
						// Добавление его в табличную модель
						tblModel.addRow(prod);
					}
			
				}
				// Удаление текущей записи
				private void deleteStat() {
					// Определяем индекс текущей строки.
					int index = tblStat.getSelectedRow();
					// Если нет выделенной строки, то выход
					if (index == -1)
						return;
					// Вывод запроса на удаления. При отказе - выход
					if (JOptionPane.showConfirmDialog(this, 
			  "Удалить статистику?", "Подтверждение", 
			  JOptionPane.YES_NO_OPTION,
					  JOptionPane.QUESTION_MESSAGE) != 
					  JOptionPane.YES_OPTION)
					  return;
					// Преобразование индекса представления в индекс модели
					int modelRow = tblStat.convertRowIndexToModel(index);
					// Создание объекта для выделенной строки
					Stat prod = kls.get(modelRow);
					try {
				// Определение кода (первичного ключа) выделенной строки
					  BigDecimal kod = prod.getIdPlayerStats();
					  // Вызов метода менеджера для удаления строки
					  if (manager.deleteStat(kod)) {
					  // Вызов метода удаления строки из табличной модели
					    tblModel.deleteRow(modelRow);
					    System.out.println("Удаление OK");
					  } else
						JOptionPane.showMessageDialog(this, 
			"Ошибка удаления строки", "Ошибка", 
			JOptionPane.ERROR_MESSAGE);
			
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(this, 
			ex.getMessage(), "Ошибка удаления", 
			JOptionPane.ERROR_MESSAGE);
					}
			
				}
		
		 // Модель данных для таблицы
		private class TKTableModel extends AbstractTableModel {

			private static final long serialVersionUID = 1L;
		private ArrayList<Stat> prods;
		 // Конструктор объекта класса
		 public TKTableModel(ArrayList<Stat> prods) {
		 this.prods = prods;
		 }
		 // Количество колонок в таблице
		 @Override
		 public int getColumnCount() {
		 return 13;
		 }
		 // Количество строк в таблице = размеру списка
		 @Override
		 public int getRowCount() {
		 return (prods==null?0:prods.size());
		 }
		 // Определение содержимого ячеек
		 @Override 
		 public Object getValueAt(int rowIndex, int columnIndex) {
		 // Выделяем объект из списка по текущему индексу
			 Stat playerStats = prods.get(rowIndex);
		 // Каждой колонке сопоставляем поле объекта
		 switch (columnIndex) {
		 case 0: // !!!  Выводим название команды или пустую строку, если команды нет
	            if (playerStats.getPlayer().getTeam() != null) {
	                return playerStats.getPlayer().getTeam().getTeam_name();
	            } else {
	                return "Неизвестная команда"; //  
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
		 // Определение названия колонок
		 @Override
		 public String getColumnName(int column) {
		 switch (column) {
		 case 0:
		 return "Команда";
		
		 case 1:
			 return "Игрок";
		 case 2:
			 return "Очков";
		 case 3:
			 return "Ассистов";
		 case 4:
			 return "Перехватов";
		 case 5:
			 return "Потерь мяча";
		 case 6:
			 return "Блокшотов";
		 case 7:
			 return "Фолов";
		 case 8:
			 return "2-х очковых";
		 case 9:
			 return "3-х очковых";
		 case 10:
			 return "Штрафных";
		 case 11:
			 return "Подборы на своем щите";
		 case 12:
			 return "Подборы на чужом щите";
		 
		 default:
		 return null;
		 }
		 }
		 // Этот метод используется для определения отрисовщика
		 // ячеек колонок в зависимости от типа данных
		 @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
		 if (c==0) // защита от null в колонке 4
		 return java.lang.Number.class;
		 else if (c == 1) // защита от null в колонке 5
	//return Date.class;
		return java.lang.Number.class;
		 else
		 return java.lang.Number.class;
		 }


	//Событие добавления строки
				public void addRow(Stat prod) {
					//  Определяем положение добавляемой строки
					int len = prods.size();
					// Добавление в новой строки в список модели
					prods.add(prod);
					// Обновление отображения строки с новыми данными
					fireTableRowsInserted(len, len);
				}
				// Событие редактирования
				public void updateRow(int index) {
					// Обновление отображения измененной строки
					fireTableRowsUpdated(index, index);
				}
				// Событие удаления
				public void deleteRow(int index) {
					//  Если удаленная строка не конце таблицы
					if (index != prods.size() - 1)
						fireTableRowsUpdated(index + 1, prods.size() - 1);
						// Удаление строки из списка модели
						prods.remove(index);
						// Обновление отображения после удаления
						fireTableRowsDeleted(index, index);
				}
		 }
		}


