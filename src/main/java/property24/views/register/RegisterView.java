package property24.views.register;

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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import property24.service.UserService;

@Route("register")
@PageTitle("Register | Property 24")
@AnonymousAllowed
@CssImport(value = "./views/register/register-view.css", themeFor = "vaadin-text-field")
@CssImport(value = "./views/register/register-view.css", themeFor = "vaadin-password-field")
@CssImport(value = "./views/register/register-view.css", themeFor = "vaadin-email-field")
public class RegisterView extends HorizontalLayout {

    private final UserService userService;

    public RegisterView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        getStyle().set("overflow", "hidden");

        // Form on LEFT, Logo on RIGHT (mirror of login)
        add(buildLeftPanel(), buildRightPanel());

        addAttachListener(e -> {
            UI.getCurrent().getPage().addStyleSheet(
                    "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap");
            injectGlobalCss();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // LEFT PANEL – Dark green, register form
    // ════════════════════════════════════════════════════════════════════════
    private Div buildLeftPanel() {
        Div panel = new Div();
        panel.getStyle()
                .set("flex", "1")
                .set("background", "#1c3b2e")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("min-height", "100vh");

        // Decorative bubbles
        panel.add(mkBubble("130px", "rgba(143,176,138,0.08)", "-65px", "-65px", null, null));
        panel.add(mkBubble("80px",  "rgba(143,176,138,0.06)", null, null, "-40px", "55%"));
        panel.add(mkBubble("45px",  "rgba(143,176,138,0.07)", "88%", null, null, "6%"));
        panel.add(mkBubble("25px",  "rgba(143,176,138,0.07)", "12%", null, null, "88%"));
        panel.add(mkBubble("60px",  "rgba(143,176,138,0.05)", "50%", null, null, "92%"));

        // ── Form Box ────────────────────────────────────────────────────
        Div formBox = new Div();
        formBox.getStyle()
                .set("width", "390px")
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
        title.setText("REGISTER");

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
                .set("margin", "0 0 22px");

        // Full Name
        Div fnLabel = fLabel("FULL NAME");
        TextField fullName = new TextField();
        fullName.setPlaceholder("Enter your full name");
        fullName.setWidthFull();
        styleDarkField(fullName);
        fullName.setPrefixComponent(svgIcon(ICON_PERSON));

        // Email
        Div emLabel = fLabel("EMAIL");
        emLabel.getStyle().set("margin-top", "12px");
        EmailField email = new EmailField();
        email.setPlaceholder("Enter your email");
        email.setWidthFull();
        styleDarkField(email);
        email.setPrefixComponent(svgIcon(ICON_EMAIL));

        // Password
        Div pwLabel = fLabel("PASSWORD");
        pwLabel.getStyle().set("margin-top", "12px");
        PasswordField password = new PasswordField();
        password.setPlaceholder("Enter your password");
        password.setWidthFull();
        styleDarkField(password);
        password.setPrefixComponent(svgIcon(ICON_LOCK));

        // Confirm Password
        Div cpLabel = fLabel("CONFIRM PASSWORD");
        cpLabel.getStyle().set("margin-top", "12px");
        PasswordField confirmPass = new PasswordField();
        confirmPass.setPlaceholder("Confirm your password");
        confirmPass.setWidthFull();
        styleDarkField(confirmPass);
        confirmPass.setPrefixComponent(svgIcon(ICON_LOCK));

        // Agreement checkbox + links
        Div agreementDiv = new Div();
        agreementDiv.getStyle()
                .set("display", "flex")
                .set("align-items", "flex-start")
                .set("gap", "8px")
                .set("margin-top", "14px");

        Checkbox agreement = new Checkbox();
        agreement.setValue(true);
        agreement.getStyle().set("margin-top", "2px");

        Div agreeText = new Div();
        agreeText.getStyle()
                .set("color", "#b8c9bf")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "12px")
                .set("line-height", "1.5");
        agreeText.getElement().setProperty("innerHTML",
                "Saya menyetujui <a href='#' style='color:#8fb08a;text-decoration:underline'>Syarat &amp; Ketentuan</a>" +
                " dan <a href='#' style='color:#8fb08a;text-decoration:underline'>Kebijakan Privasi</a>");
        agreementDiv.add(agreement, agreeText);

        // Register Button
        Button regBtn = new Button("DAFTAR SEKARANG  \u2192");
        regBtn.setId("register-btn");
        regBtn.setWidthFull();
        regBtn.getStyle()
                .set("background", "linear-gradient(135deg, #4d8f4d 0%, #8fb08a 100%)")
                .set("color", "#0a1f0f")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "700")
                .set("font-size", "12px")
                .set("letter-spacing", "2px")
                .set("border", "none")
                .set("border-radius", "8px")
                .set("height", "50px")
                .set("cursor", "pointer")
                .set("margin-top", "16px")
                .set("transition", "all 0.3s ease")
                .set("box-shadow", "0 4px 15px rgba(78,143,78,0.25)");
        regBtn.addClickListener(e ->
                onRegister(fullName.getValue(), email.getValue(),
                        password.getValue(), confirmPass.getValue(), agreement.getValue()));

        // Footer
        Div footer = new Div();
        footer.getStyle()
                .set("text-align", "center")
                .set("margin-top", "18px");
        Span hasAcct = new Span("Sudah punya akun?  ");
        hasAcct.getStyle()
                .set("color", "#8fb08a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px");
        Anchor loginLink = new Anchor("login", "Login");
        loginLink.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("font-weight", "700")
                .set("text-decoration", "underline");
        footer.add(hasAcct, loginLink);

        formBox.add(brandRow, title, subtitle, divider,
                fnLabel, fullName, emLabel, email,
                pwLabel, password, cpLabel, confirmPass,
                agreementDiv, regBtn, footer);

        // Enter key shortcut
        confirmPass.addKeyPressListener(Key.ENTER, e ->
                onRegister(fullName.getValue(), email.getValue(),
                        password.getValue(), confirmPass.getValue(), agreement.getValue()));

        panel.add(formBox);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // RIGHT PANEL – White background, logo + decorative bubbles
    // ════════════════════════════════════════════════════════════════════════
    private Div buildRightPanel() {
        Div panel = new Div();
        panel.getStyle()
                .set("flex", "1")
                .set("background", "#f4f8f5")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("min-height", "100vh");

        // Decorative bubbles (white panel)
        panel.add(mkBubble("340px", "rgba(143,176,138,0.12)", null, null, "-170px", "-170px"));
        panel.add(mkBubble("200px", "rgba(143,176,138,0.09)", "-100px", "-100px", null, null));
        panel.add(mkBubble("110px", "rgba(143,176,138,0.16)", null, "8%", null, "5%"));
        panel.add(mkBubble("60px",  "rgba(143,176,138,0.20)", null, "12%", null, "78%"));
        panel.add(mkBubble("45px",  "rgba(143,176,138,0.18)", null, "78%", null, "6%"));
        panel.add(mkBubble("75px",  "rgba(143,176,138,0.11)", null, "72%", null, "72%"));
        panel.add(mkBubble("30px",  "rgba(28,59,46,0.10)",   null, "40%", null, "2%"));
        panel.add(mkBubble("22px",  "rgba(28,59,46,0.09)",   null, "55%", null, "90%"));

        // Center content
        Image logoImg = new Image("images/logo.png", "Property 24 Logo");
        logoImg.setWidth("280px");
        logoImg.getStyle()
                .set("position", "relative")
                .set("z-index", "1");
        panel.add(logoImg);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private void onRegister(String name, String emailVal, String pass, String confirm, Boolean agreed) {
        String safeName = name != null ? name.trim() : "";
        String safeEmail = emailVal != null ? emailVal.trim() : "";
        String safePass = pass != null ? pass : "";
        String safeConfirm = confirm != null ? confirm : "";
        boolean safeAgreed = Boolean.TRUE.equals(agreed);

        if (safeName.isEmpty() || safeEmail.isEmpty() || safePass.isEmpty() || safeConfirm.isEmpty()) {
            err("Semua field (Nama, Email, Password) harus diisi!");
            return;
        }
        if (!safePass.equals(safeConfirm)) {
            err("Password dan konfirmasi password tidak sama!");
            return;
        }
        if (safePass.length() < 6) {
            err("Password minimal 6 karakter!");
            return;
        }
        if (!safeAgreed) {
            err("Kamu harus mencentang persetujuan Syarat & Ketentuan!");
            return;
        }
        try {
            userService.register(safeName, safeEmail, safePass);
            Notification ok = Notification.show(
                    "Registrasi berhasil! Silakan login.", 4000, Notification.Position.TOP_CENTER);
            ok.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate("login");
        } catch (IllegalArgumentException ex) {
            err(ex.getMessage());
        } catch (Exception ex) {
            err("Gagal mendaftar: " + ex.getMessage());
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

    private Div svgIcon(String path) {
        Div d = new Div();
        d.getElement().setProperty("innerHTML",
                "<svg width='16' height='16' fill='#8fb08a' viewBox='0 0 24 24'>" + path + "</svg>");
        d.getStyle().set("display", "flex").set("align-items", "center");
        return d;
    }

    private Div mkBubble(String size, String bg, String top, String left, String bottom, String right) {
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
            "#register-btn:hover{" +
            "  transform:translateY(-2px)!important;" +
            "  box-shadow:0 10px 28px rgba(78,143,78,0.40)!important;" +
            "}" +
            "vaadin-checkbox::part(checkbox){" +
            "  border-color:rgba(143,176,138,0.5)!important;" +
            "  background:transparent!important;" +
            "}" +
            "vaadin-checkbox[checked]::part(checkbox){" +
            "  background:#8fb08a!important;" +
            "  border-color:#8fb08a!important;" +
            "}";
        getElement().executeJs(
            "if(!document.getElementById('p24-reg-css')){" +
            "  const s=document.createElement('style');" +
            "  s.id='p24-reg-css';" +
            "  s.textContent=$0;" +
            "  document.head.appendChild(s);" +
            "}", css);
    }

    // SVG icon paths
    private static final String ICON_PERSON =
        "<path d='M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12z" +
        "M12 14.4c-3.2 0-9.6 1.6-9.6 4.8V21.6H21.6V19.2c0-3.2-6.4-4.8-9.6-4.8z'/>";
    private static final String ICON_EMAIL =
        "<path d='M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2z" +
        "M20 8l-8 5-8-5V6l8 5 8-5v2z'/>";
    private static final String ICON_LOCK =
        "<path d='M18 8h-1V6c0-2.8-2.2-5-5-5S7 3.2 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12" +
        "c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zM12 17c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z" +
        "M15.1 8H8.9V6c0-1.7 1.4-3.1 3.1-3.1 1.7 0 3.1 1.4 3.1 3.1v2z'/>";
}
