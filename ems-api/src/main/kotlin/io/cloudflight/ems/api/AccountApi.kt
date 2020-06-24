package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.InputAccount
import io.cloudflight.ems.api.dto.OutputAccount
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Account")
@RequestMapping("/api/account")
interface AccountApi {

    @ApiOperation("Returns the users")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping
    fun list(pageable: Pageable): Page<OutputAccount>

    @ApiOperation("Creates new User")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(@Valid @RequestBody account: InputAccount): OutputAccount

}
