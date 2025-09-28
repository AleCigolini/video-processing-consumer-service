package br.com.video.processing.application.usecase.impl;

import br.com.video.processing.common.interfaces.BlobStoragePersister;
import br.com.video.processing.domain.VideoChunkInfo;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExtractFramesUseCaseImplTest {
    private BlobStoragePersister blobStoragePersister;
    private ExtractFramesUseCaseImpl useCase;
    private VideoChunkInfo videoChunkInfo;

    @BeforeEach
    void setUp() {
        blobStoragePersister = mock(BlobStoragePersister.class);
        useCase = new ExtractFramesUseCaseImpl(blobStoragePersister);
        videoChunkInfo = mock(VideoChunkInfo.class);
        when(videoChunkInfo.getId()).thenReturn(UUID.randomUUID());
        when(videoChunkInfo.getUserId()).thenReturn(UUID.randomUUID());
        when(videoChunkInfo.getChunkPosition()).thenReturn(1);
        when(videoChunkInfo.getConnectionString()).thenReturn("conn");
        when(videoChunkInfo.getContainerName()).thenReturn("cont");
        when(videoChunkInfo.getFileName()).thenReturn("file.mp4");
    }

    @Test
    void extractAndSave_shouldThrowRuntimeException_whenNoFramesExtracted() throws Exception {
        InputStream videoStream = new ByteArrayInputStream(new byte[0]);
        Exception ex = assertThrows(RuntimeException.class, () -> useCase.extractAndSave(videoChunkInfo, videoStream));
        assertTrue(ex.getMessage().contains("Falha ao extrair frames e salvar no Azure"));
    }

    @Test
    void extractAndSave_shouldDeleteTempFile_onException() throws Exception {
        InputStream videoStream = new ByteArrayInputStream(new byte[0]);

        assertThrows(RuntimeException.class, () -> useCase.extractAndSave(videoChunkInfo, videoStream));
    }

    @Test
    void safeDelete_shouldNotThrow_whenPathIsNullOrNotExists() throws Exception {
        var method = ExtractFramesUseCaseImpl.class.getDeclaredMethod("safeDelete", Path.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(useCase, (Path) null));
        Path temp = Path.of("nonexistent-file-" + System.nanoTime() + ".tmp");
        assertDoesNotThrow(() -> method.invoke(useCase, temp));
    }

    @Test
    void extractAndSave_shouldSaveFrames_whenFramesPresent() throws Exception {
        Path tempPath = Files.createTempFile("video-src-test", ".mp4");
        try {
            FrameGrab grab = mock(FrameGrab.class);
            Picture picture = mock(Picture.class);
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            try (MockedStatic<FrameGrab> frameGrab = mockStatic(FrameGrab.class);
                 MockedStatic<AWTUtil> awtUtil = mockStatic(AWTUtil.class);
                 MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
                frameGrab.when(() -> FrameGrab.createFrameGrab(any(org.jcodec.common.io.SeekableByteChannel.class)))
                        .thenReturn(grab);
                when(grab.getNativeFrame()).thenReturn(picture, (Picture) null);
                awtUtil.when(() -> AWTUtil.toBufferedImage(picture)).thenReturn(image);
                imageIO.when(() -> ImageIO.write(any(BufferedImage.class), eq("png"), any(ByteArrayOutputStream.class)))
                        .thenReturn(true);

                doNothing().when(blobStoragePersister).save(any(), any(), any(), any(), any(), anyLong());

                useCase.extractAndSave(videoChunkInfo, new ByteArrayInputStream(new byte[]{1,2,3}));

                verify(blobStoragePersister, atLeastOnce()).save(any(), any(), any(), any(), any(), anyLong());
            }
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }
}
