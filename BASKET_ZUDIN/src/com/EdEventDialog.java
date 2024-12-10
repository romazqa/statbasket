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
	// Поля класса
	  private DBManager manager;
	  private BigDecimal old_key; // прежнее значение ключа
	  // Два варианта заголовка окна
	  private final static String title_add = 
	    "Добавление нового соревнования";
	  private final static String title_ed = 
	     "Редактирование соревнования";
	   //  Объектная переменная для класса соревнования
		private Event type = null;
	    // Флаг режима «добавление новой строки»
		private boolean isNewRow = false;
	    // Форматер для полей дат
		SimpleDateFormat frmt = 
	          new SimpleDateFormat("dd-MM-yyyy");
	    // Элементы (поля редактирования) для полей записи
		private JTextField edCod;
		private JTextField edLevelod;
		private JTextField edName;
		private JTextField edLoc;
		private JTextField edYear;
		@SuppressWarnings("rawtypes")
		private JComboBox cmbLevel;
		//  кнопки
		private JButton btnOk;
		private JButton btnCancel;
	    //  Конструктор класса
		public EdEventDialog(Window parent,Event type,
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
		     old_key=type.getId_event();
		     }
		  else
		     this.type = new Event();	// Новый объект
		  // Создание графического интерфейса окна
		  createGui();
		  // Назначение обработчиков для основных событий
		  bindListeners();
		  //  Получение данных
		  loadData();
		  pack();
		  // Задание режима неизменяемых размеров окна
		  setResizable(false);
		  setLocationRelativeTo(parent);
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
		  btnOk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
			  // Проверка данных, выход 
	  //   при неправильном заполнении полей
				if (!constructEvent())
					return;
				if (isNewRow) {
	// вызов метода менеджера Добавить нового здания
					if (manager.addEventt(type)) {
					// при успехе возврат Ok и закрытие окна
						setDialogResult(JDialogResult.OK);
						close();
					}
				} else 
	// вызов метода менеджера Изменить строку
					if (manager.updateEventt(type, old_key)) {
					setDialogResult(JDialogResult.OK);
					close();
				}
				}
		  });
		// Событие выбора элемента списка
			cmbLevel.addItemListener(new ItemListener() {
			 @Override
			 public void itemStateChanged(ItemEvent e) {
			 // установка значения поля внешнего ключа
			 if (e.getStateChange() == ItemEvent.SELECTED) {
			 if (e.getItem() != null) {
			 Level gr = (Level) e.getItem();
			 edLevelod.setText(gr.getKod().toString());
			 }
			 }
			 }
			});
			// Событие потери фокуса полем ввода
			edLevelod.addFocusListener(new FocusListener() {
			 @Override
			 public void focusLost(FocusEvent e) {
			 // При потере фокуса изменяем элемент списка
				//Проверка на пустоту-добавлена Романом
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
			} // конец bindListeners
		  // Метод создания графического интерфейса
		@SuppressWarnings("rawtypes")
		private void createGui() {
			// Создание панели
			JPanel pnl = new JPanel(new MigLayout(
	"insets 5", "[][]","[]5[]10[]"));
			// Создание полей для редактирования данных
	edCod = new JTextField(6);
	edLevelod = new JTextField(6);
	cmbLevel = new JComboBox ();
			edLoc = new JTextField(30);
			edName = new JTextField(20);
			edYear = new JTextField(10);
			//  Создание кнопок
			btnOk = new JButton("Сохранить");
			btnCancel = new JButton("Отмена");
			// Добавление элементов на панель
			pnl.add(new JLabel("Код"));
			pnl.add(edCod,"span");
			pnl.add(new JLabel("Название"));
			pnl.add(edName,"span");
			pnl.add(new JLabel("Место"));
			pnl.add(edLoc,"span");
			pnl.add(new JLabel("Уровень"));
			pnl.add(edLevelod, "split 2");
			pnl.add(cmbLevel, "growx, wrap");
			
			pnl.add(new JLabel("Год"));
			pnl.add(edYear,"span");
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
				edName.setText(type.getName_event().toString());
				edLoc.setText(type.getLocationn().toString());
				edCod.setText(type.getId_event().toString());
				edLevelod.setText(type.getLevel().getKod().toString());
		
			edYear.setText(type.getYear_event().toString());
			}
		// Создание списка
			// Загружаем данные в список
		
			ArrayList<Level> lst = manager.loadLevel();
			if (lst != null) {
			 // Создание модели данных на базе списка
			 @SuppressWarnings({ "rawtypes" })
			DefaultComboBoxModel model =
			new DefaultComboBoxModel(lst.toArray());
			 // Установка модели для JComboBox
			 cmbLevel.setModel(model);
			 // Определение поля внешнего ключа
			 BigDecimal grKod = (isNewRow? null :
			type.getLevel().getKod());
			 // Вызов метода установки элемента списка
			 // соответствующего значению внешнего ключа
			 setCmbItem(model, grKod);
			}
			} 
			//Создадим метод установки элемента поля списка
			// Установка элемента списка
			private void setCmbItem(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
			 BigDecimal grKod) {
			cmbLevel.setSelectedItem(null);
			if (grKod != null)
			 // Просмотр элементов списка для нахождения элемента
			 // с заданным кодом
			 for (int i = 0, c = model.getSize(); i < c; i++)
			 if (((Level) model.getElementAt(i)).
			getKod().equals(grKod)) {
			 cmbLevel.setSelectedIndex(i);
			 break;
			 }
			} 
		//Формирование объекта  перед сохранением
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
	            ex.getMessage(), "Ошибка данных",
			   JOptionPane.ERROR_MESSAGE);
			 return false;
		  }
		}
		// Возврат объекта Здание (объект застройки)
		public Event getEvent()
		{
			return type;
		}
}

