/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.cep;

import com.juege.RiskCtrlSys.flink.job.cep.pattern.DynamicPattern;
import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.functions.NullByteKeySelector;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.functions.TimedOutPartialMatchHandler;
import org.apache.flink.cep.nfa.compiler.NFACompiler;
import org.apache.flink.cep.operator.CepOperator;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import javax.annotation.Nullable;
import java.util.Map;

import static org.apache.flink.util.Preconditions.checkNotNull;

/** Utility method for creating {@link PatternStream}. */
@Internal
final class PatternStreamBuilder<IN> {

    private final DataStream<IN> inputStream;
    //第2个自定义代码
    private final DynamicPattern<IN> dynamicPattern;

    private final Pattern<IN, ?> pattern;

    private final EventComparator<IN> comparator;

    /**
     * Side output {@code OutputTag} for late data. If no tag is set late data will be simply
     * dropped.
     */
    private final OutputTag<IN> lateDataOutputTag;

    /**
     * The time behaviour to specify processing time or event time. Default time behaviour is {@link
     * TimeBehaviour#EventTime}.
     */
    private final TimeBehaviour timeBehaviour;

    /**
     * The time behaviour enum defines how the system determines time for time-dependent order and
     * operations that depend on time.
     */
    enum TimeBehaviour {
        ProcessingTime,
        EventTime
    }

    private PatternStreamBuilder(
            final DataStream<IN> inputStream,
            final Pattern<IN, ?> pattern,
            final TimeBehaviour timeBehaviour,
            @Nullable final EventComparator<IN> comparator,
            @Nullable final OutputTag<IN> lateDataOutputTag) {
        this.inputStream = checkNotNull(inputStream);
        //第1个自定义代码
        this.dynamicPattern = null;
        this.pattern = checkNotNull(pattern);
        this.timeBehaviour = checkNotNull(timeBehaviour);
        this.comparator = comparator;
        this.lateDataOutputTag = lateDataOutputTag;
    }

    /**
     * author: Juege
     * description: 第4个自定义方法
     * @param inputStream:
     * @param dynamicPattern:
     * @param timeBehaviour:
     * @param comparator:
     * @param lateDataOutputTag:
     * @return null
     */
    private PatternStreamBuilder(
            final DataStream<IN> inputStream,
            final DynamicPattern<IN> dynamicPattern,
            final TimeBehaviour timeBehaviour,
            @Nullable final EventComparator<IN> comparator,
            @Nullable final OutputTag<IN> lateDataOutputTag) {
        this.inputStream = checkNotNull(inputStream);
        this.dynamicPattern = checkNotNull(dynamicPattern);
        this.pattern = null;
        this.timeBehaviour = checkNotNull(timeBehaviour);
        this.comparator = comparator;
        this.lateDataOutputTag = lateDataOutputTag;
    }

    TypeInformation<IN> getInputType() {
        return inputStream.getType();
    }

    /**
     * Invokes the {@link org.apache.flink.api.java.ClosureCleaner} on the given function if closure
     * cleaning is enabled in the {@link ExecutionConfig}.
     *
     * @return The cleaned Function
     */
    <F> F clean(F f) {
        return inputStream.getExecutionEnvironment().clean(f);
    }

    PatternStreamBuilder<IN> withComparator(final EventComparator<IN> comparator) {
        return new PatternStreamBuilder<>(
                inputStream, pattern, timeBehaviour, checkNotNull(comparator), lateDataOutputTag);
    }

    PatternStreamBuilder<IN> withLateDataOutputTag(final OutputTag<IN> lateDataOutputTag) {
        return new PatternStreamBuilder<>(
                inputStream, pattern, timeBehaviour, comparator, checkNotNull(lateDataOutputTag));
    }

    PatternStreamBuilder<IN> inProcessingTime() {
        return new PatternStreamBuilder<>(
                inputStream, pattern, TimeBehaviour.ProcessingTime, comparator, lateDataOutputTag);
    }

    /**
     * author: Juege
     * description: 第8个自定义方法
     * @param :
     * @return org.apache.flink.cep.PatternStreamBuilder<IN>
     */
    PatternStreamBuilder<IN> inProcessingTimeJuege() {
        return new PatternStreamBuilder<>(
                inputStream, dynamicPattern, TimeBehaviour.ProcessingTime, comparator, lateDataOutputTag);
    }

