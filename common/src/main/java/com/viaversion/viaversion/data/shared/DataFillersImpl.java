/*
 * This file is part of ViaVersion - https://github.com/ViaVersion/ViaVersion
 * Copyright (C) 2023-2023 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.viaversion.data.shared;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.shared.DataFillers;
import com.viaversion.viaversion.api.protocol.Protocol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DataFillersImpl implements DataFillers {

    private static final Object LOCK = new Object(); // Prevent concurrent access with clear and client joins
    private final Map<Class<? extends Protocol>, List<Initializer>> initializersByProtocol = new HashMap<>();
    private final Map<Class<?>, Initializer> initializers = new HashMap<>();
    private final Set<Class<?>> intents = new HashSet<>();
    private boolean cleared;

    @Override
    public void register(final Class<?> type, final Protocol<?, ?, ?, ?> protocol, final Runnable initializer) {
        Preconditions.checkArgument(!cleared, "Cannot register initializer after the mapping data loader has shut down. "
                + "Consider setting ProtocolLoadingIntention to ALL in ProtocolManager instead");
        final Initializer value = new Initializer(protocol, initializer);
        initializers.put(type, value);
        initializersByProtocol.computeIfAbsent(protocol.getClass(), $ -> new ArrayList<>()).add(value);
    }

    @Override
    public void registerIntent(final Class<?> clazz) {
        Preconditions.checkArgument(!cleared, "Cannot register intention after the mapping data loader has shut down. "
                + "Consider setting ProtocolLoadingIntention to ALL in ProtocolManager instead");
        // Initializer might not have been added yet, so do not check for it
        intents.add(clazz);
    }

    @Override
    public void initialize(final Class<?> clazz) {
        final Initializer initializer = initializers.get(clazz);
        Preconditions.checkNotNull(initializer, "Initializer for " + clazz + " not found");
        initializer.run();
    }

    @Override
    public void initializeFromProtocol(final Class<? extends Protocol> clazz) {
        final List<Initializer> initializers = initializersByProtocol.get(clazz);
        if (initializers == null) {
            return;
        }

        for (final Initializer initializer : initializers) {
            initializer.run();
        }
    }

    @Override
    public void initializeRequired() {
        final List<String> loadedData = new ArrayList<>();
        final List<MappingData> loadedMappingData = new ArrayList<>();
        for (final Class<?> intent : intents) {
            final Initializer initializer = initializers.get(intent);
            if (initializer == null) {
                throw new IllegalStateException("Initializer for " + intent.getSimpleName() + " not found");
            }

            if (initializer.protocol.isRegistered()) {
                // Registered, so its data will be already be loaded
                continue;
            }

            final MappingData mappingData = initializer.protocol.getMappingData();
            if (!mappingData.isLoaded()) {
                mappingData.load();
                loadedMappingData.add(mappingData);
            }

            loadedData.add(intent.getSimpleName());
            initializer.run();
        }

        if (!loadedData.isEmpty()) {
            Via.getPlatform().getLogger().fine("Loaded additional data classes: " + String.join(", ", loadedData));

            // Unload data of unregistered protocols again
            for (final MappingData data : loadedMappingData) {
                data.unload();
            }
        }
    }

    @Override
    public boolean initializedTypesForProtocol(final Class<? extends Protocol> protocolClass) {
        final List<Initializer> initializers;
        synchronized (LOCK) {
            initializers = initializersByProtocol.get(protocolClass);
        }
        if (initializers == null) {
            return true;
        }

        for (final Initializer initializer : initializers) {
            if (!initializer.ran) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clear() {
        synchronized (LOCK) {
            initializers.clear();
            intents.clear();
            cleared = true;
        }
    }

    private static final class Initializer {
        private final Protocol<?, ?, ?, ?> protocol;
        private final Runnable loader;
        private boolean ran;

        private Initializer(final Protocol<?, ?, ?, ?> protocol, final Runnable loader) {
            this.protocol = protocol;
            this.loader = loader;
        }

        public synchronized void run() {
            if (ran) {
                return;
            }

            loader.run();
            ran = true;
        }
    }
}
