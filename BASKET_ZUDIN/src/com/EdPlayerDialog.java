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
	// Поля класса
	  private DBManager manager;
	  private BigDecimal old_key; // прежнее значение ключа
	  // Два варианта заголовка окна
	  private final static String title_add = 
	    "Добавление нового игрока";
	  private final static String title_ed = 
	     "Редактирование игрока";
	   //  Объектная переменная для класса Объект .
		private Player type = null;
	    // Флаг режима «добавление новой строки»
		private boolean isNewRow = false;
	    // Форматер для полей дат
		SimpleDateFormat frmt = 
	          new SimpleDateFormat("dd-MM-yyyy");
	    // Элементы (поля редактирования) для полей записи
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
		//  кнопки
		private JButton btnOk;
		private JButton btnCancel;
	    //  Конструктор класса
		public EdPlayerDialog(Window parent,Player type,
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
		     old_key=type.getId();
		     }
		  else
		     this.type = new Player();	// Новый объект
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
				if (!constructPlayer())
					return;
				if (isNewRow) {
	// вызов метода менеджера Добавить новый объект недв.
					if (manager.addPlayer(type)) {
					// при успехе возврат Ok и закрытие окна
						setDialogResult(JDialogResult.OK);
						close();
					}
				} else 
	// вызов метода менеджера Изменить строку
					if (manager.updatePlayer(type, old_key)) {
					setDialogResult(JDialogResult.OK);
					close();
				}
				}
		  });
			// Событие выбора элемента списка
			cmbteaar.addItemListener(new ItemListener() {
			 @Override
			 public void itemStateChanged(ItemEvent e) {
			 // установка значения поля внешнего ключа
			 if (e.getStateChange() == ItemEvent.SELECTED) {
			 if (e.getItem() != null) {
			 Team gr = (Team) e.getItem();
			 edKod_Team.setText(gr.getId_team().toString());
			 }
			 }
			 }
			});
			// Событие потери фокуса полем ввода
			edKod_Team.addFocusListener(new FocusListener() {
			 @Override
			 public void focusLost(FocusEvent e) {
			 // При потере фокуса изменяем элемент списка
				//Проверка на пустоту-добавлена Романом
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
		  // Метод создания графического интерфейса
		private void createGui() {
			// Создание панели
			JPanel pnl = new JPanel(new MigLayout(
	"insets 5", "[][]","[]5[]10[]"));
			// Создание полей для редактирования данных
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
			//  Создание кнопок
			btnOk = new JButton("Сохранить");
			btnCancel = new JButton("Отмена");
			// Добавление элементов на панель
			pnl.add(new JLabel("Игрок"));
			pnl.add(edName,"span");
			pnl.add(new JLabel("Дата рождения"));
			pnl.add(edDater,"span");
			pnl.add(new JLabel("Команда"));
			pnl.add(edKod_Team, "split 2");
			pnl.add(cmbteaar, "growx, wrap");
			pnl.add(new JLabel("Рост"));
			pnl.add(edHeie,"span");
			pnl.add(new JLabel("Вес"));
			pnl.add(edWeie,"span");
		
			pnl.add(new JLabel("Роль"));
			pnl.add(edRole,"span");
			pnl.add(new JLabel("Номер"));
			pnl.add(edNum,"span");
			pnl.add(new JLabel("Пол"));
			pnl.add(edGeed,"span");
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
		// Создание списка
					// Загружаем данные в список
				
					ArrayList<Team> lst = manager.loadTeam();
					if (lst != null) {
					 // Создание модели данных на базе списка
					 @SuppressWarnings({ "rawtypes" })
					DefaultComboBoxModel model =
					new DefaultComboBoxModel(lst.toArray());
					 // Установка модели для JComboBox
					 cmbteaar.setModel(model);
					 // Определение поля внешнего ключа
					 BigDecimal grKod = (isNewRow? null :
					type.getTeam().getId_team());
					 // Вызов метода установки элемента списка
					 // соответствующего значению внешнего ключа
					 setCmbItem(model, grKod);
					}
					} 
					//Создадим метод установки элемента поля списка
					// Установка элемента списка
					private void setCmbItem(@SuppressWarnings("rawtypes") DefaultComboBoxModel model,
					 BigDecimal grKod) {
					cmbteaar.setSelectedItem(null);
					if (grKod != null)
					 // Просмотр элементов списка для нахождения элемента
					 // с заданным кодом
					 for (int i = 0, c = model.getSize(); i < c; i++)
					 if (((Team) model.getElementAt(i)).
					getId_team().equals(grKod)) {
					 cmbteaar.setSelectedIndex(i);
					 break;
					 }
					} 
		//Формирование объекта О ж. перед сохранением
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
	            ex.getMessage(), "Ошибка данных",
			   JOptionPane.ERROR_MESSAGE);
			 return false;
		  }
		}
		// Возврат объекта Объект недв.
		public Player getPlayer()
		{
			return type;
		}
}
