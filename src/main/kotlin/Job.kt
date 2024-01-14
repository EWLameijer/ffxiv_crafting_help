import item.Stat
import item.Stat.*

/*
The dilemma
Each job is unique, you want only one of each job object.
Easiest would be to use enum for jobs (as for gear slots), but "job is CraftingJob" _is_ attractive, but you cannot
assign different interfaces (let alone classes) to members of the same enum. So I choose objects though it is more
typing and duplicates data since I also need a collection I can easily walk through.

But perhaps I or someone else will have a cleverer idea one day :)
*/

sealed class Job(
    val abbreviation: String, // "CNJ", "PGL" etc.
    val mainStats: Set<Stat>,  // absolute priority. Always choose the item with the higher main stats
    val supportingStats: Set<Stat> // for comparing gear for classes that do not truly have main stats
    // , or when the main stats are equal
) {
    // if gear is suitable for GLA, it is also suitable for PAL.
    // note that a majority of jobs (crafting jobs, gathering jobs, newer jobs like samurai or blue mage) do
    // not have any descendants, making setOf(this) a decent default value.
    open fun withDescendants() = setOf(this)

    fun relevantStats() = mainStats + supportingStats
}

sealed class ProducingJob(abbreviation: String, supportingStats: Set<Stat>) :
    Job(abbreviation, setOf(), supportingStats)

sealed class CraftingJob(abbreviation: String) : ProducingJob(abbreviation, setOf(Control, CP, Craftmanship))

sealed class GatheringJob(abbreviation: String) : ProducingJob(abbreviation, setOf(Gathering, GP, Perception))

sealed class AdventuringJob(
    abbreviation: String,
    mainStat: Stat,
    supportingStats: Set<Stat>,
    private val descendants: Set<AdventuringJob>
) :
    Job(abbreviation, setOf(mainStat, Vitality), supportingStats) {
    override fun withDescendants() = setOf(this, *this.descendants.toTypedArray())
}

sealed class MagicJob(abbreviation: String, mainStat: Stat, supportingStats: Set<Stat>, descendants: Set<MagicJob>) :
    AdventuringJob(abbreviation, mainStat, supportingStats, descendants)

sealed class HealingJob(abbreviation: String, descendants: Set<MagicJob> = setOf()) :
    MagicJob(abbreviation, Mind, setOf(Piety), descendants)

sealed class CastingJob(abbreviation: String, descendants: Set<MagicJob> = setOf()) :
    MagicJob(abbreviation, Intelligence, setOf(), descendants)

interface LeatherWearing

sealed class FightingJob(
    abbreviation: String,
    mainStat: Stat,
    supportingStats: Set<Stat> = setOf(),
    descendants: Set<FightingJob> = setOf()
) :
    AdventuringJob(abbreviation, mainStat, supportingStats, descendants), LeatherWearing

interface MailWearing : LeatherWearing

interface PlateWearing : MailWearing

sealed class DexterityJob(abbreviation: String, descendants: Set<DexterityJob> = setOf()) :
    FightingJob(abbreviation, Dexterity, setOf(), descendants)

sealed class StrengthJob(abbreviation: String, descendants: Set<StrengthJob> = setOf()) :
    FightingJob(abbreviation, Strength, setOf(), descendants)

sealed class TankingJob(abbreviation: String, descendants: Set<TankingJob> = setOf()) :
    FightingJob(abbreviation, Strength, setOf(Tenacity, Defense), descendants),
    PlateWearing

data object Alchemist : CraftingJob("ALC")
data object Archer : DexterityJob("ARC")
data object Armorer : CraftingJob("ARM")
data object Astrologian : HealingJob("AST")
data object Blacksmith : CraftingJob("BSM")
data object Botanist : GatheringJob("BTN")
data object Carpenter : CraftingJob("CRP")
data object Culinarian : CraftingJob("CUL")
data object Dancer : DexterityJob("DNC")
data object DarkKnight : TankingJob("DRK")
data object Fisher : GatheringJob("FSH")
data object Goldsmith : CraftingJob("GSM")
data object Lancer : StrengthJob("LNC"), MailWearing
data object Leatherworker : CraftingJob("LTW")
data object Machinist : DexterityJob("MCH")
data object Miner : GatheringJob("MIN")
data object Marauder : TankingJob("MRD")
data object Ninja : DexterityJob("NIN")
data object Paladin : TankingJob("PLD")
data object Pugilist : StrengthJob("PGL")
data object RedMage : CastingJob("RDM")
data object Samurai : StrengthJob("SAM")
data object Scholar : HealingJob("SCH")
data object Summoner : CastingJob("SMN")
data object Thaumaturge : CastingJob("THM")
data object Weaver : CraftingJob("WVR")
data object WhiteMage : HealingJob("WHM")
data object Arcanist : CastingJob("ACN", setOf(Scholar, Summoner))
data object Conjurer : HealingJob("CNJ", setOf(WhiteMage))
data object Gladiator : TankingJob("GLA", setOf(Paladin))
data object Rogue : DexterityJob("ROG", setOf(Ninja))

val allJobs = setOf(
    Alchemist, Arcanist, Archer, Armorer, Astrologian, Blacksmith, Botanist, Carpenter, Conjurer, Culinarian,
    Dancer, DarkKnight, Fisher, Gladiator, Goldsmith, Lancer, Leatherworker, Machinist, Miner, Marauder, Ninja, Paladin,
    Pugilist, RedMage, Rogue, Samurai, Scholar, Summoner, Thaumaturge, Weaver, WhiteMage
)

fun String.toJobOrNull() = allJobs.firstOrNull { it.abbreviation == this }

private inline fun <reified T> getJobsOfType() = allJobs.filter { it is T }.toSet()

val jobRestriction: Map<String, Set<Job>> = (allJobs.associateBy({ it.abbreviation }, { setOf(it) }) + mapOf(
    "A" to setOf(Archer, Dancer, Machinist), // "Aiming" gear
    "B" to setOf(Gladiator, Thaumaturge), // Black (after black mages, who can use shields)
    "L" to getJobsOfType<LeatherWearing>(), // Leather
    "M" to getJobsOfType<MailWearing>(), // Mail
    "N" to allJobs.toSet(), // None - can be worn by any job
    "P" to getJobsOfType<PlateWearing>(), // Plate
    "S" to setOf(Gladiator, Conjurer, Thaumaturge), // Shield-capable
    "T" to setOf(Pugilist, Samurai), // sTriking gear
    "W" to setOf(Gladiator, Conjurer) // White (after white mages, who can also use shields)
)).mapValues { it.value.flatMap { value -> value.withDescendants() }.toSet() }



