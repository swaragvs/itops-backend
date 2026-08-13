package com.itops.itopsbackend;

import static org.assertj.core.api.Assertions.assertThat;

import com.itops.itopsbackend.entity.User;
import com.itops.itopsbackend.entity.UserRole;
import com.itops.itopsbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;



@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByEmail() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPasswordHash("encodedPassword");
        user.setRole(UserRole.EMPLOYEE);
        user.setDepartment("IT");

        userRepository.save(user);

        User found = userRepository.findByEmail("alice@example.com").orElseThrow();
        assertThat(found.getName()).isEqualTo("Alice");
        assertThat(found.getRole()).isEqualTo(UserRole.EMPLOYEE);
    }
}
