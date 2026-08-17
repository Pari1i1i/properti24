package property24.views.user;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.timepicker.TimePicker;
import property24.entity.*;
import property24.service.BarangService;
import property24.service.BookingService;
import property24.service.PinjamanService;
import property24.util.AuthSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import property24.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Route("user-dashboard")
@PageTitle("Dashboard | Property 24")
@AnonymousAllowed
public class UserDashboardView extends Div {

    private final BarangService barangService;
    private final PinjamanService pinjamanService;
    private final UserService userService;
    private final BookingService bookingService;

    private User currentUser;
    private String activeTab = "dashboard";
    private String activeKategori = "all";
    private final List<Barang> selectedItems = new ArrayList<>();

    // Main containers
    private Div contentArea;
    private Div bottomNav;
    private Div navDashboard, navBorrow, navMyItems, navReturn, navBooking;

    public UserDashboardView(BarangService barangService, PinjamanService pinjamanService,
                             UserService userService, BookingService bookingService) {
        this.barangService = barangService;
        this.pinjamanService = pinjamanService;
        this.userService = userService;
        this.bookingService = bookingService;

        if (!AuthSession.isLoggedIn()) {
            addAttachListener(e -> UI.getCurrent().navigate("login"));
            return;
        }
        currentUser = AuthSession.getCurrentUser();

        setSizeFull();
        getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("background", "#eef1ee")
                .set("font-family", "'Inter', sans-serif")
                .set("overflow", "hidden")
                .set("height", "100vh");

        // Inject fonts & CSS
        addAttachListener(e -> {
            UI.getCurrent().getPage().addStyleSheet(
                    "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap");
            injectCss();
        });

        contentArea = new Div();
        contentArea.getStyle()
                .set("flex", "1")
                .set("overflow-y", "auto")
                .set("display", "flex")
                .set("flex-direction", "column");

        bottomNav = buildBottomNav();

        add(contentArea, bottomNav);
        showDashboard();
    }

    // ════════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ════════════════════════════════════════════════════════════════════════

    private Div buildBottomNav() {
        Div nav = new Div();
        nav.getStyle()
                .set("display", "flex")
                .set("background", "#1a2520")
                .set("border-top", "1px solid rgba(143,176,138,0.15)")
                .set("padding", "6px 0 4px")
                .set("z-index", "100")
                .set("flex-shrink", "0");

        navDashboard = navTab(IC_HOME, "Dashboard", true);
        navBorrow    = navTab(IC_PLUS_CIRCLE, "Pinjam", false);
        navMyItems   = navTab(IC_LIST, "My Items", false);
        navReturn    = navTab(IC_ROTATE, "Kembali", false);
        navBooking   = navTab(IC_BOOKMARK, "Booking", false);

        navDashboard.addClickListener(e -> switchTab("dashboard"));
        navBorrow.addClickListener(e -> switchTab("borrow"));
        navMyItems.addClickListener(e -> switchTab("myitems"));
        navReturn.addClickListener(e -> switchTab("return"));
        navBooking.addClickListener(e -> switchTab("mybooking"));

        nav.add(navDashboard, navBorrow, navMyItems, navReturn, navBooking);
        return nav;
    }

    private void switchTab(String tab) {
        activeTab = tab;
        updateNavActive();
        contentArea.removeAll();
        switch (tab) {
            case "dashboard" -> showDashboard();
            case "borrow"    -> showBorrowForm();
            case "myitems"   -> showMyItems();
            case "return"    -> showReturnList();
            case "mybooking" -> showMyBookings();
        }
    }

    private void updateNavActive() {
        setNavActive(navDashboard, IC_HOME,        "dashboard".equals(activeTab));
        setNavActive(navBorrow,    IC_PLUS_CIRCLE, "borrow".equals(activeTab));
        setNavActive(navMyItems,   IC_LIST,        "myitems".equals(activeTab));
        setNavActive(navReturn,    IC_ROTATE,      "return".equals(activeTab));
        setNavActive(navBooking,   IC_BOOKMARK,    "mybooking".equals(activeTab));
    }

    private void setNavActive(Div tab, String iconSvgPath, boolean active) {
        String strokeClr = active ? "#4d8f4d" : "rgba(184,201,191,0.5)";
        tab.getChildren().forEach(c -> {
            if (c instanceof Div icon) {
                icon.getElement().setProperty("innerHTML",
                        "<svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='"
                        + strokeClr + "' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'>"
                        + iconSvgPath + "</svg>");
            }
            if (c instanceof Span lbl) {
                lbl.getStyle()
                        .set("color", strokeClr)
                        .set("font-weight", active ? "600" : "400");
            }
        });
    }

    private Div navTab(String iconPath, String label, boolean active) {
        Div tab = new Div();
        tab.getStyle()
                .set("flex", "1")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", "3px")
                .set("cursor", "pointer")
                .set("padding", "4px 0");

        Div icon = new Div();
        icon.getElement().setProperty("innerHTML",
                "<svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='"
                + (active ? "#4d8f4d" : "rgba(184,201,191,0.5)")
                + "' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'>"
                + iconPath + "</svg>");

        Span lbl = new Span(label);
        lbl.getStyle()
                .set("font-size", "9px")
                .set("font-weight", active ? "600" : "400")
                .set("color", active ? "#4d8f4d" : "rgba(184,201,191,0.5)");

        tab.add(icon, lbl);
        return tab;
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOP BAR (shared)
    // ════════════════════════════════════════════════════════════════════════

    private Div buildTopBar(String centerText) {
        Div bar = new Div();
        bar.getStyle()
                .set("background", "#1e2e25")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between")
                .set("padding", "14px 16px 10px")
                .set("flex-shrink", "0");

        Div logoWrap = new Div();
        logoWrap.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("margin-right", "auto");

        Div logoRow = new Div();
        logoRow.getStyle().set("display", "flex").set("align-items", "center").set("gap", "6px");

        Div logoSvgDiv = new Div();
        logoSvgDiv.getElement().setProperty("innerHTML", logoSvg(28));

        Span logoText = new Span("PROPERTY");
        logoText.getStyle()
                .set("color", "white")
                .set("font-size", "16px")
                .set("font-weight", "800")
                .set("letter-spacing", "2px");

        logoRow.add(logoSvgDiv, logoText);
        logoWrap.add(logoRow);

        String initials = currentUser.getNamaLengkap() != null && !currentUser.getNamaLengkap().isBlank()
                ? String.valueOf(currentUser.getNamaLengkap().charAt(0)).toUpperCase() : "U";
        Div avatar = new Div();
        avatar.getStyle()
                .set("width", "34px")
                .set("height", "34px")
                .set("border-radius", "50%")
                .set("background", "#4d8f4d")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("color", "white")
                .set("font-weight", "700")
                .set("font-size", "14px");
        avatar.setText(initials);
        avatar.getStyle().set("cursor", "pointer");
        avatar.addClickListener(e -> showProfileDialog());

        bar.add(logoWrap, avatar);
        return bar;
    }

    // ════════════════════════════════════════════════════════════════════════
    // TAB 1 – DASHBOARD
    // ════════════════════════════════════════════════════════════════════════

    private void showDashboard() {
        contentArea.add(buildTopBar("PROPERTY"));

        // Greeting + search
        Div greeting = new Div();
        greeting.getStyle()
                .set("padding", "14px 16px 0")
                .set("background", "#1e2e25");

        String name = currentUser.getNamaLengkap() != null
                ? currentUser.getNamaLengkap().split(" ")[0] : "User";
        Span greetText = new Span("Halo " + name + " 👋");
        greetText.getStyle()
                .set("color", "white")
                .set("font-size", "15px")
                .set("font-weight", "600")
                .set("display", "block")
                .set("margin-bottom", "10px");

        // Search bar
        TextField search = new TextField();
        search.setPlaceholder("Search assets...");
        search.setWidthFull();
        search.addClassName("user-search");
        search.getStyle().set("margin-bottom", "12px");

        greeting.add(greetText, search);
        contentArea.add(greeting);

        // Category chips
        Div chipsWrap = new Div();
        chipsWrap.getStyle()
                .set("display", "flex")
                .set("gap", "8px")
                .set("padding", "12px 16px 8px")
                .set("background", "#1e2e25")
                .set("overflow-x", "auto")
                .set("flex-shrink", "0");

        List<String> kategoriNames = barangService.getAllKategori().stream()
                .map(Kategori::getNamaKategori).collect(Collectors.toList());

        Div allChip = chip("All", "all", true);
        chipsWrap.add(allChip);

        List<Div> chips = new ArrayList<>();
        chips.add(allChip);
        for (String k : kategoriNames) {
            Div c = chip(k, k, false);
            chipsWrap.add(c);
            chips.add(c);
        }

        // Grid container
        Div gridWrap = new Div();
        gridWrap.getStyle()
                .set("flex", "1")
                .set("background", "#eef1ee")
                .set("padding", "0 12px 12px");

        // Header row
        Div headerRow = new Div();
        headerRow.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between")
                .set("padding", "14px 4px 10px");

        Span available = new Span("Available Items");
        available.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "800")
                .set("color", "#1a2e1a");

        Span[] countBadge = {itemCountBadge(barangService.getAllBarang().size())};
        headerRow.add(available, countBadge[0]);

        Div grid = new Div();
        grid.addClassName("asset-grid-2col");

        // Populate grid
        Runnable[] reloadGrid = new Runnable[1];
        reloadGrid[0] = () -> {
            grid.removeAll();
            List<Barang> items = "all".equals(activeKategori)
                    ? barangService.getAllBarang()
                    : barangService.getByKategori(activeKategori);
            String q = search.getValue();
            if (q != null && !q.isBlank()) {
                String lq = q.toLowerCase();
                items = items.stream()
                        .filter(b -> b.getNamaBarang().toLowerCase().contains(lq))
                        .collect(Collectors.toList());
            }
            for (Barang b : items) grid.add(buildAssetCard(b));
            // update count badge
            headerRow.remove(countBadge[0]);
            countBadge[0] = itemCountBadge(items.size());
            headerRow.add(countBadge[0]);
        };

        // Chip click handler
        for (Div c : chips) {
            String catVal = c.getElement().getAttribute("data-cat");
            c.addClickListener(e -> {
                activeKategori = catVal;
                chips.forEach(ch -> styleChip(ch, ch.getElement().getAttribute("data-cat").equals(catVal)));
                reloadGrid[0].run();
            });
        }

        search.addValueChangeListener(e -> reloadGrid[0].run());
        reloadGrid[0].run();

        gridWrap.add(headerRow, grid);
        contentArea.add(chipsWrap, gridWrap);
    }

