package heast.core.security;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

/**
 * p = Prime 1
 * q = Prime 2
 * N = Part of both Keys
 *
 * phi = Euler phi-function
 * e = Part of public key
 * d = Part of private key
 */
public final class RSA {

    public static final RSA INSTANCE = new RSA();

    /**
     * Generate a new RSA keypair. The keypair consists of a private and public key + a shared modulus N.
     * @return A new RSA keypair.
     */
    public Keychain genKeyPair() {
        SecureRandom r= new SecureRandom();
        int bitLength = 4096;
        BigInteger p= BigInteger.probablePrime(bitLength,r);
        BigInteger q= BigInteger.probablePrime(bitLength,r);

        BigInteger N= p.multiply(q);
        BigInteger phi= p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e= BigInteger.probablePrime(bitLength /2,r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e= e.add(BigInteger.ONE);
        }
        BigInteger d= e.modInverse(phi);

        final HashMap<String, BigInteger> keys = new HashMap<>();
        keys.put("private", d);
        keys.put("public", e);
        keys.put("modulus", N);

        return new Keychain(
            keys
        );
    }

    /**
     * Encrypts the given plaintext with the public key. (max length=<1024 bytes>)
     * @param text The plaintext to encrypt.
     * @param key The public key.
     * @param N The modulus.
     * @return The ciphertext.
     */
    private byte[] encrypt(byte[] text, BigInteger key, BigInteger N) {
        return new BigInteger(text)
            .modPow(key,N)
            .toByteArray();
    }

    /**
     * Decrypts the given ciphertext with the private key. (max length=<1024 bytes>)
     * @param cipher The ciphertext to decrypt.
     * @param key The private key.
     * @param N The modulus.
     * @return The plaintext.
     */
    private byte[] decrypt(byte[] cipher, BigInteger key, BigInteger N) {
        return new BigInteger(cipher)
            .modPow(key,N)
            .toByteArray();
    }

    /**
     * Decrypts the large given plaintext with the private key.
     * The array(ciphertext) is strictly differentiated between info and data.
     * data= encrypted data.
     * info= information for decryption.
     * @param str The ciphertext.
     * @param e The public key.
     * @param n The modulus.
     * @return The ciphertext
     */
    public byte[] encryptLargeBytes(byte[] str, BigInteger e, BigInteger n){
        int numberOfParts=0;
        byte[] data=null;   //contains the data which is encrypted in several parts
        byte[] info=null;   //contains information that is needed for later decryption (numberOfParts, lengthOfPartOne, lengthOfPartTwo, ...)

        byte[] temp=new byte[1024];
        int j=0;
        for(int i=0;i<str.length;i++,j++){  //iterate through the array
            temp[j]=str[i];
            if(j==1023){  //stop every 1024 steps
                numberOfParts++;
                byte[] encrypted = RSA.INSTANCE.encrypt(temp,e,n);
                data = combine(data, encrypted);   //combine data with new encrypted data
                byte[] z=new byte[varIntSize(encrypted.length)];
                putVarInt(encrypted.length,z);
                info = combine(info, z);  //insert length of encrypted part into info

                j=0; temp=new byte[1024];   //reset the temporary array
            }
        }
        if(!isEmpty(temp)){
            numberOfParts++;
            byte[] encrypted= RSA.INSTANCE.encrypt(temp,e,n);
            data=combine(data,encrypted);  //combine data with remaining encrypted data
            byte[] z=new byte[varIntSize(encrypted.length)];
            putVarInt(encrypted.length,z);
            info = combine(info, z);  //insert length of encrypted part into info
        }

        byte[] z=new byte[varIntSize(numberOfParts)];
        putVarInt(numberOfParts,z);
        info= combine(z,info);   //insert number of parts at index 0 of the info array

        return combine(info,data);   //get info in front of data
    }

