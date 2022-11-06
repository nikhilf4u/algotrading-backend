package com.trading.algotrading.serviceimpl;


import com.trading.algotrading.dao.OptionChainDao;
import com.trading.algotrading.dto.OptionChainDto;
import com.trading.algotrading.dto.OptionChainForStrikePriceDto;
import com.trading.algotrading.dto.OptionChainInputDto;
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
}
