package ads.seg;

import java.util.ArrayList;
import java.util.HashMap;

public class UserService {
    private UserRepository repository = new InMemory();
    private String algHash;

    public UserService(UserRepository db, String hashAlg){
        this.repository = db;
        this.algHash = "BCrypt";
        if( hashAlg.equalsIgnoreCase("PBKDF2")){
            this.algHash = hashAlg;
        }
    }

    public UserService(UserRepository repository) {
        this.algHash = "BCrypt";
        this.repository = repository;
    }

    public boolean register(String login, String password) throws UserAlreadyExistsException {
        if(repository.findByLogin(login) == null){
            User user = new User(login, password);
            this.repository.save(user);
            return true;
        } else {
            throw new UserAlreadyExistsException();
        }
    }

    public boolean updatePassword(String login, String senhaAtual, String novaSenha) throws UserNotFoundException {
        if(repository.findByLogin(login) == null) {
            throw  new UserNotFoundException();
        }
        User user = new User(login, senhaAtual);
        User userBanco = repository.findByLogin(login);
        if (user.getPassword().equals(userBanco.getPassword())) {
            user.setPassword(novaSenha);
            repository.update(user);
        }
        return true;
    }

    public boolean authenticate(String login, String senha) throws InvalidLoginException {
        User user = new User(login,senha);
        User userBanco = repository.findByLogin(login);
        if(userBanco == null){
            throw new InvalidLoginException();
        }
        else if(!user.getPassword().equals(userBanco.getPassword())){
            throw new InvalidLoginException();
        }
        return true;
    }


}
