/* Generated SBE (Simple Binary Encoding) message codec. */
package com.koqonut.generated;

import org.agrona.MutableDirectBuffer;


/**
 * Represents a quote and amount of trade
 */
@SuppressWarnings("all")
public final class CityTempEncoder {
    public static final int BLOCK_LENGTH = 72;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final CityTempEncoder parentMessage = this;
    private MutableDirectBuffer buffer;
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

    public MutableDirectBuffer buffer() {
        return buffer;
    }

    public int initialOffset() {
        return initialOffset;
    }

    public int offset() {
        return offset;
    }

    public CityTempEncoder wrap(final MutableDirectBuffer buffer, final int offset) {
        if (buffer != this.buffer) {
            this.buffer = buffer;
        }
        this.initialOffset = offset;
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public CityTempEncoder wrapAndApplyHeader(
            final MutableDirectBuffer buffer, final int offset, final MessageHeaderEncoder headerEncoder) {
        headerEncoder
                .wrap(buffer, offset)
                .blockLength(BLOCK_LENGTH)
                .templateId(TEMPLATE_ID)
                .schemaId(SCHEMA_ID)
                .version(SCHEMA_VERSION);

        return wrap(buffer, offset + MessageHeaderEncoder.ENCODED_LENGTH);
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

    public CityTempEncoder city(final int index, final byte value) {
        if (index < 0 || index >= 64) {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 0 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public CityTempEncoder putCity(final byte[] src, final int srcOffset) {
        final int length = 64;
        if (srcOffset < 0 || srcOffset > (src.length - length)) {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(offset + 0, src, srcOffset, length);

        return this;
    }

    public CityTempEncoder city(final String src) {
        final int length = 64;
        final byte[] bytes = (null == src || src.isEmpty()) ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY : src.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (bytes.length > length) {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + bytes.length);
        }

        buffer.putBytes(offset + 0, bytes, 0, bytes.length);

        for (int start = bytes.length; start < length; ++start) {
            buffer.putByte(offset + 0 + start, (byte) 0);
        }

        return this;
    }

    public CityTempEncoder temperature(final double value) {
        buffer.putDouble(offset + 64, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    public String toString() {
        if (null == buffer) {
            return "";
        }

        return appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder) {
        if (null == buffer) {
            return builder;
        }

        final CityTempDecoder decoder = new CityTempDecoder();
        decoder.wrap(buffer, initialOffset, BLOCK_LENGTH, SCHEMA_VERSION);

        return decoder.appendTo(builder);
    }
}
