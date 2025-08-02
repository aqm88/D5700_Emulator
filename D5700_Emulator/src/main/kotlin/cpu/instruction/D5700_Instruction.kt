package org.example.cpu.instruction

class D5700_Instruction(parts:Array<Int>): Instruction(parts) {
    init {
        require(parts.size == 4) {
            "D5700_Instruction must be initialized with exactly 4 parts, but got ${parts.size}"
        }
    }
}