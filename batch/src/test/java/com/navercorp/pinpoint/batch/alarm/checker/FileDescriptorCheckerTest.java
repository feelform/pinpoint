/*
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.batch.alarm.checker;

import com.navercorp.pinpoint.batch.alarm.collector.pinot.FileDescriptorDataCollector;
import com.navercorp.pinpoint.web.alarm.vo.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author minwoo.jung
 */
@ExtendWith({MockitoExtension.class})
public class FileDescriptorCheckerTest {

    @Test
    public void checkTest() {
        Rule rule = new Rule();
        rule.setThreshold(10);
        FileDescriptorDataCollector fileDescriptorDataCollector = mock(FileDescriptorDataCollector.class);

        Map<String, Long> result = Map.ofEntries(
                Map.entry("testAgent1", 10L),
                Map.entry("testAgent2", 0L));

        when(fileDescriptorDataCollector.getFileDescriptorCount()).thenReturn(result);
        FileDescriptorChecker fileDescriptorChecker = new FileDescriptorChecker(fileDescriptorDataCollector, rule);
        fileDescriptorChecker.check();
        assertTrue(fileDescriptorChecker.isDetected());
    }

    @Test
    public void check2Test() {
        Rule rule = new Rule();
        rule.setThreshold(20);
        FileDescriptorDataCollector fileDescriptorDataCollector = mock(FileDescriptorDataCollector.class);

        Map<String, Long> result = Map.ofEntries(
                Map.entry("testAgent1", 10L),
                Map.entry("testAgent2", 0L));

        when(fileDescriptorDataCollector.getFileDescriptorCount()).thenReturn(result);
        FileDescriptorChecker fileDescriptorChecker = new FileDescriptorChecker(fileDescriptorDataCollector, rule);
        fileDescriptorChecker.check();
        assertFalse(fileDescriptorChecker.isDetected());
    }
}