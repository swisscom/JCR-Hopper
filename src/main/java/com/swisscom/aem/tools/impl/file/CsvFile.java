package com.swisscom.aem.tools.impl.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.config.File;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "API surface")
public class CsvFile implements File {
	private static final Predicate<String> REQUIRES_ESCAPING = Pattern.compile("\\R|\"|,|'").asPredicate();

	@Getter
	private final String name;

	private final List<String> contents = new LinkedList<>();

	/**
	 * Add fields in a line.
	 *
	 * @param fields the array of fields to add
	 */
	public void line(String... fields) {
		line(Arrays.stream(fields));
	}

	/**
	 * Add fields in a line.
	 *
	 * @param fields the list of fields to add
	 */
	public void line(List<String> fields) {
		line(fields.stream());
	}

	private void line(Stream<String> fields) {
		contents.add(fields.map(CsvFile::escapeField).collect(Collectors.joining(",")));
	}

	/**
	 * Add multple CSV lines in a varargs call.
	 *
	 * @param lines the lines to add
	 */
	public void lines(String[]... lines) {
		for (String[] line : lines) {
			line(line);
		}
	}

	@Override
	public String getMimeType() {
		return "text/csv;charset=utf-8";
	}

	@Override
	public String getExtension() {
		return "csv";
	}

	@Override
	public byte[] getContents() {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
		try {
			for (String line : contents) {
				writer.write(line);
				writer.write('\n');
			}
			writer.flush();
		} catch (IOException ex) {
			log.error("Failed to generate CSV output", ex);
		}
		return stream.toByteArray();
	}

	private static String escapeField(String field) {
		if (field == null) {
			return StringUtils.EMPTY;
		}
		if (REQUIRES_ESCAPING.test(field)) {
			return '"' + field.replace("\"", "\"\"") + '"';
		}
		return field;
	}
}
