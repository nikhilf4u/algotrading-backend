package com.trading.algotrading.service;

import com.trading.algotrading.dto.OIChangeDto;
import com.trading.algotrading.dto.OptionChainDto;
import com.trading.algotrading.dto.OptionChainForStrikePriceDto;
import com.trading.algotrading.dto.OptionChainInputDto;

import java.util.List;

public interface OptionChainService {
    List<OptionChainDto> getOptionChainData();
    Boolean saveOptionChainData(List<OptionChainInputDto> priceAndOpenInterestInputDtos);

    OptionChainForStrikePriceDto getOptionChainDataForStrikePrice(String strikePrice);

    OIChangeDto getChangeInOIForIndex(String index);

    List<String> getStrikePriceForBuying(Integer underlyingValue,String indexType,Integer expiryDaysCount);
}
