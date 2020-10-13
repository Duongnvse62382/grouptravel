package com.fpt.gta.algorithm.binpacking2;

import com.fpt.gta.algorithm.suggest.Record;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A Bin holding record.
 * <br/>
 * The number of items it can hold is not limited, but the added value of the
 * items it holds may not be higher than the given maximal size.
 */
@Data
@NoArgsConstructor
public class Bin {

    /**
     * maximal allowed added value of items.
     */
    private int maxSize;
    /**
     * current added value of items.
     */
    private int currentSize;
    /**
     * list of items in bin.
     */
    private List<Record> items;

    public int getSumOfTime() {
        return items.stream().mapToInt(r -> r.getTimeSpent()).sum();
    }

    /**
     * construct new bin with given maximal size.
     *
     * @param maxSize
     */
    public Bin(int maxSize) {
        this.maxSize = maxSize;
        this.currentSize = 0;
        this.items = new ArrayList<>();
    }

    /**
     * adds given item to this bin, and increases the currentSize of the bin by
     * value of item. If item does not fit, it will not be put in the bin and
     * false will be returned.
     *
     * @param item item to put in bin
     * @return true if item fit in bin, false otherwise
     */
    public boolean put(Record item) {
        if (currentSize + item.getTimeSpent() <= maxSize) {
            items.add(item);
            currentSize += item.getTimeSpent();
            return true;
        } else {
            return false; // item didn't fit
        }
    }

    /**
     * removes given item from bin and reduces the currentSize of the bin by
     * value of item.
     *
     * @param item item to remove from bin
     */
    public void remove(Record item) {
        items.remove(item);
        currentSize -= item.getTimeSpent();
    }

    /**
     * returns the number of items in this bin (NOT the added value of the
     * items).
     *
     * @return number of items in this bin
     */
    public int numberOfItems() {
        return items.size();
    }

    /**
     * creates a deep copy of this bin.
     *
     * @return deep copy of this bin
     */
    public Bin deepCopy() {
        Bin copy = new Bin(0);
        copy.items = new ArrayList<Record>(items); // Integers are not copied by reference
        copy.currentSize = currentSize;
        copy.maxSize = maxSize;
        return copy;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < items.size(); i++) {
            Record r = items.get(i);
            res += r.getName() + " " + r.getTimeSpent() + " ";
        }
        res += "    Size: " + currentSize + " (max: " + maxSize + ")";
        return res;
    }
}
