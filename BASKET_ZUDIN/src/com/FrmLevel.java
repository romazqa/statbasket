package com;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import com.data.*;

public class FrmLevel extends JDialog {
	private static final long serialVersionUID = 1L;
	// Поля класса
	private DBManager manager;
	 private JTable tblGorod;
	private JButton btnClose;
	// кнопка редактирования 
		private JButton btnEdit;
		// кнопка добавления 
		private JButton btnNew;
		// кнопка удаления 
		private JButton btnDelete;
	private TKTableModel tblModel;
	 @SuppressWarnings("unused")
	private TableRowSorter<TKTableModel> tblSorter;
	 private ArrayList<Level> gorrs;
	 // Конструктор класса
	 // параметр – менеджер соединения
	public FrmLevel(DBManager manager){
	 super();
	 this.manager=manager;
	 // Установка модального режима вывода окна
	 setModal(true);
	 //при закрытии окна освобождаем используемые им ресурсы
	 setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	 // Получение данных
	 loadData();
	 // Построение графического интерфейса окна
	 createGUI();
	 // Добавление обработчиков для основных событий
	 bindListeners();
	//pack();
	 setSize (700, 500);
	 // Заголовок окна
	 setTitle("Уровни соревнований");
	 setLocationRelativeTo(this);
	 }
	 // Получение данных
	private void loadData() {
	 // Получение данных через менеджер
	 gorrs=manager.loadLevel();
	 }
	 // Метод создания пользовательского интерфейса
	private void createGUI() {
	 // Создание панели
	 JPanel pnl = new JPanel(new MigLayout("insets 3, gapy 4",
	//	JPanel pnl = new JPanel(new MigLayout("insets 3, gap 90! 90!",
	 "[grow, fill]", "[]5[grow, fill]10[]"));
	 // Создание объекта таблицы
	 tblGorod = new JTable();
	 // Создание объекта табличной модели на базе
	 // сформированного списка
	 tblGorod.setModel(tblModel = new TKTableModel(gorrs));
	 // Создание объекта сортировки для табличной модели
	 RowSorter<TKTableModel> sorter = new
	 TableRowSorter<TKTableModel>(tblModel);
	 // Назначение объекта сортировки таблице
	 tblGorod.setRowSorter(sorter);
	 // Задаем параметры внешнего вида таблицы
	 // Выделение полосой всей текущей строки
	 tblGorod.setRowSelectionAllowed(true);
	 // Задаем промежутки между ячейками
	 tblGorod.setIntercellSpacing(new Dimension(0, 1)); 
	 // Задаем цвет сетки
	 tblGorod.setGridColor(new Color(170, 170, 255).darker());
	 // Автоматическое определение ширины последней колонки
	 tblGorod.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	 // Возможность выделения только 1 строки
	 tblGorod.getSelectionModel().setSelectionMode(
	 ListSelectionModel.SINGLE_SELECTION);
	 // Создание области прокрутки и вставка в нее таблицы
	 JScrollPane scrlPane = new JScrollPane(tblGorod);
	 scrlPane.getViewport().setBackground(Color.white);
	 scrlPane.setBorder(BorderFactory.createCompoundBorder
	(new EmptyBorder(3,0,3,0),scrlPane.getBorder()));
	 tblGorod.getColumnModel().getColumn(0).setMaxWidth(100);
	 tblGorod.getColumnModel().getColumn(0).setMinWidth(100);
	 // Создание кнопки для закрытия формы
	 btnClose = new JButton("Закрыть");
	 //Добавление на панель: метки, области с таблицей и кнопки
	 pnl.add(scrlPane, "grow, span");
	 pnl.add(btnClose, "growx 0, right");
	 // Добавление панели в окно
	 getContentPane().setLayout(
	 new MigLayout("insets 0 2 0 2, gapy 0", "[grow, fill]",
	 "[grow, fill]"));
	 getContentPane().add(pnl, "grow");
	 pnl.add(getToolBar(),"growx,wrap");//Создание  панели
//	 pnl.add(new JLabel("Справочник видов техники:"), "growx,span");
	 	pnl.add(scrlPane, "grow, span");
	 	pnl.add(btnClose, "growx 0, right");
	 }
	 // Метод назначения обработчиков
	private void bindListeners() {
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
						addGorod();
					}
				});
				//  Для кнопки редактирования
				btnEdit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						editGorod();
					}
				});
				//  Для кнопки удаления
				btnDelete.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					deleteGorod();
					}
				});
	 }
	
	 private JToolBar getToolBar() {
	     // Создание панели
		JToolBar res = new JToolBar();
		// Неизменяемое положение панели
		res.setFloatable(false);
		// Добавление кнопки «Добавить»
		// Определение местоположения изображения для кнопки
		URL url = FrmLevel.class.getResource("/images/add.png");
		// Создание кнопки с изображением
		btnNew = new JButton(new ImageIcon(url));
		// На кнопку не устанавливается фокус
		btnNew.setFocusable(false);
		//  Добавление всплывающей подсказки для кнопки
		btnNew.setToolTipText("Добавить новый вид");
		// Добавление кнопки «Удалить»
		url = FrmLevel.class.getResource("/images/delete.png");
		btnDelete = new JButton(new ImageIcon(url));
		btnDelete.setFocusable(false);
		btnDelete.setToolTipText("Удалить вид");
		// Добавление кнопки «Редактировать»
		url = FrmLevel.class.getResource("/images/edit.png");
		btnEdit = new JButton(new ImageIcon(url));
		btnEdit.setFocusable(false);
		btnEdit.setToolTipText("Изменить данные вида");
		//  Добавление кнопок на панель
		res.add(btnNew);
		res.add(btnEdit);
		res.add(btnDelete);
		// Возврат панели в качестве результата
		return res;
	   }
	 
	//Редактирование текущего клиента
		private void editGorod() {			
			int index = tblGorod.getSelectedRow();
			if (index == -1)
				return;
			// Преобразование индекса таблицы в индекс модели
			int modelRow = tblGorod.convertRowIndexToModel(index);
			// Получаем объект из модели по индексу
			Level prod = gorrs.get(modelRow);
		
			
			// Создание объекта окна редактирования
			EdLevelDialog dlg = new EdLevelDialog(this,
	    prod,manager);
			// Вызов окна и проверка кода возврата
			if (dlg.showDialog() == JDialogResult.OK) {
	               // Вызов метода обновления строки данных табличной модели
				tblModel.updateRow(modelRow);
				System.out.println("Обновление OK");
				}
		}
		// Создание нового города
			private void addGorod() {

				// Создание объекта окна редактирования
				EdLevelDialog dlg = new EdLevelDialog(this, 
					null,manager);
				// Вызов окна и проверка кода возврата
				if (dlg.showDialog() == JDialogResult.OK) {
					// Создание нового объекта по введенным данным
					Level prod = dlg.getLevel();
					//  Добавление его в табличную модель
					tblModel.addRow(prod);}

			}
			// Удаление текущего города
			private void deleteGorod() {
				// Определяем индекс текущей строки.
				int index = tblGorod.getSelectedRow();
				// Если нет выделенной строки, то выход
				if (index == -1)
					return;
				// Вывод запроса на удаления. При отказе - выход
				if (JOptionPane.showConfirmDialog(this, 
		  "Удалить уровень?", "Подтверждение", 
		  JOptionPane.YES_NO_OPTION,
				  JOptionPane.QUESTION_MESSAGE) != 
				  JOptionPane.YES_OPTION)
				  return;
				// Преобразование индекса представления в индекс модели
				int modelRow = tblGorod.convertRowIndexToModel(index);
				// Создание объекта для выделенной строки
				Level prod = gorrs.get(modelRow);
				try {
			// Определение кода (первичного ключа) выделенной строки
				  BigDecimal kod = prod.getKod();
				  // Вызов метода менеджера для удаления строки
				  if (manager.deleteLevel(kod)) {
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
	private ArrayList<Level> prods;
	 // Конструктор объекта класса
	 public TKTableModel(ArrayList<Level> prods) {
	 this.prods = prods;
	 }
	 // Количество колонок в таблице
	 @Override
	 public int getColumnCount() {
	 return 2;
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
		 Level pr = prods.get(rowIndex);
	 // Каждой колонке сопоставляем поле объекта
	 switch (columnIndex) {
	 case 0:
	 return pr.getKod();
	 case 1:
	 return pr.getName();
	
	 
	 default:
	 return null;
	 }
	 }
	 // Определение названия колонок
	 @Override
	 public String getColumnName(int column) {
	 switch (column) {
	 case 0:
	 return "Код";
	 case 1:
	 return "Наименование";
	
		 
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
	return java.lang.String.class;
	 else
		 return java.lang.String.class;
	 }


//Событие добавления строки
			public void addRow(Level prod) {
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


