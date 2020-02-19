package com.techelevator.projects.model.jdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Project;

public class JDBCProjectDAOTest {

	private JDBCProjectDAO dao;
	private static SingleConnectionDataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Before
	public void setup() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		dao = new JDBCProjectDAO(dataSource);
	}

	@Test
	public void get_all_active_projects() {

		// arrange
		truncateProjects();
		// add active projects
		long id1 = insertProject("CURRENT_DATE", null, "TESTNAME1");
		long id2 = insertProject("CURRENT_DATE", "'2020-10-05'", "TESTNAME2");
		long id3 = insertProject("CURRENT_DATE", "CURRENT_DATE", "TESTNAME3");
		
		// add not active projects
		insertProject("'2020-10-05'", null, "TESTNAME4");
		insertProject("'2012-04-02'", "'2013-05-13'", "TESTNAME5");
		insertProject(null, null, "TESTNAME6");
		
		// act
		List<Project> projects = dao.getAllActiveProjects();

		// assert
		Assert.assertEquals(id1, (long) projects.get(0).getId());
		Assert.assertEquals(id2, (long) projects.get(1).getId());
		Assert.assertEquals(id3, (long) projects.get(2).getId());
		Assert.assertEquals(3, projects.size());

	}

	@Test
	public void remove_employee_from_project() {

		// arrange
		truncateEmployeeProjects();
		String sql = "INSERT INTO project_employee VALUES (4, 5)";
		jdbcTemplate.update(sql);

		// act
		dao.removeEmployeeFromProject((long) 4, (long) 5);

		// assert
		sql = "SELECT project_id FROM project_employee";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);

		Assert.assertFalse("There is a line in this row set", row.next());

	}

	@Test
	public void add_employee_to_project() {

		// arrange
		truncateEmployeeProjects();

		// act
		dao.addEmployeeToProject((long) 4, (long) 5);

		// assert
		String sql = "SELECT project_id FROM project_employee";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();

		Assert.assertEquals((long) 4, row.getLong("project_id"));

	}

	private long insertProject(String startDate, String endDate, String testName) {
		String sql = "INSERT INTO project (project_id, name, from_date, to_date) VALUES (DEFAULT, '" +
				testName + "', ";
		SqlRowSet row;
		
			if (endDate == null && startDate == null) {
				sql += "DEFAULT, DEFAULT";
			} else if (endDate == null){
				sql = sql + startDate + ", DEFAULT";
			} else if (startDate == null) {
				sql = sql + "DEFAULT, " + endDate;
			}else {
				sql = sql + startDate + ", " + endDate;
			}
			
			sql += ") RETURNING project_id";
			row = jdbcTemplate.queryForRowSet(sql);
			row.next();
			
			return row.getLong("project_id");
	}

	private void truncateProjects() {
		String sql = "TRUNCATE project CASCADE";
		jdbcTemplate.update(sql);
	}

	private void truncateEmployeeProjects() {
		String sql = "TRUNCATE project_employee CASCADE";
		jdbcTemplate.update(sql);
	}

}
