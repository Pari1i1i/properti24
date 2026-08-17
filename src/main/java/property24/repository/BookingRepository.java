package property24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import property24.entity.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByBarangIdAndStatusIn(Long barangId, Collection<Booking.BookingStatus> statuses);

    List<Booking> findByBarangIdAndStatusIn(Long barangId, Collection<Booking.BookingStatus> statuses);

    boolean existsByBarangIdAndStatusIn(Long barangId, Collection<Booking.BookingStatus> statuses);

    boolean existsByUserIdAndBarangIdAndStatusIn(Long userId, Long barangId, Collection<Booking.BookingStatus> statuses);

    List<Booking> findByUserIdOrderByIdDesc(Long userId);

    List<Booking> findByStatusInAndBatasWaktuBefore(Collection<Booking.BookingStatus> statuses, LocalDateTime now);

    List<Booking> findByStatusInOrderByIdDesc(Collection<Booking.BookingStatus> statuses);

    List<Booking> findAllByOrderByIdDesc();
}
