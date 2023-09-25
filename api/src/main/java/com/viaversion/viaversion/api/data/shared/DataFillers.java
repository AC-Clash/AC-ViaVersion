/*
 * This file is part of ViaVersion - https://github.com/ViaVersion/ViaVersion
 * Copyright (C) 2023 ViaVersion and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.viaversion.viaversion.api.data.shared;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;

/**
 * This class is used to register data that is shared between multiple protocols.
 * <p>
 * The registration, intention, and loading processes are as follows:
 * <ol>
 *     <li>Register the type to be filled with the given mapping data in {@link AbstractProtocol#registerDataInitializers(DataFillers)}.</li>
 *     <li>Register an intent to use the data from the given type in every protocol it is used in {@link AbstractProtocol#registerIntents(DataFillers)}</li>
 *     <li>The previously registered data are initialized in the same protocols via {@link AbstractProtocol#onMappingDataLoaded()}</li>
 *     <li>At the end, {@link #initializeRequired()} goes through required data that did not have its protocol loaded</li>
 * </ol>
 */
public interface DataFillers {

    /**
     * Register a type to be filled with the given mapping data.
     *
     * @param type        type of the object to register
     * @param protocol    protocol of which the mapping data is required
     * @param initializer initializer to run when the type is registered
     */
    void register(Class<?> type, Protocol<?, ?, ?, ?> protocol, Runnable initializer);

    /**
     * Registers an intent to use the data from the given type, making sure it is loaded.
     *
     * @param clazz the class to register
     */
    void registerIntent(Class<?> clazz);

    /**
     * Initializes types from the given protocol.
     *
     * @param clazz the protocol class
     */
    void initializeFromProtocol(Class<? extends Protocol> clazz);

    /**
     * Initializes the previously registered data.
     *
     * @param clazz the class of the type to initialize
     */
    void initialize(Class<?> clazz);

    /**
     * Initializes the previously registered data that had intents set.
     *
     * @throws IllegalStateException if no initializer was found for a required type
     */
    void initializeRequired();

    /**
     * Returns whether the given protocol has had its required types initialized.
     *
     * @param protocolClass protocol class
     * @return whether the given protocol has had its required types initialized
     */
    boolean initializedTypesForProtocol(Class<? extends Protocol> protocolClass);

    /**
     * Clears stored data, to be called once all mapping data loading has been completed.
     */
    void clear();
}
