package com.techelevator.excelsior.model.dao.jdbc;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.DAOIntegrationTest;

public class JDBCCategoryDAOTest extends DAOIntegrationTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCCategoryDAO dao;
	private JdbcTemplate jdbcTemplate;

	@Before
	public void setup() {
		dataSource = (SingleConnectionDataSource) super.getDataSource();
		dao = new JDBCCategoryDAO(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	// Test getCategoryByVenueId
	@Test
	public void get_category_by_venue_id() {
		truncateCategory();

		// add venues to table
		String sql = "INSERT INTO venue (id, name, city_id, description) VALUES "
				+ "(DEFAULT, 'The Singing Belle', 2, 'Lovely') RETURNING id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long venueIdSingingBelle = row.getLong("id");

		sql = "INSERT INTO venue (id, name, city_id, description) VALUES "
				+ "(DEFAULT, 'Screaming Toy', 2, 'Shrill') RETURNING id";
		row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long venueIdScreamingToy = row.getLong("id");

		sql = "INSERT INTO venue (id, name, city_id, description) VALUES "
				+ "(DEFAULT, 'The Whispering Woman', 2, 'Quiet') RETURNING id";
		row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long venueIdWhisperingWoman = row.getLong("id");

		// test empty table returns 0 size
		List<String> categories = dao.getCategoriesByVenueId(venueIdWhisperingWoman);
		Assert.assertEquals(0, categories.size());

		// add catgories to category table
		sql = "INSERT INTO category (id, name) VALUES (DEFAULT, 'Drama') RETURNING id";
		row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long categoryIdDrama = row.getLong("id");

		sql = "INSERT INTO category (id, name) VALUES (DEFAULT, 'Horror') RETURNING id";
		row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long categoryIdHorror = row.getLong("id");

		// assign category to venue
		sql = "INSERT INTO category_venue (venue_id, category_id) VALUES (?,?)";
		jdbcTemplate.update(sql, venueIdWhisperingWoman, categoryIdHorror);

		// test venue returns 1 category
		categories = dao.getCategoriesByVenueId(venueIdWhisperingWoman);
		Assert.assertEquals(1, categories.size());

		// assign second category to venue
		sql = "INSERT INTO category_venue (venue_id, category_id) VALUES (?,?)";
		jdbcTemplate.update(sql, venueIdWhisperingWoman, categoryIdDrama);

		// test venue returns 2 categories
		categories = dao.getCategoriesByVenueId(venueIdWhisperingWoman);
		Assert.assertEquals(2, categories.size());

		// assign category to non tested venue
		sql = "INSERT INTO category_venue (venue_id, category_id) VALUES (?,?)";
		jdbcTemplate.update(sql, venueIdScreamingToy, categoryIdHorror);

		// Test to make sure tested venue still returns only 2 categories
		categories = dao.getCategoriesByVenueId(venueIdWhisperingWoman);
		Assert.assertEquals(2, categories.size());

		// Test venue with no category
		categories = dao.getCategoriesByVenueId(venueIdSingingBelle);
		Assert.assertEquals(0, categories.size());

	}

	// Test getAllCategories
	@Test
	public void get_all_categories() {
		truncateCategory();

		// test empty table returns 0 size
		List<String> categories = dao.getAllCategories();
		Assert.assertEquals(0, categories.size());

		// Add categories
		String sql = "INSERT INTO category (id, name) VALUES (DEFAULT, ?)";
		jdbcTemplate.update(sql, "Comedy");

		sql = "INSERT INTO category (id, name) VALUES (DEFAULT, ?)";
		jdbcTemplate.update(sql, "Drama");

		sql = "INSERT INTO category (id, name) VALUES (DEFAULT, ?)";
		jdbcTemplate.update(sql, "Horror");

		// test table size now returns 3
		categories = dao.getAllCategories();
		Assert.assertEquals(3, categories.size());

		// test that the correct names return
		categories = dao.getAllCategories();
		Assert.assertEquals("Comedy", categories.get(0));

		categories = dao.getAllCategories();
		Assert.assertEquals("Drama", categories.get(1));

		categories = dao.getAllCategories();
		Assert.assertEquals("Horror", categories.get(2));

	}

	private void truncateCategory() {
		String sql = "TRUNCATE category CASCADE";
		jdbcTemplate.update(sql);
	}

}
