# Spring Security 6 教程
## 通用的权限框架需求
### 1. 所有API响应数据格式需要统一。示例：
```json
{
  "code": "success",
  "message": "description",
  "data": null
}
```
      ps:
      code: 状态码，后台定义给前端用，比如“token.expired”，前端收到这个状态码后，会立刻去调用刷新token接口
      message: 状态码含义描述，可以做国际化翻译。不同语言返回不同的翻译版本
      重点：正常情况 和 异常情况 返回的数据格式都必须一样

### 2. 支持多种方式登录

   比如：用户名+密码登录，手机号+短信登录


### 3. 支持多种资源API鉴权

   比如，自家用户请求API时鉴权。对外提供开放API使用的是md5签名验签鉴权，或其他自定义鉴权

## 附录
### SpringSecurity框架默认配置存在的问题
1. 拦截了所有请求，如何放行？
2. 错误拦截返回的数据结构不统一

## 外部文档

SpringSecurity中文文档：


https://springdoc.cn/spring-security/servlet/architecture.html