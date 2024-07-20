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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    String niftyExipryDate = null;
    String bankNiftyExpiryDate = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String currentDate =  LocalDate.now().format(formatter)+"%";

    Integer niftySpotMarketPrice = 0;

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
    public ContractForStrikePriceDto getContractsForIndexAndStrikePrice(String index, String strikePrice,boolean displayDataForSelectedStrikePrice) {
        Date date = new Date();
//        System.out.println("------------"+date.getMinutes()+" "+date.getSeconds());
        ContractForStrikePriceDto contractForStrikePriceDto = new ContractForStrikePriceDto();
        contractForStrikePriceDto.setSelectedStrikePrice(displayDataForSelectedStrikePrice ? strikePrice : niftySpotMarketPrice.toString());
        List<ContractDto> contractCallList = contractDao.getContracts(index,contractForStrikePriceDto.getSelectedStrikePrice(),"Call",currentDate);
        List<ContractDto> contractPutList = contractDao.getContracts(index,contractForStrikePriceDto.getSelectedStrikePrice(),"Put",currentDate);
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
//        System.out.println("------------"+date.getMinutes()+" "+date.getSeconds());
        return contractForStrikePriceDto;
    }

    @Override
//    @Scheduled(fixedRate = 300000)
    public void saveNiftyBankContracts() {
        try {
//            System.out.println(new Date().getSeconds());
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            HttpURLConnection baseUrlConnection = (HttpURLConnection) new URL("https://www.nseindia.com/").openConnection();
            // Set common request properties
            setCommonRequestProperties(baseUrlConnection);

            // Establish initial connection to obtain cookies
            baseUrlConnection.getContent();
            List<HttpCookie> cookieList = cookieManager.getCookieStore().getCookies();

            URL url = new URL("https://www.nseindia.com//api/liveEquity-derivatives?index=nifty_bank_opt");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // Set common request properties
            setCommonRequestProperties(httpURLConnection);
            // Set cookies obtained from the initial connection
            httpURLConnection.setRequestProperty("Cookie", cookieList.stream().filter(Objects::nonNull)
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(";")));

            InputStream inputStream = httpURLConnection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            NseContractMapper data = mapper.readValue(inputStream, NseContractMapper.class);
//            System.out.println(data);
            List<Contract> contracts = new ArrayList<>();
            if(bankNiftyExpiryDate == null) {
                bankNiftyExpiryDate = getMinDate(data.getData());
            }

            for(NseContractDto nseContractDto : data.getData()) {
//                System.out.println(nseContractDto.getExpiryDate()+ " "+ nseContractDto.getUnderlying());
                if(nseContractDto.getExpiryDate().equalsIgnoreCase(bankNiftyExpiryDate)) {
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
                    contracts.add(contract);
                }
            }
            contractDao.saveAll(contracts);
//            System.out.println(new Date().getSeconds());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMinDate(List<NseContractDto> nseContractDtoList) throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
            Date minDate = null;
            for (NseContractDto contractDto : nseContractDtoList) {
                Date date = sdf.parse(contractDto.getExpiryDate());
                if (minDate == null || date.before(minDate)) {
                    minDate = date;
                }
            }
            return sdf.format(minDate);
    }



    @Override
//    @Scheduled(fixedRate = 300000)
    public void saveNiftyContracts() {
        try {
//            System.out.println(new Date().getSeconds());
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            HttpURLConnection baseUrlConnection = (HttpURLConnection) new URL("https://www.nseindia.com/").openConnection();
            // Set common request properties
            setCommonRequestProperties(baseUrlConnection);

            // Establish initial connection to obtain cookies
            baseUrlConnection.getContent();
            List<HttpCookie> cookieList = cookieManager.getCookieStore().getCookies();

            URL url = new URL("https://www.nseindia.com//api/liveEquity-derivatives?index=nse50_opt");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // Set common request properties
            setCommonRequestProperties(httpURLConnection);
            // Set cookies obtained from the initial connection
            httpURLConnection.setRequestProperty("Cookie", cookieList.stream().filter(Objects::nonNull)
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(";")));

            InputStream inputStream = httpURLConnection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            NseContractMapper data = mapper.readValue(inputStream, NseContractMapper.class);
            System.out.println(data.getTimestamp());
            List<Contract> contracts = new ArrayList<>();
            if(niftyExipryDate == null) {
                niftyExipryDate = getMinDate(data.getData());
            }
            //        niftyExipryDate = "06-Jun-2024";
            for(NseContractDto nseContractDto : data.getData()) {
                if(nseContractDto.getExpiryDate().equalsIgnoreCase(niftyExipryDate)) {
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
                    contracts.add(contract);
                    niftySpotMarketPrice = roundToNearestDivisibleBy100(nseContractDto.getUnderlyingValue());
                }
            }
            contractDao.saveAll(contracts);
//            System.out.println(new Date().getSeconds());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int roundToNearestDivisibleBy100(double value) {
        // Divide the value by 100
//        value = 1234.56; // Example double value
        int roundedValue = (int) Math.round(value); // Round the double to the nearest integer
        int divisibleBy100 = (roundedValue + 50) / 100 * 100;

        return divisibleBy100;
    }
    private void setCommonRequestProperties(HttpURLConnection connection) {
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Cache-Control", "max-age=0");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                        + " Chrome/89.0.4389.114 Safari/537.36");
        connection.setRequestProperty(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
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
//            System.out.println("Data has been written to " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}