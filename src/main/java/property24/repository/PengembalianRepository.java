package property24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import property24.entity.Pengembalian;

import java.util.List;
import java.util.Optional;

public interface PengembalianRepository extends JpaRepository<Pengembalian, Long> {
    Optional<Pengembalian> findByPinjamanDetailId(Long pinjamanDetailId);
    List<Pengembalian> findByStatusAccOrderByIdDesc(Pengembalian.StatusAcc statusAcc);
    List<Pengembalian> findAllByOrderByIdDesc();
}
