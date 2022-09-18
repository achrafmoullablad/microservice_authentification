package com.tchat.ms_authentification.service.impl;

import com.tchat.ms_authentification.bean.ConfirmationEmail;
import com.tchat.ms_authentification.bean.Role;
import com.tchat.ms_authentification.bean.User;
import com.tchat.ms_authentification.dao.UserDao;
import com.tchat.ms_authentification.dto.UserSigninDTO;
import com.tchat.ms_authentification.dto.UserSignupDTO;
import com.tchat.ms_authentification.email.EmailSender;
import com.tchat.ms_authentification.mapper.UserMapperImpl;
import com.tchat.ms_authentification.service.facade.UserService;
import com.tchat.ms_authentification.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final RoleServiceImpl roleService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final ConfirmationEmailServiceImpl emailService;

    private final EmailSender emailSender;

    private final UserMapperImpl userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<Object> signIn(UserSigninDTO user) {
        if(user.getUsernameOrEmail() != null && user.getPassword() != null){
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsernameOrEmail(), user.getPassword()));
            }catch (BadCredentialsException e){
                return new ResponseEntity<>("Email not confirmed", HttpStatus.valueOf(403));
            }
            User u = loadUserByUsername(user.getUsernameOrEmail());
            String token = jwtUtil.generateToken(u);

            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("user", u);
            return new ResponseEntity<>(map, HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>("Send all the values", HttpStatus.valueOf(400));
    }

    @Override
    public ResponseEntity<String> signUp(UserSignupDTO user) {
        User u1 = userDao.findByUsername(user.getUsername());
        User u2 = userDao.findByEmail(user.getEmail());

        if(u1 != null || u2 != null)
            return new ResponseEntity<>("User already exist", HttpStatus.valueOf(400));

        Role role = roleService.findByName("ROLE_USER");
        if(role == null)
            role = roleService.save(new Role("ROLE_USER"));
        User u = userMapper.fromSignupUser(user);
        u.setAuthorities(List.of(role));
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(u);

        sendMail(u);

        return new ResponseEntity<>("User created", HttpStatus.valueOf(201));
    }

    @Override
    public User loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userDao.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (user == null)
            throw new UsernameNotFoundException("user not found");
        return user;
    }

    public ResponseEntity<String> confirmToken(String token){
        ConfirmationEmail confirmationEmail = emailService.findByToken(token);

        if (confirmationEmail == null)
            return new ResponseEntity<>("Token not found", HttpStatus.valueOf(405));

        if(confirmationEmail.getConfirmedAt() != null)
            return new ResponseEntity<>("Email already confirmed", HttpStatus.valueOf(406));

        LocalDateTime expiresAt = confirmationEmail.getExpiresAt();

        if (expiresAt.isBefore(LocalDateTime.now())){
            sendMail(confirmationEmail.getUser());
            return new ResponseEntity<>("Token expired", HttpStatus.valueOf(407));
        }

        emailService.setConfirmed(token, LocalDateTime.now());

        User user = confirmationEmail.getUser();
        userDao.updateLocking(user.getEmail(), false);

        return new ResponseEntity<>("Email confirmed", HttpStatus.valueOf(200) );
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public List<User> findUsersByIdIn(List<Long> ids){
        return userDao.findUsersByIdInAndIsLockedFalse(ids);
    }

    public User findUserById(Long userId){
        Optional<User> optionalUser = userDao.findById(userId);
        return optionalUser.orElse(null);
    }

    @Override
    public ResponseEntity<String> lockUser(Long userId) {
        Optional<User> optionalUser = userDao.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setLocked(true);
            userDao.save(user);
            return new ResponseEntity<>("User locked", HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>("User not found", HttpStatus.valueOf(404));
    }

    private void sendMail(User user){
        String token = UUID.randomUUID().toString();

        ConfirmationEmail confirmationEmail = new ConfirmationEmail(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);

        emailService.save(confirmationEmail);
        emailSender.send(
                user.getEmail(),
                buildEmail(
                        user.getFullname(),
                        "http://localhost:8080/api/v1/authentication/confirm?token=" + token));
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}
