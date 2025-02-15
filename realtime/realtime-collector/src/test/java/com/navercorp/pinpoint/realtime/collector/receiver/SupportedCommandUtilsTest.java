/*
 * Copyright 2023 NAVER Corp.
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
package com.navercorp.pinpoint.realtime.collector.receiver;

import com.navercorp.pinpoint.realtime.dto.CommandType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author youngjin.kim2
 */
public class SupportedCommandUtilsTest {

    private static final List<CommandType> TYPES = List.of(
            CommandType.ECHO,
            CommandType.ACTIVE_THREAD_COUNT,
            CommandType.ACTIVE_THREAD_DUMP,
            CommandType.ACTIVE_THREAD_LIGHT_DUMP
    );

    private static final List<Integer> CODES = TYPES.stream()
            .map(el -> Integer.valueOf(el.getCode()))
            .toList();

    @Test
    public void testConverting() {
        List<CommandType> result = SupportedCommandUtils.newSupportCommandList(CODES);
        assertThat(result).hasSameElementsAs(TYPES);
    }

}
