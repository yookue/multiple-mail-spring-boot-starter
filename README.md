# Multiple Mail Spring Boot Starter

Spring Boot application integrates multiple `mail` quickly.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.yookue.springstarter</groupId>
        <artifactId>multiple-mail-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `spring.multiple-mail.enabled = false`

- Configure Spring Boot `application.yml` with prefix `spring.multiple-mail`

```yml
spring:
    multiple-mail:
        primary:
            host: '192.168.0.1'
            port: 25
            username: 'foo1'
            password: 'bar1'
            properties:
        secondary:
            host: '192.168.0.2'
            port: 25
            username: 'foo2'
            password: 'bar2'
            properties:
        tertiary:
        quaternary:
        quinary:
        senary:
```

> This starter supports 6 `JavaMailSenderImpl` at most.

- Configure your beans with the following bean by `@Autowired`/`@Resource` annotation, combined with `@Qualifier` annotation (take `primary` as an example)

| Bean Type          | Qualifier                                  |
|--------------------|--------------------------------------------|
| JavaMailSenderImpl | PrimaryMailSenderConfiguration.MAIL_SENDER |

## Document

- Github: https://github.com/yookue/multiple-mail-spring-boot-starter

## Requirement

- jdk 17+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to Yookue](https://yookue.com/public/donate) to support the development.

## Website

- Yookue: https://yookue.com
