package org.planetearth.words.repository;

import java.util.List;
import java.util.Set;

import org.planetearth.words.domain.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository of Paragraph Domain.
 *
 * @author Katsuyuki.T
 */
@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {

    @Query("select p from Paragraph p where p.title like %:word% or p.text like %:word% or p.remarks like %:word% order by p.id asc")
    List<Paragraph> findContainsWord(@Param("word") String word);

    @Query(
        "select p from Paragraph p where (p.title like %:word% or p.text like %:word% or p.remarks like %:word% ) "
            + "and p.id in :ids order by p.id asc")
    List<Paragraph> findContainsWord(@Param("word") String word, @Param("ids") List<Long> ids);

    @Query("select p from Paragraph p where p.id in (:ids) order by p.id asc")
    List<Paragraph> findByIds(@Param("ids") Set<Long> ids);

}
