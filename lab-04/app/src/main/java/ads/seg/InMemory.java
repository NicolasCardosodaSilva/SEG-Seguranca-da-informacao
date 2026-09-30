package ads.seg;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemory implements UserRepository {

    private HashMap<String, User> dados= new HashMap();

    @Override
    public boolean save(User user) {
        dados.put(user.getLogin(), user);
        return true;
    }

    @Override
    public boolean update(User user) {
        dados.replace(user.getLogin(), user);
        return true;
    }

    @Override
    public User findByLogin(String login) {
        return dados.get(login);
    }



}
