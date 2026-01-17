package com.example.ttracker.application.service;

import com.example.ttracker.application.port.in.AuthResponse;
import com.example.ttracker.application.port.in.LoginCommand;
import com.example.ttracker.application.port.in.RegisterCommand;
import com.example.ttracker.application.port.out.PasswordHashPort;
import com.example.ttracker.application.port.out.TokenPort;
import com.example.ttracker.application.port.out.UserRepositoryPort;
import com.example.ttracker.domain.model.Role;
import com.example.ttracker.domain.model.User;
import java.time.Instant;
import java.util.Optional;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class AuthServiceTest {
    public static void main(String[] args){
        register_savesUser_returnsUser();
        register_existingEmail_throwsException();
        login_success_returnsToken();
        login_emailNotFound_ThrowsException();
        login_passwordIncorrect_ThrowsException();
        System.out.println("success");
    }
    static class Fixture{
         final UserRepositoryPort userRepository=mock(UserRepositoryPort.class);
         final PasswordHashPort passwordHash=mock(PasswordHashPort.class);
         final TokenPort tokenPort=mock(TokenPort.class);
         final AuthService service=new AuthService(userRepository,passwordHash,tokenPort);
    }
    static void register_savesUser_returnsUser(){
        Fixture f=new Fixture();

        String rawEmail="USER11@Test.Com";
        String normalizedEmail="user11@test.com";
        String rawPassword=" pass123 ";
        String hashed="$2a$hash";

        when(f.userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.empty());
        when(f.passwordHash.hash(rawPassword)).thenReturn(hashed);

        when(f.userRepository.save(any(User.class))).thenAnswer(inv->{
            User u=inv.getArgument(0);
            return new User(
                101L,
                u.email(),
                u.passwordHash(),
                u.role(),
                u.createdAt()
            );
        });
        //act
        User saved=f.service.register(new RegisterCommand(rawEmail,rawPassword));
        //assert(returned value)
        assertThat(saved.id()).isEqualTo(101L);
        assertThat(saved.email()).isEqualTo(normalizedEmail);
        assertThat(saved.passwordHash()).isEqualTo(hashed);

        //verify
        verify(f.userRepository,times(1)).findByEmail(normalizedEmail);

        verify(f.passwordHash,times(1)).hash(rawPassword);

        ArgumentCaptor<User> userCaptor=ArgumentCaptor.forClass(User.class);
        verify(f.userRepository,times(1)).save(userCaptor.capture());
        User userToSave=userCaptor.getValue();

        assertThat(userToSave.id()).isNull();
        assertThat(userToSave.email()).isEqualTo(normalizedEmail);
        assertThat(userToSave.passwordHash()).isEqualTo(hashed);
        assertThat(userToSave.role()).isEqualTo(Role.USER);
        assertThat(userToSave.createdAt()).isNotNull();

        //no token should be generated during register()
        verifyNoInteractions(f.tokenPort);
    }
    static void register_existingEmail_throwsException(){
        Fixture f=new Fixture();

        String normalizedEmail="user11@test.com";

        User existing = new User(1L, normalizedEmail, "hash", Role.USER, Instant.now());

        when(f.userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.of(existing));

        assertThatThrownBy(()->f.service.register(new RegisterCommand("USER11@test.com","pass")))
            .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
        //verify
        verify(f.userRepository,times(1)).findByEmail(normalizedEmail);


        verify(f.userRepository,never()).save(any());

        //no token should be generated during register()
        verifyNoInteractions(f.tokenPort);
        verifyNoInteractions(f.passwordHash);
    }

    static void login_success_returnsToken(){
        Fixture f=new Fixture();

        String normalizedEmail="user11@test.com";
        User user=new User(10L,normalizedEmail,"HASHED",Role.USER,Instant.now());
        when(f.userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.of(user));
        when(f.passwordHash.matches("pass","HASHED")).thenReturn(true);
        when(f.tokenPort.generateToken(10L,normalizedEmail,Role.USER)).thenReturn("jwt-token");

        //act
        AuthResponse resp=f.service.login(new LoginCommand("USER11@Test.com","pass"));

        //assert
        assertThat(resp.token()).isEqualTo("jwt-token");

        verify(f.userRepository,times(1)).findByEmail(normalizedEmail);
        verify(f.passwordHash,times(1)).matches("pass","HASHED");
        verify(f.tokenPort,times(1)).generateToken(10L,normalizedEmail,Role.USER);
    }
    static void login_emailNotFound_ThrowsException(){
        Fixture f=new Fixture();
        when(f.userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(()->f.service.login(new LoginCommand("missing@test.com","pass")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid credentials");

        verify(f.userRepository, times(1)).findByEmail("missing@test.com");
        verifyNoInteractions(f.passwordHash);
        verifyNoInteractions(f.tokenPort);

    }
    static void login_passwordIncorrect_ThrowsException(){
        Fixture f=new Fixture();

        String normalizedEmail="user11@test.com";
        User user=new User(10L,normalizedEmail,"HASHED",Role.USER,Instant.now());
        when(f.userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.of(user));
        when(f.passwordHash.matches("wrong","HASHED")).thenReturn(false);

        //act
     assertThatThrownBy(()->f.service.login(new LoginCommand("USER11@Test.com","wrong")))
         .isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Invalid credentials");

        //assert


        verify(f.userRepository,times(1)).findByEmail(normalizedEmail);
        verify(f.passwordHash,times(1)).matches("wrong","HASHED");
        verify(f.tokenPort,never()).generateToken(any(),any(),any());
    }





}
