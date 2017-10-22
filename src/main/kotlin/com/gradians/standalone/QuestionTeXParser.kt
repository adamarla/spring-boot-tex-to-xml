package com.gradians.standalone

import java.io.File
import java.util.regex.Pattern

/**
 * Created by adamarla on 10/19/17.
 */
class QuestionTeXParser: TeXParser() {

    override fun parse(file: File): Asset {
        return if (file.resolveSibling("source.xml").exists()) parseXmlTeX(file)
        else parseTeXFile(file)
    }

    companion object {
        val statementPattern = Pattern.compile("""\\statement(.*?)\\begin\{step\}""", Pattern.DOTALL)
        val stepPattern = Pattern.compile("""\\begin\{step\}(.*?)\\end\{step\}""", Pattern.DOTALL)
        val correctPattern = Pattern.compile("""\\correct(.*)(\\incorrect|\\end\{options\})""", Pattern.DOTALL)
        val incorrectPattern = Pattern.compile("""\\incorrect(.*)(\\correct|\\end\{options\})""", Pattern.DOTALL)
        val reasonPattern = Pattern.compile("""\\reason(.*)$""", Pattern.DOTALL)
        val documentPattern = Pattern.compile("""\\begin\{document\}(.*?)\\end\{document\}""", Pattern.DOTALL)
        val cardPattern = Pattern.compile("""\\newcard""")
    }

    private fun parseTeXFile(file: File): Question {
        val text = file.readText()

        val customCommands = extractCustomCommands(text)

        val statementMatcher = statementPattern.matcher(text)
        val statement = if (statementMatcher.find()) statementMatcher.group(1).trim() else ""

        val steps: MutableList<Step> = mutableListOf()
        val stepMatcher = stepPattern.matcher(text)
        while (stepMatcher.find()) {
            val stepTeX = stepMatcher.group(1)
            val correctMatcher = correctPattern.matcher(stepTeX)
            val incorrectMatcher = incorrectPattern.matcher(stepTeX)
            val reasonMatcher = reasonPattern.matcher(stepTeX)
            steps.add(Step(if (correctMatcher.find()) convert(correctMatcher.group(1)) else null,
                    if (incorrectMatcher.find()) convert(incorrectMatcher.group(1)) else null,
                    if (reasonMatcher.find()) convert(reasonMatcher.group(1)) else ""))
        }
        return Question(file.parentFile.name.toInt(), "q/1/${file.parentFile.name}", statement, steps, customCommands)
    }

    private fun parseXmlTeX(file: File): HybridQuestion {
        val text = file.readText()
        val customCommands = extractCustomCommands(text)

        val documentMatcher = documentPattern.matcher(text)
        var document = if (documentMatcher.find()) documentMatcher.group(1).trim() else ""
        document = documentPattern.matcher(document).replaceAll("")

        val cards = document.split(cardPattern)
        return HybridQuestion(file.parentFile.name.toInt(), "q/1/${file.parentFile.name}", cards, customCommands)
    }

    private fun extractCustomCommands(text: String): MutableList<String> {
        val newCommandMatcher = newCommandPattern.matcher(text)
        val customCommands: MutableList<String> = mutableListOf()
        while (newCommandMatcher.find())
            customCommands.add(newCommandMatcher.group(1))
        return customCommands
    }


}