package com.xzjie.cms.visit.service.impl;

import com.xzjie.cms.core.service.AbstractService;
import com.xzjie.cms.visit.model.VisitLog;
import com.xzjie.cms.visit.service.VisitLogService;
import com.xzjie.cms.visit.repository.VisitLogRepository;
import com.xzjie.cms.vo.ChartData;
import com.xzjie.cms.vo.VisitStatistics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vito
 * @since 2022/3/19 1:36 下午
 */
@Service
public class VisitLogServiceImpl extends AbstractService<VisitLog, VisitLogRepository> implements VisitLogService {

    @Override
    public List<VisitStatistics> getVisitStatistics() {
        return baseRepository.findVisitStatistics();
    }

    @Override
    public ChartData getVisitData() {
        List<String> xaxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        List<VisitStatistics> list = baseRepository.findVisitData();
        list.stream().forEach(visit -> {
            xaxisData.add(visit.getName());
            seriesData.add(visit.getTotal());
        });
        return ChartData.builder().xaxisData(xaxisData).seriesData(seriesData).build();
    }
}
