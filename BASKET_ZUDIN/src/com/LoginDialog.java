package com;
import java.awt.Font;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Locale;
import java.util.Properties;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
public class LoginDialog extends JRDialog {
	private static final long serialVersionUID = 1L;
private JTextField txtLogin;
    //  Поля класса
    private JPasswordField txtPassword;
    private JButton btnEnter;
    private JButton btnCancel;
    private Connection conn;
    private DBManager manager;
    private String url_db;
    private static String loginn;
    private static String passwordn;
    private String currentLogin;
   //  Конструктор класса
   public LoginDialog(DBManager manager) {
    this.manager=manager;
    //createGUI();
    setTitle("Соединение с БД");
    // Чтение файла свойств проекта
    FileInputStream fprop;
    Properties property = new Properties();
    try {
      // Файл свойств  находится в корневой папке проекта
      fprop = new FileInputStream("conf.prop");
      property.load(fprop);
      //  Чтение строки соединения из файла свойств
      url_db = property.getProperty("URL_DB");
      loginn = property.getProperty("Login");
      passwordn = property.getProperty("Password");
      createGUI();
      
    } catch (IOException e) {
       System.err.println("ОШИБКА: Файл свойств отсутствует!");
    }
  }
  // Формирование графического интерфейса окна класса
  private void createGUI() {
     MigLayout layout = new MigLayout("insets 12, gapy 5",
            "[]12[grow, fill][]");
     JPanel res = new JPanel(layout);
     // поле с логином
    
     JLabel lbl = new JLabel("Логин: ");
     lbl.setFont(new Font("Arial", Font.PLAIN, 23));
     res.add(lbl);
     txtLogin = new JTextField(15);
     txtLogin.setFont(new Font("Arial", Font.PLAIN, 23));
   //  txtLogin.setText("postgres");
     txtLogin.setText(loginn);
     
     res.add(txtLogin, "span");
    // поле с паролем
     JLabel lbl2 = new JLabel("Пароль: ");
     lbl2.setFont(new Font("Arial", Font.PLAIN, 23));
     res.add(lbl2);
     txtPassword = new JPasswordField(15);
     txtPassword.setFont(new Font("Arial", Font.PLAIN, 23));
    // txtPassword.setText("parol");
     txtPassword.setText(passwordn);
     res.add(txtPassword, "span");
     // кнопки
     
     
     btnEnter = new JButton("Войти");
     btnEnter.setFont(new Font("Arial", Font.PLAIN, 23));
     btnCancel = new JButton("Отмена");
     btnCancel.setFont(new Font("Arial", Font.PLAIN, 23));
     res.add(btnEnter, "gaptop 8,span,split 2,right");
     res.add(btnCancel);
     getContentPane().add(res);
     // установка кнопки по умолчанию для окна
     getRootPane().setDefaultButton(btnEnter);
     // пакуем окно
     pack();
     // запрет изменения размеров
     setResizable(false);
     // привязка обработчиков событий - нажатия кнопок
     bindListeners();
  }
  //  Добавление слушателей для компонентов окна
  private void bindListeners() {
     //  Для кнопки отмена – закрытие окна
     btnCancel.addActionListener(new ActionListener(){
       @Override
       public void actionPerformed(ActionEvent e){
          setDialogResult(JDialogResult.Cancel);
          close();
       }
     });
     //  Для кнопки Войти – получение соединения
     btnEnter.addActionListener(new ActionListener() {
       @Override
       public void actionPerformed(ActionEvent e){
         if (authenticate()) {
            setDialogResult(JDialogResult.OK);
            close();
         }
        }
      });
     }
    //  Метод аутентификации
  protected boolean authenticate() {
    String login = null;
   char[] password = null;
    //String password = null;
    try{
      login = txtLogin.getText().trim();
      password = txtPassword.getPassword();
  //  login=loginn;
   // password=passwordn;
     if (login.length() == 0 || password.length == 0)
         throw new Exception("Логин и/или пароль не указаны.");
    }
    catch (Exception e)	{
       JOptionPane.showMessageDialog(this, e.getMessage(),
      "Ошибка ввода данных", JOptionPane.ERROR_MESSAGE);
       txtPassword.setText(null);
       return false;
     }
     // пробуем авторизоваться
     try {
       String pwd = new String(password);
       conn=connect(login, pwd);
       if(conn==null)
         throw new Exception("Логин,пароль или имя БД указаны не верно");
       manager.setConnection(conn);
       
     }
     catch (Exception e){
       JOptionPane.showMessageDialog(this, e.getMessage(),
       "Ошибка входа в систему", JOptionPane.ERROR_MESSAGE);
       txtPassword.setText(null);
       return false;
     }
    return true;
  }
  
  
  public String getCurrentLogin() {
      return currentLogin;
  }
  
  //  Метод соединения с БД
  private Connection connect(String log, String pass) {
     Locale.setDefault(Locale.ENGLISH);
     try {
       try {
          Class.forName("org.postgresql.Driver");
       } catch (ClassNotFoundException e) {
         e.printStackTrace();
       }
       // Данная строка для разных БД может отличаться
       //  Для PostgreSQL, Oracle, MySQL, MSSQL
       conn = DriverManager.getConnection(url_db,log, pass);
       //  Для Sybase SQL Anywhere
       //conn=DriverManager.getConnection(url_db+";uid="+log+";pwd="+pass);
       conn.setAutoCommit(false);
       return conn;
     } catch (SQLException e) {
         e.printStackTrace();
         return null;
     }
  }
}
