# Yang gw benerin dari project lu

Gw bongkar zip lu, ternyata ada beberapa hal yang bikin project ini gak bakal jalan
(compile error) atau bakal jalan tapi salah arah (nyambung ke database yang salah).
Ini daftarnya:

## 1. Bug import di RegisterView.java (compile error)
Lu udah bener rename package dari `com.property24` jadi `property24`, tapi ada satu
baris import yang kelewat:
```java
import com.property24.views.login.LoginView;   // <- ini salah, package-nya udah gak ada
```
Gw ganti jadi:
```java
import property24.views.login.LoginView;
```
Ini penyebab utama project gak bisa di-compile.

## 2. Lombok belum ada di pom.xml
Entity lu (`Barang`, `User`, dst) pakai `@Getter @Setter @NoArgsConstructor` dari Lombok,
tapi dependency Lombok-nya gak ada di `pom.xml` lu. Otomatis semua annotation itu error
"cannot find symbol". Gw tambahin:
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```
**Ingat:** install juga plugin Lombok di IntelliJ (Settings → Plugins → cari "Lombok"),
terus enable annotation processing (Settings → Build → Compiler → Annotation Processors).

## 3. Project lu masih nyambung ke H2, bukan MySQL lu
Project awal dari start.vaadin.com itu demo-nya pakai database H2 (nempel di memori,
ilang tiap restart), bukan MySQL yang udah lu buat. `application.properties` lu masih
config bawaan itu (`ddl-auto=update`, gak ada `datasource.url` sama sekali). Gw ganti
jadi:
- Datasource ke MySQL (lu tinggal ganti `NAMA_DB_LU` sesuai nama db lu)
- `ddl-auto=validate` (Hibernate cuma ngecek cocok apa nggak, gak akan ubah/drop tabel lu)
- Tambahin dependency `mysql-connector-j`, dan buang dependency `h2` (udah gak kepake)

## 4. Ada 2 "aplikasi" nabrak dalam 1 project
Project start.vaadin.com bawaannya punya:
- `com.example.application.Application` (main class demo, ada `MainLayout` + fitur
  contoh "Task" pakai H2)
- Punya lu sendiri: `property24.Property24Application`

Dua-duanya sama-sama `@SpringBootApplication`, dan masing-masing cuma "melihat" package
dia sendiri. Ini bikin bingung karena gak jelas yang mana yang jalan, dan fitur contoh
"Task" itu nyoba connect ke database yang gak match sama tabel lu. Gw **hapus total**
folder demo itu (`com/example/application/**` di main dan test) supaya project lu cuma
berisi punya lu sendiri, gak ada yang nabrak.

## 5. .git dan .idea gw buang dari zip ini
Biar bersih dan gak bentrok sama folder project lu yang sekarang. IntelliJ bakal bikin
ulang `.idea` otomatis pas lu buka project. Kalau lu udah punya git history yang mau
dipertahanin, jangan extract-timpa folder `.git` lu — cukup timpa file-file kodenya aja.

---

## Cara pasang
1. Extract zip ini, **timpa** folder project lu yang lama dengan isi ini (atau kalau
   ragu, bandingin dulu file per file).
2. Buka `src/main/resources/application.properties`, ganti `NAMA_DB_LU` sama username/password
   MySQL lu.
3. Install plugin Lombok + enable annotation processing (poin #2 di atas) kalau belum.
4. Reload Maven project di IntelliJ (klik ikon gajah/refresh di panel Maven) biar dependency baru ke-download.
5. Run `Property24Application.java` → buka `http://localhost:8080/login`.

Kalau masih ada error pas di-run, screenshot aja error-nya (biasanya muncul merah di
tab "Run" bawah IntelliJ), kirim ke gw — lebih gampang gw bantu dari situ.
