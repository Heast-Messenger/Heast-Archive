package heast.core.network;

import heast.core.security.Keychain;

import java.time.LocalDateTime;

public final class UserAccount {
    public final int id;
    public final String username;
    public final String email;
    public final String password;
    public final LocalDateTime since;
    public Keychain keychain;
    public final String avatar;

    public UserAccount(int id, String username, String email, String password, LocalDateTime since, Keychain keychain) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.since = since;
        this.keychain= keychain;
        this.avatar = "http://localhost:8000/avatars?id=" + id;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public Keychain getKeychain(){
        return this.keychain;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        return "UserAccount{" + "id=" + id + ", username='" + username + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", since=" + since + ", publicKey=" + keychain.getPublicKey() + ", privateKey=" + keychain.getPrivateKey() + ", modulus=" + keychain.getModulus() + ", secret=" + new String(keychain.getSecret()) + ", avatar='" + avatar + '\'' + '}';
    }
}