    PatternStreamBuilder<IN> inEventTime() {
        return new PatternStreamBuilder<>(
                inputStream, pattern, TimeBehaviour.EventTime, comparator, lateDataOutputTag);
    }

    /**
     * author: Juege
     * description: 第9个自定义方法
     * @param :
     * @return org.apache.flink.cep.PatternStreamBuilder<IN>
     */
    PatternStreamBuilder<IN> inEventTimeJuege() {
        return new PatternStreamBuilder<>(
                inputStream, dynamicPattern, TimeBehaviour.EventTime, comparator, lateDataOutputTag);
    }

    /**
     * Creates a data stream containing results of {@link PatternProcessFunction} to fully matching
     * event patterns.
     *
     * @param processFunction function to be applied to matching event sequences
     * @param outTypeInfo output TypeInformation of {@link PatternProcessFunction#processMatch(Map,
     *     PatternProcessFunction.Context, Collector)}
     * @param <OUT> type of output events
     * @return Data stream containing fully matched event sequence with applied {@link
     *     PatternProcessFunction}
     */
    <OUT, K> SingleOutputStreamOperator<OUT> build(
            final TypeInformation<OUT> outTypeInfo,
            final PatternProcessFunction<IN, OUT> processFunction) {

        checkNotNull(outTypeInfo);
        checkNotNull(processFunction);

        // 构造序列化器
        final TypeSerializer<IN> inputSerializer =
                inputStream.getType().createSerializer(inputStream.getExecutionConfig());

        final boolean isProcessingTime = timeBehaviour == TimeBehaviour.ProcessingTime;

        // 判断是否获取超时结果
        final boolean timeoutHandling = processFunction instanceof TimedOutPartialMatchHandler;

        //构建CepOperator
        CepOperator<IN, K, OUT> operator = null;
        if(dynamicPattern == null ) {
            // 工厂对象包含了所有的 cep State, 都是静态的
            final NFACompiler.NFAFactory<IN> nfaFactory =
                    NFACompiler.compileFactory(pattern, timeoutHandling);

            operator =
                    new CepOperator<>(
                            inputSerializer,
                            isProcessingTime,
                            nfaFactory,
                            comparator,
                            pattern.getAfterMatchSkipStrategy(),
                            processFunction,
                            lateDataOutputTag);
        }else {

            //第5个自定义代码

            operator =
                    new CepOperator<>(
                            inputSerializer,
                            isProcessingTime,
                            null,
                            comparator,
                            null,
                            processFunction,
                            lateDataOutputTag,
                            dynamicPattern,
                            timeoutHandling);
        }

        final SingleOutputStreamOperator<OUT> patternStream;
        if (inputStream instanceof KeyedStream) {
            KeyedStream<IN, K> keyedStream = (KeyedStream<IN, K>) inputStream;

            patternStream = keyedStream.transform("CepOperator", outTypeInfo, operator);
        } else {
            KeySelector<IN, Byte> keySelector = new NullByteKeySelector<>();

            patternStream =
                    inputStream
                            .keyBy(keySelector)
                            .transform("GlobalCepOperator", outTypeInfo, operator)
                            .forceNonParallel();
        }

        return patternStream;
    }

    // ---------------------------------------- factory-like methods
    // ---------------------------------------- //

    static <IN> PatternStreamBuilder<IN> forStreamAndPattern(
            final DataStream<IN> inputStream, final Pattern<IN, ?> pattern) {
        return new PatternStreamBuilder<>(
                inputStream, pattern, TimeBehaviour.EventTime, null, null);
    }


    /**
     * author: Juege
     * description: 第3个自定义方法
     * @param inputStream:
     * @param dynamicPattern:
     * @return org.apache.flink.cep.PatternStreamBuilder<IN>
     */
    static <IN> PatternStreamBuilder<IN> juegeForStreamAndPattern(
            final DataStream<IN> inputStream, final DynamicPattern<IN> dynamicPattern) {
        return new PatternStreamBuilder<>(
                inputStream, dynamicPattern, TimeBehaviour.EventTime, null, null);
    }
}
