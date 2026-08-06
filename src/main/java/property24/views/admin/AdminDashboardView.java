package property24.views.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import property24.entity.Barang;
import property24.entity.Kategori;
import property24.entity.Ruangan;
import property24.service.BarangService;
import property24.util.AuthSession;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route("admin-dashboard")
@PageTitle("Dashboard Admin | Property 24")
@CssImport("./views/admin/admin-dashboard.css")
public class AdminDashboardView extends HorizontalLayout {

    private final BarangService barangService;

    // Layout nodes that need to be refreshed
    private Div assetGridContainer;
    private Div statTotalEl;
    private Div statAvailEl;
    private Div statBorrowEl;
    private Div statCatEl;
    private String activeFilter = "all";

    // ── Sidebar nav items ─────────────────────────────────────────────────
    private Div navDashboard;
    private Div navAssets;
    private Div navBorrowed;
    private Div navApprove;
    private Div navSettings;

    public AdminDashboardView(BarangService barangService) {
        this.barangService = barangService;

        // Auth guard
        if (!AuthSession.isLoggedIn() || !AuthSession.isAdmin()) {
            addAttachListener(e -> UI.getCurrent().navigate("login"));
            return;
        }

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        getStyle().set("overflow", "hidden");
        getStyle().set("background", "#111a12");

        add(buildSidebar(), buildMainContent());

        addAttachListener(e -> {
            UI.getCurrent().getPage().addStyleSheet(
                    "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap");
            injectDashboardCss();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ════════════════════════════════════════════════════════════════════════
    private Div buildSidebar() {
        Div sidebar = new Div();
        sidebar.getStyle()
                .set("width", "200px")
                .set("min-width", "200px")
                .set("background", "#0e1b10")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("height", "100vh")
                .set("overflow", "hidden")
                .set("border-right", "1px solid rgba(255,255,255,0.05)");

        // ── Logo ──────────────────────────────────────────────────────────
        Div logoArea = new Div();
        logoArea.getStyle()
                .set("padding", "22px 20px 18px")
                .set("border-bottom", "1px solid rgba(143,176,138,0.08)");

        Div logoRow = new Div();
        logoRow.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "10px");

        // Use actual logo image
        Image logoImg = new Image("images/logo.png", "Property 24");
        logoImg.setWidth("38px");
        logoImg.setHeight("38px");
        logoImg.getStyle()
                .set("object-fit", "contain")
                .set("border-radius", "8px");

        Div brandText = new Div();
        brandText.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("line-height", "1.15");
        Span brandTop = new Span("PROPERTY");
        brandTop.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "12px")
                .set("font-weight", "800")
                .set("letter-spacing", "1.5px");
        Span brandSub = new Span("24  Management");
        brandSub.getStyle()
                .set("color", "#6aab6a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "9px")
                .set("font-weight", "500")
                .set("letter-spacing", "0.5px");
        brandText.add(brandTop, brandSub);
        logoRow.add(logoImg, brandText);
        logoArea.add(logoRow);

        // ── Navigation ────────────────────────────────────────────────────
        Div nav = new Div();
        nav.getStyle()
                .set("flex", "1")
                .set("padding", "16px 0")
                .set("overflow-y", "auto");

        Div navLabel = navSection("MAIN MENU");

        navDashboard = navItem(ICON_DASHBOARD, "Dashboard", true);
        navAssets    = navItem(ICON_BOX,       "Assets",    false);
        navBorrowed  = navItem(ICON_CLIPBOARD, "Borrowed",  false);

        Div approveLabel = navSection("APPROVE BY ADMIN");
        navApprove   = navItem(ICON_CHECK,     "Approve Assets", false);
        navSettings  = navItem(ICON_SETTINGS,  "Settings",  false);

        // Click handlers
        navDashboard.addClickListener(e -> setActiveNav("dashboard"));
        navAssets.addClickListener(e -> {
            setActiveNav("assets");
            info("Halaman Assets coming soon!");
        });
        navBorrowed.addClickListener(e -> {
            setActiveNav("borrowed");
            info("Halaman Borrowed coming soon!");
        });
        navApprove.addClickListener(e -> {
            setActiveNav("approve");
            info("Halaman Approve coming soon!");
        });
        navSettings.addClickListener(e -> {
            setActiveNav("settings");
            info("Halaman Settings coming soon!");
        });

        nav.add(navLabel, navDashboard, navAssets, navBorrowed, approveLabel, navApprove, navSettings);

        // ── User Info at bottom ───────────────────────────────────────────
        Div userInfo = new Div();
        userInfo.getStyle()
                .set("padding", "16px 20px")
                .set("border-top", "1px solid rgba(143,176,138,0.08)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("cursor", "pointer");
        userInfo.setId("sidebar-user-info");

        // Avatar
        Div avatar = new Div();
        avatar.getStyle()
                .set("width", "36px")
                .set("height", "36px")
                .set("border-radius", "50%")
                .set("background", "linear-gradient(135deg, #4d8f4d, #8fb08a)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        String initials = AuthSession.getDisplayName().substring(0, Math.min(2, AuthSession.getDisplayName().length())).toUpperCase();
        Span initSpan = new Span(initials);
        initSpan.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("font-weight", "700");
        avatar.add(initSpan);

        Div userDetails = new Div();
        userDetails.getStyle().set("flex", "1").set("min-width", "0");
        Span userName = new Span(AuthSession.getDisplayName());
        userName.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("display", "block")
                .set("white-space", "nowrap")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis");
        Span userRole = new Span("Admin");
        userRole.getStyle()
                .set("color", "#6aab6a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "11px")
                .set("font-weight", "500");
        userDetails.add(userName, userRole);

        // Logout icon
        Div logoutBtn = new Div();
        logoutBtn.getElement().setProperty("innerHTML", svgStr(ICON_LOGOUT, "16", "#8fa88f"));
        logoutBtn.getStyle().set("cursor", "pointer").set("opacity", "0.7").set("flex-shrink", "0");
        logoutBtn.setTitle("Logout");
        logoutBtn.addClickListener(e -> {
            AuthSession.logout();
            UI.getCurrent().navigate("login");
        });

        userInfo.add(avatar, userDetails, logoutBtn);
        sidebar.add(logoArea, nav, userInfo);
        return sidebar;
    }

    private Div navSection(String label) {
        Div d = new Div();
        d.getStyle()
                .set("color", "rgba(143,176,138,0.45)")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "9px")
                .set("font-weight", "700")
                .set("letter-spacing", "2px")
                .set("padding", "10px 20px 6px")
                .set("text-transform", "uppercase");
        d.setText(label);
        return d;
    }

    private Div navItem(String iconPath, String label, boolean active) {
        Div item = new Div();
        item.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("padding", "10px 20px")
                .set("cursor", "pointer")
                .set("border-radius", "0 8px 8px 0")
                .set("margin", "2px 12px 2px 0")
                .set("transition", "all 0.2s ease")
                .set("position", "relative");

        if (active) {
            item.getStyle()
                    .set("background", "rgba(106,171,106,0.12)")
                    .set("border-left", "3px solid #6aab6a");
        } else {
            item.getStyle()
                    .set("background", "transparent")
                    .set("border-left", "3px solid transparent");
        }

        Div icon = new Div();
        icon.getElement().setProperty("innerHTML", svgStr(iconPath, "16", active ? "#6aab6a" : "#5a7a5a"));
        icon.getStyle().set("flex-shrink", "0").set("display", "flex").set("align-items", "center");

        Span lbl = new Span(label);
        lbl.getStyle()
                .set("color", active ? "#c8e6c8" : "#7a9a7a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("font-weight", active ? "600" : "400");

        item.add(icon, lbl);
        return item;
    }

    private void setActiveNav(String which) {
        resetNav(navDashboard);
        resetNav(navAssets);
        resetNav(navBorrowed);
        resetNav(navApprove);
        resetNav(navSettings);

        Div target = switch (which) {
            case "assets"   -> navAssets;
            case "borrowed" -> navBorrowed;
            case "approve"  -> navApprove;
            case "settings" -> navSettings;
            default         -> navDashboard;
        };
        activateNav(target);
    }

    private void resetNav(Div item) {
        item.getStyle()
                .set("background", "transparent")
                .set("border-left", "3px solid transparent");
        if (item.getComponentCount() >= 2) {
            item.getChildren().forEach(c -> {
                if (c instanceof Div d) {
                    d.getElement().setProperty("innerHTML",
                            d.getElement().getProperty("innerHTML")
                                    .replace("#6aab6a", "#5a7a5a"));
                }
                if (c instanceof Span s) {
                    s.getStyle().set("color", "#7a9a7a").set("font-weight", "400");
                }
            });
        }
    }

    private void activateNav(Div item) {
        item.getStyle()
                .set("background", "rgba(106,171,106,0.12)")
                .set("border-left", "3px solid #6aab6a");
        item.getChildren().forEach(c -> {
            if (c instanceof Div d) {
                d.getElement().setProperty("innerHTML",
                        d.getElement().getProperty("innerHTML")
                                .replace("#5a7a5a", "#6aab6a"));
            }
            if (c instanceof Span s) {
                s.getStyle().set("color", "#c8e6c8").set("font-weight", "600");
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // MAIN CONTENT
    // ════════════════════════════════════════════════════════════════════════
    private Div buildMainContent() {
        Div main = new Div();
        main.getStyle()
                .set("flex", "1")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("height", "100vh")
                .set("overflow", "hidden")
                .set("background", "#f0f4f1");

        main.add(buildTopBar(), buildScrollableBody());
        return main;
    }

    private Div buildScrollableBody() {
        Div body = new Div();
        body.getStyle()
                .set("flex", "1")
                .set("overflow-y", "auto")
                .set("padding", "0 28px 28px")
                .set("background", "#f0f4f1");

        body.add(buildPageHeader(), buildStatsSection(), buildFilterAndGrid());
        return body;
    }

    // ── TOP BAR ──────────────────────────────────────────────────────────────
    private Div buildTopBar() {
        Div bar = new Div();
        bar.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("padding", "14px 28px")
                .set("border-bottom", "1px solid rgba(0,0,0,0.08)")
                .set("background", "#ffffff")
                .set("flex-shrink", "0");

        // Search
        TextField search = new TextField();
        search.addClassName("dashboard-search");
        search.setPlaceholder("Search assets, tags, locations...");
        search.getStyle().set("flex", "1");
        Div searchIcon = new Div();
        searchIcon.getElement().setProperty("innerHTML", svgStr(ICON_SEARCH, "16", "rgba(80,120,80,0.5)"));
        search.setPrefixComponent(searchIcon);
        search.addValueChangeListener(e -> {
            List<Barang> results = barangService.search(e.getValue());
            refreshGrid(results);
        });

        // Notification bell
        Div notifBtn = new Div();
        notifBtn.getStyle()
                .set("width", "38px")
                .set("height", "38px")
                .set("border-radius", "10px")
                .set("background", "rgba(0,0,0,0.04)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("cursor", "pointer")
                .set("position", "relative")
                .set("border", "1px solid rgba(0,0,0,0.08)")
                .set("transition", "background 0.2s");
        notifBtn.getElement().setProperty("innerHTML",
                svgStr(ICON_BELL, "18", "rgba(30,60,30,0.6)"));
        notifBtn.setId("notif-btn");

        // Notification badge
        Div badge = new Div();
        badge.getStyle()
                .set("position", "absolute")
                .set("top", "-4px")
                .set("right", "-4px")
                .set("width", "16px")
                .set("height", "16px")
                .set("border-radius", "50%")
                .set("background", "#e07a2a")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");
        Span badgeNum = new Span("3");
        badgeNum.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "9px")
                .set("font-weight", "700");
        badge.add(badgeNum);
        notifBtn.add(badge);
        notifBtn.addClickListener(e -> info("Tidak ada notifikasi baru."));

        // Add New Asset button
        Button addBtn = new Button("+ Add New Asset");
        addBtn.setId("add-asset-btn");
        addBtn.getStyle()
                .set("background", "linear-gradient(135deg, #4d8f4d, #8fb08a)")
                .set("color", "#0a1f0f")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "700")
                .set("font-size", "13px")
                .set("border", "none")
                .set("border-radius", "10px")
                .set("padding", "0 18px")
                .set("height", "38px")
                .set("cursor", "pointer")
                .set("white-space", "nowrap")
                .set("box-shadow", "0 4px 12px rgba(78,143,78,0.3)")
                .set("transition", "all 0.3s ease");
        addBtn.addClickListener(e -> buildAddAssetDialog().open());

        bar.add(search, notifBtn, addBtn);
        return bar;
    }

    // ── PAGE HEADER ───────────────────────────────────────────────────────────
    private Div buildPageHeader() {
        Div header = new Div();
        header.getStyle().set("padding", "24px 0 16px");

        Div title = new Div();
        title.getStyle()
                .set("color", "#1a2e1a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "26px")
                .set("font-weight", "800");
        title.setText("Asset Dashboard");

        Div subtitle = new Div();
        subtitle.getStyle()
                .set("color", "#6b8a6b")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("margin-top", "4px");
        subtitle.setText("Track and manage all organizational assets in one place.");

        header.add(title, subtitle);
        return header;
    }

    // ── STATS SECTION ─────────────────────────────────────────────────────────
    private Div buildStatsSection() {
        long total    = barangService.countTotal();
        long avail    = barangService.countByStatus(Barang.Status.tersedia);
        long borrowed = barangService.countByStatus(Barang.Status.dipinjam);
        long cats     = barangService.countKategori();

        Div row = new Div();
        row.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(4, 1fr)")
                .set("gap", "14px")
                .set("margin-bottom", "24px");

        statTotalEl  = statCard(ICON_MONITOR,    "TOTAL ASSETS",  total,    "#5b9bd5", "+" + total + " new", "#5b9bd5");
        statAvailEl  = statCard(ICON_CHECK_CIRC, "AVAILABLE",     avail,    "#6aab6a", "Ready",              "#6aab6a");
        statBorrowEl = statCard(ICON_ARROW_UP,   "BORROWED",      borrowed, "#e07a2a", "Active",             "#e07a2a");
        statCatEl    = statCard(ICON_FOLDER,     "CATEGORIES",    cats,     "#b07a6a", "Types",              "#b07a6a");

        row.add(statTotalEl, statAvailEl, statBorrowEl, statCatEl);
        return row;
    }

    private Div statCard(String iconPath, String label, long value, String color,
                         String badgeText, String badgeColor) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid rgba(0,0,0,0.07)")
                .set("border-radius", "14px")
                .set("padding", "18px 16px 16px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "8px")
                .set("position", "relative")
                .set("cursor", "default")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.06)")
                .set("transition", "border-color 0.2s, transform 0.2s");

        // Top row: icon + badge
        Div topRow = new Div();
        topRow.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between");

        Div iconWrap = new Div();
        iconWrap.getStyle()
                .set("width", "34px")
                .set("height", "34px")
                .set("border-radius", "9px")
                .set("background", color + "22")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        iconWrap.getElement().setProperty("innerHTML", svgStr(iconPath, "17", color));

        // Badge label top-right
        Div badge = new Div();
        badge.getStyle()
                .set("background", badgeColor + "22")
                .set("color", badgeColor)
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "10px")
                .set("font-weight", "600")
                .set("padding", "3px 8px")
                .set("border-radius", "20px")
                .set("border", "1px solid " + badgeColor + "44");
        badge.setText(badgeText);
        topRow.add(iconWrap, badge);

        // Value
        Div valDiv = new Div();
        valDiv.getStyle()
                .set("color", "#1a2e1a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "32px")
                .set("font-weight", "800")
                .set("line-height", "1")
                .set("margin-top", "6px");
        valDiv.setText(String.valueOf(value));

        // Label
        Div labelDiv = new Div();
        labelDiv.getStyle()
                .set("color", "rgba(60,100,60,0.6)")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "10px")
                .set("font-weight", "600")
                .set("letter-spacing", "1.5px")
                .set("text-transform", "uppercase")
                .set("margin-top", "2px");
        labelDiv.setText(label);

        card.add(topRow, valDiv, labelDiv);
        return card;
    }

    // ── FILTER + GRID ─────────────────────────────────────────────────────────
    private Div buildFilterAndGrid() {
        Div wrapper = new Div();

        // Filter bar
        Div filterBar = new Div();
        filterBar.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("margin-bottom", "18px")
                .set("flex-wrap", "wrap");

        List<Barang> allBarang = barangService.getAllBarang();
        List<Kategori> kategoris = barangService.getAllKategori();

        // Count label
        Div countLabel = new Div();
        countLabel.getStyle()
                .set("margin-left", "auto")
                .set("color", "rgba(60,100,60,0.55)")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "12px");
        countLabel.setText(allBarang.size() + " assets found");

        // "All" button
        Button allBtn = filterBtn("All", true);
        allBtn.setId("filter-all");
        allBtn.addClickListener(e -> {
            setActiveFilter("all", filterBar);
            List<Barang> data = barangService.getAllBarang();
            countLabel.setText(data.size() + " assets found");
            refreshGrid(data);
        });

        filterBar.add(allBtn);

        // Category buttons
        for (Kategori k : kategoris) {
            Button kBtn = filterBtn(k.getNamaKategori(), false);
            kBtn.setId("filter-" + k.getId());
            kBtn.addClickListener(e -> {
                setActiveFilter("cat-" + k.getId(), filterBar);
                List<Barang> data = barangService.getByKategori(k.getNamaKategori());
                countLabel.setText(data.size() + " assets found");
                refreshGrid(data);
            });
            filterBar.add(kBtn);
        }

        filterBar.add(countLabel);

        // Asset grid
        assetGridContainer = new Div();
        assetGridContainer.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(260px, 1fr))")
                .set("gap", "16px");

        refreshGrid(allBarang);
        wrapper.add(filterBar, assetGridContainer);
        return wrapper;
    }

    private Button filterBtn(String label, boolean active) {
        Button btn = new Button(label);
        btn.getStyle()
                .set("background", active ? "#1e3828" : "rgba(0,0,0,0.0)")
                .set("color",      active ? "#ffffff" : "#5a7a5a")
                .set("border",     active ? "1px solid #1e3828" : "1px solid rgba(0,0,0,0.12)")
                .set("border-radius", "20px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "13px")
                .set("font-weight", active ? "600" : "400")
                .set("padding", "6px 18px")
                .set("height", "34px")
                .set("cursor", "pointer")
                .set("transition", "all 0.2s");
        return btn;
    }

    private void setActiveFilter(String filterId, Div filterBar) {
        activeFilter = filterId;
        filterBar.getChildren()
                .filter(c -> c instanceof Button)
                .map(c -> (Button) c)
                .forEach(b -> {
                    boolean isActive = b.getId().orElse("").equals(
                            filterId.equals("all") ? "filter-all" : "filter-" + filterId.replace("cat-", ""));
                    b.getStyle()
                            .set("background", isActive ? "#1e3828" : "transparent")
                            .set("color",      isActive ? "#ffffff" : "#5a7a5a")
                            .set("border",     isActive ? "1px solid #1e3828" : "1px solid rgba(0,0,0,0.12)")
                            .set("box-shadow", "none")
                            .set("font-weight", isActive ? "600" : "400");
                });
    }

    private void refreshGrid(List<Barang> items) {
        assetGridContainer.removeAll();
        if (items.isEmpty()) {
            Div empty = new Div();
            empty.getStyle()
                    .set("grid-column", "1/-1")
                    .set("text-align", "center")
                    .set("padding", "60px 0")
                    .set("color", "rgba(184,201,191,0.45)")
                    .set("font-family", "'Inter', sans-serif")
                    .set("font-size", "15px");
            empty.setText("Tidak ada aset ditemukan.");
            assetGridContainer.add(empty);
            return;
        }
        for (Barang b : items) {
            assetGridContainer.add(buildAssetCard(b));
        }
    }

    // ── ASSET CARD ────────────────────────────────────────────────────────────
    private Div buildAssetCard(Barang barang) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid rgba(0,0,0,0.06)")
                .set("border-radius", "16px")
                .set("overflow", "hidden")
                .set("transition", "transform 0.2s ease, box-shadow 0.2s ease")
                .set("cursor", "pointer")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)");
        card.addClassName("asset-card");

        boolean isBorrowed = barang.getStatus() == Barang.Status.dipinjam;
        boolean isRusak    = barang.getStatus() == Barang.Status.rusak;
        String badgeColor  = isBorrowed ? "#e07a2a" : isRusak ? "#e06a6a" : "#22a05a";
        String badgeBg     = isBorrowed ? "rgba(224,122,42,0.12)" : isRusak ? "rgba(224,106,106,0.12)" : "rgba(34,160,90,0.10)";
        String badgeText   = isBorrowed ? "Borrowed" : isRusak ? "Rusak" : "Available";
        String kodeBarang  = barang.getKodeBarang() != null ? barang.getKodeBarang() : ("AST-" + String.format("%03d", barang.getId()));

        // ── Image section ────────────────────────────────────────────────
        Div imgSection = new Div();
        imgSection.getStyle()
                .set("height", "155px")
                .set("background", categoryBgColor(barang.getKategori() != null
                        ? barang.getKategori().getNamaKategori() : "default"))
                .set("position", "relative")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        // Product illustration SVG (bigger)
        Div catIconWrap = new Div();
        catIconWrap.getElement().setProperty("innerHTML",
                svgStr(categoryIconPath(barang.getKategori() != null
                        ? barang.getKategori().getNamaKategori() : ""), "72", "rgba(255,255,255,0.55)"));
        catIconWrap.getStyle().set("filter", "drop-shadow(0 4px 12px rgba(0,0,0,0.25))");
        imgSection.add(catIconWrap);

        // Status badge – top left, pill shape
        Div statusBadge = new Div();
        statusBadge.getStyle()
                .set("position", "absolute")
                .set("top", "10px")
                .set("left", "10px")
                .set("background", badgeBg)
                .set("border", "1px solid " + badgeColor + "66")
                .set("color", badgeColor)
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "10px")
                .set("font-weight", "700")
                .set("padding", "3px 10px")
                .set("border-radius", "20px")
                .set("backdrop-filter", "blur(6px)")
                .set("-webkit-backdrop-filter", "blur(6px)");
        statusBadge.setText(badgeText);

        // Asset code – top right, muted dark chip
        Div codeChip = new Div();
        codeChip.getStyle()
                .set("position", "absolute")
                .set("top", "10px")
                .set("right", "10px")
                .set("background", "rgba(0,0,0,0.30)")
                .set("color", "rgba(255,255,255,0.85)")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "9px")
                .set("font-weight", "700")
                .set("padding", "3px 8px")
                .set("border-radius", "6px")
                .set("letter-spacing", "0.5px")
                .set("backdrop-filter", "blur(6px)");
        codeChip.setText(kodeBarang);

        imgSection.add(statusBadge, codeChip);

        // ── Info section ─────────────────────────────────────────────────
        Div info = new Div();
        info.getStyle()
                .set("padding", "14px 16px 12px")
                .set("background", "#ffffff");

        // Stars first (above name)
        int starCount = barang.getBintangSaatIni() != null ? barang.getBintangSaatIni() : 0;
        Div stars = buildStars(starCount);
        stars.getStyle().set("margin-bottom", "6px");

        // Name
        Div name = new Div();
        name.getStyle()
                .set("color", "#1a2e1a")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "14px")
                .set("font-weight", "700")
                .set("margin-bottom", "4px")
                .set("white-space", "nowrap")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis");
        name.setText(barang.getNamaBarang());

        // Time / borrow info row
        Div timeRow = new Div();
        timeRow.getStyle()
                .set("display", "flex")
                .set("align-items", "flex-start")
                .set("gap", "5px")
                .set("margin-bottom", "8px");

        Div clockIcon = new Div();
        String timeIconColor = isBorrowed ? "#e07a2a" : "#999";
        clockIcon.getElement().setProperty("innerHTML", svgStr(ICON_CLOCK, "12", timeIconColor));
        clockIcon.getStyle().set("margin-top", "1px").set("flex-shrink", "0");

        Div timeText = new Div();
        timeText.getStyle()
                .set("color", isBorrowed ? "#e07a2a" : "#888")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "11px")
                .set("line-height", "1.4");

        String timeStr = barang.getDiperbaruiPada() != null
                ? barang.getDiperbaruiPada().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
                : "—";
        String timeLabel = isBorrowed ? "Last borrowed: " + timeStr : "Last updated: " + timeStr;
        timeText.setText(timeLabel);
        timeRow.add(clockIcon, timeText);

        // Description
        Div descDiv = new Div();
        descDiv.getStyle()
                .set("color", "#6b8a6b")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "11px")
                .set("line-height", "1.5")
                .set("margin-bottom", "12px")
                .set("overflow", "hidden")
                .set("display", "-webkit-box")
                .set("-webkit-line-clamp", "2")
                .set("-webkit-box-orient", "vertical")
                .set("min-height", "33px");
        String desc = barang.getDeskripsiBintang() != null && !barang.getDeskripsiBintang().isBlank()
                ? barang.getDeskripsiBintang() : "No description available";
        descDiv.setText(desc);

