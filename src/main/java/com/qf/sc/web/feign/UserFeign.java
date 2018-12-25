package com.qf.sc.web.feign;

import com.qf.sc.service.UserService;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient("service-user")
public interface UserFeign extends UserService{

}
