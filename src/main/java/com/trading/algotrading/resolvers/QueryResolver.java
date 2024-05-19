package com.trading.algotrading.resolvers;

import com.trading.algotrading.dto.*;
import com.trading.algotrading.service.ContractService;
import com.trading.algotrading.service.OptionChainService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class QueryResolver implements GraphQLQueryResolver {
    @Autowired
    OptionChainService optionChainService;

    @Autowired
    ContractService contractService;
    public List<OptionChainDto> getOptionChainData()
    {
        System.out.print(">>> getAllPriceAndOpenInterest");
        return optionChainService.getOptionChainData();
    }
    public OptionChainForStrikePriceDto getOptionChainDataForStrikePrice(Integer strikePrice)
    {
        return optionChainService.getOptionChainDataForStrikePrice(strikePrice);
    }

    public ContractForStrikePriceDto getContractsForIndexAndStrikePrice(String indexType,String strikePrice,boolean displayDataForSelectedStrikePrice)
    {
        return contractService.getContractsForIndexAndStrikePrice(indexType,strikePrice,displayDataForSelectedStrikePrice);
    }

    public OIChangeDto getChangeInOIForIndex(String index)
    {
        return optionChainService.getChangeInOIForIndex(index);
    }

    public List<StrikePriceOutputDto> getDataForStrikePriceSelection(String indexType,String actionType,String optionType)
    {
        return optionChainService.getDataForStrikePriceSelection(indexType,actionType,optionType);
    }

    public List<String> getStrikePriceList(String indexType) {
        return contractService.getStrikePriceList(indexType);
    }

    public ContractSummaryDto getContractSummary() {
        return contractService.getContractSummary();
    }

}
