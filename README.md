# vlmap-spring-loadbalancer

 #### spring cloud 灰度路由
 
 ###更新说明
 > 支持大于1.3版本的 springboot环境  
 
 > 灰度值配置使用metadata 方式配置
 
  
 

 ###路由规则说明
 
 >1.所有节点都没配置标签，返回所有实例进行负载（没有灰度服务，不做修改），
 
 >2.正常请求:
   >> 2.1： 使用没配置标签的实例进行负载（正常请求只使用非灰度实例）
 
 >3.灰度请求：
   
   >>3.1:  优先返回包含标签的实例进行负载
   
   >>3.2: 匹配不到再返回无标签的实例进行负载
 
 
  注意：
  >  对于reactive(WebFlux) 环境，因为传值是依靠ThredLocal和HystrixRequestVariable实现，reactive 里的业务方法不能确定在哪个线程里运行，所以再reactive环境中对 resttemplate、feign、 weblcient 客户端调用时负载均衡时需要手动传递请求的灰度值，网关服务 (Zuul,Gateway) 和Servlet环境服务不用考虑该问题
  
  >  集成过度阶段，灰度服务建议启用严格模式来规避对正常请求做出的错误响应
  
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
	    <version>3.0.0.RELEASE</version>
    </dependency>
```
7.条件匹配

 根据HTTP请求参数匹配条件，如果匹配则添加value的值到HTTP头信息(Loadbalancer-Tag:${value})。
 配置为JSON格式，会映射到RequestMatchParamater类，

8.应答器
 
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
      strict:  #正常请求负载到灰度节点或灰度请求负载到非灰度节点验证    
        enabled: false  
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


# 灰度标签配置
通过实例的metadata下增加gray.tags进行配置
 
以Eureka为例
```properties
eureka.instance.metadata-map.gray.tags=a,b,c,d
```

以nacos为例
```properties
spring.cloud.nacos.discovery.metadata.gray.tags=a,b,c,d   
```

以consul为例
```properties
spring.cloud.consul.discovery.tags[0]=gray.tags=a,b,c,d
```
