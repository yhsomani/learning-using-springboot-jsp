package com.ruraledu.repository;

import com.ruraledu.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StudentBatchDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchInsertStudents(List<User> students) {
        String sql = "INSERT INTO users (username, password, full_name, email, role, points) VALUES (?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(sql, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
            @Override
            public void setValues(@org.springframework.lang.NonNull PreparedStatement ps, int i) throws java.sql.SQLException {
                User student = students.get(i);
                ps.setString(1, student.getUsername());
                ps.setString(2, student.getPassword());
                ps.setString(3, student.getFullName());
                ps.setString(4, student.getEmail());
                ps.setString(5, student.getRole().name());
                ps.setInt(6, student.getPoints());
            }

            @Override
            public int getBatchSize() {
                return students.size();
            }
        });
    }
}
