package com.trading.algotrading.serviceimpl;

import com.fasterxml.jackson.core.JsonParser;
import com.trading.algotrading.dao.ContractDao;
import com.trading.algotrading.dto.*;
import com.trading.algotrading.model.Contract;
import com.trading.algotrading.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.*;
import java.util.List;
import java.util.stream.Collectors;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    @Autowired
    ContractDao contractDao;

    @Override
    public ContractSummaryDto getContractSummary() {
        List<OverallSummaryDto> overallSummaryDtos = contractDao.getOverallSummary();
        ContractSummaryDto contractSummaryDto = new ContractSummaryDto();
        List<String> callAtpSumList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        List<String> putAtpSumList = new ArrayList<>();
        for(OverallSummaryDto overallSummaryDto:overallSummaryDtos) {
            if(overallSummaryDto.getOptionType().equalsIgnoreCase("call")) {
                callAtpSumList.add(overallSummaryDto.getAtpValueSum());
                timeList.add(overallSummaryDto.getTime());
            } else {
                putAtpSumList.add(overallSummaryDto.getAtpValueSum());
            }

        }
        contractSummaryDto.setCallAtpSumList(callAtpSumList);
        contractSummaryDto.setTimeList(timeList);
        contractSummaryDto.setPutAtpSumList(putAtpSumList);
        return contractSummaryDto;
    }

    @Override
    public ContractForStrikePriceDto getContractsForIndexAndStrikePrice(String index, String strikePrice) {
        List<ContractDto> contractCallList = contractDao.getContracts(index,strikePrice,"Call");
        List<ContractDto> contractPutList = contractDao.getContracts(index,strikePrice,"Put");
        List<String> callPriceList=new ArrayList<>();
        List<String> callOpenInterestList=new ArrayList<>();
        List<String> callTimeList=new ArrayList<>();
        List<String> callValueList=new ArrayList<>();
        List<String> callAtpList=new ArrayList<>();
        List<String> putPriceList=new ArrayList<>();
        List<String> putOpenInterestList=new ArrayList<>();
        List<String> putTimeList=new ArrayList<>();
        List<String> putValueList=new ArrayList<>();
        List<String> putAtpList=new ArrayList<>();
        ContractForStrikePriceDto contractForStrikePriceDto = new ContractForStrikePriceDto();
        for (ContractDto contractDto: contractCallList) {
            callAtpList.add(contractDto.getAtp());
            callValueList.add(contractDto.getValue());
            callTimeList.add(contractDto.getTime().substring(0,5));
            callOpenInterestList.add(contractDto.getOpenInterest());
            callPriceList.add(contractDto.getPrice());
        }
        for (ContractDto contractDto: contractPutList) {
            putAtpList.add(contractDto.getAtp());
            putValueList.add(contractDto.getValue());
            putTimeList.add(contractDto.getTime().substring(0,5));
            putOpenInterestList.add(contractDto.getOpenInterest());
            putPriceList.add(contractDto.getPrice());
        }
        contractForStrikePriceDto.setCallPriceList(callPriceList);
        contractForStrikePriceDto.setCallTimeList(callTimeList);
        contractForStrikePriceDto.setCallOpenInterestList(callOpenInterestList);
        contractForStrikePriceDto.setCallAtpList(callAtpList);
        contractForStrikePriceDto.setCallValueList(callValueList);
        contractForStrikePriceDto.setPutPriceList(putPriceList);
        contractForStrikePriceDto.setPutTimeList(putTimeList);
        contractForStrikePriceDto.setPutOpenInterestList(putOpenInterestList);
        contractForStrikePriceDto.setPutAtpList(putAtpList);
        contractForStrikePriceDto.setPutValueList(putValueList);
        System.out.println("Put ATP Max:"+Collections.max(putValueList));
        System.out.println("Call ATP Max:"+Collections.max(callValueList));
        System.out.println("Put Open Interest Max:"+Collections.max(putOpenInterestList));
        System.out.println("Call Open Interest Max:"+Collections.max(callOpenInterestList));
        System.out.println("Put ATP Max:"+Collections.min(putValueList));
        System.out.println("Call ATP Max:"+Collections.min(callValueList));
        System.out.println("Put Open Interest Max:"+Collections.min(putOpenInterestList));
        System.out.println("Call Open Interest Max:"+Collections.min(callOpenInterestList));
        return contractForStrikePriceDto;
    }

    @Override
