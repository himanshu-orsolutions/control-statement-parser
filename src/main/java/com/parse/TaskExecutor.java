package com.parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
import com.parse.models.Case;
import com.parse.models.OperandType;
import com.parse.models.PredicateInfo;
import com.parse.utils.CodeFormatter;
import com.parse.utils.IndentSpaceParser;
import com.parse.utils.PredicateParser;
import com.parse.utils.PredicateRecorder;

/**
 * The controller TaskExecutor. It holds the control of the application.
 */
public class TaskExecutor {

	private static Formatter formatter = new Formatter(JavaFormatterOptions.builder().style(Style.GOOGLE).build());

	/**
	 * The list of predicate information
	 */
	private static List<PredicateInfo> predicateInfoList;

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

		// Getting the current indentation of for statement
		String spaces = IndentSpaceParser.getIndentSpaces(lines.get(startPos));
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));

		// The statement might be present in multiple lines, thus merging all
		StringBuilder statementBuilder = new StringBuilder();
		statementBuilder.append(removeComment(lines.get(startPos)));
		startPos++;

		while (startPos < totalLines
				&& IndentSpaceParser.getIndentSpacesCount(lines.get(startPos)) > indentedSpaceCount + 4) {
			statementBuilder.append(removeComment(lines.get(startPos)));
			startPos++;
		}

		PredicateInfo predicateInfo = PredicateParser.processForStatement(statementBuilder.toString());

		if (predicateInfo != null) {
			predicateInfoList.add(predicateInfo);
			updatedLines.add(spaces + predicateInfo.getVarInitializationStatement());
			updatedLines.add(spaces + predicateInfo.getInitializationStatement());
			updatedLines.add(spaces + predicateInfo.getParentStatement());
		} else {
			updatedLines.add(spaces + statementBuilder.toString());
			return startPos - 1;
		}
		List<String> innerBodyLines = new ArrayList<>();
		int bodyLineCounter = startPos;
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

		// Getting the current indentation of for statement
		String spaces = IndentSpaceParser.getIndentSpaces(lines.get(startPos));
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));

		// The statement might be present in multiple lines, thus merging all
		StringBuilder statementBuilder = new StringBuilder();
		statementBuilder.append(removeComment(lines.get(startPos)));
		startPos++;

		while (startPos < totalLines
				&& IndentSpaceParser.getIndentSpacesCount(lines.get(startPos)) > indentedSpaceCount + 4) {
			statementBuilder.append(removeComment(lines.get(startPos)));
			startPos++;
		}

		PredicateInfo predicateInfo = PredicateParser.processWhileStatement(statementBuilder.toString());

		if (predicateInfo != null) {
			predicateInfoList.add(predicateInfo);
			updatedLines.add(spaces + predicateInfo.getInitializationStatement());
			updatedLines.add(spaces + predicateInfo.getParentStatement());
		}
		List<String> innerBodyLines = new ArrayList<>();
		int bodyLineCounter = startPos;
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

		// The statement might be present in multiple lines, thus merging all
		StringBuilder statementBuilder = new StringBuilder();
		statementBuilder.append(removeComment(lines.get(bodyLineCounter)));
		bodyLineCounter++;

		while (bodyLineCounter < totalLines
				&& IndentSpaceParser.getIndentSpacesCount(lines.get(bodyLineCounter)) > indentedSpaceCount + 4) {
			statementBuilder.append(removeComment(lines.get(bodyLineCounter)));
			bodyLineCounter++;
		}

		PredicateInfo predicateInfo = PredicateParser.processDoWhileStatement(statementBuilder.toString());
		if (predicateInfo != null) {
			predicateInfoList.add(predicateInfo);
			updatedLines.add(spaces + "\t" + predicateInfo.getReuseStatement());
			updatedLines.add(spaces + predicateInfo.getParentStatement());
		}
		updatedLines.add(pos, spaces + predicateInfo.getInitializationStatement());
		return bodyLineCounter - 1;
	}

	/**
	 * Removes comment from the line of code
	 * 
	 * @param line The line
	 * @return The stripped line
	 */
	private static String removeComment(String line) {

		line = line.trim();
		if (line.contains("//")) {
			return line.substring(0, line.lastIndexOf("//")).trim();
		}
		return line;
	}

	/**
	 * Processes the if statement
	 * 
	 * @param lines
	 * @param updatedLines
	 * @param startPos
	 * @param totalLines
	 * @return
	 */
	private static int processIf(List<String> lines, List<String> updatedLines, int startPos, int totalLines,
			Integer pos) {

		// Getting the current indentation of for statement
		String spaces = IndentSpaceParser.getIndentSpaces(lines.get(startPos));
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));

		// The statement might be present in multiple lines, thus merging all
		StringBuilder statementBuilder = new StringBuilder();
		statementBuilder.append(removeComment(lines.get(startPos)));
		startPos++;

		while (startPos < totalLines
				&& IndentSpaceParser.getIndentSpacesCount(lines.get(startPos)) > indentedSpaceCount + 4) {
			statementBuilder.append(removeComment(lines.get(startPos)));
			startPos++;
		}

		PredicateInfo predicateInfo = PredicateParser.processIfStatement(statementBuilder.toString());

		if (predicateInfo != null) {
			predicateInfoList.add(predicateInfo);
			updatedLines.add(pos++, spaces + predicateInfo.getInitializationStatement());
			updatedLines.add(spaces + predicateInfo.getParentStatement());
		}
		List<String> innerBodyLines = new ArrayList<>();
		int bodyLineCounter = startPos;
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
				return bodyLineCounter;
			} else {
				bodyLineCounter--;
				updatedLines.add("}");
			}
		} else {
			bodyLineCounter--;
			updatedLines.add(spaces + "}");
		}

		return bodyLineCounter;
	}

	/**
	 * Processes the else-if statements
	 * 
	 * @param lines
	 * @param updatedLines
	 * @param totalLines
	 * @param pos
	 * @param bodyLineCounter
	 * @return
	 */
	private static int processElseIf(List<String> lines, List<String> updatedLines, int totalLines, Integer pos,
			int bodyLineCounter) {

		String line = lines.get(bodyLineCounter + 1).trim();
		while (bodyLineCounter + 1 < totalLines
				&& (line.startsWith(Keywords.ELSE_IF_I) || line.startsWith(Keywords.ELSE_IF_II))) {

			bodyLineCounter++;

			// Getting the current indentation of for statement
			String spaces = IndentSpaceParser.getIndentSpaces(lines.get(bodyLineCounter));
			int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(bodyLineCounter));

			// The statement might be present in multiple lines, thus merging all
			StringBuilder statementBuilder = new StringBuilder();
			statementBuilder.append(removeComment(lines.get(bodyLineCounter)));
			bodyLineCounter++;

			while (bodyLineCounter < totalLines
					&& IndentSpaceParser.getIndentSpacesCount(lines.get(bodyLineCounter)) > indentedSpaceCount + 4) {
				statementBuilder.append(removeComment(lines.get(bodyLineCounter)));
				bodyLineCounter++;
			}

			PredicateInfo predicateInfo = PredicateParser.processElseIfStatement(statementBuilder.toString());

			if (predicateInfo != null) {
				predicateInfoList.add(predicateInfo);
				updatedLines.add(pos++, spaces + predicateInfo.getInitializationStatement());
				updatedLines.add(spaces + predicateInfo.getParentStatement());
			}

			List<String> innerBodyLines = new ArrayList<>();
			while (bodyLineCounter < totalLines) {
				line = lines.get(bodyLineCounter);
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
				line = lines.get(bodyLineCounter).trim();
				if (line.equals("}")) {
					updatedLines.add("}");
					return bodyLineCounter;
				} else {
					bodyLineCounter--;
					updatedLines.add("}");
				}
			} else {
				bodyLineCounter--;
				updatedLines.add(spaces + "}");
			}
		}

		return bodyLineCounter;
	}

	/**
	 * Processes the ternary assignment if found
	 * 
	 * @param lines
	 * @param updatedLines
	 * @param startPos
	 * @param totalLines
	 * @return
	 */
	private static int processTernaryAssignmentIfFound(List<String> lines, List<String> updatedLines, int startPos,
			int totalLines, boolean isReturnStatement) {

		// Getting the current indentation of for statement
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));

		// The statement might be present in multiple lines, thus merging all
		StringBuilder statementBuilder = new StringBuilder();
		statementBuilder.append(removeComment(lines.get(startPos)));
		startPos++;

		int comparisonSpace = isReturnStatement ? indentedSpaceCount - 1 : indentedSpaceCount;
		while (startPos < totalLines && IndentSpaceParser.getIndentSpacesCount(lines.get(startPos)) > comparisonSpace) {
			statementBuilder.append(removeComment(lines.get(startPos)));
			startPos++;
		}

		PredicateInfo predicateInfo = isReturnStatement
				? PredicateParser.processReturnTernaryStatement(statementBuilder.toString())
				: PredicateParser.processInitializationTernaryStatement(statementBuilder.toString());
		if (predicateInfo != null) {
			predicateInfoList.add(predicateInfo);
			updatedLines.add(predicateInfo.getInitializationStatement());
			updatedLines.add(predicateInfo.getParentStatement());
		} else {
			updatedLines.add(statementBuilder.toString());
		}

		return startPos - 1;
	}

	/**
	 * Processes else statement
	 * 
	 * @param lines
	 * @param updatedLines
	 * @param startPos
	 * @param totalLines
	 * @param pos
	 * @param bodyLineCounter
	 * @return
	 */
	private static int processElse(List<String> lines, List<String> updatedLines, int totalLines, Integer pos,
			int bodyLineCounter) {

		String line = lines.get(bodyLineCounter + 1).trim();
		if (bodyLineCounter + 1 < totalLines
				&& (line.startsWith(Keywords.ELSE_I) || line.startsWith(Keywords.ELSE_II))) {

			bodyLineCounter++;
			String spaces = IndentSpaceParser.getIndentSpaces(lines.get(bodyLineCounter));
			int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(bodyLineCounter));
			updatedLines.add("else {");
			bodyLineCounter++;

			List<String> innerBodyLines = new ArrayList<>();

			while (bodyLineCounter < totalLines) {
				line = lines.get(bodyLineCounter);
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
				line = lines.get(bodyLineCounter).trim();
				if (line.equals("}")) {
					updatedLines.add("}");
					return bodyLineCounter;
				} else {
					bodyLineCounter--;
					updatedLines.add("}");
				}
			} else {
				bodyLineCounter--;
				updatedLines.add(spaces + "}");
			}
		}

		return bodyLineCounter;
	}

	/**
	 * Processes the if-elseif-else statements
	 * 
	 * @param lines        The actual lines
	 * @param updatedLines The updated lines
	 * @param startPos     The if-else start position
	 * @param totalLines   Total lines in its parent snippet
	 * @return The end position of if-else statements
	 */
	private static int processIfElseifElse(List<String> lines, List<String> updatedLines, int startPos,
			int totalLines) {

		Integer pos = updatedLines.size();
		int bodyLineCounter = processIf(lines, updatedLines, startPos, totalLines, pos);

		// Parsing the else-if statements, if present
		if (bodyLineCounter + 1 < totalLines) {
			bodyLineCounter = processElseIf(lines, updatedLines, totalLines, pos, bodyLineCounter);
		}

		// Parsing the else condition
		if (bodyLineCounter + 1 < totalLines) {
			bodyLineCounter = processElse(lines, updatedLines, totalLines, pos, bodyLineCounter);
		}

		return bodyLineCounter;
	}

	/**
	 * Gets the operand type from switch case
	 * 
	 * @param caseInfo The case info
	 * @return The operand type
	 */
	private static OperandType getOperandType(Case caseInfo) {

		String operand = caseInfo.getOperand();
		if (StringUtils.isEmpty(operand)) {
			return OperandType.NONE;
		} else {
			if (operand.matches("\\d+")) {
				return OperandType.INTEGER;
			} else if (operand.matches("'.'")) {
				return OperandType.CHARACTER;
			} else if (operand.matches("\".*\"")) {
				return OperandType.STRING;
			} else {
				return OperandType.ENUM;
			}
		}
	}

	/**
	 * Processes the switch statements
	 * 
	 * @param lines
	 * @param updatedLines
	 * @param startPos
	 * @param totalLines
	 * @return
	 */
	private static int processSwitchStatements(List<String> lines, List<String> updatedLines, int startPos,
			int totalLines) {

		// Getting the current indentation of for statement
		String spaces = IndentSpaceParser.getIndentSpaces(lines.get(startPos));
		int indentedSpaceCount = IndentSpaceParser.getIndentSpacesCount(lines.get(startPos));

		// The statement might be present in multiple lines, thus merging all
		StringBuilder statementBuilder = new StringBuilder();
		statementBuilder.append(removeComment(lines.get(startPos)));
		startPos++;

		while (IndentSpaceParser.getIndentSpacesCount(lines.get(startPos)) > indentedSpaceCount) {
			statementBuilder.append(removeComment(lines.get(startPos)));
			startPos++;
		}

		String firstOperand = PredicateParser.getSwitchFirstOperand(statementBuilder.toString());
		if (StringUtils.isNotBlank(firstOperand)) {

			List<String> innerBodyLines = new ArrayList<>();
			int bodyLineCounter = startPos;
			while (bodyLineCounter < totalLines) {
				String line = lines.get(bodyLineCounter);
				if (StringUtils.isNotBlank(line.trim())) {
					if (!StringUtils.equals(line, spaces + "}")) {
						innerBodyLines.add(line);
					} else {
						break;
					}
				}
				bodyLineCounter++;
			}

			if (!innerBodyLines.isEmpty()) {
				List<Case> cases = PredicateParser.processCases(innerBodyLines);
				if (!cases.isEmpty()) {
					OperandType operandType = getOperandType(cases.get(0));
					if (operandType.equals(OperandType.NONE)) {
						updatedLines.addAll(process(cases.get(0).getBody()));
					} else {
						if (operandType.equals(OperandType.INTEGER)) {
							firstOperand = "Integer.valueOf(" + firstOperand + ")";
						} else if (operandType.equals(OperandType.CHARACTER)) {
							firstOperand = "Character.valueOf(" + firstOperand + ")";
						}

						int totalCases = cases.size();
						int pos = updatedLines.size();
						for (int i = 0; i < totalCases; i++) {
							List<String> statements = new ArrayList<>();
							List<List<String>> bodies = new ArrayList<>();

							for (int j = i; j < totalCases; j++) {
								statements.add(
										StringUtils.join(firstOperand, ".equals(", cases.get(j).getOperand(), ")"));
								bodies.add(cases.get(j).getBody());

								if (cases.get(j).isWithBreak()) {
									break;
								}
							}

							if (i == 0) {
								String statement = StringUtils.join("if (", StringUtils.join(statements, "||"), "){");
								PredicateInfo predicateInfo = PredicateParser.processIfStatement(statement);
								predicateInfoList.add(predicateInfo);
								updatedLines.add(pos++, predicateInfo.getInitializationStatement());
								updatedLines.add(predicateInfo.getParentStatement());
							} else if (i < totalCases - 1) {
								String statement = StringUtils.join("else if (", StringUtils.join(statements, "||"),
										"){");
								PredicateInfo predicateInfo = PredicateParser.processElseIfStatement(statement);
								predicateInfoList.add(predicateInfo);
								updatedLines.add(pos++, predicateInfo.getInitializationStatement());
								updatedLines.add(predicateInfo.getParentStatement());
							} else {
								if (StringUtils.isBlank(cases.get(totalCases - 1).getOperand())) {
									updatedLines.add("else{");
								} else {
									String statement = StringUtils.join("else if (", StringUtils.join(statements, "||"),
											"){");
									PredicateInfo predicateInfo = PredicateParser.processElseIfStatement(statement);
									predicateInfoList.add(predicateInfo);
									updatedLines.add(pos++, predicateInfo.getInitializationStatement());
									updatedLines.add(predicateInfo.getParentStatement());
								}
							}

							for (List<String> body : bodies) {
								updatedLines.addAll(process(body));
							}

							updatedLines.add("}");
						}
					}
				}
			}
			return bodyLineCounter;
		} else {
			updatedLines.add(statementBuilder.toString());
			return startPos--;
		}
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
				i = processIfElseifElse(lines, updatedLines, i, totalLines);
			} else if (lines.get(i).trim().matches("^\\w+ \\w+ \\=.*")) {
				i = processTernaryAssignmentIfFound(lines, updatedLines, i, totalLines, false);
			} else if (lines.get(i).trim().matches("^return.*")) {
				i = processTernaryAssignmentIfFound(lines, updatedLines, i, totalLines, true);
			} else if (lines.get(i).trim().startsWith(Keywords.SWITCH)) {
				i = processSwitchStatements(lines, updatedLines, i, totalLines);
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

		if (args.length != 2) {
			System.out.println("Invalid arguments!");
			System.exit(1);
		}

		if (!Files.exists(Paths.get(args[0]))) {
			try {
				Files.createDirectories(Paths.get(args[0]));
			} catch (IOException ioException) {
				System.out.println("Error creating the output directory.");
			}
		}

		try {
			String formattedJava = CodeFormatter.format(Paths.get(args[1]));
			predicateInfoList = new ArrayList<>();
			List<String> updatedLines = process(Arrays.asList(formattedJava.split("\n")));

			// Saving the updated code
			StringBuilder codeBuilder = new StringBuilder();
			for (String line : updatedLines) {
				codeBuilder.append(line);
				codeBuilder.append("\n");
			}
			String formattedUpdatedCode = formatter.formatSource(codeBuilder.toString());
			saveUpdatedCode(formattedUpdatedCode,
					args[0] + File.separator + args[1].substring(args[1].lastIndexOf(File.separator) + 1));

			// Creating the predicates file
			PredicateRecorder.create(
					args[0] + File.separator + args[1].substring(args[1].lastIndexOf(File.separator) + 1),
					predicateInfoList);
		} catch (FormatterException formatterException) {
			System.out.println("Error formatting the code.");
		}
	}
}
