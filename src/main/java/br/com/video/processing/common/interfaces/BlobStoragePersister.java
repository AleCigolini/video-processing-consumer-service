package br.com.video.processing.common.interfaces;

import java.io.InputStream;

/**
 * Interface genérica para persistir qualquer conteúdo binário em um storage de blobs.
 */
public interface BlobStoragePersister {
    /**
     * Persiste um conteúdo binário no caminho especificado do container.
     *
     * @param connectionString Connection string do Azure Blob Storage.
     * @param containerName    Nome do container.
     * @param blobPath         Caminho do blob dentro do container (ex.: "zip-id-video/{id}.zip").
     * @param contentType      Content-Type do blob.
     * @param data             InputStream do conteúdo.
     * @param length           Tamanho do conteúdo em bytes.
     */
    void save(String connectionString, String containerName, String blobPath, String contentType, InputStream data, long length);
}

