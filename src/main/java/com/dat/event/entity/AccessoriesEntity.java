/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * AccessoriesEntity Class.
 * <p>
 * </p>
 *
 * @author kyi min khant
 */
@Entity
@Data
@Table(name = "tbl_accessories")
public class AccessoriesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acce_id")
    private long id;
    private String name;

    @OneToMany(mappedBy = "accessories", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestedAccessoriesEntity> requestedAccessoriesEntityList = new ArrayList<>();
}
