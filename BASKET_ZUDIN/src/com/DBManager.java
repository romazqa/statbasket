package com;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import com.data.*;
import com.helper.HelperConverter;

import com.rpt.RptData;
import com.rpt.RptParams;

public class DBManager {
	private Connection conn = null;

	public DBManager() throws SQLException {
	}

	public Connection getConnection() {
		return conn;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}
	

	// ------------------------------МЕТОД SEQ
	// Метод получения суррогатного ключа
	// Используется для всех последовательностей
	// Входной параметр - имя последовательности
	public BigDecimal getId(String seqName) {
		BigDecimal id = null;
		PreparedStatement pst = null;
		Connection con = this.getConnection();
		String stm = "SELECT nextval(?)";
		try {
			// Формирование запроса к БД
			// Выполнение запроса и получение набора данных
			pst = con.prepareStatement(stm);
			// seqName - имя последовательности
			pst.setString(1, seqName);
			ResultSet res = pst.executeQuery();
			while (res.next()) {
				id = res.getBigDecimal(1);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка получения идентификатора",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
		return id;
	}
	
	

	// Метод тестирования соединения
	public String getVersion() {
		String ver = null;
		Statement stmt;
		ResultSet rset;
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery("SELECT VERSION()");
			while (rset.next()) {
				ver = rset.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ver;
	}

	// установка пути
	public void setPath() {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			// имя схемы надо изменить !!!!
			stmt.executeUpdate("SET SEARCH_PATH=public");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void RollBack() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Установка путей к схемам
		setPath();
	}

	
	// ---------------------
		// ------------------ЗАГРУЗКА level
		// Получение перечня level из БД
		// и возврат его в виде списка
		public ArrayList<Level> loadLevel() {
			ArrayList<Level> grs = null;
			// Получение объекта соединения
			Connection con = this.getConnection();
			try {
				// Формирование запроса к БД
				Statement stmt = con.createStatement();
				// Выполнение запроса и получение набора данных
				ResultSet res = stmt.executeQuery("SELECT id_competition_level, "
						+ "competition_level\r\n"
						+ "	FROM public.level_event order by id_competition_level");
				// Создание объекта – список level
				grs = new ArrayList<Level>();
				// В цикле просмотра набора данных формируем список
				while (res.next()) {
					// Создаем объект Level
					Level pr = new Level();
					// Заполняем поля объекта данными из строки набора
					pr.setKod(res.getBigDecimal(1));
					pr.setName(res.getString(2));
					
					// Добавляем объект к списку
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
			}
			// Возврат списка
			return grs;
		}

		// ------------------ЗАГРУЗКА ГОРОД
		// Получение перечня ГОРОДОВ из БД
		// и возврат его в виде списка
		public ArrayList<Level> loadLevelForCmb() {
			ArrayList<Level> grs = null;
	//Получение объекта соединения
			Connection con = this.getConnection();
			try {
	//Формирование запроса к БД
				Statement stmt = con.createStatement();
	//Выполнение запроса и получение набора данных
				ResultSet res = stmt.executeQuery("SELECT id_competition_level, "
						+ "competition_level\r\n"
						+ "	FROM public.level_event order by competition_level");
			
	//Создание объекта – список 
				grs = new ArrayList<Level>();
	//В цикле просмотра набора данных формируем список
				while (res.next()) {
	//Создаем объект Level
					Level pr = new Level();
	//Заполняем поля объекта данными из строки набора
					pr.setKod(res.getBigDecimal(1));
					pr.setName(res.getString(2));
					

	//Добавляем объект к списку
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
			}
	//Возврат списка
			return grs;
		}

		// РЕДАКТИРОВАНИЕ ГОРОД
		// Выполнение оператора редактирования строки
		// Параметры:
		// dol – объект «ГОРОД» из формы редактирования
		// key – значение ключевого реквизита
		public boolean updateLevel(Level dol, BigDecimal key) {
			PreparedStatement pst = null;
			// Получение объекта соединения
			Connection con = this.getConnection();
			// Строка с текстом оператора (? – параметры)
		
			String stm = "UPDATE level_event set id_competition_level=?, competition_level=?"

					 + " WHERE id_competition_level=?";
			try {
				// Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора
				// 1- номер параметра

				pst.setBigDecimal(1, dol.getKod());
				pst.setString(2, dol.getName());

				
				pst.setBigDecimal(3, key);
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
			} catch (SQLException ex) {
				// В случае ошибки – отмена транзакции
				RollBack();
				// и вывод сообщения об ошибке
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка изменения данных", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}

		// ДОБАВЛЕНИЕ level
		// Выполнение оператора добавления строки
		public boolean addLevel(Level dol) {
			PreparedStatement pst = null;
			// Получение объекта соединения
			Connection con = this.getConnection();
			// Строка с текстом оператора (? – параметры)
			String stm = ""
					+ "INSERT INTO public.level_event(id_competition_level, competition_level)VALUES (?, ?)";
					
			try {
				// Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора

				pst.setBigDecimal(1, dol.getKod());
				pst.setString(2, dol.getName());


				
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
				return true;
			} catch (SQLException ex) {
				// В случае ошибки – отмена транзакции
				RollBack();
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка добавления данных", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		// УДАЛЕНИЕ ГОРОД
		// Выполнение оператора удаления строки
		public boolean deleteLevel(BigDecimal kod) {
			PreparedStatement pst = null;
			// Получение объекта соединения
			Connection con = this.getConnection();
			// Строка с текстом оператора (? – параметры)
			String stm = "DELETE FROM level_event" + " WHERE id_competition_level=?";
			try {
				// Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора
				pst.setBigDecimal(1, kod);
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
				return true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
		}


	

	//------------------ЗАГРУЗКА Team
	// Получение перечня TeamTeam из БД
	// и возврат его в виде списка 
		public ArrayList<Team> loadTeam() {
			ArrayList<Team> grs = null;
	//Получение объекта соединения
			Connection con = this.getConnection();
			try {
	//Формирование запроса к БД
				Statement stmt = con.createStatement();
	//Выполнение запроса и получение набора данных
				ResultSet res = stmt.executeQuery("SELECT * from Team Order by id_team");
	//Создание объекта – список объектов недв.
				grs = new ArrayList<Team>();
	//В цикле просмотра набора данных формируем список
				while (res.next()) {
	//Создаем объект Team
					Team pr = new Team();
	//Заполняем поля объекта данными из строки набора
					pr.setId_team(res.getBigDecimal(1));
					
					pr.setCity(res.getString(2));
					pr.setTeam_name(res.getString(3));
					
					pr.setGender_team(res.getString(4));

	//Добавляем объект к списку
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
			}
	//Возврат списка
			return grs;
		}
		//------------------ЗАГРУЗКА Team
		// Получение перечня TeamTeam из БД
		// и возврат его в виде списка 
			public ArrayList<Team> loadTeamForCmb() {
				ArrayList<Team> grs = null;
		//Получение объекта соединения
				Connection con = this.getConnection();
				try {
		//Формирование запроса к БД
					Statement stmt = con.createStatement();
		//Выполнение запроса и получение набора данных
					ResultSet res = stmt.executeQuery("SELECT * from Team Order by team_name");
		//Создание объекта – список объектов недв.
					grs = new ArrayList<Team>();
		//В цикле просмотра набора данных формируем список
					while (res.next()) {
		//Создаем объект Team
						Team pr = new Team();
		//Заполняем поля объекта данными из строки набора
						pr.setId_team(res.getBigDecimal(1));
						
						pr.setCity(res.getString(2));
						pr.setTeam_name(res.getString(3));
						
						pr.setGender_team(res.getString(4));

		//Добавляем объект к списку
						grs.add(pr);
					}
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
				}
		//Возврат списка
				return grs;
			}
		// РЕДАКТИРОВАНИЕ ОБЪЕКТ НЕДВИЖИМОСТИ
	//Выполнение оператора редактирования строки
	//Параметры:
	//dol – объект «ОБЪЕКТ НЕДВИЖИМОСТИ» из формы редактирования
	//key – значение ключевого реквизита
		public boolean updateTeam(Team dol, BigDecimal key) {
			PreparedStatement pst = null;
			// Получение объекта соединения
			Connection con = this.getConnection();
	//  Строка с текстом оператора (? – параметры)
			String stm = "UPDATE Team set " + "id_Team=?,"
	+ "city=?," + "team_name=?," + "gender_team=?"+ " WHERE id_Team=?";
			try {
				// Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора
				// 1- номер параметра

				pst.setBigDecimal(1, dol.getId_team());
				
				pst.setString(2, dol.getCity());
				pst.setString(3, dol.getTeam_name());
				pst.setString(4, dol.getGender_team());
				pst.setBigDecimal(5, key);
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
			} catch (SQLException ex) {
	//В случае ошибки – отмена транзакции 
				RollBack();
				// и вывод сообщения об ошибке
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка изменения данных", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}

	//ДОБАВЛЕНИЕ team
		// Выполнение оператора добавления строки
		public boolean addTeam(Team dol) {
			PreparedStatement pst = null;
	//Получение объекта соединения
			Connection con = this.getConnection();
			// Строка с текстом оператора (? – параметры)
			String stm = "INSERT INTO Team(id_Team,"
					+ " city, team_name, gender_team)"
					+ "  VALUES(?,?,?,?)";
			try {
				// Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора

	pst.setBigDecimal(1, dol.getId_team());
				
				pst.setString(2, dol.getCity());
				pst.setString(3, dol.getTeam_name());
				pst.setString(4, dol.getGender_team());
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
				return true;
			} catch (SQLException ex) {
				// В случае ошибки – отмена транзакции
				RollBack();
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка добавления данных", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

	//УДАЛЕНИЕ ОБЪЕКТ team
		// Выполнение оператора удаления строки
		public boolean deleteTeam(BigDecimal kod) {
			PreparedStatement pst = null;
	//Получение объекта соединения
			Connection con = this.getConnection();
			// Строка с текстом оператора (? – параметры)
			String stm = "DELETE FROM Team" + " WHERE id_Team=?";
			try {
	//Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора
				pst.setBigDecimal(1, kod);
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
				return true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		
		public ArrayList<Event> loadEventt() {
			ArrayList<Event> grs = null;
	//Получение объекта соединения
			Connection con = this.getConnection();
			try {
	//Формирование запроса к БД
				Statement stmt = con.createStatement();
	//Выполнение запроса и получение набора данных
				ResultSet res = stmt.executeQuery(
						"SELECT * from Eventt_v");
	//Создание объекта – список 
				grs = new ArrayList<Event>();
	//В цикле просмотра набора данных формируем список
				while (res.next()) {
	//Создаем объект Eventt
					Event pr = new Event();
	//Заполняем поля объекта данными из строки набора
					pr.setId_event(res.getBigDecimal(1));
					
					pr.setName_event(res.getString(2));
					pr.setLocationn(res.getString(3));
					pr.setYear_event(res.getBigDecimal(4));
					Level prr = new Level();
					prr.setKod(res.getBigDecimal(5));
					prr.setName(res.getString(6));
					
					pr.setLevel(prr);

					
				

	//Добавляем объект к списку
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
			}
	//Возврат списка
			return grs;
		}
		public ArrayList<Event> loadEventtForCmb() {
			ArrayList<Event> grs = null;
	//Получение объекта соединения
			Connection con = this.getConnection();
			try {
	//Формирование запроса к БД
				Statement stmt = con.createStatement();
	//Выполнение запроса и получение набора данных
				ResultSet res = stmt.executeQuery(
						"SELECT * from Eventt_v order by Name_event");
	//Создание объекта – список 
				grs = new ArrayList<Event>();
	//В цикле просмотра набора данных формируем список
				while (res.next()) {
	//Создаем объект Eventt
					Event pr = new Event();
	//Заполняем поля объекта данными из строки набора
					pr.setId_event(res.getBigDecimal(1));
					
					pr.setName_event(res.getString(2));
					pr.setLocationn(res.getString(3));
					pr.setYear_event(res.getBigDecimal(4));
					Level prr = new Level();
					prr.setKod(res.getBigDecimal(5));
					prr.setName(res.getString(6));
					
					pr.setLevel(prr);

					
				

	//Добавляем объект к списку
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
			}
	//Возврат списка
			return grs;
		}

		// РЕДАКТИРОВАНИЕ eve
	// Выполнение оператора редактирования строки
	//  Параметры:
	//   dol – объект «» из формы редактирования
	//   key – значение ключевого реквизита
		public boolean updateEventt(Event dol, BigDecimal key) {
			PreparedStatement pst = null;
			// Получение объекта соединения
			Connection con = this.getConnection();
			// Строка с текстом оператора (? – параметры)
			String stm = "UPDATE public.eventt\r\n"
					+ "	SET id_event=?, name_event=?, locationn=?, year_event=?, id_competition_level=?\r\n"
					+ "	WHERE id_event=?";
			try {
				// Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора
				// 1- номер параметра

				pst.setBigDecimal(1, dol.getId_event());
				pst.setString(2, dol.getName_event());
				pst.setString(3, dol.getLocationn());
				

				pst.setBigDecimal(4, dol.getYear_event());
				pst.setBigDecimal(5, dol.getLevel().getKod());
				pst.setBigDecimal(6, key);
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
			} catch (SQLException ex) {
	// В случае ошибки – отмена транзакции 
				RollBack();
				// и вывод сообщения об ошибке
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка изменения данных", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}

	//ДОБАВЛЕНИЕ 
		// Выполнение оператора добавления строки
		public boolean addEventt(Event dol) {
			PreparedStatement pst = null;
	//  Получение объекта соединения
			Connection con = this.getConnection();
			// Строка с текстом оператора (? – параметры)
			String stm = ""
					+ "INSERT INTO public.eventt(\r\n"
					+ "	id_event, name_event, locationn, year_event, id_competition_level)\r\n"
					+ "	VALUES (?, ?, ?, ?, ?);";
			try {
				// Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора

				pst.setBigDecimal(1, dol.getId_event());
				pst.setString(2, dol.getName_event());
				pst.setString(3, dol.getLocationn());
				

				pst.setBigDecimal(4, dol.getYear_event());
				pst.setBigDecimal(5, dol.getLevel().getKod());
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
				return true;
			} catch (SQLException ex) {
				// В случае ошибки – отмена транзакции
				RollBack();
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка добавления данных", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

	//УДАЛЕНИЕ ev
		// Выполнение оператора удаления строки
		public boolean deleteEventt(BigDecimal kod) {
			PreparedStatement pst = null;
	//  Получение объекта соединения
			Connection con = this.getConnection();
			// Строка с текстом оператора (? – параметры)
			String stm = "DELETE FROM Eventt" + " WHERE id_event=?";
			try {
	//  Создание объекта «Оператор с параметрами»
				pst = con.prepareStatement(stm);
				// Задаем значения параметров оператора
				pst.setBigDecimal(1, kod);
				// Выполнение оператора
				pst.executeUpdate();
				// Завершение транзакции – сохранение изменений
				con.commit();
				return true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			} finally {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		// ------------------ЗАГРУЗКА ma
		// Получение перечня  из БД
		// и возврат его в виде списка 
			public ArrayList<Matches> loadMatches() {
				ArrayList<Matches> grs = null;
		//Получение объекта соединения
				Connection con = this.getConnection();
				try {
		//Формирование запроса к БД
					Statement stmt = con.createStatement();
		//Выполнение запроса и получение набора данных
					ResultSet res = stmt.executeQuery(
							"SELECT id_matches, date_matches, team1score,"
							+ " team2score, id_event, name_event, id_team1, "
							+ "team_name, id_team2, team_name2, "
							+ "playground from Matches_v order by id_matches");
		//Создание объекта – список заявок
					grs = new ArrayList<Matches>();
		//В цикле просмотра набора данных формируем список
					while (res.next()) {
		//Создаем объект Matches
						Matches pr = new Matches();
		//Заполняем поля объекта данными из строки набора
						pr.setId_matches(res.getBigDecimal(1));
						pr.setDate_matches(res.getDate(2));
						pr.setTeam1score(res.getBigDecimal(3));
						pr.setTeam2score(res.getBigDecimal(4));
						
						Event kl = new Event();

						kl.setId_event(res.getBigDecimal(5));
						kl.setName_event(res.getString(6));
						pr.setEvent(kl);
						
						Team kl2 = new Team();

						kl2.setId_team(res.getBigDecimal(7));
						kl2.setTeam_name(res.getString(8));
						pr.setTeam1(kl2);
						Team kl3 = new Team();

						kl3.setId_team(res.getBigDecimal(9));
						kl3.setTeam_name(res.getString(10));
						pr.setTeam2(kl3);
						pr.setPlayground(res.getString(11));
		//Добавляем объект к списку
						grs.add(pr);
					}
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
				}
		//Возврат списка
				return grs;
			}

			
		//Выполнение оператора редактирования строки
		//Параметры:
		//dol – объект «m» из формы редактирования
		//key – значение ключевого реквизита
			public boolean updateMatches(Matches dol, BigDecimal key) {
				PreparedStatement pst = null;
				// Получение объекта соединения
				Connection con = this.getConnection();
		//  Строка с текстом оператора (? – параметры)
				String stm = "UPDATE public.matches\r\n"
						+ "	SET id_matches=?, "
						+ "date_matches=?, team1score=?, team2score=?, "
						+ "id_event=?, id_team1=?, id_team2=?, playground=?\r\n"
						+ "WHERE id_matches=?";
				try {
					// Создание объекта «Оператор с параметрами»
					pst = con.prepareStatement(stm);
					// Задаем значения параметров оператора
					// 1- номер параметра

					pst.setBigDecimal(1, dol.getId_matches());
					pst.setBigDecimal(3, dol.getTeam1score());
					pst.setDate(2, HelperConverter.convertFromJavaDateToSQLDate(dol.getDate_matches()));
					pst.setBigDecimal(4, dol.getTeam2score());
					
					pst.setBigDecimal(5, dol.getEvent().getId_event());

					
					pst.setBigDecimal(6, dol.getTeam1().getId_team());
					pst.setBigDecimal(7, dol.getTeam2().getId_team());
					pst.setString(8, dol.getPlayground());
					pst.setBigDecimal(9, key);
					// Выполнение оператора
					pst.executeUpdate();
					// Завершение транзакции – сохранение изменений
					con.commit();
				} catch (SQLException ex) {
		//В случае ошибки – отмена транзакции 
					RollBack();
					// и вывод сообщения об ошибке
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка изменения данных", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
					return false;
				} finally {
					try {
						pst.close();
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					}
				}
				return true;
			}

		//ДОБАВЛЕНИЕ mayc
			// Выполнение оператора добавления строки
			public boolean addMatches(Matches dol) {
				PreparedStatement pst = null;
		//Получение объекта соединения
				Connection con = this.getConnection();
				// Строка с текстом оператора (? – параметры)
				String stm = "INSERT INTO public.matches(\r\n"
						+ "	id_matches, date_matches, team1score, team2score, id_event, id_team1, id_team2, playground)\r\n"
						+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				try {
					// Создание объекта «Оператор с параметрами»
					pst = con.prepareStatement(stm);
					// Задаем значения параметров оператора
					dol.setId_matches(getId("m_seq"));
					pst.setBigDecimal(1, dol.getId_matches());
					pst.setBigDecimal(3, dol.getTeam1score());
					pst.setDate(2, HelperConverter.convertFromJavaDateToSQLDate(dol.getDate_matches()));
					pst.setBigDecimal(4, dol.getTeam2score());
					
					pst.setBigDecimal(5, dol.getEvent().getId_event());

					
					pst.setBigDecimal(6, dol.getTeam1().getId_team());
					pst.setBigDecimal(7, dol.getTeam2().getId_team());
					pst.setString(8, dol.getPlayground());
					// Выполнение оператора
					pst.executeUpdate();
					// Завершение транзакции – сохранение изменений
					con.commit();
					return true;
				} catch (SQLException ex) {
					// В случае ошибки – отмена транзакции
					RollBack();
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка добавления данных", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
					return false;
				} finally {
					try {
						pst.close();
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					}
				}
			}

		//УДАЛЕНИЕ m
			// Выполнение оператора удаления строки
			public boolean deleteMatches(BigDecimal kod) {
				PreparedStatement pst = null;
		//Получение объекта соединения
				Connection con = this.getConnection();
				// Строка с текстом оператора (? – параметры)
				String stm = "DELETE FROM Matches" + " WHERE id_matches=?";
				try {
		//Создание объекта «Оператор с параметрами»
					pst = con.prepareStatement(stm);
					// Задаем значения параметров оператора
					pst.setBigDecimal(1, kod);
					// Выполнение оператора
					pst.executeUpdate();
					// Завершение транзакции – сохранение изменений
					con.commit();
					return true;
				} catch (SQLException ex) {
					ex.printStackTrace();
					return false;
				} finally {
					try {
						pst.close();
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					}
				}
			}

			//------------------ЗАГРУЗКА КНИГА УЧЁТА
			//Получение перечня записей книги учёта из БД
			//и возврат его в виде списка 
				public ArrayList<Stat> loadStat(BigDecimal ff) {
					ArrayList<Stat> grs = null;
			//Получение объекта соединения
					Connection con = this.getConnection();
					try {
			//Формирование запроса к БД
						Statement stmt = con.createStatement();
						ResultSet res;
			//Выполнение запроса и получение набора данных
					
							res = stmt.executeQuery(
									"SELECT * from Ps_v where id_matches=" + ff);

						
			//Создание объекта – список записей книги учёта
						grs = new ArrayList<Stat>();
			//В цикле просмотра набора данных формируем список
						while (res.next()) {
			//Создаем объект Player_Stats
							Stat stats = new Stat();
			//Заполняем поля объекта данными из строки набора
							
							 stats.setIdPlayerStats(res.getBigDecimal(1));
						        stats.setPointScored(res.getInt(2));
						        stats.setAssists(res.getInt(3));
						        stats.setSteal(res.getInt(4));
						        stats.setTurnover(res.getInt(5));
						        stats.setBlockedShot(res.getInt(6));
						        stats.setFoul(res.getInt(7));
						        stats.setDoubleDouble(res.getInt(8));
						        stats.setTriple(res.getInt(9));
						        stats.setFreeThrow(res.getInt(10));
						        stats.setDr(res.getInt(11));
						        stats.setOr(res.getInt(12));
						      
						        stats.setIdMatch(res.getBigDecimal(15));
							

							Player onn = new Player();
							onn.setId(res.getBigDecimal(13));
							onn.setName(res.getString(14));
						
							Team team = loadTeamForPlayer(onn.getId()); // !!!  Добавляем новый метод
				            onn.setTeam(team);
							

							stats.setPlayer(onn);
							

			//Добавляем объект к списку
							grs.add(stats);
						}
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
					}
			//Возврат списка
					return grs;
				}

				// РЕДАКТИРОВАНИЕ КНИГА УЧЁТА
				//Выполнение оператора редактирования строки
				//Параметры:
				//dol – объект «КНИГА УЧЁТА» из формы редактирования
				//key – значение ключевого реквизита
					public boolean updateStat(Stat playerStats, BigDecimal key) {
						PreparedStatement pst = null;
						// Получение объекта соединения
						Connection con = this.getConnection();
				//Строка с текстом оператора (? – параметры)
						String stm = "UPDATE public.player_stats\r\n"
								+ "	SET id_playerstats=?, "
								+ "pointscored=?, assists=?, steal=?, turnover=?, blocked_shot=?, foul=?, double=?, triple=?, free_throw=?, defensive_rebound=?, offensive_rebound=?, id_player=?, id_matches=?\r\n"
								+ "	WHERE id_playerstats=?";
						try {
							// Создание объекта «Оператор с параметрами»
							pst = con.prepareStatement(stm);
							// Задаем значения параметров оператора
							// 1- номер параметра
							pst.setBigDecimal(1, playerStats.getIdPlayerStats());
							pst.setInt(2, playerStats.getPointScored());
							pst.setInt(3, playerStats.getAssists());
							pst.setInt(4, playerStats.getSteal());
							pst.setInt(5, playerStats.getTurnover());
							pst.setInt(6, playerStats.getBlockedShot());
							pst.setInt(7, playerStats.getFoul());
							pst.setInt(8, playerStats.getDoubleDouble());
							pst.setInt(9, playerStats.getTriple());
							pst.setInt(10, playerStats.getFreeThrow());
							pst.setInt(11, playerStats.getDr());
							pst.setInt(12, playerStats.getOr());
							pst.setBigDecimal(13, playerStats.getPlayer().getId());
							pst.setBigDecimal(14, playerStats.getIdMatch());

							pst.setBigDecimal(15, key);
							// Выполнение оператора
							pst.executeUpdate();
							// Завершение транзакции – сохранение изменений
							con.commit();
						} catch (SQLException ex) {
				//В случае ошибки – отмена транзакции 
							RollBack();
							// и вывод сообщения об ошибке
							JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка изменения данных", JOptionPane.ERROR_MESSAGE);
							ex.printStackTrace();
							return false;
						} finally {
							try {
								pst.close();
							} catch (SQLException e) {
								e.printStackTrace();
								return false;
							}
						}
						return true;
					}

				//ДОБАВЛЕНИЕ КНИГА УЧЁТА
					// Выполнение оператора добавления строки
					public boolean addStat(Stat playerStats) {
						PreparedStatement pst = null;
				//Получение объекта соединения
						Connection con = this.getConnection();
						// Строка с текстом оператора (? – параметры)
						String stm = "INSERT INTO public.player_stats("
								
								+ "	id_playerstats, pointscored, "
								+ "assists, steal, turnover, blocked_shot,"
								+ " foul, double, triple, free_throw,"
								+ " defensive_rebound, offensive_rebound,"
								+ " id_player, id_matches)"
								+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
							System.out.print(stm);
						try {
							// Создание объекта «Оператор с параметрами»
							pst = con.prepareStatement(stm);
							// Задаем значения параметров оператора
							playerStats.setIdPlayerStats(getId("ps_seq"));
							pst.setBigDecimal(1, playerStats.getIdPlayerStats());
							pst.setInt(2, playerStats.getPointScored());
							pst.setInt(3, playerStats.getAssists());
							pst.setInt(4, playerStats.getSteal());
							pst.setInt(5, playerStats.getTurnover());
							pst.setInt(6, playerStats.getBlockedShot());
							pst.setInt(7, playerStats.getFoul());
							pst.setInt(8, playerStats.getDoubleDouble());
							pst.setInt(9, playerStats.getTriple());
							pst.setInt(10, playerStats.getFreeThrow());
							pst.setInt(11, playerStats.getDr());
							pst.setInt(12, playerStats.getOr());
							pst.setBigDecimal(13, playerStats.getPlayer().getId());
							pst.setBigDecimal(14, playerStats.getIdMatch());
							// Выполнение оператора
							pst.executeUpdate();
							// Завершение транзакции – сохранение изменений
							con.commit();
							return true;
						} catch (SQLException ex) {
							// В случае ошибки – отмена транзакции
							RollBack();
							JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка добавления данных", JOptionPane.ERROR_MESSAGE);
							ex.printStackTrace();
							return false;
						} finally {
							try {
								pst.close();
							} catch (SQLException e) {
								e.printStackTrace();
								return false;
							}
						}
					}

				//УДАЛЕНИЕ КНИГА УЧЁТА
					// Выполнение оператора удаления строки
					public boolean deleteStat(BigDecimal kod) {
						PreparedStatement pst = null;
				//Получение объекта соединения
						Connection con = this.getConnection();
						// Строка с текстом оператора (? – параметры)
						String stm = "DELETE FROM Player_stats" + " WHERE id_Playerstats=?";
						try {
				//Создание объекта «Оператор с параметрами»
							pst = con.prepareStatement(stm);
							// Задаем значения параметров оператора
							pst.setBigDecimal(1, kod);
							// Выполнение оператора
							pst.executeUpdate();
							// Завершение транзакции – сохранение изменений
							con.commit();
							return true;
						} catch (SQLException ex) {
							ex.printStackTrace();
							return false;
						} finally {
							try {
								pst.close();
							} catch (SQLException e) {
								e.printStackTrace();
								return false;
							}
						}
					}

					public ArrayList<Player> loadPlayerForCmb() {
						ArrayList<Player> grs = null;
				//Получение объекта соединения
						Connection con = this.getConnection();
						try {
				//Формирование запроса к БД
							Statement stmt = con.createStatement();
				//Выполнение запроса и получение набора данных
							ResultSet res = stmt.executeQuery(
									"SELECT * from player order by player_name");
				//Создание объекта – список 
							grs = new ArrayList<Player>();
				//В цикле просмотра набора данных формируем список
							while (res.next()) {
				//Создаем объект Eventt
								Player pr = new Player();
				//Заполняем поля объекта данными из строки набора
								pr.setId(res.getBigDecimal(1));
								
								pr.setName(res.getString(3));
								
								
							
								
							

				//Добавляем объект к списку
								grs.add(pr);
							}
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
						}
				//Возврат списка
						return grs;
					}

					//------------------ЗАГРУЗКА 
					// Получение перечня Объектов  из БД
					// и возврат его в виде списка 
						public ArrayList<Player> loadPlayer() {
							ArrayList<Player> grs = null;
					//Получение объекта соединения
							Connection con = this.getConnection();
							try {
					//Формирование запроса к БД
								Statement stmt = con.createStatement();
					//Выполнение запроса и получение набора данных
								ResultSet res = stmt.executeQuery("SELECT id_player,"
										+ " birthday, player_name, height, weight, "
										+ "role, id_team, team_name, game_number, gender\r\n"
										+ "	FROM public.player_v order by id_player");
									
					//Создание объекта – список объектов недв.
								grs = new ArrayList<Player>();
					//В цикле просмотра набора данных формируем список
								while (res.next()) {
					//Создаем объект Player
									Player pr = new Player();
					//Заполняем поля объекта данными из строки набора
									pr.setId(res.getBigDecimal(1));
									pr.setBirthday(res.getDate(2));
									pr.setName(res.getString(3));
									
									pr.setHeight(res.getBigDecimal(4));
									pr.setWeight(res.getBigDecimal(5));
									pr.setRole(res.getString(6));
									Team g = new Team();
									g.setId_team(res.getBigDecimal(7));
									g.setTeam_name(res.getString(8));
									pr.setTeam(g);
									
									pr.setNum(res.getBigDecimal(9));
									
									pr.setGender(res.getString(10));
								

					//Добавляем объект к списку
									grs.add(pr);
								}
							} catch (SQLException e) {
								JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
							}
					//Возврат списка
							return grs;
						}

						// РЕДАКТИРОВАНИЕ ОБЪЕКТ НЕДВИЖИМОСТИ
					//Выполнение оператора редактирования строки
					//Параметры:
					//dol – объект «ОБЪЕКТ НЕДВИЖИМОСТИ» из формы редактирования
					//key – значение ключевого реквизита
						public boolean updatePlayer(Player dol, BigDecimal key) {
							PreparedStatement pst = null;
							// Получение объекта соединения
							Connection con = this.getConnection();
					//  Строка с текстом оператора (? – параметры)
							String stm = "UPDATE public.player\r\n"
									+ "	SET id_player=?, birthday=?, "
									+ "player_name=?, height=?, weight=?, role=?, "
									+ "id_team=?, game_number=?, gender=?\r\n"
									+ "	WHERE id_player=?";
							try {
								// Создание объекта «Оператор с параметрами»
								pst = con.prepareStatement(stm);
								// Задаем значения параметров оператора
								// 1- номер параметра

								pst.setBigDecimal(1, dol.getId());
								pst.setDate(2, HelperConverter.
										convertFromJavaDateToSQLDate(dol.getBirthday()));
								pst.setString(3, dol.getName());
								pst.setBigDecimal(4, dol.getHeight());
								pst.setBigDecimal(5, dol.getWeight());
								
								pst.setString(6, dol.getRole());
								pst.setBigDecimal(7, dol.getTeam().getId_team());
								pst.setBigDecimal(8, dol.getNum());
								pst.setString(9, dol.getGender());
								pst.setBigDecimal(10, key);
								
								
								// Выполнение оператора
								pst.executeUpdate();
								// Завершение транзакции – сохранение изменений
								con.commit();
							} catch (SQLException ex) {
					//В случае ошибки – отмена транзакции 
								RollBack();
								// и вывод сообщения об ошибке
								JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка изменения данных", JOptionPane.ERROR_MESSAGE);
								ex.printStackTrace();
								return false;
							} finally {
								try {
									pst.close();
								} catch (SQLException e) {
									e.printStackTrace();
									return false;
								}
							}
							return true;
						}

					//ДОБАВЛЕНИЕ ОБЪЕКТ НЕДВИЖИМОСТИ
						// Выполнение оператора добавления строки
						public boolean addPlayer(Player dol) {
							PreparedStatement pst = null;
					//Получение объекта соединения
							Connection con = this.getConnection();
							// Строка с текстом оператора (? – параметры)
							String stm = "INSERT INTO public.player(\r\n"
									+ "	id_player, birthday, player_name, height, weight, role, id_team, game_number, gender)\r\n"
									+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
							try {
								// Создание объекта «Оператор с параметрами»
								pst = con.prepareStatement(stm);
								// Задаем значения параметров оператора
								dol.setId(getId("pl_seq"));
								pst.setBigDecimal(1, dol.getId());
								pst.setDate(2, HelperConverter.
										convertFromJavaDateToSQLDate(dol.getBirthday()));
								pst.setString(3, dol.getName());
								pst.setBigDecimal(4, dol.getHeight());
								pst.setBigDecimal(5, dol.getWeight());
								
								pst.setString(6, dol.getRole());
								pst.setBigDecimal(7, dol.getTeam().getId_team());
								pst.setBigDecimal(8, dol.getNum());
								pst.setString(9, dol.getGender());
								// Выполнение оператора
								pst.executeUpdate();
								// Завершение транзакции – сохранение изменений
								con.commit();
								return true;
							} catch (SQLException ex) {
								// В случае ошибки – отмена транзакции
								RollBack();
								JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка добавления данных", JOptionPane.ERROR_MESSAGE);
								ex.printStackTrace();
								return false;
							} finally {
								try {
									pst.close();
								} catch (SQLException e) {
									e.printStackTrace();
									return false;
								}
							}
						}

					//УДАЛЕНИЕ ОБЪЕКТ НЕДВИЖИМОСТИ
						// Выполнение оператора удаления строки
						public boolean deletePlayer(BigDecimal kod) {
							PreparedStatement pst = null;
					//Получение объекта соединения
							Connection con = this.getConnection();
							// Строка с текстом оператора (? – параметры)
							String stm = "DELETE FROM Player" + " "
									+ "WHERE id_player=?";
							try {
					//Создание объекта «Оператор с параметрами»
								pst = con.prepareStatement(stm);
								// Задаем значения параметров оператора
								pst.setBigDecimal(1, kod);
								// Выполнение оператора
								pst.executeUpdate();
								// Завершение транзакции – сохранение изменений
								con.commit();
								return true;
							} catch (SQLException ex) {
								ex.printStackTrace();
								return false;
							} finally {
								try {
									pst.close();
								} catch (SQLException e) {
									e.printStackTrace();
									return false;
								}
							}
						}
						
						public Stat loadStatForPlayerAndMatch(BigDecimal playerId, BigDecimal matchId) {
					        Stat playerStats = null;
					        PreparedStatement pst = null;
					        Connection con = this.getConnection();
					        String stm = "SELECT * FROM player_stats WHERE id_player = ? AND id_matches = ?";

					        try {
					            pst = con.prepareStatement(stm);
					            pst.setBigDecimal(1, playerId);
					            pst.setBigDecimal(2, matchId);
					            ResultSet res = pst.executeQuery();

					            if (res.next()) {
					                playerStats = new Stat();
					                playerStats.setIdPlayerStats(res.getBigDecimal("id_playerstats"));
					                playerStats.setPointScored(res.getInt("pointscored"));
					                playerStats.setAssists(res.getInt("assists"));
					                playerStats.setSteal(res.getInt("steal"));
					                playerStats.setTurnover(res.getInt("turnover"));
					                playerStats.setBlockedShot(res.getInt("blocked_shot"));
					                playerStats.setFoul(res.getInt("foul"));
					                playerStats.setDoubleDouble(res.getInt("double"));
					                playerStats.setTriple(res.getInt("triple"));
					                playerStats.setFreeThrow(res.getInt("free_throw"));
					                playerStats.setDr(res.getInt("defensive_rebound"));
					                playerStats.setOr(res.getInt("offensive_rebound"));
					                playerStats.setIdPlayer(res.getInt("id_player"));
					                playerStats.setIdMatch(res.getBigDecimal("id_matches"));

					                Player player = loadPlayerById(playerId);
					                playerStats.setPlayer(player);
					            }
					        } catch (SQLException ex) {
					            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка получения данных статистики", JOptionPane.ERROR_MESSAGE);
					            ex.printStackTrace();
					        } finally {
					            try {
					                if (pst != null) {
					                    pst.close();
					                }
					            } catch (SQLException e) {
					                e.printStackTrace();
					            }
					        }

					        return playerStats;
					    }

					    public boolean saveStat(Stat stat) {
					        PreparedStatement pst = null;
					        Connection con = this.getConnection();
					        String stm;

					        try {
					            if (stat.getIdPlayerStats() == null) {
					                // Добавление новой записи
					                stm = "INSERT INTO player_stats (id_playerstats, pointscored, assists, steal, turnover, blocked_shot, foul, double, triple, free_throw, defensive_rebound, offensive_rebound, id_player, id_matches)" +
					                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					                pst = con.prepareStatement(stm);
					                stat.setIdPlayerStats(getId("ps_seq")); 
					                pst.setBigDecimal(1, stat.getIdPlayerStats());
					                pst.setInt(2, stat.getPointScored());
					                pst.setInt(3, stat.getAssists());
					                pst.setInt(4, stat.getSteal());
					                pst.setInt(5, stat.getTurnover());
					                pst.setInt(6, stat.getBlockedShot());
					                pst.setInt(7, stat.getFoul());
					                pst.setInt(8, stat.getDoubleDouble());
					                pst.setInt(9, stat.getTriple());
					                pst.setInt(10, stat.getFreeThrow());
					                pst.setInt(11, stat.getDr());
					                pst.setInt(12, stat.getOr());
					                pst.setInt(13, stat.getIdPlayer());
					                pst.setBigDecimal(14, stat.getIdMatch());
					            } else {
					                // Обновление существующей записи
					                stm = "UPDATE player_stats SET pointscored = ?, assists = ?, steal = ?, turnover = ?, blocked_shot = ?, foul = ?, double = ?, triple = ?, free_throw = ?, defensive_rebound = ?, offensive_rebound = ?" +
					                        " WHERE id_playerstats = ?";
					                pst = con.prepareStatement(stm);
					                pst.setInt(1, stat.getPointScored());
					                pst.setInt(2, stat.getAssists());
					                pst.setInt(3, stat.getSteal());
					                pst.setInt(4, stat.getTurnover());
					                pst.setInt(5, stat.getBlockedShot());
					                pst.setInt(6, stat.getFoul());
					                pst.setInt(7, stat.getDoubleDouble());
					                pst.setInt(8, stat.getTriple());
					                pst.setInt(9, stat.getFreeThrow());
					                pst.setInt(10, stat.getDr());
					                pst.setInt(11, stat.getOr());
					                pst.setBigDecimal(12, stat.getIdPlayerStats());
					            }

					            pst.executeUpdate();
					            con.commit();
					            return true;
					        } catch (SQLException ex) {
					            RollBack();
					            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка сохранения статистики", JOptionPane.ERROR_MESSAGE);
					            ex.printStackTrace();
					            return false;
					        } finally {
					            try {
					                if (pst != null) {
					                    pst.close();
					                }
					            } catch (SQLException e) {
					                e.printStackTrace();
					            }
					        }
					    }

					    private Player loadPlayerById(BigDecimal playerId) {
					        Player player = null;
					        PreparedStatement pst = null;
					        Connection con = this.getConnection();
					        String stm = "SELECT * FROM player WHERE id_player = ?";

					        try {
					            pst = con.prepareStatement(stm);
					            pst.setBigDecimal(1, playerId);
					            ResultSet res = pst.executeQuery();

					            if (res.next()) {
					                player = new Player();
					                player.setId(res.getBigDecimal("id_player"));
					                player.setBirthday(res.getDate("birthday"));
					                player.setName(res.getString("player_name"));
					                player.setHeight(res.getBigDecimal("height"));
					                player.setWeight(res.getBigDecimal("weight"));
					                player.setRole(res.getString("role"));
					                // Загрузите команду для игрока, если это необходимо
					                BigDecimal teamId = res.getBigDecimal("id_team");
					                if (teamId != null) {
					                    Team team = loadTeamById(teamId);
					                    player.setTeam(team);
					                }
					                player.setNum(res.getBigDecimal("game_number"));
					                player.setGender(res.getString("gender"));
					            }
					        } catch (SQLException ex) {
					            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка получения данных игрока", JOptionPane.ERROR_MESSAGE);
					            ex.printStackTrace();
					        } finally {
					            try {
					                if (pst != null) {
					                    pst.close();
					                }
					            } catch (SQLException e) {
					                e.printStackTrace();
					            }
					        }

					        return player;
					    }
					    private Team loadTeamById(BigDecimal teamId) {
					        Team team = null;
					        PreparedStatement pst = null;
					        Connection con = this.getConnection();
					        String stm = "SELECT * FROM team WHERE id_team = ?";

					        try {
					            pst = con.prepareStatement(stm);
					            pst.setBigDecimal(1, teamId);
					            ResultSet res = pst.executeQuery();

					            if (res.next()) {
					                team = new Team();
					                team.setId_team(res.getBigDecimal("id_team"));
					                team.setCity(res.getString("city"));
					                team.setTeam_name(res.getString("team_name"));
					                team.setGender_team(res.getString("gender_team"));
					            }
					        } catch (SQLException ex) {
					            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка получения данных команды", JOptionPane.ERROR_MESSAGE);
					            ex.printStackTrace();
					        } finally {
					            try {
					                if (pst != null) {
					                    pst.close();
					                }
					            } catch (SQLException e) {
					                e.printStackTrace();
					            }
					        }

					        return team;
					    }
					    
					    private Team loadTeamForPlayer(BigDecimal playerId) {
					        PreparedStatement pst = null;
					        Connection con = this.getConnection();
					        String stm = "SELECT t.* FROM team t " +
					                     "JOIN player p ON t.id_team = p.id_team " +
					                     "WHERE p.id_player = ?";

					        try {
					            pst = con.prepareStatement(stm);
					            pst.setBigDecimal(1, playerId);
					            ResultSet res = pst.executeQuery();
					            if (res.next()) {
					                Team team = new Team();
					                team.setId_team(res.getBigDecimal("id_team"));
					                team.setCity(res.getString("city"));
					                team.setTeam_name(res.getString("team_name"));
					                team.setGender_team(res.getString("gender_team"));
					                return team;
					            }
					        } catch (SQLException e) {
					            JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
					        }
					        return null;
					    }
					    


					    
					    
	public ArrayList<RptData> getDataReport(RptParams params) {
		// Получение объекта соединения с БД
		Connection con = this.getConnection();
		// Создание объекта запроса к БД
		PreparedStatement pst = null;
		// Текст запроса к БД
		String stm = "SELECT * FROM get_client_info(?::date,?::date)";
		try {
			pst = con.prepareStatement(stm);
			// Задание значений параметров процедуры
			if (params.StartDate == null)
				pst.setDate(1, null);
			else
				pst.setDate(1, HelperConverter.convertFromJavaDateToSQLDate(params.StartDate));
			if (params.EndDate == null)
				pst.setDate(2, null);
			else
				pst.setDate(2, HelperConverter.convertFromJavaDateToSQLDate(params.EndDate));
			// Выполнение запроса к БД
			ResultSet res = pst.executeQuery();
			// Создание коллекции объектов базовой сущности
			ArrayList<RptData> rptData = new ArrayList<>();
			// Цикл формирования источника данных
			while (res.next()) {
				RptData rowData = new RptData();
				// Присвоение полям объекта базовой сущности
				// значений набора данных, полученных запросом
				rowData.setCod_client(res.getBigDecimal(1));
				rowData.setFio_client(res.getString(2));
				rowData.setName(res.getString(3));
				rowData.setCount(res.getBigDecimal(4));
				rowData.setSumma(res.getBigDecimal(5));
				rowData.setAvgsum(res.getBigDecimal(6));
				// Добавление объекта в коллекцию
				rptData.add(rowData);
			}
			res.close();
			// Возврат объекта базовой сущности
			return rptData;
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка получения данных", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			RollBack();
			return null;
		} finally {
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}