package property24.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kategori")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Kategori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_kategori", nullable = false)
    private String namaKategori;
}
