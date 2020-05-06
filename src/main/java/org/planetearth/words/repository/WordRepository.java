package org.planetearth.words.repository;

import java.util.List;
import java.util.Set;

import org.planetearth.words.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository of Word Domain.
 *
 * @author Katsuyuki.T
 */
public interface WordRepository extends JpaRepository<Word, Long> {

    @Query("select w from Word w where w.id in :ids ")
    List<Word> findByIds(@Param("ids") Set<Long> ids);

    @Query("select w from Word w inner join w.context c where c.id = :contextId ")
    List<Word> findByContextId(@Param("contextId") Long contextId);

    @Query("select w from Word w where (w.notation like %:word% or w.reading like %:word% "
        + "or w.conversion like %:word% or w.abbreviation like %:word% or w.note like %:word%) "
        + "order by w.reading asc")
    List<Word> findContainsWord(@Param("word") String word);

    @Query("select w from Word w inner join w.context c where c.id in :contextIds order by w.reading asc")
    List<Word> findContainsWord(@Param("contextIds") Set<Long> contextIds);

    @Query("select w from Word w inner join w.context c where c.id in :contextIds "
        + "and (w.notation like %:word% or w.reading like %:word% or w.conversion like %:word% "
        + "or w.abbreviation like %:word% or w.note like %:word%) order by w.reading asc")
    List<Word> findContainsWord(@Param("contextIds") Set<Long> contextIds,
        @Param("word") String word);

    @Query("select w from Word w inner join w.context c where c.id in :contextIds "
        + "and (w.notation like %:word% or w.reading like %:word% or w.conversion like %:word% "
        + "or w.abbreviation like %:word% or w.note like %:word%) and w.id in :ids "
        + "order by w.reading asc")
    List<Word> findContainsWord(@Param("contextIds") Set<Long> contextIds,
        @Param("word") String word,
        @Param("ids") List<Long> ids);

    @Query("select w from Word w inner join w.context c where c.id =  :contextId "
        + "and w.notation = :notation order by w.reading asc")
    Word findByNotation(@Param("contextId") Long contextId, @Param("notation") String notation);

    @Query("select w from Word w inner join w.context c where c.id in  :contextIds "
        + "and w.notation = :notation order by w.reading asc")
    List<Word> findByNotation(@Param("contextIds") Set<Long> contextIds,
        @Param("notation") String notation);

    @Query("select w from Word w where (w.notation like %:word% or w.reading like %:word% "
        + "or w.conversion like %:word% or w.abbreviation like %:word% or w.note like %:word%) "
        + "and w.id in :ids order by w.reading asc")
    List<Word> findContainsWord(@Param("word") String word, @Param("ids") List<Long> ids);

}
