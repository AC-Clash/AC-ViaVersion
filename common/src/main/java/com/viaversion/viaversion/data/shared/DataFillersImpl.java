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
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.shared.DataFillers;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DataFillersImpl implements DataFillers {

    private final Map<Class<?>, Initializer> initializers = new HashMap<>();
    private final Set<Class<?>> intents = new HashSet<>();

    @Override
    public void register(final Class<?> type, final MappingData mappingData, final Runnable initializer) {
        initializers.put(type, new Initializer(mappingData, initializer));
    }

    @Override
    public void registerIntent(final Class<?> clazz) {
        // Initializer might not have been added yet, so we can't null check
        intents.add(clazz);
    }

    @Override
    public void initialize(final Class<?> clazz) {
        final Initializer initializer = initializers.get(clazz);
        Preconditions.checkNotNull(initializer, "Initializer for " + clazz + " not found");
        initializer.loader.run();
    }

    @Override
    public synchronized void initializeRequired() {
        for (final Class<?> intent : intents) {
            final Initializer initializer = initializers.get(intent);
            if (initializer == null) {
                throw new IllegalStateException("Initializer for " + intent.getSimpleName() + " not found");
            }

            if (!initializer.mappingData.isLoaded()) {
                initializer.mappingData.load(); // TODO DONT LOAD ALL, MAKE SURE TO CALL THIS ASYNC
                initializer.loader.run();
                initializer.mappingData.unload();
            }
        }

        intents.clear();
        initializers.clear();
    }

    private static final class Initializer {
        private final MappingData mappingData;
        private final Runnable loader;

        private Initializer(final MappingData mappingData, final Runnable loader) {
            this.mappingData = mappingData;
            this.loader = loader;
        }
    }
}
