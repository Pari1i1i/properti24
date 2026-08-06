package property24.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "riwayat_perbaikan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RiwayatPerbaikan {

    public enum StatusPerbaikan { proses, selesai, dibatalkan }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "barang_id")
    private Barang barang;

    @ManyToOne
    @JoinColumn(name = "dilaporkan_oleh")
    private User dilaporkanOleh;

    @Column(name = "tgl_masuk", insertable = false, updatable = false)
    private LocalDateTime tglMasuk;

    @Column(name = "tgl_selesai")
    private LocalDateTime tglSelesai;

    @Column(name = "teknisi_vendor")
    private String teknisiVendor;

    private BigDecimal biaya = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String catatan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_perbaikan")
    private StatusPerbaikan statusPerbaikan;
}
