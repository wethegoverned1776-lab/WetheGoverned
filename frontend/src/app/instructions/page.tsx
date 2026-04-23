"use client";

import { useState } from "react";
import { useAccount } from "wagmi";
import { ConnectButton } from "@/components/auth/ConnectButton";

export default function InstructionsPage() {
  const { isConnected } = useAccount();
  const [district, setDistrict] = useState("FL-06");
  const [content, setContent] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // Logic to:
    // 1. Upload content to Arweave/IPFS
    // 2. Generate ZK-Proof of district residency
    // 3. Call Instructions.sol contract
    alert("Instruction submitted to the blockchain!");
  };

  return (
    <div className="max-w-2xl mx-auto">
      <header className="mb-12">
        <h2 className="text-3xl font-bold text-white mb-2">Direct Instructions</h2>
        <p className="text-slate-400">
          Send immutable, verified instructions to your district representative.
          Your residency is proven via ZK-proof, keeping your identity private.
        </p>
      </header>

      {!isConnected ? (
        <div className="bg-slate-900 border border-slate-700 p-8 rounded-2xl text-center">
          <p className="mb-6 text-slate-300">Connect your wallet to send instructions.</p>
          <ConnectButton />
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">District</label>
            <input
              type="text"
              value={district}
              disabled
              className="w-full bg-slate-800 border border-slate-700 rounded-lg px-4 py-2 text-slate-500 cursor-not-allowed"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">Instruction Content</label>
            <textarea
              rows={6}
              placeholder="e.g. I instruct you to vote 'Nay' on HR-123..."
              value={content}
              onChange={(e) => setContent(e.target.value)}
              className="w-full bg-slate-800 border border-slate-700 rounded-lg px-4 py-2 text-white focus:ring-2 focus:ring-accent focus:border-transparent transition-all outline-none"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-accent text-slate-900 font-bold py-3 rounded-lg hover:bg-white transition-all shadow-lg shadow-accent/20"
          >
            Submit Instruction (ZK-Proof Required)
          </button>
        </form>
      )}

      <section className="mt-16">
        <h3 className="text-xl font-bold text-white mb-6">Recent Public Instructions</h3>
        <div className="space-y-4">
          <div className="bg-slate-900/50 border border-slate-800 p-6 rounded-xl">
            <div className="flex justify-between items-start mb-4">
              <span className="text-accent text-xs font-bold uppercase tracking-widest">FL-06</span>
              <span className="text-slate-500 text-xs">2 hours ago</span>
            </div>
            <p className="text-slate-300 text-sm italic">"I instruct you to prioritize the local infrastructure bill regarding the A1A coastal reinforcements..."</p>
          </div>
        </div>
      </section>
    </div>
  );
}
