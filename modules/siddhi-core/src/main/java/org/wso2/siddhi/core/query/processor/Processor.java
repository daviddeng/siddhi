/*
 * Copyright (c) 2005 - 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.wso2.siddhi.core.query.processor;

import org.wso2.siddhi.core.event.ComplexEventChunk;

public interface Processor {

    /**
     * Process the handed StreamEvent
     *
     * @param complexEventChunk event chunk to be processed
     */
    public void process(ComplexEventChunk complexEventChunk);

    /**
     * Get next processor element in the processor chain. Processed event should be sent to next processor
     *
     * @return
     */
    public Processor getNextProcessor();

    /**
     * Set next processor element in processor chain
     *
     * @param processor Processor to be set as next element of processor chain
     */
    public void setNextProcessor(Processor processor);

    /**
     * Set as the last element of the processor chain
     *
     * @param processor Last processor in the chain
     */
    public void setToLast(Processor processor);

    /**
     * Clone a copy of processor
     *
     * @return
     * @param key
     */
    public Processor cloneProcessor(String key);

}
