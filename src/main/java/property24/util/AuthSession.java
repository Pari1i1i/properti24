package property24.util;

import com.vaadin.flow.server.VaadinSession;
import property24.entity.User;

/**
 * Helper statis untuk mengelola auth session via VaadinSession.
 */
public class AuthSession {

    private static final String USER_KEY = "currentUser";

    private AuthSession() {}

    public static void setCurrentUser(User user) {
        VaadinSession.getCurrent().setAttribute(USER_KEY, user);
    }

    public static User getCurrentUser() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return null;
        return (User) session.getAttribute(USER_KEY);
    }

    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public static void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(USER_KEY, null);
        }
    }

    public static boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && user.getRole() == User.Role.admin;
    }

    public static String getDisplayName() {
        User user = getCurrentUser();
        if (user == null) return "Guest";
        return user.getNamaLengkap() != null ? user.getNamaLengkap() : user.getUsername();
    }
}
