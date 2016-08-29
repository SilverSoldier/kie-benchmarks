/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.cep;

import org.drools.benchmarks.common.AbstractThroughputBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.domain.EventA;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

@State(Scope.Benchmark)
public class WindowThroughputBenchmark extends AbstractThroughputBenchmark {

    @Setup
    public void setupKieBase() throws ProviderException {
        final String drl = " import " + EventA.class.getCanonicalName() + ";\n" +
                " declare " + EventA.class.getName() + " @role( event ) @duration(duration) end\n" +
                " rule R1 \n" +
                " when \n" +
                "     EventA() over window:time(200ms)\n" +
                " then \n" +
                " end";

        createKieBaseFromDrl(drl, EventProcessingOption.STREAM);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        createKieSession();
        final Runnable fireUntilHalt = new Runnable() {
            public void run() {
                kieSession.fireUntilHalt();
            }
        };
    }

    @Benchmark
    @Threads(1)
    public FactHandle insertEvent() {
        final EventA event = new EventA();
        event.setDuration(100);
        final FactHandle factHandle = kieSession.insert(event);
//        kieSession.fireAllRules();
        return factHandle;
    }
}
