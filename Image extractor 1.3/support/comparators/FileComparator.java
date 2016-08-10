package comparators;

import java.util.Comparator;
import java.io.File;
/**
 * Comparator on two Integers in the usual order.
 * 
 * @author liberato
 *
 */
public class FileComparator implements Comparator<File> {
	@Override
	public int compare(File o1, File o2) {
		String path1 = o1.getName();
		String path2 = o2.getName();
		String numString1 = path1.substring(13,18).replace(".", "0");
		String numString2 = path2.substring(13,18).replace(".", "0");
		
		Integer num1 = Integer.parseInt(numString1);
		Integer num2 = Integer.parseInt(numString2);
		
		return num1.compareTo(num2);
	}
}
