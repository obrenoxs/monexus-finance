package com.monexus.finance.transaction.mapper;

import com.monexus.finance.transaction.dto.request.TransactionRequest;
import com.monexus.finance.transaction.dto.response.TransactionResponse;
import com.monexus.finance.transaction.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(TransactionRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    TransactionResponse toResponse(Transaction transaction);
}
