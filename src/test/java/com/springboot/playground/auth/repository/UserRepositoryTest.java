package com.springboot.playground.auth.repository;

import com.springboot.playground.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername_Success() {
        User user = new User("dbuser", "password123", "USER");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("dbuser");
        assertTrue(found.isPresent());
        assertEquals("dbuser", found.get().getUsername());
        assertEquals("password123", found.get().getPassword());
        assertEquals("USER", found.get().getRoles());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testSave_DuplicateUsername_ThrowsException() {
        User user1 = new User("unique_user", "password123", "USER");
        userRepository.saveAndFlush(user1);

        User user2 = new User("unique_user", "anotherpassword", "ADMIN");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }
}
