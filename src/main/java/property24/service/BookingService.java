package property24.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property24.entity.*;
import property24.repository.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    public static final int DEFAULT_EXPIRY_HOURS = 24;
    public static final List<Booking.BookingStatus> ACTIVE_STATUSES = List.of(
            Booking.BookingStatus.menunggu_persetujuan,
            Booking.BookingStatus.disetujui
    );

    private final BookingRepository bookingRepository;
    private final PinjamanService pinjamanService;

    // ── READ ──────────────────────────────────────────────────────────────────

    public Optional<Booking> getActiveBookingForBarang(Barang barang) {
        if (barang == null || barang.getId() == null) return Optional.empty();
        return bookingRepository.findFirstByBarangIdAndStatusIn(barang.getId(), ACTIVE_STATUSES);
    }

    public boolean isBarangBooked(Barang barang) {
        if (barang == null || barang.getId() == null) return false;
        return bookingRepository.existsByBarangIdAndStatusIn(barang.getId(), ACTIVE_STATUSES);
    }

    public boolean hasUserActiveBooking(User user, Barang barang) {
        if (user == null || user.getId() == null || barang == null || barang.getId() == null) return false;
        return bookingRepository.existsByUserIdAndBarangIdAndStatusIn(user.getId(), barang.getId(), ACTIVE_STATUSES);
    }

    public List<Booking> getBookingsByUser(User user) {
        if (user == null || user.getId() == null) return List.of();
        return bookingRepository.findByUserIdOrderByIdDesc(user.getId());
    }

    public List<Booking> getAllActiveBookings() {
        return bookingRepository.findByStatusInOrderByIdDesc(ACTIVE_STATUSES);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAllByOrderByIdDesc();
    }

    // ── WRITE ─────────────────────────────────────────────────────────────────

    @Transactional
    public Booking createBooking(User user, Barang barang, Ruangan ruangan, LocalDateTime tglRencanaAmbil, String catatan) {
        if (barang.getStatus() != Barang.Status.tersedia) {
            throw new IllegalStateException("Barang tidak tersedia untuk dibooking.");
        }
        if (isBarangBooked(barang)) {
            throw new IllegalStateException("Barang sedang dibooking oleh pengguna lain.");
        }
        if (hasUserActiveBooking(user, barang)) {
            throw new IllegalStateException("Anda sudah memiliki booking aktif untuk barang ini.");
        }

        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBarang(barang);
        booking.setRuangan(ruangan);
        booking.setTglBooking(now);
        booking.setTglRencanaAmbil(tglRencanaAmbil != null ? tglRencanaAmbil : now);
        // Expiry set 24 hours after target pickup time (or 24h from now if pickup time is in the past)
        LocalDateTime refTime = (tglRencanaAmbil != null && tglRencanaAmbil.isAfter(now)) ? tglRencanaAmbil : now;
        booking.setBatasWaktu(refTime.plusHours(DEFAULT_EXPIRY_HOURS));
        booking.setCatatan(catatan);
        booking.setStatus(Booking.BookingStatus.menunggu_persetujuan);

        return bookingRepository.save(booking);
    }

    @Transactional
    public void approveBooking(Booking booking, User admin) {
        if (booking == null || booking.getStatus() != Booking.BookingStatus.menunggu_persetujuan) {
            throw new IllegalStateException("Booking tidak dalam status menunggu persetujuan.");
        }

        booking.setStatus(Booking.BookingStatus.disetujui);
        bookingRepository.save(booking);
    }

    @Transactional
    public void rejectBooking(Booking booking, User admin, String alasanTolak) {
        if (booking == null || booking.getStatus() != Booking.BookingStatus.menunggu_persetujuan) {
            throw new IllegalStateException("Booking tidak dalam status menunggu persetujuan.");
        }

        if (alasanTolak != null && !alasanTolak.isBlank()) {
            String catatan = booking.getCatatan() != null ? booking.getCatatan() : "";
            booking.setCatatan(catatan + " [Ditolak Admin: " + alasanTolak.trim() + "]");
        }

        booking.setStatus(Booking.BookingStatus.ditolak);
        bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Booking booking, User currentUser) {
        if (booking == null || !ACTIVE_STATUSES.contains(booking.getStatus())) {
            throw new IllegalStateException("Booking tidak dalam status aktif.");
        }

        boolean isOwner = booking.getUser() != null && booking.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.Role.admin;

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("Anda tidak berhak membatalkan booking ini.");
        }

        booking.setStatus(Booking.BookingStatus.dibatalkan);
        bookingRepository.save(booking);
    }

    @Transactional
    public Pinjaman convertToPinjaman(Booking booking, Ruangan ruangan, String tujuan,
                                      LocalDate tglRencanaKembali, String fotoPeminjam) {
        if (booking == null || !ACTIVE_STATUSES.contains(booking.getStatus())) {
            throw new IllegalStateException("Booking tidak valid atau tidak dalam status aktif.");
        }

        // Reuse existing PinjamanService.createPinjaman
        Pinjaman pinjaman = pinjamanService.createPinjaman(
                booking.getUser(),
                List.of(booking.getBarang()),
                ruangan,
                tujuan,
                tglRencanaKembali,
                fotoPeminjam
        );

        booking.setStatus(Booking.BookingStatus.diambil);
        bookingRepository.save(booking);

        return pinjaman;
    }

    @Transactional
    public int expireOverdueBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> overdue = bookingRepository.findByStatusInAndBatasWaktuBefore(ACTIVE_STATUSES, now);
        for (Booking b : overdue) {
            b.setStatus(Booking.BookingStatus.kedaluwarsa);
            bookingRepository.save(b);
        }
        return overdue.size();
    }
}
