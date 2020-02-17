package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Project> getAllActiveProjects() {
		List<Project> project = new ArrayList<>();

		String sql = "SELECT * FROM project WHERE (from_date < CURRENT_DATE AND to_date > CURRENT_DATE) "
				+ "OR (from_date < CURRENT_DATE AND to_date IS NULL)";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while (results.next()) {
			project.add(mapRowToProject(results));
		}

		return project;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sql = "DELETE FROM project_employee WHERE project_id = ? AND employee_id = ?";
		jdbcTemplate.update(sql, projectId, employeeId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sql = "INSERT INTO project_employee VALUES (?, ?)";
		jdbcTemplate.update(sql, projectId, employeeId);
	}

	private Project mapRowToProject(SqlRowSet result) {
		Project project = new Project();

		project.setId(Long.parseLong(result.getString("project_id")));
		project.setName(result.getString("name"));
		project.setStartDate(LocalDate.parse(result.getString("from_date")));
		if (result.getString("to_date") != null) {
			project.setEndDate(LocalDate.parse(result.getString("to_date")));
		}
		return project;
	}
}
