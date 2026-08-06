package property24.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import property24.entity.User;
import property24.util.AuthSession;
import property24.views.admin.AdminDashboardView;

/**
 * Route "/" — redirect ke login jika belum login, atau ke dashboard sesuai role.
 */
@Route("")
@PageTitle("Property 24")
@AnonymousAllowed
public class MainRedirectView extends Div {

    public MainRedirectView() {
        if (!AuthSession.isLoggedIn()) {
            UI.getCurrent().navigate("login");
            return;
        }
        User user = AuthSession.getCurrentUser();
        if (user.getRole() == User.Role.admin) {
            UI.getCurrent().navigate(AdminDashboardView.class);
        } else {
            UI.getCurrent().navigate("user-dashboard");
        }
    }
}
