"use client";

import { useAccount, useConnect, useDisconnect } from "wagmi";
import { injected } from "wagmi/connectors";
import { useState, useEffect } from "react";
import { createSiweSession } from "@/lib/auth/siwe";
import { useWalletClient } from "wagmi";

export function ConnectButton() {
  const { address, isConnected, chainId } = useAccount();
  const { connect } = useConnect();
  const { disconnect } = useDisconnect();
  const { data: walletClient } = useWalletClient();
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);

  const handleLogin = async () => {
    if (!walletClient || !address || !chainId) return;
    try {
      const session = await createSiweSession(walletClient, address, chainId);
      console.log("SIWE Session Created:", session);
      // In a real app, you would send this to your backend or store it in a decentralized session store
      alert("Successfully signed in with SIWE!");
    } catch (error) {
      console.error("Login failed:", error);
    }
  };

  if (!mounted) return null;

  if (isConnected) {
    return (
      <div className="flex gap-4 items-center">
        <span className="text-sm text-slate-400 font-mono">
          {address?.slice(0, 6)}...{address?.slice(-4)}
        </span>
        <button
          onClick={handleLogin}
          className="bg-accent text-slate-900 px-4 py-2 rounded-full text-sm font-bold hover:bg-white transition-all"
        >
          Sign Message
        </button>
        <button
          onClick={() => disconnect()}
          className="text-sm text-slate-400 hover:text-white underline"
        >
          Disconnect
        </button>
      </div>
    );
  }

  return (
    <button
      onClick={() => connect({ connector: injected() })}
      className="bg-accent text-slate-900 px-4 py-2 rounded-full text-sm font-bold hover:bg-white transition-all"
    >
      Connect Wallet
    </button>
  );
}
