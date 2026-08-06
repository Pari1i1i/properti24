package property24.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pinjaman_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PinjamanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pinjaman_id")
    private Pinjaman pinjaman;

    @ManyToOne
    @JoinColumn(name = "ruangan_id")
    private Ruangan ruangan;

    @ManyToOne
    @JoinColumn(name = "barang_id")
    private Barang barang;

    @Column(name = "tujuan_pinjam", columnDefinition = "TEXT")
    private String tujuanPinjam;

    @Column(name = "tgl_rencana_kembali")
    private LocalDate tglRencanaKembali;

    @Column(name = "sudah_dikembalikan")
    private Boolean sudahDikembalikan = false;
}
