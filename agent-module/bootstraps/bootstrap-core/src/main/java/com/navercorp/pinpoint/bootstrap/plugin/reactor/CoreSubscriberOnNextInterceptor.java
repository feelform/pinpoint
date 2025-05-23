/*
 * Copyright 2024 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.bootstrap.plugin.reactor;

import com.navercorp.pinpoint.bootstrap.async.AsyncContextAccessorUtils;
import com.navercorp.pinpoint.bootstrap.context.AsyncContext;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AsyncContextSpanEventNestedBlockApiIdAwareAroundInterceptor;
import com.navercorp.pinpoint.common.trace.ServiceType;

public class CoreSubscriberOnNextInterceptor extends AsyncContextSpanEventNestedBlockApiIdAwareAroundInterceptor {
    public static final String REACTOR_ON_NEXT_TRACE_SCOPE = "##REACTOR_ON_NEXT_TRACE_SCOPE";
    private final ServiceType serviceType;

    public CoreSubscriberOnNextInterceptor(TraceContext traceContext, ServiceType serviceType) {
        super(traceContext, REACTOR_ON_NEXT_TRACE_SCOPE);
        this.serviceType = serviceType;
    }

    @Override
    public AsyncContext getAsyncContext(Object target, Object[] args) {
        return AsyncContextAccessorUtils.getAsyncContext(target);
    }

    @Override
    public void doInBeforeTrace(SpanEventRecorder recorder, AsyncContext asyncContext, Object target, int apiId, Object[] args) {
    }

    public AsyncContext getAsyncContext(Object target, Object[] args, Object result, Throwable throwable) {
        return AsyncContextAccessorUtils.getAsyncContext(target);
    }

    @Override
    public void doInAfterTrace(SpanEventRecorder recorder, Object target, int apiId, Object[] args, Object result, Throwable throwable) {
        recorder.recordServiceType(serviceType);
        recorder.recordApiId(apiId);
        recorder.recordException(throwable);
    }
}