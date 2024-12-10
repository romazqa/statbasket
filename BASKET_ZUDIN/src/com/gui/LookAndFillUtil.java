package com.gui;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
* Класс для установки внешнего вида приложения
*/
public class LookAndFillUtil {
/**
* Установка стиля по умолчанию
*/
public static void setDefault()
{
JDialog.setDefaultLookAndFeelDecorated(true);
JFrame.setDefaultLookAndFeelDecorated(true);
}
/**
* Установки стиля JGoodies
* @param backgroundColor цвет фона
*
* @throws UnsupportedLookAndFeelException
* @throws IllegalAccessException
* @throws InstantiationException
* @throws ClassNotFoundException
*/
public static void setGoodies(final Color backgroundColor)
throws UnsupportedLookAndFeelException,
IllegalAccessException,
InstantiationException,
ClassNotFoundException
{
UIManager.setLookAndFeel(
"com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
}
}