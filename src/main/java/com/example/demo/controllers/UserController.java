package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user==null) {
            logger.error("Could not find user '{}'", username);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        // exit early for bad account creation request
        if (!validCreateUserRequest(createUserRequest)) {
            logger.error(
                "Invalid CreateUserRequest. Could not create new user with username: {}",
                createUserRequest.getUsername()
            );
            return ResponseEntity.badRequest().build();
        }

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
        userRepository.save(user);

        logger.info("Created new user account with username: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    /**
     * Validates a CreateUserRequest object for creating a new user account.
     * To be valid, the request must pass the following tests:
     * 1. The username cannot already exist in our repository.
     * 2. The password must meet our password criteria.
     * 3. The password and confirmPassword must be equal.
     *
     * @param createUserRequest An object containing the information for creating
     *                          a new user.
     * @return True if all the above tests pass.
     */
    private boolean validCreateUserRequest(CreateUserRequest createUserRequest) {
        return validPasswordCriteriaMet(createUserRequest.getPassword())
                   && createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())
                   && userRepository.findByUsername(createUserRequest.getUsername()) == null;
    }

    /**
     * Confirms user password entry matches our criteria for a valid password.
     * Currently, criteria is only at least 7 characters.
     *
     * @param password The password entered by the user.
     * @return True if password matches our criteria for valid password.
     */
    private boolean validPasswordCriteriaMet(String password) {
        return password.length() >= 7;
    }
}