    /**
     * Decrypts a large given ciphertext with the private key.
     * The number of parts
     * @param cipher The ciphertext to decrypt.
     * @param key The private key.
     * @param N The modulus.
     * @return The plaintext.
     */
    public byte[] decryptLargeBytes(byte[] cipher, BigInteger key, BigInteger N){

        //Structure: numberOfParts(varInt), lengthOfPartOne(varInt), ..., lengthOfPartN(varInt)

        byte[] data=null;
        int[] info=null;

        int[] k= new int[1];
        cipher= getVarInt(cipher,k);
        int numberOfParts= k[0];  //number of Parts

        for(int i=0;i<numberOfParts;i++){
            int[] x= new int[1];
            cipher= getVarInt(cipher,x);
            info= combine(info, x); //feed the part-lengths into the info array
        }

        //all additional information should now be deleted from the cipher array
        //now the data can be decrypted
        if(info==null)
            return null;

        byte[] temp=new byte[info[0]];
        info= popFirst(info);

        for(int i=0,j=0;i<cipher.length;i++,j++){
            if(j==temp.length){
                temp = RSA.INSTANCE.decrypt(temp,key,N);
                data= combine(data,temp);

                if(info.length==0) {
                    break;
                }
                j=0;temp= new byte[info[0]];
                info=popFirst(info);
            }
                temp[j]=cipher[i];
        }

        data= trim(data);   //cut off unused bytes
        return data;
    }

    /**
     * Puts a variable length integer at the start of a byte[].
     * @param v The value to make variable-length.
     * @param sink The byte[] the value is written to.
     */
    public void putVarInt(int v, byte[] sink) {
        int offset= 0;
        do {
            // Encode next 7 bits + terminator bit
            int bits = v & 0x7F;
            v >>>= 7;
            byte b = (byte) (bits + ((v != 0) ? 0x80 : 0));
            sink[offset++] = b;
        } while (v != 0);
    }

    /**
     * Gets a variable length integer from the start of a byte[] and deletes it from there.
     * @param src The original byte[] with the varInt.
     * @param dst The byte[] the varInt is written to.
     * @return The original byte[] without the varInt.
     */
    public byte[] getVarInt(byte[] src, int[] dst) {
        int result = 0;
        int shift = 0;
        int offset= 0;
        int b;
        do {
            if (shift >= 32) {
                // Out of range
                throw new IndexOutOfBoundsException("varInt too long");
            }
            // Get 7 bits from next byte
            b = src[offset++];
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        dst[0] = result;
        src= popFirst(src,varIntSize(result));   //pops the varInt off
        return src;
    }

    /**
     * Trims unused length of a byte[].
     * @param src The array to be trimmed.
     * @return The array without unnecessary bytes.
     */
    public byte[] trim(byte[] src){
        if(src==null)
            return null;
        for(int i=0;i<src.length;i++){
            if(src[i]==0){
                byte[] temp=new byte[i];
                System.arraycopy(src, 0, temp, 0, i);
                return temp;
            }
        }
        return null;
    }

    /**
     * Predicts the length of a varInt.
     * @param i The integer to predict the var-length of.
     * @return The predicted length of the varInt.
     */
    public static int varIntSize(int i) {
        int result = 0;
        do {
            result++;
            i >>>= 7;
        } while (i != 0);
        return result;
    }

    /**
     * Pops off the first byte of an array.
     * @param x The array to pop.
     */
    private byte[] popFirst(byte[] x, int len){
        byte[] shifted=new byte[x.length-1];  //
        for(int i=len,j=0;i<x.length;i++,j++){   //
            shifted[j]=x[i];                   // pop the first byte off the array
        }                                      //
        return shifted;                            //
    }
    private int[] popFirst(int[] x){
        int[] shifted=new int[x.length-1];  //
        for(int i=1,j=0;i<x.length;i++,j++){ //
            shifted[j]=x[i];                 // pop the first byte off the array
        }                                    //
        return shifted;                           //
    }
    /**
     * Utility function to check if a byte-array is empty or not.
     * @see #encryptLargeBytes(byte[], BigInteger, BigInteger) 
     */
    private boolean isEmpty(byte[] array) {
        return Arrays.equals(array, new byte[array.length]);
    }
    /**
     * Utility function to combine 2 arrays.
     * @return array1 and array2 combined.
     * @see #encryptLargeBytes(byte[], BigInteger, BigInteger)
     */
    private byte[] combine(byte[] one, byte[] two){
        if(one==null)
            return two;
        if(two==null)
            return one;
        byte[] combined=new byte[one.length+two.length];
        ByteBuffer buff = ByteBuffer.wrap(combined);
        buff.put(one);
        buff.put(two);

        return buff.array();
    }
    private int[] combine(int[] one, int[] two){
        if(one==null)
            return two;
        if(two==null)
            return one;
        int[] combined=new int[one.length+two.length];
        IntBuffer buff = IntBuffer.wrap(combined);
        buff.put(one);
        buff.put(two);

        return buff.array();
    }
}
