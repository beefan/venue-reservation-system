package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;

public class JDBCDepartmentDAO implements DepartmentDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Department> getAllDepartments() {
		List<Department> department = new ArrayList<>();

		String sql = "SELECT department_id, name FROM department";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while (results.next()) {
			department.add(mapRowToDepartment(results));
		}

		return department;
	}

	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		List<Department> department = new ArrayList<>();

		String sql = "SELECT department_id, name FROM department WHERE name ILIKE ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, "%" + nameSearch + "%");

		while (results.next()) {
			department.add(mapRowToDepartment(results));
		}

		return department;
	}

	@Override
	public void saveDepartment(Department updatedDepartment) {
		String sql = "UPDATE department SET name = ? WHERE department_id = ?";
		jdbcTemplate.update(sql, updatedDepartment.getName(), updatedDepartment.getId());
	}

	@Override
	public Department createDepartment(Department newDepartment) {
		String sql = "INSERT INTO department (name) VALUES (?)";
		jdbcTemplate.update(sql, newDepartment.getName());

		return newDepartment;
	}

	@Override
	public Department getDepartmentById(Long id) {

		String sql = "SELECT department_id, name FROM department WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

		return mapRowToDepartment(results);
	}

	private Department mapRowToDepartment(SqlRowSet result) {
		Department department = new Department();

		department.setId(Long.parseLong(result.getString("department_id")));
		department.setName(result.getString("name"));

		return department;
	}

}
