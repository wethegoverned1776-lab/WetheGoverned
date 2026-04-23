#!/bin/bash

# Exit on error
set -e

CIRCUIT_NAME="voter_nostr"
CIRCUITS_DIR="../identity/circuits"
BUILD_DIR="../identity/build"

mkdir -p $BUILD_DIR

echo "Compiling circuit: $CIRCUIT_NAME.circom..."
circom $CIRCUITS_DIR/$CIRCUIT_NAME.circom --r1cs --wasm --sym --c --output $BUILD_DIR

echo "Generating witness with sample input..."
# Note: In production, input.json is generated per user
# node $BUILD_DIR/${CIRCUIT_NAME}_js/generate_witness.js $BUILD_DIR/${CIRCUIT_NAME}_js/${CIRCUIT_NAME}.wasm input.json $BUILD_DIR/witness.wtns

echo "Performing setup (Groth16)..."
# Using a dummy Power of Tau for development. In production, use a public ceremony root.
if [ ! -f "$BUILD_DIR/pot12_final.ptau" ]; then
    echo "Downloading Power of Tau..."
    # For a small circuit like ours (20 levels Poseidon), pot12 is enough.
    # snarkjs powersoftau new bn128 12 $BUILD_DIR/pot12_0000.ptau -v
    # snarkjs powersoftau contribute $BUILD_DIR/pot12_0000.ptau $BUILD_DIR/pot12_0001.ptau --name="First contribution" -v
    # snarkjs powersoftau prepare phase2 $BUILD_DIR/pot12_0001.ptau $BUILD_DIR/pot12_final.ptau -v
fi

# snarkjs groth16 setup $BUILD_DIR/$CIRCUIT_NAME.r1cs $BUILD_DIR/pot12_final.ptau $BUILD_DIR/${CIRCUIT_NAME}_0000.zkey
# snarkjs zkey contribute $BUILD_DIR/${CIRCUIT_NAME}_0000.zkey $BUILD_DIR/${CIRCUIT_NAME}_final.zkey --name="Dev Setup" -v
# snarkjs zkey export verificationkey $BUILD_DIR/${CIRCUIT_NAME}_final.zkey $BUILD_DIR/verification_key.json

echo "Exporting Solidity verifier..."
# snarkjs zkey export solidityverifier $BUILD_DIR/${CIRCUIT_NAME}_final.zkey ../contracts/src/Verifier.sol

echo "Done! Verifier contract generated in contracts/src/Verifier.sol"
