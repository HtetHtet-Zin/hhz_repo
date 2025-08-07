/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.mappers;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * BaseMapper Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
public interface BaseMapper<D, E> {
    D toDTO(E entity);
    E toEntity(D dto);
    List<D> toDtoList(List<E> entityList);
    List<E> toEntityList(List<D> dtoList);
}