        // Action buttons
        Div actions = new Div();
        actions.getStyle()
                .set("display", "flex")
                .set("gap", "8px")
                .set("border-top", "1px solid rgba(0,0,0,0.06)")
                .set("padding-top", "10px");

        Button editBtn = actionBtnLight("Edit", ICON_EDIT, "#3a7bd5", barang);
        Button detailBtn = actionBtnLightGreen("Detail", ICON_EYE, barang);
        actions.add(editBtn, detailBtn);

        info.add(stars, name, timeRow, descDiv, actions);
        card.add(imgSection, info);
        return card;
    }

    private Button actionBtnLight(String label, String iconPath, String color, Barang barang) {
        Button btn = new Button();
        btn.getElement().setProperty("innerHTML",
                svgStr(iconPath, "13", color) + " <span style='margin-left:3px'>" + label + "</span>");
        btn.getStyle()
                .set("background", "rgba(58,123,213,0.08)")
                .set("color", color)
                .set("border", "1px solid rgba(58,123,213,0.18)")
                .set("border-radius", "8px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("padding", "6px 12px")
                .set("cursor", "pointer")
                .set("flex", "1")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("transition", "all 0.2s");
        btn.addClickListener(e -> buildEditAssetDialog(barang).open());
        return btn;
    }

    private Button actionBtnLightGreen(String label, String iconPath, Barang barang) {
        Button btn = new Button();
        btn.getElement().setProperty("innerHTML",
                svgStr(iconPath, "13", "#3a7a5a") + " <span style='margin-left:3px'>" + label + "</span>");
        btn.getStyle()
                .set("background", "rgba(58,122,90,0.08)")
                .set("color", "#3a7a5a")
                .set("border", "1px solid rgba(58,122,90,0.18)")
                .set("border-radius", "8px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("padding", "6px 12px")
                .set("cursor", "pointer")
                .set("flex", "1")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("transition", "all 0.2s");
        btn.addClickListener(e -> showBarangDetail(barang));
        return btn;
    }

    private Div buildStars(int count) {
        Div row = new Div();
        row.getStyle().set("display", "flex").set("gap", "2px");
        for (int i = 1; i <= 5; i++) {
            Div star = new Div();
            star.getElement().setProperty("innerHTML",
                    svgStr(ICON_STAR, "14", i <= count ? "#f5a623" : "#dde0da"));
            row.add(star);
        }
        return row;
    }

    private void showBarangDetail(Barang b) {
        Dialog d = new Dialog();
        d.setModal(true);
        d.setWidth("420px");
        d.getElement().getStyle()
                .set("--lumo-base-color", "#1e2e20")
                .set("--lumo-body-text-color", "white");

        VerticalLayout content = new VerticalLayout();
        content.getStyle()
                .set("background", "#1e2e20")
                .set("border-radius", "16px")
                .set("padding", "24px");
        content.setSpacing(false);
        content.setPadding(false);

        Div dTitle = new Div();
        dTitle.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "20px")
                .set("font-weight", "700")
                .set("margin-bottom", "16px");
        dTitle.setText("Detail: " + b.getNamaBarang());

        String[][] rows = {
            {"Kode Barang",   b.getKodeBarang()   != null ? b.getKodeBarang()   : "—"},
            {"Kategori",      b.getKategori()      != null ? b.getKategori().getNamaKategori() : "—"},
            {"Ruangan",       b.getRuangan()       != null ? b.getRuangan().getNamaRuangan()   : "—"},
            {"Status",        b.getStatus()        != null ? b.getStatus().name() : "—"},
            {"Stock",         String.valueOf(b.getStock() != null ? b.getStock() : 0)},
            {"Rating",        (b.getBintangSaatIni() != null ? b.getBintangSaatIni() : 0) + " / 5"},
            {"Deskripsi",     b.getDeskripsiBintang() != null ? b.getDeskripsiBintang() : "—"},
        };

        for (String[] r : rows) {
            Div row = new Div();
            row.getStyle()
                    .set("display", "flex")
                    .set("justify-content", "space-between")
                    .set("padding", "8px 0")
                    .set("border-bottom", "1px solid rgba(255,255,255,0.05)");
            Div lbl = new Div();
            lbl.getStyle()
                    .set("color", "rgba(184,201,191,0.65)")
                    .set("font-family", "'Inter', sans-serif")
                    .set("font-size", "12px");
            lbl.setText(r[0]);
            Div val = new Div();
            val.getStyle()
                    .set("color", "white")
                    .set("font-family", "'Inter', sans-serif")
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("text-align", "right")
                    .set("max-width", "200px");
            val.setText(r[1]);
            row.add(lbl, val);
            content.add(row);
        }

        Button closeBtn = new Button("Tutup", ev -> d.close());
        closeBtn.getStyle()
                .set("background", "rgba(143,176,138,0.2)")
                .set("color", "#8fb08a")
                .set("border", "1px solid rgba(143,176,138,0.3)")
                .set("border-radius", "8px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "600")
                .set("margin-top", "16px")
                .set("width", "100%");

        content.add(dTitle);
        content.add(closeBtn);
        d.add(content);
        d.open();
    }

    // ── ADD ASSET DIALOG ──────────────────────────────────────────────────────
    private Dialog buildAddAssetDialog() {
        Dialog d = new Dialog();
        d.setModal(true);
        d.setWidth("520px");

        VerticalLayout content = dialogLayout();

        Div dTitle = dialogTitle("Tambah Aset Baru");
        Hr hr = new Hr();
        hr.getStyle().set("border-color", "rgba(143,176,138,0.15)").set("margin", "4px 0 16px");

        TextField kode      = dialogTextField("Kode Barang",  "Contoh: PRJ-001");
        TextField nama      = dialogTextField("Nama Barang",  "Nama aset");
        TextArea  deskripsi = dialogTextArea("Deskripsi",     "Deskripsi kondisi aset");
        IntegerField stock  = dialogIntField("Stock",          1);
        IntegerField rating = dialogIntField("Rating (1-5)",   3);

        ComboBox<Kategori> kategoriBox = new ComboBox<>("Kategori");
        kategoriBox.setItems(barangService.getAllKategori());
        kategoriBox.setItemLabelGenerator(Kategori::getNamaKategori);
        kategoriBox.setWidthFull();
        styleDialogField(kategoriBox);

        ComboBox<Ruangan> ruanganBox = new ComboBox<>("Ruangan");
        ruanganBox.setItems(barangService.getAllRuangan());
        ruanganBox.setItemLabelGenerator(Ruangan::getNamaRuangan);
        ruanganBox.setWidthFull();
        styleDialogField(ruanganBox);

        ComboBox<Barang.Status> statusBox = new ComboBox<>("Status");
        statusBox.setItems(Barang.Status.values());
        statusBox.setValue(Barang.Status.tersedia);
        statusBox.setWidthFull();
        styleDialogField(statusBox);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        form.add(kode, nama, kategoriBox, ruanganBox, statusBox, stock, rating, deskripsi);
        form.setColspan(deskripsi, 2);
        form.setColspan(statusBox, 2);

        Div btnRow = new Div();
        btnRow.getStyle()
                .set("display", "flex")
                .set("gap", "10px")
                .set("margin-top", "16px");

        Button cancel = dialogCancelBtn("Batal", d);
        Button save   = dialogSaveBtn("Simpan");
        save.addClickListener(e -> {
            if (nama.getValue().isBlank()) { err("Nama barang harus diisi!"); return; }
            Barang b = new Barang();
            b.setKodeBarang(kode.getValue().isBlank() ? null : kode.getValue());
            b.setNamaBarang(nama.getValue().trim());
            b.setKategori(kategoriBox.getValue());
            b.setRuangan(ruanganBox.getValue());
            b.setStatus(statusBox.getValue() != null ? statusBox.getValue() : Barang.Status.tersedia);
            b.setStock(stock.getValue() != null ? stock.getValue() : 0);
            b.setBintangSaatIni(Math.min(5, Math.max(1, rating.getValue() != null ? rating.getValue() : 3)));
            b.setDeskripsiBintang(deskripsi.getValue());
            barangService.save(b);
            refreshGrid(barangService.getAllBarang());
            d.close();
            ok("Aset berhasil ditambahkan!");
        });

        btnRow.add(cancel, save);
        content.add(dTitle, hr, form, btnRow);
        d.add(content);
        return d;
    }

    // ── EDIT ASSET DIALOG ─────────────────────────────────────────────────────
    private Dialog buildEditAssetDialog(Barang barang) {
        Dialog d = new Dialog();
        d.setModal(true);
        d.setWidth("520px");

        VerticalLayout content = dialogLayout();

        Div dTitle = dialogTitle("Edit Aset: " + barang.getNamaBarang());
        Hr hr = new Hr();
        hr.getStyle().set("border-color", "rgba(143,176,138,0.15)").set("margin", "4px 0 16px");

        TextField kode      = dialogTextField("Kode Barang",  barang.getKodeBarang() != null ? barang.getKodeBarang() : "");
        TextField nama      = dialogTextField("Nama Barang",  barang.getNamaBarang());
        TextArea  deskripsi = dialogTextArea("Deskripsi",     barang.getDeskripsiBintang() != null ? barang.getDeskripsiBintang() : "");
        IntegerField stock  = dialogIntField("Stock",          barang.getStock() != null ? barang.getStock() : 0);
        IntegerField rating = dialogIntField("Rating (1-5)",   barang.getBintangSaatIni() != null ? barang.getBintangSaatIni() : 3);

        ComboBox<Kategori> kategoriBox = new ComboBox<>("Kategori");
        kategoriBox.setItems(barangService.getAllKategori());
        kategoriBox.setItemLabelGenerator(Kategori::getNamaKategori);
        kategoriBox.setValue(barang.getKategori());
        kategoriBox.setWidthFull();
        styleDialogField(kategoriBox);

        ComboBox<Ruangan> ruanganBox = new ComboBox<>("Ruangan");
        ruanganBox.setItems(barangService.getAllRuangan());
        ruanganBox.setItemLabelGenerator(Ruangan::getNamaRuangan);
        ruanganBox.setValue(barang.getRuangan());
        ruanganBox.setWidthFull();
        styleDialogField(ruanganBox);

        ComboBox<Barang.Status> statusBox = new ComboBox<>("Status");
        statusBox.setItems(Barang.Status.values());
        statusBox.setValue(barang.getStatus());
        statusBox.setWidthFull();
        styleDialogField(statusBox);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        form.add(kode, nama, kategoriBox, ruanganBox, statusBox, stock, rating, deskripsi);
        form.setColspan(deskripsi, 2);
        form.setColspan(statusBox, 2);

        Div btnRow = new Div();
        btnRow.getStyle().set("display", "flex").set("gap", "10px").set("margin-top", "16px");

        // Delete button
        Button deleteBtn = new Button("Hapus");
        deleteBtn.getStyle()
                .set("background", "rgba(224,106,106,0.12)")
                .set("color", "#e06a6a")
                .set("border", "1px solid rgba(224,106,106,0.25)")
                .set("border-radius", "8px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "600")
                .set("padding", "0 18px")
                .set("height", "40px")
                .set("cursor", "pointer");
        deleteBtn.addClickListener(e -> {
            barangService.delete(barang.getId());
            refreshGrid(barangService.getAllBarang());
            d.close();
            ok("Aset berhasil dihapus.");
        });

        Button cancel = dialogCancelBtn("Batal", d);
        Button save   = dialogSaveBtn("Simpan Perubahan");
        save.addClickListener(e -> {
            if (nama.getValue().isBlank()) { err("Nama barang harus diisi!"); return; }
            barang.setKodeBarang(kode.getValue().isBlank() ? null : kode.getValue());
            barang.setNamaBarang(nama.getValue().trim());
            barang.setKategori(kategoriBox.getValue());
            barang.setRuangan(ruanganBox.getValue());
            barang.setStatus(statusBox.getValue() != null ? statusBox.getValue() : Barang.Status.tersedia);
            barang.setStock(stock.getValue() != null ? stock.getValue() : 0);
            barang.setBintangSaatIni(Math.min(5, Math.max(1, rating.getValue() != null ? rating.getValue() : 3)));
            barang.setDeskripsiBintang(deskripsi.getValue());
            barangService.save(barang);
            refreshGrid(barangService.getAllBarang());
            d.close();
            ok("Aset berhasil diperbarui!");
        });

        btnRow.add(deleteBtn, cancel, save);
        content.add(dTitle, hr, form, btnRow);
        d.add(content);
        return d;
    }

    // ════════════════════════════════════════════════════════════════════════
    // DIALOG HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private VerticalLayout dialogLayout() {
        VerticalLayout v = new VerticalLayout();
        v.getStyle()
                .set("background", "#182218")
                .set("border-radius", "16px")
                .set("padding", "24px");
        v.setSpacing(false);
        v.setPadding(false);
        return v;
    }

    private Div dialogTitle(String text) {
        Div t = new Div();
        t.getStyle()
                .set("color", "white")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "18px")
                .set("font-weight", "700")
                .set("margin-bottom", "4px");
        t.setText(text);
        return t;
    }

    private TextField dialogTextField(String label, String value) {
        TextField f = new TextField(label);
        f.setValue(value);
        f.setWidthFull();
        f.addClassName("dialog-field");
        styleDialogField(f);
        return f;
    }

    private TextArea dialogTextArea(String label, String value) {
        TextArea f = new TextArea(label);
        f.setValue(value);
        f.setWidthFull();
        f.setMinHeight("70px");
        f.addClassName("dialog-field");
        styleDialogField(f);
        return f;
    }

    private IntegerField dialogIntField(String label, int value) {
        IntegerField f = new IntegerField(label);
        f.setValue(value);
        f.setWidthFull();
        f.addClassName("dialog-field");
        styleDialogField(f);
        return f;
    }

    private void styleDialogField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("--lumo-body-text-color", "white")
                .set("--lumo-secondary-text-color", "rgba(184,201,191,0.7)")
                .set("--lumo-primary-color", "#8fb08a")
                .set("--lumo-primary-text-color", "#8fb08a")
                .set("--lumo-base-color", "#182218")
                .set("--lumo-contrast-5pct", "rgba(255,255,255,0.05)")
                .set("--lumo-contrast-10pct", "rgba(143,176,138,0.25)")
                .set("--vaadin-input-field-background", "rgba(255,255,255,0.05)")
                .set("--vaadin-input-field-border-color", "rgba(143,176,138,0.25)")
                .set("--vaadin-input-field-value-color", "white")
                .set("--vaadin-input-field-placeholder-color", "rgba(184,201,191,0.4)");
    }

    private Button dialogCancelBtn(String label, Dialog d) {
        Button b = new Button(label, e -> d.close());
        b.getStyle()
                .set("background", "rgba(255,255,255,0.05)")
                .set("color", "rgba(184,201,191,0.8)")
                .set("border", "1px solid rgba(255,255,255,0.1)")
                .set("border-radius", "8px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "600")
                .set("flex", "1")
                .set("height", "40px")
                .set("cursor", "pointer");
        return b;
    }

    private Button dialogSaveBtn(String label) {
        Button b = new Button(label);
        b.getStyle()
                .set("background", "linear-gradient(135deg, #4d8f4d, #8fb08a)")
                .set("color", "#0a1f0f")
                .set("border", "none")
                .set("border-radius", "8px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-weight", "700")
                .set("flex", "2")
                .set("height", "40px")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 12px rgba(78,143,78,0.3)");
        return b;
    }

    // ════════════════════════════════════════════════════════════════════════
    // NOTIFICATION HELPERS
    // ════════════════════════════════════════════════════════════════════════
    private void err(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    private void ok(String msg) {
        Notification n = Notification.show(msg, 3000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
    private void info(String msg) {
        Notification n = Notification.show(msg, 3000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    // ════════════════════════════════════════════════════════════════════════
    // VISUAL HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private String logoSvg(int size) {
        return String.format(
            "<svg width='%d' height='%d' viewBox='0 0 120 120' xmlns='http://www.w3.org/2000/svg'>" +
            "<polygon points='14,48 58,22 58,82 14,108' fill='#1e3460'/>" +
            "<polygon points='58,22 104,48 104,108 58,82' fill='#3a9898'/>" +
            "<polygon points='14,48 58,22 104,48 58,74' fill='#5dcfca'/>" +
            "<circle cx='26' cy='103' r='16' fill='#6aab6a'/>" +
            "<text x='26' y='108.5' text-anchor='middle' " +
            "font-family='Inter,Arial Black,sans-serif' font-size='11' font-weight='900' fill='white'>24</text>" +
            "</svg>", size, size);
    }

    private String svgStr(String pathData, String size, String fill) {
        return "<svg width='" + size + "' height='" + size +
               "' fill='" + fill + "' viewBox='0 0 24 24' xmlns='http://www.w3.org/2000/svg'>" +
               pathData + "</svg>";
    }

    private String categoryGradient(String cat) {
        if (cat == null) return "linear-gradient(135deg, #1e2e20, #2a3e2c)";
        return switch (cat.toLowerCase()) {
            case "elektronik", "electronics"  -> "linear-gradient(135deg, #1a2a4a, #2a3a6a)";
            case "audio"                      -> "linear-gradient(135deg, #2a1a3a, #3a2a4a)";
            case "photography", "fotografi"   -> "linear-gradient(135deg, #1a3a2a, #2a4a3a)";
            case "furniture", "furnitur"      -> "linear-gradient(135deg, #3a2a1a, #4a3a2a)";
            default                           -> "linear-gradient(135deg, #1e2e2a, #2a3e36)";
        };
    }

    private String categoryBgColor(String cat) {
        if (cat == null) return "linear-gradient(145deg, #2e4a36, #1e3228)";
        return switch (cat.toLowerCase()) {
            case "elektronik", "electronics"  -> "linear-gradient(145deg, #1d3558, #142545)";
            case "audio"                      -> "linear-gradient(145deg, #3a1f50, #27133c)";
            case "photography", "fotografi"   -> "linear-gradient(145deg, #1a4435, #0e2e22)";
            case "furniture", "furnitur"      -> "linear-gradient(145deg, #4a3320, #2e1e10)";
            case "jaringan", "network"        -> "linear-gradient(145deg, #1e3a4a, #0e2535)";
            default                           -> "linear-gradient(145deg, #2a3e30, #1a2a20)";
        };
    }

    private String categoryIconPath(String cat) {
        if (cat == null) return ICON_BOX;
        return switch (cat.toLowerCase()) {
            case "elektronik", "electronics"  -> ICON_MONITOR;
            case "audio"                      -> ICON_MIC;
            case "photography", "fotografi"   -> ICON_CAMERA;
            case "furniture", "furnitur"      -> ICON_BOX;
            default                           -> ICON_BOX;
        };
    }

    private void injectDashboardCss() {
        String css =
            "body,html{margin:0;padding:0;}" +
            "#add-asset-btn:hover{" +
            "  transform:translateY(-2px)!important;" +
            "  box-shadow:0 8px 20px rgba(78,143,78,0.45)!important;" +
            "}" +
            "#notif-btn:hover{background:rgba(0,0,0,0.07)!important;}" +
            "#sidebar-user-info:hover{background:rgba(255,255,255,0.06);border-radius:12px;}" +
            ".asset-card:hover{" +
            "  transform:translateY(-4px)!important;" +
            "  box-shadow:0 14px 32px rgba(0,0,0,0.13)!important;" +
            "}" +
            "vaadin-text-field.dashboard-search{width:100%;}" +
            "vaadin-text-field.dashboard-search::part(input-field){" +
            "  background:#f0f4f1!important;" +
            "  border:1px solid rgba(0,0,0,0.10)!important;" +
            "  border-radius:22px!important;" +
            "  padding-left:12px!important;" +
            "}" +
            "vaadin-text-field.dashboard-search::part(value){color:#1a2e1a!important;}" +
            "vaadin-text-field.dashboard-search::part(placeholder){color:rgba(60,100,60,0.45)!important;}";
        getElement().executeJs(
            "if(!document.getElementById('p24-dash-css')){" +
            "  const s=document.createElement('style');" +
            "  s.id='p24-dash-css';" +
            "  s.textContent=$0;" +
            "  document.head.appendChild(s);" +
            "}", css);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SVG ICON PATH DATA
    // ════════════════════════════════════════════════════════════════════════
    private static final String ICON_DASHBOARD =
        "<rect x='3' y='3' width='7' height='7' rx='1'/><rect x='14' y='3' width='7' height='7' rx='1'/>" +
        "<rect x='3' y='14' width='7' height='7' rx='1'/><rect x='14' y='14' width='7' height='7' rx='1'/>";
    private static final String ICON_BOX =
        "<path d='M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z'/>" +
        "<polyline points='3.27 6.96 12 12.01 20.73 6.96'/><line x1='12' y1='22.08' x2='12' y2='12'/>";
    private static final String ICON_CLIPBOARD =
        "<path d='M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2'/>" +
        "<rect x='8' y='2' width='8' height='4' rx='1' ry='1'/>" +
        "<line x1='9' y1='13' x2='15' y2='13'/><line x1='9' y1='17' x2='15' y2='17'/>";
    private static final String ICON_CHECK =
        "<path d='M22 11.08V12a10 10 0 1 1-5.93-9.14'/>" +
        "<polyline points='22 4 12 14.01 9 11.01'/>";
    private static final String ICON_SETTINGS =
        "<circle cx='12' cy='12' r='3'/>" +
        "<path d='M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z'/>";
    private static final String ICON_LOGOUT =
        "<path d='M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4'/>" +
        "<polyline points='16 17 21 12 16 7'/><line x1='21' y1='12' x2='9' y2='12'/>";
    private static final String ICON_SEARCH =
        "<circle cx='11' cy='11' r='8'/><line x1='21' y1='21' x2='16.65' y2='16.65'/>";
    private static final String ICON_BELL =
        "<path d='M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9'/>" +
        "<path d='M13.73 21a2 2 0 0 1-3.46 0'/>";
    private static final String ICON_CHECK_CIRC =
        "<path d='M22 11.08V12a10 10 0 1 1-5.93-9.14'/>" +
        "<polyline points='22 4 12 14.01 9 11.01'/>";
    private static final String ICON_ARROW_UP =
        "<line x1='12' y1='19' x2='12' y2='5'/><polyline points='5 12 12 5 19 12'/>";
    private static final String ICON_ACTIVITY =
        "<polyline points='22 12 18 12 15 21 9 3 6 12 2 12'/>";
    private static final String ICON_FOLDER =
        "<path d='M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z'/>";
    private static final String ICON_TAG =
        "<path d='M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z'/>" +
        "<line x1='7' y1='7' x2='7.01' y2='7'/>";
    private static final String ICON_EDIT =
        "<path d='M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7'/>" +
        "<path d='M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z'/>";
    private static final String ICON_EYE =
        "<path d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z'/>" +
        "<circle cx='12' cy='12' r='3'/>";
    private static final String ICON_STAR =
        "<polygon points='12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2'/>";
    private static final String ICON_CLOCK =
        "<circle cx='12' cy='12' r='10'/><polyline points='12 6 12 12 16 14'/>";
    private static final String ICON_MONITOR =
        "<rect x='2' y='3' width='20' height='14' rx='2' ry='2'/>" +
        "<line x1='8' y1='21' x2='16' y2='21'/><line x1='12' y1='17' x2='12' y2='21'/>";
    private static final String ICON_MIC =
        "<path d='M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z'/>" +
        "<path d='M19 10v2a7 7 0 0 1-14 0v-2'/><line x1='12' y1='19' x2='12' y2='23'/>" +
        "<line x1='8' y1='23' x2='16' y2='23'/>";
    private static final String ICON_CAMERA =
        "<path d='M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z'/>" +
        "<circle cx='12' cy='13' r='4'/>";
}
