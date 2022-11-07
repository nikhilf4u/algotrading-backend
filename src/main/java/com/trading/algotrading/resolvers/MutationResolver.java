package com.trading.algotrading.resolvers;

import com.trading.algotrading.dto.OptionChainInputDto;
import com.trading.algotrading.dto.TotalOITrendDto;
import com.trading.algotrading.service.OptionChainService;
import com.trading.algotrading.service.TotalOIService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MutationResolver implements GraphQLMutationResolver {
    @Autowired
    OptionChainService optionChainService;

    @Autowired
    TotalOIService totalOIService;
    public Boolean saveOptionChainData(List<OptionChainInputDto> optionChainInputDtos){
        return optionChainService.saveOptionChainData(optionChainInputDtos);
    }
    public Boolean saveTotalOIData(Integer totalCallOI,Integer totalPutOI,String time,String index){
        return totalOIService.saveTotalOIData(totalCallOI,totalPutOI,time,index);
    }


}
