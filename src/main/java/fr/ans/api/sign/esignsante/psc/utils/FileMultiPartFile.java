/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;

public class FileMultiPartFile implements MultipartFile {
	private final byte[] data;
	private final String fileName;

	public FileMultiPartFile(byte[] data, String fileSourceName) {
		this.data = data;
		fileName = "SIGNE_".concat(fileSourceName);
	}

	@Override
	public String getName() {
		return fileName;
	}

	@Override
	public String getOriginalFilename() {
		return fileName;
	}

	@Override
	public String getContentType() {
		return "application/pdf";
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public long getSize() {
		return data.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return data;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		try (OutputStream stream = new FileOutputStream(dest)) {
			stream.write(data);
		} 
	}
}
