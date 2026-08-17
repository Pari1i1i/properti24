package property24.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property24.entity.Barang;
import property24.entity.RiwayatPerbaikan;
import property24.entity.User;
import property24.repository.BarangRepository;
import property24.repository.RiwayatPerbaikanRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerbaikanService {

    private final RiwayatPerbaikanRepository perbaikanRepository;
    private final BarangRepository barangRepository;

    // ── READ ─────────────────────────────────────────────────────────────────

    public List<RiwayatPerbaikan> getAll() {
        return perbaikanRepository.findAllByOrderByIdDesc();
    }

    public List<RiwayatPerbaikan> getByStatus(RiwayatPerbaikan.StatusPerbaikan status) {
        return perbaikanRepository.findByStatusPerbaikanOrderByIdDesc(status);
    }

    public long countProses() {
        return perbaikanRepository.findByStatusPerbaikanOrderByIdDesc(
                RiwayatPerbaikan.StatusPerbaikan.proses).size();
    }

    // ── WRITE ─────────────────────────────────────────────────────────────────

    /**
     * Catat kerusakan baru. Otomatis ubah status barang jadi 'rusak'.
     */
    @Transactional
    public RiwayatPerbaikan laporKerusakan(Barang barang, User dilaporkanOleh,
                                           String catatan, String teknisiVendor,
                                           BigDecimal biaya) {
        RiwayatPerbaikan rp = new RiwayatPerbaikan();
        rp.setBarang(barang);
        rp.setDilaporkanOleh(dilaporkanOleh);
        rp.setCatatan(catatan);
        rp.setTeknisiVendor(teknisiVendor);
        rp.setBiaya(biaya != null ? biaya : BigDecimal.ZERO);
        rp.setStatusPerbaikan(RiwayatPerbaikan.StatusPerbaikan.proses);

        // Tandai barang sebagai rusak
        barang.setStatus(Barang.Status.rusak);
        barangRepository.save(barang);

        return perbaikanRepository.save(rp);
    }

    /**
     * Update status perbaikan ke 'diperbaiki' (sedang diperbaiki oleh teknisi).
     * Status barang menjadi 'diperbaiki'.
     */
    @Transactional
    public void mulaiPerbaikan(RiwayatPerbaikan rp, String teknisiVendor) {
        rp.setTeknisiVendor(teknisiVendor);
        rp.setStatusPerbaikan(RiwayatPerbaikan.StatusPerbaikan.proses);
        rp.getBarang().setStatus(Barang.Status.diperbaiki);
        barangRepository.save(rp.getBarang());
        perbaikanRepository.save(rp);
    }

    /**
     * Tandai perbaikan selesai. Status barang kembali ke 'tersedia'.
     */
    @Transactional
    public void selesaikanPerbaikan(RiwayatPerbaikan rp, BigDecimal biayaAkhir, String catatanSelesai) {
        rp.setStatusPerbaikan(RiwayatPerbaikan.StatusPerbaikan.selesai);
        rp.setTglSelesai(LocalDateTime.now());
        if (biayaAkhir != null) rp.setBiaya(biayaAkhir);
        if (catatanSelesai != null && !catatanSelesai.isBlank()) rp.setCatatan(catatanSelesai);

        rp.getBarang().setStatus(Barang.Status.tersedia);
        barangRepository.save(rp.getBarang());
        perbaikanRepository.save(rp);
    }

    /**
     * Batalkan laporan perbaikan. Status barang kembali ke 'tersedia'.
     */
    @Transactional
    public void batalkanPerbaikan(RiwayatPerbaikan rp) {
        rp.setStatusPerbaikan(RiwayatPerbaikan.StatusPerbaikan.dibatalkan);
        rp.getBarang().setStatus(Barang.Status.tersedia);
        barangRepository.save(rp.getBarang());
        perbaikanRepository.save(rp);
    }

    @Transactional
    public void save(RiwayatPerbaikan rp) {
        perbaikanRepository.save(rp);
    }
}
