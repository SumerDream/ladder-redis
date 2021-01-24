package com.murphy.edu.ladder.redis.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


@Slf4j
public final class GzipUtil {

    /**
     * 序列化
     *
     * @param object b
     * @return byte b
     */
    public static byte[] serialize(Object object) {
        try {
            return writeObject(object);
        } catch (Exception e) {
            log.warn("GzipUtil.serialize.gzip error {}", e.getMessage(), e);
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return bytes;
        } catch (IOException e) {
            log.error("GzipUtil.serialize error {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 反序列化
     *
     * @param bytes b
     * @return Object o
     */
    public static Object deserialize(byte[] bytes) {
        try {
            return readObject(bytes);
        } catch (Exception e) {
            log.warn("GzipUtil.deserialize.gzip error {}", e.getMessage(), e);
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            return object;
        } catch (Exception e) {
            log.warn("GzipUtil.deserialize error {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * gizp
     *
     * @param object o
     * @return byte[]
     * @throws IOException e
     */
    private static byte[] writeObject(Object object) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteOutputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();
        gzipOutputStream.close();
        byteOutputStream.close();
        return byteOutputStream.toByteArray();
    }

    /**
     * un gizp
     *
     * @param bytes b
     * @return Object o
     * @throws IOException e
     */
    private static Object readObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        gzipInputStream.close();
        byteArrayInputStream.close();
        return object;
    }
}
