package com.trading.algotrading.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.algotrading.dao.Atp2PrevDetailsDao;
import com.trading.algotrading.dao.Atp3InitialDetailDao;
import com.trading.algotrading.dao.ContractDao;
import com.trading.algotrading.dao.ContractDataDao;
import com.trading.algotrading.dto.*;
import com.trading.algotrading.model.Atp2PrevDetails;
import com.trading.algotrading.model.Atp3InitialDetails;
import com.trading.algotrading.model.Contract;
import com.trading.algotrading.model.ContractData;
import com.trading.algotrading.service.ContractDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContractDataServiceImpl implements ContractDataService {

    @Autowired
    ContractDataDao contractDataDao;

    @Autowired
    Atp2PrevDetailsDao atp2PrevDetailsDao;

    @Autowired
    Atp3InitialDetailDao atp3InitialDetailDao;
    String niftyExipryDate = null;
    String bankNiftyExpiryDate = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String currentDate = LocalDate.now().format(formatter) + "%";


    Integer niftySpotMarketPrice = 0;

    @Override
    public List<AtpDataDto> getAtpData() {
        List<AtpDataDto> atpDataDtoList = new ArrayList<>();
//        niftySpotMarketPrice = 23000;
        List<ContractAtpDataDto> contractAtpDataDtoList = contractDataDao.getAtpData(niftySpotMarketPrice-500,niftySpotMarketPrice+500);
        for(ContractAtpDataDto contractAtpDataDto:contractAtpDataDtoList) {
            AtpDataDto atpDataDto = new AtpDataDto();
            atpDataDto.setStrikePrice(contractAtpDataDto.getStrikePrice());
            atpDataDto.setOptionType(contractAtpDataDto.getOptionType());
            atpDataDto.setLastPrice(contractAtpDataDto.getLastPrice());
            atpDataDto.setAtp3(contractAtpDataDto.getAtp3());
            atpDataDto.setAtp1(contractAtpDataDto.getAtp1());
            atpDataDto.setTime(contractAtpDataDto.getTime());
            atpDataDtoList.add(atpDataDto);
        }
        return atpDataDtoList;
    }

    @Override
    public StrikePriceContractDataDto getContractDataForIndexAndStrikePrice(String index, Integer strikePrice, boolean displayDataForSelectedStrikePrice) {
//        currentDate = "2024-06-03%";
        StrikePriceContractDataDto strikePriceContractDataDto = new StrikePriceContractDataDto();
        strikePriceContractDataDto.setSelectedStrikePrice(displayDataForSelectedStrikePrice ? strikePrice : niftySpotMarketPrice);
        List<ContractDataDto> contractDataCallList = contractDataDao.getContractData(index,strikePriceContractDataDto.getSelectedStrikePrice()-300,"Call",currentDate);
        List<ContractDataDto> contractDataPutList = contractDataDao.getContractData(index,strikePriceContractDataDto.getSelectedStrikePrice()+200,"Put",currentDate);
        List<Double> callLastPriceList=new ArrayList<>();
        List<Double> callMarketPriceValueList=new ArrayList<>();
        List<String> callTimeList=new ArrayList<>();
        List<Double> callAtp1List=new ArrayList<>();
        List<Double> callAtp1ValueList=new ArrayList<>();
        List<Double> callAtp3List=new ArrayList<>();
        List<Double> callAtp3ValueList=new ArrayList<>();
        List<Double> putLastPriceList=new ArrayList<>();
        List<Double> putMarketPriceValueList=new ArrayList<>();
        List<String> putTimeList=new ArrayList<>();
        List<Double> putAtp1List=new ArrayList<>();
        List<Double> putAtp1ValueList=new ArrayList<>();
        List<Double> putAtp3List=new ArrayList<>();
        List<Double> putAtp3ValueList=new ArrayList<>();

        for (ContractDataDto contractDataDto: contractDataCallList) {
            callLastPriceList.add(contractDataDto.getLastPrice());
            callMarketPriceValueList.add(contractDataDto.getMarketPriceValue());
            callAtp1List.add(contractDataDto.getAtp1());
            callAtp1ValueList.add(contractDataDto.getAtp1Value());
            callAtp3List.add(contractDataDto.getAtp3());
            callAtp3ValueList.add(contractDataDto.getAtp3Value());
            callTimeList.add(contractDataDto.getTime());
        }
        for (ContractDataDto contractDataDto: contractDataPutList) {
            putLastPriceList.add(contractDataDto.getLastPrice());
            putMarketPriceValueList.add(contractDataDto.getMarketPriceValue());
            putAtp1List.add(contractDataDto.getAtp1());
            putAtp1ValueList.add(contractDataDto.getAtp1Value());
            putAtp3List.add(contractDataDto.getAtp3());
            putAtp3ValueList.add(contractDataDto.getAtp3Value());
            putTimeList.add(contractDataDto.getTime());
        }
        strikePriceContractDataDto.setCallTimeList(callTimeList);
        strikePriceContractDataDto.setPutTimeList(putTimeList);
        strikePriceContractDataDto.setCallLastPriceList(callLastPriceList);
        strikePriceContractDataDto.setPutLastPriceList(putLastPriceList);
        strikePriceContractDataDto.setCallMarketPriceValueList(callMarketPriceValueList);
        strikePriceContractDataDto.setPutMarketPriceValueList(putMarketPriceValueList);
        strikePriceContractDataDto.setCallATP1List(callAtp1List);
        strikePriceContractDataDto.setPutATP1List(putAtp1List);
        strikePriceContractDataDto.setCallAtp1ValueList(callAtp1ValueList);
        strikePriceContractDataDto.setPutAtp1ValueList(putAtp1ValueList);
        strikePriceContractDataDto.setCallATP3List(callAtp3List);
        strikePriceContractDataDto.setPutATP3List(putAtp3List);
        strikePriceContractDataDto.setCallAtp3ValueList(callAtp3ValueList);
        strikePriceContractDataDto.setPutAtp3ValueList(putAtp3ValueList);
     return strikePriceContractDataDto;
    }

//    @Scheduled(fixedRate = 300000)
    public void dataCorrection() throws Exception{
        List<DataCorrectionDto> nseDataCorrectionDtos = contractDataDao.getContractDataForCorrection();
        for(DataCorrectionDto nseContractDataDto:nseDataCorrectionDtos) {
            if(nseContractDataDto.getAtp1() == 0.0) continue;
            Atp3InitialDetailsDto atp3InitialValue = atp3InitialDetailDao.getAtp3Details("06-Jun-2024", nseContractDataDto.getOptionType(), nseContractDataDto.getStrikePrice());
            if (atp3InitialValue.getReset() == 0) {
                Atp3AndTotalOIDto atp3AndTotalOIDto = contractDataDao.getLastAtp3AndTotalOpenInterestValue(nseContractDataDto.getStrikePrice(), nseContractDataDto.getOptionType());
                atp3InitialDetailDao.updateAtp3AndTotalOpenInterestValue(atp3AndTotalOIDto.getAtp3Value(), atp3AndTotalOIDto.getTotalOpenInterest(), "06-Jun-2024", nseContractDataDto.getOptionType(), nseContractDataDto.getStrikePrice());
                Double changeInOi = nseContractDataDto.getOpenInterest() - atp3AndTotalOIDto.getTotalOpenInterest();
                Double atp3Value = atp3AndTotalOIDto.getAtp3Value() + (changeInOi * nseContractDataDto.getAtp1() * 25);
//                contractData.setAtp3Value(atp3Value);
                Double atp3 = 0.0;
                if (atp3Value != 0.0) {
                    atp3 = atp3Value / (nseContractDataDto.getOpenInterest() * 25);
//                    contractData.setAtp3(atp3);
                }
                System.out.println(atp3+" - "+atp3Value+" - "+nseContractDataDto.getId()+" "+nseContractDataDto.getStrikePrice()+" "+nseContractDataDto.getOptionType()+" "+nseContractDataDto.getTime());
                contractDataDao.correctAtp3Values(atp3,atp3Value,nseContractDataDto.getId());
            } else {
                Double changeInOi = nseContractDataDto.getOpenInterest() - atp3InitialValue.getTotalOpenInterest();
                Double atp3Value = atp3InitialValue.getAtp3Value() + (changeInOi * nseContractDataDto.getAtp1() * 25);
//                contractData.setAtp3Value(atp3Value);
                Double atp3 = 0.0;
                if (atp3Value != 0.0) {
                    atp3 = atp3Value / (nseContractDataDto.getOpenInterest() * 25);
//                    contractData.setAtp3(atp3);
                }
//                else contractData.setAtp3(0.0);
                System.out.println(atp3+" "+atp3Value+" "+nseContractDataDto.getId()+" "+nseContractDataDto.getStrikePrice()+" "+nseContractDataDto.getOptionType()+" "+nseContractDataDto.getTime());
                contractDataDao.correctAtp3Values(atp3,atp3Value,nseContractDataDto.getId());
            }
        }
        System.out.println("------------------ending-----------------");
    }

    @Override
    @Scheduled(fixedRate = 300000)
    public void saveNiftyContractData() throws IOException, ParseException {
        String url = "https://www.nseindia.com//api/liveEquity-derivatives?index=nse50_opt";
        NseContractDataMapper data = getDataFromNse(url);
        if (data == null) return;
        List<ContractData> contracts = new ArrayList<>();
        if (niftyExipryDate == null) {
            niftyExipryDate = getMinDate(data.getData());
        }
        niftyExipryDate = "04-Jul-2024";
        for (NseContractDataDto nseContractDataDto : data.getData()) {
            if (nseContractDataDto.getExpiryDate().equalsIgnoreCase(niftyExipryDate)) {
                ContractData contractData = new ContractData();
                contractData.setNseContractValue(nseContractDataDto.getValue());
                contractData.setNseTotalOpenInterest(nseContractDataDto.getOpenInterest());
                contractData.setNseVolume(nseContractDataDto.getVolume());
                contractData.setNseLastPrice(nseContractDataDto.getLastPrice());
                contractData.setStrikePrice(nseContractDataDto.getStrikePrice());
                contractData.setOptionType(nseContractDataDto.getOptionType());
                contractData.setMarketPriceValue(contractData.getNseLastPrice() * contractData.getNseTotalOpenInterest() * 25);
                contractData.setIndexType(nseContractDataDto.getUnderlying());
                if (nseContractDataDto.getVolume() != 0) {
                    contractData.setAtp1(nseContractDataDto.getValue() / nseContractDataDto.getVolume());
                } else {
                    contractData.setAtp1(0.0);
                }
                contractData.setAtp1Value(contractData.getAtp1() * nseContractDataDto.getOpenInterest() * 25);
                Atp2PrevDetailsDto atp2PrevDetailsDto = atp2PrevDetailsDao.getAtp2Details(niftyExipryDate, nseContractDataDto.getOptionType(), nseContractDataDto.getStrikePrice());
                if (atp2PrevDetailsDto == null) {
                    Atp2PrevDetails atp2PrevDetails = new Atp2PrevDetails();
                    atp2PrevDetails.setAtp2Value(contractData.getAtp1Value());
                    atp2PrevDetails.setNseContractValue(nseContractDataDto.getValue());
                    atp2PrevDetails.setNseVolume(nseContractDataDto.getVolume());
                    atp2PrevDetails.setNseTotalOpenInterest(nseContractDataDto.getOpenInterest());
                    atp2PrevDetails.setExpiryDate(niftyExipryDate);
                    atp2PrevDetails.setOptionType(nseContractDataDto.getOptionType());
                    atp2PrevDetails.setStrikePrice(nseContractDataDto.getStrikePrice());
                    atp2PrevDetailsDao.save(atp2PrevDetails);
                    contractData.setAtp2(contractData.getAtp1());
                    contractData.setAtp2Value(contractData.getAtp1Value());
                } else {
                    Double nseContractValue = nseContractDataDto.getValue() - atp2PrevDetailsDto.getNseContractValue();
                    Double nseVolume = nseContractDataDto.getVolume() - atp2PrevDetailsDto.getNseVolume();
                    Double nseTotalOpenInterest = nseContractDataDto.getOpenInterest() - atp2PrevDetailsDto.getNseTotalOpenInterest();
                    if (nseVolume != 0) {
                        contractData.setAtp2(nseContractValue / nseVolume);
                    } else {
                        contractData.setAtp2(0.0);
                    }
                    Double atp2Value = contractData.getAtp2() * nseTotalOpenInterest * 25;
                    atp2Value += atp2PrevDetailsDto.getAtp2Value();
                    contractData.setAtp2Value(atp2Value);
                    atp2PrevDetailsDao.updateAtp2Details(nseContractDataDto.getValue(), nseContractDataDto.getOpenInterest(), nseContractDataDto.getVolume(), contractData.getAtp2Value(), niftyExipryDate, nseContractDataDto.getOptionType(), nseContractDataDto.getStrikePrice());
                }
                Atp3InitialDetailsDto atp3InitialValue = atp3InitialDetailDao.getAtp3Details(niftyExipryDate, nseContractDataDto.getOptionType(), nseContractDataDto.getStrikePrice());
                if (atp3InitialValue == null) {
                    Atp3InitialDetails atp3InitialDetails = new Atp3InitialDetails();
                    atp3InitialDetails.setAtp3Value(contractData.getAtp1Value());
                    atp3InitialDetails.setExpiryDate(niftyExipryDate);
                    atp3InitialDetails.setOptionType(nseContractDataDto.getOptionType());
                    atp3InitialDetails.setStrikePrice(nseContractDataDto.getStrikePrice());
                    atp3InitialDetails.setTotalOpenInterest(nseContractDataDto.getOpenInterest());
                    atp3InitialDetails.setReset(1);
                    atp3InitialDetailDao.save(atp3InitialDetails);
                    contractData.setAtp3(contractData.getAtp1());
                    contractData.setAtp3Value(contractData.getAtp1Value());
                } else if (atp3InitialValue.getReset() == 0) {
                    Atp3AndTotalOIDto atp3AndTotalOIDto = contractDataDao.getLastAtp3AndTotalOpenInterestValue(nseContractDataDto.getStrikePrice(), nseContractDataDto.getOptionType());
                    atp3InitialDetailDao.updateAtp3AndTotalOpenInterestValue(atp3AndTotalOIDto.getAtp3Value(), atp3AndTotalOIDto.getTotalOpenInterest(), niftyExipryDate, nseContractDataDto.getOptionType(), nseContractDataDto.getStrikePrice());
                    Double changeInOi = nseContractDataDto.getOpenInterest() - atp3AndTotalOIDto.getTotalOpenInterest();
                    Double atp3Value = atp3AndTotalOIDto.getAtp3Value() + (changeInOi * contractData.getAtp1() * 25);
                    contractData.setAtp3Value(atp3Value);
                    if (atp3Value != 0.0) {
                        Double atp3 = atp3Value / (nseContractDataDto.getOpenInterest() * 25);
                        contractData.setAtp3(atp3);
                    } else contractData.setAtp3(0.0);
                } else {
                    Double changeInOi = nseContractDataDto.getOpenInterest() - atp3InitialValue.getTotalOpenInterest();
                    Double atp3Value = atp3InitialValue.getAtp3Value() + (changeInOi * contractData.getAtp1() * 25);
                    contractData.setAtp3Value(atp3Value);
                    if (atp3Value != 0.0) {
                        Double atp3 = atp3Value / (nseContractDataDto.getOpenInterest() * 25);
                        contractData.setAtp3(atp3);
                    } else contractData.setAtp3(0.0);
                }

                contractData.setTime(data.getTimestamp().split(" ")[1]);
                contracts.add(contractData);
                niftySpotMarketPrice = roundToNearestDivisibleBy100(nseContractDataDto.getUnderlyingValue());
            }
        }
        contractDataDao.saveAll(contracts);
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

    public NseContractDataMapper getDataFromNse(String indexUrl) throws IOException {
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

            URL url = new URL(indexUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // Set common request properties
            setCommonRequestProperties(httpURLConnection);
            // Set cookies obtained from the initial connection
            httpURLConnection.setRequestProperty("Cookie", cookieList.stream().filter(Objects::nonNull)
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(";")));

            InputStream inputStream = httpURLConnection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            NseContractDataMapper data = mapper.readValue(inputStream, NseContractDataMapper.class);
            if (data != null) System.out.println(data.getTimestamp());
            return data;
        } catch (Exception e) {
            System.out.println("Exception Occured while getting data for " + indexUrl);
            e.printStackTrace();
        }
        return null;
    }

    public String getMinDate(List<NseContractDataDto> nseContractDtoList) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date minDate = null;
        for (NseContractDataDto contractDto : nseContractDtoList) {
            Date date = sdf.parse(contractDto.getExpiryDate());
            if (minDate == null || date.before(minDate)) {
                minDate = date;
            }
        }
        return sdf.format(minDate);
    }

    @Override
    public void saveNiftyBankContractData() throws IOException {

    }

    @Override
    public List<Integer> getContractDataStrikePriceList(String indexType) {
        return contractDataDao.getStrikePriceList(indexType);
    }

    @Override
    public ContractSummaryDto getContractSummary() {
        return null;
    }
}
