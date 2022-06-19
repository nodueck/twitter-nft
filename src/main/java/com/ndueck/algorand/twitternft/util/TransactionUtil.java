package com.ndueck.algorand.twitternft.util;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class TransactionUtil {

    @Autowired
    private AlgodClient client;

    @Autowired
    private ObjectMapper objectMapper;

    // utility function to wait on a transaction to be confirmed
    public void waitForConfirmation(String txID) throws Exception {

        Long lastRound = client.GetStatus().execute().body().lastRound;

        while (true) {
            // Check the pending tranactions
            Response<PendingTransactionResponse> pendingInfo = client.PendingTransactionInformation(txID).execute();
            if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
                // Got the completed Transaction
                log.debug("Transaction " + txID + " confirmed in round " + pendingInfo.body().confirmedRound);
                break;
            }
            lastRound++;
            client.WaitForBlock(lastRound).execute();
        }
    }

    public void printCreatedAsset(Account account, Long assetID) throws Exception {
        com.algorand.algosdk.v2.client.model.Account accountInfo = client.AccountInformation(account.getAddress()).execute().body();
        accountInfo.createdAssets.stream()
                .filter(createdAsset -> assetID.longValue() == createdAsset.index.longValue())
                .findFirst()
                .ifPresent(asset ->
                        {
                            try {
                                log.info("Created Asset Info: {}", objectMapper.writeValueAsString(asset));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    public void printAssetHolding(Account account, Long assetID) throws Exception {
        com.algorand.algosdk.v2.client.model.Account accountInfo = client.AccountInformation(account.getAddress()).execute().body();
        accountInfo.assets.stream()
                .filter(createdAsset -> assetID.longValue() == createdAsset.assetId.longValue())
                .findFirst()
                .ifPresent(asset ->
                        {
                            try {
                                log.info("Asset Holding Info: {}", objectMapper.writeValueAsString(asset));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    public String submitTransaction(SignedTransaction signedTx) throws Exception {
        // Msgpack encode the signed transaction
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTx);
        return client.RawTransaction().rawtxn(encodedTxBytes).execute().body().txId;
    }
}
