package heast.core.network;

import heast.core.security.Keychain;
import io.netty.buffer.ByteBuf;
import heast.core.security.AES;
import heast.core.security.RSA;
import heast.core.utility.ByteBufImpl;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

public class PacketBuf extends ByteBufImpl {

    public PacketBuf(ByteBuf buf) {
        super(buf);
    }

    /**
     * Writes a variable-length integer to the buffer that may use less bytes than a regular integer.
     * @param value The integer to write.
     *
     * @see #readVarInt() to read the integer back.
     */
    public void writeVarInt(int value) {
        while ((value & -128) != 0) {
            writeByte(value & 127 | 128);
            value >>>= 7;   //7 because a byte has 8 bits
        }
        writeByte(value);
    }

    /**
     * Reads a variable-length integer from the buffer.
     * @return The integer that was read.
     */
    public int readVarInt() {
        int run = 0, len = 0;
        byte cur;
        do {
            cur = readByte();
            run |= (cur & 127) << len++ * 7;
            if (len > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((cur & 128) == 128);

        return run;
    }

    /**
     * Writes a string of any length to the buffer.
     * @param str The string to write.
     *
     * @see #readString() to read the string back.
     */
    public void writeString(final String str) {
        if (str != null) {
            writeVarInt(str.length());
            writeBytes(str.getBytes());
        } else {
            writeVarInt(-1);
        }
    }

    /**
     * Writes a by RSA encrypted byte array of any length to the buffer.
     * @param str The string to write.
     * @see #readBytes() to read the String back (as raw Data).
     */
    public void writeBytesEncryptRSA(final byte[] str, BigInteger e,BigInteger n){
        if (str != null) {
            byte[] data=RSA.INSTANCE.encryptLargeBytes(str,e,n);

            writeVarInt(data.length);
            writeBytes(data);
        } else {
            writeVarInt(-1);
        }
    }

    /**
     * Reads a string of any length from the buffer.
     * @return The string read.
     *
     * @see #writeString(String) to write the string to the buffer.
     */
    public String readString() {
        int len = readVarInt();
        if (len != -1) {
            return readBytes(len).toString(StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }
    /**
     * Reads an encrypted String of any length from the buffer.
     * @return The data read.
     *
     * @see #writeBytesEncryptRSA to write the string to the buffer.
     */
    public byte[] readBytes() {
        int len = readVarInt();
        if (len != -1) {

            byte[] data= new byte[len];

            readBytes(data);

            return data;
        } else {
            return null;
        }
    }

    public void writePlainBytes(byte[] bytes){
        if (bytes != null) {
            writeVarInt(bytes.length);
            writeBytes(bytes);
        } else {
            writeVarInt(-1);
        }
    }

    /**
     * Writes an enum to the buffer.
     * @param value The enum to write.
     * @param <E> The enum type.
     */
    public <E extends Enum<E>> void writeEnum(final E value) {
        writeVarInt(value.ordinal());
    }

    /**
     * Reads an enum from the buffer.
     * @param <E> The enum type.
     * @param clazz The enum class.
     * @return The enum read.
     */
    public <E extends Enum<E>> E readEnum(final Class<E> clazz) {
        return clazz.getEnumConstants()[readVarInt()];
    }

    /**
     * Writes an RSA Key to the buffer.
     * @param key The key to write.
     *
     * @see #readRSAKey() to read the key back.
     */
    public void writeRSAKey(final BigInteger key) {
        if (key != null) {
            writeString(key.toString());
        } else {
            writeString(null);
        }
    }

    /**
     * Reads an RSA Key from the buffer.
     * @return The key read.
     *
     * @see #writeRSAKey(BigInteger) to write the key to the buffer.
     */
    public final BigInteger readRSAKey() {
        String key = readString();
        if (!key.isEmpty()) {
            return new BigInteger(key);
        } else {
            return null;
        }
    }

    /**
     * Writes an AES Key to the buffer.
     * @param key The key to write.
     *
     * @see #readAESKey() to read the key back.
     */
    public void writeAESKey(final byte[] key) {
        if (key != null && key.length != 64) {
            writeBytes(key);
        } else {
            writeByte(0x00);
        }
    }

    /**
     * Reads an AES Key from the buffer.
     * @return The key read.
     *
     * @see #writeAESKey(byte[]) to write the key to the buffer.
     */
    public final byte[] readAESKey() {
        if (readByte() != 0x00) {
            byte[] key = new byte[64];
            readBytes(key);
            return key;
        } else {
            return null;
        }
    }

    /**
     * Writes the shared modulus to the buffer.
     * @param modulus The modulus to write.
     *
     * @see #readModulus() to read the modulus back.
     */
    public void writeModulus(final BigInteger modulus) {
        if (modulus != null) {
            writeString(modulus.toString());
        } else {
            writeString(null);
        }
    }

    /**
     * Reads the shared modulus from the buffer.
     * @return The modulus read.
     *
     * @see #writeModulus(BigInteger) to write the modulus to the buffer.
     */
    public final BigInteger readModulus() {
        String modulus = readString();
        if (!modulus.isEmpty()) {
            return new BigInteger(modulus);
        } else {
            return null;
        }
    }

    /**
     * Writes a timestamp to the buffer.
     * @param timestamp The timestamp to write.
     *
     * @see #readTimestamp() to read the timestamp back.
     */
    public void writeTimestamp(final LocalDateTime timestamp) {
        writeString(timestamp.format(
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        ));
    }

    /**
     * Reads a timestamp from the buffer.
     * @return The timestamp read.
     *
     * @see #writeTimestamp(LocalDateTime) to write the timestamp to the buffer.
     */
    public final LocalDateTime readTimestamp() {
        return LocalDateTime.parse(readString(),
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );
    }

    /**
     * Writes a user to the buffer.
     * @param user The user to write.
     */
    public void writeUser(final UserAccount user) {
        if (user != null) {
            writeVarInt(user.getId());
            writeRSAKey(user.getKeychain().getPublicKey());
            writeRSAKey(user.getKeychain().getPrivateKey());
            writeRSAKey(user.getKeychain().getModulus());
            writePlainBytes(user.getKeychain().getSecret());
            writeString(user.getUsername());
            writeString(user.getEmail());
            writeString(user.getPassword());
            writeTimestamp(user.getSince());
        } else {
            writeVarInt(-1);
        }
    }

    /**
     * Reads a user from the buffer.
     * @return The user read.
     */
    public final UserAccount readUser() {
        int id = readVarInt();
        if (id != -1) {
            final HashMap<String, BigInteger> keys = new HashMap<>();
            keys.put("public", readRSAKey());
            keys.put("private", readRSAKey());
            keys.put("modulus", readRSAKey());

            Keychain keychain= new Keychain(keys);
            keychain.setSecret(readBytes());

            String s1= readString();
            String s2= readString();
            String s3= readString();

            return new UserAccount(
                id, s1, s2, s3, readTimestamp(), keychain
            );
        }  else {
            return null;
        }
    }
}
