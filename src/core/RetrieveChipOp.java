package core;

import core.IChipOperation;

public class RetrieveChipOp implements IChipOperation {

    @Override
    public int compute(int nbToApply, int nodeCurrentChips) {
        return nodeCurrentChips - nbToApply;
    }
}
