# vlmap-spring-loadbalancer

 
 
1.支持的SpringBoot 版本

>  spring-boot 2

2.支持的注册中心类型
  
 >  Eureka , Nacos ,Consul
 
3.支持的网关类型

>  Zuul, Gateway

4.MVN坐标


4.使用实例
  >@EnableGrayLoadBalancer  开启灰度路由
  
 ```java

package com.github.vlmap.gateway;


import com.github.vlmap.spring.loadbalancer.annotation.EnableGrayLoadBalancer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableGrayLoadBalancer
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);

    }

}


```


4.标签负载均衡配置

 
   
 >  以下配置动态修改即时生效,建议使用配置中心对 （服务灰度值）进行配置
   
 
```yaml
vlmap:
  spring:
    loadbalancer:
      enabled: true   #当前服务是否启用灰度路由，默认值： true
      header-name: Loadbalancer-Tag #取header-name的值作为灰度路由的值来匹配，支持动态配置，默认值：Loadbalancer-Tag
      feign:
        enabled: true #feign客户端是否启用灰度路由，默认值： true
      rest-template:
        enabled: true #RestTemplate客户端是否启用灰度路由，默认值： true
      web-client:
        enabled: true #WebClient客户端是否启用灰度路由，默认值： true
      strict:
        enabled: true #是否启用严格模式(如果启用，Loadbalancer-Tag的值必匹配当前服务说配置的灰度值，不匹配返回 HTTP code)，默认值： true
        code: 403     #严格模式验证不通过返回的状态码
        message: Fibbon   #严格模式验证不通过返回的状态描述
        ignore:
          default:
            enabled: true  #启用默认忽略列表  默认值： true
          path:            #忽略列表，匹配列表的请求将不启用严格模式
            - /antpath/**   # ANT-PATH
            - /antpath2/**


#（服务灰度值） 配置
MICRO-CLOUD-SERVICE: # 大写  , 这里是 ribbon 要请求的服务的 service-id 值
  ribbon:
    tagOfServers:
      - id:  172.18.70.27:7004   #  远程服务具体节点的ID(注册中心中注册的 IP:PORT ，)，支持动态配置
        tags: debug1,qqq,debug3  # 指定节点灰度路由匹配的值，多个用“,”分割，支持动态配置
      - id:    172.18.70.27:7005
        tags: ddd,qqq,debug

```
