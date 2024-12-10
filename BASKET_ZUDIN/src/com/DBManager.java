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
	

	// ------------------------------����� SEQ
	// ����� ��������� ������������ �����
	// ������������ ��� ���� �������������������
	// ������� �������� - ��� ������������������
	public BigDecimal getId(String seqName) {
		BigDecimal id = null;
		PreparedStatement pst = null;
		Connection con = this.getConnection();
		String stm = "SELECT nextval(?)";
		try {
			// ������������ ������� � ��
			// ���������� ������� � ��������� ������ ������
			pst = con.prepareStatement(stm);
			// seqName - ��� ������������������
			pst.setString(1, seqName);
			ResultSet res = pst.executeQuery();
			while (res.next()) {
				id = res.getBigDecimal(1);
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ��������������",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
		return id;
	}
	
	

	// ����� ������������ ����������
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

	// ��������� ����
	public void setPath() {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			// ��� ����� ���� �������� !!!!
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
		// ��������� ����� � ������
		setPath();
	}

	
	// ---------------------
		// ------------------�������� level
		// ��������� ������� level �� ��
		// � ������� ��� � ���� ������
		public ArrayList<Level> loadLevel() {
			ArrayList<Level> grs = null;
			// ��������� ������� ����������
			Connection con = this.getConnection();
			try {
				// ������������ ������� � ��
				Statement stmt = con.createStatement();
				// ���������� ������� � ��������� ������ ������
				ResultSet res = stmt.executeQuery("SELECT id_competition_level, "
						+ "competition_level\r\n"
						+ "	FROM public.level_event order by id_competition_level");
				// �������� ������� � ������ level
				grs = new ArrayList<Level>();
				// � ����� ��������� ������ ������ ��������� ������
				while (res.next()) {
					// ������� ������ Level
					Level pr = new Level();
					// ��������� ���� ������� ������� �� ������ ������
					pr.setKod(res.getBigDecimal(1));
					pr.setName(res.getString(2));
					
					// ��������� ������ � ������
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
			}
			// ������� ������
			return grs;
		}

		// ------------------�������� �����
		// ��������� ������� ������� �� ��
		// � ������� ��� � ���� ������
		public ArrayList<Level> loadLevelForCmb() {
			ArrayList<Level> grs = null;
	//��������� ������� ����������
			Connection con = this.getConnection();
			try {
	//������������ ������� � ��
				Statement stmt = con.createStatement();
	//���������� ������� � ��������� ������ ������
				ResultSet res = stmt.executeQuery("SELECT id_competition_level, "
						+ "competition_level\r\n"
						+ "	FROM public.level_event order by competition_level");
			
	//�������� ������� � ������ 
				grs = new ArrayList<Level>();
	//� ����� ��������� ������ ������ ��������� ������
				while (res.next()) {
	//������� ������ Level
					Level pr = new Level();
	//��������� ���� ������� ������� �� ������ ������
					pr.setKod(res.getBigDecimal(1));
					pr.setName(res.getString(2));
					

	//��������� ������ � ������
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
			}
	//������� ������
			return grs;
		}

		// �������������� �����
		// ���������� ��������� �������������� ������
		// ���������:
		// dol � ������ �����Ļ �� ����� ��������������
		// key � �������� ��������� ���������
		public boolean updateLevel(Level dol, BigDecimal key) {
			PreparedStatement pst = null;
			// ��������� ������� ����������
			Connection con = this.getConnection();
			// ������ � ������� ��������� (? � ���������)
		
			String stm = "UPDATE level_event set id_competition_level=?, competition_level=?"

					 + " WHERE id_competition_level=?";
			try {
				// �������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������
				// 1- ����� ���������

				pst.setBigDecimal(1, dol.getKod());
				pst.setString(2, dol.getName());

				
				pst.setBigDecimal(3, key);
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
				con.commit();
			} catch (SQLException ex) {
				// � ������ ������ � ������ ����������
				RollBack();
				// � ����� ��������� �� ������
				JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
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

		// ���������� level
		// ���������� ��������� ���������� ������
		public boolean addLevel(Level dol) {
			PreparedStatement pst = null;
			// ��������� ������� ����������
			Connection con = this.getConnection();
			// ������ � ������� ��������� (? � ���������)
			String stm = ""
					+ "INSERT INTO public.level_event(id_competition_level, competition_level)VALUES (?, ?)";
					
			try {
				// �������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������

				pst.setBigDecimal(1, dol.getKod());
				pst.setString(2, dol.getName());


				
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
				con.commit();
				return true;
			} catch (SQLException ex) {
				// � ������ ������ � ������ ����������
				RollBack();
				JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ���������� ������", JOptionPane.ERROR_MESSAGE);
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

		// �������� �����
		// ���������� ��������� �������� ������
		public boolean deleteLevel(BigDecimal kod) {
			PreparedStatement pst = null;
			// ��������� ������� ����������
			Connection con = this.getConnection();
			// ������ � ������� ��������� (? � ���������)
			String stm = "DELETE FROM level_event" + " WHERE id_competition_level=?";
			try {
				// �������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������
				pst.setBigDecimal(1, kod);
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
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


	

	//------------------�������� Team
	// ��������� ������� TeamTeam �� ��
	// � ������� ��� � ���� ������ 
		public ArrayList<Team> loadTeam() {
			ArrayList<Team> grs = null;
	//��������� ������� ����������
			Connection con = this.getConnection();
			try {
	//������������ ������� � ��
				Statement stmt = con.createStatement();
	//���������� ������� � ��������� ������ ������
				ResultSet res = stmt.executeQuery("SELECT * from Team Order by id_team");
	//�������� ������� � ������ �������� ����.
				grs = new ArrayList<Team>();
	//� ����� ��������� ������ ������ ��������� ������
				while (res.next()) {
	//������� ������ Team
					Team pr = new Team();
	//��������� ���� ������� ������� �� ������ ������
					pr.setId_team(res.getBigDecimal(1));
					
					pr.setCity(res.getString(2));
					pr.setTeam_name(res.getString(3));
					
					pr.setGender_team(res.getString(4));

	//��������� ������ � ������
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
			}
	//������� ������
			return grs;
		}
		//------------------�������� Team
		// ��������� ������� TeamTeam �� ��
		// � ������� ��� � ���� ������ 
			public ArrayList<Team> loadTeamForCmb() {
				ArrayList<Team> grs = null;
		//��������� ������� ����������
				Connection con = this.getConnection();
				try {
		//������������ ������� � ��
					Statement stmt = con.createStatement();
		//���������� ������� � ��������� ������ ������
					ResultSet res = stmt.executeQuery("SELECT * from Team Order by team_name");
		//�������� ������� � ������ �������� ����.
					grs = new ArrayList<Team>();
		//� ����� ��������� ������ ������ ��������� ������
					while (res.next()) {
		//������� ������ Team
						Team pr = new Team();
		//��������� ���� ������� ������� �� ������ ������
						pr.setId_team(res.getBigDecimal(1));
						
						pr.setCity(res.getString(2));
						pr.setTeam_name(res.getString(3));
						
						pr.setGender_team(res.getString(4));

		//��������� ������ � ������
						grs.add(pr);
					}
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
				}
		//������� ������
				return grs;
			}
		// �������������� ������ ������������
	//���������� ��������� �������������� ������
	//���������:
	//dol � ������ ������� �����������Ȼ �� ����� ��������������
	//key � �������� ��������� ���������
		public boolean updateTeam(Team dol, BigDecimal key) {
			PreparedStatement pst = null;
			// ��������� ������� ����������
			Connection con = this.getConnection();
	//  ������ � ������� ��������� (? � ���������)
			String stm = "UPDATE Team set " + "id_Team=?,"
	+ "city=?," + "team_name=?," + "gender_team=?"+ " WHERE id_Team=?";
			try {
				// �������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������
				// 1- ����� ���������

				pst.setBigDecimal(1, dol.getId_team());
				
				pst.setString(2, dol.getCity());
				pst.setString(3, dol.getTeam_name());
				pst.setString(4, dol.getGender_team());
				pst.setBigDecimal(5, key);
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
				con.commit();
			} catch (SQLException ex) {
	//� ������ ������ � ������ ���������� 
				RollBack();
				// � ����� ��������� �� ������
				JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
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

	//���������� team
		// ���������� ��������� ���������� ������
		public boolean addTeam(Team dol) {
			PreparedStatement pst = null;
	//��������� ������� ����������
			Connection con = this.getConnection();
			// ������ � ������� ��������� (? � ���������)
			String stm = "INSERT INTO Team(id_Team,"
					+ " city, team_name, gender_team)"
					+ "  VALUES(?,?,?,?)";
			try {
				// �������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������

	pst.setBigDecimal(1, dol.getId_team());
				
				pst.setString(2, dol.getCity());
				pst.setString(3, dol.getTeam_name());
				pst.setString(4, dol.getGender_team());
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
				con.commit();
				return true;
			} catch (SQLException ex) {
				// � ������ ������ � ������ ����������
				RollBack();
				JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ���������� ������", JOptionPane.ERROR_MESSAGE);
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

	//�������� ������ team
		// ���������� ��������� �������� ������
		public boolean deleteTeam(BigDecimal kod) {
			PreparedStatement pst = null;
	//��������� ������� ����������
			Connection con = this.getConnection();
			// ������ � ������� ��������� (? � ���������)
			String stm = "DELETE FROM Team" + " WHERE id_Team=?";
			try {
	//�������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������
				pst.setBigDecimal(1, kod);
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
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
	//��������� ������� ����������
			Connection con = this.getConnection();
			try {
	//������������ ������� � ��
				Statement stmt = con.createStatement();
	//���������� ������� � ��������� ������ ������
				ResultSet res = stmt.executeQuery(
						"SELECT * from Eventt_v");
	//�������� ������� � ������ 
				grs = new ArrayList<Event>();
	//� ����� ��������� ������ ������ ��������� ������
				while (res.next()) {
	//������� ������ Eventt
					Event pr = new Event();
	//��������� ���� ������� ������� �� ������ ������
					pr.setId_event(res.getBigDecimal(1));
					
					pr.setName_event(res.getString(2));
					pr.setLocationn(res.getString(3));
					pr.setYear_event(res.getBigDecimal(4));
					Level prr = new Level();
					prr.setKod(res.getBigDecimal(5));
					prr.setName(res.getString(6));
					
					pr.setLevel(prr);

					
				

	//��������� ������ � ������
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
			}
	//������� ������
			return grs;
		}
		public ArrayList<Event> loadEventtForCmb() {
			ArrayList<Event> grs = null;
	//��������� ������� ����������
			Connection con = this.getConnection();
			try {
	//������������ ������� � ��
				Statement stmt = con.createStatement();
	//���������� ������� � ��������� ������ ������
				ResultSet res = stmt.executeQuery(
						"SELECT * from Eventt_v order by Name_event");
	//�������� ������� � ������ 
				grs = new ArrayList<Event>();
	//� ����� ��������� ������ ������ ��������� ������
				while (res.next()) {
	//������� ������ Eventt
					Event pr = new Event();
	//��������� ���� ������� ������� �� ������ ������
					pr.setId_event(res.getBigDecimal(1));
					
					pr.setName_event(res.getString(2));
					pr.setLocationn(res.getString(3));
					pr.setYear_event(res.getBigDecimal(4));
					Level prr = new Level();
					prr.setKod(res.getBigDecimal(5));
					prr.setName(res.getString(6));
					
					pr.setLevel(prr);

					
				

	//��������� ������ � ������
					grs.add(pr);
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
			}
	//������� ������
			return grs;
		}

		// �������������� eve
	// ���������� ��������� �������������� ������
	//  ���������:
	//   dol � ������ �� �� ����� ��������������
	//   key � �������� ��������� ���������
		public boolean updateEventt(Event dol, BigDecimal key) {
			PreparedStatement pst = null;
			// ��������� ������� ����������
			Connection con = this.getConnection();
			// ������ � ������� ��������� (? � ���������)
			String stm = "UPDATE public.eventt\r\n"
					+ "	SET id_event=?, name_event=?, locationn=?, year_event=?, id_competition_level=?\r\n"
					+ "	WHERE id_event=?";
			try {
				// �������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������
				// 1- ����� ���������

				pst.setBigDecimal(1, dol.getId_event());
				pst.setString(2, dol.getName_event());
				pst.setString(3, dol.getLocationn());
				

				pst.setBigDecimal(4, dol.getYear_event());
				pst.setBigDecimal(5, dol.getLevel().getKod());
				pst.setBigDecimal(6, key);
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
				con.commit();
			} catch (SQLException ex) {
	// � ������ ������ � ������ ���������� 
				RollBack();
				// � ����� ��������� �� ������
				JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
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

	//���������� 
		// ���������� ��������� ���������� ������
		public boolean addEventt(Event dol) {
			PreparedStatement pst = null;
	//  ��������� ������� ����������
			Connection con = this.getConnection();
			// ������ � ������� ��������� (? � ���������)
			String stm = ""
					+ "INSERT INTO public.eventt(\r\n"
					+ "	id_event, name_event, locationn, year_event, id_competition_level)\r\n"
					+ "	VALUES (?, ?, ?, ?, ?);";
			try {
				// �������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������

				pst.setBigDecimal(1, dol.getId_event());
				pst.setString(2, dol.getName_event());
				pst.setString(3, dol.getLocationn());
				

				pst.setBigDecimal(4, dol.getYear_event());
				pst.setBigDecimal(5, dol.getLevel().getKod());
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
				con.commit();
				return true;
			} catch (SQLException ex) {
				// � ������ ������ � ������ ����������
				RollBack();
				JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ���������� ������", JOptionPane.ERROR_MESSAGE);
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

	//�������� ev
		// ���������� ��������� �������� ������
		public boolean deleteEventt(BigDecimal kod) {
			PreparedStatement pst = null;
	//  ��������� ������� ����������
			Connection con = this.getConnection();
			// ������ � ������� ��������� (? � ���������)
			String stm = "DELETE FROM Eventt" + " WHERE id_event=?";
			try {
	//  �������� ������� ��������� � �����������
				pst = con.prepareStatement(stm);
				// ������ �������� ���������� ���������
				pst.setBigDecimal(1, kod);
				// ���������� ���������
				pst.executeUpdate();
				// ���������� ���������� � ���������� ���������
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
		// ------------------�������� ma
		// ��������� �������  �� ��
		// � ������� ��� � ���� ������ 
			public ArrayList<Matches> loadMatches() {
				ArrayList<Matches> grs = null;
		//��������� ������� ����������
				Connection con = this.getConnection();
				try {
		//������������ ������� � ��
					Statement stmt = con.createStatement();
		//���������� ������� � ��������� ������ ������
					ResultSet res = stmt.executeQuery(
							"SELECT id_matches, date_matches, team1score,"
							+ " team2score, id_event, name_event, id_team1, "
							+ "team_name, id_team2, team_name2, "
							+ "playground from Matches_v order by id_matches");
		//�������� ������� � ������ ������
					grs = new ArrayList<Matches>();
		//� ����� ��������� ������ ������ ��������� ������
					while (res.next()) {
		//������� ������ Matches
						Matches pr = new Matches();
		//��������� ���� ������� ������� �� ������ ������
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
		//��������� ������ � ������
						grs.add(pr);
					}
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
				}
		//������� ������
				return grs;
			}

			
		//���������� ��������� �������������� ������
		//���������:
		//dol � ������ �m� �� ����� ��������������
		//key � �������� ��������� ���������
			public boolean updateMatches(Matches dol, BigDecimal key) {
				PreparedStatement pst = null;
				// ��������� ������� ����������
				Connection con = this.getConnection();
		//  ������ � ������� ��������� (? � ���������)
				String stm = "UPDATE public.matches\r\n"
						+ "	SET id_matches=?, "
						+ "date_matches=?, team1score=?, team2score=?, "
						+ "id_event=?, id_team1=?, id_team2=?, playground=?\r\n"
						+ "WHERE id_matches=?";
				try {
					// �������� ������� ��������� � �����������
					pst = con.prepareStatement(stm);
					// ������ �������� ���������� ���������
					// 1- ����� ���������

					pst.setBigDecimal(1, dol.getId_matches());
					pst.setBigDecimal(3, dol.getTeam1score());
					pst.setDate(2, HelperConverter.convertFromJavaDateToSQLDate(dol.getDate_matches()));
					pst.setBigDecimal(4, dol.getTeam2score());
					
					pst.setBigDecimal(5, dol.getEvent().getId_event());

					
					pst.setBigDecimal(6, dol.getTeam1().getId_team());
					pst.setBigDecimal(7, dol.getTeam2().getId_team());
					pst.setString(8, dol.getPlayground());
					pst.setBigDecimal(9, key);
					// ���������� ���������
					pst.executeUpdate();
					// ���������� ���������� � ���������� ���������
					con.commit();
				} catch (SQLException ex) {
		//� ������ ������ � ������ ���������� 
					RollBack();
					// � ����� ��������� �� ������
					JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
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

		//���������� mayc
			// ���������� ��������� ���������� ������
			public boolean addMatches(Matches dol) {
				PreparedStatement pst = null;
		//��������� ������� ����������
				Connection con = this.getConnection();
				// ������ � ������� ��������� (? � ���������)
				String stm = "INSERT INTO public.matches(\r\n"
						+ "	id_matches, date_matches, team1score, team2score, id_event, id_team1, id_team2, playground)\r\n"
						+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				try {
					// �������� ������� ��������� � �����������
					pst = con.prepareStatement(stm);
					// ������ �������� ���������� ���������
					dol.setId_matches(getId("m_seq"));
					pst.setBigDecimal(1, dol.getId_matches());
					pst.setBigDecimal(3, dol.getTeam1score());
					pst.setDate(2, HelperConverter.convertFromJavaDateToSQLDate(dol.getDate_matches()));
					pst.setBigDecimal(4, dol.getTeam2score());
					
					pst.setBigDecimal(5, dol.getEvent().getId_event());

					
					pst.setBigDecimal(6, dol.getTeam1().getId_team());
					pst.setBigDecimal(7, dol.getTeam2().getId_team());
					pst.setString(8, dol.getPlayground());
					// ���������� ���������
					pst.executeUpdate();
					// ���������� ���������� � ���������� ���������
					con.commit();
					return true;
				} catch (SQLException ex) {
					// � ������ ������ � ������ ����������
					RollBack();
					JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ���������� ������", JOptionPane.ERROR_MESSAGE);
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

		//�������� m
			// ���������� ��������� �������� ������
			public boolean deleteMatches(BigDecimal kod) {
				PreparedStatement pst = null;
		//��������� ������� ����������
				Connection con = this.getConnection();
				// ������ � ������� ��������� (? � ���������)
				String stm = "DELETE FROM Matches" + " WHERE id_matches=?";
				try {
		//�������� ������� ��������� � �����������
					pst = con.prepareStatement(stm);
					// ������ �������� ���������� ���������
					pst.setBigDecimal(1, kod);
					// ���������� ���������
					pst.executeUpdate();
					// ���������� ���������� � ���������� ���������
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

			//------------------�������� ����� �ר��
			//��������� ������� ������� ����� ����� �� ��
			//� ������� ��� � ���� ������ 
				public ArrayList<Stat> loadStat(BigDecimal ff) {
					ArrayList<Stat> grs = null;
			//��������� ������� ����������
					Connection con = this.getConnection();
					try {
			//������������ ������� � ��
						Statement stmt = con.createStatement();
						ResultSet res;
			//���������� ������� � ��������� ������ ������
					
							res = stmt.executeQuery(
									"SELECT * from Ps_v where id_matches=" + ff);

						
			//�������� ������� � ������ ������� ����� �����
						grs = new ArrayList<Stat>();
			//� ����� ��������� ������ ������ ��������� ������
						while (res.next()) {
			//������� ������ Player_Stats
							Stat stats = new Stat();
			//��������� ���� ������� ������� �� ������ ������
							
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
						
							Team team = loadTeamForPlayer(onn.getId()); // !!!  ��������� ����� �����
				            onn.setTeam(team);
							

							stats.setPlayer(onn);
							

			//��������� ������ � ������
							grs.add(stats);
						}
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
					}
			//������� ������
					return grs;
				}

				// �������������� ����� �ר��
				//���������� ��������� �������������� ������
				//���������:
				//dol � ������ ������ �ר��� �� ����� ��������������
				//key � �������� ��������� ���������
					public boolean updateStat(Stat playerStats, BigDecimal key) {
						PreparedStatement pst = null;
						// ��������� ������� ����������
						Connection con = this.getConnection();
				//������ � ������� ��������� (? � ���������)
						String stm = "UPDATE public.player_stats\r\n"
								+ "	SET id_playerstats=?, "
								+ "pointscored=?, assists=?, steal=?, turnover=?, blocked_shot=?, foul=?, double=?, triple=?, free_throw=?, defensive_rebound=?, offensive_rebound=?, id_player=?, id_matches=?\r\n"
								+ "	WHERE id_playerstats=?";
						try {
							// �������� ������� ��������� � �����������
							pst = con.prepareStatement(stm);
							// ������ �������� ���������� ���������
							// 1- ����� ���������
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
							// ���������� ���������
							pst.executeUpdate();
							// ���������� ���������� � ���������� ���������
							con.commit();
						} catch (SQLException ex) {
				//� ������ ������ � ������ ���������� 
							RollBack();
							// � ����� ��������� �� ������
							JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
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

				//���������� ����� �ר��
					// ���������� ��������� ���������� ������
					public boolean addStat(Stat playerStats) {
						PreparedStatement pst = null;
				//��������� ������� ����������
						Connection con = this.getConnection();
						// ������ � ������� ��������� (? � ���������)
						String stm = "INSERT INTO public.player_stats("
								
								+ "	id_playerstats, pointscored, "
								+ "assists, steal, turnover, blocked_shot,"
								+ " foul, double, triple, free_throw,"
								+ " defensive_rebound, offensive_rebound,"
								+ " id_player, id_matches)"
								+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
							System.out.print(stm);
						try {
							// �������� ������� ��������� � �����������
							pst = con.prepareStatement(stm);
							// ������ �������� ���������� ���������
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
							// ���������� ���������
							pst.executeUpdate();
							// ���������� ���������� � ���������� ���������
							con.commit();
							return true;
						} catch (SQLException ex) {
							// � ������ ������ � ������ ����������
							RollBack();
							JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ���������� ������", JOptionPane.ERROR_MESSAGE);
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

				//�������� ����� �ר��
					// ���������� ��������� �������� ������
					public boolean deleteStat(BigDecimal kod) {
						PreparedStatement pst = null;
				//��������� ������� ����������
						Connection con = this.getConnection();
						// ������ � ������� ��������� (? � ���������)
						String stm = "DELETE FROM Player_stats" + " WHERE id_Playerstats=?";
						try {
				//�������� ������� ��������� � �����������
							pst = con.prepareStatement(stm);
							// ������ �������� ���������� ���������
							pst.setBigDecimal(1, kod);
							// ���������� ���������
							pst.executeUpdate();
							// ���������� ���������� � ���������� ���������
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
				//��������� ������� ����������
						Connection con = this.getConnection();
						try {
				//������������ ������� � ��
							Statement stmt = con.createStatement();
				//���������� ������� � ��������� ������ ������
							ResultSet res = stmt.executeQuery(
									"SELECT * from player order by player_name");
				//�������� ������� � ������ 
							grs = new ArrayList<Player>();
				//� ����� ��������� ������ ������ ��������� ������
							while (res.next()) {
				//������� ������ Eventt
								Player pr = new Player();
				//��������� ���� ������� ������� �� ������ ������
								pr.setId(res.getBigDecimal(1));
								
								pr.setName(res.getString(3));
								
								
							
								
							

				//��������� ������ � ������
								grs.add(pr);
							}
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
						}
				//������� ������
						return grs;
					}

					//------------------�������� 
					// ��������� ������� ��������  �� ��
					// � ������� ��� � ���� ������ 
						public ArrayList<Player> loadPlayer() {
							ArrayList<Player> grs = null;
					//��������� ������� ����������
							Connection con = this.getConnection();
							try {
					//������������ ������� � ��
								Statement stmt = con.createStatement();
					//���������� ������� � ��������� ������ ������
								ResultSet res = stmt.executeQuery("SELECT id_player,"
										+ " birthday, player_name, height, weight, "
										+ "role, id_team, team_name, game_number, gender\r\n"
										+ "	FROM public.player_v order by id_player");
									
					//�������� ������� � ������ �������� ����.
								grs = new ArrayList<Player>();
					//� ����� ��������� ������ ������ ��������� ������
								while (res.next()) {
					//������� ������ Player
									Player pr = new Player();
					//��������� ���� ������� ������� �� ������ ������
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
								

					//��������� ������ � ������
									grs.add(pr);
								}
							} catch (SQLException e) {
								JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
							}
					//������� ������
							return grs;
						}

						// �������������� ������ ������������
					//���������� ��������� �������������� ������
					//���������:
					//dol � ������ ������� �����������Ȼ �� ����� ��������������
					//key � �������� ��������� ���������
						public boolean updatePlayer(Player dol, BigDecimal key) {
							PreparedStatement pst = null;
							// ��������� ������� ����������
							Connection con = this.getConnection();
					//  ������ � ������� ��������� (? � ���������)
							String stm = "UPDATE public.player\r\n"
									+ "	SET id_player=?, birthday=?, "
									+ "player_name=?, height=?, weight=?, role=?, "
									+ "id_team=?, game_number=?, gender=?\r\n"
									+ "	WHERE id_player=?";
							try {
								// �������� ������� ��������� � �����������
								pst = con.prepareStatement(stm);
								// ������ �������� ���������� ���������
								// 1- ����� ���������

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
								
								
								// ���������� ���������
								pst.executeUpdate();
								// ���������� ���������� � ���������� ���������
								con.commit();
							} catch (SQLException ex) {
					//� ������ ������ � ������ ���������� 
								RollBack();
								// � ����� ��������� �� ������
								JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
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

					//���������� ������ ������������
						// ���������� ��������� ���������� ������
						public boolean addPlayer(Player dol) {
							PreparedStatement pst = null;
					//��������� ������� ����������
							Connection con = this.getConnection();
							// ������ � ������� ��������� (? � ���������)
							String stm = "INSERT INTO public.player(\r\n"
									+ "	id_player, birthday, player_name, height, weight, role, id_team, game_number, gender)\r\n"
									+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
							try {
								// �������� ������� ��������� � �����������
								pst = con.prepareStatement(stm);
								// ������ �������� ���������� ���������
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
								// ���������� ���������
								pst.executeUpdate();
								// ���������� ���������� � ���������� ���������
								con.commit();
								return true;
							} catch (SQLException ex) {
								// � ������ ������ � ������ ����������
								RollBack();
								JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ���������� ������", JOptionPane.ERROR_MESSAGE);
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

					//�������� ������ ������������
						// ���������� ��������� �������� ������
						public boolean deletePlayer(BigDecimal kod) {
							PreparedStatement pst = null;
					//��������� ������� ����������
							Connection con = this.getConnection();
							// ������ � ������� ��������� (? � ���������)
							String stm = "DELETE FROM Player" + " "
									+ "WHERE id_player=?";
							try {
					//�������� ������� ��������� � �����������
								pst = con.prepareStatement(stm);
								// ������ �������� ���������� ���������
								pst.setBigDecimal(1, kod);
								// ���������� ���������
								pst.executeUpdate();
								// ���������� ���������� � ���������� ���������
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
					            JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������ ����������", JOptionPane.ERROR_MESSAGE);
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
					                // ���������� ����� ������
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
					                // ���������� ������������ ������
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
					            JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ���������� ����������", JOptionPane.ERROR_MESSAGE);
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
					                // ��������� ������� ��� ������, ���� ��� ����������
					                BigDecimal teamId = res.getBigDecimal("id_team");
					                if (teamId != null) {
					                    Team team = loadTeamById(teamId);
					                    player.setTeam(team);
					                }
					                player.setNum(res.getBigDecimal("game_number"));
					                player.setGender(res.getString("gender"));
					            }
					        } catch (SQLException ex) {
					            JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������ ������", JOptionPane.ERROR_MESSAGE);
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
					            JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������ �������", JOptionPane.ERROR_MESSAGE);
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
					            JOptionPane.showMessageDialog(null, e.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
					        }
					        return null;
					    }
					    


					    
					    
	public ArrayList<RptData> getDataReport(RptParams params) {
		// ��������� ������� ���������� � ��
		Connection con = this.getConnection();
		// �������� ������� ������� � ��
		PreparedStatement pst = null;
		// ����� ������� � ��
		String stm = "SELECT * FROM get_client_info(?::date,?::date)";
		try {
			pst = con.prepareStatement(stm);
			// ������� �������� ���������� ���������
			if (params.StartDate == null)
				pst.setDate(1, null);
			else
				pst.setDate(1, HelperConverter.convertFromJavaDateToSQLDate(params.StartDate));
			if (params.EndDate == null)
				pst.setDate(2, null);
			else
				pst.setDate(2, HelperConverter.convertFromJavaDateToSQLDate(params.EndDate));
			// ���������� ������� � ��
			ResultSet res = pst.executeQuery();
			// �������� ��������� �������� ������� ��������
			ArrayList<RptData> rptData = new ArrayList<>();
			// ���� ������������ ��������� ������
			while (res.next()) {
				RptData rowData = new RptData();
				// ���������� ����� ������� ������� ��������
				// �������� ������ ������, ���������� ��������
				rowData.setCod_client(res.getBigDecimal(1));
				rowData.setFio_client(res.getString(2));
				rowData.setName(res.getString(3));
				rowData.setCount(res.getBigDecimal(4));
				rowData.setSumma(res.getBigDecimal(5));
				rowData.setAvgsum(res.getBigDecimal(6));
				// ���������� ������� � ���������
				rptData.add(rowData);
			}
			res.close();
			// ������� ������� ������� ��������
			return rptData;
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "������ ��������� ������", JOptionPane.ERROR_MESSAGE);
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