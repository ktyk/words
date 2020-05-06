package org.planetearth.words.repository;

import java.util.List;

import org.planetearth.words.domain.Context;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository of Context Domain.
 *
 * @author katsuyuki.t
 */
public interface ContextRepository extends JpaRepository<Context, Long> {

    @Query("select c from Context c where c.name = :name ")
    Context findByName(@Param("name") String name);

    @Query("select c from Context c where (c.name like %:name% or c.parentName like %:name% ) "
        + "order by c.parentName asc, c.name asc")
    List<Context> findContainsName(@Param("name") String name);

    @Query("select c from Context c where (c.name like %:name% or c.parentName like %:name% ) "
        + "and c.id in :ids order by c.parentName asc, c.name asc")
    List<Context> findContainsName(@Param("name") String name, @Param("ids") List<Long> ids);

    @Query("select c from Context c "
        + "where c.name = :name and c.parentName = :parentName ")
    Context findByNameWithParent(@Param("name") String name,
        @Param("parentName") String parentName);
}
