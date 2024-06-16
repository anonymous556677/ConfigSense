/*
 * Decompiled with CFR.
 */
package main.java;

import java.util.Arrays;
import java.util.Random;
import main.java.Helper;
import main.java.components.gamma.GammaInterface;
import main.java.components.graph.GraphInterface;
import main.java.components.graph.algorithms.IdxInterface;
import main.java.components.hash.HashInterface;
import main.java.components.phi.PhiInterface;

public class Catena {
    private Helper helper = new Helper();
    private String _vId;
    private HashInterface _h;
    private HashInterface _hPrime;
    private GammaInterface _gamma;
    private GraphInterface _f;
    private PhiInterface _phi;
    private int _d = 0;
    private int _n;
    private int _k;
    private int _gLow;
    private int _gHigh;
    private int _lambda;

    /**
	 * Main function of catena to hash a password
	 * 
	 * @param pwd			Password to be hashed
	 * @param salt			Salt of arbitrary length
	 * @param publicInput	User choosen public input
	 * @param gamma			Input for graph size
	 * @param m				User desired output length of hash
	 * 
	 * @return xTrun		Hash of pwd
	 */
    public byte[] catena(byte[] pwd, byte[] salt, byte[] publicInput, byte[] gamma, int m) {
        byte[] t = this.compTweak(this.get_vId(), this.get_d(), this.get_lambda(), m, salt.length, publicInput);
        this.get_h().update(this.helper.concateByteArrays(t, pwd, salt));
        byte[] x = this.get_h().doFinal();
        this.erasePwd(pwd);
        x = this.flap((this.get_gLow() + 1) / 2, x, gamma);
        this.get_h().update(x);
        x = this.get_h().doFinal();
        byte[] gByte = new byte[1];
        int g = this.get_gLow();
        while (g <= this.get_gHigh()) {
            if (x.length < this.get_n()) {
                x = this.helper.paddWithZero(x, this.get_n());
            }
            x = this.flap(g, x, gamma);
            gByte[0] = (byte)g;
            this.get_h().update(this.helper.concateByteArrays(gByte, x));
            x = this.get_h().doFinal();
            x = this.helper.truncate(x, m);
            ++g;
        }
        return x;
    }
    /**
	 * flap function from catena specification
	 * 
	 * @param g
	 * @param xIn
	 * @param gamma
	 * @return
	 */
    private byte[] flap(int g, byte[] xIn, byte[] gamma) {
        this.get_hPrime().reset();
        int iterations = (int)Math.pow(2.0, g);
        byte[][] v = new byte[iterations + 2][this.get_k()];
        byte[] xHinit = this.hInit(xIn);
        System.arraycopy(xHinit, 0, v[0], 0, this.get_k());
        System.arraycopy(xHinit, this.get_k(), v[1], 0, this.get_k());
        int i = 2;
        while (i < iterations + 2) {
            this.get_hPrime().update(this.helper.concateByteArrays(v[i - 1], v[i - 2]));
            v[i] = this.get_hPrime().doFinal();
            ++i;
        }
        byte[][] v2 = new byte[iterations][this.get_k()];
        System.arraycopy(v, 2, v2, 0, v2.length);
        this.get_hPrime().reset();
        v2 = this.gamma(g, v2, gamma);
        this.get_hPrime().reset();
        v2 = this.f(g, v2, this.get_lambda());
        this.get_hPrime().reset();
        v2 = this.phi(g, v2, v2[v2.length - 1]);
        return v2[v2.length - 1];
    }

    public byte[] flapPub(int g, byte[] xIn, byte[] gamma) {
        return this.flap(g, xIn, gamma);
    }

	/**
	 * Initialisation of the 2 values for flap rounds
	 * 
	 * @param x		Input Array
	 * @return 		2 hashed values v_-1, V_-2 in one byte array
	 * 				(output is already splitted in the middle and swapped)
	 */
    private byte[] hInit(byte[] x) {
        int l = 2 * this.get_k() / this.get_n();
        byte[][] xLoop = new byte[l][this.get_n()];
        byte[] iByte = new byte[1];
        int i = 0;
        while (i <= l - 1) {
            iByte[0] = (byte)i;
            this.get_h().update(this.helper.concateByteArrays(iByte, x));
            xLoop[i] = this.get_h().doFinal();
            this.get_h().reset();
            ++i;
        }
        return this.helper.twoDimByteArrayToOne(xLoop);
    }

