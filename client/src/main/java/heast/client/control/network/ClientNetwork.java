package heast.client.control.network;

import heast.client.view.WelcomeView;
import heast.core.security.Keychain;
import heast.core.security.RSA;
import io.netty.channel.Channel;
import heast.core.network.ClientConnection;
import heast.core.network.c2s.*;
import heast.core.utility.Validator;
import javafx.application.Platform;

import java.math.BigInteger;

public final class ClientNetwork {

    public static final ClientNetwork INSTANCE = new ClientNetwork();


    public Keychain keychain;           //needs to be pulled from Database
    public ClientConnection connection;         //connection to the auth-server
    public ClientConnection chatConnection;     //connection to the chat-server
    public byte[] passwordCypher;              //password as byte[]

    public BigInteger serverPublicKey;//auth-server rn
    public BigInteger serverModulus;//auth-server rn

    public void initialize() {

        System.out.println("Initializing client network...");
    }

    public void shutdown() {
        System.out.println("Shutting down client network...");
        if (this.connection != null) {
            connection.send(
                new LogoutC2SPacket(LogoutC2SPacket.Reason.CLIENT_QUIT)
            );
            Channel channel = connection.getChannel();
            if (channel != null) {
                channel.close();
            }
        }
        if(this.chatConnection!=null){
            Channel channel= chatConnection.getChannel();
            if (channel != null) {
                channel.close();
            }
        }
    }

    public void signup(String uname, String email, String password) {
        System.out.println("Signing up...");
        String username = uname.replace(" ", "_").trim();
        String address = email.trim();

        if (!Validator.isUsernameValid(username)) {
            System.err.println("Invalid username");
            return;
        }

        if (!Validator.isEmailValid(address)) {
            System.err.println("Invalid email");
            return;
        }

        if (!Validator.isPasswordValid(password)) {
            System.err.println("Invalid password");
            return;
        }
        this.passwordCypher=password.getBytes();

        Thread genKeys= new Thread(()->{
            keychain= RSA.INSTANCE.genKeyPair();
            Platform.runLater(()->{
                WelcomeView.LoadingPane.INSTANCE.changeLabel("keychain generated");
            });
            System.out.println("Keychain generated");
            connection.send(
                    new SignupC2SPacket(
                            username, address, password, keychain, serverPublicKey, serverModulus
                    )
            );
        });

        WelcomeView.LoadingPane.INSTANCE.changeLabel("generating keychain");
        genKeys.start();
    }

    public void deleteAccount(String email){
        if (!Validator.isEmailValid(email.trim())) {
            System.err.println("Invalid email");
            return;
        }
        connection.send(
                new DeleteAcC2SPacket(
                        email, serverPublicKey, serverModulus
                )
        );
    }

    public void login(String email, String password) {
        System.out.println("Logging in...");
        String address = email.trim();

        if (!Validator.isEmailValid(address)) {
            System.err.println("Invalid email");
            return;
        }

        if (!Validator.isPasswordValid(password)) {
            System.err.println("Invalid password");
            return;
        }

        this.passwordCypher=password.getBytes();
        connection.send(
            new LoginC2SPacket(
                address, password, serverPublicKey, serverModulus
            )
        );
    }

    public void reset(String email, String newPassword) {
        System.out.println("Resetting Account...");
        String address = email.trim();

        if (!Validator.isEmailValid(address)) {
            System.err.println("Invalid email");
            return;
        }

        if (!Validator.isPasswordValid(newPassword)) {
            System.err.println("Invalid password");
            return;
        }

        this.passwordCypher=newPassword.getBytes();
        connection.send(
            new ResetC2SPacket(address, newPassword, serverPublicKey, serverModulus)
        );
    }

    public void logout() {
        System.out.println("Logging out...");
        connection.send(new LogoutC2SPacket(LogoutC2SPacket.Reason.LOGOUT));
    }

    public void verify(String verificationCode) {
        if (!Validator.isVerificationCodeValid(verificationCode)) {
            System.err.println("Invalid verification code");
            return;
        }

        System.out.println("Verifying Account with code: " + verificationCode + "...");
        connection.send(
            new VerificationC2SPacket(verificationCode, serverPublicKey, serverModulus)
        );
    }

    public void tryAddServer(String host, int port) {
        if (!Validator.isIpAddressValid(host) || !Validator.isPortValid(port)) {
            System.err.println("Invalid server address");
            return;
        }
    }

    public void testConnection(String host, int port) {
        if (!Validator.isIpAddressValid(host) || !Validator.isPortValid(port)) {
            System.err.println("Invalid server address");
            return;
        }
    }
}
