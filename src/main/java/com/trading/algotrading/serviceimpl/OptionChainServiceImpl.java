package com.trading.algotrading.serviceimpl;


import com.trading.algotrading.dao.OptionChainDao;
import com.trading.algotrading.dto.*;
import com.trading.algotrading.model.OptionChain;
import com.trading.algotrading.service.OptionChainService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
}
