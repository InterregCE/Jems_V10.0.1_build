package io.cloudflight.ems.client

import io.cloudflight.ems.api.AccountRoleApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author Ondrej Oravcok (ondrej.oravcok@cloudflight.io)
 * @version 1.0
 */
@FeignClient(name = "accountrole", url = "\${ems.url}")
interface AccountRoleClient : AccountRoleApi
