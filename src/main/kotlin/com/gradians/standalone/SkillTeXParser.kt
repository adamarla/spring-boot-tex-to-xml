package com.gradians.standalone

import java.io.File
import java.util.regex.Pattern

/**
 * Created by adamarla on 10/4/17.
 */

class SkillTeXParser: TeXParser() {

    override fun parse(file: File): Asset {
        val text = file.readText()

        val newCommandMatcher = newCommandPattern.matcher(text)
        while (newCommandMatcher.find()) {
            newCommandMatcher.group(1)
        }

        val titleMatcher = titlePattern.matcher(text)
        val title = if (titleMatcher.find()) Pair(titleMatcher.group(1).trim(),
                titleMatcher.group(2).trim().replace(Regex("\n\\s*"), ""))
        else Pair("", "")

        val studyNoteMatcher = studyNotePattern.matcher(text)
        val studyNote = if (studyNoteMatcher.find()) convert(studyNoteMatcher.group(1))
        else "could not convert ${file.path}"

        return Skill(file.parentFile.name.toInt(), "skills/${file.parent}", title, studyNote)
    }

    companion object {
        val titlePattern = Pattern.compile("""\\begin\{narrow\}.*\\textcolor\{blue\}\{(.*)\}(.*)\\end\{narrow\}""",
                Pattern.DOTALL)
        val studyNotePattern = Pattern.compile("""\\reason(.*)\\end\{skill\}""", Pattern.DOTALL)
    }

}
