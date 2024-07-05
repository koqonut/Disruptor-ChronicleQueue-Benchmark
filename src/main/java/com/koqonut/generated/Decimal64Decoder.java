/* Generated SBE (Simple Binary Encoding) message codec. */
package com.koqonut.generated;

import org.agrona.DirectBuffer;

@SuppressWarnings("all")
public final class Decimal64Decoder {
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;
    public static final int ENCODED_LENGTH = 8;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private DirectBuffer buffer;

    public static int mantissaEncodingOffset() {
        return 0;
    }

    public static int mantissaEncodingLength() {
        return 8;
    }

    public static int mantissaSinceVersion() {
        return 0;
    }

    public static long mantissaNullValue() {
        return -9223372036854775808L;
    }

    public static long mantissaMinValue() {
        return -9223372036854775807L;
    }

    public static long mantissaMaxValue() {
        return 9223372036854775807L;
    }

    public static int exponentEncodingOffset() {
        return 8;
    }

    public static int exponentEncodingLength() {
        return 0;
    }

    public static int exponentSinceVersion() {
        return 0;
    }

    public static byte exponentNullValue() {
        return (byte) -128;
    }

    public static byte exponentMinValue() {
        return (byte) -127;
    }

    public static byte exponentMaxValue() {
        return (byte) 127;
    }

    public Decimal64Decoder wrap(final DirectBuffer buffer, final int offset) {
        if (buffer != this.buffer) {
            this.buffer = buffer;
        }
        this.offset = offset;

        return this;
    }

    public DirectBuffer buffer() {
        return buffer;
    }

    public int offset() {
        return offset;
    }

    public int encodedLength() {
        return ENCODED_LENGTH;
    }

    public int sbeSchemaId() {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion() {
        return SCHEMA_VERSION;
    }

    public long mantissa() {
        return buffer.getLong(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public byte exponent() {
        return (byte) -2;
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

        builder.append('(');
        builder.append("mantissa=");
        builder.append(this.mantissa());
        builder.append('|');
        builder.append(')');

        return builder;
    }
}
