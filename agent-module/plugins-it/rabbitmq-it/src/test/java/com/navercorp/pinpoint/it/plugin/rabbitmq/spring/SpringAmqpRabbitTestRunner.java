/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.it.plugin.rabbitmq.spring;

import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.it.plugin.rabbitmq.util.RabbitMQTestConstants;
import org.junit.jupiter.api.Assertions;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import test.pinpoint.plugin.rabbitmq.MessageConverter;
import test.pinpoint.plugin.rabbitmq.spring.TestMessageHolder;
import test.pinpoint.plugin.rabbitmq.spring.service.TestReceiverService;
import test.pinpoint.plugin.rabbitmq.spring.service.TestSenderService;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author HyunGil Jeong
 */
public class SpringAmqpRabbitTestRunner {

    private static final long TRACE_WAIT_TIME_MS = 3000L;

    private final TestSenderService testSenderService;
    private final TestReceiverService testReceiverService;
    private final TestMessageHolder testMessageHolder;
    private final ConnectionFactory connectionFactory;
    private final String remoteAddress;

    public SpringAmqpRabbitTestRunner(TestApplicationContext context) {
        Objects.requireNonNull(context, "context");

        this.testSenderService = context.getTestSenderService();
        this.testMessageHolder = context.getTestMessageHolder();
        this.testReceiverService = context.getTestReceiverService();
        this.connectionFactory = context.getConnectionFactory();
        this.remoteAddress = connectionFactory.getHost() + ":" + connectionFactory.getPort();
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public PluginTestVerifier runPush(int expectedTraceCount) {
        final String message = "hello rabbit mq";

        testSenderService.sendMessage(RabbitMQTestConstants.EXCHANGE, RabbitMQTestConstants.ROUTING_KEY_PUSH, message);
        Assertions.assertEquals(message, testMessageHolder.getMessage(10, TimeUnit.SECONDS));

        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        awaitAndVerifyTraceCount(verifier, expectedTraceCount, TRACE_WAIT_TIME_MS);
        return verifier;
    }

    public PluginTestVerifier runPull(int expectedTraceCount) throws InterruptedException {
        final String message = "hello rabbit mq!";

        testSenderService.sendMessage(RabbitMQTestConstants.EXCHANGE, RabbitMQTestConstants.ROUTING_KEY_PULL, message);
        Assertions.assertEquals(message, testReceiverService.receiveMessage(MessageConverter.FOR_TEST));

        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        awaitAndVerifyTraceCount(verifier, expectedTraceCount, TRACE_WAIT_TIME_MS);
        return verifier;
    }

    public PluginTestVerifier runPull(int expectedTraceCount, long timeoutMs) throws InterruptedException {
        final String message = "hello rabbit mq!";

        testSenderService.sendMessage(RabbitMQTestConstants.EXCHANGE, RabbitMQTestConstants.ROUTING_KEY_PULL, message);
        Assertions.assertEquals(message, testReceiverService.receiveMessage(MessageConverter.FOR_TEST, timeoutMs));

        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        awaitAndVerifyTraceCount(verifier, expectedTraceCount, TRACE_WAIT_TIME_MS);
        return verifier;
    }

    private static void awaitAndVerifyTraceCount(PluginTestVerifier verifier, int expectedTraceCount, long maxWaitMs) {
        final long waitIntervalMs = 100L;
        long maxWaitTime = Math.max(maxWaitMs, waitIntervalMs);
        long startTime = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() - startTime < maxWaitTime) {
                try {
                    verifier.verifyTraceCount(expectedTraceCount);
                    return;
                } catch (AssertionError e) {
                    // ignore and retry
                    await(waitIntervalMs);
                }
            }
            verifier.verifyTraceCount(expectedTraceCount);
        } finally {
            verifier.printCache();
        }
    }

    private static void await(long waitIntervalMs) {
        try {
            Thread.sleep(waitIntervalMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
