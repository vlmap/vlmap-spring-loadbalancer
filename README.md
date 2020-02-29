# vlmap-spring-loadbalancer

 #### spring cloud 灰度路由
 
 ###更新说明
 > 增加Hystrix 支持
 > 请求入口增加根据HTTP参数动态添加添加灰度值
 > 增加条件匹配功能
 > 增加应答器功能
 > 修复一些bug

 ###路由规则说明
 
 >1.被调用的所有节点都没配灰度标签（没有灰度服务），使用所有服务节点进行负载，
 
 >2.当前请求为无标签请求时，排除所有包含灰度环境的服务节点，然后进行负载
 
 >3.当前请求为有标签请求时,仅使用灰度环境标签一致的服务节点进行负载
 
 >4.匹配不到灰度环境标签,则使用无标签服务节点（排除是灰度的服务）进行负载
 
  注意：
  >  对于reactive(WebFlux) 环境，因为传值是依靠ThredLocal和HystrixRequestVariable实现，reactive 里的业务方法不能确定在哪个线程里运行，所以再reactive环境中对 resttemplate、feign、 weblcient 客户端调用时负载均衡时需要手动传递请求的灰度值，网关服务 (Zuul,Gateway) 和Servlet环境服务不用考虑该问题
  
1.支持的SpringBoot 版本

>  spring-boot 2

2.支持的注册中心类型
  
 >  Eureka , Nacos ,Consul
 
3.支持的网关类型

>  Zuul, Gateway

4.注册的客户端类型
> Feign,RestTemplate,WebClient.  响应式环境需要手动实现灰度值传递

5.灰度调用示例
```text
 curl -H "LoadBalancer-Tag:debug" http://localhost:8080/demo/test
```
6.MVN坐标
>Step 1. Add the JitPack repository to your build file
 ```xml
 
    <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
>  Step 2. Add the dependency
```xml
    <dependency>
	    <groupId>com.github.vlmap</groupId>
	    <artifactId>vlmap-spring-loadbalancer</artifactId>
	    <version>1.0.1.RELEASE</version>
    </dependency>
```
7.条件匹配(新增)

 根据HTTP请求参数匹配条件，如果匹配则添加value的值到HTTP头信息(Loadbalancer-Tag:${value})。
 配置为JSON格式，会映射到RequestMatchParamater类，

8.应答器(新增)
 
  如果Loadbalancer-Tag的值与value值匹配则根据配置生成响应内容并直接返回。
  配置为JSON格式，会映射到RequestMatchParamater类，

```yaml
vlmap:
 spring: 
   loadbalancer: 
      attacher:   #条件匹配,匹配则添加value的值到HTTP头信息(Loadbalancer-Tag:${value})
        commands: #映射到RequestMatchParamater
          - "{\"value\":\"responder\",\"params\":{\"a\":[\"1\"]},\"path\":\"/**\"}" 
      responder:  #应答器
        commands: #映射到ResponderParamater 
          - "{\"value\":\"responder\",\"body\":\"success\"}"    
```

 9. Actuator (新增) 
    访问路径 /actuator/gray
    显示灰度相关的信息。
10.使用实例
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


11.标签负载均衡配置

 
   
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
      controller:  
        enabled: true #reactive(WebFlux) 环境 controller 否启用灰度路由,保证标签能传到Contoller层，默认值： true
      strict:
        enabled: true #是否启用严格模式(如果启用，Loadbalancer-Tag的值必匹配当前服务说配置的灰度值，不匹配返回 HTTP code)，默认值： true
        code: 403     #严格模式验证不通过返回的状态码
        message: Fibbon   #严格模式验证不通过返回的状态描述
        ignore:       #忽略列表，匹配列表的请求将不启用严格模式
          default:
            enabled: true  #启用默认忽略列表  默认值： true
          path:           
            - /antpath/**   # ANT-PATH
            - /antpath2/**
      attacher:
          commands: #映射到RequestMatchParamater
            - "{\"value\":\"responder\",\"params\":{\"a\":[\"1\"]},\"path\":\"/**\"}" 
      responder:
          commands: #映射到ResponderParamater 
            - "{\"value\":\"responder\",\"body\":\"success\"}"    
```     


#（服务灰度值） 配置
```
MICRO-CLOUD-SERVICE: # 大写  , 这里是 ribbon 要请求的服务的 service-id 值
  ribbon:
    gray:
      - id:  172.18.70.27:7004   #  远程服务具体节点的ID(注册中心中注册的 IP:PORT ，静态服务使用listOfServers值)，支持动态配置
        tags: debug,debug1,debug2  # 指定节点灰度路由匹配的值，多个用“,”分割，支持动态配置
      - id:    172.18.70.27:7005
        tags: debug,debug1,debug2

```
