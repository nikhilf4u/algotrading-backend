package com.trading.algotrading.serviceimpl;

import com.trading.algotrading.dao.TotalOIDao;
import com.trading.algotrading.dto.TotalOIDto;
import com.trading.algotrading.dto.TotalOITrendDto;
import com.trading.algotrading.model.TotalOI;
import com.trading.algotrading.service.TotalOIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class TotalOIServiceImpl implements TotalOIService {
    @Autowired
    TotalOIDao totalOIDao;

    @Override
    public TotalOITrendDto getTotalOITrendData(String index){
        List<TotalOIDto> totalOIDtos=totalOIDao.getOITrendData(index);
        TotalOITrendDto totalOITrendDto=new TotalOITrendDto();
        List<Integer> totalCallOIList=new ArrayList<>();
        List<Integer> totalPutOIList=new ArrayList<>();
        List<String> timeList=new ArrayList<>();
        for (TotalOIDto totalOIDto :totalOIDtos) {
            totalCallOIList.add(totalOIDto.getTotalCallOI());
            totalPutOIList.add(totalOIDto.getTotalPutOI());
            timeList.add(totalOIDto.getTime());
        }
        totalOITrendDto.setTotalCallOIList(totalCallOIList);
        totalOITrendDto.setTotalPutOIList(totalPutOIList);
        totalOITrendDto.setTimeList(timeList);
        return totalOITrendDto;
    }

    @Override
    public Boolean saveTotalOIData(Integer totalCallOI, Integer totalPutOI, String time,String index)
    {
        TotalOI totalOI=new TotalOI();
        totalOI.setTotalCallOI(totalCallOI);
        totalOI.setTotalPutOI(totalPutOI);
        totalOI.setTime(time);
        totalOI.setIndex(index);
        totalOIDao.save(totalOI);
        return true;
    }
}
