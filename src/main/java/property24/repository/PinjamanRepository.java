package property24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import property24.entity.Pinjaman;

import java.util.List;

public interface PinjamanRepository extends JpaRepository<Pinjaman, Long> {
    List<Pinjaman> findByUserId(Long userId);
    List<Pinjaman> findByUserIdAndStatusPinjaman(Long userId, Pinjaman.StatusPinjaman status);
}
