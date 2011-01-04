package org.jwebsocket.netty.engines;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class JWebSocketKeyStore {
    private static final short[] DATA = new short[] {};
    
    public static InputStream asInputStream() {
        byte[] data = new byte[DATA.length];
        for (int i = 0; i < data.length; i ++) {
            data[i] = (byte) DATA[i];
        }
        return new ByteArrayInputStream(data);
    }

    public static char[] getCertificatePassword() {
        return "jwebsocket".toCharArray();
    }

    public static char[] getKeyStorePassword() {
        return "jwebsocket".toCharArray();
    }

    private JWebSocketKeyStore() {
        throw new AssertionError();
    }
}
