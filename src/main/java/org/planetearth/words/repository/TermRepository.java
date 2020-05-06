package org.planetearth.words.repository;

import java.util.List;
import java.util.Set;

import org.planetearth.words.domain.Context;
import org.planetearth.words.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository of Term Domain.
 *
 * @author Katsuyuki.T
 */
public interface TermRepository extends JpaRepository<Term, Long> {

    @Query("select t from Term t where (t.name like %:word% or t.explanation like %:word% "
        + "or t.reading like %:word% ) order by t.reading asc")
    List<Term> findContainsWord(@Param("word") String word);

    @Query("select t from Term t inner join t.context c where c.id in :contextIds "
        + "order by t.reading asc")
    List<Term> findContainsWord(@Param("contextIds") Set<Long> contextIds);

    @Query("select t from Term t inner join t.context c where c.id in :contextIds "
        + "and (t.name like %:word% or t.reading like %:word% or t.explanation like %:word%) "
        + "order by t.reading asc")
    List<Term> findContainsWord(@Param("contextIds") Set<Long> contextIds,
        @Param("word") String word);

    @Query("select t from Term t inner join t.context c where c.id in :contextIds "
        + "and (t.name like %:word% or t.reading like %:word% or t.explanation like %:word%) "
        + "and t.id in :ids "
        + "order by t.reading asc")
    List<Term> findContainsWord(@Param("contextIds") Set<Long> contextIds,
        @Param("word") String word,
        @Param("contextIds") List<Long> ids);

    @Query("select t from Term t where (t.name like %:word% or t.explanation like %:word% "
        + "or t.reading like %:word% ) and t.id in :ids order by t.reading asc")
    List<Term> findContainsWord(@Param("word") String word, @Param("ids") List<Long> ids);

    // Query Creation from Method Name
    List<Term> findByContext(Context context);

    @Query(
        "select t from Term t inner join t.context c where c.id =  :contextId and t.name = :name "
            + "order by t.reading asc")
    Term findByName(@Param("contextId") Long contextId, @Param("name") String name);

}
