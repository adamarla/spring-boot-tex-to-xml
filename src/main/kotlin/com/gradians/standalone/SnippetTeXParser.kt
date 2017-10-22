package com.gradians.standalone

import java.io.File
import java.util.regex.Pattern

/**
 * Created by adamarla on 10/18/17.
 */

class SnippetTeXParser : TeXParser() {

    override fun parse(file: File): Asset {
        val text = file.readText()

        val newCommandMatcher = newCommandPattern.matcher(text)
        val customCommands: MutableList<String> = mutableListOf()
        while (newCommandMatcher.find())
            customCommands.add(newCommandMatcher.group(1))

        val statementMatcher = statementPattern.matcher(text)
        val statement = if (statementMatcher.find())
                    if (statementMatcher.group(1).trim().contains("incorrect"))
                        Pair(convert(statementMatcher.group(2).trim()), null)
                    else
                        Pair(null, convert(statementMatcher.group(2).trim()))
                else Pair(null, null)

        val reasonMatcher = reasonPattern.matcher(text)
        val studyNote = if (reasonMatcher.find()) convert(reasonMatcher.group(1))
        else "could not convert ${file.path}"

        val step = Step(statement.first, statement.second, studyNote)
        return Snippet(file.parentFile.name.toInt(), "snippets/${file.parent}", step, customCommands)
    }

    companion object {
        private val statementPattern = Pattern.compile("""(\\correct|\\incorrect)(.*)\\reason""", Pattern.DOTALL)
        private val reasonPattern = Pattern.compile("""\\reason(.*)\\end\{snippet\}""", Pattern.DOTALL)
    }

}