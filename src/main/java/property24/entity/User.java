package property24.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    // Nama enum sengaja huruf kecil biar persis sama kayak value ENUM di MySQL
    public enum Role { admin, user, teknisi }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "nama_lengkap")
    private String namaLengkap;

    private String kelas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;

    @PrePersist
    protected void onCreate() {
        if (dibuatPada == null) {
            dibuatPada = LocalDateTime.now();
        }
        if (email == null || email.isBlank()) {
            email = username;
        }
        if (username == null || username.isBlank()) {
            username = email;
        }
    }
}
