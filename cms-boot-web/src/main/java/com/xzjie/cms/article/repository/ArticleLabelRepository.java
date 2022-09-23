package com.xzjie.cms.article.repository;

import com.xzjie.cms.article.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ArticleLabelRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    @Query(nativeQuery = true, value = "select * from cms_article a where a.id < :#{#id} and category_id = :#{#categoryId} order by id desc limit 0,1")
    Article findByIdLessThanAndCategoryId(Long id, Long categoryId);

    @Query(nativeQuery = true, value = "select * from cms_article a where a.id > :#{#id} and category_id = :#{#categoryId} order by id desc limit 0,1")
    Article findByIdGreaterThanAndCategoryId(Long id, Long categoryId);
}
