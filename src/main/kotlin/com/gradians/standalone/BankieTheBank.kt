package com.gradians.standalone

import java.io.File

/**
 * Created by adamarla on 10/5/17.
 */

interface BankieTheBank {

    fun locateVault(): File

    fun locateSkills(): File

    fun locateSnippets(): File

    fun locateQ1s(): File

    fun locateQ2s(): File

}