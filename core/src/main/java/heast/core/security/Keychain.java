package heast.core.security;

import java.math.BigInteger;
import java.util.Map;

public final class Keychain {

    private final Map<String, BigInteger> keys;
    private byte[] secret;  //encrypted private key

    public Keychain(Map<String, BigInteger> keys) {
        this.keys = keys;
        this.secret=null;
    }

    public BigInteger getPrivateKey() {
        return keys.get("private");
    }

    public BigInteger getPublicKey() {
        return keys.get("public");
    }

    public BigInteger getModulus() {
        return keys.get("modulus");
    }

    public void setPrivateKey(BigInteger key){
        this.keys.put("private",key);
    }

    public byte[] getSecret() {
        return secret;
    }

    public void setSecret(byte[] secret){
        this.secret=secret;
    }

    @Override
    public String toString() {
        return "public=" + getPublicKey() + ", private=" + getPrivateKey() + ", modulus=" + getModulus() + ", secret=" + new String(getSecret());
    }
}
