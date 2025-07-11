package edu.uniquindio.dentalmanagementsystembackend.Account;

import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.*;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.ValidationCode;
import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.config.JWTUtils;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.AuthenticationException;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.exception.AccountNotFoundException;
import edu.uniquindio.dentalmanagementsystembackend.repository.RecoveryCodeRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.validationCodeRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.ServiciosCuentaImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

/**
 * Test class for account-related functionalities using Mockito.
 * This class contains unit tests for operations offered by the ServiciosCuenta service.
 */
@ExtendWith(MockitoExtension.class)
class AccountTest {

    @Mock
    private CuentaRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private validationCodeRepository validationCodeRepository;

    @Mock
    private RecoveryCodeRepository recoveryCodeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private ServiciosCuentaImpl serviciosCuenta;

    private Account testAccount;
    private User testUser;
    private LoginDTO testLoginDTO;
    private CrearCuentaDTO testCrearCuentaDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setEmail("test@example.com");
        testAccount.setPassword("encodedPassword");
        testAccount.setRol(Rol.PACIENTE);
        testAccount.setStatus(AccountStatus.ACTIVE);

        // Configurar ValidationCode para el test
        ValidationCode validationCode = new ValidationCode();
        validationCode.setCode("12345");
        testAccount.setRegistrationValidationCode(validationCode);

        testUser = new User();
        testUser.setIdNumber("1234567890");
        testUser.setName("Test");
        testUser.setLastName("User");
        testUser.setPhoneNumber("3001234567");
        testUser.setAddress("Test Address");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setAccount(testAccount);
        testAccount.setUser(testUser);

        testLoginDTO = new LoginDTO("1234567890", "password123");
        testCrearCuentaDTO = new CrearCuentaDTO(
                "1234567890",
                "Test",
                "User",
                "3001234567",
                "Test Address",
                LocalDate.of(1990, 1, 1),
                "test@example.com",
                "Password123!"
        );
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        when(accountRepository.findByIdUNumber("1234567890")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(anyString(), anyMap())).thenReturn("mocked-jwt-token");

        // Act
        TokenDTO result = serviciosCuenta.login(testLoginDTO);

        // Assert
        assertNotNull(result);
        assertNotNull(result.token());
        verify(accountRepository).findByIdUNumber("1234567890");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtils).generateToken(anyString(), anyMap());
        // NO verificar userRepository.findByIdNumber() porque no se llama
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(accountRepository.findByIdUNumber("1234567890")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            serviciosCuenta.login(testLoginDTO);
        });
        verify(accountRepository).findByIdUNumber("1234567890");
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        when(accountRepository.findByIdUNumber("1234567890")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            serviciosCuenta.login(testLoginDTO);
        });
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void testLogin_AccountInactive() {
        // Arrange
        testAccount.setStatus(AccountStatus.INACTIVE);
        when(accountRepository.findByIdUNumber("1234567890")).thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            serviciosCuenta.login(testLoginDTO);
        });
    }

    @Test
    void testCrearCuenta_Success() throws Exception, EmailAlreadyExistsException, UserAlreadyExistsException, DatabaseOperationException, EmailSendingException {
        // Arrange
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByIdNumber("1234567890")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        doNothing().when(emailService).sendCodevalidation(anyString(), anyString());

        // Act
        String result = serviciosCuenta.crearCuenta(testCrearCuentaDTO);

        // Assert
        assertNotNull(result);
        verify(accountRepository).findByEmail("test@example.com");
        verify(userRepository).existsByIdNumber("1234567890");
        verify(passwordEncoder).encode("Password123!");
        verify(accountRepository).save(any(Account.class));
        verify(emailService).sendCodevalidation(anyString(), anyString());
    }

    @Test
    void testCrearCuenta_EmailAlreadyExists() {
        // Arrange
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> {
            serviciosCuenta.crearCuenta(testCrearCuentaDTO);
        });
        verify(accountRepository).findByEmail("test@example.com");
    }

    @Test
    void testCrearCuenta_UserAlreadyExists() {
        // Arrange
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByIdNumber("1234567890")).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            serviciosCuenta.crearCuenta(testCrearCuentaDTO);
        });
        verify(userRepository).existsByIdNumber("1234567890");
    }

    @Test
    void testEliminarCuenta_Success() throws Exception {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        serviciosCuenta.eliminarCuenta(1L);

        // Assert
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
        assertEquals(AccountStatus.INACTIVE, testAccount.getStatus());
    }

    @Test
    void testEliminarCuenta_AccountNotFound() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            serviciosCuenta.eliminarCuenta(1L);
        });
        verify(accountRepository).findById(1L);
    }

    @Test
    void testObtenerPerfil_Success() throws Exception, UserNotFoundException, AccountNotFoundException {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        PerfilDTO result = serviciosCuenta.obtenerPerfil(1L);

        // Assert
        assertNotNull(result);
        assertEquals("1234567890", result.idNumber());
        assertEquals("Test", result.name());
        assertEquals("User", result.lastName());
        assertEquals("3001234567", result.phoneNumber());
        assertEquals("Test Address", result.address());
        assertEquals(LocalDate.of(1990, 1, 1), result.birthDate());
        assertEquals("test@example.com", result.email());
        verify(accountRepository).findById(1L);
    }

    @Test
    void testObtenerPerfil_AccountNotFound() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            serviciosCuenta.obtenerPerfil(1L);
        });
        verify(accountRepository).findById(1L);
    }

    @Test
    void testActualizarUsuario_Success() throws Exception, UserNotFoundException, AccountNotFoundException {
        // Arrange
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO(
                "Nuevo Nombre",
                "Nuevo Apellido",
                "3001234568",
                "Nueva Dirección",
                "nuevo@email.com"
        );
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        String result = serviciosCuenta.actualizarUsuario(1L, dto);

        // Assert
        assertEquals("Usuario actualizado exitosamente.", result);
        verify(accountRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testActualizarUsuario_AccountNotFound() {
        // Arrange
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO(
                "Nuevo Nombre",
                "Nuevo Apellido",
                "3001234568",
                "Nueva Dirección",
                "nuevo@email.com"
        );
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            serviciosCuenta.actualizarUsuario(1L, dto);
        });
        verify(accountRepository).findById(1L);
    }

    @Test
    void testUpdatePassword_Success() throws Exception, InvalidCurrentPasswordException, PasswordMismatchException {
        // Arrange
        UpdatePasswordDTO dto = new UpdatePasswordDTO(
                "oldPassword",
                "newPassword123!",
                "newPassword123!"
        );
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123!")).thenReturn("newEncodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        String result = serviciosCuenta.updatePassword(1L, dto);

        // Assert
        assertEquals("La contraseña ha sido cambiada exitosamente.", result);
        verify(accountRepository).findById(1L);
        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword123!");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testUpdatePassword_InvalidCurrentPassword() {
        // Arrange
        UpdatePasswordDTO dto = new UpdatePasswordDTO(
                "wrongPassword",
                "newPassword123!",
                "newPassword123!"
        );
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCurrentPasswordException.class, () -> {
            serviciosCuenta.updatePassword(1L, dto);
        });
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void testUpdatePassword_PasswordMismatch() {
        // Arrange
        UpdatePasswordDTO dto = new UpdatePasswordDTO(
                "oldPassword",
                "newPassword123!",
                "differentPassword"
        );
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // Act & Assert
        assertThrows(PasswordMismatchException.class, () -> {
            serviciosCuenta.updatePassword(1L, dto);
        });
    }
}
