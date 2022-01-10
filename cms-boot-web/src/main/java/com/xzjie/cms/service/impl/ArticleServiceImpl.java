package com.xzjie.cms.service.impl;

import com.xzjie.cms.client.dto.ArticleDetailResponse;
import com.xzjie.cms.client.dto.CaseResponse;
import com.xzjie.cms.convert.CategoryConverter;
import com.xzjie.cms.core.service.AbstractService;
import com.xzjie.cms.dto.ArticleHotResult;
import com.xzjie.cms.dto.ArticleRecommendStatResult;
import com.xzjie.cms.dto.CategoryTree;
import com.xzjie.cms.enums.Sorting;
import com.xzjie.cms.model.Article;
import com.xzjie.cms.model.ArticleHot;
import com.xzjie.cms.model.ArticleRecommendStat;
import com.xzjie.cms.model.Category;
import com.xzjie.cms.repository.ArticleHotRepository;
import com.xzjie.cms.repository.ArticleRecommendStatRepository;
import com.xzjie.cms.repository.ArticleRepository;
import com.xzjie.cms.repository.CategoryRepository;
import com.xzjie.cms.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ArticleServiceImpl extends AbstractService<Article, ArticleRepository> implements ArticleService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ArticleHotRepository articleHotRepository;
    @Autowired
    private ArticleRecommendStatRepository articleRecommendStatRepository;

    @Override
    public Article getArticle(Long id) {
        return baseRepository.getOne(id);
    }

    @Override
    public ArticleDetailResponse getArticleDetail(Long id) {
        Article article = this.getArticle(id);
        Article prev = null;
        Article next = null;
        if (article.getCategoryId() != null) {
            prev = baseRepository.findByIdLessThanAndCategoryId(id, article.getCategoryId());
            next = baseRepository.findByIdGreaterThanAndCategoryId(id, article.getCategoryId());
        }
        return ArticleDetailResponse.create(article, prev, next);
    }

    @Override
    public Page<Article> getArticle(Integer page, int size, Article query) {
        Sort sort = Sort.by("id").descending();
        if (Sorting.asc.equals(query.getSorting())) {
            sort = Sort.by("id").ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Article> articles = baseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query == null) {
                return null;
            }
            if (null != query.getRecommendStat()) {
//                String attribute = query.getRecommendStat().getClass().getName();
                predicates.add(criteriaBuilder.equal(root.get("recommendStat").as(String.class), query.getRecommendStat()));
            }
            if (null != query.getCategoryId()) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId").as(String.class), query.getCategoryId()));
            }
            if (null != query.getTitle()) {
                predicates.add(criteriaBuilder.like(root.get("title").as(String.class), "%" + query.getTitle() + "%"));
            }

            //add add criteria to predicates
