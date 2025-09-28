package br.com.video.processing.application.usecase;

import br.com.video.processing.domain.VideoChunkInfo;

import java.io.InputStream;

/**
 * Use case para extrair frames de um vídeo e salvar como ZIP no Azure.
 */
public interface ExtractFramesUseCase {
    /**
     * Extrai frames do vídeo indicado e salva um arquivo ZIP no caminho "zip-id-video/{id}.zip".
     * Assumimos extração de 1 frame por segundo.
     * O controlador é responsável por fornecer e fechar o InputStream do vídeo.
     *
     * @param videoChunkInfo informações do vídeo e do storage.
     * @param videoStream    stream do conteúdo do vídeo.
     */
    void extractAndSave(VideoChunkInfo videoChunkInfo, InputStream videoStream);
}
