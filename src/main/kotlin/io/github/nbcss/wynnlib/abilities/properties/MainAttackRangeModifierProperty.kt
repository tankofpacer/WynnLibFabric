package io.github.nbcss.wynnlib.abilities.properties

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.i18n.Translations
import io.github.nbcss.wynnlib.utils.Symbol
import io.github.nbcss.wynnlib.utils.removeDecimal
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class MainAttackRangeModifierProperty(ability: Ability, data: JsonElement): AbilityProperty(ability) {
    companion object: Factory {
        override fun create(ability: Ability, data: JsonElement): AbilityProperty {
            return MainAttackRangeModifierProperty(ability, data)
        }
        override fun getKey(): String = "main_range_modifier"
    }
    private val modifier: Double = data.asDouble

    fun getMainAttackRangeModifier(): Double = modifier

    override fun getTooltip(): List<Text> {
        val value = (if(modifier <= 1) Translations.TOOLTIP_SUFFIX_BLOCK else Translations.TOOLTIP_SUFFIX_BLOCKS)
            .formatted(Formatting.WHITE, null, (if (modifier > 0) "+" else "") + removeDecimal(modifier))
        return listOf(Symbol.RANGE.asText().append(" ")
            .append(Translations.TOOLTIP_ABILITY_MAIN_ATTACK_RANGE.formatted(Formatting.GRAY).append(": "))
            .append(value))
    }
}