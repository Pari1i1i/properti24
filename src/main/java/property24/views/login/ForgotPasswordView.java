package property24.views.login;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
import property24.service.PasswordResetService;

@Route("forgot-password")
@PageTitle("Forgot Password | Property 24")
@AnonymousAllowed
@CssImport(value = "./views/login/login-view.css", themeFor = "vaadin-text-field")
@CssImport(value = "./views/login/login-view.css", themeFor = "vaadin-password-field")
@CssImport(value = "./views/login/login-view.css", themeFor = "vaadin-email-field")
public class ForgotPasswordView extends HorizontalLayout {

    private final PasswordResetService passwordResetService;

    // View Components for Steps
    private Div formBox;
    private Div step1Container;
    private Div step2Container;
    private Div step3Container;
    private Div successContainer;

    // Form inputs
    private EmailField emailField;
    private TextField otpField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;

    // State
    private String currentEmail = "";

    public ForgotPasswordView(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        getStyle().set("overflow", "hidden");

        add(buildLeftPanel(), buildRightPanel());

        addAttachListener(e -> {
            UI.getCurrent().getPage().addStyleSheet(
                    "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap");
            injectGlobalCss();
        });
    }

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
                .set("min-height", "100vh");

        panel.add(mkBubble("340px", "rgba(143,176,138,0.12)", "-170px", "-170px", null, null));
        panel.add(mkBubble("200px", "rgba(143,176,138,0.09)", null, null, "-100px", "-100px"));
        panel.add(mkBubble("110px", "rgba(143,176,138,0.16)", "8%", null, null, "5%"));
        panel.add(mkBubble("60px",  "rgba(143,176,138,0.20)", "12%", null, null, "78%"));
        panel.add(mkBubble("45px",  "rgba(143,176,138,0.18)", "78%", null, null, "6%"));