	/**
	 * No clue how to test private functions so this wrapper exists
	 * 
	 * @param x		Initial value to instantiate v-2 and v-1
	 * @return		v-2 and v-1 combined in one array
	 */
    public byte[] testHInit(byte[] x) {
        return this.hInit(x);
    }

	/**
	 * salt dependent update with random access
	 * 
	 * @param g		garlic
	 * @param x		hash array
	 * @param gamma	gamma
	 * @return		hash array
	 */
    private byte[][] gamma(int g, byte[][] x, byte[] gamma) {
        return this.get_gamma().gamma(g, x, gamma);
    }

	/**
	 * phi function from catena specification
	 * 
	 * @param x		hash input
	 * @return		hash output
	 */
    private byte[][] f(int g, byte[][] x, int lambda) {
        return this.get_f().graph(g, x, lambda);
    }

    private byte[][] phi(int garlic, byte[][] x, byte[] m) {
        return this.get_phi().phi(garlic, x, m);
    }

	/**
	 * Combine Tweak Array
	 * 
	 * @param vId		Version ID
	 * @param mode		Mode of catena
	 * @param lambda	Lambda
	 * @param outLen	Output Length
	 * @param sLen		Salt Length
	 * @param aData		Additional Data
	 * @return			Combined Tweak
	 */
    private byte[] compTweak(String vId, int mode, int lambda, int outLen, int sLen, byte[] aData) {
        byte[] modeByte = new byte[1];
        byte[] lambdaByte = new byte[1];
        byte[] outLenByte = this.helper.intToByteArrayLittleEndian(outLen, 2);
        byte[] sLenByte = this.helper.intToByteArrayLittleEndian(sLen, 2);
        this.get_h().update(this.helper.string2Bytes(vId));
        byte[] vIdH = this.get_h().doFinal();
        this.get_h().reset();
        this.get_h().update(aData);
        byte[] aDataH = this.get_h().doFinal();
        this.get_h().reset();
        modeByte[0] = (byte)mode;
        lambdaByte[0] = (byte)lambda;
        return this.helper.concateByteArrays(vIdH, modeByte, lambdaByte, outLenByte, sLenByte, aDataH);
    }
	/**
	 * public interface for testing tweak computation
	 * 
	 * @param vId		String, VersionID
	 * @param mode		Integer, Mode of Catena
	 * @param lambda	Integer, The depth of the graph structure.
	 * @param outLen	Integer, Output length.
	 * @param sLen		Integer, Salt length.
	 * @param aData		byte[], Associated data of the user and/or the host.
	 * @return tweak	byte[], The calculatetd tweak.
	 */
    public byte[] testCompTweak(String vId, int mode, int lambda, int outLen, int sLen, byte[] aData) {
        return this.compTweak(vId, mode, lambda, outLen, sLen, aData);
    }

    /**
	 * Clear the password
	 * 
	 * @param pwd	the password to be cleared
	 */
    private final void erasePwd(byte[] pwd) {
        Arrays.fill(pwd, (byte)0);
    }

    /**
	 * Initializes Catena
	 * 
	 * initializrs all needed variables and functions with default values
	 * 
	 * @param h			main hash function
	 * @param hPrime	reduced hash function
	 * @param gamma		gamma function (e.g. SaltMix)
	 * @param f			graph
	 * @param idx		index function for graph
	 * @param phi		phi function
	 * @param gLow		minimum Garlic
	 * @param gHigh		maximum Garlic
	 * @param lambda	depth of graphs
	 * @param vID		version ID
	 */
    public void init(HashInterface h, HashInterface hPrime, GammaInterface gamma, GraphInterface f, IdxInterface idx, PhiInterface phi, int gLow, int gHigh, int lambda, String vID) {
        this._h = h;
        this._hPrime = hPrime;
        this._gamma = gamma;
        this._gamma.setH(this.get_h());
        this._gamma.setHPrime(this.get_hPrime());
        this._f = f;
        this._f.setH(this.get_h());
        this._f.setHPrime(this.get_hPrime());
        this._f.setIndexing(idx);
        this._phi = phi;
        this._phi.setH(this.get_h());
        this._phi.setHPrime(this.get_hPrime());
        this._gLow = gLow;
        this._gHigh = gHigh;
        this._lambda = lambda;
        this._n = this.get_h().getOutputSize();
        this._k = this.get_hPrime().getOutputSize();
        this._vId = vID;
    }

