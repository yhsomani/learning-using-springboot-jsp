package com.ruraledu.repository;

import com.ruraledu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.username = :username AND u.deleted = false")
    Optional<User> findByUsername(@org.springframework.data.repository.query.Param("username") String username);
    
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmail(@org.springframework.data.repository.query.Param("email") String email);
    
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.parent.id = :parentId AND u.deleted = false")
    List<User> findByParentId(@org.springframework.data.repository.query.Param("parentId") Long parentId);
    
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.deleted = false ORDER BY u.points DESC")
    List<User> findTopStudents(Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.deleted = false ORDER BY u.points DESC")
    List<User> findTop5ByOrderByPointsDesc();

    @org.springframework.data.jpa.repository.Query("SELECT count(u) FROM User u WHERE u.role = :role AND u.points > :points AND u.deleted = false")
    long countByRoleAndPointsGreaterThan(@org.springframework.data.repository.query.Param("role") User.Role role, @org.springframework.data.repository.query.Param("points") int points);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.deleted = false ORDER BY u.id DESC")
    List<User> findTop10ByOrderByIdDesc();
    
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAll(Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.deleted = false")
    List<User> findAllActive();

    @org.springframework.data.jpa.repository.Query("SELECT count(u) FROM User u WHERE u.deleted = false")
    long count();
}
