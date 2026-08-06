# DESIGN SYSTEM & ARCHITECTURE SPECIFICATION (DESIGN.MD)
**Project Name:** Property 24 - Asset Management System  
**Technology Stack:** Java 21, Spring Boot 3.x, Vaadin Flow 24+, Spring Data JPA, Spring Security, MySQL/MariaDB  
**Target Platform:** Responsive Web (Desktop & Mobile-first Layouts)

---

## 1. Executive Summary & Architecture Overview

`Property 24` is an enterprise-grade Asset Management System designed for tracking, requesting, approving, and maintaining organizational hardware and equipment assets. The application utilizes **Vaadin Flow** with **Spring Boot** to deliver a seamless, component-driven Single-Page Application (SPA) experience with server-side state management and full UI reactivity without writing client-side JavaScript.

---

## 2. Visual Design & UI System

### 2.1 Color Palette & Theme Tokens
The UI is styled using Vaadin's Lumo Theme custom CSS variables, matching the visual identity shown in the design assets:

```css
:root {
  /* Primary & Brand Colors */
  --p24-green-dark: #1E382B;       /* Deep Forest Green (Sidebar & Dark Cards) */
  --p24-green-medium: #2C4E3C;     /* Medium Forest Green */
  --p24-green-accent: #6C9475;     /* Sage / Mint Green (Active Nav & Primary Buttons) */
  --p24-green-light: #E8F0EB;      /* Light Tint Background for Badges */

  /* Surface & Background Colors */
  --p24-bg-main: #F4F6F5;          /* Main Content Area */
  --p24-bg-card: #FFFFFF;          /* Surface Cards */
  --p24-bg-sidebar: #233E30;       /* Sidebar Navy/Dark Green */

  /* Status Colors */
  --p24-status-available: #28A745; /* Success Green Badge */
  --p24-status-borrowed: #E67E22;  /* Warning Orange Badge */
  --p24-status-pending: #17A2B8;   /* Info Blue/Cyan Badge */
  --p24-status-repair: #DC3545;    /* Danger Red Badge */

  /* Neutral & Text */
  --p24-text-primary: #1A1A1A;
  --p24-text-secondary: #666666;
  --p24-text-muted: #8C8C8C;
  --p24-border-color: #E2E8F0;

  /* Geometry */
  --lumo-border-radius-m: 12px;
  --lumo-border-radius-l: 18px;
}
```

### 2.2 Typography
- **Primary Font:** Inter / system-ui (`sans-serif`).
- **Heading Styles:** Bold, uppercase section titles (`LETTER-SPACING: 1px`).
- **Rating Display:** Custom Star Rating Component (1-5 filled yellow stars `★`).

### 2.3 Component Mapping (Vaadin Components)
| Design Element | Vaadin Component | Styling Notes |
| :--- | :--- | :--- |
| **Sidebar Navigation** | `AppLayout` + `SideNav` / `SideNavItem` | Dark background (`#233E30`), rounded active indicators |
| **Asset Grid / Catalog** | `FlexLayout` / `VirtualList` of `Card` Views | Responsive auto-fit grid (`repeat(auto-fill, minmax(240px, 1fr))`) |
| **Status Badges** | `Span` with `theme="badge"` | Customized colors per state (Available, Borrowed, Pending) |
| **Asset Image Dropper** | `Upload` + `MemoryBuffer` | Drag-and-drop file upload target with custom drop zone |
| **Forms & Filters** | `TextField`, `ComboBox`, `Select`, `DatePicker` | Rounded borders (`border-radius: 8px`) |
| **Modals & Drawers** | `Dialog` / `SideDrawer` | Rounded corner overlays for item checkout |

---

## 3. Database Schema & Entity Design (ERD Mapping)

Based on the Relational ERD specification, the JPA domain entities are structured as follows:

