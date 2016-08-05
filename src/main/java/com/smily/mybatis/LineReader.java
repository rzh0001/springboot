package com.smily.mybatis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

public class LineReader extends BufferedReader implements Iterable<String> {
	public LineReader(String fileName, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
		this(new InputStreamReader(new FileInputStream(fileName), encoding));
	}

	public LineReader(File file, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
		this(new InputStreamReader(new FileInputStream(file), encoding));
	}

	public LineReader(InputStream inputStream, String encoding) throws UnsupportedEncodingException {
		this(new InputStreamReader(inputStream, encoding));
	}

	public LineReader(String fileName, Charset charset) throws FileNotFoundException, UnsupportedEncodingException {
		this(new InputStreamReader(new FileInputStream(fileName), charset));
	}

	public LineReader(File file, Charset charset) throws FileNotFoundException, UnsupportedEncodingException {
		this(new InputStreamReader(new FileInputStream(file), charset));
	}

	public LineReader(InputStream inputStream, Charset charset) throws UnsupportedEncodingException {
		this(new InputStreamReader(inputStream, charset));
	}

	public LineReader(String fileName) throws FileNotFoundException {
		this(new FileReader(fileName));
	}

	public LineReader(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	public LineReader(Reader reader) {
		super(reader);
	}

	@Override
	public Iterator<String> iterator() {
		return IOUtils.lineIterator(this);
	}
}
