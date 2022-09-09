package me.progape.java.datachannel.protocol.shared.types;

/**
 * @author progape
 * @date 2022-05-08
 */
public class BytesWrapper {
    private final byte[] bytes;
    private final int offset;

    public BytesWrapper(byte[] bytes, int offset) {
        this.bytes = bytes;
        if (offset < 0 || offset >= this.bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        this.offset = offset;
    }

    public byte at(int pos) {
        if (this.offset + pos >= this.bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        return this.bytes[this.offset + pos];
    }

    public BytesWrapper forward(int bytes) {
        return new BytesWrapper(this.bytes, this.offset + bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getOffset() {
        return offset;
    }
}
