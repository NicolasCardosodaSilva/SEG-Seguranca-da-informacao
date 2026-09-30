package ads.seg;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemory implements UserRepository {

    private HashMap<String, User> usuarios = new HashMap();

    @Override
    public boolean save(User user) {
        usuarios.put(user.getLogin(), user);
        return true;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User findByLogin(String login) {
            return usuarios.get(login);
    }


}
