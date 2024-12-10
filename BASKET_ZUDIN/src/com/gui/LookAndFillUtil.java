package com.gui;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
* ����� ��� ��������� �������� ���� ����������
*/
public class LookAndFillUtil {
/**
* ��������� ����� �� ���������
*/
public static void setDefault()
{
JDialog.setDefaultLookAndFeelDecorated(true);
JFrame.setDefaultLookAndFeelDecorated(true);
}
/**
* ��������� ����� JGoodies
* @param backgroundColor ���� ����
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