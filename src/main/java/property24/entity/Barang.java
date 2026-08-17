package property24.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "barang")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Barang {

    public enum Status { tersedia, dipinjam, rusak, diperbaiki }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kode_barang")
    private String kodeBarang;

    @ManyToOne
    @JoinColumn(name = "kategori_id")
    private Kategori kategori;

    @ManyToOne
    @JoinColumn(name = "ruangan_id")
    private Ruangan ruangan;

    @Column(name = "nama_barang", nullable = false)
    private String namaBarang;

    @Column(name = "bintang_saat_ini")
    private Integer bintangSaatIni;

    @Column(name = "deskripsi_bintang", columnDefinition = "TEXT")
    private String deskripsiBintang;

    private Integer stock = 0;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "foto_barang")
    private String fotoBarang;

    @Column(name = "diperbarui_pada", insertable = false, updatable = false)
    private LocalDateTime diperbaruiPada;
}
