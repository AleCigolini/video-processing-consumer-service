package br.com.video.processing.presentation.kafka;

import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;

/**
 * Interface para o consumidor do tópico de vídeos enviados.
 * <p>
 * Este serviço recebe informações sobre vídeos que foram enviados (upload concluído)
 * e executa a lógica de processamento do vídeo para divisão em frames
 * </p>
 *
 * <p>
 * Implementações desta interface devem consumir mensagens contendo os dados do vídeo enviado,
 * representados por {@link UploadedVideoInfoDto}.
 * </p>
 */
public interface VideoProcessingConsumer {
    /**
     * Consome uma mensagem referente a um vídeo enviado e executa o processamento do vídeo.
     *
     * @param uploadedVideoInfoDto informações do vídeo enviado para processamento
     */
    void consume(UploadedVideoInfoDto uploadedVideoInfoDto);
}
