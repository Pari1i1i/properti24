package property24.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pengembalian")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Pengembalian {

    public enum StatusAcc { pending, approved, rejected }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pinjaman_detail_id")
    private PinjamanDetail pinjamanDetail;

    @Column(name = "foto_pengembalian")
    private String fotoPengembalian;

    @Column(name = "tgl_kembali", insertable = false, updatable = false)
    private LocalDateTime tglKembali;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_acc")
    private StatusAcc statusAcc;

    @ManyToOne
    @JoinColumn(name = "admin_id_acc")
    private User adminAcc;

    @Column(name = "catatan_admin", columnDefinition = "TEXT")
    private String catatanAdmin;

    @Column(name = "catatan_kondisi", columnDefinition = "TEXT")
    private String catatanKondisi;
}
