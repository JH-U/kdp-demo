# 权限框架要求
1. 所有API响应数据格式需要统一
```json
{
  "code": "success",
  "message": "description",
  "data": null
}
```
2. 响应数据提示新增支持国际化
    
    根据Header： Accept-Language的不同，返回的message提示也不一样


3. 支持多种方式登录
   
    比如：用户名+密码登录，手机号+短信登录


4. 支持多种资源API鉴权
   
    比如，内部系统API使用Jwt鉴权。对外提供开发API使用的是md5签名验签鉴权，或其他自定义鉴权


# 外部资源

SpringSecurity中文文档：


https://springdoc.cn/spring-security/servlet/architecture.html