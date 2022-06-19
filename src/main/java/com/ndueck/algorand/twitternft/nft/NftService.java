package com.ndueck.algorand.twitternft.nft;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndueck.algorand.twitternft.ipfs.IpfsFileReference;
import com.ndueck.algorand.twitternft.ipfs.IpfsService;
import com.ndueck.algorand.twitternft.util.TransactionUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.*;
import java.math.BigInteger;

@Log4j2
@Service
public class NftService {

    @Autowired
    private AlgodClient client;

    @Autowired
    private TransactionUtil util;
    @Autowired
    private IpfsService ipfsService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String IPFS_URL_PREFIX = "ipfs://";

    public void createNFT(InputStream data, String name, Account account) throws Exception {
        IpfsFileReference imageIpfsReference = ipfsService.writeInputStream(data);
        IpfsFileReference metadataIpfsReference = createMetaData(imageIpfsReference);

        TransactionParametersResponse params = client.TransactionParams().execute().body();
        params.fee = (long) 1000;
        Transaction tx = Transaction.AssetCreateTransactionBuilder()
                .sender(account.getAddress())
                .assetTotal(BigInteger.valueOf(1))
                .assetDecimals(0)
                .assetName(name)
                .url(IPFS_URL_PREFIX + metadataIpfsReference)
                .metadataHashUTF8(metadataIpfsReference.toString())
                .defaultFrozen(false)
                .suggestedParams(params)
                .build();
        SignedTransaction signedTx = account.signTransaction(tx);
        String id = util.submitTransaction(signedTx);
        System.out.println("Transaction ID: " + id);
        util.waitForConfirmation(id);
        PendingTransactionResponse pTrx = client.PendingTransactionInformation(id).execute().body();

        // Now that the transaction is confirmed we can get the assetID
        Long assetID = pTrx.assetIndex;
        log.info("Created NFT with AssetID {} ", assetID);
        util.printCreatedAsset(account, assetID);
        util.printAssetHolding(account, assetID);
    }

    private IpfsFileReference createMetaData(IpfsFileReference fileReference) throws IOException {
        NftMetadataArc69 metadata = NftMetadataArc69.builder()
                .description("Created By Twitter NFT-Creator")
                .mediaUrl(IPFS_URL_PREFIX + fileReference.toString())
                .mimeType(MimeTypeUtils.IMAGE_PNG)
                .externalUrl("tbd") //TODO
                .build();

        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        objectMapper.writeValue(writer, metadata);
        return ipfsService.writeInputStream(pis);
    }

}
