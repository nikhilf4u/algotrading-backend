package com.trading.algotrading.serviceimpl;


import com.trading.algotrading.dao.OptionChainDao;
import com.trading.algotrading.dto.*;
import com.trading.algotrading.model.OptionChain;
import com.trading.algotrading.service.OptionChainService;
import com.trading.algotrading.utils.Constanst;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OptionChainServiceImpl implements OptionChainService {
    @Autowired
    OptionChainDao optionChainDao;

    @Autowired
    ModelMapper modelMapper;
    @Override
    public List<OptionChainDto> getOptionChainData()
    {
        return optionChainDao.getOptionChainData();
    }

    @Override
    public Boolean saveOptionChainData(List<OptionChainInputDto> optionChainInputDtos)
    {
        DecimalFormat formatToTwoDecimal = new DecimalFormat("0.00");
        DecimalFormat formatToThreeDecimal = new DecimalFormat("0.000");
        List<OptionChain> optionChainList=new ArrayList<OptionChain>();
        for (OptionChainInputDto optionChainInputDto: optionChainInputDtos) {
            OptionChain optionChain=new OptionChain();
            optionChain.setIndex(optionChainInputDto.getIndex());
            optionChain.setDaysLeftToExpire(optionChainInputDto.getDaysLeftToExpire());
            optionChain.setImpliedVolatility(optionChainInputDto.getImpliedVolatility());
            optionChain.setOpenInterest(optionChainInputDto.getOpenInterest());
            optionChain.setTime(optionChainInputDto.getTime());
            optionChain.setStrikePrice(optionChainInputDto.getStrikePrice());
            optionChain.setPrice(optionChainInputDto.getPrice());
            optionChain.setTotalTradedVolume(optionChainInputDto.getTotalTradedVolume());
            Float d1=null,d2=null,delta=null,theta=null,gamma=null,vega=null;
            if(optionChainInputDto.getImpliedVolatility()!=0) {
                d1 = Float.valueOf(formatToThreeDecimal.format(getD1(optionChainInputDto.getUnderlyingValue(), Integer.valueOf(optionChainInputDto.getStrikePrice().substring(0, optionChainInputDto.getStrikePrice().length() - 2)), Constanst.RATE_OF_INTEREST, Constanst.DIVIDEND, optionChainInputDto.getImpliedVolatility(), optionChainInputDto.getDaysLeftToExpire())));
                d2 = Float.valueOf(formatToThreeDecimal.format(getD2(optionChainInputDto.getUnderlyingValue(), Integer.valueOf(optionChainInputDto.getStrikePrice().substring(0, optionChainInputDto.getStrikePrice().length() - 2)), Constanst.RATE_OF_INTEREST, Constanst.DIVIDEND, optionChainInputDto.getImpliedVolatility(), optionChainInputDto.getDaysLeftToExpire())));
                delta = Float.valueOf(formatToTwoDecimal.format(calculateDelta(Constanst.DIVIDEND, optionChainInputDto.getDaysLeftToExpire(), optionChainInputDto.getStrikePrice().substring(optionChainInputDto.getStrikePrice().length() - 2), d1)));
                theta = Float.valueOf(formatToTwoDecimal.format(calculateTheta(optionChainInputDto.getUnderlyingValue(), d1, optionChainInputDto.getImpliedVolatility(), optionChainInputDto.getDaysLeftToExpire(), Constanst.DIVIDEND, delta, Constanst.RATE_OF_INTEREST, Integer.valueOf(optionChainInputDto.getStrikePrice().substring(0, optionChainInputDto.getStrikePrice().length() - 2)), d2, optionChainInputDto.getStrikePrice().substring(optionChainInputDto.getStrikePrice().length() - 2))));
                gamma = Float.valueOf(formatToTwoDecimal.format(calculateGamma(d1, optionChainInputDto.getDaysLeftToExpire(), Constanst.DIVIDEND, optionChainInputDto.getUnderlyingValue(), optionChainInputDto.getImpliedVolatility())));
                vega = Float.valueOf(formatToTwoDecimal.format(calculateVega(d1, optionChainInputDto.getDaysLeftToExpire(), Constanst.DIVIDEND, optionChainInputDto.getUnderlyingValue())));
            }
            optionChain.setGamma(gamma);
            optionChain.setDelta(delta);
            optionChain.setTheta(theta);
            optionChain.setVega(vega);
            optionChainList.add(optionChain);
        }
        optionChainDao.saveAll(optionChainList);
        return true;
    }

    @Override
    public OptionChainForStrikePriceDto getOptionChainDataForStrikePrice(Integer strikePrice)
    {
        List<OptionChainDto> optionChainCallDtoList=optionChainDao.getOptionChainDataForStrikePrice(strikePrice+"CE");
        List<OptionChainDto> optionChainPutDtoList=optionChainDao.getOptionChainDataForStrikePrice(strikePrice+"PE");
        List<Float> callPriceList=new ArrayList<>();
        List<Integer> callOpenInterestList=new ArrayList<>();
        List<String> callTimeList=new ArrayList<>();
        List<Float> putPriceList=new ArrayList<>();
        List<Integer> putOpenInterestList=new ArrayList<>();
        List<String> putTimeList=new ArrayList<>();
        List<Integer> overallChangeInOI=new ArrayList<>();
        OptionChainForStrikePriceDto optionChainForStrikePriceDto=new OptionChainForStrikePriceDto();
        for (OptionChainDto optionChainDto:optionChainCallDtoList) {
            callPriceList.add(optionChainDto.getPrice());
            callOpenInterestList.add(optionChainDto.getOpenInterest());
            callTimeList.add(optionChainDto.getTime());
        }
        for (OptionChainDto optionChainDto:optionChainPutDtoList) {
            putPriceList.add(optionChainDto.getPrice());
            putOpenInterestList.add(optionChainDto.getOpenInterest());
            putTimeList.add(optionChainDto.getTime());
        }
        for(int i=0;i<callOpenInterestList.size();i++) {
            overallChangeInOI.add(putOpenInterestList.get(i) - callOpenInterestList.get(i));
        }
        optionChainForStrikePriceDto.setCallPriceList(callPriceList);
        optionChainForStrikePriceDto.setCallTimeList(callTimeList);
        optionChainForStrikePriceDto.setCallOpenInterestList(callOpenInterestList);
        optionChainForStrikePriceDto.setPutPriceList(putPriceList);
        optionChainForStrikePriceDto.setPutTimeList(putTimeList);
        optionChainForStrikePriceDto.setPutOpenInterestList(putOpenInterestList);
        optionChainForStrikePriceDto.setOverallChangeInOIList(overallChangeInOI);
        return optionChainForStrikePriceDto;
    }

    @Override
    public OIChangeDto getChangeInOIForIndex(String index){
        List<OIChangeResponseDto> callOIChange=optionChainDao.getTotalCallOI(index);
        List<OIChangeResponseDto> putOIChange=optionChainDao.getTotalPutOI(index);
        OIChangeDto oiChangeDto=new OIChangeDto();
        List<Integer> changeInOI=new ArrayList<>();
        List<String> timeList=new ArrayList<>();
        for(int i=0;i<callOIChange.size();i++)
        {
            changeInOI.add(putOIChange.get(i).getTotalOpenInterest() -callOIChange.get(i).getTotalOpenInterest());
            timeList.add(callOIChange.get(i).getTime());

        }
        oiChangeDto.setOiChangeList(changeInOI);
        oiChangeDto.setTimeList(timeList);
        return oiChangeDto;
    }
    public Double getRiskFreeRateOfInterest(Integer rateOfInterest)
    {
        return (double)rateOfInterest/100;
    }

    public Double getVolatility(Float impliedVolatility)
    {
        return (double)impliedVolatility/100;
    }

    public Double getTimeToExpire(Integer daysLeftToExpire)
    {
        return (double)daysLeftToExpire/365;
    }

    public Double getDividend(Integer dividend)
    {
        return (double)dividend/100;
    }

    public Double getD1(Integer underlyingValue,Integer strikePrice,Integer rateOfInterest,Integer dividend,Float impliedVolatility,Integer daysLeftToExpire)
    {
        return (Math.log(((double)underlyingValue/strikePrice))+(((getRiskFreeRateOfInterest(rateOfInterest)-getDividend(dividend))+(Math.pow(getVolatility(impliedVolatility),2)/2))*getTimeToExpire(daysLeftToExpire)))/(getVolatility(impliedVolatility)*Math.sqrt(getTimeToExpire(daysLeftToExpire)));
    }

    public Double getD2(Integer underlyingValue,Integer strikePrice,Integer rateOfInterest,Integer dividend,Float impliedVolatility,Integer daysLeftToExpire)
    {
        return (Math.log(((double) underlyingValue/strikePrice))+(((getRiskFreeRateOfInterest(rateOfInterest)-getDividend(dividend))-(Math.pow(getVolatility(impliedVolatility),2)/2))*getTimeToExpire(daysLeftToExpire)))/(getVolatility(impliedVolatility)*Math
                .sqrt(getTimeToExpire(daysLeftToExpire)));
    }

    public Double calculateDelta(Integer dividend,Integer timeToExpire,String optionType,Float d1)
    {
        Double delta=null;
        if(optionType.equalsIgnoreCase("pe"))
        {
            delta=Math.exp(((-1*getDividend(dividend))*getTimeToExpire(timeToExpire)))*(CNDF(d1)-1);
        }
        else {
            delta=Math.exp(((-1*getDividend(dividend))*getTimeToExpire(timeToExpire)))*CNDF(d1);
        }
        return delta;
    }
    public Double calculateTheta(Integer underlyingPrice,Float d1,Float impliedVolatility,Integer daysLeftToExpire,Integer dividend,Float delta,Integer rateOfInterest,Integer strikePrice,Float d2,String optionType)
    {
        Double theta=null;
        if(optionType.equalsIgnoreCase("ce"))
        {
            theta=((((-1*((((underlyingPrice*((1/Math.sqrt((2*Math.PI)))*Math.exp(((-1*Math.pow(d1,2))/2))))*getVolatility(impliedVolatility))*Math.exp(((-1*getTimeToExpire(daysLeftToExpire))*getDividend(dividend))))/(2*Math.sqrt(getTimeToExpire(daysLeftToExpire)))))+((getDividend(dividend)*underlyingPrice)*delta))-(((getRiskFreeRateOfInterest(rateOfInterest)*strikePrice)*Math.exp(((-1*getRiskFreeRateOfInterest(rateOfInterest))*getTimeToExpire(daysLeftToExpire))))*CNDF(d2))))/365;
        }
        else{
            theta=(((((-1*((((underlyingPrice*((1/Math.sqrt((2*Math.PI)))*Math.exp(((-1*Math.pow(d1,2))/2))))*getVolatility(impliedVolatility))*Math.exp(((-1*getTimeToExpire(daysLeftToExpire))*getDividend(dividend))))))/(2*Math.sqrt(getTimeToExpire(daysLeftToExpire))))-(((getDividend(dividend)*underlyingPrice)*CNDF((-1*d1)))*Math.exp(((-1*getTimeToExpire(daysLeftToExpire))*getDividend(dividend)))))+(((getRiskFreeRateOfInterest(rateOfInterest)*strikePrice)*Math.exp(((-1*getRiskFreeRateOfInterest(rateOfInterest))*getTimeToExpire(daysLeftToExpire))))*CNDF((-1*d2)))))/365;
        }
        return theta;
    }

    public Double calculateGamma(Float d1,Integer daysLeftToExpire,Integer dividend,Integer underlyingPrice,Float impliedVolatility)
    {
        return ((((1/Math.sqrt((2*Math.PI)))*Math.exp(((-1*Math.pow(d1,2))/2)))*Math.exp(((-1*daysLeftToExpire)*getDividend(dividend))))/((underlyingPrice*getVolatility(impliedVolatility))*Math.sqrt(getTimeToExpire(daysLeftToExpire))));
    }

    public Double calculateVega(Float d1,Integer daysLeftToExpire,Integer dividend,Integer underlyingPrice)
    {
        return (((((1/Math.sqrt((2*Math.PI)))*Math.exp(((-1*Math.pow(d1,2))/2)))*Math.exp(((-1*getTimeToExpire(daysLeftToExpire))*getDividend(dividend))))*underlyingPrice)*Math.sqrt(getTimeToExpire(daysLeftToExpire)))/100;
    }
    public double CNDF(double x)
        {
            int neg = (x < 0d) ? 1 : 0;
            if ( neg == 1)
                x *= -1d;
            double k = (1d / ( 1d + 0.2316419 * x));
            double y = (((( 1.330274429 * k - 1.821255978) * k + 1.781477937) *
                    k - 0.356563782) * k + 0.319381530) * k;
            y = 1.0 - 0.398942280401 * Math.exp(-0.5 * x * x) * y;

            return (1d - neg) * y + neg * (1d - y);
        }
    @Override
    public List<StrikePriceOutputDto> getDataForStrikePriceSelection(String indexType,String actionType,String optionType)
    {
        List<StrikePriceDto> strikePriceDtoList=optionChainDao.getDataForStrikePriceSelection(indexType,optionType);
        List<StrikePriceOutputDto> strikePriceOutputDtoList=new ArrayList<>();
        Set<String> set=new HashSet<>();
        for (StrikePriceDto strikePriceDto: strikePriceDtoList) {
            if(!set.contains(strikePriceDto.getStrikePrice())) {
                set.add(strikePriceDto.getStrikePrice());
                StrikePriceOutputDto strikePriceOutputDto = new StrikePriceOutputDto();
                strikePriceOutputDto.setDelta(strikePriceDto.getDelta());
                strikePriceOutputDto.setVega(strikePriceDto.getVega());
                strikePriceOutputDto.setTheta(strikePriceDto.getTheta());
                strikePriceOutputDto.setGamma(strikePriceDto.getGamma());
                strikePriceOutputDto.setStrikePrice(strikePriceDto.getStrikePrice());
                strikePriceOutputDto.setVolume(strikePriceDto.getVolume());
                strikePriceOutputDtoList.add(strikePriceOutputDto);
                }
            }
        return strikePriceOutputDtoList;
    }
}
