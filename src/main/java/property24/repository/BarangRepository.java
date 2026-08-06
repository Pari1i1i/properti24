package property24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import property24.entity.Barang;

import java.util.List;

public interface BarangRepository extends JpaRepository<Barang, Long> {
    List<Barang> findByStatus(Barang.Status status);
    long countByStatus(Barang.Status status);
    List<Barang> findByKategoriNamaKategoriContainingIgnoreCase(String keyword);
    List<Barang> findByNamaBarangContainingIgnoreCase(String keyword);

    @Query("SELECT b FROM Barang b WHERE LOWER(b.namaBarang) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(b.kodeBarang) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Barang> search(String q);
}
