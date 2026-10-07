package ads.seg;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserService {
    private UserRepository repository = new InMemory();
    private String algHash;
    private final int ITERATIONS = 1000;
    private  final int KEY_LENGHT = 128;
    private SecureRandom secureRandom = new SecureRandom();

    public UserService(UserRepository db, String hashAlg){
        this.repository = db;
        this.algHash = "BCrypt";
        if( hashAlg.equalsIgnoreCase("PBKDF2")){
            this.algHash = "PBKDF2WithHmacSHA512";
        }
    }

    public UserService(UserRepository repository) {
        this.algHash = "BCrypt";
        this.repository = repository;
    }

    public boolean register(String login, String password) throws UserAlreadyExistsException {
        if(repository.findByLogin(login) == null){
            if(algHash == "BCrypt"){
                return repository.save(new User(login, new String(PasswordHashing.hashPasswordWithBCrypt(password.toCharArray()))) );
            }
            else {
                byte[] salt = new byte[16];
                secureRandom.nextBytes(salt);
                try {
                    return repository.save(new User(login, salt + "$" + PasswordHashing.hashPasswordWithPBKDF2(password.toCharArray(), salt, algHash,ITERATIONS, KEY_LENGHT)));
                } catch (NoSuchAlgorithmException e) {
                    return false;
                } catch (InvalidKeySpecException e) {
                    return false;
                }
            }

        } else {
            throw new UserAlreadyExistsException();
        }


    }

    public boolean updatePassword(String login, String senhaAtual, String novaSenha) throws UserNotFoundException {
        if(repository.findByLogin(login) == null) {
            throw new UserNotFoundException();
        }
            if(algHash == "BCrypt"){
                User user;
                return  repository.update(new User(login, new String(PasswordHashing.hashPasswordWithBCrypt(novaSenha.toCharArray()))));
            }
            else {
                byte[] salt = new byte[16];
                secureRandom.nextBytes(salt);
                try {
                    return repository.update(new User(login, salt + "$" + PasswordHashing.hashPasswordWithPBKDF2(novaSenha.toCharArray(), salt, algHash,ITERATIONS, KEY_LENGHT)));
                } catch (NoSuchAlgorithmException e) {
                    return  false;
                } catch (InvalidKeySpecException e) {
                    return false;
                }
            }
        }


    public boolean authenticate(String login, String senha) throws InvalidLoginException {

        User userBanco = repository.findByLogin(login);
        if(userBanco == null){
            throw new InvalidLoginException();
        }
        if(algHash == "Bcrypt"){
            PasswordHashing.verifyPasswordWithBCrypt(senha.toCharArray(),userBanco.getPassword().getBytes());
            return  true;
        }

        byte[] salt = userBanco.getPassword().split("\\$")[0].getBytes();
        byte[] hashedPassword = userBanco.getPassword().split("\\$")[1].getBytes();

        try {
            PasswordHashing.verifyPasswordWithPBKDF2(senha.toCharArray(), salt,  algHash,ITERATIONS, KEY_LENGHT, hashedPassword) ;
        } catch (NoSuchAlgorithmException e) {
            return false;
        } catch (InvalidKeySpecException e) {
            return false;
        }
        return true;
    }


}
