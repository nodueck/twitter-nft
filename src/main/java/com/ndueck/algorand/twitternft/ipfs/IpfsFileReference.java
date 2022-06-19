package com.ndueck.algorand.twitternft.ipfs;

import io.ipfs.multihash.Multihash;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class IpfsFileReference {

    private Multihash multihash;

    Multihash getMultihash() {
        return multihash;
    }

    @Override
    public String toString() {
        return multihash.toBase58();
    }
}
