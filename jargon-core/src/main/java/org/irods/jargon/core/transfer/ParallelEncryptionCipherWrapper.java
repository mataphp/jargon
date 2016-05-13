/**
 * 
 */
package org.irods.jargon.core.transfer;

import javax.crypto.Cipher;

import org.irods.jargon.core.connection.NegotiatedClientServerConfiguration;
import org.irods.jargon.core.connection.PipelineConfiguration;

/**
 * Wrapper for a cipher that will encrypt parallel transfer data
 * 
 * @author Mike Conway - DICE
 *
 */
public abstract class ParallelEncryptionCipherWrapper extends
		ParallelCipherWrapper {

	ParallelEncryptionCipherWrapper(
			PipelineConfiguration pipelineConfiguration,
			NegotiatedClientServerConfiguration negotiatedClientServerConfiguration) {
		super(pipelineConfiguration, negotiatedClientServerConfiguration,
				Cipher.ENCRYPT_MODE);
	}

	/**
	 * Encrypt the given data
	 * 
	 * @param input
	 *            <code>byte[]</code> of plaintext data
	 * @return {@link EncryptionBuffer}
	 */
	abstract EncryptionBuffer encrypt(byte[] input);

}
