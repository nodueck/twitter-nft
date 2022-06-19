package com.ndueck.algorand.twitternft.ipfs;

import com.ndueck.algorand.twitternft.config.ApplicationConfiguration;
import com.ndueck.algorand.twitternft.ipfs.config.IpfsConfiguration;
import com.ndueck.algorand.twitternft.ipfs.config.IpfsConfigurationProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


//TODO put this in integration test folder
@ExtendWith(SpringExtension.class)
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = {ApplicationConfiguration.class, IpfsConfiguration.class, IpfsConfigurationProperties.class, IpfsServiceImpl.class})
public class IpfsServiceTest {

    @Autowired
    private IpfsService ipfsService;

    @TempDir
    private File tempDir;
    private File testFile;

    @Container
    private final static GenericContainer<?> ipfsContainer = new GenericContainer<>("ipfs/go-ipfs:latest") // tested with v0.13.0
            .withExposedPorts(5001);

    @DynamicPropertySource
    private static void registerIpfsProperties(DynamicPropertyRegistry registry) {
        registry.add("ipfs.host", () -> ipfsContainer.getHost());
        registry.add("ipfs.port", () -> ipfsContainer.getMappedPort(5001));
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        testFile = new File(tempDir, "testFile.txt");
        FileWriter writer = new FileWriter(testFile);
        writer.append("Test");
        writer.close();
    }

    @AfterEach
    public void afterEach() {
        testFile.delete();
    }

    @Test
    public void testUploadAndDownloadAsBytes() throws IOException {
        IpfsFileReference fileReference = ipfsService.writeFile(testFile);

        byte[] actualFileContent = ipfsService.readFileBytes(fileReference);

        String testFileContent = Files.readString(testFile.toPath());
        assertEquals(testFileContent, new String(actualFileContent));
    }

    @Test
    public void testUploadAndDownloadAsInputStream() throws IOException {
        IpfsFileReference fileReference = ipfsService.writeFile(testFile);

        InputStream actualFileStream = ipfsService.readFileStream(fileReference);

        String testFileContent = Files.readString(testFile.toPath());
        String actualFileContent = new String(actualFileStream.readAllBytes());
        assertEquals(testFileContent, actualFileContent);
    }

    @Test
    public void listFiles() throws IOException {
        IpfsFileReference fileReference = ipfsService.writeFile(testFile);

        List<IpfsFileReference> fileList = ipfsService.listFiles();

        assertTrue(fileList.contains(fileReference));
    }
}
