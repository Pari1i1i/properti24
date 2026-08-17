package property24.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingDatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initDatabaseSchema() {
        // Fix column types so they match the updated entity
        try {
            jdbcTemplate.execute(
                "ALTER TABLE booking MODIFY COLUMN tgl_rencana_ambil DATETIME NULL"
            );
            log.info("BookingDatabaseInitializer: tgl_rencana_ambil updated to DATETIME.");
        } catch (Exception e) {
            log.warn("BookingDatabaseInitializer (tgl_rencana_ambil): {}", e.getMessage());
        }

        try {
            jdbcTemplate.execute(
                "ALTER TABLE booking MODIFY COLUMN status VARCHAR(50) NOT NULL"
            );
            log.info("BookingDatabaseInitializer: status column updated to VARCHAR(50).");
        } catch (Exception e) {
            log.warn("BookingDatabaseInitializer (status): {}", e.getMessage());
        }


        try {
            int updated = jdbcTemplate.update(
                "UPDATE booking SET status = 'menunggu_persetujuan' WHERE status = 'menunggu'"
            );
            if (updated > 0) {
                log.info("BookingDatabaseInitializer: Migrated {} row(s) from status 'menunggu' -> 'menunggu_persetujuan'.", updated);
            }
        } catch (Exception e) {
            log.warn("BookingDatabaseInitializer (migrate menunggu): {}", e.getMessage());
        }


        try {
            jdbcTemplate.execute(
                "ALTER TABLE booking MODIFY COLUMN tgl_booking DATETIME NOT NULL"
            );
        } catch (Exception e) {
            log.warn("BookingDatabaseInitializer (tgl_booking): {}", e.getMessage());
        }

        // ── Auto-heal & synchronize barang status ─────────────────────────────
        try {
            // Fix any invalid or null status values in barang table
            jdbcTemplate.update(
                "UPDATE barang SET status = 'tersedia' WHERE status IS NULL OR status NOT IN ('tersedia', 'dipinjam', 'rusak', 'diperbaiki')"
            );

            // Reset barang status to 'tersedia' if it is marked 'dipinjam' but has no active loan in pinjaman_detail
            int resetCount = jdbcTemplate.update(
                "UPDATE barang b SET b.status = 'tersedia' " +
                "WHERE b.status = 'dipinjam' " +
                "AND b.id NOT IN (" +
                "  SELECT pd.barang_id FROM pinjaman_detail pd " +
                "  JOIN pinjaman p ON pd.pinjaman_id = p.id " +
                "  WHERE p.status_pinjaman = 'dipinjam'" +
                ")"
            );
            if (resetCount > 0) {
                log.info("BookingDatabaseInitializer: Reset {} barang status back to 'tersedia'.", resetCount);
            }
        } catch (Exception e) {
            log.warn("BookingDatabaseInitializer (auto-heal status): {}", e.getMessage());
        }
    }
}
