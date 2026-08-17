package property24.service;

import lombok.*;
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

    // ── DTO for detailed per-item requests ──────────────────────────────────
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class ItemBorrowRequest {
        private Barang barang;
        private Ruangan ruangan;
        private String tujuan;
        private LocalDate tglPinjam;
        private LocalDate tglRencanaKembali;
    }

    @Transactional
    public Pinjaman createPinjamanDetailed(User user, List<ItemBorrowRequest> requests, String fotoPeminjam) {
        Pinjaman pinjaman = new Pinjaman();
        pinjaman.setUser(user);
        pinjaman.setFotoPeminjam(fotoPeminjam);
        pinjaman.setStatusPinjaman(Pinjaman.StatusPinjaman.dipinjam);
        pinjaman = pinjamanRepository.save(pinjaman);

        for (ItemBorrowRequest req : requests) {
            PinjamanDetail detail = new PinjamanDetail();
            detail.setPinjaman(pinjaman);
            detail.setBarang(req.getBarang());
            detail.setRuangan(req.getRuangan());
            detail.setTujuanPinjam(req.getTujuan());
            detail.setTglRencanaKembali(req.getTglRencanaKembali() != null ? req.getTglRencanaKembali() : LocalDate.now());
            detail.setSudahDikembalikan(false);
            pinjamanDetailRepository.save(detail);

            Barang b = req.getBarang();
            if (b != null) {
                b.setStatus(Barang.Status.dipinjam);
                barangRepository.save(b);
            }
        }

        return pinjaman;
    }

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
        Optional<Pengembalian> existingOpt = pengembalianRepository.findByPinjamanDetailId(detail.getId());
        Pengembalian p = existingOpt.orElseGet(Pengembalian::new);
        p.setPinjamanDetail(detail);
        p.setCatatanKondisi(catatan);
        if (fotoPengembalian != null && !fotoPengembalian.isBlank()) {
            p.setFotoPengembalian(fotoPengembalian);
        }
        p.setStatusAcc(Pengembalian.StatusAcc.pending);
        p.setCatatanAdmin(null);
        Pengembalian saved = pengembalianRepository.save(p);

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

            // Check if all items in parent loan are now returned
            if (detail.getPinjaman() != null) {
                Pinjaman parent = detail.getPinjaman();
                List<PinjamanDetail> allDetails = pinjamanDetailRepository.findByPinjamanId(parent.getId());
                boolean allReturned = allDetails.stream()
                        .allMatch(d -> Boolean.TRUE.equals(d.getSudahDikembalikan()));
                if (allReturned) {
                    parent.setStatusPinjaman(Pinjaman.StatusPinjaman.selesai);
                    pinjamanRepository.save(parent);
                }
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
