package com.ndueck.algorand.twitternft.ipfs;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@Service
public class IpfsServiceImpl implements IpfsService {

    @Autowired
    private IPFS ipfsClient;

    @Override
    public IpfsFileReference writeFile(final File file) throws IOException {
        if(file.isFile()) {
            final NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file);
            final List<MerkleNode> response = ipfsClient.add(fileWrapper);
            final Multihash hash = response.get(0).hash;
            ipfsClient.pin.add(hash);
            return new IpfsFileReference(hash);
        } else {
            throw new IllegalArgumentException("Argument is not an actual file.");
        }
    }

    @Override
    public IpfsFileReference writeInputStream(InputStream inputStream) throws IOException {
        NamedStreamable.InputStreamWrapper inputStreamWrapper = new NamedStreamable.InputStreamWrapper(inputStream);
        final List<MerkleNode> response = ipfsClient.add(inputStreamWrapper);
        final Multihash hash = response.get(0).hash;
        ipfsClient.pin.add(hash);
        return new IpfsFileReference(hash);
    }

    @Override
    public byte[] readFileBytes(final IpfsFileReference ipfsFileReference) throws IOException {
        return ipfsClient.cat(ipfsFileReference.getMultihash());
    }

    @Override
    public InputStream readFileStream(final IpfsFileReference hash) throws IOException {
        return ipfsClient.catStream(hash.getMultihash());
    }

    @Override
    public List<IpfsFileReference> listFiles() throws IOException {
        return ipfsClient.pin.ls(IPFS.PinType.all)
                .keySet().stream()
                .map(multihash -> new IpfsFileReference(multihash))
                .collect(Collectors.toList());
    }

}
