package com.trading.algotrading.service;

import com.trading.algotrading.dto.*;

import java.util.List;

public interface OptionChainService {
    List<OptionChainDto> getOptionChainData();
    Boolean saveOptionChainData(List<OptionChainInputDto> priceAndOpenInterestInputDtos);

    OptionChainForStrikePriceDto getOptionChainDataForStrikePrice(String strikePrice);

    OIChangeDto getChangeInOIForIndex(String index);

    List<StrikePriceOutputDto> getDataForStrikePriceSelection(String indexType,String actionType,String optionType);

    OIChangeDto getChangeInOIForStrikePrice(Integer strikePrice);
}
