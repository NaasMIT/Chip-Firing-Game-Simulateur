package core;

import java.util.Set;

public class AddChipOp implements IChipOperation {

    @Override
    public int compute(int nbToApply, int nodeCurrentChips) {
        return nbToApply + nodeCurrentChips;
    }
}
