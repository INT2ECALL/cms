package com.xzjie.cms.convert;

import com.xzjie.cms.system.account.dto.AccountDto;
import com.xzjie.cms.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author Vito
 * @since 2022/3/6 11:46 下午
 */
@Mapper(componentModel = Converter.componentModel, uses = {}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoConverter extends Converter<AccountDto, Account> {
    UserDtoConverter INSTANCE = Mappers.getMapper(UserDtoConverter.class);
}
