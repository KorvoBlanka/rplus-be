import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aleksandr on 23.01.17.
 */

public class SessionManager {

    public static Map<String, Session> sessions;

    public SessionManager() {
        sessions = new HashMap<>();
    }
}
