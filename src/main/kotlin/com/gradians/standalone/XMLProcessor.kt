package com.gradians.standalone

import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Created by adamarla on 10/5/17.
 */

class XMLProcessor {

    fun process(asset: Asset) = serializeDOM(createDOM(asset))

    private fun createDOM(asset: Asset): Document {
        val template = when (asset) {
            is Skill -> SKILL
            is Question -> QUESTION
            is Snippet -> SNIPPET
            is HybridQuestion -> {
                val path = "/home/adamarla/work/alt-bank/${asset.path}/source.xml"
                println(path)
                File(path).readText()
            }
            else -> ""
        }

        val xmlDom = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                parse(InputSource(StringReader(template.trimMargin())))

        when (asset) {
            is Skill -> {
                val texs = xmlDom.getElementsByTagName("tex")
                texs.item(0).textContent = "\\title{${asset.title.first}}\n${asset.title.second}"
                texs.item(1).textContent = asset.studyNote
            }
            is Snippet -> {
                val customCommands = xmlDom.getElementsByTagName("customCommands").item(0)
                asset.customCommands.map {
                    val command = xmlDom.createElement("command")
                    command.appendChild(xmlDom.createTextNode(it))
                    customCommands.appendChild(command)
                }

                val texs = xmlDom.getElementsByTagName("tex")
                texs.item(0).textContent = asset.step.correct ?: asset.step.incorrect
                texs.item(1).textContent = asset.step.reason
            }
            is Question -> {
                val customCommands = xmlDom.getElementsByTagName("customCommands").item(0)
                asset.customCommands.map {
                    val command = xmlDom.createElement("command")
                    command.appendChild(xmlDom.createTextNode(it))
                    customCommands.appendChild(command)
                }

                val statementTex = xmlDom.getElementsByTagName("tex").item(0)
                statementTex.textContent = asset.statement

                val stepsTag = xmlDom.getElementsByTagName("steps").item(0)
                asset.steps.map { step ->
                    val stepTag = xmlDom.createElement("step")
                    val optionsTag = xmlDom.createElement("options")
                    listOf(step.correct, step.incorrect).map { option ->
                        if (!option.isNullOrBlank()) {
                            val optionTag = xmlDom.createElement("tex")
                            optionTag.appendChild(xmlDom.createTextNode(option))
                            if (option == step.incorrect) {
                                optionTag.setAttribute("correct", "false")
                            }
                            optionsTag.appendChild(optionTag)
                        }
                    }
                    stepTag.appendChild(optionsTag)
                    val reasonTag = xmlDom.createElement("reason")
                    val texTag = xmlDom.createElement("tex")
                    texTag.appendChild(xmlDom.createTextNode(step.reason))
                    reasonTag.appendChild(texTag)
                    stepTag.appendChild(reasonTag) // </step>
                    stepsTag.appendChild(stepTag) // </steps>
                }
            }
            is HybridQuestion -> {
                val customCommandsTag = xmlDom.createElement("customCommands")
                asset.customCommands.map {
                    val command = xmlDom.createElement("command")
                    command.appendChild(xmlDom.createTextNode(it))
                    customCommandsTag.appendChild(command)
                }

                val statementTag = xmlDom.getElementsByTagName("statement").item(0)
                xmlDom.firstChild.insertBefore(customCommandsTag, statementTag)

                val texs = xmlDom.getElementsByTagName("tex")
                for (i in 0 until texs.length) {
                    texs.item(i).textContent = asset.texs[i]
                    texs.item(i).attributes.removeNamedItem("isImage")
                }
            }
        }
        return xmlDom
    }

    private fun serializeDOM(dom: Document): String {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.outputProperties = XML_OUT_PROPS

        val writer = StringWriter()
        transformer.transform(DOMSource(dom), StreamResult(writer))
        return writer.toString()
    }

    companion object {
        private val XML_OUT_PROPS = mapOf<String, String>(
                "{http://xml.apache.org/xslt}indent-amount" to "2",
                OutputKeys.INDENT to "yes", OutputKeys.METHOD to "xml")
                .toProperties()

        private const val SKILL = """<?xml version="1.0" encoding="utf-8"?>
            |<skill xmlns="http://www.gradians.com">
            |  <render><tex/></render><reason><tex/></reason>
            |</skill>"""

        private const val SNIPPET = """<?xml version="1.0" encoding="utf-8"?>
            <snippet xmlns="http://www.gradians.com">
                <customCommands/>
                <render><tex/></render><reason><tex/></reason>
            </snippet>"""

        private const val QUESTION = """|<?xml version="1.0" encoding="utf-8"?>
            |<question xmlns="http://www.gradians.com">
            |  <customCommands />
            |  <statement><tex/></statement>
            |  <steps/>
            |</question>"""

    }

}

