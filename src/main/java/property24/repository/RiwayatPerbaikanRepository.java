package property24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import property24.entity.RiwayatPerbaikan;

import java.util.List;

@Repository
public interface RiwayatPerbaikanRepository extends JpaRepository<RiwayatPerbaikan, Long> {

    List<RiwayatPerbaikan> findAllByOrderByIdDesc();

    List<RiwayatPerbaikan> findByStatusPerbaikanOrderByIdDesc(RiwayatPerbaikan.StatusPerbaikan status);

    List<RiwayatPerbaikan> findByBarangIdOrderByIdDesc(Long barangId);
}
