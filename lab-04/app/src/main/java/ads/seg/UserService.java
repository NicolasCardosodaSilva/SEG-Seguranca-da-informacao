package ads.seg;

import javax.xml.transform.Source;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class UserService {
    private UserRepository repository;
    private String algHash;
    private final int ITERATIONS = 1000;
    private final int KEY_LENGHT = 128;
    private SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private String selecionarAlgHash(String hashAlg) {
        if (hashAlg == null) {
            return "BCrypt";
        }
        if (hashAlg.equalsIgnoreCase("BCrypt") || hashAlg.equalsIgnoreCase("Bcrypt")) {
            return "BCrypt";
        }
        else if (hashAlg.equalsIgnoreCase("PBKDF2") || hashAlg.equalsIgnoreCase("pbkdf2")) {
            return "PBKDF2WithHmacSHA512";
        }
        else {
            return "BCrypt";
        }
    }

    public UserService(UserRepository db, String hashAlg) {
        this.repository = db;
        this.algHash = selecionarAlgHash(hashAlg);
    }

    public UserService(UserRepository repository) {
        this.algHash = "BCrypt";
        this.repository = repository;
    }

    public boolean register(String login, String password) throws UserAlreadyExistsException {

        if (repository.findByLogin(login) == null) {

            if (algHash.equalsIgnoreCase("BCrypt")) {
                return repository.save(new User(login, new String(PasswordHashing.hashPasswordWithBCrypt(password.toCharArray()), StandardCharsets.UTF_8)));
            }

            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);
            String salt64 = ENCODER.encodeToString(salt);
            try {
                return repository.save(new User(login, salt64 + "$" + ENCODER.encodeToString(PasswordHashing.hashPasswordWithPBKDF2(password.toCharArray(), salt, algHash, ITERATIONS, KEY_LENGHT))));
            } catch (NoSuchAlgorithmException e) {
                return false;
            } catch (InvalidKeySpecException e) {
                return false;
            }
        } else {
            throw new UserAlreadyExistsException();
        }
    }

    public boolean updatePassword(String login, String senhaAtual, String novaSenha) throws UserNotFoundException, InvalidLoginException, InvalidPasswordException {
        if (repository.findByLogin(login) == null) {
            throw new UserNotFoundException();
        }

        boolean autenticacao = authenticate(login, senhaAtual);
        if(!autenticacao) {
            throw new InvalidPasswordException();
        }
            if (algHash.equalsIgnoreCase("BCrypt")) {
                return repository.update(new User(login, new String(PasswordHashing.hashPasswordWithBCrypt(novaSenha.toCharArray()), StandardCharsets.UTF_8)));
            }

            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);
            String salt64 = ENCODER.encodeToString(salt);

            try {
                return repository.update(new User(login, salt64 + "$" + ENCODER.encodeToString(PasswordHashing.hashPasswordWithPBKDF2(novaSenha.toCharArray(), salt, algHash, ITERATIONS, KEY_LENGHT))));
            } catch (NoSuchAlgorithmException e) {
                return false;
            } catch (InvalidKeySpecException e) {
                return false;
            }

    }

    public boolean authenticate(String login, String senha) throws InvalidLoginException, InvalidPasswordException {
        User userBanco = repository.findByLogin(login);
        if (userBanco == null) {
            throw new InvalidLoginException();
        }

        if (algHash.equalsIgnoreCase("PBKDF2WithHmacSHA512")) {
            String[] parts = userBanco.getPassword().split("\\$", 2);

            byte[] salt = DECODER.decode(parts[0]);
            byte[] hashedPassword = DECODER.decode(parts[1]);

            try {
                boolean isValid = PasswordHashing.verifyPasswordWithPBKDF2(senha.toCharArray(), salt, algHash, ITERATIONS, KEY_LENGHT, hashedPassword);
                if (!isValid) {
                    throw new InvalidPasswordException();
                }
                return true;
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new InvalidPasswordException();
            }
        }

        boolean isValidBCrypt = PasswordHashing.verifyPasswordWithBCrypt(senha.toCharArray(), userBanco.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!isValidBCrypt) {
            throw new InvalidPasswordException();
        }

        return true;
    }

    public void setAlgHash(String algHash) {
        this.algHash = selecionarAlgHash(algHash);
    }
}
