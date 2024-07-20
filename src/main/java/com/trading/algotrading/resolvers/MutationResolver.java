package com.trading.algotrading.resolvers;

import com.trading.algotrading.dto.ContractInputDto;
import com.trading.algotrading.dto.OptionChainInputDto;
import com.trading.algotrading.service.ContractDataService;
import com.trading.algotrading.service.ContractService;
import com.trading.algotrading.service.OptionChainService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MutationResolver implements GraphQLMutationResolver {
    @Autowired
    OptionChainService optionChainService;

    @Autowired
    ContractService contractService;

    @Autowired
    ContractDataService contractDataService;
    public Boolean saveOptionChainData(List<OptionChainInputDto> optionChainInputDtos){
        return optionChainService.saveOptionChainData(optionChainInputDtos);
    }


}
