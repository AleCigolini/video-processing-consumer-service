package br.com.video.processing.application.usecase;

import br.com.video.processing.domain.VideoChunkInfo;

import java.io.InputStream;

/**
 * Use case interface para obtenção de vídeos.
 */
public interface GetVideoUseCase {
    /**
     * Obtém o vídeo conforme as informações fornecidas.
     *
     * @param videoChunkInfo Informações do vídeo a ser obtido.
     * @return InputStream do vídeo obtido.
     */
    InputStream getVideo(VideoChunkInfo videoChunkInfo);
}