    public void setGHigh(int gHigh) {
        this._gHigh = gHigh;
    }

    public void setGLow(int gLow) {
        this._gLow = gLow;
    }

    public void setD(int d) {
        this._d = d;
    }

    public byte[] keyedClientIndependentUpdate(byte[] hashOld, int gHighOld, int gHighNew, byte[] gamma, int outputLenth, byte[] serverKey, byte[] userID) throws Exception {
        if (gHighOld >= gHighNew) {
            throw new Exception("New gHigh value should be bigger as the old one.");
        }
        byte[] keystream = this.computeKeyStream(serverKey, userID, gHighOld, outputLenth);
        byte[] oldHash = this.helper.xor(hashOld, keystream);
        byte[] newHash = this.clientIndependentUpdate(oldHash, gHighOld, gHighNew, gamma, outputLenth);
        byte[] newKeystream = this.computeKeyStream(serverKey, userID, gHighNew, outputLenth);
        return this.helper.xor(newHash, newKeystream);
    }

    private byte[] computeKeyStream(byte[] serverKey, byte[] userID, int gHigh, int outLen) {
        byte[] gByte = new byte[]{(byte)gHigh};
        this.get_h().update(this.helper.concateByteArrays(serverKey, userID, gByte, serverKey));
        byte[] output = this.get_h().doFinal();
        output = this.helper.truncate(output, outLen);
        return output;
    }

    public byte[] clientIndependentUpdate(byte[] hashOld, int gHighOld, int gHighNew, byte[] gamma, int outputLenth) throws Exception {
        if (gHighOld >= gHighNew) {
            throw new Exception("New gHigh value should be bigger as the old one.");
        }
        int n = this.get_h().getOutputSize();
        byte[] newHash = new byte[n];
        byte[] gByte = new byte[1];
        System.arraycopy(hashOld, 0, newHash, 0, hashOld.length);
        int i = gHighOld + 1;
        while (i < gHighNew + 1) {
            if (newHash.length < this.get_n()) {
                newHash = this.helper.paddWithZero(newHash, n);
            }
            newHash = this.flap(i, newHash, gamma);
            gByte[0] = (byte)i;
            this.get_h().update(this.helper.concateByteArrays(gByte, newHash));
            newHash = this.get_h().doFinal();
            this.get_h().reset();
            newHash = this.helper.truncate(newHash, outputLenth);
            ++i;
        }
        return newHash;
    }

    public byte[] keyedPasswordHashing(byte[] pwd, byte[] key, byte[] salt, byte[] gamma, byte[] a_data, int out_len, byte[] userID) {
        byte[] gHighBytes = new byte[]{(byte)this.get_gHigh()};
        this.get_h().update(this.helper.concateByteArrays(key, userID, gHighBytes, key));
        byte[] z = this.helper.truncate(this.get_h().doFinal(), out_len);
        byte[] hash = this.catena(pwd, salt, a_data, gamma, out_len);
        return this.helper.xor(z, hash);
    }

