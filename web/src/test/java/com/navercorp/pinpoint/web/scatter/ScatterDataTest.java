/*
 * Copyright 2019 NAVER Corp.
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

package com.navercorp.pinpoint.web.scatter;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.web.vo.scatter.Dot;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Taejin Koo
 */
public class ScatterDataTest {

    private static final String agentId = "agent";
    private static final String transactionAgentId = "transactionAgent";

    @Test
    public void addDotTest() {
        int count = 100;

        long from = 1000;
        long to = 10000;
        int xGroupUnit = 100;
        int yGroupUnit = 100;

        ScatterDataBuilder builder = new ScatterDataBuilder(from, to, xGroupUnit, yGroupUnit);
        List<Dot> dotList = createDotList(agentId, transactionAgentId, count, from);

        for (Dot dot : dotList) {
            builder.addDot(dot);
        }

        List<Dot> dots = extractDotList(builder.build());
        assertThat(dots).hasSize(count);
    }

    @Test
    public void addDotTest2() {
        long from = 1000;
        long to = 10000;
        int xGroupUnit = 100;
        int yGroupUnit = 100;

        ScatterDataBuilder builder = new ScatterDataBuilder(from, to, xGroupUnit, yGroupUnit);

        long currentTime = System.currentTimeMillis();

        TransactionId transactionId1 = TransactionId.of(transactionAgentId, currentTime, 1);
        TransactionId transactionId2 = TransactionId.of(transactionAgentId, currentTime, 2);

        long acceptedTime = Math.max(Math.abs(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)), from);
        int executionTime = (int) Math.abs(ThreadLocalRandom.current().nextLong(60 * 1000));

        long acceptedTime2 = Math.max(Math.abs(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)), from);

        Dot dot1 = new Dot(transactionId1, acceptedTime2, executionTime, 0, agentId);
        Dot dot2 = new Dot(transactionId2, acceptedTime2, executionTime, 1, agentId);

        builder.addDot(dot1);
        builder.addDot(dot2);
        ScatterData scatterData = builder.build();

        List<DotGroups> values = scatterData.getScatterData();
        assertThat(values).hasSize(1);

        for (DotGroups dotGroups : values) {
            Map<Dot, DotGroup> dotGroupLeaders = dotGroups.getDotGroupLeaders();
            assertThat(dotGroupLeaders.keySet()).hasSize(2);
        }
    }

    @Test
    public void addDotTest3() {
        long from = 1000;
        long to = 10000;
        int xGroupUnit = 100;
        int yGroupUnit = 100;

        ScatterDataBuilder builder = new ScatterDataBuilder(from, to, xGroupUnit, yGroupUnit);

        long currentTime = System.currentTimeMillis();

        TransactionId transactionId = TransactionId.of(transactionAgentId, currentTime, 1);

        long acceptedTime = Math.max(Math.abs(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)), from);
        int executionTime = (int) Math.abs(ThreadLocalRandom.current().nextLong(60 * 1000));

        long acceptedTime2 = Math.max(Math.abs(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)), from);

        Dot dot1 = new Dot(transactionId, acceptedTime2, executionTime, 0, agentId);
        Dot dot2 = new Dot(transactionId, acceptedTime2, executionTime, 0, agentId);

        builder.addDot(dot1);
        builder.addDot(dot2);

        List<Dot> dots = extractDotList(builder.build());
        assertThat(dots).hasSize(2);
    }

    private List<Dot> createDotList(String agentId, String transactionAgentId, int createSize, long from) {
        long currentTime = System.currentTimeMillis();

        List<TransactionId> transactionIdList = new ArrayList<>(createSize);
        for (int i = 0; i < createSize; i++) {
            transactionIdList.add(TransactionId.of(transactionAgentId, currentTime, i));
        }

        long acceptedTime = Math.max(Math.abs(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)), from);
        int executionTime = (int) Math.abs(ThreadLocalRandom.current().nextLong(60 * 1000));

        List<Dot> dotList = new ArrayList<>(createSize);
        for (int i = 0; i < createSize; i++) {
            int exceptionCode = ThreadLocalRandom.current().nextInt(0, 2);
            dotList.add(new Dot(transactionIdList.get(i), Math.max(Math.abs(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)), from), executionTime, exceptionCode, agentId));
        }

        Collections.shuffle(dotList);

        return dotList;
    }

    private List<Dot> extractDotList(ScatterData scatterData) {
        List<Dot> dotList = new ArrayList<>();

        for (DotGroups dotGroups : scatterData.getScatterData()) {
            dotList.addAll(dotGroups.getSortedDotSet());
        }

        return dotList;
    }

}
