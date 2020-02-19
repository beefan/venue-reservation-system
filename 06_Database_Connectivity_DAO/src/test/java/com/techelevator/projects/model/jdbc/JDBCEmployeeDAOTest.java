package com.techelevator.projects.model.jdbc;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Employee;

import org.junit.*;

public class JDBCEmployeeDAOTest {

	private JDBCEmployeeDAO dao;
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
		dao = new JDBCEmployeeDAO(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void get_all_employees() {
		truncateEmployee();

		// test empty employee table returns 0 length employee list
		List<Employee> employees = dao.getAllEmployees();
		Assert.assertEquals(0, employees.size());

		String sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, DEFAULT, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01')";
		jdbcTemplate.update(sql);

		// test employee table with one employee inserted returns 1 length list
		employees = dao.getAllEmployees();
		Assert.assertEquals(1, employees.size());
	}

	@Test
	public void search_employees_by_name() {

		// insert test employee
		String sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, DEFAULT, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01')";
		jdbcTemplate.update(sql);

		// search by full name
		Employee employee = dao.searchEmployeesByName("FIRSTNAME", "LASTNAME").get(0);

		Assert.assertEquals("FIRSTNAME", employee.getFirstName());
		Assert.assertEquals("LASTNAME", employee.getLastName());

		// search with last name
		employee = dao.searchEmployeesByName("", "LASTNAME").get(0);

		Assert.assertEquals("FIRSTNAME", employee.getFirstName());
		Assert.assertEquals("LASTNAME", employee.getLastName());

		// search with first name
		employee = dao.searchEmployeesByName("FIRSTNAME", "").get(0);
		Assert.assertEquals("FIRSTNAME", employee.getFirstName());
		Assert.assertEquals("LASTNAME", employee.getLastName());

		// search with like names
		employee = dao.searchEmployeesByName("FIRST", "NAME").get(0);
		Assert.assertEquals("FIRSTNAME", employee.getFirstName());
		Assert.assertEquals("LASTNAME", employee.getLastName());

		employee = dao.searchEmployeesByName("NAME", "LAST").get(0);
		Assert.assertEquals("FIRSTNAME", employee.getFirstName());
		Assert.assertEquals("LASTNAME", employee.getLastName());

	}

	@Test
	public void get_employees_by_department_id() {
		truncateEmployee();

		// insert test employee dept. 4
		String sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, 4, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01')";
		jdbcTemplate.update(sql);

		// insert dept. 5 employee
		sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, 5, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01')";
		jdbcTemplate.update(sql);

		// get dept. 4 employee
		List<Employee> employee = dao.getEmployeesByDepartmentId(4);

		Assert.assertEquals(1, employee.size());

	}

	@Test
	public void get_employees_without_projects() {
		truncateEmployee();

		// insert test employee w/o project
		String sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, DEFAULT, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01')";
		jdbcTemplate.update(sql);

		// insert employee with a project
		sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, DEFAULT, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01') RETURNING employee_id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long employeeWithProjectId = row.getLong("employee_id");

		sql = "INSERT INTO project_employee VALUES (4, " + employeeWithProjectId + ")";
		jdbcTemplate.update(sql);

		// get employees without project
		List<Employee> employee = dao.getEmployeesWithoutProjects();

		Assert.assertEquals(1, employee.size());

	}

	@Test
	public void get_employees_by_project_id() {

		truncateEmployee();
		truncateEmployeeProjects();

		// insert test employee w/o project
		String sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, DEFAULT, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01')";
		jdbcTemplate.update(sql);

		// insert employee with a project
		sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, DEFAULT, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01') RETURNING employee_id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long employeeWithProjectId = row.getLong("employee_id");

		sql = "INSERT INTO project_employee VALUES (4, " + employeeWithProjectId + ")";
		jdbcTemplate.update(sql);

		List<Employee> employee = dao.getEmployeesByProjectId((long) 4);

		Assert.assertEquals(1, employee.size());

	}

	@Test
	public void change_employee_department() {
		truncateEmployee();

		// insert employee to test
		String sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) "
				+ "VALUES (DEFAULT, DEFAULT, 'FIRSTNAME', 'LASTNAME', '1992-01-09', 'M', '2019-01-01') RETURNING employee_id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long employeeId= row.getLong("employee_id");
		
		dao.changeEmployeeDepartment(employeeId, (long)4);
		
		sql = "SELECT employee_id FROM employee WHERE department_id = 4";
		row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long actualEmployeeId = row.getLong("employee_id");
		
		Assert.assertEquals(employeeId, actualEmployeeId);

	}

	private void truncateEmployee() {
		String sql = "TRUNCATE employee CASCADE";
		jdbcTemplate.update(sql);
	}

	private void truncateEmployeeProjects() {
		String sql = "TRUNCATE project_employee CASCADE";
		jdbcTemplate.update(sql);
	}

}