```
                          ┌──────────────┐
                          │     user     │
                          └──────┬───────┘
                                 │ 1
                                 │
                                 │ N
                          ┌──────┴───────┐
                          │   pinjaman   │
                          └──────┬───────┘
                                 │ 1
                                 │
                                 │ N
┌──────────────┐          ┌──────┴───────┐          ┌──────────────┐
│   ruangan    ├──────────┤pinjaman_detail├──────────┤    barang    │
└──────────────┘ 1      N └──────┬───────┘ N      1 └──────┬───────┘
                                 │ 1                       │ 1
                                 │                         │
                                 │ 1                       │ N
                          ┌──────┴───────┐          ┌──────┴───────┐
                          │ pengembalian │          │   kategori   │
                          └──────────────┘          └──────────────┘
                                                           │ 1
                                                           │ N
                                                    ┌──────┴──────────────┐
                                                    │ riwayat_perbaikan   │
                                                    └─────────────────────┘
```

### 3.1 Entity Specifications

1. **`User` Entity (`user`)**
   - `id`: `BIGINT` (PK, Auto-increment)
   - `username`: `VARCHAR(50)` (NOT NULL, UNIQUE)
   - `password`: `VARCHAR(255)` (NOT NULL, BCrypt Encrypted)
   - `namaLengkap`: `VARCHAR(100)`
   - `kelas`: `VARCHAR(50)`
   - `role`: `ENUM('ADMIN', 'USER')`
   - `dibuatPada`: `TIMESTAMP`

2. **`Kategori` Entity (`kategori`)**
   - `id`: `BIGINT` (PK)
   - `namaKategori`: `VARCHAR(100)` (NOT NULL)

3. **`Ruangan` Entity (`ruangan`)**
   - `id`: `BIGINT` (PK)
   - `namaRuangan`: `VARCHAR(100)` (NOT NULL)

4. **`Barang` Entity (`barang`)**
   - `id`: `BIGINT` (PK)
   - `kodeBarang`: `VARCHAR(50)` (e.g., `AST-001`)
   - `kategori`: `@ManyToOne Kategori`
   - `ruangan`: `@ManyToOne Ruangan`
   - `namaBarang`: `VARCHAR(150)` (NOT NULL)
   - `bintangSaatIni`: `INT` (Rating 1-5)
   - `deskripsiBintang`: `TEXT` (Condition note e.g., "Layak pakai")
   - `stock`: `INT`
   - `status`: `ENUM('AVAILABLE', 'BORROWED', 'PENDING_RETURN', 'IN_REPAIR')`
   - `fotoBarang`: `VARCHAR(255)`
   - `diperbaruiPada`: `TIMESTAMP`

5. **`Pinjaman` Entity (`pinjaman`)**
   - `id`: `BIGINT` (PK)
   - `user`: `@ManyToOne User`
   - `fotoPeminjam`: `VARCHAR(255)`
   - `tglPinjam`: `TIMESTAMP`
   - `statusPinjaman`: `ENUM('SUBMITTED', 'ACTIVE', 'RETURN_REQUESTED', 'COMPLETED')`

6. **`PinjamanDetail` Entity (`pinjaman_detail`)**
   - `id`: `BIGINT` (PK)
   - `pinjaman`: `@ManyToOne Pinjaman`
   - `ruangan`: `@ManyToOne Ruangan`
   - `barang`: `@ManyToOne Barang`
   - `tujuanPinjam`: `TEXT`
   - `tglRencanaKembali`: `DATE`
   - `sudahDikembalikan`: `BOOLEAN`

7. **`Pengembalian` Entity (`pengembalian`)**
   - `id`: `BIGINT` (PK)
   - `pinjamanDetail`: `@OneToOne PinjamanDetail`
   - `fotoPengembalian`: `VARCHAR(255)` (Mandatory upload proof)
   - `tglKembali`: `TIMESTAMP`
   - `statusAcc`: `ENUM('PENDING', 'APPROVED', 'REJECTED')`
   - `adminAcc`: `@ManyToOne User`
   - `catatanAdmin`: `TEXT`
   - `catatanKondisi`: `TEXT`

