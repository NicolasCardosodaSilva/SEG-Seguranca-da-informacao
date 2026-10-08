package ads.seg;

import java.beans.Encoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class UserService {
    private UserRepository repository;
    private String algHash;
    private final int ITERATIONS = 1000;
    private  final int KEY_LENGHT = 128;
    private SecureRandom secureRandom = new SecureRandom();
    private static Base64.Encoder ENCODER = Base64.getEncoder();
    private static Base64.Decoder DECODER = Base64.getDecoder();



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
            if(algHash.equalsIgnoreCase("BCrypt")){
                return repository.save(new User(login, new String(PasswordHashing.hashPasswordWithBCrypt(password.toCharArray()))) );
            }
            else {
                byte[] salt = new byte[16];
                secureRandom.nextBytes(salt);
                String salt64 = ENCODER.encodeToString(salt);
                try {
                    return repository.save(new User(login, salt64 + "$" +  ENCODER.encodeToString(PasswordHashing.hashPasswordWithPBKDF2(password.toCharArray(), salt, algHash,ITERATIONS, KEY_LENGHT))));
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

    public boolean updatePassword(String login, String senhaAtual, String novaSenha) throws UserNotFoundException, InvalidLoginException, InvalidPasswordException {
        if(repository.findByLogin(login) == null) {
            throw new UserNotFoundException();
        }
        if(authenticate(login,senhaAtual)){
            if(algHash.equalsIgnoreCase("Bcrypt")){
                return  repository.update(new User(login, new String(PasswordHashing.hashPasswordWithBCrypt(novaSenha.toCharArray()))));
            }
            else {
                byte[] salt = new byte[16];
                secureRandom.nextBytes(salt);
                String salt64 = ENCODER.encodeToString(salt);

                try {
                    return repository.update(new User(login, salt64 + "$" +  ENCODER.encodeToString(PasswordHashing.hashPasswordWithPBKDF2(novaSenha.toCharArray(), salt, algHash,ITERATIONS, KEY_LENGHT))));
                } catch (NoSuchAlgorithmException e) {
                    return  false;
                } catch (InvalidKeySpecException e) {
                    return false;
                }
            }
        }
        throw new InvalidPasswordException();
    }


    public boolean authenticate(String login, String senha) throws InvalidLoginException {

        User userBanco = repository.findByLogin(login);
        if(userBanco == null){
            throw new InvalidLoginException();
        }
        if(algHash.equalsIgnoreCase("PBKDF2") ) {


            byte[] salt = DECODER.decode(userBanco.getPassword().split("\\$")[0]);
            byte[] hashedPassword = DECODER.decode(userBanco.getPassword().split("\\$")[1]);

            try {
                return PasswordHashing.verifyPasswordWithPBKDF2(senha.toCharArray(), salt, algHash, ITERATIONS, KEY_LENGHT, hashedPassword);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                return false;
            }
        } else {
            return PasswordHashing.verifyPasswordWithBCrypt(senha.toCharArray(), DECODER.decode(userBanco.getPassword()));
        }
    }

    public void setAlgHash(String algHash) {
        this.algHash = algHash;
    }
}
