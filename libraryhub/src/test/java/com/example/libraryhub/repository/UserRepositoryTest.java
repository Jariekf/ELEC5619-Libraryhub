package com.example.libraryhub.repository;

import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Customer testUser;

    @BeforeEach
    void setUp() {
        testUser = new Customer(null, "testuser", "test@example.com", "hashedPassword");
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("testuser");
        
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testSaveUser() {
        Customer newUser = new Customer(null, "newuser", "new@example.com", "hash");
        Customer saved = userRepository.save(newUser);
        
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
    }

    @Test
    void testDeleteUser() {
        Long userId = testUser.getId();
        userRepository.deleteById(userId);
        
        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    void testFindAll() {
        assertTrue(userRepository.findAll().size() >= 1);
    }
}
