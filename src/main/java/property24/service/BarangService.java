package property24.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import property24.entity.Barang;
import property24.entity.Kategori;
import property24.entity.Ruangan;
import property24.repository.BarangRepository;
import property24.repository.KategoriRepository;
import property24.repository.RuanganRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarangService {

    private final BarangRepository barangRepository;
    private final KategoriRepository kategoriRepository;
    private final RuanganRepository ruanganRepository;

    // ─── READ ─────────────────────────────────────────────────────────────────

    public List<Barang> getAllBarang() {
        return barangRepository.findAll();
    }

    public List<Barang> getByStatus(Barang.Status status) {
        return barangRepository.findByStatus(status);
    }

    public List<Barang> getByKategori(String namaKategori) {
        return barangRepository.findByKategoriNamaKategoriContainingIgnoreCase(namaKategori);
    }

    public List<Barang> search(String query) {
        if (query == null || query.isBlank()) return getAllBarang();
        return barangRepository.search(query.trim());
    }

    public Barang getById(Long id) {
        return barangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Barang tidak ditemukan: " + id));
    }

    // ─── COUNTS ───────────────────────────────────────────────────────────────

    public long countTotal() {
        return barangRepository.count();
    }

    public long countByStatus(Barang.Status status) {
        return barangRepository.countByStatus(status);
    }

    public long countActive() {
        return countByStatus(Barang.Status.tersedia) + countByStatus(Barang.Status.dipinjam);
    }

    public long countKategori() {
        return kategoriRepository.count();
    }

    // ─── KATEGORI & RUANGAN ───────────────────────────────────────────────────

    public List<Kategori> getAllKategori() {
        return kategoriRepository.findAll();
    }

    public List<Ruangan> getAllRuangan() {
        return ruanganRepository.findAll();
    }

    // ─── WRITE ────────────────────────────────────────────────────────────────

    public Barang save(Barang barang) {
        return barangRepository.save(barang);
    }

    public void delete(Long id) {
        barangRepository.deleteById(id);
    }
}
