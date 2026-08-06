package property24.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ruangan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ruangan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_ruangan", nullable = false)
    private String namaRuangan;
}
