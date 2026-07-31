package com.monexus.finance.wallet.mapper;

import com.monexus.finance.wallet.dto.response.WalletResponse;
import com.monexus.finance.wallet.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletResponse toResponse(Wallet wallet);
}
