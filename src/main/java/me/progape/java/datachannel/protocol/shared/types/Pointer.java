package me.progape.java.datachannel.protocol.shared.types;

/**
 * @author progape
 * @date 2022-05-08
 */
public class Pointer<T> {
    private T data;

    public Pointer(T data) {
        this.data = data;
    }

    public T get() {
        return data;
    }

    public void set(T data) {
        this.data = data;
    }
}
