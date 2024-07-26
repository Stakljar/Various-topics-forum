package com.example.various_topics_forum.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.various_topics_forum.auth.dto.AuthRequest;
import com.example.various_topics_forum.auth.dto.AuthResponse;
import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.auth.dto.RegistrationRequest;
import com.example.various_topics_forum.exception.exceptions.EmailException;
import com.example.various_topics_forum.exception.exceptions.UserAlreadyEnabledException;
import com.example.various_topics_forum.security.JwtService;
import com.example.various_topics_forum.user.User;
import com.example.various_topics_forum.user.UserMapper;
import com.example.various_topics_forum.user.UserRepository;
import com.example.various_topics_forum.user.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender javaMailSender;

    @Value("${application.security.jwt.email-confirmation-token.expiration-time}")
    private long emailConfirmationTokenExpiration;

    @Value("${application.security.jwt.access-token.expiration-time}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-token.expiration-time}")
    private long refreshTokenExpiration;

    public AuthResponse authenticateUser(AuthRequest request, HttpServletResponse response) {
        Authentication authentication =  authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        final User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(user, accessTokenExpiration);
        String refreshToken = jwtService.generateToken(user, refreshTokenExpiration);
        setRefreshTokenCookie(response, refreshToken);
        return new AuthResponse(user.getId(), accessToken, user.getRole().ordinal());
    }

    @Transactional
    public IdResponse registerUser(RegistrationRequest registrationRequest) {
        User user = userMapper.toUser(registrationRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String emailConfirmationToken = jwtService.generateToken(user, emailConfirmationTokenExpiration);
        boolean isPresent = userRepository.findByEmail(user.getEmail()).isPresent();
        User savedUser = userRepository.save(user);

        // It will throw exception before this anyways if the user exists,
        // it serves so that e-mail is not sent in case user exists
        if (isPresent) {
            return null;
        }
        this.sendEmailWithButton(user.getEmail(), emailConfirmationToken, "please click the button below to confirm your e-mail.", 
                "Confirm e-mail", "http://localhost:3000/confirm-email");
    	return userMapper.toIdResponse(savedUser);
    }

    public void resendConfirmationEmail(String email) {
        User user = (User) userService.loadUserByUsername(email);
        if (user.isEnabled()) {
            throw new UserAlreadyEnabledException("E-mail has already been confirmed");
        }
        String emailConfirmationToken = jwtService.generateToken(user, emailConfirmationTokenExpiration);
    	this.sendEmailWithButton(user.getEmail(), emailConfirmationToken, "please click the button below to confirm your e-mail.", 
    	        "Confirm e-mail", "http://localhost:3000/confirm-email");
    }

    public void sendPasswordResetEmail(String email) {
        User user = (User) userService.loadUserByUsername(email);
        String emailConfirmationToken = jwtService.generateToken(user, emailConfirmationTokenExpiration);
        this.sendEmailWithButton(user.getEmail(), emailConfirmationToken, "please click the button below to reset your password.",
                "Reset password", "http://localhost:3000/new-password");
    }

    public AuthResponse confirmRegistrationEmail(String email, String token, HttpServletResponse response) {
    	User user = (User) userService.loadUserByUsername(email);
    	if (user.isEnabled()) {
    	    throw new UserAlreadyEnabledException("E-mail has already been confirmed");
    	}
    	boolean isValid;
    	try {
    	    isValid = jwtService.isTokenValid(token, user);
    	}
    	catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Registration token has expired");
        }
    	if (!isValid) {
    		throw new JwtException("Invalid registration token");
    	}
    	user.setEnabled(true);
    	User savedUser = userRepository.save(user);
        String accessToken = jwtService.generateToken(user, accessTokenExpiration);
        String refreshToken = jwtService.generateToken(user, refreshTokenExpiration);
        setRefreshTokenCookie(response, refreshToken);
        return new AuthResponse(savedUser.getId(), accessToken, savedUser.getRole().ordinal());
    }

    public void resetPassword(String email, String token, String newPassword) {
        User user = (User) userService.loadUserByUsername(email);
        boolean isValid;
        try {
            isValid = jwtService.isTokenValid(token, user);
        }
        catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Registration token has expired");
        }
        if (!isValid) {
            throw new JwtException("Invalid registration token");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public AuthResponse refreshUserAccessToken(String refreshToken) {
        if (refreshToken == null) {
            throw new JwtException("Token not supplied");
        }
        String username = jwtService.extractUsername(refreshToken);
        User user =  (User) userService.loadUserByUsername(username);
        boolean isValid;
        try {
            isValid = jwtService.isTokenValid(refreshToken, user);
        }
        catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token has expired");
        }
        if (!isValid) {
            throw new JwtException("Invalid token");
        }
        String accessToken = jwtService.generateToken(user, accessTokenExpiration);
        return new AuthResponse(user.getId(), accessToken, user.getRole().ordinal());
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    	Cookie cookie = new Cookie("refresh_token", refreshToken);
    	cookie.setHttpOnly(true);
    	cookie.setSecure(true);
    	response.addCookie(cookie);
    }

    @Async
    public void sendEmailWithButton(String to, String token, String text, String buttonText, String link) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    
            String htmlContent = "<html>" +
                    "    <head>" +
                    "        <style>" +
                    "            body {" +
                    "                font-family: sans-serif;" +
                    "                padding: 10px;" +
                    "            }\n" +
                    "            h1 {" +
                    "                width: fit-content;" +
                    "                font-size: 2.4rem;" +
                    "                background: linear-gradient(120deg, #665f5a, #daa582);" +
                    "                -webkit-background-clip: text;" +
                    "                -webkit-text-fill-color: transparent;" +
                    "                cursor: pointer;" +
                    "            }" +
                    "            body > div {" +
                    "                background-color: white;" +
                    "                display: inline-block;" +
                    "                padding: 20px;" +
                    "            }\n" +
                    "            .button {" +
                    "                display: inline-block;" +
                    "                margin-top: 15px;" +
                    "                margin-bottom: 25px;" +
                    "                margin-left: 30px;" +
                    "                padding: 10px 20px;" +
                    "                text-decoration: none;" +
                    "                color: white;" +
                    "                border-radius: 15px;" +
                    "                cursor: pointer;" +
                    "                background-color: #5c3215;" +
                    "            }" +
                    "            .button:hover, .button:active {" +
                    "                background-color: #b4774a;" +
                    "            }" +
                    "        </style>" +
                    "    </head>" +
                    "    <body>" +
                    "        <div>" +
                    "           <h1>Topics Unraveled</h1>" +
                    "           <p>Greetings,<br><br>" + text + "</p>" +
                    "           <a href=\"" + link + "?email=" + to + "&token=" + token +
                    "               \"class=\"button\">" + buttonText + "</a>" +
                    "           <div><i>Link will expire in " + emailConfirmationTokenExpiration / 60000 + " minutes.</i><div>" +
                    "        <div>" +
                    "    </body>" +
                    "</html>";
    
            helper.setTo(to);
            helper.setSubject("E-mail confirmation for Topics Unraveled website");
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        }
        catch (Exception e) {
            throw new EmailException("Server failed to send e-mail");
        }
    }
}
