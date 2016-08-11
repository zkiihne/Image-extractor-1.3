package Main;

import java.util.Comparator;

import sorters.AbstractSorter;
import structures.SwapList;

public class QuickSorter<T> extends AbstractSorter<T> {

	public QuickSorter(SwapList<T> list, Comparator<T> comparator) {
		super(list, comparator);
	}

	@Override
	public SwapList<T> sort() {
		// TODO
		
		/*
		 * Note: When choosing a pivot, choose the element in the middle of
		 * the sub-array. That is,
		 * 
		 * pivotIndex = (firstIndex + lastIndex) / 2;
		 */
		quickSort();
		return list;
	}
	private void quickSort() {
		 qs( 0, list.size() - 1);
	}
	
	private void qs(int firstIndex,int lastIndex) {
		 if (firstIndex < lastIndex) {
			 
		 int splitPoint = split( firstIndex, lastIndex);
		 qs( firstIndex, splitPoint - 1);
		 qs( splitPoint + 1, lastIndex);
		 
		 }
	}
	
	private int split(int firstIndex,int lastIndex) {
		 // note about choice
		 int splitIndex = (firstIndex + lastIndex )/ 2;

		 // move splitValue out of the way
		 list.swap(splitIndex, lastIndex);

		 // move all values smaller than the splitValue to the
		 // front of the array
		 int nextSwapIndex = firstIndex;
		 for (int i = firstIndex; i < lastIndex; i++) {
			 
		 if (list.compare(i, lastIndex, comparator) <= 0) {
		 list.swap(i, nextSwapIndex);
		 nextSwapIndex++;
		 }
		 
		 }

		 // return the splitValue to the space right after the
		 // values smaller than it
		 list.swap( nextSwapIndex, lastIndex);

		 // and tell the caller where the splitValue ended up
		 return nextSwapIndex;
	}
	
}