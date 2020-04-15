package com.parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.google.googlejavaformat.java.JavaFormatterOptions.Style;
import com.parse.constants.Keywords;
import com.parse.models.PredicateInfo;
import com.parse.utils.CodeFormatter;
import com.parse.utils.IndentSpaceParser;
import com.parse.utils.PredicateParser;
import com.parse.utils.PredicateRecorder;

/**
 * The controller TaskExecutor. It holds the control of the application.
 */
public class TaskExecutor {

	/**
	 * Processes the for loop
	 * 
	 * @param lines        The actual lines
	 * @param updatedLines The updated lines
	 * @param startPos     The for loop start position
	 * @param totalLines   Total lines in its parent snippet
	 * @return The end position of for loop
	 */
	private static int processForLoop(List<String> lines, List<String> updatedLines, int startPos, int totalLines) {

		String spaces = IndentSpaceParser.getIndentSpaces(lines.get(startPos));
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));
		PredicateInfo predicateInfo = PredicateParser.processForStatement(lines.get(startPos));

		if (predicateInfo != null) {
			PredicateRecorder.record(predicateInfo.getReuseStatement());
			updatedLines.add(spaces + predicateInfo.getVarInitializationStatement());
			updatedLines.add(spaces + predicateInfo.getInitializationStatement());
			updatedLines.add(spaces + predicateInfo.getParentStatement());
		}
		List<String> innerBodyLines = new ArrayList<>();
		int bodyLineCounter = startPos + 1;
		while (bodyLineCounter < totalLines) {
			String line = lines.get(bodyLineCounter);
			if (StringUtils.isNotBlank(line.trim())) {
				if (IndentSpaceParser.getIndentSpacesCount(line) > indentedSpaceCount) {
					innerBodyLines.add(line);
				} else {
					break;
				}
			}
			bodyLineCounter++;
		}

		updatedLines.addAll(process(innerBodyLines));
		if (predicateInfo != null) {
			updatedLines.add(spaces + "\t" + predicateInfo.getVarChangeStatement());
			updatedLines.add(spaces + "\t" + predicateInfo.getReuseStatement());
		}

		if (bodyLineCounter < totalLines
				&& IndentSpaceParser.getIndentSpacesCount(lines.get(bodyLineCounter)) == indentedSpaceCount
				&& lines.get(bodyLineCounter).trim().equals("}")) {
			updatedLines.add(lines.get(bodyLineCounter));
		} else {
			bodyLineCounter--;
			updatedLines.add(spaces + "}");
		}

		return bodyLineCounter;
	}

	/**
	 * Processes the while loop
	 * 
	 * @param lines        The actual lines
	 * @param updatedLines The updated lines
	 * @param startPos     The while loop start position
	 * @param totalLines   Total lines in its parent snippet
	 * @return The end position of while loop
	 */
	private static int processWhileLoop(List<String> lines, List<String> updatedLines, int startPos, int totalLines) {

		String spaces = IndentSpaceParser.getIndentSpaces(lines.get(startPos));
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));
		PredicateInfo predicateInfo = PredicateParser.processWhileStatement(lines.get(startPos));

		if (predicateInfo != null) {
			PredicateRecorder.record(predicateInfo.getReuseStatement());
			updatedLines.add(spaces + predicateInfo.getInitializationStatement());
			updatedLines.add(spaces + predicateInfo.getParentStatement());
		}
		List<String> innerBodyLines = new ArrayList<>();
		int bodyLineCounter = startPos + 1;
		while (bodyLineCounter < totalLines) {
			String line = lines.get(bodyLineCounter);
			if (StringUtils.isNotBlank(line.trim())) {
				if (IndentSpaceParser.getIndentSpacesCount(line) > indentedSpaceCount) {
					innerBodyLines.add(line);
				} else {
					break;
				}
			}
			bodyLineCounter++;
		}

		updatedLines.addAll(process(innerBodyLines));
		if (predicateInfo != null) {
			updatedLines.add(spaces + "\t" + predicateInfo.getReuseStatement());
		}

		if (bodyLineCounter < totalLines
				&& IndentSpaceParser.getIndentSpacesCount(lines.get(bodyLineCounter)) == indentedSpaceCount
				&& lines.get(bodyLineCounter).trim().equals("}")) {
			updatedLines.add(lines.get(bodyLineCounter));
		} else {
			bodyLineCounter--;
			updatedLines.add(spaces + "}");
		}

		return bodyLineCounter;
	}

	/**
	 * Processes the do-while loop
	 * 
	 * @param lines        The actual lines
	 * @param updatedLines The updated lines
	 * @param startPos     The do-while loop start position
	 * @param totalLines   Total lines in its parent snippet
	 * @return The end position of do-while loop
	 */
	private static int processDoWhileLoop(List<String> lines, List<String> updatedLines, int startPos, int totalLines) {

		int pos = updatedLines.size();
		String spaces = IndentSpaceParser.getIndentSpaces(lines.get(startPos));
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));
		updatedLines.add(lines.get(startPos));

		List<String> innerBodyLines = new ArrayList<>();
		int bodyLineCounter = startPos + 1;
		while (bodyLineCounter < totalLines) {
			String line = lines.get(bodyLineCounter);
			if (StringUtils.isNotBlank(line.trim())) {
				if (IndentSpaceParser.getIndentSpacesCount(line) > indentedSpaceCount) {
					innerBodyLines.add(line);
				} else {
					break;
				}
			}
			bodyLineCounter++;
		}

		updatedLines.addAll(process(innerBodyLines));

		PredicateInfo predicateInfo = PredicateParser.processDoWhileStatement(lines.get(bodyLineCounter));
		if (predicateInfo != null) {
			PredicateRecorder.record(predicateInfo.getReuseStatement());
			updatedLines.add(spaces + "\t" + predicateInfo.getReuseStatement());
			updatedLines.add(spaces + predicateInfo.getParentStatement());
		}
		updatedLines.add(pos, spaces + predicateInfo.getInitializationStatement());
		return bodyLineCounter;
	}

	/**
	 * Processes the if-else statements
	 * 
	 * @param lines        The actual lines
	 * @param updatedLines The updated lines
	 * @param startPos     The if-else start position
	 * @param totalLines   Total lines in its parent snippet
	 * @return The end position of if-else statements
	 */
	private static int processIfElse(List<String> lines, List<String> updatedLines, int startPos, int totalLines) {

		String spaces = IndentSpaceParser.getIndentSpaces(lines.get(startPos));
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));
		PredicateInfo predicateInfo = PredicateParser.processIfElseStatement(lines.get(startPos));

		if (predicateInfo != null) {
			PredicateRecorder.record(predicateInfo.getReuseStatement());
			updatedLines.add(spaces + predicateInfo.getInitializationStatement());
			updatedLines.add(spaces + predicateInfo.getParentStatement());
		}
		List<String> innerBodyLines = new ArrayList<>();
		int bodyLineCounter = startPos + 1;
		while (bodyLineCounter < totalLines) {
			String line = lines.get(bodyLineCounter);
			if (StringUtils.isNotBlank(line.trim())) {
				if (IndentSpaceParser.getIndentSpacesCount(line) > indentedSpaceCount) {
					innerBodyLines.add(line);
				} else {
					break;
				}
			}
			bodyLineCounter++;
		}

		updatedLines.addAll(process(innerBodyLines));

		if (bodyLineCounter < totalLines
				&& IndentSpaceParser.getIndentSpacesCount(lines.get(bodyLineCounter)) == indentedSpaceCount) {
			String line = lines.get(bodyLineCounter).trim();
			if (line.equals("}")) {
				updatedLines.add("}");
			} else if (line.startsWith("}")) {
				updatedLines.add("}");
				bodyLineCounter--;
			}
		} else {
			bodyLineCounter--;
			updatedLines.add(spaces + "}");
		}

		return bodyLineCounter;
	}

	/**
	 * Processes the lines of code
	 * 
	 * @param lines The lines
	 * @return The processed lines of code
	 */
	private static List<String> process(List<String> lines) {

		List<String> updatedLines = new ArrayList<>();
		int totalLines = lines.size();

		for (int i = 0; i < totalLines; i++) {
			if (lines.get(i).trim().startsWith(Keywords.FOR)) {
				i = processForLoop(lines, updatedLines, i, totalLines);
			} else if (lines.get(i).trim().startsWith(Keywords.WHILE)) {
				i = processWhileLoop(lines, updatedLines, i, totalLines);
			} else if (lines.get(i).trim().startsWith(Keywords.DO)) {
				i = processDoWhileLoop(lines, updatedLines, i, totalLines);
			} else if (lines.get(i).trim().startsWith(Keywords.IF)) {
				i = processIfElse(lines, updatedLines, i, totalLines);
			} else {
				updatedLines.add(lines.get(i));
			}
		}

		return updatedLines;
	}

	/**
	 * Saves the updated code
	 * 
	 * @param code     The code
	 * @param fileName The file name
	 */
	private static void saveUpdatedCode(String code, String fileName) {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write(code);
			writer.flush();
		} catch (IOException ioException) {
			System.out.println("Error saving the updated code.");
		}
	}

	/**
	 * Execution starts from here
	 * 
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("Invalid arguments!");
			System.exit(1);
		}

		Formatter formatter = new Formatter(JavaFormatterOptions.builder().style(Style.GOOGLE).build());
		try {
			String formattedJava = CodeFormatter.format(Paths.get(args[0]));
			List<String> updatedLines = process(Arrays.asList(formattedJava.split("\n")));

			// Saving the updated code
			StringBuilder codeBuilder = new StringBuilder();
			for (String line : updatedLines) {
				codeBuilder.append(line);
			}
			String formattedUpdatedCode = formatter.formatSource(codeBuilder.toString());
			saveUpdatedCode(formattedUpdatedCode, args[0].substring(args[0].lastIndexOf(File.separator) + 1));
		} catch (FormatterException formatterException) {
			System.out.println("Error formatting the code.");
		}
	}
}