8. **`RiwayatPerbaikan` Entity (`riwayat_perbaikan`)**
   - `id`: `BIGINT` (PK)
   - `barang`: `@ManyToOne Barang`
   - `dilaporkanOleh`: `@ManyToOne User`
   - `tglMasuk`: `TIMESTAMP`
   - `tglSelesai`: `TIMESTAMP`
   - `teknisiVendor`: `VARCHAR(150)`
   - `biaya`: `DECIMAL(12,2)`
   - `catatan`: `TEXT`
   - `statusPerbaikan`: `ENUM('IN_PROGRESS', 'RESOLVED', 'SCRAPPED')`

---

## 4. Application Workflows & State Machine (Flowchart Realization)

```
                       ┌─────────────────────────┐
                       │       User Login        │
                       └────────────┬────────────┘
                                    │
                           ┌────────┴────────┐
                           │ Auth Successful?│
                           └───────┬─┬───────┘
                           Gagal   │ │   Berhasil
              ┌────────────────────┘ └────────────────────┐
              ▼                                           ▼
      [Display Error]                          ┌──────────┴──────────┐
                                               │   Check User Role   │
                                               └──────────┬──────────┤
                                                  Admin   │          │ User
                                         ┌────────────────┘          └────────────────┐
                                         ▼                                            ▼
                             ┌───────────────────────┐                    ┌───────────────────────┐
                             │    Admin Dashboard    │                    │     User Catalog      │
                             └───────────┬───────────┘                    └───────────┬───────────┘
                                         │                                            │
                             ┌───────────┴───────────┐                    ┌───────────┴───────────┐
                             │ Review Return Request │                    │ Select Asset & Borrow │
                             └───────────┬───────────┘                    └───────────┬───────────┘
                                         │                                            │
                             ┌───────────┴───────────┐                    ┌───────────┴───────────┐
                             │ Verify Proof Photo &  │                    │ Upload Proof & Submit │
                             │  Condition Rating     │                    │     Borrow Request    │
                             └───────────┬───────────┘                    └───────────┬───────────┘
                                         │                                            │
                             ┌───────────┴───────────┐                    ┌───────────┴───────────┐
                             │ Approve Return Request│                    │ Return Item Process:  │
                             │ (Status -> AVAILABLE) │                    │ Upload Photo Required │
                             └───────────────────────┘                    └───────────────────────┘
```

### 4.1 Asset Lifecycle States
- **`AVAILABLE`**: Asset is present in inventory and can be selected by users.
- **`BORROWED`**: Active loan confirmed by user submission.
- **`PENDING_RETURN`**: User uploaded return proof photo and submitted return form. Awaiting Admin verification.
- **`IN_REPAIR`**: Under maintenance or technician servicing.

---

## 5. UI Screen Breakdown & Vaadin View Implementation

### 5.1 Login & Register Screen (`/login`, `/register`)
- **Layout:** Split layout on Desktop (50/50 Green & Light background). Stacked single column on Mobile.
- **Left/Top Brand Section:** Prominent "PROPERTY 24" logo with dark green background motif (`#1E382B`).
- **Right/Bottom Form Card:**
  - Dark Green container card (`#233E30`) with rounded corners (`18px`).
  - Inputs: `TextField` for Username/Full Name/Email, `PasswordField` for Password with eye-toggle.
  - "Remember me" `Checkbox` and "Forgot Password?" link.
  - Primary Submit Action: Rounded pill button with hover transitions.

### 5.2 Admin Asset Dashboard (`/admin/dashboard`)
- **Top Header:** Search bar ("Search assets, tags, locations..."), Notification Icon, "+ Add New Asset" Button.
- **Metric Summary Cards:**
  1. **Total Assets:** Counter (e.g., `8`), Badge (`+2 new`).
  2. **Available:** Counter (e.g., `4`), Badge (`Ready`).
  3. **Borrowed:** Counter (e.g., `4`), Badge (`Active`).
  4. **Categories:** Counter (e.g., `3`), Badge (`Types`).
