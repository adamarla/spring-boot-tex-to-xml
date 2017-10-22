package com.gradians.standalone

import java.io.File

/**
 * Created by adamarla on 10/17/17.
 */
class TeXProcessor {

    private val skillTeXParser = SkillTeXParser()
    private val snippetTeXParser = SnippetTeXParser()
    private val questionTeXParser = QuestionTeXParser()

    fun process(file: File): Asset {
        val parser = when (file.parentFile.parentFile.name) {
            "skills" -> skillTeXParser
            "snippets" -> snippetTeXParser
            else -> questionTeXParser
        }
        return parser.parse(file)
    }

}