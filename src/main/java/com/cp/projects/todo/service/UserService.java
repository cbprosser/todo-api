package com.cp.projects.todo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.model.table.VerificationToken;
import com.cp.projects.todo.repo.UserRepo;
import com.cp.projects.todo.repo.VerificationTokenRepo;
import com.cp.projects.todo.util.EmailUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UserService {

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private VerificationTokenRepo verificationTokenRepo;

  @SuppressWarnings("null")
  @Transactional
  public UserDTO saveUser(User user) throws Exception {
    if (user == null) {
      throw new Exception("User missing");
    }
    log.info("Encrypting password");
    User savedUser = null;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String passRaw = user.getPassword();
    String passEncoded = encoder.encode(passRaw);

    User encodedUser = user.toBuilder().password(passEncoded).build();

    log.info("Saving user with encrypted password");
    savedUser = userRepo.save(encodedUser);

    log.info("{} saved, generating verification token", savedUser);
    VerificationToken token = verificationTokenRepo.save(VerificationToken.builder().user(savedUser).build());

    log.info("Sending confirmation email");
    EmailUtil.sendConfirmUserEmail(savedUser.getEmail(), savedUser.getUsername(), token);
    return new UserDTO(savedUser);
  }

  @SuppressWarnings("null")
  public UserDTO getUserByID(UUID userID) {
    log.info("Finding user");
    return new UserDTO(userRepo.findById(userID).get());
  }

  @SuppressWarnings("null")
  @Transactional
  public void verifyUser(UUID token) throws Exception {
    Optional<VerificationToken> optVerificationToken = verificationTokenRepo.findByToken(token);
    if (!optVerificationToken.isPresent()) {
      throw new Exception("User verification token not found");
    }
    VerificationToken verificationToken = optVerificationToken.get();
    userRepo.save(verificationToken.getUser().toBuilder().enabled(true).build());
    verificationTokenRepo.delete(verificationToken);
  }

}
