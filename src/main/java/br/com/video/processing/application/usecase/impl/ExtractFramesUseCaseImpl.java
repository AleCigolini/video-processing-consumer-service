package br.com.video.processing.application.usecase.impl;

import br.com.video.processing.application.usecase.ExtractFramesUseCase;
import br.com.video.processing.common.interfaces.BlobStoragePersister;
import br.com.video.processing.domain.VideoChunkInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class ExtractFramesUseCaseImpl implements ExtractFramesUseCase {

    private final BlobStoragePersister blobStoragePersister;

    @ConfigProperty(name = "video.frames.sample-every-n-frames", defaultValue = "30")
    int sampleEveryNFrames;

    @Inject
    public ExtractFramesUseCaseImpl(BlobStoragePersister blobStoragePersister) {
        this.blobStoragePersister = blobStoragePersister;
    }

    @Override
    public void extractAndSave(VideoChunkInfo videoChunkInfo, InputStream videoStream) {
        Path tempVideo = null;
        try {
            tempVideo = Files.createTempFile("video-src-" + videoChunkInfo.getVideoId(), ".mp4");
            Files.copy(videoStream, tempVideo, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            try (SeekableByteChannel channel = NIOUtils.readableChannel(tempVideo.toFile())) {
                FrameGrab grab = FrameGrab.createFrameGrab(channel);

                AtomicInteger frameIndex = new AtomicInteger(0);
                int saved = 0;
                final int step = sampleEveryNFrames > 0 ? sampleEveryNFrames : 1;
                Picture picture;
                while ((picture = grab.getNativeFrame()) != null) {
                    int idx = frameIndex.getAndIncrement();
                    if (idx % step != 0) {
                        continue;
                    }

                    BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);

                    String fileName = String.format("part_%04d_frame_%06d.png", videoChunkInfo.getChunkPosition(), idx);
                    String blobPath = videoChunkInfo.getUserId() + "/" + videoChunkInfo.getVideoId() + "/" + fileName;

                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        ImageIO.write(bufferedImage, "png", baos);
                        byte[] bytes = baos.toByteArray();
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                            blobStoragePersister.save(
                                    videoChunkInfo.getConnectionString(),
                                    videoChunkInfo.getContainerName(),
                                    blobPath,
                                    "image/png",
                                    bais,
                                    bytes.length
                            );
                        }
                    }
                    saved++;
                }

                if (saved == 0) {
                    throw new RuntimeException("Nenhum frame foi extraído do vídeo");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao extrair frames e salvar no Azure: " + e.getMessage(), e);
        } finally {
            safeDelete(tempVideo);
        }
    }

    private void safeDelete(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignored) {
            }
        }
    }
}
