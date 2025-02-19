package by.fleamarket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ad_photo")
public class AdPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;

    @OneToOne
    @JoinColumn(name = "binary_content_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BinaryContent binaryContent;

    private Integer fileSize;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ad_id")
    private Ad ad;
}
