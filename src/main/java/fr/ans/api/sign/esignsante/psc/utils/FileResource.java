/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public class FileResource implements Resource{
	private final byte[] data;
	private final String fileName;

	public FileResource(byte[] data, String fileSourceName ) {
		this.data = data;
		fileName = "SIGNE_".concat(fileSourceName);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	@Override
	public boolean exists() {	
		return true;
	}

	@Override
	public URL getURL() throws IOException {
		return null;
	}

	@Override
	public URI getURI() throws IOException {
		return null;
	}

	@Override
	public File getFile() throws IOException {	
		return null;
	}

	@Override
	public long contentLength() throws IOException {     
		return data.length;
	}

	@Override
	public long lastModified() throws IOException {
		return 0;
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		return null;
	}

	@Override
	public String getFilename() {
		return fileName;
	}

	@Override
	public String getDescription() {	
		return "Document PDF signé par esignsante";
	}

	
}
