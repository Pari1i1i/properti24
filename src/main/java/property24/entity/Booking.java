package property24.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Booking {

    public enum BookingStatus { menunggu_persetujuan, disetujui, diambil, ditolak, dibatalkan, kedaluwarsa }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "barang_id", nullable = false)
    private Barang barang;

    @ManyToOne
    @JoinColumn(name = "ruangan_id")
    private Ruangan ruangan;

    @Column(name = "tgl_booking", nullable = false)
    private LocalDateTime tglBooking;

    @Column(name = "tgl_rencana_ambil", columnDefinition = "DATETIME")
    private LocalDateTime tglRencanaAmbil;

    @Column(name = "batas_waktu", nullable = false)
    private LocalDateTime batasWaktu;

    @Column(name = "catatan", columnDefinition = "TEXT")
    private String catatan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private BookingStatus status;

    @PrePersist
    public void prePersist() {
        if (tglBooking == null) {
            tglBooking = LocalDateTime.now();
        }
        if (status == null) {
            status = BookingStatus.menunggu_persetujuan;
        }
    }
}
