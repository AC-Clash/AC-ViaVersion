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
package com.viaversion.viaversion.api.protocol;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

/**
 * A functional interface to determine whether a protocol should be loaded by {@link ProtocolManager}.
 */
@FunctionalInterface
public interface ProtocolLoadingIntention {

    /**
     * A protocol loading intention that loads all protocols.
     */
    ProtocolLoadingIntention ALL = ($1, $2, $3) -> true;

    /**
     * Returns whether the protocol should be loaded by {@link ProtocolManager}.
     *
     * @param protocol      the protocol to check
     * @param clientVersion the client version supported by the protocol
     * @param serverVersion the server version supported by the protocol
     * @return whether the protocol should be loaded
     */
    boolean shouldBeLoaded(Protocol<?, ?, ?, ?> protocol, ProtocolVersion clientVersion, ProtocolVersion serverVersion);

    static ProtocolLoadingIntention fromServerVersion(final ProtocolVersion minServerVersion) {
        return (protocol, clientVersion, serverVersion) -> clientVersion.higherThan(serverVersion) && clientVersion.higherThan(minServerVersion);
    }

    static ProtocolLoadingIntention upToClientVersion(final ProtocolVersion maxClientVersion) {
        return (protocol, clientVersion, serverVersion) -> clientVersion.higherThan(serverVersion) && serverVersion.lowerThan(maxClientVersion);
    }

    static ProtocolLoadingIntention forServerVersion(final ProtocolVersion version) {
        return (protocol, clientVersion, serverVersion) -> clientVersion.higherThan(serverVersion)
                ? version.lowerThan(clientVersion) : version.higherThan(clientVersion);
    }

    static ProtocolLoadingIntention forClientVersion(final ProtocolVersion version) {
        return (protocol, clientVersion, serverVersion) -> clientVersion.lowerThan(serverVersion)
                ? version.higherThan(clientVersion) : version.lowerThan(serverVersion);
    }
}
