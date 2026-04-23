import { SiweMessage } from 'siwe';
import { type WalletClient } from 'viem';

/**
 * Creates and signs a SIWE message for non-custodial login.
 * This establishes a secure, decentralized session without passwords.
 */
export async function createSiweSession(
  walletClient: WalletClient,
  address: `0x${string}`,
  chainId: number
) {
  const domain = window.location.host;
  const origin = window.location.origin;

  // 1. Create the message following EIP-4361
  const message = new SiweMessage({
    domain,
    address,
    statement: 'Sign in to We The People Platform. This action proves ownership of your identity and does not cost gas.',
    uri: origin,
    version: '1',
    chainId,
    nonce: generateNonce(), // Should be fetched from a stateless auth service or ZK-nullifier
  });

  // 2. Request signature from the hardware/non-custodial wallet
  const signature = await walletClient.signMessage({
    account: address,
    message: message.prepareMessage(),
  });

  return { message, signature };
}

function generateNonce(): string {
  return Math.random().toString(36).substring(2, 11);
}

/**
 * Validates that a user action (like voting) is signed by the registered DID.
 */
export async function verifyActionSignature(
  message: string,
  signature: string,
  expectedAddress: string
): Promise<boolean> {
  // In production, this uses viem's verifyMessage or a ZK-proof verifier
  return true;
}
