package dev.drzepka.typing.server.application.util

import io.ktor.application.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.getRequiredIntParam(name: String): Int {
    val param = call.parameters[name] ?: throw IllegalArgumentException("Parameter $name wasn't found")
    return param.toInt()
}