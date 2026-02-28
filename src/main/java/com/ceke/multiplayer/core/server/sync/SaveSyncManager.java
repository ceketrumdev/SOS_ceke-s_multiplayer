/* ************************************************************************** */
/*                                                                            */
/*       ::::::::  :::::::::: :::    ::: ::::::::::                           */
/*     :+:    :+: :+:        :+:   :+:  :+:                                   */
/*    +:+        +:+        +:+  +:+   +:+                                    */
/*   +#+        +#++:++#   +#++:++    +#++:++#                                */
/*  +#+        +#+        +#+  +#+   +#+                                      */
/* #+#    #+# #+#        #+#   #+#  #+#                                       */
/* ########  ########## ###    ### ##########                                 */
/*                                                                            */
/*   SaveSyncManager.java                                                   */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.sync;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility to zip a save directory into a byte array (for the host to send)
 * and to unzip a byte array back into a save directory (for the client to
 * load).
 */
public final class SaveSyncManager {

    private SaveSyncManager() {
    }

    /**
     * Reads the given save file, compresses it into a zip byte array.
     */
    public static byte[] zipSaveFile(Path saveFile) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry zipEntry = new ZipEntry(saveFile.getFileName().toString());
            zos.putNextEntry(zipEntry);
            Files.copy(saveFile, zos);
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    /**
     * Unzips the given zip bytes and writes the extracted save file to the target
     * path.
     */
    public static void unzipSaveFile(byte[] zipBytes, Path targetSaveFile) throws IOException {
        Files.createDirectories(targetSaveFile.getParent());
        try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
                ZipInputStream zis = new ZipInputStream(bais)) {

            ZipEntry entry = zis.getNextEntry();
            if (entry != null) {
                Files.copy(zis, targetSaveFile, StandardCopyOption.REPLACE_EXISTING);
                zis.closeEntry();
            }
        }
    }
}
