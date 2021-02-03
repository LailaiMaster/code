# part03-static_resource

**静态资源包含**:

- html
- css
- js
- vue

**静态资源管理**：

1. 单独构建一个服务管理静态资源【大公司】。
    1. 使用 SpringBoot
    2. 使用 nginx
2. 第三方：OSS、七牛、马云【小公司推荐】。

**SpringBoot 静态资源管理**：使用 `thymeleaf`

```groovy
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```

所有的资源都放在 `resource/static` 资源目录下。比如将图片放在 `resource/static/imgs/a.png` 下，则访问路径为：`localhost:8081/imgs/test@2x.png`。

