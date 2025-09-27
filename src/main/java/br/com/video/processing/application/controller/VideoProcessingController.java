package br.com.video.processing.application.controller;


import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;

/**
 * Interface for video splitter controller.
 */
public interface VideoProcessingController {
    /**
     * Divide um vídeo em partes menores.
     *
     * @param uploadedVideoInfoDto Informações sobre o vídeo que será dividido.
     */
    void processVideo(UploadedVideoInfoDto uploadedVideoInfoDto);
}
