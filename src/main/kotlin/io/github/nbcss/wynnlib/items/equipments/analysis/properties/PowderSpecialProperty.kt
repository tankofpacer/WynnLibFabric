package io.github.nbcss.wynnlib.items.equipments.analysis.properties

import io.github.nbcss.wynnlib.data.PowderSpecial
import net.minecraft.text.Text
import java.util.regex.Pattern

class PowderSpecialProperty: AnalysisProperty {
    companion object {
        private val SPEC_NAME_PATTERN = Pattern.compile(" {3}(.+)")
        private val DURATION_PATTERN = Pattern.compile("Duration: (\\d+\\.?\\d*)")
        private val DAMAGE_PATTERN = Pattern.compile("Damage: (\\+?\\d+\\.?\\d*)")
        private val BOOST_PATTERN = Pattern.compile("Damage Boost: \\+(\\d+\\.?\\d*)")
        private val RADIUS_PATTERN = Pattern.compile("Radius: (\\d+\\.?\\d*)")
        private val CHAINS_PATTERN = Pattern.compile("Chains: (\\d+\\.?\\d*)")
        private val KNOCKBACK_PATTERN = Pattern.compile("Knockback: (\\d+\\.?\\d*)")
        private val FACTORY_MAP: Map<String, SpecFactory> = mapOf(
            pairs = listOf(
                QuakeSpec,
                RageSpec,
                ChainLightningSpec,
                KillStreakSpec,
                CourageSpec,
                EnduranceSpec,
                CurseSpec,
                ConcentrationSpec,
                WindPrisonSpec,
                DodgeSpec,
            ).map { it.name to it }.toTypedArray()
        )

        private fun toDataString(text: Text): String? {
            if (text.siblings.isEmpty())
                return null
            val base = text.siblings[0]
            if (base.siblings.size != 2)
                return null
            return base.siblings[1].asString()
        }

        private fun readValue(text: Text, pattern: Pattern): Double {
            toDataString(text)?.let {
                val matcher = pattern.matcher(it)
                if(matcher.find()) {
                    return matcher.group(1).toDouble()
                }
            }
            return 0.0
        }
        const val KEY = "POWDER_SPEC"
    }
    private var powderSpec: PowderSpecial? = null

    fun getPowderSpecial(): PowderSpecial? = powderSpec

    override fun set(tooltip: List<Text>, line: Int): Int {
        if (tooltip[line].siblings.isEmpty())
            return 0
        val base = tooltip[line].siblings[0]
        val matcher = SPEC_NAME_PATTERN.matcher(base.asString())
        if(matcher.find()){
            FACTORY_MAP[matcher.group(1)]?.let { factory ->
                val result = factory.read(tooltip, line + 1)
                powderSpec = result.first
                return 1 + result.second
            }
        }
        return 0
    }

    override fun getKey(): String = KEY

    interface SpecFactory {
        val name: String
        fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int>
    }

    object QuakeSpec : SpecFactory {
        override val name: String = "Quake"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 2 > tooltip.size)
                return null to 0
            val radius = readValue(tooltip[line], RADIUS_PATTERN)
            val damage = readValue(tooltip[line + 1], DAMAGE_PATTERN)
            return PowderSpecial.Quake(radius, damage.toInt(), 0) to 2
        }
    }

    object RageSpec : SpecFactory {
        override val name: String = "Rage [% ❤ Missing]"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 1 > tooltip.size)
                return null to 0
            val damage = readValue(tooltip[line], DAMAGE_PATTERN)
            return PowderSpecial.Rage(damage, 0) to 1
        }
    }

    object ChainLightningSpec : SpecFactory {
        override val name: String = "Chain Lightning"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 2 > tooltip.size)
                return null to 0
            val chains = readValue(tooltip[line], CHAINS_PATTERN)
            val damage = readValue(tooltip[line + 1], DAMAGE_PATTERN)
            return PowderSpecial.ChainLightning(chains.toInt(), damage.toInt(), 0) to 2
        }
    }

    object KillStreakSpec : SpecFactory {
        override val name: String = "Kill Streak [Mob Killed]"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 2 > tooltip.size)
                return null to 0
            val damage = readValue(tooltip[line], DAMAGE_PATTERN)
            val duration = readValue(tooltip[line + 1], DURATION_PATTERN)
            return PowderSpecial.KillStreak(damage, duration, 0) to 2
        }
    }

    object CourageSpec : SpecFactory {
        override val name: String = "Courage"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 3 > tooltip.size)
                return null to 0
            val duration = readValue(tooltip[line], DURATION_PATTERN)
            val damage = readValue(tooltip[line + 1], DAMAGE_PATTERN)
            val boost = readValue(tooltip[line + 2], BOOST_PATTERN)
            return PowderSpecial.Courage(duration, damage, boost, 0) to 3
        }
    }

    object EnduranceSpec : SpecFactory {
        override val name: String = "Endurance [Hit Taken]"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 2 > tooltip.size)
                return null to 0
            val damage = readValue(tooltip[line], DAMAGE_PATTERN)
            val duration = readValue(tooltip[line + 1], DURATION_PATTERN)
            return PowderSpecial.Endurance(damage, duration, 0) to 2
        }
    }

    object CurseSpec : SpecFactory {
        override val name: String = "Curse"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 2 > tooltip.size)
                return null to 0
            val duration = readValue(tooltip[line], DURATION_PATTERN)
            val boost = readValue(tooltip[line + 1], BOOST_PATTERN)
            return PowderSpecial.Curse(duration, boost, 0) to 2
        }
    }

    object ConcentrationSpec : SpecFactory {
        override val name: String = "Concentration [Mana Used]"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 2 > tooltip.size)
                return null to 0
            val damage = readValue(tooltip[line], DAMAGE_PATTERN)
            val duration = readValue(tooltip[line + 1], DURATION_PATTERN)
            return PowderSpecial.Concentration(damage, duration, 0) to 2
        }
    }

    object WindPrisonSpec : SpecFactory {
        override val name: String = "Wind Prison"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 3 > tooltip.size)
                return null to 0
            val duration = readValue(tooltip[line], DURATION_PATTERN)
            val boost = readValue(tooltip[line + 1], BOOST_PATTERN)
            val knockback = readValue(tooltip[line + 2], KNOCKBACK_PATTERN)
            return PowderSpecial.WindPrison(duration, boost, knockback.toInt(), 0) to 3
        }
    }

    object DodgeSpec : SpecFactory {
        override val name: String = "Dodge [Near Mobs]"
        override fun read(tooltip: List<Text>, line: Int): Pair<PowderSpecial?, Int> {
            if (line + 2 > tooltip.size)
                return null to 0
            val damage = readValue(tooltip[line], DAMAGE_PATTERN)
            val duration = readValue(tooltip[line + 1], DURATION_PATTERN)
            return PowderSpecial.Dodge(damage, duration, 0) to 2
        }
    }
}