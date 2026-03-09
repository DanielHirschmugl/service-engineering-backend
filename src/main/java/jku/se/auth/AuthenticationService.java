package jku.se.auth;

import jakarta.transaction.Transactional;
import jku.se.config.JwtService;
import jku.se.exceptions.NoAccessException;
import jku.se.exceptions.UserAlreadyExistsException;
import jku.se.exceptions.UserNotFoundException;
import jku.se.exceptions.WrongInputException;
import jku.se.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request)
            throws UserAlreadyExistsException, WrongInputException {

        var user = User.builder()
                .username(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .phonenumber(request.getPhonenumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Sie haben bereits einen Account.");
        }
        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Dieser Benutzername ist nicht verfügbar.");
        }

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().toString())
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) throws UserNotFoundException, NoAccessException {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Benutzer nicht gefunden"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NoAccessException("Falsches Passwort");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword()
                )
        );
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().toString())
                .build();
    }
}
