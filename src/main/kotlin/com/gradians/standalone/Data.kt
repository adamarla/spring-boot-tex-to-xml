package com.gradians.standalone

/**
 * Created by adamarla on 10/4/17.
 */
abstract class Asset(id: Int, path: String)

data class Skill(val id: Int, val path: String, val title: Pair<String, String>, val studyNote: String):
        Asset(id, path)

data class Snippet(val id: Int, val path: String, val step: Step,
                   val customCommands: List<String>): Asset(id, path)

data class Question(val id: Int, val path: String, val statement: String, val steps: List<Step>,
                    val customCommands: List<String>): Asset(id, path)

data class HybridQuestion(val id: Int, val path: String, val texs: List<String>,
                  val customCommands: List<String>): Asset(id, path)

data class Step(val correct: String? = null, val incorrect: String? = null, val reason: String)
