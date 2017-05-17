package se.kth.growonlyset;

/**
 * Created by mikael on 2017-05-17.
 */
public class TwoPhaseSet {
    private GSet set;
    private GSet tombstone;

    public void add(Object element) {
        if(!set.contains(element)) {
            set.add(element);
        }
    }

    public void remove(Object element) {
        if(set.contains(element) && !tombstone.contains(element)) {
            tombstone.add(element);
        }
    }

    public boolean contains(Object element) {
        if(set.contains(element) && !tombstone.contains(element)) {
            return true;
        }
        return false;
    }

    public String print() {
        return set.print();
    }
}
