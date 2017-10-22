package com.gradians.standalone

import java.io.File
import java.util.regex.Pattern

/**
 * Created by adamarla on 10/19/17.
 */

abstract class TeXParser {

    abstract fun parse(file: File): Asset

    protected fun convert(latex: String): String {
        val lines = latex.split('\n')
        val tex = StringBuilder()
        var autoLineBreak = true
        tex.append("%text\n") // start text-mode
        for (line in lines.map { it.trim() }) {
            if (line.startsWith("%text") || line == "%")
                continue
            else if (line.startsWith("\\correct") || line.startsWith("\\incorrect")) {
                autoLineBreak = false
            }

            if (line.contains("\\begin")) {
                if (line.startsWith("\\begin{itemize}")) {
                    autoLineBreak = false
                    continue
                }

                if (line.startsWith("\\begin{align}") || line.startsWith("\\begin{center}") ||
                        line.contains("\\begin{cases}")) {
                    tex.append("\n%\n") // end text-mode
                    autoLineBreak = false
                }
                tex.append(line).append("\n")
            } else if (line.contains("\\end")) {
                if (line.startsWith("\\end{itemize}")) {
                    autoLineBreak = true
                    continue
                }

                if (line.startsWith("\\end{align}") || line.startsWith("\\end{center}") ||
                        line.contains("\\end{cases}")) {
                    tex.append("\n").append(line).append("\n") // end math-mode
                    tex.append("%text\n") // resume text-mode
                    autoLineBreak = true
                } else {
                    tex.append("\n").append(line)
                }
            } else if (line.startsWith("\\[")) {
                tex.append("\n").append(line).append("\n")
            } else if (!line.isEmpty()) {
                tex.append(if (autoLineBreak) " " else "\n").append(line)
            }
        }
        tex.append("%\n") // end text-mode
        return replace(tex.toString())
    }

    private fun replace(impureTex: String): String {
        return replacements.keys.fold(impureTex, { tex, regex ->
            tex.replace(regex, replacements.get(regex).toString())
        })
    }

    companion object {
        val newCommandPattern = Pattern.compile("""\\newcommand(.*)$""")
        val replacements = mapOf(Regex("\\\\item *\\{(.*)\\}") to " - $1",
                Regex("\\\\textbf\\{(.*)\\}") to "\\\\textbf\\{$1 \\}",
                Regex("\\\\smallmath") to "",
                Regex("\\\\underline") to "\\\\underline\\\\text",
                Regex("\\\\newline") to "\n",
                Regex("\\\\toprule") to "\\\\hline",
                Regex("\\\\midrule") to "\\\\hline",
                Regex("\\\\bottomrule") to "\\\\hline",
                Regex("\\{center\\}") to "\\{align\\}")
    }

}