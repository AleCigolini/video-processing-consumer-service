package br.com.video.processing.application.gateway;

import br.com.video.processing.domain.VideoChunkInfo;

import java.io.InputStream;
import java.util.Optional;

/**
 * Interface para o gateway de vídeo.
 */
public interface VideoGateway {

    /**
     * Obtém o vídeo com base nas informações fornecidas.
     *
     * @param videoChunkInfo Informações do vídeo a ser obtido.
     * @return Um InputStream do vídeo.
     */
    Optional<InputStream> getVideo(VideoChunkInfo videoChunkInfo);

}
