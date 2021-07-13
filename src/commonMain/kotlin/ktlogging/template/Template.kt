package ktlogging.template

data class Templated(
    val template: String,
    val evaluated: String,
    val items: Map<String, String>,
)

fun template(template: String, vararg items: Any): Templated {
    val textsAndHoles = extractHoles(template)
    var itemIdx = 0
    val itemMap = mutableMapOf<String, String>()
    val extracted = textsAndHoles.fold("") { acc, pair ->
        acc + if (pair.first == TextOrHole.HOLE) {
            if (itemIdx < items.size) {
                val evaluated = items[itemIdx++].toString()
                itemMap[pair.second] = evaluated
                evaluated
            } else "{${pair.second}}"
        } else pair.second
    }
    return Templated(
        template,
        extracted,
        itemMap.toMap(),
    )
}

enum class TextOrHole { TEXT, HOLE }

fun extractHoles(template: String): List<Pair<TextOrHole, String>> {
    val textsAndHoles = mutableListOf<Pair<TextOrHole, String>>()
    var state = TextOrHole.TEXT
    var holeStart = 0
    var textStart = 0
    template.forEachIndexed { i, c ->
        when (c) {
            '{' -> if (state == TextOrHole.TEXT) {
                holeStart = i
                if (i > textStart)
                    textsAndHoles.add(TextOrHole.TEXT to template.substring(textStart, i))
                state = TextOrHole.HOLE
            }
            '}' -> if (state == TextOrHole.HOLE) {
                textStart = i + 1
                if (i - holeStart > 1)
                    textsAndHoles.add(TextOrHole.HOLE to template.substring(holeStart + 1, i))
                state = TextOrHole.TEXT
            }
        }
    }
    if (state == TextOrHole.TEXT && template.length > textStart)
        textsAndHoles.add(TextOrHole.TEXT to template.substring(textStart, template.length))

    return textsAndHoles.toList()
}
