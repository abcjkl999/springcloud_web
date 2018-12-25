package com.qf.sc.web.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.qf.sc.entity.User;
import com.qf.sc.web.feign.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@DefaultProperties(defaultFallback = "serviceDegradation")
public class UserController {

        @Autowired
        private RedisTemplate redisTemplate;

        @Autowired
        private UserFeign userFeign;

        @RequestMapping(value= "/find/{id}",method= RequestMethod.GET)
        @ResponseBody
        @HystrixCommand(commandProperties = {
                @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "2500"),
                @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value = "10"),
                @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value = "10000"),
                @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value = "40")
        })
        public User findUsersByid(@PathVariable Long id){
                /* try {
                        Thread.sleep(2500);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }*/
                if(id % 2 == 0){
                        throw new RuntimeException("ruaruarua");
                }
                return userFeign.findUser(id);
        }

        @RequestMapping("/")
        public String toLogin(){
                return "login";
        }

        @RequestMapping("/login")
        public String login(String uname, HttpServletResponse response){
                //1 通过您输入的用户名和密码 去数据库中查询

                //2 如果没有直接返回错误信息
                //3  如果有...
                //3.1 把当前用户信息保存到 redis中  key:value
                String token = UUID.randomUUID().toString();
                System.out.println("token:" +token);
                redisTemplate.opsForValue().set(token,uname);
                //3.2 吧key 爆存在cookie中 xie 回到用户的浏览器上
                Cookie cookie = new Cookie("cs1801_token",token);
                response.addCookie(cookie);
                //4 跳转到下个页面
                return "index";
        }

        public User serviceDegradation(){
                return new User(1L,"服务降级了");
        }
}
