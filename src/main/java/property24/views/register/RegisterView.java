package property24.views.register;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
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
        getStyle()
                .set("overflow", "auto")
                .set("flex-wrap", "wrap");

        Div leftPanel  = buildLeftPanel();
        Div rightPanel = buildRightPanel();
        leftPanel.addClassName("reg-left-panel");
        rightPanel.addClassName("reg-right-panel");
        add(leftPanel, rightPanel);

        addAttachListener(e -> {
            UI.getCurrent().getPage().addStyleSheet(
                    "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap");
            injectGlobalCss();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // LEFT PANEL
    // ════════════════════════════════════════════════════════════════════════
    private Div buildLeftPanel() {
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

        panel.add(mkBubble("130px", "rgba(143,176,138,0.08)", "-65px", "-65px", null, null));
        panel.add(mkBubble("80px",  "rgba(143,176,138,0.06)", null, null, "-40px", "55%"));
        panel.add(mkBubble("45px",  "rgba(143,176,138,0.07)", "88%", null, null, "6%"));
        panel.add(mkBubble("25px",  "rgba(143,176,138,0.07)", "12%", null, null, "88%"));
        panel.add(mkBubble("60px",  "rgba(143,176,138,0.05)", "50%", null, null, "92%"));

        Div formBox = new Div();
        formBox.addClassName("reg-formbox");
        formBox.getStyle()
                .set("width", "100%").set("max-width", "380px")
                .set("padding", "0 24px").set("position", "relative").set("z-index", "1");

        // Brand header
        Div brandRow = new Div();
        brandRow.getStyle().set("display", "flex").set("align-items", "center")
                .set("gap", "10px").set("margin-bottom", "6px");
        Div smallLogo = new Div();
        smallLogo.getElement().setProperty("innerHTML", logoSvg(30));
        Span brandLabel = new Span("PROPERTY 24");
        brandLabel.getStyle().set("color", "#8fb08a").set("font-family", "'Inter', sans-serif")
                .set("font-size", "11px").set("font-weight", "700").set("letter-spacing", "3px");
        brandRow.add(smallLogo, brandLabel);

        Div title = new Div();
        title.getStyle().set("color", "white").set("font-family", "'Inter', sans-serif")
                .set("font-size", "44px").set("font-weight", "800")
                .set("letter-spacing", "8px").set("line-height", "1").set("margin", "4px 0 2px");
        title.setText("REGISTER");

        Div subtitle = new Div();
        subtitle.getStyle().set("color", "#8fb08a").set("font-family", "'Inter', sans-serif")
                .set("font-size", "12px").set("margin-bottom", "22px");
        subtitle.setText("Asset Management System");

        Hr divider = new Hr();
        divider.getStyle().set("border", "none")
                .set("border-top", "1px solid rgba(143,176,138,0.2)").set("margin", "0 0 22px");

        // Full Name
        Div fnLabel = fLabel("FULL NAME");
        TextField fullName = new TextField();
        fullName.setPlaceholder("Masukkan nama lengkap");
        fullName.setWidthFull();
        styleDarkField(fullName);
        fullName.setPrefixComponent(svgIcon(ICON_PERSON));
        fullName.getElement().setAttribute("autocomplete", "off");
        fullName.getElement().setAttribute("name", "reg-fullname");

        // Email
        Div emLabel = fLabel("EMAIL");
        emLabel.getStyle().set("margin-top", "12px");
        EmailField email = new EmailField();
        email.setPlaceholder("Masukkan email");
        email.setWidthFull();
        styleDarkField(email);
        email.setPrefixComponent(svgIcon(ICON_EMAIL));
        email.getElement().setAttribute("autocomplete", "off");
        email.getElement().setAttribute("name", "reg-email");

        // Password
        Div pwLabel = fLabel("PASSWORD");
        pwLabel.getStyle().set("margin-top", "12px");
        PasswordField password = new PasswordField();
        password.setPlaceholder("Masukkan password");
        password.setWidthFull();
        styleDarkField(password);
        password.setPrefixComponent(svgIcon(ICON_LOCK));
        password.getElement().setAttribute("autocomplete", "new-password");
        password.getElement().setAttribute("name", "reg-password");

        // Confirm Password
        Div cpLabel = fLabel("CONFIRM PASSWORD");
        cpLabel.getStyle().set("margin-top", "12px");
        PasswordField confirmPass = new PasswordField();
        confirmPass.setPlaceholder("Ulangi password");
        confirmPass.setWidthFull();
        styleDarkField(confirmPass);
        confirmPass.setPrefixComponent(svgIcon(ICON_LOCK));
        confirmPass.getElement().setAttribute("autocomplete", "new-password");
        confirmPass.getElement().setAttribute("name", "reg-confirm");

        // Guru / Siswa radio
        Div roleLabel = fLabel("PERAN");
        roleLabel.getStyle().set("margin-top", "14px");

        RadioButtonGroup<String> roleRadio = new RadioButtonGroup<>();
        roleRadio.setItems("Guru", "Siswa");
        roleRadio.setValue("Siswa");
        roleRadio.getElement().getStyle()
                .set("--lumo-body-text-color", "#c8dfc8")
                .set("--lumo-primary-color", "#8fb08a")
                .set("color", "#c8dfc8")
                .set("font-family", "'Inter',sans-serif")
                .set("font-size", "13px");

        // Kelas (only for Siswa)
        Div kelasLabel = fLabel("KELAS");
        kelasLabel.getStyle().set("margin-top", "12px");
        TextField kelasField = new TextField();
        kelasField.setPlaceholder("Contoh: X IPA 1, XII TKJ 2");
        kelasField.setWidthFull();
        styleDarkField(kelasField);
        kelasField.getElement().setAttribute("autocomplete", "off");
        kelasField.getElement().setAttribute("name", "reg-kelas");

        kelasLabel.setVisible(true);
        kelasField.setVisible(true);

        roleRadio.addValueChangeListener(ev -> {
            boolean isSiswa = "Siswa".equals(ev.getValue());
            kelasLabel.setVisible(isSiswa);
            kelasField.setVisible(isSiswa);
        });

        // Agreement checkbox — align-items:center untuk lurusin
        Div agreementDiv = new Div();
        agreementDiv.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("margin-top", "14px");

        Checkbox agreement = new Checkbox();
        agreement.setValue(false);

        Div agreeText = new Div();
        agreeText.getStyle().set("display", "flex").set("flex-wrap", "wrap").set("align-items", "center").set("gap", "2px");

        Span agreePrefix = new Span("Saya menyetujui\u00a0");
        agreePrefix.getStyle().set("color", "#b8c9bf").set("font-family", "'Inter',sans-serif").set("font-size", "12px");

        Span syaratLink = new Span("Syarat & Ketentuan");
        syaratLink.getStyle().set("color", "#8fb08a").set("text-decoration", "underline")
                .set("cursor", "pointer").set("font-family", "'Inter',sans-serif").set("font-size", "12px");
        syaratLink.addClickListener(ev -> showSyaratDialog());

        Span dan = new Span("\u00a0dan\u00a0");
        dan.getStyle().set("color", "#b8c9bf").set("font-family", "'Inter',sans-serif").set("font-size", "12px");

        Span privasiLink = new Span("Kebijakan Privasi");
        privasiLink.getStyle().set("color", "#8fb08a").set("text-decoration", "underline")
                .set("cursor", "pointer").set("font-family", "'Inter',sans-serif").set("font-size", "12px");
        privasiLink.addClickListener(ev -> showPrivasiDialog());

        agreeText.add(agreePrefix, syaratLink, dan, privasiLink);
        agreementDiv.add(agreement, agreeText);

        // Register Button
        Button regBtn = new Button("DAFTAR SEKARANG  \u2192");
        regBtn.setId("register-btn");
        regBtn.setWidthFull();
        regBtn.getStyle()
                .set("background", "linear-gradient(135deg, #4d8f4d 0%, #8fb08a 100%)")
                .set("color", "#0a1f0f").set("font-family", "'Inter', sans-serif")
                .set("font-weight", "700").set("font-size", "12px").set("letter-spacing", "2px")
                .set("border", "none").set("border-radius", "8px").set("height", "50px")
                .set("cursor", "pointer").set("margin-top", "16px").set("transition", "all 0.3s ease")
                .set("box-shadow", "0 4px 15px rgba(78,143,78,0.25)");
        regBtn.addClickListener(e -> onRegister(
                fullName.getValue(), email.getValue(),
                password.getValue(), confirmPass.getValue(),
                agreement.getValue(), roleRadio.getValue(), kelasField.getValue()));

        // Footer
        Div footer = new Div();
        footer.getStyle().set("text-align", "center").set("margin-top", "18px");
        Span hasAcct = new Span("Sudah punya akun?  ");
        hasAcct.getStyle().set("color", "#8fb08a").set("font-family", "'Inter', sans-serif").set("font-size", "13px");
        Anchor loginLink = new Anchor("login", "Login");
        loginLink.getStyle().set("color", "white").set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px").set("font-weight", "700").set("text-decoration", "underline");
        footer.add(hasAcct, loginLink);

        formBox.add(brandRow, title, subtitle, divider,
                fnLabel, fullName, emLabel, email,
                pwLabel, password, cpLabel, confirmPass,
                roleLabel, roleRadio, kelasLabel, kelasField,
                agreementDiv, regBtn, footer);

        confirmPass.addKeyPressListener(Key.ENTER, e -> onRegister(
                fullName.getValue(), email.getValue(),
                password.getValue(), confirmPass.getValue(),
                agreement.getValue(), roleRadio.getValue(), kelasField.getValue()));

        panel.add(formBox);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // RIGHT PANEL
    // ════════════════════════════════════════════════════════════════════════
    private Div buildRightPanel() {
        Div panel = new Div();
        panel.getStyle().set("flex", "1").set("background", "#f4f8f5")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center")
                .set("position", "relative").set("overflow", "hidden").set("min-height", "200px");

        panel.add(mkBubble("340px", "rgba(143,176,138,0.12)", null, null, "-170px", "-170px"));
        panel.add(mkBubble("200px", "rgba(143,176,138,0.09)", "-100px", "-100px", null, null));
        panel.add(mkBubble("110px", "rgba(143,176,138,0.16)", null, "8%", null, "5%"));
        panel.add(mkBubble("60px",  "rgba(143,176,138,0.20)", null, "12%", null, "78%"));
        panel.add(mkBubble("45px",  "rgba(143,176,138,0.18)", null, "78%", null, "6%"));
        panel.add(mkBubble("75px",  "rgba(143,176,138,0.11)", null, "72%", null, "72%"));
        panel.add(mkBubble("30px",  "rgba(28,59,46,0.10)",   null, "40%", null, "2%"));
        panel.add(mkBubble("22px",  "rgba(28,59,46,0.09)",   null, "55%", null, "90%"));

        Image logoImg = new Image("images/logo.png", "Property 24 Logo");
        logoImg.setWidth("280px");
        logoImg.getStyle().set("position", "relative").set("z-index", "1");
        panel.add(logoImg);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // SYARAT & KETENTUAN DIALOG
    // ════════════════════════════════════════════════════════════════════════
    private void showSyaratDialog() {
        Dialog d = new Dialog();
        d.setWidth("520px");
        d.setMaxHeight("80vh");

        Div content = new Div();
        content.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "12px")
                .set("padding", "20px 24px").set("overflow-y", "auto");

        Span header = new Span("\uD83D\uDCCB Syarat & Ketentuan");
        header.getStyle().set("font-family", "'Inter',sans-serif").set("font-size", "18px")
                .set("font-weight", "800").set("color", "#1a2e1a");

        Span updated = new Span("Terakhir diperbarui: Agustus 2026");
        updated.getStyle().set("font-size", "11px").set("color", "#8fb08a").set("font-family", "'Inter',sans-serif");

        Hr hr = new Hr();
        hr.getStyle().set("border", "none").set("border-top", "1px solid #e8f0e8").set("margin", "4px 0");
        content.add(header, updated, hr);

        String[][] sections = {
            {"1. Penggunaan Sistem",
             "Sistem manajemen aset Property 24 hanya boleh digunakan oleh civitas akademika yang terdaftar secara resmi. " +
             "Pengguna wajib menggunakan akun pribadi masing-masing dan tidak diperkenankan meminjamkan akun kepada pihak lain."},
            {"2. Peminjaman Aset",
             "Setiap peminjaman aset harus dilakukan melalui sistem ini dengan menyertakan tujuan penggunaan yang jelas. " +
             "Aset yang dipinjam harus dikembalikan tepat waktu sesuai tanggal rencana pengembalian yang disepakati."},
            {"3. Tanggung Jawab Pengguna",
             "Pengguna bertanggung jawab penuh atas kondisi aset selama masa peminjaman. " +
             "Kerusakan yang terjadi akibat kelalaian pengguna akan diproses sesuai kebijakan institusi yang berlaku."},
            {"4. Pengembalian Aset",
             "Pengembalian aset wajib disertai foto bukti pengembalian yang jelas. " +
             "Pengembalian dinyatakan sah setelah diverifikasi dan disetujui oleh Admin."},
            {"5. Sanksi Pelanggaran",
             "Pelanggaran terhadap ketentuan ini dapat mengakibatkan akun dinonaktifkan dan pengguna tidak dapat mengajukan peminjaman baru " +
             "hingga permasalahan diselesaikan bersama pihak pengelola."},
            {"6. Perubahan Ketentuan",
             "Pengelola berhak mengubah syarat dan ketentuan ini sewaktu-waktu. Perubahan akan diinformasikan melalui sistem " +
             "dan berlaku efektif sejak tanggal pembaruan."}
        };

        for (String[] s : sections) {
            Div sec = new Div();
            sec.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "4px");
            Span secTitle = new Span(s[0]);
            secTitle.getStyle().set("font-family", "'Inter',sans-serif").set("font-size", "13px")
                    .set("font-weight", "700").set("color", "#1a2e1a");
            Span secBody = new Span(s[1]);
            secBody.getStyle().set("font-family", "'Inter',sans-serif").set("font-size", "12px")
                    .set("color", "#5a7a5a").set("line-height", "1.6");
            sec.add(secTitle, secBody);
            content.add(sec);
        }

        Button closeBtn = new Button("Tutup");
        closeBtn.getStyle()
                .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)").set("color", "white")
                .set("border", "none").set("border-radius", "8px").set("height", "38px")
                .set("cursor", "pointer").set("margin-top", "8px")
                .set("font-family", "'Inter',sans-serif").set("font-weight", "700");
        closeBtn.setWidthFull();
        closeBtn.addClickListener(ev -> d.close());
        content.add(closeBtn);
        d.add(content);
        d.open();
    }

    // ════════════════════════════════════════════════════════════════════════
    // KEBIJAKAN PRIVASI DIALOG
    // ════════════════════════════════════════════════════════════════════════
    private void showPrivasiDialog() {
        Dialog d = new Dialog();
        d.setWidth("520px");
        d.setMaxHeight("80vh");

        Div content = new Div();
        content.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "12px")
                .set("padding", "20px 24px").set("overflow-y", "auto");

        Span header = new Span("\uD83D\uDD12 Kebijakan Privasi");
        header.getStyle().set("font-family", "'Inter',sans-serif").set("font-size", "18px")
                .set("font-weight", "800").set("color", "#1a2e1a");

        Span updated = new Span("Terakhir diperbarui: Agustus 2026");
        updated.getStyle().set("font-size", "11px").set("color", "#8fb08a").set("font-family", "'Inter',sans-serif");

        Hr hr = new Hr();
        hr.getStyle().set("border", "none").set("border-top", "1px solid #e8f0e8").set("margin", "4px 0");
        content.add(header, updated, hr);

        String[][] sections = {
            {"1. Data yang Kami Kumpulkan",
             "Kami mengumpulkan data yang Anda berikan saat registrasi, yaitu: nama lengkap, alamat email, kelas (untuk siswa), " +
             "serta data aktivitas peminjaman aset di dalam sistem."},
            {"2. Penggunaan Data",
             "Data Anda digunakan semata-mata untuk keperluan pengelolaan peminjaman aset institusi. " +
             "Data tidak akan dijual, disewakan, atau dibagikan kepada pihak ketiga di luar institusi."},
            {"3. Keamanan Data",
             "Kami menerapkan enkripsi password menggunakan standar BCrypt. " +
             "Akses ke data pengguna dibatasi hanya untuk Admin sistem yang berwenang."},
            {"4. Retensi Data",
             "Data akun Anda akan disimpan selama Anda masih terdaftar sebagai pengguna aktif. " +
             "Akun yang tidak aktif dalam jangka waktu lama dapat dihapus oleh Admin sesuai kebijakan institusi."},
            {"5. Hak Pengguna",
             "Anda berhak meminta koreksi data pribadi Anda dengan menghubungi pengelola sistem. " +
             "Anda juga berhak meminta penghapusan akun dengan syarat tidak ada peminjaman aktif yang belum diselesaikan."},
            {"6. Kontak",
             "Untuk pertanyaan mengenai kebijakan privasi ini, silakan hubungi pengelola Property 24 melalui kantor administrasi institusi."}
        };

        for (String[] s : sections) {
            Div sec = new Div();
            sec.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "4px");
            Span secTitle = new Span(s[0]);
            secTitle.getStyle().set("font-family", "'Inter',sans-serif").set("font-size", "13px")
                    .set("font-weight", "700").set("color", "#1a2e1a");
            Span secBody = new Span(s[1]);
            secBody.getStyle().set("font-family", "'Inter',sans-serif").set("font-size", "12px")
                    .set("color", "#5a7a5a").set("line-height", "1.6");
            sec.add(secTitle, secBody);
            content.add(sec);
        }

        Button closeBtn = new Button("Tutup");
        closeBtn.getStyle()
                .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)").set("color", "white")
                .set("border", "none").set("border-radius", "8px").set("height", "38px")
                .set("cursor", "pointer").set("margin-top", "8px")
                .set("font-family", "'Inter',sans-serif").set("font-weight", "700");
        closeBtn.setWidthFull();
        closeBtn.addClickListener(ev -> d.close());
        content.add(closeBtn);
        d.add(content);
        d.open();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REGISTER HANDLER
    // ════════════════════════════════════════════════════════════════════════
    private void onRegister(String name, String emailVal, String pass, String confirm,
                            Boolean agreed, String role, String kelas) {
        String safeName    = name     != null ? name.trim()     : "";
        String safeEmail   = emailVal != null ? emailVal.trim() : "";
        String safePass    = pass     != null ? pass            : "";
        String safeConfirm = confirm  != null ? confirm         : "";
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
        if ("Siswa".equals(role) && (kelas == null || kelas.isBlank())) {
            err("Kelas wajib diisi untuk siswa!");
            return;
        }
        if (!safeAgreed) {
            err("Kamu harus mencentang persetujuan Syarat & Ketentuan!");
            return;
        }
        try {
            var user = userService.register(safeName, safeEmail, safePass);
            if ("Siswa".equals(role) && kelas != null && !kelas.isBlank()) {
                user.setKelas(kelas.trim());
                userService.save(user);
            }
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
                .set("color", "#b8c9bf").set("font-family", "'Inter', sans-serif")
                .set("font-size", "10px").set("font-weight", "700")
                .set("letter-spacing", "2px").set("margin-bottom", "5px");
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
                .set("position", "absolute").set("width", size).set("height", size)
                .set("border-radius", "50%").set("background", bg).set("pointer-events", "none");
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
            "  border: 2px solid #8fb08a!important;" +
            "  background: rgba(255,255,255,0.12)!important;" +
            "  border-radius: 4px!important;" +
            "}" +
            "vaadin-checkbox[checked]::part(checkbox){" +
            "  background:#8fb08a!important;" +
            "  border-color:#8fb08a!important;" +
            "}" +
            "vaadin-radio-button::part(radio){" +
            "  border: 2px solid #8fb08a!important;" +
            "  background: rgba(255,255,255,0.08)!important;" +
            "}" +
            "vaadin-radio-button[checked]::part(radio){" +
            "  background:#8fb08a!important;" +
            "  border-color:#8fb08a!important;" +
            "}" +
            "vaadin-radio-button::part(label){" +
            "  color:#c8dfc8!important;" +
            "  font-family:'Inter',sans-serif!important;" +
            "  font-size:13px!important;" +
            "vaadin-dialog-overlay::part(content){" +
            "  padding: 24px!important;" +
            "  border-radius: 18px!important;" +
            "}" +
            "@media(max-width:700px){" +
            "  .reg-left-panel{order:2!important;min-height:unset!important;flex:none!important;width:100%!important;padding:28px 0 40px!important;}" +
            "  .reg-right-panel{order:1!important;min-height:200px!important;flex:none!important;width:100%!important;}" +
            "  .reg-formbox{max-width:100%!important;}" +
            "}";
        getElement().executeJs(
            "if(!document.getElementById('p24-reg-css')){" +
            "  const s=document.createElement('style');" +
            "  s.id='p24-reg-css';" +
            "  s.textContent=$0;" +
            "  document.head.appendChild(s);" +
            "}", css);
    }

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
