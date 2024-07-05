/* Generated SBE (Simple Binary Encoding) message codec. */
package com.koqonut.generated;

import org.agrona.DirectBuffer;


/**
 * Represents a quote and amount of trade
 */
@SuppressWarnings("all")
public final class CityTempDecoder {
    public static final int BLOCK_LENGTH = 72;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final CityTempDecoder parentMessage = this;
    int actingBlockLength;
    int actingVersion;
    private DirectBuffer buffer;
    private int initialOffset;
    private int offset;
    private int limit;

    public static int cityId() {
        return 1;
    }

    public static int citySinceVersion() {
        return 0;
    }

    public static int cityEncodingOffset() {
        return 0;
    }

    public static int cityEncodingLength() {
        return 64;
    }

    public static String cityMetaAttribute(final MetaAttribute metaAttribute) {
        if (MetaAttribute.PRESENCE == metaAttribute) {
            return "required";
        }

        return "";
    }

    public static byte cityNullValue() {
        return (byte) 0;
    }

    public static byte cityMinValue() {
        return (byte) 32;
    }

    public static byte cityMaxValue() {
        return (byte) 126;
    }

    public static int cityLength() {
        return 64;
    }

    public static String cityCharacterEncoding() {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static int temperatureId() {
        return 2;
    }

    public static int temperatureSinceVersion() {
        return 0;
    }

    public static int temperatureEncodingOffset() {
        return 64;
    }

    public static int temperatureEncodingLength() {
        return 8;
    }

    public static String temperatureMetaAttribute(final MetaAttribute metaAttribute) {
        if (MetaAttribute.PRESENCE == metaAttribute) {
            return "required";
        }

        return "";
    }

    public static double temperatureNullValue() {
        return Double.NaN;
    }

    public static double temperatureMinValue() {
        return 4.9E-324d;
    }

    public static double temperatureMaxValue() {
        return 1.7976931348623157E308d;
    }

    public int sbeBlockLength() {
        return BLOCK_LENGTH;
    }

    public int sbeTemplateId() {
        return TEMPLATE_ID;
    }

    public int sbeSchemaId() {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion() {
        return SCHEMA_VERSION;
    }

    public String sbeSemanticType() {
        return "";
    }

    public DirectBuffer buffer() {
        return buffer;
    }

    public int initialOffset() {
        return initialOffset;
    }

    public int offset() {
        return offset;
    }

    public CityTempDecoder wrap(
            final DirectBuffer buffer,
            final int offset,
            final int actingBlockLength,
            final int actingVersion) {
        if (buffer != this.buffer) {
            this.buffer = buffer;
        }
        this.initialOffset = offset;
        this.offset = offset;
        this.actingBlockLength = actingBlockLength;
        this.actingVersion = actingVersion;
        limit(offset + actingBlockLength);

        return this;
    }

    public CityTempDecoder wrapAndApplyHeader(
            final DirectBuffer buffer,
            final int offset,
            final MessageHeaderDecoder headerDecoder) {
        headerDecoder.wrap(buffer, offset);

        final int templateId = headerDecoder.templateId();
        if (TEMPLATE_ID != templateId) {
            throw new IllegalStateException("Invalid TEMPLATE_ID: " + templateId);
        }

        return wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version());
    }

    public CityTempDecoder sbeRewind() {
        return wrap(buffer, initialOffset, actingBlockLength, actingVersion);
    }

    public int sbeDecodedLength() {
        final int currentLimit = limit();
        sbeSkip();
        final int decodedLength = encodedLength();
        limit(currentLimit);

        return decodedLength;
    }

    public int encodedLength() {
        return limit - offset;
    }

    public int limit() {
        return limit;
    }

    public void limit(final int limit) {
        this.limit = limit;
    }

    public byte city(final int index) {
        if (index < 0 || index >= 64) {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 0 + (index * 1);

        return buffer.getByte(pos);
    }

    public int getCity(final byte[] dst, final int dstOffset) {
        final int length = 64;
        if (dstOffset < 0 || dstOffset > (dst.length - length)) {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(offset + 0, dst, dstOffset, length);

        return length;
    }

    public String city() {
        final byte[] dst = new byte[64];
        buffer.getBytes(offset + 0, dst, 0, 64);

        int end = 0;
        for (; end < 64 && dst[end] != 0; ++end) ;

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.UTF_8);
    }

    public double temperature() {
        return buffer.getDouble(offset + 64, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public String toString() {
        if (null == buffer) {
            return "";
        }

        final CityTempDecoder decoder = new CityTempDecoder();
        decoder.wrap(buffer, initialOffset, actingBlockLength, actingVersion);

        return decoder.appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder) {
        if (null == buffer) {
            return builder;
        }

        final int originalLimit = limit();
        limit(initialOffset + actingBlockLength);
        builder.append("[CityTemp](sbeTemplateId=");
        builder.append(TEMPLATE_ID);
        builder.append("|sbeSchemaId=");
        builder.append(SCHEMA_ID);
        builder.append("|sbeSchemaVersion=");
        if (parentMessage.actingVersion != SCHEMA_VERSION) {
            builder.append(parentMessage.actingVersion);
            builder.append('/');
        }
        builder.append(SCHEMA_VERSION);
        builder.append("|sbeBlockLength=");
        if (actingBlockLength != BLOCK_LENGTH) {
            builder.append(actingBlockLength);
            builder.append('/');
        }
        builder.append(BLOCK_LENGTH);
        builder.append("):");
        builder.append("city=");
        for (int i = 0; i < cityLength() && this.city(i) > 0; i++) {
            builder.append((char) this.city(i));
        }
        builder.append('|');
        builder.append("temperature=");
        builder.append(this.temperature());

        limit(originalLimit);

        return builder;
    }

    public CityTempDecoder sbeSkip() {
        sbeRewind();

        return this;
    }
}
