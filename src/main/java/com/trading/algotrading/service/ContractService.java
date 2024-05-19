package com.trading.algotrading.service;

import com.trading.algotrading.dto.ContractForStrikePriceDto;
import com.trading.algotrading.dto.ContractInputDto;
import com.trading.algotrading.dto.ContractSummaryDto;

import java.io.IOException;
import java.util.List;

public interface ContractService {

    ContractForStrikePriceDto getContractsForIndexAndStrikePrice(String index,String strikePrice,boolean displayDataForSelectedStrikePrice);
    void saveNiftyContracts() throws IOException;

    void saveNiftyBankContracts() throws IOException;

    List<String> getStrikePriceList(String indexType);

    ContractSummaryDto getContractSummary();
}
