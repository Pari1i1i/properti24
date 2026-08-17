package property24.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property24.entity.*;
import property24.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PinjamanService {

    private final PinjamanRepository pinjamanRepository;
    private final PinjamanDetailRepository pinjamanDetailRepository;
    private final PengembalianRepository pengembalianRepository;
    private final BarangRepository barangRepository;

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<PinjamanDetail> getDetailsByUser(User user) {
        return pinjamanDetailRepository.findByPinjamanUserId(user.getId());
    }

    public List<PinjamanDetail> getActiveDetailsByUser(User user) {
        return pinjamanDetailRepository.findByPinjamanUserIdAndSudahDikembalikan(user.getId(), false);
    }

    public Optional<Pengembalian> getPengembalianForDetail(PinjamanDetail detail) {
        return pengembalianRepository.findByPinjamanDetailId(detail.getId());
    }

    // ── WRITE ─────────────────────────────────────────────────────────────────

    @Transactional
    public Pinjaman createPinjaman(User user, List<Barang> items, Ruangan ruangan,
                                   String tujuan, LocalDate tglRencanaKembali, String fotoPeminjam) {
        Pinjaman pinjaman = new Pinjaman();
        pinjaman.setUser(user);
        pinjaman.setFotoPeminjam(fotoPeminjam);
        pinjaman.setStatusPinjaman(Pinjaman.StatusPinjaman.dipinjam);
        pinjaman = pinjamanRepository.save(pinjaman);

        for (Barang barang : items) {
            PinjamanDetail detail = new PinjamanDetail();
            detail.setPinjaman(pinjaman);
            detail.setBarang(barang);
            detail.setRuangan(ruangan);
            detail.setTujuanPinjam(tujuan);
            detail.setTglRencanaKembali(tglRencanaKembali);
            detail.setSudahDikembalikan(false);
            pinjamanDetailRepository.save(detail);

            // Auto-update barang status to dipinjam
            barang.setStatus(Barang.Status.dipinjam);
            barangRepository.save(barang);
        }

        return pinjaman;
    }

    // Overload for backward compatibility
    @Transactional
    public Pinjaman createPinjaman(User user, List<Barang> items, Ruangan ruangan,
                                   String tujuan, LocalDate tglRencanaKembali) {
        return createPinjaman(user, items, ruangan, tujuan, tglRencanaKembali, null);
    }

    @Transactional
    public Pengembalian submitPengembalian(PinjamanDetail detail, String catatan, String fotoPengembalian) {
        Pengembalian p = new Pengembalian();
        p.setPinjamanDetail(detail);
        p.setCatatanKondisi(catatan);
        p.setFotoPengembalian(fotoPengembalian);
        p.setStatusAcc(Pengembalian.StatusAcc.pending);
        Pengembalian saved = pengembalianRepository.save(p);

        // NOTE: detail.sudahDikembalikan remains FALSE until admin verifies and approves!
        // Barang status remains 'dipinjam' until admin verifies.

        return saved;
    }

    // Overload for backward compatibility
    @Transactional
    public Pengembalian submitPengembalian(PinjamanDetail detail, String catatan) {
        return submitPengembalian(detail, catatan, null);
    }

    public List<Pengembalian> getPendingPengembalian() {
        return pengembalianRepository.findByStatusAccOrderByIdDesc(Pengembalian.StatusAcc.pending);
    }

    public List<Pengembalian> getAllPengembalian() {
        return pengembalianRepository.findAllByOrderByIdDesc();
    }

    // Admin verification approval
    @Transactional
    public void approvePengembalian(Pengembalian p, User admin, Barang.Status newStatus) {
        p.setStatusAcc(Pengembalian.StatusAcc.approved);
        p.setAdminAcc(admin);
        pengembalianRepository.save(p);

        PinjamanDetail detail = p.getPinjamanDetail();
        if (detail != null) {
            detail.setSudahDikembalikan(true);
            pinjamanDetailRepository.save(detail);

            if (detail.getBarang() != null) {
                Barang b = detail.getBarang();
                b.setStatus(newStatus != null ? newStatus : Barang.Status.tersedia);
                barangRepository.save(b);
            }
        }
    }

    @Transactional
    public void approvePengembalian(Pengembalian p, User admin) {
        approvePengembalian(p, admin, Barang.Status.tersedia);
    }

    // Admin rejection
    @Transactional
    public void rejectPengembalian(Pengembalian p, User admin, String catatanAdmin) {
        p.setStatusAcc(Pengembalian.StatusAcc.rejected);
        p.setAdminAcc(admin);
        p.setCatatanAdmin(catatanAdmin);
        pengembalianRepository.save(p);
    }
}
