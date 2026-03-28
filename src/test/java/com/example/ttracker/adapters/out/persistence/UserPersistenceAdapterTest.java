package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.application.service.MySqlTestcontainerBase;
import com.example.ttracker.security.adapters.out.persistence.UserPersistenceAdapter;
import com.example.ttracker.security.domain.model.Role;
import com.example.ttracker.security.domain.model.User;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserPersistenceAdapterTest extends MySqlTestcontainerBase {

    @Autowired
    private UserPersistenceAdapter userPersistenceAdapter;

    @Autowired
    private JdbcTemplate template;

    public static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) ->
        new User(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("profile_image_url"),
            rs.getString("email"),
            rs.getString("password_hash"),
            Role.valueOf(rs.getString("role")),
            rs.getTimestamp("created_at").toInstant()
        );

    @Test
    void save_should_persist_user_and_be_queryable_via_jdbc() {
        // given
        Instant now = Instant.now();
        User user = new User(null, "John Doe", null, "email@gmail.com", "hashed", Role.USER, now);

        // act
        User saved = userPersistenceAdapter.save(user);

        // assert
        assertNotNull(saved.id());
        User dbUser = template.queryForObject(
            "SELECT id,name,profile_image_url,email,password_hash,role,created_at FROM users WHERE id=?",
            USER_ROW_MAPPER,
            saved.id()
        );
        assertNotNull(dbUser);
        assertEquals(saved.id(), dbUser.id());
        assertEquals("John Doe", dbUser.name());
        assertNull(dbUser.profileImageUrl());
        assertEquals("email@gmail.com", dbUser.email());
        assertEquals("hashed", dbUser.passwordHash());
        assertEquals(Role.USER, dbUser.role());
        assertNotNull(dbUser.createdAt());
    }

    @Test
    void findByid_and_be_queryable_via_jdbc() {
        Instant now = Instant.now();
        template.update(
            "INSERT INTO users (id,name,profile_image_url,email,password_hash,role,created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
            1L, "John Doe", null, "email@gmail.com", "hashed", "USER", now
        );
        User dbUser = template.queryForObject(
            "SELECT id,name,profile_image_url,email,password_hash,role,created_at FROM users WHERE id=?",
            USER_ROW_MAPPER,
            1L
        );
        assertNotNull(dbUser);
        assertEquals(1L, dbUser.id());
        assertEquals("John Doe", dbUser.name());
        assertNull(dbUser.profileImageUrl());
        assertEquals("email@gmail.com", dbUser.email());
        assertEquals("hashed", dbUser.passwordHash());
        assertEquals(Role.USER, dbUser.role());
        assertNotNull(dbUser.createdAt());
    }

    @Test
    void findAll_and_be_queryable_via_jdbc() {
        Instant now = Instant.now();
        template.update(
            "INSERT INTO users (id,name,profile_image_url,email,password_hash,role,created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
            1L, "John Doe", null, "email@gmail.com", "hashed", "USER", now
        );
        template.update(
            "INSERT INTO users (id,name,profile_image_url,email,password_hash,role,created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
            2L, "Jane Doe", null, "email1@gmail.com", "hashed", "USER", now
        );
        var users = template.query(
            "SELECT id,name,profile_image_url,email,password_hash,role,created_at FROM users ORDER BY id",
            USER_ROW_MAPPER
        );
        assertThat(users.get(0).id()).isEqualTo(1L);
        assertThat(users.get(0).name()).isEqualTo("John Doe");
        assertThat(users.get(0).profileImageUrl()).isNull();
        assertThat(users.get(0).email()).isEqualTo("email@gmail.com");
        assertThat(users.get(0).passwordHash()).isEqualTo("hashed");
        assertThat(users.get(0).role()).isEqualTo(Role.USER);

        assertThat(users.get(1).id()).isEqualTo(2L);
        assertThat(users.get(1).name()).isEqualTo("Jane Doe");
        assertThat(users.get(1).profileImageUrl()).isNull();
        assertThat(users.get(1).email()).isEqualTo("email1@gmail.com");
        assertThat(users.get(1).passwordHash()).isEqualTo("hashed");
        assertThat(users.get(1).role()).isEqualTo(Role.USER);
    }

    @Test
    void findByEmail_and_be_queryable_via_jdbc() {
        Instant now = Instant.now();
        template.update(
            "INSERT INTO users (id,name,profile_image_url,email,password_hash,role,created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
            1L, "John Doe", null, "email@gmail.com", "hashed", "USER", now
        );
        User dbUser = template.queryForObject(
            "SELECT id,name,profile_image_url,email,password_hash,role,created_at FROM users WHERE email=?",
            USER_ROW_MAPPER,
            "email@gmail.com"
        );
        assertNotNull(dbUser);
        assertEquals(1L, dbUser.id());
        assertEquals("John Doe", dbUser.name());
        assertNull(dbUser.profileImageUrl());
        assertEquals("email@gmail.com", dbUser.email());
        assertEquals("hashed", dbUser.passwordHash());
        assertEquals(Role.USER, dbUser.role());
        assertNotNull(dbUser.createdAt());
    }
}