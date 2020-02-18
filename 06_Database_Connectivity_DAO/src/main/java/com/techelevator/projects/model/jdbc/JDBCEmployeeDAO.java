package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<>();

		String sql = "SELECT * FROM employee";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while (results.next()) {
			employees.add(mapRowToEmployee(results));
		}

		return employees;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		List<Employee> employees = new ArrayList<>();

		String sql = "SELECT * FROM employee WHERE first_name ILIKE ? AND last_name ILIKE ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, "%" + firstNameSearch + "%", "%" + lastNameSearch + "%");

		while (results.next()) {
			employees.add(mapRowToEmployee(results));
		}

		return employees;
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		List<Employee> employees = new ArrayList<>();

		String sql = "SELECT * FROM employee WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

		while (results.next()) {
			employees.add(mapRowToEmployee(results));
		}

		return employees;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> employees = new ArrayList<>();

		String sql = "SELECT * FROM employee LEFT JOIN project_employee "
				+ "ON employee.employee_id = project_employee.employee_id "
				+ "WHERE project_employee.employee_id IS NULL";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while (results.next()) {
			employees.add(mapRowToEmployee(results));
		}

		return employees;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		List<Employee> employees = new ArrayList<>();

		String sql = "SELECT * FROM employee JOIN project_employee "
				+ "ON employee.employee_id = project_employee.employee_id " + "WHERE project_employee.project_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);

		while (results.next()) {
			employees.add(mapRowToEmployee(results));
		}

		return employees;
	}

	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String sql = "UPDATE employee SET department_id = ? WHERE employee_id = ?";
		jdbcTemplate.update(sql, departmentId, employeeId);
	}

	private Employee mapRowToEmployee(SqlRowSet result) {
		Employee employee = new Employee();

		employee.setId(Long.parseLong(result.getString("employee_id")));
		if (result.getString("department_id") != null) {
			employee.setDepartmentId(Long.parseLong(result.getString("department_id")));
		}
		employee.setFirstName(result.getString("first_name"));
		employee.setLastName(result.getString("last_name"));
		employee.setBirthDay(LocalDate.parse(result.getString("birth_date")));
		employee.setGender(result.getString("gender").charAt(0));
		employee.setHireDate(LocalDate.parse(result.getString("hire_date")));

		return employee;
	}

}
