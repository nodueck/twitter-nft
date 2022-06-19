package com.ndueck.algorand.twitternft.nft;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.MimeType;

@Data
@Builder
public class NftMetadataArc69 {

    /**
     * (Required) Describes the standard used.
     */
    @JsonProperty("standard")
    private final String standard = "arc69";

    /**
     * Describes the asset to which this token represents.
     */
    @JsonProperty("description")
    private String description;

    /**
     * A URI pointing to an external website. Borrowed from Open Sea's metadata format (https://docs.opensea.io/docs/metadata-standards).
     */
    @JsonProperty("external_url")
    private String externalUrl;

    /**
     * A URI pointing to a high resolution version of the asset's media.
     */
    @JsonProperty("media_url")
    private String mediaUrl;

    /**
     * Properties following the EIP-1155 'simple properties' format. (https://github.com/ethereum/EIPs/blob/master/EIPS/eip-1155.md#erc-1155-metadata-uri-json-schema)
     */
    @JsonProperty("properties")
    private Object properties;

    /**
     * Describes the MIME type of the ASA's URL (`au` field).
     */
    @JsonProperty("mime_type")
    private MimeType mimeType;
}