    private Div buildAssetCard(Barang b) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("overflow", "hidden")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.07)")
                .set("cursor", "pointer")
                .set("transition", "transform 0.15s");
        card.addClassName("asset-card-u");

        // Photo
        Div imgWrap = new Div();
        imgWrap.getStyle()
                .set("position", "relative")
                .set("height", "110px")
                .set("background", "#e8f0ea")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("overflow", "hidden");

        if (b.getFotoBarang() != null && !b.getFotoBarang().isBlank()) {
            Image img = new Image("images/" + b.getFotoBarang().trim(), b.getNamaBarang());
            img.getStyle().set("width", "100%").set("height", "100%").set("object-fit", "cover");
            imgWrap.add(img);
        } else {
            Div ic = new Div();
            ic.getElement().setProperty("innerHTML",
                    "<svg width='36' height='36' viewBox='0 0 24 24' fill='none' stroke='#8fb08a' stroke-width='1.5'>"
                    + IC_BOX + "</svg>");
            imgWrap.add(ic);
        }

        boolean isRusak = b.getStatus() == Barang.Status.rusak;
        boolean isDiperbaiki = b.getStatus() == Barang.Status.diperbaiki;
        boolean borrowed = b.getStatus() == Barang.Status.dipinjam;
        boolean isBooked = b.getStatus() == Barang.Status.tersedia && bookingService.isBarangBooked(b);
        boolean avail = b.getStatus() == Barang.Status.tersedia && !isBooked;

        String badgeTxt = isRusak ? "RUSAK" : isDiperbaiki ? "DIPERBAIKI" : borrowed ? "BORROWED" : isBooked ? "DIBOOKING" : "AVAILABLE";
        String badgeClr = isRusak ? "#ff5252" : isDiperbaiki ? "#e07a2a" : borrowed ? "#ff9f43" : isBooked ? "#e07a2a" : "#2ed573";
        String badgeBg  = isRusak ? "rgba(255,82,82,0.2)" : isDiperbaiki ? "rgba(224,122,42,0.2)" : borrowed ? "rgba(255,159,67,0.2)" : isBooked ? "rgba(224,122,42,0.2)" : "rgba(46,213,115,0.2)";

        Div badge = new Div();
        badge.setText(badgeTxt);
        badge.getStyle()
                .set("position", "absolute").set("top", "6px").set("left", "6px")
                .set("background", badgeBg).set("color", badgeClr)
                .set("font-size", "8px").set("font-weight", "700")
                .set("padding", "2px 7px").set("border-radius", "20px")
                .set("border", "1px solid " + badgeClr);
        imgWrap.add(badge);

        String kode = b.getKodeBarang() != null ? b.getKodeBarang() : "AST-" + String.format("%03d", b.getId());
        Div kodeChip = new Div();
        kodeChip.setText(kode);
        kodeChip.getStyle()
                .set("position", "absolute").set("top", "6px").set("right", "6px")
                .set("background", "rgba(0,0,0,0.4)").set("color", "white")
                .set("font-size", "8px").set("font-weight", "700")
                .set("padding", "2px 6px").set("border-radius", "5px");
        imgWrap.add(kodeChip);

        // Info
        Div info = new Div();
        info.getStyle().set("padding", "8px 10px 10px");

        Span nama = new Span(b.getNamaBarang());
        nama.getStyle()
                .set("font-size", "13px").set("font-weight", "700")
                .set("color", "#1a2e1a").set("display", "block")
                .set("white-space", "nowrap").set("overflow", "hidden")
                .set("text-overflow", "ellipsis");

        String katName = b.getKategori() != null ? b.getKategori().getNamaKategori().toUpperCase() : "";
        Span kat = new Span(katName);
        kat.getStyle()
                .set("font-size", "9px").set("color", "#6b8a6b")
                .set("font-weight", "600").set("letter-spacing", "0.5px")
                .set("display", "block").set("margin-bottom", "4px");

        // Stars
        int stars = b.getBintangSaatIni() != null ? b.getBintangSaatIni() : 0;
        Div starsDiv = new Div();
        starsDiv.getStyle().set("display", "flex").set("gap", "1px").set("margin-bottom", "4px");
        for (int i = 1; i <= 5; i++) {
            Span s = new Span("★");
            s.getStyle().set("color", i <= stars ? "#f5a623" : "#dde0da").set("font-size", "12px");
            starsDiv.add(s);
        }

        // Desc
        String descTxt = b.getDeskripsiBintang() != null && !b.getDeskripsiBintang().isBlank()
                ? b.getDeskripsiBintang() : "";
        Span desc = new Span(descTxt);
        desc.getStyle()
                .set("font-size", "10px").set("color", "#7a9a7a")
                .set("display", "block").set("overflow", "hidden")
                .set("display", "-webkit-box")
                .set("-webkit-line-clamp", "2")
                .set("-webkit-box-orient", "vertical")
                .set("min-height", "28px");

        info.add(kat, nama, starsDiv, desc);
        card.add(imgWrap, info);

        card.addClickListener(e -> showItemDetail(b));
        return card;
    }

    private void showItemDetail(Barang b) {
        com.vaadin.flow.component.dialog.Dialog d = new com.vaadin.flow.component.dialog.Dialog();
        d.setModal(true);
        d.setWidth("360px");
        d.getElement().getStyle().set("--lumo-base-color", "#16281b").set("--lumo-body-text-color", "white");

        VerticalLayout layout = new VerticalLayout();
        layout.getStyle()
                .set("background", "#16281b").set("border-radius", "18px")
                .set("padding", "20px").set("border", "1px solid rgba(143,176,138,0.2)")
                .set("gap", "0");
        layout.setSpacing(false);
        layout.setPadding(false);

        // Photo
        Div photoWrap = new Div();
        photoWrap.getStyle()
                .set("width", "100%").set("min-height", "160px")
                .set("border-radius", "12px").set("overflow", "hidden")
                .set("background", "#1c3b2e").set("display", "flex")
                .set("align-items", "center").set("justify-content", "center")
                .set("margin-bottom", "14px");
        if (b.getFotoBarang() != null && !b.getFotoBarang().isBlank()) {
            Image img = new Image("images/" + b.getFotoBarang().trim(), b.getNamaBarang());
            img.getStyle().set("width", "100%").set("height", "auto").set("max-height", "200px").set("object-fit", "contain");
            photoWrap.add(img);
        } else {
            Div ic = new Div();
            ic.getElement().setProperty("innerHTML", "<svg width='48' height='48' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.4)' stroke-width='1.5'>" + IC_BOX + "</svg>");
            photoWrap.add(ic);
        }

        Span title = new Span(b.getNamaBarang());
        title.getStyle().set("color", "white").set("font-size", "18px").set("font-weight", "800")
                .set("display", "block").set("margin-bottom", "8px");

        String katStr = b.getKategori() != null ? b.getKategori().getNamaKategori() : "—";
        String ruanganStr = b.getRuangan() != null ? b.getRuangan().getNamaRuangan() : "—";
        String stockStr = (b.getStock() != null ? b.getStock() : 0) + " unit";

        layout.add(photoWrap, title);
        layout.add(detailRow("Kategori", katStr));
        layout.add(detailRow("Ruangan", ruanganStr));
        layout.add(detailRow("Stock", stockStr));
        if (b.getDeskripsiBintang() != null && !b.getDeskripsiBintang().isBlank()) {
            layout.add(detailRow("Kondisi", b.getDeskripsiBintang()));
        }

        // Action footer
        Div footer = new Div();
        footer.getStyle().set("display", "flex").set("gap", "8px")
                .set("margin-top", "16px").set("width", "100%");

        Button closeBtn = new Button("Tutup", ev -> d.close());
        closeBtn.getStyle()
                .set("flex", "1").set("background", "rgba(255,255,255,0.08)")
                .set("color", "#b8c9bf").set("border", "1px solid rgba(255,255,255,0.15)")
                .set("border-radius", "10px").set("height", "42px").set("cursor", "pointer");

        boolean isRusak = b.getStatus() == Barang.Status.rusak;
        boolean isDiperbaiki = b.getStatus() == Barang.Status.diperbaiki;
        boolean isDipinjam = b.getStatus() == Barang.Status.dipinjam;
        boolean isBooked = b.getStatus() == Barang.Status.tersedia && bookingService.isBarangBooked(b);
        boolean canBorrow = b.getStatus() == Barang.Status.tersedia && !isBooked;

        if (canBorrow) {
            Button bookingBtn = new Button("Booking", ev -> {
                d.close();
                showBookingModal(b);
            });
            bookingBtn.getStyle()
                    .set("flex", "1").set("border", "none").set("border-radius", "10px")
                    .set("height", "42px").set("cursor", "pointer")
                    .set("font-weight", "700").set("font-size", "12px")
                    .set("background", "linear-gradient(135deg,#e07a2a,#b35c17)")
                    .set("color", "white");

            Button borrowBtn = new Button("Pinjam", ev -> {
                d.close();
                boolean exists = selectedItems.stream()
                        .anyMatch(item -> (item.getId() != null && item.getId().equals(b.getId()))
                                || (item.getKodeBarang() != null && item.getKodeBarang().equalsIgnoreCase(b.getKodeBarang())));
                if (!exists) {
                    selectedItems.add(b);
                } else {
                    info("Barang '" + b.getNamaBarang() + "' sudah ada di dalam daftar peminjaman!");
                }
                switchTab("borrow");
            });
            borrowBtn.getStyle()
                    .set("flex", "1").set("border", "none").set("border-radius", "10px")
                    .set("height", "42px").set("cursor", "pointer")
                    .set("font-weight", "700").set("font-size", "12px")
                    .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)")
                    .set("color", "white");

            footer.add(closeBtn, bookingBtn, borrowBtn);
        } else {
            String textBtn = isRusak ? "Barang Rusak" :
                             isDiperbaiki ? "Sedang Diperbaiki" :
                             isDipinjam ? "Sedang Dipinjam" :
                             isBooked ? "Sedang Dibooking" : "Tidak Tersedia";
            Button disabledBtn = new Button(textBtn);
            disabledBtn.setEnabled(false);
            disabledBtn.getStyle()
                    .set("flex", "2").set("border", "none").set("border-radius", "10px")
                    .set("height", "42px").set("cursor", "default")
                    .set("font-weight", "700").set("font-size", "13px")
                    .set("background", isRusak ? "#c62828" : isDiperbaiki ? "#e07a2a" : "#555")
                    .set("color", "white");

            footer.add(closeBtn, disabledBtn);
        }
        layout.add(footer);
        d.add(layout);
        d.open();
    }

    // ════════════════════════════════════════════════════════════════════════
    // TAB 2 – BORROW FORM
    // ════════════════════════════════════════════════════════════════════════

    private void showBorrowForm() {
        contentArea.add(buildTopBar("PROPERTY"));

        Div page = new Div();
        page.getStyle()
                .set("flex", "1")
                .set("background", "#f5f7f5")
                .set("overflow-y", "auto")
                .set("padding", "16px");

        // Page header
        Div ph = new Div();
        ph.getStyle().set("display", "flex").set("align-items", "center").set("gap", "10px").set("margin-bottom", "16px");
        Div backBtn = new Div();
        backBtn.getElement().setProperty("innerHTML",
                "<svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='#1a2e1a' stroke-width='2'>"
                + "<polyline points='15 18 9 12 15 6'/></svg>");
        backBtn.getStyle().set("cursor", "pointer");
        backBtn.addClickListener(e -> switchTab("dashboard"));
        Span phTitle = new Span("Borrow Request");
        phTitle.getStyle().set("font-size", "18px").set("font-weight", "800").set("color", "#1a2e1a");
        Span phSub = new Span("Fill in the details to submit a borrow request.");
        phSub.getStyle().set("font-size", "11px").set("color", "#6b8a6b").set("display", "block");
        Div phTxt = new Div();
        phTxt.add(phTitle, phSub);
        ph.add(backBtn, phTxt);
        page.add(ph);

        // Selected Items Section — PER-ITEM BORROW DETAILS
        Div selectedSection = new Div();
        selectedSection.addClassName("borrow-card");
        Span selLabel = new Span();
        selLabel.getStyle().set("font-size", "12px").set("color", "#6b8a6b").set("display", "block").set("margin-bottom", "10px");
        selectedSection.add(formSectionTitle("Detail Peminjaman Barang"), selLabel);

        Div itemList = new Div();
        itemList.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "16px");
        selectedSection.add(itemList);

        List<Ruangan> allRuangan = barangService.getAllRuangan();
        List<ItemFormHolder> holders = new ArrayList<>();

        Runnable[] refreshItems = new Runnable[1];
        refreshItems[0] = () -> {
            itemList.removeAll();
            holders.clear();
            selLabel.setText(selectedItems.size() + " item(s) dipilih — Atur tempat & tanggal untuk setiap barang");

            for (Barang item : new ArrayList<>(selectedItems)) {
                Div itemCard = new Div();
                itemCard.getStyle()
                        .set("background", "#ffffff")
                        .set("border", "1px solid #d4e2d4")
                        .set("border-radius", "12px")
                        .set("padding", "14px 16px")
                        .set("display", "flex")
                        .set("flex-direction", "column")
                        .set("gap", "12px")
                        .set("box-shadow", "0 2px 6px rgba(0,0,0,0.03)");

                // 1. Header: Thumb + Title + Remove button
                Div headerRow = new Div();
                headerRow.getStyle().set("display", "flex").set("align-items", "center").set("gap", "10px");

                Div thumb = new Div();
                thumb.getStyle().set("width", "48px").set("height", "48px")
                        .set("border-radius", "8px").set("overflow", "hidden")
                        .set("flex-shrink", "0").set("background", "#e0eae0");
                if (item.getFotoBarang() != null && !item.getFotoBarang().isBlank()) {
                    Image img = new Image("images/" + item.getFotoBarang().trim(), item.getNamaBarang());
                    img.getStyle().set("width", "100%").set("height", "100%").set("object-fit", "cover");
                    thumb.add(img);
                }

                Div meta = new Div();
                meta.getStyle().set("flex", "1");
                Span iName = new Span(item.getNamaBarang());
                iName.getStyle().set("font-size", "14px").set("font-weight", "700").set("color", "#1a2e1a").set("display", "block");
                String katN = item.getKategori() != null ? item.getKategori().getNamaKategori().toUpperCase() : "";
                String kodeN = item.getKodeBarang() != null ? " [" + item.getKodeBarang() + "]" : "";
                Span iKat = new Span(katN + kodeN);
                iKat.getStyle().set("font-size", "10px").set("color", "#6b8a6b").set("font-weight", "600");
                meta.add(iName, iKat);

                Div removeBtn = new Div();
                removeBtn.getElement().setProperty("innerHTML",
                        "<svg width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='#e06a6a' stroke-width='2'>"
                                + "<line x1='18' y1='6' x2='6' y2='18'/><line x1='6' y1='6' x2='18' y2='18'/></svg>");
                removeBtn.getStyle().set("cursor", "pointer").set("flex-shrink", "0");
                removeBtn.addClickListener(ev -> {
                    selectedItems.remove(item);
                    refreshItems[0].run();
                });
                headerRow.add(thumb, meta, removeBtn);

                // 2. Ruangan Pemakaian
                ComboBox<Ruangan> ruanganBox = new ComboBox<>("Ruang Pemakaian *");
                ruanganBox.setItems(allRuangan);
                ruanganBox.setItemLabelGenerator(Ruangan::getNamaRuangan);
                ruanganBox.setWidthFull();
                ruanganBox.setPlaceholder("Pilih ruangan pemakaian...");
                if (item.getRuangan() != null) {
                    ruanganBox.setValue(item.getRuangan());
                } else if (!allRuangan.isEmpty()) {
                    ruanganBox.setValue(allRuangan.get(0));
                }

                // 3. Tujuan Peminjaman
                TextField tujuanField = new TextField("Tujuan Peminjaman *");
                tujuanField.setPlaceholder("Keperluan peminjaman untuk " + item.getNamaBarang());
                tujuanField.setWidthFull();

                // 4. Date Row (Default: Today LocalDate.now())
                DatePicker tglPinjamPicker = new DatePicker("Tanggal Pinjam *");
                tglPinjamPicker.setMin(LocalDate.now());
                tglPinjamPicker.setValue(LocalDate.now());
                tglPinjamPicker.setWidthFull();

                DatePicker tglKembaliPicker = new DatePicker("Tanggal Kembali *");
                tglKembaliPicker.setMin(LocalDate.now());
                tglKembaliPicker.setValue(LocalDate.now());
                tglKembaliPicker.setWidthFull();

                tglPinjamPicker.addValueChangeListener(ev -> {
                    LocalDate start = ev.getValue();
                    if (start != null) {
                        if (start.isBefore(LocalDate.now())) {
                            tglPinjamPicker.setValue(LocalDate.now());
                            start = LocalDate.now();
                        }
                        tglKembaliPicker.setMin(start);
                        if (tglKembaliPicker.getValue() != null && tglKembaliPicker.getValue().isBefore(start)) {
                            tglKembaliPicker.setValue(start);
                        }
                    }
                });

                tglKembaliPicker.addValueChangeListener(ev -> {
                    LocalDate end = ev.getValue();
                    LocalDate start = tglPinjamPicker.getValue() != null ? tglPinjamPicker.getValue() : LocalDate.now();
                    if (end != null && end.isBefore(start)) {
                        tglKembaliPicker.setValue(start);
                    }
                });

                Div datesGrid = new Div();
                datesGrid.getStyle().set("display", "grid")
                        .set("grid-template-columns", "1fr 1fr").set("gap", "10px");
                datesGrid.add(tglPinjamPicker, tglKembaliPicker);

                itemCard.add(headerRow, ruanganBox, tujuanField, datesGrid);
                itemList.add(itemCard);

                ItemFormHolder holder = new ItemFormHolder();
                holder.barang = item;
                holder.ruanganBox = ruanganBox;
                holder.tujuanField = tujuanField;
                holder.tglPinjamPicker = tglPinjamPicker;
                holder.tglKembaliPicker = tglKembaliPicker;
                holders.add(holder);
            }

            Div addMore = new Div();
            addMore.getElement().setProperty("innerHTML",
                    "<span style='color:#4d8f4d;font-size:13px;font-weight:600;cursor:pointer'>+ Tambah Barang Lain</span>");
            addMore.addClickListener(ev -> switchTab("dashboard"));
            itemList.add(addMore);
        };
        refreshItems[0].run();
        page.add(selectedSection);

        // Upload foto bukti — real file picker
        String[] borrowFotoName = {null};
        Div uploadCard = new Div();
        uploadCard.addClassName("borrow-card");
        Span uploadLabel = new Span("Upload Foto Bukti Peminjam *");
        uploadLabel.getStyle().set("font-size", "13px").set("font-weight", "600")
                .set("color", "#1a2e1a").set("display", "block").set("margin-bottom", "8px");
        MemoryBuffer borrowBuffer = new MemoryBuffer();
        Upload borrowUpload = new Upload(borrowBuffer);
        borrowUpload.setAcceptedFileTypes("image/*");
        borrowUpload.setMaxFiles(1);
        borrowUpload.setMaxFileSize(10 * 1024 * 1024);
        borrowUpload.setUploadButton(buildUserUploadButton("📷  Pilih Foto Bukti"));
        borrowUpload.setDropLabel(new Span("JPG, PNG, or HEIC · Max 10MB"));
        borrowUpload.setWidthFull();
        borrowUpload.addSucceededListener(ev -> borrowFotoName[0] = ev.getFileName());
        uploadCard.add(uploadLabel, borrowUpload);
        page.add(uploadCard);

        // Inline validation error banner
        Div errBanner = new Div();
        errBanner.getStyle()
                .set("background", "#ffebee")
                .set("color", "#c62828")
                .set("border", "1px solid #ef9a9a")
                .set("border-radius", "10px")
                .set("padding", "12px 14px")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("display", "none")
                .set("margin-bottom", "10px")
                .set("align-items", "center")
                .set("gap", "8px");
        page.add(errBanner);

        // Submit button
        Button submitBtn = new Button("Ajukan Peminjaman");
        submitBtn.setWidthFull();
        submitBtn.getStyle()
                .set("height", "50px").set("border-radius", "12px")
                .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)")
                .set("color", "white").set("font-weight", "700")
                .set("font-size", "14px").set("border", "none")
                .set("cursor", "pointer").set("margin-top", "4px");

        submitBtn.addClickListener(e -> {
            errBanner.getStyle().set("display", "none");

            if (selectedItems.isEmpty()) {
                errBanner.setText("⚠️ Belum ada barang yang dipilih! Silakan kembali ke Dashboard untuk memilih barang.");
                errBanner.getStyle().set("display", "flex");
                err("Pilih minimal 1 barang dari Dashboard!");
                return;
            }

            for (ItemFormHolder h : holders) {
                h.ruanganBox.setInvalid(false);
                h.tujuanField.setInvalid(false);

                if (h.ruanganBox.getValue() == null) {
                    h.ruanganBox.setInvalid(true);
                    errBanner.setText("⚠️ Harap pilih Ruang Pemakaian untuk '" + h.barang.getNamaBarang() + "'!");
                    errBanner.getStyle().set("display", "flex");
                    err("Pilih ruangan pemakaian untuk " + h.barang.getNamaBarang() + "!");
                    return;
                }
                if (h.tujuanField.getValue().isBlank()) {
                    h.tujuanField.setInvalid(true);
                    errBanner.setText("⚠️ Harap isi Tujuan Peminjaman untuk '" + h.barang.getNamaBarang() + "'!");
                    errBanner.getStyle().set("display", "flex");
                    err("Isi tujuan peminjaman untuk " + h.barang.getNamaBarang() + "!");
                    return;
                }
                if (h.tglPinjamPicker.getValue() == null) {
                    errBanner.setText("⚠️ Harap pilih Tanggal Pinjam untuk '" + h.barang.getNamaBarang() + "'!");
                    errBanner.getStyle().set("display", "flex");
                    err("Pilih tanggal pinjam!");
                    return;
                }
                if (h.tglKembaliPicker.getValue() == null) {
                    errBanner.setText("⚠️ Harap pilih Tanggal Kembali untuk '" + h.barang.getNamaBarang() + "'!");
                    errBanner.getStyle().set("display", "flex");
                    err("Pilih tanggal kembali!");
                    return;
                }
            }

            if (borrowFotoName[0] == null) {
                errBanner.setText("⚠️ Harap upload / ambil Foto Bukti terlebih dahulu!");
                errBanner.getStyle().set("display", "flex");
                err("Upload foto bukti terlebih dahulu!");
                return;
            }

            String savedFoto = null;
            try {
                savedFoto = property24.util.FileUploadHelper.saveImage(borrowBuffer, borrowFotoName[0]);
            } catch (Exception ex) {
                errBanner.setText("⚠️ Gagal menyimpan foto bukti: " + ex.getMessage());
                errBanner.getStyle().set("display", "flex");
                err("Gagal menyimpan foto bukti!");
                return;
            }

            try {
                List<PinjamanService.ItemBorrowRequest> requests = new ArrayList<>();
                for (ItemFormHolder h : holders) {
                    PinjamanService.ItemBorrowRequest req = new PinjamanService.ItemBorrowRequest(
                            h.barang,
                            h.ruanganBox.getValue(),
                            h.tujuanField.getValue().trim(),
                            h.tglPinjamPicker.getValue(),
                            h.tglKembaliPicker.getValue()
                    );
                    requests.add(req);
                }

                pinjamanService.createPinjamanDetailed(currentUser, requests, savedFoto);
                selectedItems.clear();
                ok("Peminjaman berhasil diajukan! Barang berhasil dipinjam.");
                switchTab("myitems");
            } catch (Exception ex) {
                errBanner.setText("⚠️ Gagal mengajukan peminjaman: " + ex.getMessage());
                errBanner.getStyle().set("display", "flex");
                err("Gagal mengajukan peminjaman: " + ex.getMessage());
            }
        });

        Span disclaimer = new Span("Dengan submit, Anda menyetujui Syarat & Ketentuan peminjaman.");
        disclaimer.getStyle().set("font-size", "10px").set("color", "#8fb08a")
                .set("display", "block").set("text-align", "center").set("margin-top", "8px");

        // Catatan tambahan (opsional)
        TextArea catatan = new TextArea("Catatan Tambahan (Opsional)");
        catatan.setPlaceholder("Catatan khusus jika ada (opsional)");
        catatan.setWidthFull();
        catatan.setHeight("80px");
        Div catatanCard = new Div();
        catatanCard.addClassName("borrow-card");
        catatanCard.add(catatan);

        Div infoBox = new Div();
        infoBox.getStyle()
                .set("background", "#e8f5e8").set("border-radius", "10px")
                .set("padding", "12px 14px").set("margin-top", "4px");
        Span infoTxt = new Span("Pastikan semua data yang diisi sudah benar. Peminjaman akan langsung aktif.");
        infoTxt.getStyle().set("font-size", "11px").set("color", "#3a6a3a");
        infoBox.add(infoTxt);

        Div submitWrap = new Div();
        submitWrap.getStyle().set("padding", "0 0 8px");
        submitWrap.add(errBanner, submitBtn, disclaimer, catatanCard, infoBox);

        page.add(submitWrap);
        contentArea.add(page);
    }

    private static class ItemFormHolder {
        Barang barang;
        ComboBox<Ruangan> ruanganBox;
        TextField tujuanField;
        DatePicker tglPinjamPicker;
        DatePicker tglKembaliPicker;
    }

    // ════════════════════════════════════════════════════════════════════════
    // TAB 3 – MY ITEMS (Barang Dipinjam)
    // ════════════════════════════════════════════════════════════════════════

    private void showMyItems() {
        contentArea.add(buildTopBar("PROPERTY"));

        Div page = new Div();
        page.getStyle()
                .set("flex", "1")
                .set("background", "#f5f7f5")
                .set("overflow-y", "auto")
                .set("padding", "16px");

        // Header
        Span title = new Span("My Borrowed Items");
        title.getStyle().set("font-size", "18px").set("font-weight", "800").set("color", "#1a2e1a").set("display", "block");
        Span sub = new Span("Track your active loans and return items when done");
        sub.getStyle().set("font-size", "11px").set("color", "#6b8a6b").set("display", "block").set("margin-bottom", "14px");
        page.add(title, sub);

        // Filter chips
        Div chipsWrap = new Div();
        chipsWrap.getStyle().set("display", "flex").set("gap", "8px").set("margin-bottom", "14px").set("overflow-x", "auto");
        String[] filters = {"All", "Active", "Pending Return", "Rejected", "Returned"};
        List<Div> filterChipList = new ArrayList<>();
        String[] activeMyFilter = {"All"};

        List<PinjamanDetail> allDetails = pinjamanService.getDetailsByUser(currentUser);

        // Item count
        Span countLabel = new Span();
        countLabel.getStyle().set("font-size", "12px").set("color", "#6b8a6b").set("display", "block").set("margin-bottom", "10px");

        // Items list container
        Div list = new Div();
        list.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "10px");

        Runnable reloadMyItems = () -> {
            list.removeAll();
            List<PinjamanDetail> filtered = allDetails.stream()
                .filter(d -> {
                    boolean isReturned = Boolean.TRUE.equals(d.getSudahDikembalikan());
                    Optional<Pengembalian> pengembalian = pinjamanService.getPengembalianForDetail(d);
                    Pengembalian.StatusAcc pAcc = pengembalian.map(Pengembalian::getStatusAcc).orElse(null);
                    boolean isPendingReturn = pengembalian.isPresent() && !isReturned && pAcc == Pengembalian.StatusAcc.pending;
                    boolean isRejectedReturn = pengembalian.isPresent() && !isReturned && pAcc == Pengembalian.StatusAcc.rejected;

                    return switch (activeMyFilter[0]) {
                        case "Active" -> !isReturned && !isPendingReturn && !isRejectedReturn;
                        case "Pending Return" -> isPendingReturn;
                        case "Rejected" -> isRejectedReturn;
                        case "Returned" -> isReturned;
                        default -> true; // All
                    };
                })
                .sorted((d1, d2) -> {
                    boolean r1 = Boolean.TRUE.equals(d1.getSudahDikembalikan());
                    boolean r2 = Boolean.TRUE.equals(d2.getSudahDikembalikan());
                    if (r1 != r2) {
                        return r1 ? 1 : -1; // Active items (false) first at top, Returned (true) at bottom
                    }
                    Long id1 = d1.getId() != null ? d1.getId() : 0L;
                    Long id2 = d2.getId() != null ? d2.getId() : 0L;
                    return Long.compare(id2, id1); // Newest first
                })
                .collect(Collectors.toList());

            countLabel.setText(filtered.size() + " items");

            if (filtered.isEmpty()) {
                Div empty = new Div();
                empty.getStyle().set("text-align", "center").set("padding", "32px").set("color", "#8fb08a");
                empty.setText("Tidak ada barang untuk kategori ini.");
                list.add(empty);
            } else {
                for (PinjamanDetail d : filtered) {
                    list.add(buildMyItemCard(d));
                }
            }
        };

        for (String f : filters) {
            Div c = chip(f, f, f.equals(activeMyFilter[0]));
            filterChipList.add(c);
            c.addClickListener(ev -> {
                activeMyFilter[0] = f;
                filterChipList.forEach(ch -> styleChip(ch, ch.getElement().getAttribute("data-cat").equals(f)));
                reloadMyItems.run();
            });
            chipsWrap.add(c);
        }

        reloadMyItems.run();
        page.add(chipsWrap, countLabel, list);
        contentArea.add(page);
    }

    private Div buildMyItemCard(PinjamanDetail d) {
        Barang b = d.getBarang();
        Optional<Pengembalian> pengembalian = pinjamanService.getPengembalianForDetail(d);
        Pengembalian pObj = pengembalian.orElse(null);
        Pengembalian.StatusAcc pAcc = pObj != null ? pObj.getStatusAcc() : null;

        boolean isReturned = d.getSudahDikembalikan() != null && d.getSudahDikembalikan();
        boolean isPendingReturn = pengembalian.isPresent() && !isReturned && pAcc == Pengembalian.StatusAcc.pending;
        boolean isRejectedReturn = pengembalian.isPresent() && !isReturned && pAcc == Pengembalian.StatusAcc.rejected;

        String statusTxt = isReturned ? "RETURNED"
                : isPendingReturn ? "PENDING"
                : isRejectedReturn ? "DITOLAK"
                : "BORROWED";
        String statusClr = isReturned ? "#2ed573"
                : isPendingReturn ? "#ffd32a"
                : isRejectedReturn ? "#ff5252"
                : "#ff9f43";
        String statusBg = isReturned ? "rgba(46,213,115,0.15)"
                : isPendingReturn ? "rgba(255,211,42,0.15)"
                : isRejectedReturn ? "rgba(255,82,82,0.15)"
                : "rgba(255,159,67,0.15)";

        Div card = new Div();
        card.getStyle()
                .set("background", "white").set("border-radius", "12px")
                .set("padding", "12px").set("box-shadow", "0 2px 6px rgba(0,0,0,0.06)")
                .set("display", "flex").set("gap", "12px").set("align-items", "flex-start");

        // Thumb
        Div thumb = new Div();
        thumb.getStyle()
                .set("width", "60px").set("height", "60px").set("flex-shrink", "0")
                .set("border-radius", "10px").set("overflow", "hidden")
                .set("background", "#e8f0ea").set("display", "flex")
                .set("align-items", "center").set("justify-content", "center");
        if (b != null && b.getFotoBarang() != null && !b.getFotoBarang().isBlank()) {
            Image img = new Image("images/" + b.getFotoBarang().trim(), b.getNamaBarang());
            img.getStyle().set("width", "100%").set("height", "100%").set("object-fit", "cover");
            thumb.add(img);
        }

        // Meta
        Div meta = new Div();
        meta.getStyle().set("flex", "1").set("min-width", "0");

        // Name + status badge row
        Div nameRow = new Div();
        nameRow.getStyle().set("display", "flex").set("align-items", "center")
                .set("justify-content", "space-between").set("margin-bottom", "3px");

        String bName = b != null ? b.getNamaBarang() : "—";
        Span bNameSpan = new Span(bName);
        bNameSpan.getStyle().set("font-size", "14px").set("font-weight", "700")
                .set("color", "#1a2e1a").set("white-space", "nowrap")
                .set("overflow", "hidden").set("text-overflow", "ellipsis")
                .set("max-width", "160px");

        Div sBadge = new Div();
        sBadge.setText(statusTxt);
        sBadge.getStyle()
                .set("background", statusBg).set("color", statusClr)
                .set("font-size", "9px").set("font-weight", "700")
                .set("padding", "2px 8px").set("border-radius", "20px")
                .set("border", "1px solid " + statusClr).set("flex-shrink", "0");

        nameRow.add(bNameSpan, sBadge);
        meta.add(nameRow);

        String kode = b != null && b.getKodeBarang() != null ? b.getKodeBarang()
                : "AST-" + (b != null ? String.format("%03d", b.getId()) : "?");
        Span kodeSpan = new Span(kode);
        kodeSpan.getStyle().set("font-size", "10px").set("color", "#8fb08a")
                .set("font-weight", "600").set("display", "block").set("margin-bottom", "4px");
        meta.add(kodeSpan);

        // Date info
        String pinjamDate = d.getPinjaman().getTglPinjam() != null
                ? d.getPinjaman().getTglPinjam().format(DateTimeFormatter.ofPattern("MMM d, yyyy")) : "—";
        String kembaliDate = d.getTglRencanaKembali() != null
                ? d.getTglRencanaKembali().format(DateTimeFormatter.ofPattern("MMM d, yyyy")) : "—";

        Div dateRow = new Div();
        dateRow.getStyle().set("display", "flex").set("align-items", "center").set("gap", "6px").set("margin-bottom", "3px");
        Div calIco = new Div();
        calIco.getElement().setProperty("innerHTML",
                "<svg width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='#8fb08a' stroke-width='2'><rect x='3' y='4' width='18' height='18' rx='2'/><line x1='16' y1='2' x2='16' y2='6'/><line x1='8' y1='2' x2='8' y2='6'/><line x1='3' y1='10' x2='21' y2='10'/></svg>");
        Span dateSpan = new Span("Borrowed: " + pinjamDate + " · Due: " + kembaliDate);
        dateSpan.getStyle().set("font-size", "10px").set("color", "#6b8a6b");
        dateRow.add(calIco, dateSpan);
        meta.add(dateRow);

        // Location
        String ruanganStr = d.getRuangan() != null ? d.getRuangan().getNamaRuangan() : "—";
        Div locRow = new Div();
        locRow.getStyle().set("display", "flex").set("align-items", "center").set("gap", "6px");
        Div locIco = new Div();
        locIco.getElement().setProperty("innerHTML",
                "<svg width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='#8fb08a' stroke-width='2'><path d='M21 10c0 7-9 13-9 13S3 17 3 10a9 9 0 1 1 18 0z'/><circle cx='12' cy='10' r='3'/></svg>");
        Span locSpan = new Span(ruanganStr);
        locSpan.getStyle().set("font-size", "10px").set("color", "#6b8a6b");
        locRow.add(locIco, locSpan);
        meta.add(locRow);

        // Return & Lapor Rusak buttons (only for active borrowed items)
        if (!isReturned && !isPendingReturn && !isRejectedReturn) {
            Div btnRow = new Div();
            btnRow.getStyle().set("display", "flex").set("gap", "8px").set("margin-top", "10px");

            Button returnBtn = new Button("Kembalikan");
            returnBtn.getStyle()
                    .set("flex", "1").set("height", "36px")
                    .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)")
                    .set("color", "white").set("border", "none").set("border-radius", "8px")
                    .set("font-weight", "700").set("font-size", "12px").set("cursor", "pointer");
            returnBtn.addClickListener(ev -> showReturnForm(d));

            Button laporRusakBtn = new Button("⚠️ Lapor Rusak");
            laporRusakBtn.getStyle()
                    .set("flex", "1").set("height", "36px")
                    .set("background", "white").set("color", "#c62828")
                    .set("border", "1px solid #ef9a9a").set("border-radius", "8px")
                    .set("font-weight", "700").set("font-size", "12px").set("cursor", "pointer");
            laporRusakBtn.addClickListener(ev -> showLaporRusakUserForm(d));

            btnRow.add(returnBtn, laporRusakBtn);
            meta.add(btnRow);
        } else if (isReturned) {
            Div verifiedRow = new Div();
            verifiedRow.getStyle().set("display", "flex").set("align-items", "center").set("gap", "4px").set("margin-top", "8px");
            Div checkIco = new Div();
            checkIco.getElement().setProperty("innerHTML",
                    "<svg width='14' height='14' viewBox='0 0 24 24' fill='none' stroke='#2ed573' stroke-width='2'><polyline points='20 6 9 17 4 12'/></svg>");
            Span verTxt = new Span("Verified & Returned");
            verTxt.getStyle().set("font-size", "11px").set("color", "#2ed573").set("font-weight", "600");
            verifiedRow.add(checkIco, verTxt);
            meta.add(verifiedRow);
        } else if (isRejectedReturn) {
            Div rejectBox = new Div();
            rejectBox.getStyle()
                    .set("background", "#ffebee").set("border", "1px solid #ef9a9a")
                    .set("border-radius", "10px").set("padding", "10px 12px")
                    .set("margin-top", "10px").set("display", "flex")
                    .set("flex-direction", "column").set("gap", "6px");

            Span rHeader = new Span("⚠️ Pengembalian Ditolak Admin!");
            rHeader.getStyle().set("font-size", "12px").set("font-weight", "700").set("color", "#c62828");

            String noteAdmin = pObj != null && pObj.getCatatanAdmin() != null && !pObj.getCatatanAdmin().isBlank()
                    ? pObj.getCatatanAdmin()
                    : "Tidak ada catatan spesifik dari admin.";
            Span rNote = new Span("Alasan Admin: " + noteAdmin);
            rNote.getStyle().set("font-size", "11px").set("color", "#b71c1c");

            Button reSubmitBtn = new Button("🔄 Ajukan Ulang Pengembalian");
            reSubmitBtn.getStyle()
                    .set("background", "linear-gradient(135deg,#e07a2a,#b35c17)")
                    .set("color", "white").set("border", "none").set("border-radius", "8px")
                    .set("font-size", "11px").set("font-weight", "700")
                    .set("height", "34px").set("cursor", "pointer").set("margin-top", "4px");
            reSubmitBtn.addClickListener(ev -> showReturnForm(d));

            rejectBox.add(rHeader, rNote, reSubmitBtn);
            meta.add(rejectBox);
        } else {
            Span pendingTxt = new Span("Waiting for admin verification");
            pendingTxt.getStyle().set("font-size", "11px").set("color", "#8fb08a")
                    .set("display", "block").set("margin-top", "8px").set("font-style", "italic");
            meta.add(pendingTxt);
        }

        card.add(thumb, meta);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    // TAB 4 – RETURN LIST / RETURN FORM
    // ════════════════════════════════════════════════════════════════════════

    private void showReturnList() {
        contentArea.add(buildTopBar("PROPERTY"));

        Div page = new Div();
        page.getStyle()
                .set("flex", "1")
                .set("background", "#f5f7f5")
                .set("overflow-y", "auto")
                .set("padding", "16px");

        Span title = new Span("Pengembalian");
        title.getStyle().set("font-size", "18px").set("font-weight", "800").set("color", "#1a2e1a").set("display", "block");
        Span sub = new Span("Pilih barang yang ingin dikembalikan");
        sub.getStyle().set("font-size", "11px").set("color", "#6b8a6b").set("display", "block").set("margin-bottom", "14px");
        page.add(title, sub);

        List<PinjamanDetail> active = pinjamanService.getActiveDetailsByUser(currentUser);
        if (active.isEmpty()) {
            Div empty = new Div();
            empty.getStyle().set("text-align", "center").set("padding", "48px 16px").set("color", "#8fb08a");
            Div emptyIco = new Div();
            emptyIco.getElement().setProperty("innerHTML",
                    "<svg width='48' height='48' viewBox='0 0 24 24' fill='none' stroke='#8fb08a' stroke-width='1.5'>"
                    + IC_BOX + "</svg>");
            emptyIco.getStyle().set("display", "flex").set("justify-content", "center").set("margin-bottom", "12px");
            Span emptyTxt = new Span("Tidak ada barang yang perlu dikembalikan saat ini.");
            emptyTxt.getStyle().set("font-size", "13px");
            empty.add(emptyIco, emptyTxt);
            page.add(empty);
        } else {
            for (PinjamanDetail d : active) {
                Div card = buildMyItemCard(d);
                page.add(card);
            }
        }
        contentArea.add(page);
    }

    private void showReturnForm(PinjamanDetail detail) {
        contentArea.removeAll();
        activeTab = "return";
        updateNavActive();
        contentArea.add(buildTopBar("PROPERTY"));

        Div page = new Div();
        page.getStyle()
                .set("flex", "1")
                .set("background", "#f5f7f5")
                .set("overflow-y", "auto")
                .set("padding", "16px");

        // Back + header
        Div ph = new Div();
        ph.getStyle().set("display", "flex").set("align-items", "center").set("gap", "10px").set("margin-bottom", "16px");
        Div backBtn = new Div();
        backBtn.getElement().setProperty("innerHTML",
                "<svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='#1a2e1a' stroke-width='2'>"
                + "<polyline points='15 18 9 12 15 6'/></svg>");
        backBtn.getStyle().set("cursor", "pointer");
        backBtn.addClickListener(e -> switchTab("myitems"));
        Span phTitle = new Span("Return Item");
        phTitle.getStyle().set("font-size", "18px").set("font-weight", "800").set("color", "#1a2e1a");
        Span phSub = new Span("Upload foto bukti bahwa barang telah dikembalikan ke lokasi aslinya.");
        phSub.getStyle().set("font-size", "11px").set("color", "#6b8a6b").set("display", "block");
        Div phTxt = new Div();
        phTxt.add(phTitle, phSub);
        ph.add(backBtn, phTxt);
        page.add(ph);

        // Item info card
        Barang b = detail.getBarang();
        Div itemCard = new Div();
        itemCard.addClassName("borrow-card");
        itemCard.getStyle().set("display", "flex").set("gap", "12px").set("align-items", "center");

        Div thumb = new Div();
        thumb.getStyle()
                .set("width", "64px").set("height", "64px").set("flex-shrink", "0")
                .set("border-radius", "10px").set("overflow", "hidden")
                .set("background", "#e8f0ea");
        if (b != null && b.getFotoBarang() != null && !b.getFotoBarang().isBlank()) {
            Image img = new Image("images/" + b.getFotoBarang().trim(), b.getNamaBarang());
            img.getStyle().set("width", "100%").set("height", "100%").set("object-fit", "cover");
            thumb.add(img);
        }

        Div meta = new Div();
        meta.getStyle().set("flex", "1");
        Span bName = new Span(b != null ? b.getNamaBarang() : "—");
        bName.getStyle().set("font-size", "14px").set("font-weight", "700").set("color", "#1a2e1a").set("display", "block");
        Div avBadge = new Div();
        avBadge.setText("AVAILABLE");
        avBadge.getStyle()
                .set("display", "inline-block").set("background", "rgba(46,213,115,0.15)")
                .set("color", "#2ed573").set("border", "1px solid #2ed573")
                .set("font-size", "9px").set("font-weight", "700")
                .set("padding", "2px 8px").set("border-radius", "20px");
        String ruanganStr = detail.getRuangan() != null ? detail.getRuangan().getNamaRuangan() : "—";
        Span locSpan = new Span("📍 " + ruanganStr);
        locSpan.getStyle().set("font-size", "11px").set("color", "#6b8a6b").set("display", "block").set("margin-top", "3px");
        meta.add(bName, avBadge, locSpan);
        itemCard.add(thumb, meta);
        page.add(itemCard);

        // Upload foto bukti penempatan — real file picker
        String[] returnFotoName = {null};
        Div uploadCard = new Div();
        uploadCard.addClassName("borrow-card");
        Span ulLabel = new Span("Upload Foto Bukti Penempatan Barang *");
        ulLabel.getStyle().set("font-size", "13px").set("font-weight", "600")
                .set("color", "#1a2e1a").set("display", "block").set("margin-bottom", "8px");
        MemoryBuffer returnBuffer = new MemoryBuffer();
        Upload returnUpload = new Upload(returnBuffer);
        returnUpload.setAcceptedFileTypes("image/*");
        returnUpload.setMaxFiles(1);
        returnUpload.setMaxFileSize(10 * 1024 * 1024);
        returnUpload.setUploadButton(buildUserUploadButton("📷  Pilih Foto Penempatan"));
        returnUpload.setDropLabel(new Span("JPG, PNG, or HEIC · Max 10MB"));
        returnUpload.setWidthFull();
        returnUpload.addSucceededListener(ev -> returnFotoName[0] = ev.getFileName());
        uploadCard.add(ulLabel, returnUpload);
        page.add(uploadCard);

        // Catatan pengembalian (opsional)
        TextArea catatan = new TextArea("Catatan Pengembalian (Opsional)");
        catatan.setPlaceholder("Catatan kondisi barang saat dikembalikan (opsional)...");
        catatan.setWidthFull();
        catatan.setHeight("90px");
        Div catatanCard = new Div();
        catatanCard.addClassName("borrow-card");
        catatanCard.add(catatan);
        page.add(catatanCard);

        // Inline validation error banner
        Div errBanner = new Div();
        errBanner.getStyle()
                .set("background", "#ffebee")
                .set("color", "#c62828")
                .set("border", "1px solid #ef9a9a")
                .set("border-radius", "10px")
                .set("padding", "12px 14px")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("display", "none")
                .set("margin-bottom", "10px")
                .set("align-items", "center")
                .set("gap", "8px");
        page.add(errBanner);

        // Submit button
        Button kirimBtn = new Button("Kirim Pengembalian");
        kirimBtn.setWidthFull();
        kirimBtn.getStyle()
                .set("height", "50px").set("border-radius", "12px")
                .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)")
                .set("color", "white").set("font-weight", "700")
                .set("font-size", "14px").set("border", "none")
                .set("cursor", "pointer").set("margin-top", "4px");

        kirimBtn.addClickListener(e -> {
            errBanner.getStyle().set("display", "none");
            if (returnFotoName[0] == null) {
                errBanner.setText("⚠️ Harap upload / ambil Foto Bukti Penempatan Barang terlebih dahulu!");
                errBanner.getStyle().set("display", "flex");
                err("Upload foto bukti penempatan terlebih dahulu!");
                return;
            }

            String savedFoto = null;
            try {
                savedFoto = property24.util.FileUploadHelper.saveImage(returnBuffer, returnFotoName[0]);
            } catch (Exception ex) {
                errBanner.setText("⚠️ Gagal menyimpan foto pengembalian: " + ex.getMessage());
                errBanner.getStyle().set("display", "flex");
                err("Gagal menyimpan foto pengembalian!");
                return;
            }

            try {
                pinjamanService.submitPengembalian(detail, catatan.getValue(), savedFoto);
                ok("Pengembalian berhasil diajukan! Menunggu verifikasi admin.");
                switchTab("myitems");
            } catch (Exception ex) {
                errBanner.setText("⚠️ Gagal mengajukan pengembalian: " + ex.getMessage());
                errBanner.getStyle().set("display", "flex");
                err("Gagal mengajukan pengembalian!");
            }
        });
        page.add(kirimBtn);

        // Petunjuk box
        if (detail.getRuangan() != null) {
            Div infoBox = new Div();
            infoBox.getStyle()
                    .set("background", "#e8f5e8").set("border-radius", "10px")
                    .set("padding", "12px 14px").set("margin-top", "12px");
            Span infoTitle = new Span("Petunjuk Pengembalian: " + ruanganStr);
            infoTitle.getStyle().set("font-size", "12px").set("font-weight", "700")
                    .set("color", "#2d6a2d").set("display", "block").set("margin-bottom", "4px");
            Span infoTxt = new Span("Foto harus menunjukkan barang di lokasi yang benar. Pastikan barang dikembalikan ke lokasi semula.");
            infoTxt.getStyle().set("font-size", "11px").set("color", "#3a6a3a");
            infoBox.add(infoTitle, infoTxt);
            page.add(infoBox);
        }

        contentArea.add(page);
    }

    private void showLaporRusakUserForm(PinjamanDetail detail) {
        contentArea.removeAll();
        activeTab = "return";
        updateNavActive();
        contentArea.add(buildTopBar("PROPERTY"));

        Div page = new Div();
        page.getStyle()
                .set("flex", "1")
                .set("background", "#f5f7f5")
                .set("overflow-y", "auto")
                .set("padding", "16px");

        // Back + header
        Div ph = new Div();
        ph.getStyle().set("display", "flex").set("align-items", "center").set("gap", "10px").set("margin-bottom", "16px");
        Div backBtn = new Div();
        backBtn.getElement().setProperty("innerHTML",
                "<svg width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='#1a2e1a' stroke-width='2'>"
                + "<polyline points='15 18 9 12 15 6'/></svg>");
        backBtn.getStyle().set("cursor", "pointer");
        backBtn.addClickListener(e -> switchTab("myitems"));
        Span phTitle = new Span("⚠️ Laporkan Kerusakan Barang");
        phTitle.getStyle().set("font-size", "18px").set("font-weight", "800").set("color", "#c62828");
        Span phSub = new Span("Barang yang Anda pinjam mengalami kerusakan? Laporkan dan ajukan pengembalian.");
        phSub.getStyle().set("font-size", "11px").set("color", "#6b8a6b").set("display", "block");
        Div phTxt = new Div();
        phTxt.add(phTitle, phSub);
        ph.add(backBtn, phTxt);
        page.add(ph);

        // Item info card
        Barang b = detail.getBarang();
        Div itemCard = new Div();
        itemCard.addClassName("borrow-card");
        itemCard.getStyle().set("display", "flex").set("gap", "12px").set("align-items", "center");

        Div thumb = new Div();
        thumb.getStyle()
                .set("width", "64px").set("height", "64px").set("flex-shrink", "0")
                .set("border-radius", "10px").set("overflow", "hidden")
                .set("background", "#e8f0ea");
        if (b != null && b.getFotoBarang() != null && !b.getFotoBarang().isBlank()) {
            Image img = new Image("images/" + b.getFotoBarang().trim(), b.getNamaBarang());
            img.getStyle().set("width", "100%").set("height", "100%").set("object-fit", "cover");
            thumb.add(img);
        }

        Div meta = new Div();
        meta.getStyle().set("flex", "1");
        Span bName = new Span(b != null ? b.getNamaBarang() : "—");
        bName.getStyle().set("font-size", "14px").set("font-weight", "700").set("color", "#1a2e1a").set("display", "block");
        Div avBadge = new Div();
        avBadge.setText("MEMBUTUHKAN VERIFIKASI ADMIN");
        avBadge.getStyle()
                .set("display", "inline-block").set("background", "#fff3e0")
                .set("color", "#e65100").set("border", "1px solid #ffe0b2")
                .set("font-size", "9px").set("font-weight", "700")
                .set("padding", "2px 8px").set("border-radius", "20px");
        String ruanganStr = detail.getRuangan() != null ? detail.getRuangan().getNamaRuangan() : "—";
        Span locSpan = new Span("📍 " + ruanganStr);
        locSpan.getStyle().set("font-size", "11px").set("color", "#6b8a6b").set("display", "block").set("margin-top", "3px");
        meta.add(bName, avBadge, locSpan);
        itemCard.add(thumb, meta);
        page.add(itemCard);

        // Upload foto bukti kerusakan
        String[] fotoNameArr = {null};
        Div uploadCard = new Div();
        uploadCard.addClassName("borrow-card");
        Span ulLabel = new Span("Upload Foto Bukti Kerusakan *");
        ulLabel.getStyle().set("font-size", "13px").set("font-weight", "700")
                .set("color", "#1a2e1a").set("display", "block").set("margin-bottom", "8px");
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/*");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(10 * 1024 * 1024);
        upload.setUploadButton(buildUserUploadButton("📷  Pilih Foto Kerusakan"));
        upload.setDropLabel(new Span("JPG, PNG, HEIC · Max 10MB"));
        upload.setWidthFull();
        upload.addSucceededListener(ev -> fotoNameArr[0] = ev.getFileName());
        uploadCard.add(ulLabel, upload);
        page.add(uploadCard);

        // Deskripsi kerusakan (wajib)
        Div catatanCard = new Div();
        catatanCard.addClassName("borrow-card");
        TextArea catatanField = new TextArea("Deskripsi Kerusakan *");
        catatanField.setPlaceholder("Jelaskan kerusakan yang terjadi (misal: layar pecah, mati total, tombol lepas)...");
        catatanField.setWidthFull();
        catatanField.setMinHeight("90px");
        catatanCard.add(catatanField);
        page.add(catatanCard);

        // Submit button
        Button kirimBtn = new Button("⚠️ Kirim Laporan Kerusakan & Return");
        kirimBtn.setWidthFull();
        kirimBtn.getStyle()
                .set("height", "50px").set("border-radius", "12px")
                .set("background", "linear-gradient(135deg,#e02a2a,#b31717)")
                .set("color", "white").set("font-weight", "700")
                .set("font-size", "14px").set("border", "none")
                .set("cursor", "pointer").set("margin-top", "6px");

        kirimBtn.addClickListener(e -> {
            if (fotoNameArr[0] == null) {
                err("Upload foto bukti kerusakan terlebih dahulu!");
                return;
            }
            if (catatanField.getValue().isBlank()) {
                err("Isi deskripsi kerusakan terlebih dahulu!");
                return;
            }

            String savedFoto = null;
            try {
                savedFoto = property24.util.FileUploadHelper.saveImage(buffer, fotoNameArr[0]);
            } catch (Exception ex) {
                err("Gagal menyimpan foto bukti kerusakan!");
                return;
            }

            try {
                String fullNote = "[LAPORAN KERUSAKAN USER] " + catatanField.getValue().trim();
                pinjamanService.submitPengembalian(detail, fullNote, savedFoto);
                ok("Laporan kerusakan berhasil dikirim. Menunggu verifikasi admin.");
                switchTab("myitems");
            } catch (Exception ex) {
                err("Gagal mengirim laporan kerusakan: " + ex.getMessage());
            }
        });
        page.add(kirimBtn);

        contentArea.add(page);
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private Div chip(String label, String catKey, boolean active) {
        Div c = new Div();
        c.getElement().setAttribute("data-cat", catKey);
        c.setText(label);
        styleChip(c, active);
        c.getStyle().set("cursor", "pointer").set("white-space", "nowrap")
                .set("flex-shrink", "0").set("user-select", "none");
        return c;
    }

    private void styleChip(Div c, boolean active) {
        c.getStyle()
                .set("padding", "6px 14px").set("border-radius", "20px")
                .set("font-size", "13px").set("font-weight", active ? "700" : "500")
                .set("background", active ? "#4d8f4d" : "rgba(255,255,255,0.12)")
                .set("color", active ? "white" : "#b8c9bf")
                .set("border", active ? "1.5px solid #4d8f4d" : "1.5px solid rgba(184,201,191,0.2)");
    }

    private Span itemCountBadge(int count) {
        Span s = new Span(count + " items");
        s.getStyle()
                .set("background", "#1e2e25").set("color", "#8fb08a")
                .set("font-size", "11px").set("font-weight", "600")
                .set("padding", "3px 10px").set("border-radius", "20px");
        return s;
    }

    private Div detailRow(String label, String value) {
        Div row = new Div();
        row.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "2px")
                .set("padding", "8px 0")
                .set("border-bottom", "1px solid rgba(255,255,255,0.05)");
        Span l = new Span(label);
        l.getStyle()
                .set("color", "rgba(184,201,191,0.55)")
                .set("font-size", "10px")
                .set("font-weight", "600")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.6px");
        Span v = new Span(value);
        v.getStyle()
                .set("color", "white")
                .set("font-size", "13px")
                .set("font-weight", "600");
        row.add(l, v);
        return row;
    }

    /** Detail row styled for white/light card backgrounds */
    private Div lightDetailRow(String label, String value) {
        Div row = new Div();
        row.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "2px")
                .set("padding", "8px 0")
                .set("border-bottom", "1px solid #f0f4f0");
        Span l = new Span(label);
        l.getStyle()
                .set("color", "#8aaa8a")
                .set("font-size", "10px")
                .set("font-weight", "600")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.6px")
                .set("font-family", "'Inter', sans-serif");
        Span v = new Span(value != null && !value.isBlank() ? value : "—");
        v.getStyle()
                .set("color", "#1a2e1a")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("font-family", "'Inter', sans-serif");
        row.add(l, v);
        return row;
    }

    private Div formSectionTitle(String text) {
        Div d = new Div();
        d.setText(text);
        d.getStyle()
                .set("font-size", "14px").set("font-weight", "700")
                .set("color", "#1a2e1a").set("margin-bottom", "10px");
        return d;
    }

    private Button buildUserUploadButton(String label) {
        Button btn = new Button(label);
        btn.getStyle()
                .set("background", "#e8f5e8")
                .set("color", "#4d8f4d")
                .set("border", "1.5px dashed rgba(77,143,77,0.4)")
                .set("border-radius", "10px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "600")
                .set("font-size", "14px")
                .set("width", "100%")
                .set("height", "52px")
                .set("cursor", "pointer");
        return btn;
    }

    private void ok(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void err(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void info(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    private void injectCss() {
        String css =
            "body,html{margin:0;padding:0;}" +
            ".asset-grid-2col{display:grid;grid-template-columns:repeat(2,1fr);gap:10px;}" +
            ".asset-card-u:hover{transform:translateY(-2px);box-shadow:0 8px 20px rgba(0,0,0,0.12)!important;}" +
            /* borrow-card: force dark text for ALL Vaadin fields inside white cards */
            ".borrow-card{background:white;border-radius:12px;padding:14px;box-shadow:0 2px 6px rgba(0,0,0,0.06);margin-bottom:10px;" +
            "--lumo-body-text-color:#1a2e1a;--lumo-secondary-text-color:#4a6a4a;" +
            "--lumo-primary-color:#4d8f4d;--lumo-primary-text-color:#4d8f4d;" +
            "--lumo-base-color:#fff;--lumo-contrast-5pct:rgba(0,0,0,0.04);" +
            "--lumo-contrast-10pct:rgba(0,0,0,0.1);" +
            "--vaadin-input-field-value-color:#1a2e1a;" +
            "--vaadin-input-field-background:#f8faf8;" +
            "--vaadin-input-field-border-color:rgba(77,143,77,0.3);" +
            "--vaadin-input-field-focused-border-color:#4d8f4d;" +
            "color:#1a2e1a;}" +
            ".borrow-card input, .borrow-card textarea{color:#1a2e1a!important;-webkit-text-fill-color:#1a2e1a!important;background-color:transparent!important;}" +
            ".borrow-card input:-webkit-autofill, .borrow-card input:-webkit-autofill:hover, .borrow-card input:-webkit-autofill:focus, .borrow-card input:-webkit-autofill:active, .borrow-card textarea:-webkit-autofill{" +
            "  -webkit-box-shadow:0 0 0 40px #f8faf8 inset!important;" +
            "  -webkit-text-fill-color:#1a2e1a!important;" +
            "  caret-color:#1a2e1a!important;" +
            "}" +
            ".borrow-card vaadin-text-field::part(value)," +
            ".borrow-card vaadin-text-area::part(value)," +
            ".borrow-card vaadin-combo-box::part(value)," +
            ".borrow-card vaadin-date-picker::part(value){color:#1a2e1a!important;-webkit-text-fill-color:#1a2e1a!important;}" +
            ".borrow-card vaadin-text-field::part(input-field)," +
            ".borrow-card vaadin-text-area::part(input-field)," +
            ".borrow-card vaadin-combo-box::part(input-field)," +
            ".borrow-card vaadin-date-picker::part(input-field){background:#f8faf8!important;border:1px solid rgba(77,143,77,0.3)!important;min-width:0!important;font-size:12px!important;padding:0 6px!important;}" +
            ".borrow-card vaadin-date-picker{min-width:0!important;width:100%!important;max-width:100%!important;}" +
            ".borrow-card vaadin-date-picker::part(value){font-size:12px!important;}" +
            ".borrow-card vaadin-text-field label,.borrow-card vaadin-text-area label," +
            ".borrow-card vaadin-date-picker label,.borrow-card vaadin-combo-box label{color:#4a6a4a!important;font-weight:600;}" +
            /* search bar on dark top panel */
            ".user-search{--lumo-body-text-color:#1a2e1a;}" +
            "vaadin-text-field.user-search::part(input-field){background:rgba(255,255,255,0.12)!important;" +
            "border-radius:22px!important;border:1px solid rgba(143,176,138,0.3)!important;}" +
            "vaadin-text-field.user-search::part(value){color:white!important;}" +
            "vaadin-text-field.user-search::part(placeholder){color:rgba(184,201,191,0.5)!important;}";
        getElement().executeJs(
            "if(!document.getElementById('p24-user-css')){" +
            "  const s=document.createElement('style');" +
            "  s.id='p24-user-css';" +
            "  s.textContent=$0;" +
            "  document.head.appendChild(s);" +
            "}", css);
    }

    // ── PROFILE DIALOG ────────────────────────────────────────────────────────
    private void showProfileDialog() {
        Dialog d = new Dialog();
        d.setModal(true);
        d.setWidth("380px");

        VerticalLayout layout = new VerticalLayout();
        layout.getStyle()
                .set("background", "#16281b")
                .set("border-radius", "18px")
                .set("padding", "20px")
                .set("border", "1px solid rgba(143,176,138,0.2)")
                .set("gap", "12px");
        layout.setSpacing(false);
        layout.setPadding(false);

        Div header = new Div();
        header.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("width", "100%")
                .set("margin-bottom", "6px");

        String initials = currentUser.getNamaLengkap() != null && !currentUser.getNamaLengkap().isBlank()
                ? String.valueOf(currentUser.getNamaLengkap().charAt(0)).toUpperCase() : "U";

        Div avCircle = new Div();
        avCircle.getStyle()
                .set("width", "60px").set("height", "60px")
                .set("border-radius", "50%").set("background", "#4d8f4d")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center")
                .set("color", "white").set("font-weight", "800").set("font-size", "24px");
        avCircle.setText(initials);

        Span nameTxt = new Span(currentUser.getNamaLengkap() != null ? currentUser.getNamaLengkap() : currentUser.getUsername());
        nameTxt.getStyle().set("color", "white").set("font-size", "16px").set("font-weight", "700");

        Span roleBadge = new Span(currentUser.getRole() != null ? currentUser.getRole().name().toUpperCase() : "USER");
        roleBadge.getStyle()
                .set("background", "rgba(77,143,77,0.2)").set("color", "#8fb08a")
                .set("font-size", "10px").set("font-weight", "700")
                .set("padding", "3px 10px").set("border-radius", "20px")
                .set("border", "1px solid #4d8f4d");

        header.add(avCircle, nameTxt, roleBadge);
        layout.add(header);

        TextField nameField = new TextField("Nama Lengkap");
        nameField.setValue(currentUser.getNamaLengkap() != null ? currentUser.getNamaLengkap() : "");
        nameField.setWidthFull();
        styleDarkField(nameField);

        TextField emailField = new TextField("Email");
        emailField.setValue(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        emailField.setWidthFull();
        styleDarkField(emailField);

        TextField kelasField = new TextField("Kelas / Unit");
        kelasField.setValue(currentUser.getKelas() != null ? currentUser.getKelas() : "");
        kelasField.setWidthFull();
        styleDarkField(kelasField);

        layout.add(nameField, emailField, kelasField);

        Button saveBtn = new Button("Simpan Profil");
        saveBtn.setWidthFull();
        saveBtn.getStyle()
                .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)")
                .set("color", "white").set("font-weight", "700")
                .set("border", "none").set("border-radius", "10px")
                .set("height", "44px").set("cursor", "pointer").set("margin-top", "8px");

        saveBtn.addClickListener(ev -> {
            currentUser.setNamaLengkap(nameField.getValue().trim());
            currentUser.setEmail(emailField.getValue().trim());
            currentUser.setKelas(kelasField.getValue().trim());
            userService.save(currentUser);
            AuthSession.setCurrentUser(currentUser);
            ok("Profil berhasil diperbarui!");
            d.close();
            switchTab(activeTab);
        });

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setWidthFull();
        logoutBtn.getStyle()
                .set("background", "rgba(224,106,106,0.15)")
                .set("color", "#e06a6a").set("border", "1px solid rgba(224,106,106,0.3)")
                .set("font-weight", "700").set("border-radius", "10px")
                .set("height", "44px").set("cursor", "pointer");

        logoutBtn.addClickListener(ev -> {
            d.close();
            AuthSession.logout();
            UI.getCurrent().navigate("login");
        });

        layout.add(saveBtn, logoutBtn);
        d.add(layout);
        d.open();
    }

    private void styleDarkField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("--lumo-primary-text-color", "#ffffff")
                .set("--lumo-secondary-text-color", "#e2f0e2")
                .set("--lumo-body-text-color", "#ffffff")
                .set("--lumo-contrast-90pct", "#ffffff")
                .set("--lumo-contrast-80pct", "#ffffff")
                .set("--lumo-contrast-70pct", "#e2f0e2")
                .set("--lumo-contrast-60pct", "#c2e0c2")
                .set("--lumo-contrast-50pct", "#a2d0a2")
                .set("--vaadin-input-field-label-color", "#e2f0e2")
                .set("--vaadin-input-field-label-font-weight", "700")
                .set("--vaadin-input-field-value-color", "#ffffff")
                .set("--vaadin-input-field-placeholder-color", "rgba(255,255,255,0.65)")
                .set("--vaadin-input-field-background", "rgba(255,255,255,0.12)")
                .set("--vaadin-input-field-border-color", "rgba(143,176,138,0.5)");
    }

    private void showBookingModal(Barang b) {
        Dialog d = new Dialog();
        d.setModal(true);
        d.setWidth("min(440px, 92vw)");
        d.getElement().getStyle().set("--lumo-overlay-border-radius", "20px");

        VerticalLayout layout = new VerticalLayout();
        layout.getStyle()
                .set("background", "#ffffff")
                .set("border-radius", "20px")
                .set("padding", "0")
                .set("gap", "0")
                .set("box-shadow", "0 10px 40px rgba(0,0,0,0.12)");
        layout.setSpacing(false);
        layout.setPadding(false);

        // ── Header bar ──────────────────────────────────────────────────────
        Div headerBar = new Div();
        headerBar.getStyle()
                .set("background", "linear-gradient(135deg,#e07a2a,#b35c17)")
                .set("padding", "18px 22px 16px")
                .set("border-radius", "20px 20px 0 0");

        Span headerTitle = new Span("📋  Ajukan Booking Barang");
        headerTitle.getStyle().set("color", "white").set("font-size", "17px").set("font-weight", "800").set("display", "block");

        Span headerSub = new Span("Reservasi barang untuk diambil di waktu mendatang. Membutuhkan persetujuan admin.");
        headerSub.getStyle().set("color", "rgba(255,255,255,0.9)").set("font-size", "11px").set("margin-top", "3px").set("display", "block");
        headerBar.add(headerTitle, headerSub);

        // ── Barang info banner ───────────────────────────────────────────────
        Div infoBanner = new Div();
        infoBanner.getStyle()
                .set("background", "#f9faf9")
                .set("border-bottom", "1px solid #eef2ee")
                .set("padding", "14px 22px")
                .set("display", "flex").set("align-items", "center").set("gap", "14px");

        Div iconBox = new Div();
        iconBox.getStyle()
                .set("width", "44px").set("height", "44px").set("background", "#eef7ee")
                .set("border-radius", "10px").set("border", "1px solid #c8e6c8")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center")
                .set("flex-shrink", "0");
        iconBox.getElement().setProperty("innerHTML",
                "<svg width='22' height='22' viewBox='0 0 24 24' fill='none' stroke='#2e7d32' stroke-width='2.5'>" +
                "<path d='M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z'/></svg>");

        Div barangInfo = new Div();
        Span namaBarang = new Span(b.getNamaBarang());
        namaBarang.getStyle().set("color", "#1a2e1a").set("font-size", "15px").set("font-weight", "800").set("display", "block");

        String katText = (b.getKategori() != null ? b.getKategori().getNamaKategori() : "—") +
                (b.getRuangan() != null ? "  ·  " + b.getRuangan().getNamaRuangan() : "");
        Span katSpan = new Span(katText);
        katSpan.getStyle().set("color", "#4d8f4d").set("font-size", "12px").set("font-weight", "700");
        barangInfo.add(namaBarang, katSpan);

        infoBanner.add(iconBox, barangInfo);

        // ── Form body ────────────────────────────────────────────────────────
        Div formBody = new Div();
        formBody.getStyle().set("padding", "20px 22px");

        ComboBox<Ruangan> ruanganBox = new ComboBox<>("Ruang Pemakaian *");
        ruanganBox.setItems(barangService.getAllRuangan());
        ruanganBox.setItemLabelGenerator(Ruangan::getNamaRuangan);
        ruanganBox.setWidthFull();
        if (b.getRuangan() != null) {
            ruanganBox.setValue(b.getRuangan());
        }

        DatePicker tglAmbilPicker = new DatePicker("Tanggal Rencana Ambil");
        tglAmbilPicker.setMin(LocalDate.now());
        tglAmbilPicker.setValue(LocalDate.now());
        tglAmbilPicker.setWidthFull();
        tglAmbilPicker.getStyle().set("margin-top", "12px");
        tglAmbilPicker.addValueChangeListener(ev -> {
            if (ev.getValue() != null && ev.getValue().isBefore(LocalDate.now())) {
                tglAmbilPicker.setValue(LocalDate.now());
            }
        });

        TimePicker jamAmbilPicker = new TimePicker("Jam Ambil");
        jamAmbilPicker.setValue(LocalTime.of(8, 0));
        jamAmbilPicker.setStep(java.time.Duration.ofMinutes(30));
        jamAmbilPicker.setWidthFull();
        jamAmbilPicker.getStyle().set("margin-bottom", "14px");

        TextArea catatanArea = new TextArea("Catatan / Keperluan *");
        catatanArea.setPlaceholder("Tuliskan keperluan atau alasan peminjaman...");
        catatanArea.setMinHeight("80px");
        catatanArea.setWidthFull();

        // Info note
        Div noteBox = new Div();
        noteBox.getStyle()
                .set("background", "#fff8e1").set("border", "1px solid #ffe082")
                .set("border-radius", "10px").set("padding", "10px 14px").set("margin-top", "14px")
                .set("display", "flex").set("gap", "8px").set("align-items", "flex-start");
        Span noteIcon = new Span("ℹ️");
        noteIcon.getStyle().set("flex-shrink", "0").set("font-size", "14px");
        Span noteText = new Span("Booking Anda akan mengunci barang sementara agar tidak dipinjam pengguna lain, dan perlu disetujui oleh admin terlebih dahulu.");
        noteText.getStyle().set("color", "#b78103").set("font-size", "12px").set("line-height", "1.5").set("font-weight", "500");
        noteBox.add(noteIcon, noteText);

        formBody.add(ruanganBox, tglAmbilPicker, jamAmbilPicker, catatanArea, noteBox);

        // Footer
        Div footer = new Div();
        footer.getStyle()
                .set("display", "flex").set("gap", "10px").set("padding", "0 22px 20px")
                .set("border-top", "1px solid #eef2ee").set("padding-top", "16px");

        Button cancelBtn = new Button("Batal", ev -> d.close());
        cancelBtn.getStyle()
                .set("flex", "1").set("background", "#f0f4f0")
                .set("color", "#4a6a4a").set("border", "none")
                .set("border-radius", "10px").set("height", "44px").set("cursor", "pointer")
                .set("font-weight", "600");

        Button submitBtn = new Button("🔖  Ajukan Booking", ev -> {
            try {
                if (ruanganBox.getValue() == null) {
                    err("Pilih ruang pemakaian terlebih dahulu!");
                    return;
                }
                LocalDate dateVal = tglAmbilPicker.getValue();
                LocalTime timeVal = jamAmbilPicker.getValue() != null ? jamAmbilPicker.getValue() : LocalTime.of(8, 0);
                if (dateVal == null) {
                    err("Pilih tanggal rencana ambil!");
                    return;
                }
                if (catatanArea.getValue() == null || catatanArea.getValue().isBlank()) {
                    err("Tuliskan keperluan / catatan booking!");
                    return;
                }
                LocalDateTime tglJamAmbil = LocalDateTime.of(dateVal, timeVal);
                bookingService.createBooking(currentUser, b, ruanganBox.getValue(), tglJamAmbil, catatanArea.getValue());
                ok("✅  Booking berhasil diajukan! Menunggu konfirmasi admin.");
                d.close();
                switchTab("mybooking");
            } catch (Exception ex) {
                err(ex.getMessage());
            }
        });
        submitBtn.getStyle()
                .set("flex", "2").set("border", "none").set("border-radius", "10px")
                .set("height", "44px").set("cursor", "pointer").set("font-weight", "800")
                .set("font-size", "13px")
                .set("background", "linear-gradient(135deg,#e07a2a,#b35c17)").set("color", "white");

        footer.add(cancelBtn, submitBtn);
        layout.add(headerBar, infoBanner, formBody, footer);
        d.add(layout);
        d.open();
    }

    private void showMyBookings() {
        contentArea.add(buildTopBar("PROPERTY"));

        Div page = new Div();
        page.getStyle()
                .set("flex", "1")
                .set("background", "#f5f7f5")
                .set("overflow-y", "auto")
                .set("padding", "16px 16px 80px");

        // Header
        Div header = new Div();
        header.getStyle().set("margin-bottom", "16px");
        Span heading = new Span("Booking Saya");
        heading.getStyle().set("font-size", "20px").set("font-weight", "800").set("color", "#1a2e1a").set("display", "block");
        Span subHeading = new Span("Daftar reservasi barang aktif dan riwayat booking Anda.");
        subHeading.getStyle().set("font-size", "12px").set("color", "#6b8a6b").set("display", "block").set("margin-top", "2px");
        header.add(heading, subHeading);
        page.add(header);

        List<Booking> list = bookingService.getBookingsByUser(currentUser);

        if (list.isEmpty()) {
            Div emptyBox = new Div();
            emptyBox.getStyle()
                    .set("background", "white").set("border-radius", "16px")
                    .set("padding", "48px 20px").set("text-align", "center")
                    .set("box-shadow", "0 2px 10px rgba(0,0,0,0.04)")
                    .set("border", "1px solid rgba(0,0,0,0.06)");
            Span emptyIcon = new Span("🔖");
            emptyIcon.getStyle().set("font-size", "36px").set("display", "block").set("margin-bottom", "10px");
            Span emptyTxt = new Span("Belum ada riwayat booking.");
            emptyTxt.getStyle().set("color", "#1a2e1a").set("font-size", "14px").set("font-weight", "700").set("display", "block");
            Span emptySub = new Span("Pilih barang dari Dashboard lalu klik tombol 'Booking' untuk mereservasi barang.");
            emptySub.getStyle().set("color", "#6b8a6b").set("font-size", "12px").set("margin-top", "4px").set("display", "block");
            emptyBox.add(emptyIcon, emptyTxt, emptySub);
            page.add(emptyBox);
        } else {
            DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

            for (Booking bk : list) {
                Div card = new Div();
                card.getStyle()
                        .set("background", "#ffffff")
                        .set("border-radius", "16px")
                        .set("padding", "18px 20px")
                        .set("margin-bottom", "14px")
                        .set("box-shadow", "0 2px 10px rgba(0,0,0,0.04)")
                        .set("border", "1px solid rgba(0,0,0,0.06)");

                Div topRow = new Div();
                topRow.getStyle().set("display", "flex").set("justify-content", "space-between")
                        .set("align-items", "center").set("margin-bottom", "12px");

                String namaB = bk.getBarang() != null ? bk.getBarang().getNamaBarang() : "Barang # " + bk.getId();
                Span itemName = new Span(namaB);
                itemName.getStyle().set("color", "#1a2e1a").set("font-size", "15px").set("font-weight", "800");

                String stText;
                String stBg;
                String stColor;
                String stBorder;

                switch (bk.getStatus()) {
                    case menunggu_persetujuan -> {
                        stText = "⏳ MENUNGGU ACC";
                        stBg = "#fff3e0"; stColor = "#e65100"; stBorder = "#ffe0b2";
                    }
                    case disetujui -> {
                        stText = "✅ DISETUJUI (SIAP DIAMBIL)";
                        stBg = "#e8f5e9"; stColor = "#2e7d32"; stBorder = "#a5d6a7";
                    }
                    case diambil -> {
                        stText = "📦 SUDAH DIAMBIL";
                        stBg = "#e3f2fd"; stColor = "#1565c0"; stBorder = "#90caf9";
                    }
                    case ditolak -> {
                        stText = "❌ DITOLAK";
                        stBg = "#ffebee"; stColor = "#c62828"; stBorder = "#ef9a9a";
                    }
                    case dibatalkan -> {
                        stText = "🚫 DIBATALKAN";
                        stBg = "#f5f5f5"; stColor = "#616161"; stBorder = "#e0e0e0";
                    }
                    case kedaluwarsa -> {
                        stText = "⏰ KEDALUWARSA";
                        stBg = "#ffebee"; stColor = "#c62828"; stBorder = "#ef9a9a";
                    }
                    default -> {
                        stText = "—"; stBg = "#f5f5f5"; stColor = "#616161"; stBorder = "#e0e0e0";
                    }
                }

                Span stBadge = new Span(stText);
                stBadge.getStyle()
                        .set("background", stBg).set("color", stColor)
                        .set("font-size", "10px").set("font-weight", "700")
                        .set("padding", "4px 10px").set("border-radius", "12px")
                        .set("border", "1px solid " + stBorder);

                topRow.add(itemName, stBadge);
                card.add(topRow);

                String tglB = bk.getTglBooking() != null ? bk.getTglBooking().format(dtFmt) : "—";
                String tglR = bk.getTglRencanaAmbil() != null ? bk.getTglRencanaAmbil().format(dtFmt) : "—";
                String tglExp = bk.getBatasWaktu() != null ? bk.getBatasWaktu().format(dtFmt) : "—";
                String ru = bk.getRuangan() != null ? bk.getRuangan().getNamaRuangan() :
                        (bk.getBarang() != null && bk.getBarang().getRuangan() != null ? bk.getBarang().getRuangan().getNamaRuangan() : "—");

                Div infoGrid = new Div();
                infoGrid.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "0");
                infoGrid.add(lightDetailRow("Ruang Pemakaian", ru));
                infoGrid.add(lightDetailRow("Waktu Booking", tglB));
                infoGrid.add(lightDetailRow("Rencana Ambil", tglR));
                infoGrid.add(lightDetailRow("Batas Waktu (Expiry)", tglExp));
                if (bk.getCatatan() != null && !bk.getCatatan().isBlank()) {
                    infoGrid.add(lightDetailRow("Catatan", bk.getCatatan()));
                }
                card.add(infoGrid);

                if (bk.getStatus() == Booking.BookingStatus.menunggu_persetujuan || bk.getStatus() == Booking.BookingStatus.disetujui) {
                    Hr divider = new Hr();
                    divider.getStyle().set("border-color", "#eef2ee").set("margin", "12px 0 8px");
                    card.add(divider);

                    Div actionRow = new Div();
                    actionRow.getStyle().set("display", "flex").set("gap", "10px").set("justify-content", "flex-end");

                    Button cancelBtn = new Button("Batalkan Booking", ev -> {
                        Dialog confirmDialog = new Dialog();
                        confirmDialog.setModal(true);
                        confirmDialog.setWidth("360px");

                        VerticalLayout dLayout = new VerticalLayout();
                        dLayout.getStyle().set("background", "#ffffff").set("border-radius", "16px").set("padding", "20px");

                        Span confirmTitle = new Span("Batalkan Booking?");
                        confirmTitle.getStyle().set("color", "#c62828").set("font-size", "16px").set("font-weight", "bold");

                        Span msg = new Span("Apakah Anda yakin ingin membatalkan booking barang ini?");
                        msg.getStyle().set("color", "#4a6a4a").set("font-size", "13px");

                        Div footer = new Div();
                        footer.getStyle().set("display", "flex").set("gap", "10px").set("margin-top", "16px").set("width", "100%");

                        Button noBtn = new Button("Tidak", e -> confirmDialog.close());
                        noBtn.getStyle().set("flex", "1").set("background", "#f0f4f0").set("color", "#4a6a4a").set("border-radius", "8px");

                        Button yesBtn = new Button("Ya, Batalkan", e -> {
                            try {
                                bookingService.cancelBooking(bk, currentUser);
                                ok("Booking berhasil dibatalkan.");
                                confirmDialog.close();
                                switchTab("mybooking");
                            } catch (Exception ex) {
                                err(ex.getMessage());
                            }
                        });
                        yesBtn.getStyle().set("flex", "1").set("background", "#c62828").set("color", "white").set("border", "none").set("border-radius", "8px");

                        footer.add(noBtn, yesBtn);
                        dLayout.add(confirmTitle, msg, footer);
                        confirmDialog.add(dLayout);
                        confirmDialog.open();
                    });
                    cancelBtn.getStyle()
                            .set("background", "#ffebee").set("color", "#c62828")
                            .set("border", "1px solid #ef9a9a").set("border-radius", "8px")
                            .set("padding", "6px 16px").set("font-size", "12px").set("font-weight", "700").set("cursor", "pointer");

                    if (bk.getStatus() == Booking.BookingStatus.disetujui) {
                        Button pinjamBtn = new Button("📦 Ambil & Pinjam", ev -> showFinalizeBookingDialog(bk));
                        pinjamBtn.getStyle()
                                .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)")
                                .set("color", "white").set("border", "none").set("border-radius", "8px")
                                .set("padding", "6px 16px").set("font-weight", "700").set("font-size", "12px")
                                .set("cursor", "pointer");

                        actionRow.add(cancelBtn, pinjamBtn);
                    } else {
                        actionRow.add(cancelBtn);
                    }

                    card.add(actionRow);
                }

                page.add(card);
            }
        }

        contentArea.add(page);
    }

    private void showFinalizeBookingDialog(Booking bk) {
        Dialog d = new Dialog();
        d.setModal(true);
        d.setWidth("min(440px, 92vw)");

        VerticalLayout layout = new VerticalLayout();
        layout.getStyle()
                .set("background", "#ffffff").set("border-radius", "20px")
                .set("padding", "0").set("gap", "0")
                .set("box-shadow", "0 10px 40px rgba(0,0,0,0.12)");
        layout.setSpacing(false);
        layout.setPadding(false);

        // Header bar
        Div headerBar = new Div();
        headerBar.getStyle()
                .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)")
                .set("padding", "16px 22px 14px")
                .set("border-radius", "20px 20px 0 0");

        Span headerTitle = new Span("📦 Konfirmasi Pengambilan");
        headerTitle.getStyle().set("color", "white").set("font-size", "16px").set("font-weight", "800").set("display", "block");

        Span headerSub = new Span("Booking disetujui admin. Lengkapi data untuk mengambil barang.");
        headerSub.getStyle().set("color", "rgba(255,255,255,0.85)").set("font-size", "11px").set("margin-top", "3px").set("display", "block").set("line-height", "1.4");
        headerBar.add(headerTitle, headerSub);

        // Read-only info section
        Div formBody = new Div();
        formBody.getStyle().set("padding", "20px 22px");

        Div infoBox = new Div();
        infoBox.getStyle()
                .set("background", "#f9faf9")
                .set("border", "1px solid #eef2ee")
                .set("border-radius", "12px")
                .set("padding", "14px 16px")
                .set("margin-bottom", "16px");

        String namaBarangVal = bk.getBarang() != null ? bk.getBarang().getNamaBarang() : "—";
        String ruanganVal = bk.getRuangan() != null ? bk.getRuangan().getNamaRuangan() :
                (bk.getBarang() != null && bk.getBarang().getRuangan() != null ? bk.getBarang().getRuangan().getNamaRuangan() : "—");
        String catatanVal = (bk.getCatatan() != null && !bk.getCatatan().isBlank()) ? bk.getCatatan() : "Keperluan Peminjaman";

        infoBox.add(lightDetailRow("Barang", namaBarangVal));
        infoBox.add(lightDetailRow("Ruang Pemakaian", ruanganVal));
        infoBox.add(lightDetailRow("Tujuan / Catatan", catatanVal));

        // Inputs to fill
        // Info rows — full width, no isolated box
        infoBox.getStyle()
                .set("background", "transparent")
                .set("border", "none")
                .set("border-radius", "0")
                .set("padding", "0")
                .set("margin-bottom", "0");

        DatePicker tglKembaliPicker = new DatePicker("Tanggal Rencana Kembali *");
        tglKembaliPicker.setMin(LocalDate.now());
        tglKembaliPicker.setValue(LocalDate.now().plusDays(7));
        tglKembaliPicker.setWidthFull();
        tglKembaliPicker.getStyle().set("margin-top", "4px");
        tglKembaliPicker.addValueChangeListener(ev -> {
            if (ev.getValue() != null && ev.getValue().isBefore(LocalDate.now())) {
                tglKembaliPicker.setValue(LocalDate.now());
            }
        });

        // Upload foto bukti
        String[] fotoNameArr = {null};
        Div uploadWrapper = new Div();
        uploadWrapper.getStyle()
                .set("margin-top", "14px")
                .set("width", "100%");
        Span uploadLabel = new Span("Upload Foto Bukti *");
        uploadLabel.getStyle()
                .set("font-size", "13px").set("font-weight", "700")
                .set("color", "#1a2e1a").set("display", "block").set("margin-bottom", "6px")
                .set("font-family", "'Inter', sans-serif");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/*");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(10 * 1024 * 1024);
        upload.setUploadButton(buildUserUploadButton("📷  Pilih Foto Bukti"));
        upload.setDropLabel(new Span("JPG, PNG, HEIC · Max 10MB"));
        upload.setWidthFull();
        upload.addSucceededListener(ev -> fotoNameArr[0] = ev.getFileName());

        uploadWrapper.add(uploadLabel, upload);

        formBody.add(infoBox, tglKembaliPicker, uploadWrapper);

        // Footer — equal width buttons, full text
        Div footer = new Div();
        footer.getStyle()
                .set("display", "flex").set("gap", "10px")
                .set("padding", "16px 22px 20px")
                .set("border-top", "1px solid #eef2ee");

        Button cancelBtn = new Button("Batal", ev -> d.close());
        cancelBtn.getStyle()
                .set("flex", "1").set("background", "#f0f4f0")
                .set("color", "#4a6a4a").set("border", "none")
                .set("border-radius", "10px").set("height", "44px").set("cursor", "pointer")
                .set("font-weight", "600").set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px");

        Button submitBtn = new Button("📦 Pinjam Sekarang", ev -> {
            if (tglKembaliPicker.getValue() == null) {
                err("Pilih tanggal rencana kembali!");
                return;
            }
            if (fotoNameArr[0] == null) {
                err("Upload foto bukti terlebih dahulu!");
                return;
            }

            String savedFoto = null;
            try {
                savedFoto = property24.util.FileUploadHelper.saveImage(buffer, fotoNameArr[0]);
            } catch (Exception ex) {
                err("Gagal menyimpan foto bukti: " + ex.getMessage());
                return;
            }

            try {
                Ruangan targetRuangan = bk.getRuangan() != null ? bk.getRuangan() :
                        (bk.getBarang() != null ? bk.getBarang().getRuangan() : null);

                bookingService.convertToPinjaman(
                        bk,
                        targetRuangan,
                        catatanVal,
                        tglKembaliPicker.getValue(),
                        savedFoto
                );
                ok("Peminjaman berhasil dikonfirmasi!");
                d.close();
                switchTab("myitems");
            } catch (Exception ex) {
                err(ex.getMessage());
            }
        });
        submitBtn.getStyle()
                .set("flex", "2").set("border", "none").set("border-radius", "10px")
                .set("height", "44px").set("cursor", "pointer").set("font-weight", "800")
                .set("font-size", "13px").set("font-family", "'Inter', sans-serif")
                .set("background", "linear-gradient(135deg,#4d8f4d,#2d6a2d)").set("color", "white");

        footer.add(cancelBtn, submitBtn);
        layout.add(headerBar, formBody, footer);
        d.add(layout);
        d.open();
    }

    private Div infoRow(String label, String val) {
        Div row = new Div();
        row.getStyle().set("display", "flex").set("justify-content", "space-between").set("align-items", "center").set("font-size", "13px").set("margin-bottom", "6px");
        Span l = new Span(label);
        l.getStyle().set("color", "#a8cda8").set("font-weight", "500");
        Span v = new Span(val != null && !val.isBlank() ? val : "—");
        v.getStyle().set("color", "#ffffff").set("font-weight", "700").set("text-align", "right");
        row.add(l, v);
        return row;
    }

    private String logoSvg(int size) {
        return String.format(
            "<svg width='%d' height='%d' viewBox='0 0 120 120' xmlns='http://www.w3.org/2000/svg'>" +
            "<polygon points='14,48 58,22 58,82 14,108' fill='#1e3460'/>" +
            "<polygon points='58,22 104,48 104,108 58,82' fill='#3a9898'/>" +
            "<polygon points='14,48 58,22 104,48 58,74' fill='#5dcfca'/>" +
            "<circle cx='26' cy='103' r='16' fill='#6aab6a'/>" +
            "<text x='26' y='108.5' text-anchor='middle' font-family='Inter,sans-serif' font-size='11' font-weight='900' fill='white'>24</text>" +
            "</svg>", size, size);
    }


    private static final String IC_HOME =
        "<path d='M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z'/><polyline points='9 22 9 12 15 12 15 22'/>";
    private static final String IC_PLUS_CIRCLE =
        "<circle cx='12' cy='12' r='10'/><line x1='12' y1='8' x2='12' y2='16'/><line x1='8' y1='12' x2='16' y2='12'/>";
    private static final String IC_LIST =
        "<line x1='8' y1='6' x2='21' y2='6'/><line x1='8' y1='12' x2='21' y2='12'/><line x1='8' y1='18' x2='21' y2='18'/>" +
        "<line x1='3' y1='6' x2='3.01' y2='6'/><line x1='3' y1='12' x2='3.01' y2='12'/><line x1='3' y1='18' x2='3.01' y2='18'/>";
    private static final String IC_ROTATE =
        "<polyline points='1 4 1 10 7 10'/><path d='M3.51 15a9 9 0 1 0 .49-4.41'/>";
    private static final String IC_MENU =
        "<line x1='3' y1='12' x2='21' y2='12'/><line x1='3' y1='6' x2='21' y2='6'/><line x1='3' y1='18' x2='21' y2='18'/>";
    private static final String IC_BOX =
        "<path d='M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z'/>";
    private static final String IC_CAMERA =
        "<path d='M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z'/><circle cx='12' cy='13' r='4'/>";
    private static final String IC_BOOKMARK =
        "<path d='M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z'/>";
}
