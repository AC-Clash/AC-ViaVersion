package us.myles.ViaVersion.protocols.protocolsnapshotto1_12_2;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import us.myles.ViaVersion.protocols.protocolsnapshotto1_12_2.packets.EntityPackets;
import us.myles.ViaVersion.protocols.protocolsnapshotto1_12_2.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocolsnapshotto1_12_2.packets.WorldPackets;
import us.myles.ViaVersion.protocols.protocolsnapshotto1_12_2.storage.EntityTracker;

// Development of 1.13 support!
public class ProtocolSnapshotTo1_12_2 extends Protocol {

    static {
        MappingData.init();
    }

    @Override
    protected void registerPackets() {
        // Register grouped packet changes
        EntityPackets.register(this);
        WorldPackets.register(this);
        InventoryPackets.register(this);

        // Outgoing packets

        // Statistics
        registerOutgoing(State.PLAY, 0x7, 0x7, new PacketRemapper() {
            @Override
            public void registerMap() {
                // TODO: This packet has changed

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.cancel();
                    }
                });
            }
        });

        registerOutgoing(State.PLAY, 0xF, 0xE);

        // Tab-Complete
        registerOutgoing(State.PLAY, 0xE, 0x10, new PacketRemapper() {
            @Override
            public void registerMap() {
                // TODO: This packet has changed
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.cancel();
                    }
                });
            }
        });

        // New packet 0x11, declare commands
        registerOutgoing(State.PLAY, 0x11, 0x12);
        registerOutgoing(State.PLAY, 0x12, 0x13);
        registerOutgoing(State.PLAY, 0x13, 0x14);

        registerOutgoing(State.PLAY, 0x15, 0x16);

        registerOutgoing(State.PLAY, 0x17, 0x18);

        registerOutgoing(State.PLAY, 0x1A, 0x1B);
        registerOutgoing(State.PLAY, 0x1B, 0x1C);
        registerOutgoing(State.PLAY, 0x1C, 0x1D);
        registerOutgoing(State.PLAY, 0x1D, 0x1E);
        registerOutgoing(State.PLAY, 0x1E, 0x1F);
        registerOutgoing(State.PLAY, 0x1F, 0x20);

        registerOutgoing(State.PLAY, 0x21, 0x22);

        // Join (save dimension id)
        registerOutgoing(State.PLAY, 0x23, 0x24, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Entity ID
                map(Type.UNSIGNED_BYTE); // 1 - Gamemode
                map(Type.INT); // 2 - Dimension

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientChunks = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 1);
                        clientChunks.setEnvironment(dimensionId);
                    }
                });
            }
        });

        registerOutgoing(State.PLAY, 0x24, 0x25);
        registerOutgoing(State.PLAY, 0x25, 0x26);
        registerOutgoing(State.PLAY, 0x26, 0x27);
        registerOutgoing(State.PLAY, 0x27, 0x28);
        registerOutgoing(State.PLAY, 0x28, 0x29);
        registerOutgoing(State.PLAY, 0x29, 0x2A);
        registerOutgoing(State.PLAY, 0x2A, 0x2B);

        // Craft Recipe Response
        registerOutgoing(State.PLAY, 0x2B, 0x2C, new PacketRemapper() {
            @Override
            public void registerMap() {
                // TODO: This packet has changed
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.cancel();
                    }
                });
            }
        });

        registerOutgoing(State.PLAY, 0x2C, 0x2D);
        registerOutgoing(State.PLAY, 0x2D, 0x2E);
        registerOutgoing(State.PLAY, 0x2E, 0x2F);
        registerOutgoing(State.PLAY, 0x2F, 0x30);
        registerOutgoing(State.PLAY, 0x30, 0x31);

        // Unlock Recipes
        registerOutgoing(State.PLAY, 0x31, 0x32, new PacketRemapper() {
            @Override
            public void registerMap() {
                // TODO: This packet has changed
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.cancel();
                    }
                });
            }
        });

        registerOutgoing(State.PLAY, 0x33, 0x34);
        registerOutgoing(State.PLAY, 0x34, 0x35);

        // Respawn (save dimension id)
        registerOutgoing(State.PLAY, 0x35, 0x36, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Dimension ID
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 0);
                        clientWorld.setEnvironment(dimensionId);
                    }
                });
            }
        });

        registerOutgoing(State.PLAY, 0x36, 0x37);
        registerOutgoing(State.PLAY, 0x37, 0x38);
        registerOutgoing(State.PLAY, 0x38, 0x39);
        registerOutgoing(State.PLAY, 0x39, 0x3A);
        registerOutgoing(State.PLAY, 0x3A, 0x3B);
        registerOutgoing(State.PLAY, 0x3B, 0x3C);

        registerOutgoing(State.PLAY, 0x3D, 0x3E);
        registerOutgoing(State.PLAY, 0x3E, 0x3F);

        registerOutgoing(State.PLAY, 0x40, 0x41);
        registerOutgoing(State.PLAY, 0x41, 0x42);
        // Scoreboard Objective
        registerOutgoing(State.PLAY, 0x42, 0x43, new PacketRemapper() {
            @Override
            public void registerMap() {
                // TODO: This packet has changed
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.cancel();
                    }
                });
            }
        });

        registerOutgoing(State.PLAY, 0x43, 0x44);
        registerOutgoing(State.PLAY, 0x44, 0x45);
        registerOutgoing(State.PLAY, 0x45, 0x46);
        registerOutgoing(State.PLAY, 0x46, 0x47);
        registerOutgoing(State.PLAY, 0x47, 0x48);
        registerOutgoing(State.PLAY, 0x48, 0x49);
        // New packet 0x4A - Stop sound (TODO: Migrate from Plugin Messages)
        registerOutgoing(State.PLAY, 0x49, 0x4B);
        registerOutgoing(State.PLAY, 0x4A, 0x4C);
        registerOutgoing(State.PLAY, 0x4B, 0x4D);
        registerOutgoing(State.PLAY, 0x4C, 0x4E);
        registerOutgoing(State.PLAY, 0x4D, 0x4F);
        registerOutgoing(State.PLAY, 0x4E, 0x50);
        registerOutgoing(State.PLAY, 0x4F, 0x51);
        // New packet 0x52 - Declare Recipes
        // New packet 0x53 - Tags

        // Incoming packets
        registerIncoming(State.PLAY, 0x2, 0x1);
        registerIncoming(State.PLAY, 0x3, 0x2);
        registerIncoming(State.PLAY, 0x4, 0x3);

        // Tab-Complete
        registerIncoming(State.PLAY, 0x1, 0x4, new PacketRemapper() {
            @Override
            public void registerMap() {
                // TODO: This packet has changed
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.cancel();
                    }
                });
            }
        });

        // Craft Recipe Request
        registerIncoming(State.PLAY, 0x12, 0x12, new PacketRemapper() {
            @Override
            public void registerMap() {
                // TODO: This packet has changed
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new EntityTracker(userConnection));
        if (!userConnection.has(ClientWorld.class))
            userConnection.put(new ClientWorld(userConnection));
    }
}
