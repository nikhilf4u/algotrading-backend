package com.trading.algotrading.resolvers;

import com.trading.algotrading.dto.OIChangeDto;
import com.trading.algotrading.dto.OptionChainDto;
import com.trading.algotrading.dto.OptionChainForStrikePriceDto;
import com.trading.algotrading.dto.TotalOITrendDto;
import com.trading.algotrading.service.OptionChainService;
import com.trading.algotrading.service.TotalOIService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class QueryResolver implements GraphQLQueryResolver {
    @Autowired
    OptionChainService optionChainService;
    @Autowired
    TotalOIService totalOIService;
    public List<OptionChainDto> getOptionChainData()
    {
        System.out.print(">>> getAllPriceAndOpenInterest");
        return optionChainService.getOptionChainData();
    }
    public TotalOITrendDto getTotalOITrendData(String index)
    {
        return totalOIService.getTotalOITrendData(index);
    }

    public OptionChainForStrikePriceDto getOptionChainDataForStrikePrice(String strikePrice)
    {
        return optionChainService.getOptionChainDataForStrikePrice(strikePrice);
    }

    public OIChangeDto getChangeInOIForIndex(String index)
    {
        return optionChainService.getChangeInOIForIndex(index);
    }
}
