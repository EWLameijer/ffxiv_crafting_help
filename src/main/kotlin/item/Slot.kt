package item

// NOTE: For convenience, weapons are generally classed as their slot: main hand. Of course, there are
// dual-wielders, like rogues, but generally each class equips one weapon. Exceptions: THM/BLM, CNJ/WHM:
// can either have one-handed weapon or two-handed; T is reserved for them.
// Stockings (S) = Feet + Leg
enum class Slot(val abbreviation: Char) {
    Hands('A'), Body('B'), Cowl('C'), Earrings('E'), Feet('F'),
    Head('H'), Legs('L'), MainHand('M'), Neck('N'),
    OffHand('O'), Ring('R'), Stockings('S'), TwoHand('T'), Wrists('W');
}

// while most gear can be worn by any job that can wear that armor type, and you have to check stats
// to see whether it is suitable for a job, the hand slots are special as they generally very strongly restrict
// the jobs which can use the gear. Generally only one job (like PGL/MNK), at most three (BLM, WHM, GLA) for shields
val primarySlots = setOf(Slot.MainHand, Slot.TwoHand, Slot.OffHand)

