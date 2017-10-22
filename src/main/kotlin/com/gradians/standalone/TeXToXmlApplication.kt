package com.gradians.standalone

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@SpringBootApplication
class TexToXmlApplication: CommandLineRunner {

    @Autowired
    lateinit var xmlProcessor: XMLProcessor

    @Autowired
    lateinit var teXProcessor: TeXProcessor

    val bankieTheBank = object: BankieTheBank {
        val workDir = File("/home/adamarla/work")
        override fun locateVault() = workDir.resolve("alt-bank")
        override fun locateSkills() = locateVault().resolve("skills")
        override fun locateSnippets() = locateVault().resolve("snippets")
        override fun locateQ1s() = locateVault().resolve("q/1")
        override fun locateQ2s() = locateVault().resolve("q/2")
    }

    override fun run(vararg args: String?) {
        listOf(bankieTheBank.locateVault()).map {
            it.walk().filter { it.name == "source.tex" }.map {
                it.resolveSibling("source.tmp").writeText(xmlProcessor.process(teXProcessor.process(it)))
            }.toList()
        }
    }

}

@Configuration
class ComponentFactory {

    @Bean
    fun xmlProcessor(): XMLProcessor = XMLProcessor()

    @Bean
    fun teXProcessor(): TeXProcessor = TeXProcessor()

}

fun main(args: Array<String>) {
    SpringApplication.run(TexToXmlApplication::class.java, *args)
}
