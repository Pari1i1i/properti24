package property24.views.login;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import property24.entity.User;
import property24.service.UserService;
import property24.util.AuthSession;
import property24.views.admin.AdminDashboardView;

import java.util.Optional;

@Route("login")
@PageTitle("Login | Property 24")
@AnonymousAllowed
@CssImport(value = "./views/login/login-view.css", themeFor = "vaadin-text-field")
@CssImport(value = "./views/login/login-view.css", themeFor = "vaadin-password-field")
public class LoginView extends HorizontalLayout {

    private final UserService userService;

    public LoginView(UserService userService) {
        this.userService = userService;

        // ── Auth guard ──────────────────────────────────────────────────────
        if (AuthSession.isLoggedIn()) {
            addAttachListener(e -> redirectToDashboard());
            return;
        }

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        getStyle()
                .set("overflow", "auto")
                .set("flex-wrap", "wrap");

        Div leftPanel  = buildLeftPanel();
        Div rightPanel = buildRightPanel();
        leftPanel.addClassName("login-left-panel");
        rightPanel.addClassName("login-right-panel");
        add(leftPanel, rightPanel);

        // Inject global styles + Google Fonts
        addAttachListener(e -> {
            UI.getCurrent().getPage().addStyleSheet(
                    "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap");
            injectGlobalCss();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // LEFT PANEL – White background, logo, decorative bubbles
    // ════════════════════════════════════════════════════════════════════════
    private Div buildLeftPanel() {
        Div panel = new Div();
        panel.getStyle()
                .set("flex", "1")
                .set("background", "#f4f8f5")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("min-height", "240px");

        // ── Decorative Bubbles ───────────────────────────────────────────
        panel.add(mkBubble("340px", "rgba(143,176,138,0.12)", "-170px", "-170px", null, null));
        panel.add(mkBubble("200px", "rgba(143,176,138,0.09)", null, null, "-100px", "-100px"));
        panel.add(mkBubble("110px", "rgba(143,176,138,0.16)", "8%", null, null, "5%"));
        panel.add(mkBubble("60px",  "rgba(143,176,138,0.20)", "12%", null, null, "78%"));
        panel.add(mkBubble("45px",  "rgba(143,176,138,0.18)", "78%", null, null, "6%"));
        panel.add(mkBubble("75px",  "rgba(143,176,138,0.11)", "72%", null, null, "72%"));
        panel.add(mkBubble("30px",  "rgba(28,59,46,0.10)",   "40%", null, null, "2%"));
        panel.add(mkBubble("22px",  "rgba(28,59,46,0.09)",   "55%", null, null, "90%"));
        // Large center halo
        panel.add(mkBubble("300px", "rgba(143,176,138,0.07)", "50%", "50%", null, null,
                "-150px", "-150px"));

        // ── Center Content ───────────────────────────────────────────────
        Image logoImg = new Image("images/logo.png", "Property 24 Logo");
        logoImg.setWidth("280px");
        logoImg.getStyle()
                .set("position", "relative")
                .set("z-index", "1");
        panel.add(logoImg);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // RIGHT PANEL – Dark green, login form
    // ════════════════════════════════════════════════════════════════════════
    private Div buildRightPanel() {
        Div panel = new Div();
        panel.getStyle()
                .set("flex", "1")
                .set("min-width", "320px")
                .set("background", "#1c3b2e")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("min-height", "100vh")
                .set("padding", "32px 0");

        // Decorative bubbles on dark panel
        panel.add(mkBubble("140px", "rgba(143,176,138,0.08)", "-70px", "-70px", null, null));
        panel.add(mkBubble("80px",  "rgba(143,176,138,0.06)", null, null, "-40px", "60%"));
        panel.add(mkBubble("50px",  "rgba(143,176,138,0.07)", "85%", null, null, "8%"));
        panel.add(mkBubble("28px",  "rgba(143,176,138,0.07)", "15%", null, null, "85%"));

        // ── Form Box ────────────────────────────────────────────────────
        Div formBox = new Div();
        formBox.addClassName("login-formbox");
        formBox.getStyle()
                .set("width", "100%")
                .set("max-width", "380px")
                .set("padding", "0 24px")
                .set("position", "relative")
                .set("z-index", "1");

        // Brand header
        Div brandRow = new Div();
        brandRow.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "10px")
                .set("margin-bottom", "6px");
        Div smallLogo = new Div();
        smallLogo.getElement().setProperty("innerHTML", logoSvg(30));
        Span brandLabel = new Span("PROPERTY 24");
        brandLabel.getStyle()
                .set("color", "#8fb08a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "11px")
                .set("font-weight", "700")
                .set("letter-spacing", "3px");
        brandRow.add(smallLogo, brandLabel);

        // Title
        Div title = new Div();
        title.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "44px")
                .set("font-weight", "800")
                .set("letter-spacing", "8px")
                .set("line-height", "1")
                .set("margin", "4px 0 2px");
        title.setText("LOGIN");

        // Subtitle
        Div subtitle = new Div();
        subtitle.getStyle()
                .set("color", "#8fb08a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "12px")
                .set("margin-bottom", "22px");
        subtitle.setText("Asset Management System");

        // Divider
        Hr divider = new Hr();
        divider.getStyle()
                .set("border", "none")
                .set("border-top", "1px solid rgba(143,176,138,0.2)")
                .set("margin", "0 0 26px");

        // Username
        Div uLabel = fLabel("EMAIL");
        TextField username = new TextField();
        username.setPlaceholder("Enter your Email");
        username.setWidthFull();
        username.getElement().setAttribute("id", "login-username");
        styleDarkField(username);
        Div uIcon = svgIcon(ICON_PERSON);
        username.setPrefixComponent(uIcon);

        // Password
        Div pLabel = fLabel("PASSWORD");
        pLabel.getStyle().set("margin-top", "14px");
        PasswordField password = new PasswordField();
        password.setPlaceholder("Enter your password");
        password.setWidthFull();
        password.getElement().setAttribute("id", "login-password");
        styleDarkField(password);
        Div pIcon = svgIcon(ICON_LOCK);
        password.setPrefixComponent(pIcon);

        // Remember me + Forgot
        Checkbox remember = new Checkbox("Remember me");
        remember.getStyle()
                .set("color", "#b8c9bf")
                .set("font-size", "13px");
        Anchor forgot = new Anchor("forgot-password", "Forgot Password?");
        forgot.getStyle()
                .set("color", "#8fb08a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("text-decoration", "none");
        HorizontalLayout remRow = new HorizontalLayout(remember, forgot);
        remRow.setWidthFull();
        remRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        remRow.setAlignItems(FlexComponent.Alignment.CENTER);
        remRow.setPadding(false);
        remRow.getStyle().set("margin", "12px 0 4px");

        // Login Button
        Button loginBtn = new Button("LOGIN  \u2192");
        loginBtn.setId("login-btn");
        loginBtn.setWidthFull();
        loginBtn.getStyle()
                .set("background", "linear-gradient(135deg, #4d8f4d 0%, #8fb08a 100%)")
                .set("color", "#0a1f0f")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "700")
                .set("font-size", "13px")
                .set("letter-spacing", "3px")
                .set("border", "none")
                .set("border-radius", "8px")
                .set("height", "50px")
                .set("cursor", "pointer")
                .set("margin-top", "18px")
                .set("transition", "all 0.3s ease")
                .set("box-shadow", "0 4px 15px rgba(78,143,78,0.25)");
        loginBtn.addClickListener(e -> onLogin(username.getValue(), password.getValue()));

        // Footer
        Div footer = new Div();
        footer.getStyle()
                .set("text-align", "center")
                .set("margin-top", "22px");
        Span noAcct = new Span("Don't have account?  ");
        noAcct.getStyle()
                .set("color", "#8fb08a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px");
        Anchor regLink = new Anchor("register", "Register");
        regLink.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("font-weight", "700")
                .set("text-decoration", "underline");
        footer.add(noAcct, regLink);

        formBox.add(brandRow, title, subtitle, divider,
                uLabel, username, pLabel, password,
                remRow, loginBtn, footer);

        username.addKeyPressListener(Key.ENTER, e -> onLogin(username.getValue(), password.getValue()));
        password.addKeyPressListener(Key.ENTER, e -> onLogin(username.getValue(), password.getValue()));

        panel.add(formBox);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private void onLogin(String user, String pass) {
        if (user.isBlank() || pass.isBlank()) {
            err("Username dan password tidak boleh kosong!");
            return;
        }
        Optional<User> found = userService.login(user.trim(), pass);
        if (found.isPresent()) {
            AuthSession.setCurrentUser(found.get());
            redirectToDashboard();
        } else {
            err("Email atau password salah. Coba lagi.");
        }
    }

    private void redirectToDashboard() {
        User u = AuthSession.getCurrentUser();
        if (u != null && u.getRole() == User.Role.admin) {
            UI.getCurrent().navigate(AdminDashboardView.class);
        } else {
            UI.getCurrent().navigate("user-dashboard");
        }
    }

    private void err(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private Div fLabel(String text) {
        Div d = new Div();
        d.getStyle()
                .set("color", "#b8c9bf")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "10px")
                .set("font-weight", "700")
                .set("letter-spacing", "2px")
                .set("margin-bottom", "5px");
        d.setText(text);
        return d;
    }

    private void styleDarkField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("--lumo-body-text-color", "white")
                .set("--lumo-secondary-text-color", "rgba(184,201,191,0.75)")
                .set("--lumo-primary-color", "#8fb08a")
                .set("--lumo-primary-text-color", "#8fb08a")
                .set("--lumo-base-color", "#1c3b2e")
                .set("--lumo-contrast-5pct", "rgba(255,255,255,0.07)")
                .set("--lumo-contrast-10pct", "rgba(143,176,138,0.3)")
                .set("--vaadin-input-field-background", "rgba(255,255,255,0.07)")
                .set("--vaadin-input-field-border-color", "rgba(143,176,138,0.32)")
                .set("--vaadin-input-field-value-color", "white")
                .set("--vaadin-input-field-placeholder-color", "rgba(184,201,191,0.45)")
                .set("--vaadin-input-field-focused-border-color", "#8fb08a");
    }

    private Div svgIcon(String svgPath) {
        Div d = new Div();
        d.getElement().setProperty("innerHTML",
                "<svg width='16' height='16' fill='#8fb08a' viewBox='0 0 24 24' xmlns='http://www.w3.org/2000/svg'>"
                        + svgPath + "</svg>");
        d.getStyle().set("display", "flex").set("align-items", "center");
        return d;
    }

    /** Bubble helper – positions absolutely inside relative parent */
    private Div mkBubble(String size, String bg, String top, String left, String bottom, String right) {
        return mkBubble(size, bg, top, left, bottom, right, null, null);
    }

    private Div mkBubble(String size, String bg, String top, String left, String bottom, String right,
                         String mt, String ml) {
        Div d = new Div();
        d.getStyle()
                .set("position", "absolute")
                .set("width", size)
                .set("height", size)
                .set("border-radius", "50%")
                .set("background", bg)
                .set("pointer-events", "none");
        if (top    != null) d.getStyle().set("top", top);
        if (left   != null) d.getStyle().set("left", left);
        if (bottom != null) d.getStyle().set("bottom", bottom);
        if (right  != null) d.getStyle().set("right", right);
        if (mt     != null) d.getStyle().set("margin-top", mt);
        if (ml     != null) d.getStyle().set("margin-left", ml);
        return d;
    }

    private String logoSvg(int size) {
        return String.format(
            "<svg width='%d' height='%d' viewBox='0 0 120 120' xmlns='http://www.w3.org/2000/svg'>" +
            "<polygon points='14,48 58,22 58,82 14,108' fill='#1e3460'/>" +
            "<polygon points='58,22 104,48 104,108 58,82' fill='#3a9898'/>" +
            "<polygon points='14,48 58,22 104,48 58,74' fill='#5dcfca'/>" +
            "<line x1='58' y1='22' x2='58' y2='82' stroke='rgba(0,0,0,0.14)' stroke-width='1.5'/>" +
            "<circle cx='26' cy='103' r='16' fill='#6aab6a'/>" +
            "<text x='26' y='108.5' text-anchor='middle' " +
            "font-family='Inter,Arial Black,sans-serif' font-size='11' font-weight='900' fill='white'>24</text>" +
            "</svg>", size, size);
    }

    private void injectGlobalCss() {
        String css =
            "body,html{margin:0;padding:0;}" +
            "#login-btn:hover{" +
            "  transform:translateY(-2px)!important;" +
            "  box-shadow:0 10px 28px rgba(78,143,78,0.40)!important;" +
            "}" +
            "a{cursor:pointer;}" +
            "vaadin-checkbox::part(checkbox){" +
            "  border: 2px solid #8fb08a!important;" +
            "  background: rgba(255,255,255,0.12)!important;" +
            "  border-radius: 4px!important;" +
            "}" +
            "vaadin-checkbox[checked]::part(checkbox){" +
            "  background:#8fb08a!important;" +
            "  border-color:#8fb08a!important;" +
            "}" +
            /* Fix browser autofill override for login page — keep dark bg + white text */
            ".login-formbox input:-webkit-autofill," +
            ".login-formbox input:-webkit-autofill:hover," +
            ".login-formbox input:-webkit-autofill:focus," +
            ".login-formbox input:-webkit-autofill:active{" +
            "  -webkit-box-shadow:0 0 0 40px #1c3b2e inset!important;" +
            "  -webkit-text-fill-color:white!important;" +
            "  caret-color:white!important;" +
            "}" +
            /* Mobile: hide the logo left panel, show only the dark form panel stacked */
            "@media(max-width:700px){" +
            "  .login-left-panel{min-height:220px!important;flex:none!important;width:100%!important;}" +
            "  .login-right-panel{min-height:unset!important;flex:none!important;width:100%!important;padding:28px 0 40px!important;}" +
            "  .login-formbox{max-width:100%!important;}" +
            "}";
        getElement().executeJs(
            "if(!document.getElementById('p24-login-css')){" +
            "  const s=document.createElement('style');" +
            "  s.id='p24-login-css';" +
            "  s.textContent=$0;" +
            "  document.head.appendChild(s);" +
            "}", css);
    }

    // ── SVG icon path data ─────────────────────────────────────────────────
    private static final String ICON_PERSON =
        "<path d='M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12z" +
        "M12 14.4c-3.2 0-9.6 1.6-9.6 4.8V21.6H21.6V19.2c0-3.2-6.4-4.8-9.6-4.8z'/>";
    private static final String ICON_LOCK =
        "<path d='M18 8h-1V6c0-2.8-2.2-5-5-5S7 3.2 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12" +
        "c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zM12 17c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z" +
        "M15.1 8H8.9V6c0-1.7 1.4-3.1 3.1-3.1 1.7 0 3.1 1.4 3.1 3.1v2z'/>";
}
