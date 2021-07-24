package io.klogging.template

public fun templateItems(template: String, vararg values: Any?): Map<String, Any?> {
    val itemNames = extractItemNames(template)
    return itemNames.zip(values).toMap()
}

private enum class TextOrHole { TEXT, HOLE }

public fun extractItemNames(template: String): List<String> {
    val itemNames = mutableListOf<String>()
    var state = TextOrHole.TEXT
    var holeStart = 0
    template.forEachIndexed { i, c ->
        when (c) {
            '{' -> if (state == TextOrHole.TEXT) {
                holeStart = i
                state = TextOrHole.HOLE
            }
            '}' -> if (state == TextOrHole.HOLE) {
                if (i - holeStart > 1) itemNames.add(template.substring(holeStart + 1, i))
                state = TextOrHole.TEXT
            }
        }
    }
    return itemNames
}
