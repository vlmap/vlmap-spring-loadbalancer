# vlmap-cloud-tools

1. 标签负载均衡配置
 
```yaml
spring:
  tools:
    tag-loadbalancer:
      enabled: true   #当前服务是否启用灰度路由，默认值： true
      header: debug   #当前服务灰度标签，从该服务发起的HTTP请求都会包含此Header信息,默认值为空
      header-name: Loadbalancer-Tag #取header-name的值作为灰度路由的值来匹配，支持动态配置，默认值：Loadbalancer-Tag
      feign:
        enabled: true #feign客户端是否启用灰度路由，默认值： true
      rest-template:
        enabled: true #RestTemplate客户端是否启用灰度路由，默认值： true
      web-client:
        enabled: true #WebClient客户端是否启用灰度路由，默认值： true

MICRO-CLOUD-GAME2: # 这里是 ribbon 要请求的微服务的 service-id 值
  ribbon:
    listOfServers: http://localhost:7900,http://localhost:7899,http://localhost:7898
    tagOfServers:
      - id: localhost:7900   #  远程服务具体节点的ID，支持动态配置
        tags: debug1,qqq,debug3  # 指定节点灰度路由匹配的值，多个用“,”分割，支持动态配置
      - id:    localhost:7899
        tags: ddd,qqq,debug

```
