package com.trading.algotrading.serviceimpl;


import com.trading.algotrading.dao.OptionChainDao;
import com.trading.algotrading.dto.*;
import com.trading.algotrading.model.OptionChain;
import com.trading.algotrading.service.OptionChainService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
        List<OptionChain> optionChainList=new ArrayList<OptionChain>();
        for (OptionChainInputDto optionChainInputDto: optionChainInputDtos) {
            OptionChain optionChain=modelMapper.map(optionChainInputDto,OptionChain.class);
            optionChainList.add(optionChain);
        }
        optionChainDao.saveAll(optionChainList);
        return true;
    }

    @Override
    public OptionChainForStrikePriceDto getOptionChainDataForStrikePrice(String strikePrice)
    {
        List<OptionChainDto> optionChainDtoList=optionChainDao.getOptionChainDataForStrikePrice(strikePrice);
        List<Float> priceList=new ArrayList<>();
        List<Integer> openInterestList=new ArrayList<>();
        List<String> timeList=new ArrayList<>();
        OptionChainForStrikePriceDto optionChainForStrikePriceDto=new OptionChainForStrikePriceDto();
        for (OptionChainDto optionChainDto:optionChainDtoList) {
            priceList.add(optionChainDto.getPrice());
            openInterestList.add(optionChainDto.getOpenInterest());
            timeList.add(optionChainDto.getTime());
        }
        optionChainForStrikePriceDto.setPriceList(priceList);
        optionChainForStrikePriceDto.setTimeList(timeList);
        optionChainForStrikePriceDto.setOpenInterestList(openInterestList);
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

    @Override
    public  List<String> getStrikePriceForBuying(Integer underlyingValue,String indexType,Integer expiryDaysCount)
    {
        DecimalFormat formatToTwoDecimal = new DecimalFormat("0.00");
        DecimalFormat formatToThreeDecimal = new DecimalFormat("0.000");
        List<StrikePriceSelectionDto> strikePriceSelectionDtos=optionChainDao.getOptionChainDataForStrikePriceSelection(indexType);
        for (StrikePriceSelectionDto strikePriceSelectionDto:strikePriceSelectionDtos) {
            if(strikePriceSelectionDto.getImpliedVolatility()!=0) {
               Double d1= Double.valueOf(formatToThreeDecimal.format(getD1(underlyingValue,Integer.valueOf(strikePriceSelectionDto.getStrikePrice().substring(0,strikePriceSelectionDto.getStrikePrice().length()-2)),10,0,strikePriceSelectionDto.getImpliedVolatility(),5)));
               Double d2=Double.valueOf(formatToThreeDecimal.format(getD2(underlyingValue,Integer.valueOf(strikePriceSelectionDto.getStrikePrice().substring(0,strikePriceSelectionDto.getStrikePrice().length()-2)),10,0,strikePriceSelectionDto.getImpliedVolatility(),5)));
               Double delta= Double.valueOf(formatToTwoDecimal.format(calculateDelta(0,5,strikePriceSelectionDto.getStrikePrice().substring(strikePriceSelectionDto.getStrikePrice().length()-2),d1)));
               Double theta=Double.valueOf(formatToTwoDecimal.format(calculateTheta(underlyingValue,d1,strikePriceSelectionDto.getImpliedVolatility(),5,0,delta,10,Integer.valueOf(strikePriceSelectionDto.getStrikePrice().substring(0,strikePriceSelectionDto.getStrikePrice().length()-2)),d2,strikePriceSelectionDto.getStrikePrice().substring(strikePriceSelectionDto.getStrikePrice().length()-2))));
               Double gamma=Double.valueOf(formatToTwoDecimal.format(calculateGamma(d1,5,0,underlyingValue,strikePriceSelectionDto.getImpliedVolatility())));
               Double vega=Double.valueOf(formatToTwoDecimal.format(calculateVega(d1,5,0,underlyingValue)));
               System.out.println(d1+" "+d2+" "+delta+" "+theta+" "+gamma+" "+vega+" "+strikePriceSelectionDto.getStrikePrice()+" "+strikePriceSelectionDto.getImpliedVolatility());
            }
        }
        return null;
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

    public Double calculateDelta(Integer dividend,Integer timeToExpire,String optionType,Double d1)
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
    public Double calculateTheta(Integer underlyingPrice,Double d1,Float impliedVolatility,Integer daysLeftToExpire,Integer dividend,Double delta,Integer rateOfInterest,Integer strikePrice,Double d2,String optionType)
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

    public Double calculateGamma(Double d1,Integer daysLeftToExpire,Integer dividend,Integer underlyingPrice,Float impliedVolatility)
    {
        return ((((1/Math.sqrt((2*Math.PI)))*Math.exp(((-1*Math.pow(d1,2))/2)))*Math.exp(((-1*daysLeftToExpire)*getDividend(dividend))))/((underlyingPrice*getVolatility(impliedVolatility))*Math.sqrt(getTimeToExpire(daysLeftToExpire))));
    }

    public Double calculateVega(Double d1,Integer daysLeftToExpire,Integer dividend,Integer underlyingPrice)
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
    }
