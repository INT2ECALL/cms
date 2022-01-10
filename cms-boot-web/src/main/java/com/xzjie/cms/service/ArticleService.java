package com.xzjie.cms.service;

import com.xzjie.cms.client.dto.ArticleDetailResponse;
import com.xzjie.cms.client.dto.CaseResponse;
import com.xzjie.cms.core.service.BaseService;
import com.xzjie.cms.dto.ArticleHotResult;
import com.xzjie.cms.dto.ArticleRecommendStatResult;
import com.xzjie.cms.dto.CategoryTree;
import com.xzjie.cms.model.Article;
import com.xzjie.cms.model.ArticleHot;
import com.xzjie.cms.model.ArticleRecommendStat;
import com.xzjie.cms.model.Category;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;


public interface ArticleService extends BaseService<Article> {

    Article getArticle(Long id);

    ArticleDetailResponse getArticleDetail(Long id);

    Page<Article> getArticle(Integer page, int size, Article query);

    Category getCategory(Long id);

    List<CaseResponse> getCaseData(Long categoryId, Article article, Integer page, Integer size);

    List<Category> getCategory();

    Page<Category> getCategory(Integer page, int size, Category category);

    List<CategoryTree> getCategoryTree();

    boolean saveCategory(Category category);

    boolean updateCategory(Category category);

    boolean deleteCategory(Long id);

    Page<ArticleHotResult> getArticleHot(Integer page, int size);

    boolean saveArticleHot(Set<Long> ids);

    boolean updateArticleHot(ArticleHot articleHot);

    boolean deleteArticleHot(Set<Long> ids);

    Page<ArticleRecommendStatResult> getRecommendStat(Integer page, int size);

    boolean saveRecommendStat(Set<Long> ids);

    boolean updateRecommendStat(ArticleRecommendStat recommendStat);

    boolean deleteRecommendStat(Set<Long> ids);
}