- **Category Filter Pills:** Horizontal pill group (`All`, `Electronics`, `Audio`, `Photography`).
- **Asset Grid Cards:** Displaying Thumbnail, Badge (`Available` / `Borrowed`), Code (e.g., `AST-001`), Title, Star Rating (`1-5`), Condition text, and borrower details if currently on loan.

### 5.3 Add New Asset Page (`/admin/assets/new`)
- **Top Bar:** Back button + Action Title.
- **Image Upload Dropzone:** Centered circular/square file uploader supporting `PNG/JPG` dragging.
- **Form Fields:** `Name`, `Category` Dropdown, `Description` TextArea, `Rating` star selector widget.
- **Action:** Solid green `SAVE` button.

### 5.4 Approve by Admin / Return Request Inspection (`/admin/approvals`)
- **Header:** "Accepted access" / Pending Return queue count.
- **Detail View:**
  - **Left Column:** Original Asset details (Serial No, Borrower Name, Return Date, Borrow Duration, Location, Condition Status).
  - **Right Column:** "Proof of Return (User Upload)" - Large photo display submitted by borrower as evidence of return.
  - **Assessment Block:** Admin Condition Rating stars input + Notes textarea.
  - **Action Buttons:** `BACK`, `PENDING ADMIN APPROVAL` / `APPROVE RETURN`.

### 5.5 Mobile User Catalog & Borrow Request (`/user/dashboard`, `/user/borrow`)
- **Mobile Header:** Brand Logo top bar + Profile Avatar + Hamburger Drawer toggle.
- **Filter Pills:** Horizontal scrollable categories (`Audio`, `Photography`, `Electronics`, `All`).
- **Borrow Form Flow:**
  1. Selected Items list with removable tags.
  2. Dropdown: `Ruang Pemakaian` (Usage Room).
  3. Textarea: `Tujuan Peminjaman` (Loan Purpose).
  4. Date Pickers: `Tanggal Pinjam` & `Tanggal Kembali`.
  5. File Upload: `Upload Foto Bukti` (Mandatory dropzone). Validation disables submit button until photo is present.
  6. Submit button: "Kirim Pengembalian / Submit Peminjaman".

---

## 6. Project Structure & Code Architecture

```
src/main/java/com/property24/
├── config/
│   ├── SecurityConfig.java
│   └── VaadinAppShell.java
├── entity/
│   ├── User.java
│   ├── Barang.java
│   ├── Kategori.java
│   ├── Ruangan.java
│   ├── Pinjaman.java
│   ├── PinjamanDetail.java
│   ├── Pengembalian.java
│   └── RiwayatPerbaikan.java
├── repository/
│   ├── UserRepository.java
│   ├── BarangRepository.java
│   ├── PinjamanRepository.java
│   └── PengembalianRepository.java
├── service/
│   ├── AuthService.java
│   ├── AssetService.java
│   └── BorrowService.java
└── views/
    ├── components/
    │   ├── AssetCard.java
    │   ├── StarRating.java
    │   └── StatusBadge.java
    ├── admin/
    │   ├── AdminDashboardView.java
    │   ├── AddAssetView.java
    │   └── ApprovalView.java
    ├── user/
    │   ├── UserCatalogView.java
    │   ├── BorrowFormView.java
    │   └── UserBorrowedItemsView.java
    └── auth/
        ├── LoginView.java
        └── RegisterView.java
```

---

## 7. Security & Business Logic Rules

1. **Role-Based Access Control (RBAC):**
   - Routes starting with `/admin/*` require `ROLE_ADMIN`.
   - Routes starting with `/user/*` require `ROLE_USER` or `ROLE_ADMIN`.
2. **Proof Upload Enforcement:**
   - A loan return process **cannot be submitted** without an uploaded proof photo (`foto_pengembalian`). The UI validation must show a warning banner: *"Foto bukti pengembalian wajib diisi"* if missing.
3. **Asset Availability Locking:**
   - When an asset is added to an active loan request, its status transitions to `BORROWED` to prevent double-booking.
   - Upon return request, its status transitions to `PENDING_RETURN`.
   - Upon admin approval, its status reverts to `AVAILABLE`.

---
*End of Design Specification Document.*
