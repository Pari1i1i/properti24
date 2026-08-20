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
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;

    // State
    private String currentEmail = "";

    public ForgotPasswordView(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        getStyle()
                .set("overflow", "auto")
                .set("flex-wrap", "wrap");

        Div leftPanel  = buildLeftPanel();
        Div rightPanel = buildRightPanel();
        leftPanel.addClassName("fp-left-panel");
        rightPanel.addClassName("fp-right-panel");
        add(leftPanel, rightPanel);

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

        Image logoImg = new Image("images/logo.png", "Property 24 Logo");
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
                .set("min-height", "100vh")
                .set("padding", "32px 0");

        panel.add(mkBubble("500px", "rgba(143,176,138,0.04)", "-200px", null, null, "-200px"));
        panel.add(mkBubble("300px", "rgba(143,176,138,0.05)", null, "-100px", "-100px", null));

        formBox = new Div();
        formBox.addClassName("fp-formbox");
        formBox.getStyle()
                .set("width", "100%")
                .set("max-width", "380px")
                .set("padding", "0 24px")
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
        Anchor loginLink = new Anchor("login", "← Kembali ke Halaman Login");
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
        emailField.getElement().setAttribute("autocomplete", "off");
        emailField.getElement().setAttribute("name", "fp-email");

        Button sendOtpBtn = mkButton("KIRIM KODE OTP  →");
        sendOtpBtn.addClickListener(e -> handleSendOtp());
        emailField.addKeyPressListener(Key.ENTER, e -> handleSendOtp());

        step1Container.add(eLabel, emailField, sendOtpBtn);
    }

    private void buildStep2() {
        step2Container = new Div();
        step2Container.getStyle().set("display", "none");

        Div infoBox = new Div();
        infoBox.getStyle()
                .set("background", "rgba(143,176,138,0.12)")
                .set("border-left", "3px solid #8fb08a")
                .set("padding", "12px 14px")
                .set("border-radius", "8px")
                .set("margin-bottom", "18px")
                .set("color", "#b8c9bf")
                .set("font-size", "12px")
                .set("line-height", "1.5");
        infoBox.setText("Kode 6-digit OTP telah dikirimkan ke email Anda. Periksa kotak masuk atau folder spam Gmail.");

        Div oLabel = fLabel("MASUKKAN 6-DIGIT KODE OTP");
        oLabel.getStyle().set("margin-bottom", "10px");

        // Android-styled 6-digit OTP container
        Div otpContainer = new Div();
        otpContainer.addClassName("android-otp-container");
        otpContainer.getStyle()
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("gap", "8px")
                .set("margin-bottom", "18px");

        for (int i = 1; i <= 6; i++) {
            Div digitBox = new Div();
            digitBox.getElement().setProperty("innerHTML",
                    "<input type='tel' maxlength='1' class='android-otp-digit' id='otp-digit-" + i + "' " +
                    "data-index='" + i + "' inputmode='numeric' pattern='[0-9]*' autocomplete='one-time-code' />");
            otpContainer.add(digitBox);
        }

        Button verifyBtn = mkButton("VERIFIKASI OTP  →");
        verifyBtn.addClickListener(e -> handleVerifyOtp());

        Button resendBtn = new Button("Kirim Ulang OTP");
        resendBtn.setWidthFull();
        resendBtn.getStyle()
                .set("background", "transparent")
                .set("color", "#8fb08a")
                .set("border", "1px solid rgba(143,176,138,0.4)")
                .set("border-radius", "8px")
                .set("height", "44px")
                .set("margin-top", "10px")
                .set("cursor", "pointer")
                .set("font-weight", "600")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px");
        resendBtn.addClickListener(e -> handleSendOtp());

        step2Container.add(infoBox, oLabel, otpContainer, verifyBtn, resendBtn);
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
        newPasswordField.getElement().setAttribute("autocomplete", "new-password");

        Div cpLabel = fLabel("KONFIRMASI PASSWORD BARU");
        cpLabel.getStyle().set("margin-top", "14px");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPlaceholder("Ulangi password baru");
        confirmPasswordField.setWidthFull();
        styleDarkField(confirmPasswordField);
        confirmPasswordField.setPrefixComponent(svgIcon(ICON_LOCK));
        confirmPasswordField.getElement().setAttribute("autocomplete", "new-password");

        Button resetBtn = mkButton("SIMPAN PASSWORD BARU  →");
        resetBtn.addClickListener(e -> handleResetPassword());
        confirmPasswordField.addKeyPressListener(Key.ENTER, e -> handleResetPassword());

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

        if (step == 2) {
            // Attach Android OTP auto-advance & auto-focus script
            String otpInitJs =
                    "setTimeout(() => {" +
                    "  const inputs = document.querySelectorAll('.android-otp-digit');" +
                    "  if (inputs.length > 0) {" +
                    "    inputs[0].focus();" +
                    "    inputs.forEach((input, index) => {" +
                    "      input.oninput = (e) => {" +
                    "        const val = e.target.value;" +
                    "        if (val.length === 1 && index < inputs.length - 1) {" +
                    "          inputs[index + 1].focus();" +
                    "        }" +
                    "      };" +
                    "      input.onkeydown = (e) => {" +
                    "        if (e.key === 'Backspace' && !e.target.value && index > 0) {" +
                    "          inputs[index - 1].focus();" +
                    "        }" +
                    "      };" +
                    "      input.onpaste = (e) => {" +
                    "        e.preventDefault();" +
                    "        const pasteData = (e.clipboardData || window.clipboardData).getData('text').trim();" +
                    "        if (/^\\d{6}$/.test(pasteData)) {" +
                    "          pasteData.split('').forEach((digit, i) => {" +
                    "            if (inputs[i]) inputs[i].value = digit;" +
                    "          });" +
                    "          inputs[inputs.length - 1].focus();" +
                    "        }" +
                    "      };" +
                    "    });" +
                    "  }" +
                    "}, 150);";
            UI.getCurrent().getPage().executeJs(otpInitJs);
        }
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
        String getOtpJs =
                "let otp = '';" +
                "document.querySelectorAll('.android-otp-digit').forEach(inp => otp += (inp.value || ''));" +
                "return otp;";

        UI.getCurrent().getPage().executeJs(getOtpJs).then(String.class, otp -> {
            String cleanOtp = otp != null ? otp.trim() : "";
            if (cleanOtp.length() < 6) {
                err("Masukkan lengkap 6 digit kode OTP!");
                return;
            }

            boolean valid = passwordResetService.verifyOtp(currentEmail, cleanOtp);
            if (valid) {
                success("Kode OTP valid! Silakan masukkan password baru Anda.");
                showStep(3);
            } else {
                err("Kode OTP salah atau sudah kedaluwarsa.");
            }
        });
    }

    private void handleResetPassword() {
        String np = newPasswordField.getValue() != null ? newPasswordField.getValue() : "";
        String cp = confirmPasswordField.getValue() != null ? confirmPasswordField.getValue() : "";

        if (np.length() < 6) {
            err("Password baru minimal harus 6 karakter!");
            return;
        }
        if (!np.equals(cp)) {
            err("Konfirmasi password baru tidak cocok!");
            return;
        }

        String getOtpJs =
                "let otp = '';" +
                "document.querySelectorAll('.android-otp-digit').forEach(inp => otp += (inp.value || ''));" +
                "return otp;";

        UI.getCurrent().getPage().executeJs(getOtpJs).then(String.class, otp -> {
            String cleanOtp = otp != null ? otp.trim() : "";
            try {
                passwordResetService.resetPassword(currentEmail, cleanOtp, np);
                showStep(4);
            } catch (Exception e) {
                err(e.getMessage());
            }
        });
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
                .set("border-radius", "10px")
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
        String css =
                "body,html{margin:0;padding:0;}" +
                ".android-otp-digit {" +
                "  width: 44px;" +
                "  height: 52px;" +
                "  background: rgba(255, 255, 255, 0.08);" +
                "  border: 2px solid rgba(143, 176, 138, 0.35);" +
                "  border-radius: 12px;" +
                "  color: #ffffff;" +
                "  font-family: 'Inter', monospace, sans-serif;" +
                "  font-size: 22px;" +
                "  font-weight: 800;" +
                "  text-align: center;" +
                "  outline: none;" +
                "  transition: all 0.2s ease;" +
                "  box-sizing: border-box;" +
                "}" +
                ".android-otp-digit:focus {" +
                "  border-color: #8fb08a !important;" +
                "  background: rgba(255, 255, 255, 0.14) !important;" +
                "  box-shadow: 0 0 12px rgba(143, 176, 138, 0.45) !important;" +
                "  transform: translateY(-2px);" +
                "}" +
                /* Fix browser autofill override */
                ".fp-formbox input:-webkit-autofill," +
                ".fp-formbox input:-webkit-autofill:hover," +
                ".fp-formbox input:-webkit-autofill:focus," +
                ".fp-formbox input:-webkit-autofill:active {" +
                "  -webkit-box-shadow: 0 0 0 40px #1c3b2e inset !important;" +
                "  -webkit-text-fill-color: white !important;" +
                "  caret-color: white !important;" +
                "}" +
                /* Mobile / Android Stacked Layout (Top Logo Banner + Bottom Curved Card) */
                "@media(max-width: 768px) {" +
                "  vaadin-horizontal-layout, .fp-left-panel, .fp-right-panel { box-sizing: border-box !important; }" +
                "  .fp-left-panel {" +
                "    order: 1 !important;" +
                "    display: flex !important;" +
                "    flex: none !important;" +
                "    width: 100% !important;" +
                "    height: 210px !important;" +
                "    min-height: 210px !important;" +
                "    max-height: 210px !important;" +
                "    background: #f4f8f5 !important;" +
                "    align-items: center !important;" +
                "    justify-content: center !important;" +
                "    position: relative !important;" +
                "    overflow: hidden !important;" +
                "  }" +
                "  .fp-left-panel img {" +
                "    width: 220px !important;" +
                "    max-width: 75% !important;" +
                "    height: auto !important;" +
                "  }" +
                "  .fp-right-panel {" +
                "    order: 2 !important;" +
                "    flex: 1 1 auto !important;" +
                "    width: 100% !important;" +
                "    min-width: 100% !important;" +
                "    max-width: 100% !important;" +
                "    min-height: calc(100vh - 190px) !important;" +
                "    background: #1c3b2e !important;" +
                "    border-radius: 32px 32px 0 0 !important;" +
                "    margin-top: -24px !important;" +
                "    padding: 32px 20px 48px !important;" +
                "    box-sizing: border-box !important;" +
                "    position: relative !important;" +
                "    z-index: 2 !important;" +
                "    box-shadow: 0 -10px 30px rgba(0, 0, 0, 0.2) !important;" +
                "    align-items: flex-start !important;" +
                "  }" +
                "  .fp-formbox {" +
                "    width: 100% !important;" +
                "    max-width: 400px !important;" +
                "    padding: 0 !important;" +
                "    margin: 0 auto !important;" +
                "  }" +
                "  .android-otp-digit {" +
                "    width: calc((100vw - 72px) / 6);" +
                "    max-width: 48px;" +
                "    height: 52px;" +
                "    font-size: 20px;" +
                "  }" +
                "}";

        UI.getCurrent().getPage().executeJs(
                "if (!document.getElementById('p24-fp-css')) {" +
                "  const s = document.createElement('style');" +
                "  s.id = 'p24-fp-css';" +
                "  s.textContent = $0;" +
                "  document.head.appendChild(s);" +
                "}", css);
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