//    @Scheduled(fixedRate = 300000)
    public void saveContracts() throws IOException {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        HttpURLConnection baseUrlConnection = (HttpURLConnection) new URL("https://www.nseindia.com/").openConnection();
        baseUrlConnection.setRequestProperty("Connection", "keep-alive");
        baseUrlConnection.setRequestProperty("Cache-Control", "max-age=0");
        baseUrlConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        baseUrlConnection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                        + " Chrome/89.0.4389.114 Safari/537.36");
        baseUrlConnection.setRequestProperty(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        baseUrlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        baseUrlConnection.getContent();
        List<HttpCookie> cookieList = cookieManager.getCookieStore().getCookies();

        URL url = new URL("https://www.nseindia.com//api/liveEquity-derivatives?index=nse50_opt");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Cookie", cookieList.stream().filter(Objects::nonNull)
                .map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(";")));
        httpURLConnection.setRequestProperty("Connection", "keep-alive");
        httpURLConnection.setRequestProperty("Cache-Control", "max-age=0");
        httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        httpURLConnection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                        + " Chrome/89.0.4389.114 Safari/537.36");
        httpURLConnection.setRequestProperty(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        InputStream inputStream = httpURLConnection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        NseContractMapper data = mapper.readValue(inputStream, NseContractMapper.class);
        System.out.println(data.getTimestamp());
        List<Contract> contracts = new ArrayList<>();
        for(NseContractDto nseContractDto : data.getData()) {
            if(nseContractDto.getExpiryDate().equalsIgnoreCase("04-Apr-2024")) {
                Contract contract = new Contract();
                contract.setIndexType(nseContractDto.getUnderlying());
                if(nseContractDto.getVolume() != 0){
                    contract.setAtp(String.valueOf(nseContractDto.getValue() / nseContractDto.getVolume()));
                } else {
                    contract.setAtp("0.0");
                }
                contract.setTime(data.getTimestamp().split(" ")[1]);
                contract.setValue(String.valueOf(Float.parseFloat(contract.getAtp()) * nseContractDto.getOpenInterest() * 50));
                contract.setPrice(String.valueOf(nseContractDto.getLastPrice()));
                contract.setStrikePrice(String.valueOf(nseContractDto.getStrikePrice()));
                contract.setOptionType(String.valueOf(nseContractDto.getOptionType()));
                contract.setOpenInterest(String.valueOf(nseContractDto.getOpenInterest()*nseContractDto.getLastPrice()*50));
//                contract.setOpenInterest(String.valueOf(nseContractDto.getOpenInterest()*50));

                contracts.add(contract);
            }
        }
        contractDao.saveAll(contracts);
    }
    @Override
    public List<String> getStrikePriceList(String indexType) {
        return contractDao.getStrikePriceList(indexType);
    }
    private static void parseHtmlAndWriteToFile(String htmlContent, String outputFile) {
        try {
            Document document = Jsoup.parse(htmlContent);
            Elements stockRows = document.select("table tr");

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            for (Element stockRow : stockRows) {
                Elements columns = stockRow.select("td");

                if (columns.size() > 1) {
                    String stockSymbol = columns.get(0).text();
                    String stockName = columns.get(1).text();

                    // Writing data to the text file
                    writer.write(stockSymbol + "\t" + stockName);
                    writer.newLine();
                }
            }

            writer.close();
            System.out.println("Data has been written to " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}