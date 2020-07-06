package io.cloudflight.ems.client

import io.cloudflight.ems.api.UserRoleApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author Ondrej Oravcok (ondrej.oravcok@cloudflight.io)
 * @version 1.0
 */
@FeignClient(name = "userrole", url = "\${ems.url}")
interface UserRoleClient : UserRoleApi
