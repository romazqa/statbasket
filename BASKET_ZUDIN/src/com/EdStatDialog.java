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
	// Поля класса
	  private DBManager manager;
	  private BigDecimal fk_key;
	  private BigDecimal old_key; // прежнее значение ключа
	  // Два варианта заголовка окна
	  private final static String title_add = 
	    "Добавление новой информации";
	  private final static String title_ed = 
	     "Редактирование статистики";
	   //  Объектная переменная для класса Книга учёта
		private Stat type = null;
	    // Флаг режима «добавление новой строки»
		private boolean isNewRow = false;
	    // Форматер для полей дат
		SimpleDateFormat frmt = 
	          new SimpleDateFormat("dd-MM-yyyy");
	    // Элементы (поля редактирования) для полей записи
		private JTextField edpointscored;
		
		
		private JTextField edAssistsoim;
		private JTextField edSteal;
		private JTextField ed2, ed3;
		private JTextField edDr, edOr,  edFree;
		private JTextField edTurnover, edBlockedshot, edFoull;
	
		@SuppressWarnings("rawtypes")
		private JComboBox cmbPerso;
		private JTextField edCod_perso;
		//  кнопки
		private JButton btnOk;
		private JButton btnCancel;
		
		 
		
			
			
	    //  Конструктор класса
		public EdStatDialog(Window parent,Stat type,
	                    DBManager manager, BigDecimal fk_key) {
	      this.manager = manager;
	  // Установка флага для режима добавления новой строки
		  isNewRow = type == null ? true : false;
		  // Определение заголовка для операций добавл./редакт.
		  setTitle(isNewRow ? title_add : title_ed);
		     this.fk_key=fk_key;
		  // Определение объекта редактируемой строки  
		  if (!isNewRow) {
		     this.type = type; // Существующий объект
		     // Сохранение прежнего значения ключа
		     old_key=type.getIdPlayerStats();
		
		
		     }
		  else {
		     this.type = new Stat();	// Новый объект
		    
		  }
		  // Создание графического интерфейса окна
		  createGui();
		  // Назначение обработчиков для основных событий
		  bindListeners();
		  //  Получение данных
		  loadData();
		  setSize(900,500);
		  pack();
		  // Задание режима неизменяемых размеров окна
		  setResizable(false);
		  
		  setLocationRelativeTo(parent);
		}
		// Метод настройки кнопок формы
		
	
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
		  btnOk.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
				  // Проверка данных, выход 
		  //   при неправильном заполнении полей
					if (!constructStat())
						return;
					if (isNewRow) {
		// вызов метода менеджера Добавить новый st
						if (manager.addStat(type)) {
						// при успехе возврат Ok и закрытие окна
							setDialogResult(JDialogResult.OK);
							close();
						}
					} else 
		// вызов метода менеджера Изменить строку
						if (manager.updateStat(type, old_key)) {
						setDialogResult(JDialogResult.OK);
						close();
					}
					}
			  });
		  
		  //  Обработка  кнопки «Сохранить»
		 
		 
			
				// Событие выбора элемента списка
				  cmbPerso.addItemListener(new ItemListener() {
				  @Override
				  public void itemStateChanged(ItemEvent e) {
				  // установка значения поля внешнего ключа
				  if (e.getStateChange() == ItemEvent.SELECTED) {
				  if (e.getItem() != null) {
				  Player c4 = (Player) e.getItem();
				  edCod_perso.setText(c4.getId().toString());
				  }
				  }
				  }
				  });
				  // Событие потери фокуса полем ввода
				  edCod_perso.addFocusListener(new FocusListener() {
				  @Override
				  public void focusLost(FocusEvent e) {
				  // При потере фокуса изменяем элемент списка
						//Проверка на пустоту-добавлена Романом
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
				
				} // конец 
		
		  // Метод создания графического интерфейса
		@SuppressWarnings("rawtypes")
		private void createGui() {
			// Создание панели
			JPanel pnl = new JPanel(new MigLayout(
	"insets 5", "[][]","[]5[]10[]"));
			// Создание полей для редактирования данных
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
			
			//  Создание кнопок
			btnOk = new JButton("Сохранить");
			btnCancel = new JButton("Отмена");
			// Добавление элементов на панель
			//pnl.add(new JLabel("ИД книги учёта"));
		//	pnl.add(edCod,"span");
			
			pnl.add(new JLabel("Игрок"));
			pnl.add(edCod_perso, "split 2");
			pnl.add(cmbPerso, "growx, wrap");
			pnl.add(new JLabel("Очков"));
			pnl.add(edpointscored,"span");
			pnl.add(new JLabel("Кол-во ассистов"));
			pnl.add(edAssistsoim,"span");
			pnl.add(new JLabel("Кол-во перехватов"));
			pnl.add(edSteal,"span");

			pnl.add(new JLabel("Кол-во потерь мяча"));
			pnl.add(edTurnover,"span");
			pnl.add(new JLabel("Кол-во блок-шотов"));
			pnl.add(edBlockedshot, "span");
			
			pnl.add(new JLabel("Кол-во фолов"));
		
			pnl.add(edFoull, "span");
			
			
			pnl.add(new JLabel("Кол-во 2-х очковых"));
			pnl.add(ed2,"span");

			pnl.add(new JLabel("Кол-во 3-х очковых"));
			pnl.add(ed3,"span");

			pnl.add(new JLabel("Кол-во штрафных"));
			pnl.add(edFree,"span");
			pnl.add(new JLabel("Кол-во подборов на своем щите"));
			pnl.add(edDr,"span");
			pnl.add(new JLabel("Кол-во подборов на чужом щите"));
			pnl.add(edOr,"span");
		
			pnl.add(btnOk, "span, split 2, center, sg ");
			pnl.add(btnCancel, "sg 1");
			//  Добавление панели в окно фрейма
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(pnl, BorderLayout.CENTER);
			
			
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
			// Создание модели данных на базе списка
			@SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model5 =
			new DefaultComboBoxModel(lst6.toArray());
			// Установка модели для JComboBox
			cmbPerso.setModel(model5);
			BigDecimal grKod;
			// Определение поля внешнего ключа
			 //НОВАЯ ВЕРСИЯ ОПРЕДЕЛЕНИЯ ПОЛЯ ВНЕШНЕГО КЛЮЧА 
			 grKod = (isNewRow? null :
				 type.getPlayer()==null? null :
					 type.getPlayer().getId());
			// Вызов метода установки элемента списка
			// соответствующего значению внешнего ключа
			setCmbItem6(model5, grKod);
			}
		// Создание списка
			// Загружаем данные в список
		
			
			} 
			
			// Установка элемента списка
			private void setCmbItem6(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
			BigDecimal grKod) {
			cmbPerso.setSelectedItem(null);
			if (grKod != null)
			// Просмотр элементов списка для нахождения элемента
			// с заданным кодом
			for (int i = 0, c = model.getSize(); i < c; i++)
			if (((Player) model.getElementAt(i)).
			getId().equals(grKod)) {
				cmbPerso.setSelectedIndex(i);
				break;
				}
			}
			
		//Формирование объекта Книга учёта перед сохранением
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
	            ex.getMessage(), "Ошибка данных",
			   JOptionPane.ERROR_MESSAGE);
			 return false;
		  }
		}
		// Возврат объекта Книга учёта
		public Stat getStat()
		{
			return type;
		}
	
		 
		
		
		
			
		
		}

