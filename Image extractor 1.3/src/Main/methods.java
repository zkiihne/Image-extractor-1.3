package Main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import com.idrsolutions.image.tiff.TiffDecoder;

import Main.QuickSorter;
import comparators.FileComparator;
import structures.SwapList;
import structures.ArrayBasedSwapList;

/**
 * 
 * @author zkiihne
 * @version 1.7 8/9/16
 * This is the main class of the program, all the functionality is contained within this class.
 */

public class methods extends Thread {

	Thread t;
	String name = "";
	int ratio;
	List<List<Integer>> pList;
	List<String> tFiles;
	String inp;
	String da;
	public static long LAST_TIME = 0;

	/**
	 * This is the start method for the Thread part of this class. It will begin a new thread when called
	 * @param n a variable used to tell where to start in the list, usually is 0
	 * @param pageList The list of pages 
	 * @param tiffFiles The addresses of the tif files to be cut up
	 * @param input The XML file to be parsed
	 * @param dat The address of the dat file
	 */
	public void start(int n, List<List<Integer>> pageList, List<String> tiffFiles, String input, String dat) {

		if (t == null) 
		{
			da = dat;
			inp = input;
			pList = pageList;
			tFiles = tiffFiles;
			ratio = n;
			this.start();
		}
	}
	/**
	 * This is the run method for the Thread. It is called with this.start() in the start method
	 */
	public void run() {
		
		//lastPage is set to -1 at the start so that it never equals the page on the first go around
		int lastPage = -1;
		int imgNum = 1;
		String address;
		String nameOfFile;
		RandomAccessFile raf;
		TiffDecoder decoder;
		BufferedImage decodedImage;
		int page = 0;

		if (pList == null)
		{
			//This is done just to make sure to skip over documents without any pictures in them
		} else {
			//Goes through each page in the list
			for (int i = 0; i < pList.size(); i += 1) {

				try {
					//Get the page number
					page = pList.get(i).get(0);
					page--;
					// This counts what image number it is on that particular page
					if (page != lastPage) {
						lastPage = page;
						imgNum = 1;
					} else {
						imgNum++;
					}

					// The address of the folder it should go to
					address = runner.IMAGES_ROOT + da.substring(0, 5) + "/" + da.substring(11, 15) + "/" + "600/";
					// The name of the file, <journal name>_<page number>_<Image number>_tmbn.tif
					nameOfFile = inp.substring(11, 30) + "_" + page + "_" + imgNum + "_tmbn";
					try {
						// The tif image
						raf = new RandomAccessFile(address + tFiles.get(page) + ".tif", "r");
						// Decodes the tif, turning it into a buffered image object
						decoder = new TiffDecoder(raf);
						decodedImage = decoder.read();
						raf.close();

						// This method will then cut out and save the image
						methods.cutImage(decodedImage, pList.get(i).get(1), pList.get(i).get(2), pList.get(i).get(3),
								pList.get(i).get(4), nameOfFile, address, true);

					} catch (Exception e) {

						try {
							// Sometimes black and white tif images will instead
							// be read as .000 images by windows, due to the
							// image name schema
							// This catch is meant to correct for this error
							// It does the same thing as the above, just without
							// adding .tif to the end of the file name
							raf = new RandomAccessFile(address + tFiles.get(page), "r");
							decoder = new TiffDecoder(raf);
							decodedImage = decoder.read();
							raf.close();
							methods.cutImage(decodedImage, pList.get(i).get(1), pList.get(i).get(2),
									pList.get(i).get(3), pList.get(i).get(4), nameOfFile, address, true);

						} catch (Exception d) {

							//These print statements are for debugging purposes
							//If an exception is caught here I highly recommend uncomenting them
							//System.out.println("Error: address :" + address + "   " + tFiles.get(i));
							//System.out.println("Error: tFiles size :" + tFiles.size());
							//System.out.println("Error: page size :" + page);
							d.printStackTrace();
						}
					}
					//This print statement is predominantly for the purpose of letting
					//the user know that the code is still working as well as how far along it is
					System.out.println(
							i + "     Tif file: " + page + "   " + tFiles.get(page--) + ".tif");
				} catch (Exception e) {
					writeToLog(e.getMessage(),"");
					e.printStackTrace();
				}

				if (this.isInterrupted()) {
					System.out.println("INTERRUPTED");
				}

			}
		}
	}

