package ads.seg;

import java.util.ArrayList;
import java.util.HashMap;

public class UserService {
    private UserRepository repository = new InMemory();

    public boolean register(User user){

        this.repository.save(user);
    }

    public boolean updatePassword(String login, String senhaAtual){

    }

    public boolean authenticate(String login, String senha){

    }


}