//            for (SearchCriteria criteria : list) {
//                if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
//                    predicates.add(builder.greaterThan(
//                            root.get(criteria.getKey()), criteria.getValue().toString()));
//                } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
//                    predicates.add(builder.lessThan(
//                            root.get(criteria.getKey()), criteria.getValue().toString()));
//                } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
//                    predicates.add(builder.greaterThanOrEqualTo(
//                            root.get(criteria.getKey()), criteria.getValue().toString()));
//                } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
//                    predicates.add(builder.lessThanOrEqualTo(
//                            root.get(criteria.getKey()), criteria.getValue().toString()));
//                } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
//                    predicates.add(builder.notEqual(
//                            root.get(criteria.getKey()), criteria.getValue()));
//                } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
//                    predicates.add(builder.equal(
//                            root.get(criteria.getKey()), criteria.getValue()));
//                } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
//                    predicates.add(builder.like(
//                            builder.lower(root.get(criteria.getKey())),
//                            "%" + criteria.getValue().toString().toLowerCase() + "%"));
//                } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
//                    predicates.add(builder.like(
//                            builder.lower(root.get(criteria.getKey())),
//                            criteria.getValue().toString().toLowerCase() + "%"));
//                } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
//                    predicates.add(builder.like(
//                            builder.lower(root.get(criteria.getKey())),
//                            "%" + criteria.getValue().toString().toLowerCase()));
//                } else if (criteria.getOperation().equals(SearchOperation.IN)) {
//                    predicates.add(builder.in(root.get(criteria.getKey())).value(criteria.getValue()));
//                } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
//                    predicates.add(builder.not(root.get(criteria.getKey())).in(criteria.getValue()));
//                }
//            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable);

        articles.forEach(article -> {
            Category category = categoryRepository.getOne(article.getCategoryId());
            article.setCategoryName(category.getCategoryName());
        });
        return articles;
    }

    @Override
    public Category getCategory(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public List<CaseResponse> getCaseData(Long categoryId, Article article, Integer page, Integer size) {
        List<Category> categories = categoryRepository.findCategoriesByPidOrderBySort(categoryId);
        List<CaseResponse> caseResponses = CategoryConverter.INSTANCE.source(categories);

        for (CaseResponse caseResponse : caseResponses) {
            article.setCategoryId(caseResponse.getId());
            Page<Article> articlePage = this.getArticle(page, size, article);
            caseResponse.setArticles(articlePage.getContent());
        }
        return caseResponses;
    }

    @Override
    public List<Category> getCategory() {
        return categoryRepository.findCategoriesByPid(0L);
    }

    @Override
    public Page<Category> getCategory(Integer page, int size, Category query) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
//        return categoryRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            if (query == null) {
//                return null;
//            }
//            if (null != query.getPid()) {
//                predicates.add(criteriaBuilder.equal(root.get("pid").as(String.class), query.getPid()));
//            }
//
//            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
//        }, pageable);
        return categoryRepository.findCategoriesByPid(0L, pageable);
    }

    @Override
    public List<CategoryTree> getCategoryTree() {
        List<Category> categories = categoryRepository.findCategoriesByPid(0L);

        List<CategoryTree> categoryTrees = new ArrayList<>();
        BeanUtils.copyProperties(categories, categoryTrees);
        categoryTrees = getTree(categoryTrees);
        return categoryTrees;
    }

    @Override
    public boolean saveCategory(Category category) {
        category.setCreateDate(LocalDateTime.now());
        category.setState(1);
        categoryRepository.save(category);
        return true;
    }

    @Override
    public boolean updateCategory(Category category) {
        Category model = categoryRepository.findById(category.getId()).orElseGet(Category::new);
        model.copy(category);
        categoryRepository.save(model);
        return true;
    }

    @Override
    public boolean deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        return true;
    }

    private List<CategoryTree> getTree(List<CategoryTree> categoryTrees) {
        Map<Long, CategoryTree> dataMap = new HashMap<>();
        for (CategoryTree categoryTree : categoryTrees) {
            dataMap.put(categoryTree.getId(), categoryTree);
        }

        List<CategoryTree> trees = new ArrayList<>();
        for (Map.Entry<Long, CategoryTree> entry : dataMap.entrySet()) {
            CategoryTree categoryTree = entry.getValue();
            if (categoryTree.getParentId() == null) {
                trees.add(categoryTree);
            } else {
                if (dataMap.get(Long.valueOf(categoryTree.getParentId())) != null) {
                    dataMap.get(Long.valueOf(categoryTree.getParentId())).getChildren().add(categoryTree);
                }
            }
        }
        return trees;
    }


    @Override
    public boolean update(Article article) {
        Article model = baseRepository.findById(article.getId()).orElseGet(Article::new);
        model.copy(article);
        model.setUpdateDate(LocalDateTime.now());
        baseRepository.save(model);
        return true;
    }

    @Override
    public Page<ArticleHotResult> getArticleHot(Integer page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sort").descending());
        return articleHotRepository.findArticleHot(pageable);
    }

    @Override
    public boolean saveArticleHot(Set<Long> ids) {
        List<ArticleHot> list = new ArrayList<>();
        for (Long articleId : ids) {
            if (!articleHotRepository.existsByArticleId(articleId)) {
                ArticleHot model = new ArticleHot();
                model.setArticleId(articleId);
                model.setSort(50);
                list.add(model);
            }
        }
        articleHotRepository.saveAll(list);
        return true;
    }

    @Override
    public boolean updateArticleHot(ArticleHot articleHot) {
        ArticleHot model = articleHotRepository.findById(articleHot.getId()).orElseGet(ArticleHot::new);
        model.copy(articleHot);
        articleHotRepository.save(model);
        return true;
    }

    @Override
    public boolean deleteArticleHot(Set<Long> ids) {
        ids.forEach(id -> {
            articleHotRepository.deleteById(id);
        });
        return true;
    }

    @Override
    public Page<ArticleRecommendStatResult> getRecommendStat(Integer page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sort").descending());
        return articleRecommendStatRepository.findArticleRecommendStat(pageable);
    }

    @Override
    public boolean saveRecommendStat(Set<Long> ids) {
        List<ArticleRecommendStat> list = new ArrayList<>();
        for (Long articleId : ids) {
            if (!articleRecommendStatRepository.existsByArticleId(articleId)) {
                ArticleRecommendStat model = new ArticleRecommendStat();
                model.setArticleId(articleId);
                model.setSort(50);
                list.add(model);
            }
        }
        articleRecommendStatRepository.saveAll(list);
        return true;
    }

    @Override
    public boolean updateRecommendStat(ArticleRecommendStat recommendStat) {
        ArticleRecommendStat model = articleRecommendStatRepository.findById(recommendStat.getId()).orElseGet(ArticleRecommendStat::new);
        model.copy(recommendStat);
        articleRecommendStatRepository.save(model);
        return true;
    }

    @Override
    public boolean deleteRecommendStat(Set<Long> ids) {
        ids.forEach(id -> {
            articleRecommendStatRepository.deleteById(id);
        });
        return true;
    }
}