	/**
	 * @param input
	 *            the XML file to be parsed, formated like this: <publication
	 *            number>/<file name>
	 * @param dat
	 *            the DAT file that goes with the XML File, formated like this:
	 *            <publication number>/<file name>
	 */
	public static boolean run(String input, String dat) {

		System.out.println("Started: " + input);
		File inputFile = new File(runner.OCR_ROOT + input);// "XML/seri/"
		File datFile = new File(runner.METADATA_ROOT + dat);// "DAT/seri/"
		List<List<Integer>> pageList = methods.xmlParse(inputFile);
		// a list of pictures and their page numbers
		// the list contains the following data:
		//0:Page # 1:X coor 2:Y coor 3:Width 4:Height
		List<String> tiffFiles = methods.datParse(datFile, inputFile);
		//This starts the thread off, to run on its own.
		methods meth0 = new methods();
		meth0.start(0, pageList, tiffFiles, input, dat);
		return true;
	}

	/**
	 * 
	 * @param datFile
	 *            The DAT file that is being parsed
	 * @param inputFile
	 *            The file whose bib code will be used to find its .tif files
	 * @return This method will return the .tif files associated with the
	 *         specified file
	 */
	public static List<String> datParse(File datFile, File inputFile) {
		try {
			Scanner sc = new Scanner(datFile);
			String rest = "((\\s)*(\\d\\d\\d\\d\\d\\d\\d.000|0999999P\\d\\d\\d|\\d\\d\\d\\d\\d\\d\\d,\\d\\d\\d)*(\\n)*)*";
			// the possible file names in regex form
			String name = inputFile.getName().substring(0, 14);
			//replaces the .'s with numbers
			for (int i = 14; i < 19; i++) {
				if (inputFile.getName().charAt(i) == '.') {
					name += "\\D";
				} else {
					name += inputFile.getName().charAt(i);
				}
			}
			Pattern p = Pattern.compile(name + "\\sseri/...../\\d\\d\\d\\d/\\s\\d\\d\\d" + rest);
			// regex for the file name

			String listOfTiff = "";
			// This will be the string that contains all
			// the file names for the specified journal
			MatchResult m;
			while (sc.findWithinHorizon(p, 0) != null) {
				m = sc.match();
				listOfTiff = m.group(0);
			}
			p = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d.000|0999999P\\d\\d\\d|\\d\\d\\d\\d\\d\\d\\d,\\d\\d\\d");
			// the possible file names in regex form
			Matcher matcher = p.matcher(listOfTiff);
			// The tifs that comprise the specified xml file
			List<String> tiffList = new ArrayList<String>();
			// Pattern matches all the individual pages in the string produced above
			while (matcher.find()) {

				tiffList.add(matcher.group(0));
			}
			sc.close();
			return tiffList;
		} catch (Exception e) {
			writeToLog(e.getMessage(),"");
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * 
	 * @param inputFile
	 *            The XML file that this method will parse through, looking for
	 *            pictures
	 * @return Returns a list of integers that specifiy an image in the xml
	 *         file. The integers are as follows: Page, x cor, y cor, width,
	 *         height
	 * 
	 */
	public static List<List<Integer>> xmlParse(File inputFile) {
		try {
			// This will be the final list of pictures
			// the list contains the following data:
			//0:Page # 1:X coor 2:Y coor 3:Width 4:Height
			List<List<Integer>> pictureList = new ArrayList<List<Integer>>();
			// Parsing the xml file, turning it into a node list containing the whole file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			// The pages from the xml
			NodeList nList = doc.getElementsByTagName("page");
			NodeList children;
			Node child;
			List<Integer> tempList;
			int pageCount = 1;
			Element tempElement;
			int left;
			int right;
			int top;
			int bottom;
			int xCor;
			int yCor;
			int width;
			int height;
			// System.out.println("Pages: " + nList.getLength());
			// This loop goes through each page within the xml
			for (int temp = 0; temp < nList.getLength(); temp++) {
				// Each node is an element of the page and thus by going through
				// node by node the program will
				// parse through the xml page by page
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					// Selects only the pages within the xml
					if (nNode.getNodeName().equals("page")) {
						children = nNode.getChildNodes();
						// This loop goes thorugh each node in the page, looking
						// for pictures

						for (int i = 0; i < children.getLength(); i++) {
							child = children.item(i);
							if (child.getNodeType() == Node.ELEMENT_NODE) {
								tempElement = (Element) child;
								// Selects only pictures and extracts their
								// attributes, adding them to the list that will
								// be returned
								if (tempElement.getAttribute("blockType").equals("Picture")) {
									tempList = new ArrayList<Integer>();
									tempList.add(pageCount);
									left = Integer.parseInt(tempElement.getAttribute("l"));
									right = Integer.parseInt(tempElement.getAttribute("r"));
									top = Integer.parseInt(tempElement.getAttribute("t"));
									bottom = Integer.parseInt(tempElement.getAttribute("b"));
									xCor = left;
									yCor = top;
									width = right - left;
									height = bottom - top;
									tempList.add(xCor);
									tempList.add(yCor);
									tempList.add(width);
									tempList.add(height);
									pictureList.add(tempList);
								}
							}
						}
						pageCount++;
					}

				}
			}
			return pictureList;
		} catch (Exception e) {
			System.out.println("FileNotFound: Bib code not found");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param image
	 *            The image a smaller image will be generated from
	 * @param xCor
	 *            The x coordinate of the inner picture
	 * @param yCor
	 *            The x coordinate of the inner picture
	 * @param width
	 *            The width of the inner picture
	 * @param height
	 *            The height of the inner picture
	 * @param nameOfFile
	 *            The name of the file the inner picture will be saved to
	 * @param address
	 *            The address the inner picture will be saved to
	 */
	public static void cutImage(BufferedImage image, int xCor, int yCor, int width, int height, String nameOfFile,
			String address, boolean save) {
		// creates a slightly expanded view of the image, to catch graph labels etc.
		double scale = 0.2;
		xCor -= height * scale;
		yCor -= width * scale / 2;
		height += height * scale;
		width += width * scale / 2;

		//This size limit is both arbitrary and neccessary
		//It is limited because the most errors are caused
		//by small images such as ones of equations
		if (width * height >= 360000) {

			try {
				// Corrects for oversizing that could have been caused by the
				// above scaling
				if (xCor < 0) {
					xCor = 0;
				}
				if (yCor < 0) {
					yCor = 0;
				}
				if (xCor + width > image.getWidth()) {
					width = image.getWidth() - xCor;
				}
				if (yCor + height > image.getHeight()) {
					height = image.getHeight() - yCor;
				}
				// cuts up the image
				image = image.getSubimage(xCor, yCor, width, height);
			} catch (RasterFormatException e) {
				// This happens when the specified subimage goes out of the
				// bounds of the original
				System.out.println("RASTER: " + xCor + "," + width + ":" + image.getWidth() + " "
						+ (yCor + "," + height) + ":" + image.getHeight());
			}
			// for testing purposes
			if (save) {
				// Saves the file to the thumbnails folder
				File thumbnails = new File(address + "/Thumbnails/");
				thumbnails.mkdir();
				File saveFile = new File(address + "/Thumbnails/" + nameOfFile + ".png");
				int hello = classifyAsGraph(image,0,"");
				String type = "NA";
				if(hello == 0)
				{
					type = "Graph";
				}
				else if(hello == 1)
				{
					type = "InBetween";
				}
				else if(hello == 2)
				{
					type = "Picture";
				}
				createXML("0", width + "", "0", height + "", nameOfFile.substring(20, 21), type, address, nameOfFile);
				try {
					ImageIO.write(image, "png", saveFile);
				} catch (IOException e) {
					writeToLog(e.getMessage(),"");
					e.printStackTrace();
				}
			}

		} else {
			
		}

	}

	/**
	 * @param dat
	 *         	the name of the .dat file, including the journal name, <jounal
	 *            name>/<file name ex: AJ/0111/AJ...0111.dat
	 */
	public static void datFileRunThrough(String dat) {
		System.out.println("Run through started");
		try {
			// The file name for the dat file
			List<String> articleList = fetchArticles(dat);
			// Runs on each xml file in the dat
			// Use 10 instead of articleList.size() for testing purposes, its
			// much faster
			String input = "";
			for (int i = 0; i < /** articleList.size() */
			100; i++) {
				// Input is the xml file
				if (runner.VERSION.equals("server")) {
					runner.BIB = articleList.get(i).substring(0, 19);
					runner.JOURNAL = dat.substring(0, 5);
					runner.VOLUME = dat.substring(11, 15);

					input = runner.JOURNAL + "/" + runner.VOLUME + "/" + runner.BIB + ".luratech.xml";
				} else {
					input = dat.substring(0, 8) + articleList.get(i).substring(0, 19) + ".luratech.xml";
				}
				// Cuts out each image from the xml file, using the .dat as a
				// refrence
				run(input, dat);
				while (Thread.activeCount() >= 6) {
					Thread.sleep(10);

				}
			}
		} catch (Exception e) {
			writeToLog(e.getMessage(),"");
			e.printStackTrace();
		}
		try {
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @param dat The
	 *            address of the dat file
	 * @return The list of articles within that file ie the xml addresses
	 */
	public static List<String> fetchArticles(String dat) {
		try {
			System.out.println("DAT:" + dat);
			File datFile = new File(runner.METADATA_ROOT + dat);
			Scanner sc = new Scanner(datFile);
			String files = "\\d\\d\\d\\d\\d\\d\\d.000|0999999P\\d\\d\\d|\\d\\d\\d\\d\\d\\d\\d,\\d\\d\\d";
			// Regex for the tif file names
			
			String regex = "\\d\\d\\d\\d.." + "...." + "\\d\\d\\d" + "......" + "\\sseri/...../..../\\s\\d\\d\\d"
					+ "((\\s)*(" + files + ")*(\\n)*)*";
			// Regex for each xml file and its tifs
			
			Pattern p = Pattern.compile(regex);
			// The list of xml files and their tif files
			// The tif part actually goes unused and the run() method comes up
			// with it again(could be optimized
			List<String> articleList = new ArrayList<String>();
			MatchResult m;
			while (sc.findWithinHorizon(p, 0) != null) {
				m = sc.match();
				articleList.add(m.group(0));
			}
			sc.close();
			System.out.println("Size: " + articleList.size());
			return articleList;
		} catch (Exception e) {
			System.out.println("FileNotFound: The name of the volume you inputted does not exist in the archives");

			writeToLog("FileNotFound: The name of the volume you inputted does not exist in the archives","");
			return null;
		}
	}

	/**
	 * 
	 * @param journal The
	 *            name of the journal
	 * @param volume The
	 *            volume name/number for that journal
	 * @return The name of the dat file, composed based on this information
	 */
	public static String deriveDatName(String journal, String volume) {
		//composes what the dat file should be named based off of the volume and journal
		String datName = "";
		for (int i = 0; i < 5; i++) {
			if (journal.length() < i + 1) {
				datName = datName + ".";
			} else {
				datName = datName + journal.charAt(i);
			}
		}
		for (int i = 4; i > 0; i--) {
			if (volume.length() < i) {
				datName = datName + ".";
			} else {
				datName = datName + volume.charAt(4 - i);
			}
		}

		return datName + ".dat";
	}

	/**
	 * This method goes through an entire journal, executing run on each tif file
	 * @param journal The
	 *            name of the journal that images will be extracted from
	 */
	public static void journalRunThrough(String journal) {
		File journ = new File(runner.METADATA_ROOT + journal + "/");
		//This is the list of files that are contained within the journal
		File[] list = journ.listFiles();
		if (list == null) {
			System.out.println("Journal is invalid");
		} else {
			
			int count = 1;
			for (File f : list) {
					//This print statement is solely for the user to know where they are
					System.out.println("File number: " + count);
					count++;
					methods.datFileRunThrough(journal + "/" + f.getName());
			}
		}
	}

	private static final Comparator<File> File_COMPARATOR = new FileComparator();
	/**
	 * This method is an implementation of quick sort. This sorts the files by name
	 * @param fileList The files to be sorted
	 * @return File[] The files sorted into an ordered list
	 */
	public static File[] sortByNum(File[] fileList) {
		
		//Implementation of quicksort
		SwapList<File> result;
		SwapList<File> absl = new ArrayBasedSwapList<File>(fileList);
		QuickSorter<File> qs = new QuickSorter<File>(absl, File_COMPARATOR);
		result = qs.sort();
		File[] returnList = new File[result.size()];
		for (int i = 0; i < result.size(); i++) {
			returnList[i] = result.get(i);
		}
		return returnList;
	}
	/**
	 * Sorts the files at the location into Graphs Pictures and In between
	 * @param location The files to be sorted
	 */
	public static void sort(File location) {
		if (location.isDirectory()) {

			File[] list = location.listFiles();
			//Sorts the list alphabetically
			list = sortByNum(list);
			BufferedImage buff;
			try {
				
				//This code simply deletes the files in the old location before you begin sorting
				File f;
				File directory0 = new File(runner.IMAGES_ROOT + "/" + runner.JOURNAL + "/" + runner.VOLUME + "/Graph/");
				File directory1 = new File(
						runner.IMAGES_ROOT + "/" + runner.JOURNAL + "/" + runner.VOLUME + "/InBetween/");
				File directory2 = new File(
						runner.IMAGES_ROOT + "/" + runner.JOURNAL + "/" + runner.VOLUME + "/Pictures/");

				File[] fl0 = directory0.listFiles();
				File[] fl1 = directory1.listFiles();
				File[] fl2 = directory2.listFiles();

				if (directory0.exists()) {
					for (int i = 0; i < fl0.length; i++) {
						fl0[i].delete();
					}
				}
				directory0.delete();

				if (directory1.exists()) {
					for (int i = 0; i < fl1.length; i++) {
						fl1[i].delete();
					}
					directory1.delete();
				}

				if (directory2.exists()) {
					for (int i = 0; i < fl2.length; i++) {
						fl2[i].delete();
					}
				}
				directory2.delete();

				//This is the actual sorting part of the code
				//For each image in the list...
				for (int i = 0; i < list.length; i++) {
					f = list[i];
					buff = null;
					//The image is read
					try {
						buff = ImageIO.read(f);
					} catch (Exception me) {
						System.out.println("Error in: " + i);
						writeToLog(me.getMessage(),"");
						me.printStackTrace();
					}
					//The image is classified as a graph picture or in between
					int d = classifyAsGraph(buff, i, list[i].getAbsolutePath());
					if (d == 0) {
						ImageIO.write(buff, "png", new File(runner.IMAGES_ROOT + "/" + runner.JOURNAL + "/"
								+ runner.VOLUME + "/Graph/" + f.getName()));
					} else if (d == 1) {
						ImageIO.write(buff, "png", new File(runner.IMAGES_ROOT + "/" + runner.JOURNAL + "/"
								+ runner.VOLUME + "/InBetween/" + f.getName()));
					} else {
						ImageIO.write(buff, "png", new File(runner.IMAGES_ROOT + "/" + runner.JOURNAL + "/"
								+ runner.VOLUME + "/Pictures/" + f.getName()));
					}
				}

			} catch (Exception e) {

				writeToLog(e.getMessage(),"");
				e.printStackTrace();
			}
		} else {
			System.out.println("The file you inputed is not a directory");
		}

	}

	/**
	 * This method classifies a given image as a graph in between or picture
	 * It returns 0 1 and 2 respectfully 
	 * @param image The
	 *            BufferedImage file to be classified
	 * @param num unused
	 * @param path useless
	 * @return Returns true if its a graph, false if it is not
	 */
	public static int classifyAsGraph(BufferedImage image, int num, String path) {
		int width = image.getWidth();
		int height = image.getHeight();
		int rgb = 0;
		int count = 0;
		Color color;
		int mult = 5;
		//counts the number of colored pixels in the image
		for (int i = 0; i < width; i += mult)// ++)
		{
			for (int s = 0; s < height; s += mult)// ++)
			{
				rgb = image.getRGB(i, s);
				color = new Color(rgb);
				if (color.getRed() + color.getBlue() + color.getGreen() == 765) {
					count++;
				}
			}
		}
		//The determinant is what is used to classify the images
		//It is compared to the ratio
		double determinant = .45;
		//The ratio of white to non white pixels
		double ratio = (count + 0.0) * (mult * mult) / (width * height);

		if (ratio > determinant * 2) {
			// System.out.println(" Number: " + num + " Type: " + 0 + " Ratio: "
			// + ratio + " Path: " + path);
			return 0;
		} else if (ratio > determinant) // its a graph
		{
			return 1;
		} else// its a picture
		{
			return 2;
		}
	}

	/**
	 * @deprecated This method is not longer in use. It does not work as intended
	 * @param image The image to be classified
	 * @param num meaningless
	 * @param path The path to the image
	 * @return the classification number
	 */
	public static int classifyAsGraphBySections(BufferedImage image, int num, String path) {
		int width = image.getWidth();
		int height = image.getHeight();
		int mult = 10;
		int darkCount = 0;
		ArrayList<BufferedImage> biList = new ArrayList<BufferedImage>();

		for (int i = 0; i < width - width / mult; i += width / mult)// ++)
		{

			for (int s = 0; s < height - height / mult; s += height / mult)// ++)
			{

				biList.add(image.getSubimage(i, s, width / mult, height / mult));
			}
		}

		for (int a = 0; a < biList.size(); a++) {
			BufferedImage bi = biList.get(a);
			int r = classifyAsGraph(bi, 0, "", 0.3);
			if (r == 0) {

			} else {
				darkCount++;
			}
			
		}
		if (darkCount > 20) {
			return 2;
		} else {
			return 0;
		}
	}

	
	/**
	 * 
	 * @param image The
	 *            BufferedImage file to be classified
	 * @param num useless
	 * @param path useless
	 * @param deter sets the determanent 
	 * @return Returns true if its a graph, false if it is not
	 */
	public static int classifyAsGraph(BufferedImage image, int num, String path, double deter) {
		int width = image.getWidth();
		int height = image.getHeight();
		int rgb = 0;
		int count = 0;
		Color color;
		int mult = 1;
		for (int i = 0; i < width; i += mult)// ++)
		{
			for (int s = 0; s < height; s += mult)// ++)
			{
				rgb = image.getRGB(i, s);
				color = new Color(rgb);
				if (color.getRed() + color.getBlue() + color.getGreen() == 765) {
					count++;
				}
			}
		}

		double determinant = deter;// % that is white
		double ratio = (count + 0.0) * (mult * mult) / (width * height);// (count
																		// +
																		// 0.0)/(width*height);

		if (ratio > determinant * 2) {
			// System.out.println(" Number: " + num + " Type: " + 0 + " Ratio: "
			// + ratio + " Path: " + path);
			return 0;
		} else if (ratio > determinant) // its a graph
		{
			// System.out.println(" Number: " + num + " Type: " + 1 + " Ratio: "
			// + ratio + " Path: " + path);
			return 1;
		} else// its a picture
		{
			// System.out.println(" Number: " + num + " Type: " + 2 + " Ratio: "
			// + ratio + " Path: " + path);
			return 2;
		}
	}

	/**
	 * This method writes to either an error log or simply a program log.
	 * Make the first arguement "Error" for the error log and "Text" for the text log
	 * @param error Tells the type of message
	 * @param message The message to be recorded
	 */
	public static void writeToLog(String error, String message) {
		File log;
		if (error == "Clear") {
			log = new File("ErrorLog.txt");
			log.delete();
			log = new File("ErrorLog.txt");
		} else if (error == "Text") {
			log = new File("ProgramLog.txt");
			try {
				FileWriter fw = new FileWriter(log.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(message);
				bw.close();
			} catch (Exception e) {
				writeToLog(e.getMessage(), "");
				e.printStackTrace();
			}
		} else {
			log = new File("ErrorLog.txt");
			log.delete();
			log = new File("ErrorLog.txt");
			try {
				FileWriter fw = new FileWriter(log);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(error);
				bw.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param left the left coordinate
	 * @param right the right coordinate
	 * @param top the top coordinate
	 * @param bottom the bottom coordinate
	 * @param page the page number
	 * @param type the type of the image
	 * @param address the address where the image will be saved
	 * @param nameOfFile the name of the XML file to be made
	 */
	public static void createXML(String left, String right, String top, String bottom, String page, String type,
			String address, String nameOfFile) {
		try {
			//general structure:
			//<block>
			//	<region></region>
			//</block>
			//<bibcode></bibcode>
			//<journal></journal>
			//<volume></volume>
			//<page></page>
			//<type></type>
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// root element
			Element rootElement = doc.createElement("graphic");
			doc.appendChild(rootElement);

			//block element
			Element supercar = doc.createElement("block");
			rootElement.appendChild(supercar);

			// setting attribute to element
			Attr attr0 = doc.createAttribute("blockType");
			attr0.setValue("Picture");
			supercar.setAttributeNode(attr0);

			Attr attr1 = doc.createAttribute("l");
			attr1.setValue(left);
			supercar.setAttributeNode(attr1);

			Attr attr2 = doc.createAttribute("t");
			attr2.setValue(top);
			supercar.setAttributeNode(attr2);

			Attr attr3 = doc.createAttribute("r");
			attr3.setValue(right);
			supercar.setAttributeNode(attr3);

			Attr attr4 = doc.createAttribute("b");
			attr4.setValue(bottom);
			supercar.setAttributeNode(attr4);

			Element carname1 = doc.createElement("region");

			supercar.appendChild(carname1);

			Element carname11 = doc.createElement("rect");

			carname1.appendChild(carname11);

			Attr attr11 = doc.createAttribute("l");
			attr11.setValue(left);
			carname11.setAttributeNode(attr11);

			Attr attr12 = doc.createAttribute("t");
			attr12.setValue(top);
			carname11.setAttributeNode(attr12);

			Attr attr13 = doc.createAttribute("r");
			attr13.setValue(right);
			carname11.setAttributeNode(attr13);

			Attr attr14 = doc.createAttribute("b");
			attr14.setValue(bottom);
			carname11.setAttributeNode(attr14);

			Element carname2 = doc.createElement("bibcode");
			carname2.appendChild(doc.createTextNode(runner.BIB));
			rootElement.appendChild(carname2);

			Element carname3 = doc.createElement("journal");
			carname3.appendChild(doc.createTextNode(runner.JOURNAL));
			rootElement.appendChild(carname3);

			Element carname4 = doc.createElement("volume");
			carname4.appendChild(doc.createTextNode(runner.VOLUME));
			rootElement.appendChild(carname4);

			Element carname5 = doc.createElement("page");
			carname5.appendChild(doc.createTextNode(page));
			rootElement.appendChild(carname5);

			Element carname6 = doc.createElement("type");
			carname6.appendChild(doc.createTextNode(type));
			rootElement.appendChild(carname6);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			File folder = new File(address + "/Metadata/");
			folder.mkdir();
			File saveFile = new File(address + "/Metadata/" + nameOfFile + ".xml");

			StreamResult result = new StreamResult(saveFile);

			transformer.transform(source, result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
