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

package com.navercorp.pinpoint.profiler.sender;

import com.navercorp.pinpoint.common.profiler.message.DataSender;
import com.navercorp.pinpoint.profiler.context.Span;
import com.navercorp.pinpoint.profiler.context.SpanChunk;
import com.navercorp.pinpoint.profiler.context.SpanType;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author emeroad
 */
public class CountingDataSender implements DataSender<SpanType> {

    private final AtomicInteger senderCounter = new AtomicInteger();

    private final AtomicInteger spanCounter = new AtomicInteger();
    private final AtomicInteger spanChunkCounter = new AtomicInteger();

    @Override
    public boolean send(SpanType data) {
        senderCounter.incrementAndGet();
        if (data instanceof Span) {
            this.spanCounter.incrementAndGet();
        } else if (data instanceof SpanChunk) {
            this.spanChunkCounter.incrementAndGet();
        }
        return false;
    }

    @Override
    public void close() {
        this.senderCounter.set(0);
        this.spanCounter.set(0);
        this.spanChunkCounter.set(0);
    }


    public int getSenderCounter() {
        return senderCounter.get();
    }

    public int getSpanChunkCounter() {
        return spanChunkCounter.get();
    }

    public int getSpanCounter() {
        return spanCounter.get();
    }

    public int getTotalCount() {
        return senderCounter.get();
    }

    @Override
    public String toString() {
        return "CountingDataSender{" +
                ", senderCounter=" + senderCounter +
                ", spanCounter=" + spanCounter +
                ", spanChunkCounter=" + spanChunkCounter +
                '}';
    }
}
