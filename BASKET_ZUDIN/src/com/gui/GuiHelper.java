package com.gui;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.*;

public class GuiHelper {
/**
* Автонастройки ширины столбцоы по содержимому
*/
public static void initColumnSizes(JTable tbl,
AbstractTableModel tblModel) {
if (tbl.getTableHeader() == null) return;
if (tbl.getRowCount()==0) return;
TableCellRenderer headerRenderer =
tbl.getTableHeader().getDefaultRenderer();
for (int i = 0; i < tblModel.getColumnCount(); i++) {
// ширина заголовка
TableColumn column = tbl.getColumnModel().getColumn(i);
Component comp = headerRenderer.getTableCellRendererComponent(null,
column.getHeaderValue(), false, false, 0, 0);
int headerWidth = comp.getPreferredSize().width;
// ширина содержимого
int cellWidth = 40;
Class<?> _class = tblModel.getColumnClass(i);
for (int j = 0; j < tblModel.getRowCount(); j++){
comp=tbl.getDefaultRenderer(_class).getTableCellRendererComponent(
tbl, tblModel.getValueAt(j, i), false, false, 0, i);
cellWidth = Math.max(comp.getPreferredSize().width, cellWidth);
}
// берем максимальное значение
column.setPreferredWidth(Math.max(headerWidth, cellWidth) + 10);
}
}
}