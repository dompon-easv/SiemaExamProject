package dk.siema.siemaexamproject.dal.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public  class BytesConverter {
    /* UUID → BINARY(16) */
    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    /* BINARY(16) → UUID */
    public static UUID bytesToUUID(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }
}
