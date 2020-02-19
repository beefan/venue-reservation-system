package com.techelevator.projects.model.jdbc;

import java.sql.SQLException;
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

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;

public class JDBCDepartmentDAOTest {

	private JDBCDepartmentDAO dao;
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
		dao = new JDBCDepartmentDAO(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void get_all_departments() {
		truncateDepartment();

		// test empty department table returns 0 length department list
		List<Department> departments = dao.getAllDepartments();
		Assert.assertEquals(0, departments.size());

		String sql = "INSERT INTO department (department_id, name) " + "VALUES (DEFAULT, 'PARTY DEPARTMENT')";
		jdbcTemplate.update(sql);

		// test department table with one department inserted returns 1 length list
		departments = dao.getAllDepartments();
		Assert.assertEquals(1, departments.size());
	}

	@Test
	public void search_department_by_name() {
		truncateDepartment();

		String sql = "INSERT INTO department (department_id, name) " + "VALUES (DEFAULT, 'PARTY DEPARTMENT')";
		jdbcTemplate.update(sql);

		// get department by full name
		List<Department> departments = dao.searchDepartmentsByName("PARTY DEPARTMENT");
		Assert.assertEquals(1, departments.size());

		// get department by partial name
		departments = dao.searchDepartmentsByName("ARTY DEPART");
		Assert.assertEquals(1, departments.size());

		// empty search gets everything
		departments = dao.searchDepartmentsByName("");
		Assert.assertEquals(1, departments.size());

		// wrong search gets nothing
		departments = dao.searchDepartmentsByName("PHISHING");
		Assert.assertEquals(0, departments.size());

	}

	@Test
	public void save_department() {
		truncateDepartment();

		// insert depart.
		String sql = "INSERT INTO department (department_id, name) "
				+ "VALUES (DEFAULT, 'PARTY DEPARTMENT') RETURNING department_id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long deptId = row.getLong("department_id");

		// create new dept.
		Department updatedDepartment = new Department();

		updatedDepartment.setId(deptId);
		updatedDepartment.setName("Phishing");

		// change the name
		dao.saveDepartment(updatedDepartment);

		// query the table
		sql = "SELECT name FROM department where name = 'Phishing'";
		row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		String name = row.getString("name");

		// compare the changed name
		Assert.assertEquals("Phishing", name);

	}

	@Test
	public void create_department() {
		truncateDepartment();

		// create new dept.
		Department updatedDepartment = new Department();

		updatedDepartment.setName("Phishing");

		// create the dept in table
		dao.createDepartment(updatedDepartment);

		// query the table
		String sql = "SELECT name FROM department where name = 'Phishing'";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		String name = row.getString("name");

		// compare the result
		Assert.assertEquals("Phishing", name);

	}
	
	@Test
	public void get_department_by_id() {
		truncateDepartment();
		
		// insert depart.
		String sql = "INSERT INTO department (name) "
				+ "VALUES ('PARTY DEPARTMENT') RETURNING department_id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();  
		long deptId = row.getLong("department_id");
		
		Department dept = dao.getDepartmentById(deptId);
		
		
		Assert.assertEquals("PARTY DEPARTMENT", dept.getName());
		
		//get department that doesn't exist
		dept = dao.getDepartmentById(deptId + 1);
		Assert.assertNull(dept);
	}

	private void truncateDepartment() {
		String sql = "TRUNCATE department CASCADE";
		jdbcTemplate.update(sql);
	}
}
