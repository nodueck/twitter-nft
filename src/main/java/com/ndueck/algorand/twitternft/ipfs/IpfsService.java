package com.ndueck.algorand.twitternft.ipfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


//TODO replace this for and NftIpfsService that takes a file and metadata
/**
 * A simple adapter for java-ipfs-http-client that lets you upload/download files.
 */
public interface IpfsService {

    IpfsFileReference writeFile(final File file) throws IOException;

    IpfsFileReference writeInputStream(final InputStream inputStream) throws IOException;

    byte[] readFileBytes(final IpfsFileReference hash) throws IOException;

    InputStream readFileStream(final IpfsFileReference hash) throws IOException;

    List<IpfsFileReference> listFiles() throws IOException;

}
