package tn.famytech.esprit.utils;

import java.security.*;

import org.bouncycastle.util.encoders.Base64;




public class DigitalSignature {
	
	private KeyPairGenerator KeyGenerator;
	private KeyPair keyPair;
	private PublicKey publicKey;
	private Signature signature;
	
	public DigitalSignature() throws NoSuchAlgorithmException {
		this.KeyGenerator=KeyPairGenerator.getInstance("RSA");
		this.KeyGenerator.initialize(2048);
		this.keyPair=this.KeyGenerator.generateKeyPair();
		this.signature=Signature.getInstance("SHA256withRSA");
		
	}
	
	public PrivateKey getPrivateKey() {
		return this.keyPair.getPrivate();
	}
	
	public PublicKey getPublicKey() {
		return this.keyPair.getPublic();
	}
	
	public byte[] generateSignature(String message) throws InvalidKeyException, SignatureException {
		signature.initSign(getPrivateKey());
		byte[] bytes=message.getBytes();
		signature.update(bytes);
		byte[] finalSignature = signature.sign();
		return finalSignature;
	}
	
	public String convertSignature(byte[] signature) {
		return new String(Base64.encode(signature));
	}
	
	public String convertPublicKey(PublicKey key) {
		return new String(Base64.encode(key.getEncoded()));
	}

}
