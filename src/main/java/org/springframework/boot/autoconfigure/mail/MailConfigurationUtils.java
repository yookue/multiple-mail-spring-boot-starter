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

package org.springframework.boot.autoconfigure.mail;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.mail.Session;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.yookue.commonplexus.springutil.support.SingletonObjectProvider;


/**
 * Utilities for configuring mail sender
 *
 * @author David Hsing
 * @see org.springframework.mail.javamail.JavaMailSenderImpl
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MailConfigurationUtils {
    public static Session jndiMailSession(@Nonnull MailProperties properties) throws IllegalStateException {
        MailSenderJndiConfiguration configuration = new MailSenderJndiConfiguration(properties);
        return configuration.session();
    }

    public static JavaMailSenderImpl jndiMailSender(@Nonnull MailProperties properties, @Nonnull Session session) {
        MailSenderJndiConfiguration configuration = new MailSenderJndiConfiguration(properties);
        return configuration.mailSender(session);
    }

    public static JavaMailSenderImpl classicMailSender(@Nonnull MailProperties properties, @Nullable SslBundles bundles) {
        MailSenderPropertiesConfiguration configuration = new MailSenderPropertiesConfiguration();
        return configuration.mailSender(properties, SingletonObjectProvider.ofNullable(bundles));
    }
}
