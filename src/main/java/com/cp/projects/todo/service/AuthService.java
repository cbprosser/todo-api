package com.cp.projects.todo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cp.projects.todo.model.dto.AuthDTO;
import com.cp.projects.todo.model.dto.RecoverDTO;
import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.model.table.VerificationToken;
import com.cp.projects.todo.repo.AuthRepo;
import com.cp.projects.todo.repo.VerificationTokenRepo;
import com.cp.projects.todo.util.EmailUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AuthService {

  @Autowired
  private AuthRepo authRepo;

  @Autowired
  private VerificationTokenRepo verificationTokenRepo;

  public UserDTO findUserByUsernameAndPassword(AuthDTO authDTO) {
    User authorizedUser = authRepo.findUserByUsernameAndPassword(authDTO.getUsername(), authDTO.getPassword());
    return new UserDTO(authorizedUser);
  }

  public boolean insertUser(User user) {
    if (user == null)
      return false;
    authRepo.save(user);
    return true;
  }

  public void createUser(@NonNull User user) {
    authRepo.save(user.getUsername(), user.getPassword(), user.getEmail());
  }

  @SuppressWarnings("null")
  public void sendRecoveryEmail(RecoverDTO recoverDTO) throws Exception {
    log.info("Finding user");
    User foundUser = authRepo.findUserByEmail(recoverDTO.getEmail());

    if (!foundUser.isEnabled()) {
      throw new Exception("User is not enabled.");
    }

    log.info("Creating verification token");
    VerificationToken token = verificationTokenRepo.save(VerificationToken.builder()
        .user(foundUser)
        .build());

    log.info("Verification token created, sending recovery email.");
    EmailUtil.sendRecoveryEmail(token);
  }

  @Transactional
  @SuppressWarnings("null")
  public void recoverUser(UUID token, RecoverDTO recoverDTO) throws Exception {
    log.info("Locating verification token.");
    Optional<VerificationToken> optVerificationToken = verificationTokenRepo.findByToken(token);
    if (!optVerificationToken.isPresent()) {
      throw new Exception("Verification token not found.");
    }
    VerificationToken verificationToken = optVerificationToken.get();

    log.info("Encoding Password.");
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String passRaw = recoverDTO.getPassword();
    String passEncoded = encoder.encode(passRaw);

    log.info("Removing verification token.");
    verificationTokenRepo.delete(verificationToken);

    log.info("Saving user with new password.");
    User encodedUser = verificationToken.getUser().toBuilder().password(passEncoded).build();
    authRepo.save(encodedUser);
  }
}
