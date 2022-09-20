package heast.core.network.c2s;

import heast.core.network.c2s.listener.ServerAuthListener;
import heast.core.network.Packet;
import heast.core.network.PacketBuf;
import heast.core.security.AES;
import heast.core.security.Keychain;
import heast.core.security.RSA;

import java.math.BigInteger;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A packet sent by the client to the server to signup on the platform.
 */
public final class SignupC2SPacket implements Packet<ServerAuthListener> {
    private byte[] username;
    private byte[] email;
    private byte[] password;
    private byte[] privateKeyClient;
    private final BigInteger publicKeyClient;
    private final BigInteger modulusClient;

    private final BigInteger publicKey;
    private final BigInteger modulus;

    public SignupC2SPacket(String username, String email, String password, Keychain keychain, BigInteger publicKey, BigInteger modulus) {
        this.username = username.getBytes();
        this.email = email.getBytes();
        this.password = password.getBytes();
        this.publicKeyClient= keychain.getPublicKey();
        this.modulusClient= keychain.getModulus();
        this.privateKeyClient= AES.INSTANCE.encrypt(keychain.getPrivateKey().toString().getBytes(),password.getBytes());

        this.publicKey = publicKey;
        this.modulus = modulus;
    }

    public SignupC2SPacket(PacketBuf buf) {
        this.username = buf.readBytes();
        this.email = buf.readBytes();
        this.password = buf.readBytes();


        this.privateKeyClient= buf.readBytes();
        this.publicKeyClient= buf.readRSAKey();
        this.modulusClient= buf.readRSAKey();

        // null, because the server knows its keys
        this.publicKey=null;
        this.modulus=null;
    }

    public void decrypt(BigInteger privateKey, BigInteger modulus){
        this.email= RSA.INSTANCE.decryptLargeBytes(this.email,privateKey,modulus);
        this.password= RSA.INSTANCE.decryptLargeBytes(this.password,privateKey,modulus);
        this.username= RSA.INSTANCE.decryptLargeBytes(this.username,privateKey,modulus);
        this.privateKeyClient= RSA.INSTANCE.decryptLargeBytes(this.privateKeyClient,privateKey,modulus);

    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeBytesEncryptRSA(username,publicKey,modulus);
        buf.writeBytesEncryptRSA(email,publicKey,modulus);
        buf.writeBytesEncryptRSA(password,publicKey,modulus);

        buf.writeBytesEncryptRSA(privateKeyClient, publicKey, modulus);

        buf.writeRSAKey(publicKeyClient);
        buf.writeRSAKey(modulusClient);
    }

    @Override
    public void apply(ServerAuthListener listener) {
        listener.onSignup(this);
    }

    public String getUsername() {
        return new String(this.username);
    }

    public String getEmail() {
        return new String(this.email);
    }

    public String getPassword() {
        return new String(this.password);
    }

    public BigInteger getPublicKey(){
        return this.publicKeyClient;
    }
    public byte[] getPrivateKey(){
        return this.privateKeyClient;
    }
    public BigInteger getModulus(){
        return this.modulusClient;
    }
}