        Image logoImg = new Image("/images/logo.png", "Property 24 Logo");
        logoImg.setWidth("280px");
        logoImg.getStyle().set("position", "relative").set("z-index", "1");
        panel.add(logoImg);
        return panel;
    }

    private Div buildRightPanel() {
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

        panel.add(mkBubble("500px", "rgba(143,176,138,0.04)", "-200px", null, null, "-200px"));
        panel.add(mkBubble("300px", "rgba(143,176,138,0.05)", null, "-100px", "-100px", null));

        formBox = new Div();
        formBox.getStyle()
                .set("width", "390px")
                .set("position", "relative")
                .set("z-index", "1");

        // Header section
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

        Div title = new Div();
        title.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "34px")
                .set("font-weight", "800")
                .set("letter-spacing", "4px")
                .set("line-height", "1.2")
                .set("margin", "4px 0 2px");
        title.setText("FORGOT PASSWORD");

        Div subtitle = new Div();
        subtitle.getStyle()
                .set("color", "#8fb08a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "12px")
                .set("margin-bottom", "22px");
        subtitle.setText("Verifikasi OTP via Email Gmail");

        Hr divider = new Hr();
        divider.getStyle()
                .set("border", "none")
                .set("border-top", "1px solid rgba(143,176,138,0.2)")
                .set("margin", "0 0 24px");

        buildStep1();
        buildStep2();
        buildStep3();
        buildSuccessStep();

        // Footer back link
        Div footer = new Div();
        footer.getStyle()
                .set("text-align", "center")
                .set("margin-top", "24px");
        Anchor loginLink = new Anchor("login", "\u2190 Kembali ke Halaman Login");
        loginLink.getStyle()
                .set("color", "#8fb08a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("text-decoration", "none")
                .set("font-weight", "600");
        footer.add(loginLink);

        formBox.add(brandRow, title, subtitle, divider, step1Container, step2Container, step3Container, successContainer, footer);
        showStep(1);

        panel.add(formBox);
        return panel;
    }

    private void buildStep1() {
        step1Container = new Div();

        Div eLabel = fLabel("EMAIL AKUN ANDA");
        emailField = new EmailField();
        emailField.setPlaceholder("contoh@gmail.com");
        emailField.setWidthFull();
        styleDarkField(emailField);
        emailField.setPrefixComponent(svgIcon(ICON_EMAIL));

        Button sendOtpBtn = mkButton("KIRIM KODE OTP  \u2192");
        sendOtpBtn.addClickListener(e -> handleSendOtp());
        emailField.addKeyPressListener(Key.ENTER, e -> handleSendOtp());

        step1Container.add(eLabel, emailField, sendOtpBtn);
    }

    private void buildStep2() {
        step2Container = new Div();
        step2Container.getStyle().set("display", "none");

        Div infoBox = new Div();
        infoBox.getStyle()
                .set("background", "rgba(143,176,138,0.1)")
                .set("border-left", "3px solid #8fb08a")
                .set("padding", "10px 14px")
                .set("border-radius", "6px")
                .set("margin-bottom", "18px")
                .set("color", "#b8c9bf")
                .set("font-size", "12px");
        infoBox.setText("Kode 6-digit OTP telah dikirimkan ke email Anda. Periksa kotak masuk / spam Gmail.");

        Div oLabel = fLabel("KODE OTP (6 DIGIT)");
        otpField = new TextField();
        otpField.setPlaceholder("123456");
        otpField.setMaxLength(6);
        otpField.setWidthFull();
        otpField.getStyle().set("text-align", "center").set("letter-spacing", "6px").set("font-size", "18px");
        styleDarkField(otpField);

        Button verifyBtn = mkButton("VERIFIKASI OTP  \u2192");
        verifyBtn.addClickListener(e -> handleVerifyOtp());
        otpField.addKeyPressListener(Key.ENTER, e -> handleVerifyOtp());

        Button resendBtn = new Button("Kirim Ulang OTP");
        resendBtn.setWidthFull();
        resendBtn.getStyle()
                .set("background", "transparent")
                .set("color", "#8fb08a")
                .set("border", "1px solid rgba(143,176,138,0.4)")
                .set("border-radius", "8px")
                .set("height", "42px")
                .set("margin-top", "10px")
                .set("cursor", "pointer");
        resendBtn.addClickListener(e -> handleSendOtp());

        step2Container.add(infoBox, oLabel, otpField, verifyBtn, resendBtn);
    }

    private void buildStep3() {
        step3Container = new Div();
        step3Container.getStyle().set("display", "none");

        Div npLabel = fLabel("PASSWORD BARU");
        newPasswordField = new PasswordField();
        newPasswordField.setPlaceholder("Minimal 6 karakter");
        newPasswordField.setWidthFull();
        styleDarkField(newPasswordField);
        newPasswordField.setPrefixComponent(svgIcon(ICON_LOCK));

        Div cpLabel = fLabel("KONFIRMASI PASSWORD BARU");
        cpLabel.getStyle().set("margin-top", "14px");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPlaceholder("Ulangi password baru");
        confirmPasswordField.setWidthFull();
        styleDarkField(confirmPasswordField);
        confirmPasswordField.setPrefixComponent(svgIcon(ICON_LOCK));

        Button resetBtn = mkButton("SIMPAN PASSWORD BARU  \u2192");
        resetBtn.addClickListener(e -> handleResetPassword());

        step3Container.add(npLabel, newPasswordField, cpLabel, confirmPasswordField, resetBtn);
    }

    private void buildSuccessStep() {
        successContainer = new Div();
        successContainer.getStyle()
                .set("display", "none")
                .set("text-align", "center")
                .set("padding", "20px 0");

        Div checkIcon = new Div();
        checkIcon.getElement().setProperty("innerHTML",
                "<svg width='64' height='64' viewBox='0 0 24 24' fill='none' stroke='#8fb08a' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M22 11.08V12a10 10 0 1 1-5.93-9.14'/><polyline points='22 4 12 14.01 9 11.01'/></svg>");

        Div msgTitle = new Div();
        msgTitle.setText("Password Berhasil Diubah!");
        msgTitle.getStyle()
                .set("color", "white")
                .set("font-size", "20px")
                .set("font-weight", "700")
                .set("margin", "16px 0 8px");

        Div msgSub = new Div();
        msgSub.setText("Silakan login menggunakan password baru Anda.");
        msgSub.getStyle()
                .set("color", "#b8c9bf")
                .set("font-size", "13px")
                .set("margin-bottom", "24px");

        Button loginBtn = mkButton("KE HALAMAN LOGIN");
        loginBtn.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));

        successContainer.add(checkIcon, msgTitle, msgSub, loginBtn);
    }

    private void showStep(int step) {
        step1Container.getStyle().set("display", step == 1 ? "block" : "none");
        step2Container.getStyle().set("display", step == 2 ? "block" : "none");
        step3Container.getStyle().set("display", step == 3 ? "block" : "none");
        successContainer.getStyle().set("display", step == 4 ? "block" : "none");
    }

    private void handleSendOtp() {
        String email = emailField.getValue() != null ? emailField.getValue().trim() : "";
        if (email.isEmpty()) {
            err("Masukkan alamat email akun Anda!");
            return;
        }

        try {
            passwordResetService.generateAndSendOtp(email);
            currentEmail = email;
            success("Kode OTP berhasil dikirim ke " + email);
            showStep(2);
        } catch (IllegalArgumentException e) {
            err(e.getMessage());
        } catch (Exception e) {
            err("Gagal mengirim OTP: " + e.getMessage());
        }
    }

    private void handleVerifyOtp() {
        String otp = otpField.getValue() != null ? otpField.getValue().trim() : "";
        if (otp.isEmpty() || otp.length() < 6) {
            err("Masukkan 6-digit kode OTP!");
            return;
        }

        boolean valid = passwordResetService.verifyOtp(currentEmail, otp);
        if (valid) {
            success("Kode OTP valid! Silakan masukkan password baru Anda.");
            showStep(3);
        } else {
            err("Kode OTP salah atau sudah kedaluwarsa.");
        }
    }

    private void handleResetPassword() {
        String np = newPasswordField.getValue() != null ? newPasswordField.getValue() : "";
        String cp = confirmPasswordField.getValue() != null ? confirmPasswordField.getValue() : "";
        String otp = otpField.getValue() != null ? otpField.getValue().trim() : "";

        if (np.length() < 6) {
            err("Password baru minimal harus 6 karakter!");
            return;
        }
        if (!np.equals(cp)) {
            err("Konfirmasi password baru tidak cocok!");
            return;
        }

        try {
            passwordResetService.resetPassword(currentEmail, otp, np);
            showStep(4);
        } catch (Exception e) {
            err(e.getMessage());
        }
    }

    private Button mkButton(String text) {
        Button b = new Button(text);
        b.setWidthFull();
        b.getStyle()
                .set("background", "linear-gradient(135deg, #4d8f4d 0%, #8fb08a 100%)")
                .set("color", "#0a1f0f")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "700")
                .set("font-size", "13px")
                .set("letter-spacing", "2px")
                .set("border", "none")
                .set("border-radius", "8px")
                .set("height", "50px")
                .set("cursor", "pointer")
                .set("margin-top", "18px")
                .set("transition", "all 0.3s ease")
                .set("box-shadow", "0 4px 15px rgba(78,143,78,0.25)");
        return b;
    }

    private Div fLabel(String text) {
        Div d = new Div();
        d.getStyle()
                .set("color", "#b8c9bf")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "10px")
                .set("font-weight", "700")
                .set("letter-spacing", "2px")
                .set("margin-bottom", "6px");
        d.setText(text);
        return d;
    }

    private void styleDarkField(com.vaadin.flow.component.HasStyle field) {
        field.getStyle()
                .set("background", "rgba(10,31,15,0.7)")
                .set("border", "1px solid rgba(143,176,138,0.3)")
                .set("border-radius", "8px")
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px");
    }

    private Div mkBubble(String size, String bg, String top, String left, String bottom, String right) {
        Div b = new Div();
        b.getStyle()
                .set("position", "absolute")
                .set("width", size)
                .set("height", size)
                .set("border-radius", "50%")
                .set("background", bg)
                .set("pointer-events", "none");
        if (top != null) b.getStyle().set("top", top);
        if (left != null) b.getStyle().set("left", left);
        if (bottom != null) b.getStyle().set("bottom", bottom);
        if (right != null) b.getStyle().set("right", right);
        return b;
    }

    private Div svgIcon(String d) {
        Div icon = new Div();
        icon.getElement().setProperty("innerHTML",
                "<svg width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='#8fb08a' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='" + d + "'/></svg>");
        icon.getStyle().set("display", "flex").set("align-items", "center").set("margin-left", "8px");
        return icon;
    }

    private String logoSvg(int size) {
        return "<svg width='" + size + "' height='" + size + "' viewBox='0 0 24 24' fill='none' stroke='#8fb08a' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z'/><polyline points='9 22 9 12 15 12 15 22'/></svg>";
    }

    private void injectGlobalCss() {
        UI.getCurrent().getPage().executeJs(
                "if (!document.getElementById('fp-global-styles')) {" +
                "  var style = document.createElement('style');" +
                "  style.id = 'fp-global-styles';" +
                "  style.innerHTML = `" +
                "    vaadin-text-field::part(input-field), vaadin-password-field::part(input-field), vaadin-email-field::part(input-field) {" +
                "      background-color: transparent !important;" +
                "      border: none !important;" +
                "    }" +
                "    vaadin-text-field input, vaadin-password-field input, vaadin-email-field input {" +
                "      color: #ffffff !important;" +
                "      font-family: 'Inter', sans-serif !important;" +
                "      font-size: 13px !important;" +
                "    }" +
                "  `;" +
                "  document.head.appendChild(style);" +
                "}"
        );
    }

    private void err(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void success(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static final String ICON_EMAIL = "M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z'/><polyline points='22,6 12,13 2,6";
    private static final String ICON_LOCK = "M19 11H5a2 2 0 0 0-2 2v7a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7a2 2 0 0 0-2-2zm-7 0V7a5 5 0 0 1 10 0v4";
}
