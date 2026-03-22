package com.gui;

import com.data.*; // Импортируем все ваши модели

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GuiHelper {

    // Старые методы, если они у вас были, можно оставить или удалить,
    // если они больше не используются.

    /**
     * Универсальный метод для заполнения JTable данными из списка объектов (List).
     * <p>
     * ВАЖНО: Этот метод работает на основе ПОРЯДКА колонок и ПОРЯДКА вызова get-методов.
     * Он не смотрит на названия колонок.
     *
     * @param table      Таблица для заполнения.
     * @param objectList Список объектов для отображения.
     * @param <T>        Тип объектов в списке.
     */
	public static <T> void addObjectsToTable(JTable table, List<T> objectList) {
	    DefaultTableModel model = (DefaultTableModel) table.getModel();
	    model.setRowCount(0);

	    if (objectList == null || objectList.isEmpty()) {
	        return;
	    }

	    for (T obj : objectList) {
	        Object[] rowData = createRowDataForObject(obj, model.getColumnCount());
	        if (rowData != null) {
	            model.addRow(rowData);
	        }
	    }
	}

	private static Object[] createRowDataForObject(Object obj, int columnCount) {
	    Object[] rowData = new Object[columnCount];

	    if (obj instanceof Player) {
	        Player player = (Player) obj;
	        rowData[0] = player.getName(); // Используем getName()
	        rowData[1] = (player.getTeam() != null) ? player.getTeam().getName() : "Без команды"; // Используем getName()
	        rowData[2] = player.getGameNumber();
	        rowData[3] = player.getHeight();
	        rowData[4] = player.getWeight();

	    } else if (obj instanceof Team) {
	        Team team = (Team) obj;
	        rowData[0] = team.getName(); // Используем getName()
	        rowData[1] = team.getCity();
	        rowData[2] = team.getGender();

	    } else if (obj instanceof Matches) {
	        Matches match = (Matches) obj;
	        rowData[0] = match.getDate();
	        rowData[1] = (match.getTeam1() != null) ? match.getTeam1().getName() : "N/A"; // Используем getName()
	        rowData[2] = match.getTeam1Score();
	        rowData[3] = match.getTeam2Score();
	        rowData[4] = (match.getTeam2() != null) ? match.getTeam2().getName() : "N/A"; // Используем getName()
	        rowData[5] = match.getPlayground();

	    } else if (obj instanceof Event) {
	        Event event = (Event) obj;
	        rowData[0] = event.getName();
	        rowData[1] = event.getLocation();
	        rowData[2] = event.getYear();
	        rowData[3] = (event.getCompetitionLevel() != null) ? event.getCompetitionLevel().getName() : "N/A"; // Используем getName()

	    } else if (obj instanceof Level) {
	        Level level = (Level) obj;
	        rowData[0] = level.getName(); // Используем getName()
	    }
	    
	    return rowData;
	}
    
    // Вы можете оставить здесь и другие ваши вспомогательные методы,
    // например, для центрирования текста в ячейках.
}