    public byte[] keyDerivation(byte[] pwd, byte[] salt, byte[] publicInput, byte[] gamma, int outLen, int keySize, byte[] keyIdentifier) {
        int d = 1;
        byte[] tweak = this.compTweak(this.get_vId(), d, this.get_lambda(), outLen, salt.length, publicInput);
        this.get_h().update(this.helper.concateByteArrays(tweak, pwd, salt));
        byte[] x = this.get_h().doFinal();
        x = this.flap((this.get_gLow() + 1) / 2, x, gamma);
        this.erasePwd(pwd);
        this.get_h().update(x);
        x = this.get_h().doFinal();
        byte[] gByte = new byte[1];
        int g = this.get_gLow();
        while (g <= this.get_gHigh()) {
            if (x.length < this.get_n()) {
                x = this.helper.paddWithZero(x, this.get_n());
            }
            x = this.flap(g, x, gamma);
            gByte[0] = (byte)g;
            this.get_h().update(this.helper.concateByteArrays(gByte, x));
            x = this.get_h().doFinal();
            x = this.helper.truncate(x, outLen);
            ++g;
        }
        int limit = (int)Math.ceil((double)keySize / (double)this.get_h().getOutputSize());
        byte[] outputKey = new byte[]{};
        int i = 1;
        while (i < limit + 1) {
            byte[] iByte = this.helper.intToByteArrayLittleEndian(i, 2);
            byte[] keySizeByte = this.helper.intToByteArrayLittleEndian(keySize, 2);
            this.get_h().update(this.helper.concateByteArrays(iByte, keyIdentifier, keySizeByte, x));
            byte[] tmp = this.get_h().doFinal();
            outputKey = this.helper.concateByteArrays(outputKey, tmp);
            ++i;
        }
        return this.helper.truncate(outputKey, keySize);
    }

    public byte[] serverReliefClient(byte[] pwd, byte[] salt, byte[] aData, int outLen, byte[] gamma) {
        int d = 0;
        byte[] t = this.compTweak(this.get_vId(), d, this.get_lambda(), outLen, salt.length, aData);
        this.get_h().update(this.helper.concateByteArrays(t, pwd, salt));
        byte[] x = this.get_h().doFinal();
        x = this.flap((this.get_gLow() + 1) / 2, x, gamma);
        this.erasePwd(pwd);
        this.get_h().update(x);
        x = this.get_h().doFinal();
        if (this.get_gHigh() > this.get_gLow()) {
            byte[] gByte = new byte[1];
            int g = this.get_gLow();
            while (g < this.get_gHigh()) {
                if (x.length < this.get_n()) {
                    x = this.helper.paddWithZero(x, outLen);
                }
                x = this.flap(g, x, gamma);
                gByte[0] = (byte)g;
                this.get_h().update(this.helper.concateByteArrays(gByte, x));
                x = this.get_h().doFinal();
                x = this.helper.truncate(x, outLen);
                ++g;
            }
        }
        if (x.length < this.get_n()) {
            x = this.helper.paddWithZero(x, this.get_n());
        }
        x = this.flap(this.get_gHigh(), x, gamma);
        return x;
    }

    public byte[] serverReliefServer(int outLen, byte[] lastHash) {
        byte[] gByte = new byte[]{(byte)this.get_gHigh()};
        this.get_h().update(this.helper.concateByteArrays(gByte, lastHash));
        return this.helper.truncate(this.get_h().doFinal(), outLen);
    }

    public POWstruct proofOfWorkServer(byte[] pwd, byte[] salt, byte[] aData, int outLen, byte[] gamma, int p, int mode) {
        this.setD(2);
        POWstruct output = new POWstruct();
        byte[] inpPWD = new byte[pwd.length];
        System.arraycopy(pwd, 0, inpPWD, 0, pwd.length);
        output.pwd = inpPWD;
        output.salt = salt;
        output.aData = aData;
        output.gLow = this.get_gLow();
        output.gHigh = this.get_gHigh();
        output.outLen = outLen;
        output.gamma = gamma;
        output.p = p;
        output.rHash = this.catena(pwd, salt, aData, gamma, outLen);
        output.mode = mode;
        if (mode == 0) {
            byte[] inpSalt = new byte[salt.length];
            System.arraycopy(salt, 0, inpSalt, 0, salt.length);
            int a = (1 << 8 * (int)Math.ceil((double)p / 8.0)) - (1 << p);
            byte[] mask = this.helper.intToBytes(a);
            int saltLength = inpSalt.length;
            int maskLength = mask.length;
            byte[] newMask = new byte[saltLength];
            if (maskLength > saltLength) {
                System.arraycopy(mask, maskLength - saltLength, newMask, 0, saltLength);
            } else {
                System.arraycopy(mask, 0, newMask, 0, maskLength);
            }
            int newMaskLength = newMask.length;
            int i = 0;
            while (i < newMaskLength) {
                if (newMask[i] != 0) break;
                newMask[i] = -1;
                ++i;
            }
            i = 0;
            while (i < newMaskLength) {
                inpSalt[saltLength - newMaskLength + i] = (byte)(inpSalt[saltLength - newMaskLength + i] & newMask[i]);
                ++i;
            }
            output.salt = inpSalt;
            return output;
        }
        if (mode == 1) {
            output.pwd = new byte[0];
            return output;
        }
        return new POWstruct();
    }

