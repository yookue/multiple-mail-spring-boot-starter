/*
 * Copyright (c) 2020 Yookue Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yookue.springstarter.multiplemail.config;


import jakarta.activation.MimeType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailConfigurationUtils;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.yookue.commonplexus.springcondition.annotation.ConditionalOnAnyProperties;
import com.yookue.commonplexus.springcondition.annotation.ConditionalOnMissingProperty;


/**
 * Senary configuration for mail sender
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.multiple-mail", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnAnyProperties(value = {
    @ConditionalOnProperty(prefix = SenaryMailSenderConfiguration.PROPERTIES_PREFIX, name = "host"),
    @ConditionalOnProperty(prefix = SenaryMailSenderConfiguration.PROPERTIES_PREFIX, name = "jndi-name")
})
@ConditionalOnClass(value = {MimeMessage.class, MimeType.class, MailSender.class})
@AutoConfigureAfter(value = QuinaryMailSenderConfiguration.class)
@AutoConfigureBefore(value = MailSenderAutoConfiguration.class)
@Import(value = {SenaryMailSenderConfiguration.Entry.class, SenaryMailSenderConfiguration.Jndi.class, SenaryMailSenderConfiguration.Classic.class, SenaryMailSenderConfiguration.Validator.class})
public class SenaryMailSenderConfiguration {
    public static final String PROPERTIES_PREFIX = "spring.multiple-mail.senary";    // $NON-NLS-1$
    public static final String MAIL_PROPERTIES = "senaryMailProperties";    // $NON-NLS-1$
    public static final String MAIL_SESSION = "senaryMailSession";    // $NON-NLS-1$
    public static final String MAIL_SENDER = "senaryMailSender";    // $NON-NLS-1$
    public static final String SSL_BUNDLES = "senaryMailSslBundles";    // $NON-NLS-1$


    /**
     * Mail sender entry
     *
     * @author David Hsing
     */
    @Order(value = 0)
    static class Entry {
        @Bean(name = MAIL_PROPERTIES)
        @ConditionalOnMissingBean(name = MAIL_PROPERTIES)
        @ConfigurationProperties(prefix = PROPERTIES_PREFIX)
        public MailProperties mailProperties() {
            return new MailProperties();
        }
    }


    /**
     * Mail sender of JNDI
     *
     * @author David Hsing
     */
    @ConditionalOnProperty(prefix = PROPERTIES_PREFIX, name = "jndi-name")
    @ConditionalOnClass(value = Session.class)
    @ConditionalOnBean(name = MAIL_PROPERTIES, value = MailProperties.class)
    @Order(value = 1)
    static class Jndi {
        @Bean(name = MAIL_SESSION)
        @ConditionalOnMissingBean(name = MAIL_SESSION)
        public Session mailSession(@Qualifier(value = MAIL_PROPERTIES) @Nonnull MailProperties properties) throws IllegalStateException {
            return MailConfigurationUtils.jndiMailSession(properties);
        }

        @Bean(name = MAIL_SENDER)
        @ConditionalOnBean(name = MAIL_SESSION)
        @ConditionalOnMissingBean(name = MAIL_SENDER)
        public JavaMailSenderImpl mailSender(@Qualifier(value = MAIL_PROPERTIES) @Nonnull MailProperties properties, @Qualifier(value = MAIL_SESSION) @Nonnull Session session) {
            return MailConfigurationUtils.jndiMailSender(properties, session);
        }
    }


    /**
     * Mail sender of classic
     *
     * @author David Hsing
     */
    @ConditionalOnMissingProperty(prefix = PROPERTIES_PREFIX, name = "jndi-name")
    @ConditionalOnBean(name = MAIL_PROPERTIES, value = MailProperties.class)
    @Order(value = 2)
    static class Classic {
        @Bean(name = MAIL_SENDER)
        @ConditionalOnMissingBean(name = MAIL_SENDER)
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        public JavaMailSenderImpl mailSender(@Qualifier(value = MAIL_PROPERTIES) @Nonnull MailProperties properties, @Qualifier(value = SSL_BUNDLES) @Nullable SslBundles bundles) {
            return MailConfigurationUtils.classicMailSender(properties, bundles);
        }
    }


    /**
     * Mail sender validator
     *
     * @author David Hsing
     */
    @ConditionalOnProperty(prefix = PROPERTIES_PREFIX, value = "test-connection")
    @ConditionalOnBean(name = MAIL_SENDER)
    @Order(value = 3)
    static class Validator implements InitializingBean {
        @Autowired
        @Qualifier(value = MAIL_SENDER)
        private JavaMailSenderImpl mailSender;

        @Override
        public void afterPropertiesSet() throws IllegalStateException {
            new MailSenderValidatorAutoConfiguration(mailSender);
        }
    }
}
