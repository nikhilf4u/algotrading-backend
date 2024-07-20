package com.trading.algotrading.service;

import com.trading.algotrading.dto.AtpDataDto;
import com.trading.algotrading.dto.ContractSummaryDto;
import com.trading.algotrading.dto.StrikePriceContractDataDto;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface ContractDataService {

    StrikePriceContractDataDto getContractDataForIndexAndStrikePrice(String index, Integer strikePrice, boolean displayDataForSelectedStrikePrice);

    void saveNiftyContractData() throws IOException, ParseException;

    void saveNiftyBankContractData() throws IOException;

    List<Integer> getContractDataStrikePriceList(String indexType);

    List<AtpDataDto> getAtpData();

    ContractSummaryDto getContractSummary();
}