    public byte[] proofOfWorkClient(POWstruct input) {
        this.setD(2);
        if (input.mode == 0) {
            int numBytes = (int)Math.ceil((double)input.p / 8.0);
            int upperBound = 1 << input.p;
            Random rand = new Random();
            int randomOffset = rand.nextInt(upperBound);
            int i = 0;
            while (i < upperBound) {
                byte[] pwd = new byte[input.pwd.length];
                System.arraycopy(input.pwd, 0, pwd, 0, input.pwd.length);
                byte[] saltInput = new byte[input.salt.length];
                System.arraycopy(input.salt, 0, saltInput, 0, input.salt.length);
                byte[] pepperPre = this.helper.intToBytes((i + randomOffset) % upperBound);
                byte[] pepper = new byte[numBytes];
                System.arraycopy(pepperPre, pepperPre.length - numBytes, pepper, 0, numBytes);
                if (saltInput.length == pepper.length) {
                    saltInput = pepper;
                } else {
                    int j = 1;
                    while (j == numBytes) {
                        saltInput[saltInput.length - j] = (byte)(saltInput[saltInput.length - j] + pepper[pepper.length - j]);
                        ++j;
                    }
                }
                byte[] actualHash = this.catena(pwd, saltInput, input.aData, input.gamma, input.outLen);
                if (this.helper.bytes2hex(actualHash).equals(this.helper.bytes2hex(input.rHash))) {
                    return saltInput;
                }
                ++i;
            }
            return new byte[0];
        }
        if (input.mode == 1) {
            int numBytes = (int)Math.ceil((double)input.p / 8.0);
            int upperBound = 1 << input.p;
            Random rand = new Random();
            int randomOffset = rand.nextInt(upperBound);
            int i = 0;
            while (i < upperBound) {
                byte[] pepperPwd = this.helper.intToBytes((i + randomOffset) % upperBound);
                byte[] sectretPwd = new byte[numBytes];
                System.arraycopy(pepperPwd, pepperPwd.length - numBytes, sectretPwd, 0, numBytes);
                byte[] saveForReturn = new byte[sectretPwd.length];
                System.arraycopy(sectretPwd, 0, saveForReturn, 0, sectretPwd.length);
                byte[] actual = this.catena(sectretPwd, input.salt, input.aData, input.gamma, input.outLen);
                if (this.helper.bytes2hex(actual).equals(this.helper.bytes2hex(input.rHash))) {
                    return saveForReturn;
                }
                ++i;
            }
            return new byte[0];
        }
        return new byte[0];
    }

    public String get_vId() {
        return this._vId;
    }

    public HashInterface get_h() {
        return this._h;
    }

    public HashInterface get_hPrime() {
        return this._hPrime;
    }

    public GammaInterface get_gamma() {
        return this._gamma;
    }

    public GraphInterface get_f() {
        return this._f;
    }

    public PhiInterface get_phi() {
        return this._phi;
    }

    public int get_d() {
        return this._d;
    }

    public int get_n() {
        return this._n;
    }

    public int get_k() {
        return this._k;
    }

    public int get_gLow() {
        return this._gLow;
    }

    public int get_gHigh() {
        return this._gHigh;
    }

    public int get_lambda() {
        return this._lambda;
    }

    public class POWstruct {
        public byte[] pwd;
        public byte[] salt;
        public byte[] aData;
        public int gLow;
        public int gHigh;
        public int outLen;
        public byte[] gamma;
        public int p;
        public byte[] rHash;
        public int mode;
    }
}
