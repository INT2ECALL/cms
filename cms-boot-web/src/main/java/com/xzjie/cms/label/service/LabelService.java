package com.xzjie.cms.label.service;

import com.xzjie.cms.core.service.BaseService;
import com.xzjie.cms.dto.LabelQueryDto;
import com.xzjie.cms.label.model.Label;
import com.xzjie.cms.vo.LabelVo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LabelService extends BaseService<Label> {

    List<Label> getLabelList();

    List<LabelVo> getLabelCounterList();

    List<Label> getLabelByArticleId(Long articleId);

    Page<Label> getLabel(LabelQueryDto query);
}
