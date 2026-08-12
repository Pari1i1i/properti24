package property24.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pinjaman")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Pinjaman {

    public enum StatusPinjaman { dipinjam, ditolak, selesai }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "foto_peminjam")
    private String fotoPeminjam;

    @Column(name = "tgl_pinjam", insertable = false, updatable = false)
    private LocalDateTime tglPinjam;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pinjaman")
    private StatusPinjaman statusPinjaman;
}
