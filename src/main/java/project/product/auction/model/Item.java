package project.product.auction.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "desc")
    private String description;

    @Column(name = "exp_time")
    private LocalDateTime expTime;

    @Column(name = "category")
    private String category;
    
}
