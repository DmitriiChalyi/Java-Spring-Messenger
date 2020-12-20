package com.dchalyi.dao;

import com.dchalyi.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// Create
	public Message create(final Message message) {
		final String sql = "INSERT INTO message (text) VALUES (?)";
		final KeyHolder keyHolder = new GeneratedKeyHolder();

		this.jdbcTemplate.update(connection -> {
			PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, message.getText());
			return statement;
		}, keyHolder);

		Integer newMessageId = keyHolder.getKey().intValue();
		message.setId(newMessageId);
		return message;
	}

	// Retrieve
	public List<Message> findAll() {
		final String sql = "SELECT * FROM message";
		return this.jdbcTemplate.query(sql, new MessageMapper());
	}

	// Retrieve
	// Note when no row exists: <https://stackoverflow.com/a/16390624/339302>
	public Optional<Message> findById(Integer id) {
		final String sql = "SELECT id, text FROM message WHERE id = ?";

		return this.jdbcTemplate.query(
				sql,
				rs -> rs.next() ?
						Optional.of(new MessageMapper().mapRow(rs, 1)) :
						Optional.empty(),
				id);
	}

	// Update
	public boolean update(final Message message) {
		final String sql = "UPDATE message SET text=? WHERE id=?";
		final Object[] params = new Object[]{message.getText(), message.getId()};

		return this.jdbcTemplate.update(sql, params) == 1;
	}

	// Delete
	public boolean delete(Integer id) {
		final String sql = "DELETE FROM message WHERE id = ?";
		final Object[] params = new Object[]{id};

		return this.jdbcTemplate.update(sql, params) == 1;
	}

	class MessageMapper implements RowMapper<Message> {
		@Override
		public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Message message = new Message();
			message.setId(rs.getInt("id"));
			message.setText(rs.getString("text"));
			return message;
		}

	}
}
