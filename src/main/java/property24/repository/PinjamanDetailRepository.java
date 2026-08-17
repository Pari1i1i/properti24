package property24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import property24.entity.PinjamanDetail;

import java.util.List;

public interface PinjamanDetailRepository extends JpaRepository<PinjamanDetail, Long> {
    List<PinjamanDetail> findByPinjamanId(Long pinjamanId);
    List<PinjamanDetail> findByPinjamanUserId(Long userId);
    List<PinjamanDetail> findByPinjamanUserIdAndSudahDikembalikan(Long userId, Boolean sudahDikembalikan);
}
