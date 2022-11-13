package com.trading.algotrading.resolvers;

import com.trading.algotrading.dto.OIChangeDto;
import com.trading.algotrading.dto.OptionChainDto;
import com.trading.algotrading.dto.OptionChainForStrikePriceDto;
import com.trading.algotrading.service.OptionChainService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class QueryResolver implements GraphQLQueryResolver {
    @Autowired
    OptionChainService optionChainService;
    public List<OptionChainDto> getOptionChainData()
    {
        System.out.print(">>> getAllPriceAndOpenInterest");
        return optionChainService.getOptionChainData();
    }
    public OptionChainForStrikePriceDto getOptionChainDataForStrikePrice(String strikePrice)
    {
        return optionChainService.getOptionChainDataForStrikePrice(strikePrice);
    }

    public OIChangeDto getChangeInOIForIndex(String index)
    {
        return optionChainService.getChangeInOIForIndex(index);
    }

    public  List<String> getStrikePriceForBuying(Integer underlyingValue,String indexType,Integer expiryDaysCount)
    {
        return  optionChainService.getStrikePriceForBuying(underlyingValue,indexType,expiryDaysCount);
    }
}
