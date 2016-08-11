package Main;

import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

//import comparators.IntegerComparator;
//import comparators.LexicographicStringComparator;

public class TestSuites {
	@Rule
	public Timeout globalTimeout = new Timeout(50000000L, TimeUnit.MILLISECONDS);

//	private static final Comparator<Integer> INTEGER_COMPARATOR = new IntegerComparator();
//	private static final Comparator<String> STRING_COMPARATOR = new LexicographicStringComparator();

	@Before
	public void before() {

	}
	
	@Test
	public void testRoots()
	{
		String ocr = runner.OCR_ROOT;
		String meta = runner.METADATA_ROOT;
		String images = runner.IMAGES_ROOT;
		File f1 = new File(ocr);
		File f2 = new File(meta);
		File f3 = new File(images);
		assertTrue(f1.exists() && f1.isDirectory() && f1.canRead());
		assertTrue(f2.exists() && f2.isDirectory() && f2.canRead());
		assertTrue(f3.exists() && f3.isDirectory() && f3.canRead());
	}
	
	@Test
	public void testVolume()
	{
		System.out.println(runner.IMAGES_ROOT +runner.JOURNAL + "/" + runner.VOLUME);
		File f1 = new File(runner.IMAGES_ROOT + runner.JOURNAL + "/"+ runner.VOLUME);
		if(f1.exists()){}else{System.out.println("Volume test failed: File does not exist");}
		if(f1.isDirectory()){}else{System.out.println("Volume test failed: File is not a directory");}
		if(f1.canRead()){}else{System.out.println("Volume test failed: File cannot be read");}
	}
	@Test
	public void testJournal()
	{
		File f1 = new File(runner.IMAGES_ROOT + runner.JOURNAL);
		if(f1.exists()){}else{System.out.println("Journal test failed: File does not exist");}
		if(f1.isDirectory()){}else{System.out.println("Journal test failed: File is not a directory");}
		if(f1.canRead()){}else{System.out.println("Journal test failed: File cannot be read");}
	}
	@Test
	public void testOCR()
	{
		File f1 = new File(runner.OCR_ROOT);
		if(f1.exists()){}else{System.out.println("OCR root test failed: File does not exist");}
		if(f1.isDirectory()){}else{System.out.println("OCR root test failed: File is not a directory");}
		if(f1.canRead()){}else{System.out.println("OCR root test failed: File cannot be read");}
	}
	@Test
	public void testMETA()
	{
		File f1 = new File(runner.METADATA_ROOT);
		if(f1.exists()){}else{System.out.println("Metadata root test failed: File does not exist");}
		if(f1.isDirectory()){}else{System.out.println("Metadata root test failed: File is not a directory");}
		if(f1.canRead()){}else{System.out.println("Metadata root test failed: File cannot be read");}
	}
	@Test
	public void testIMAGES()
	{
		File f1 = new File(runner.IMAGES_ROOT);
		if(f1.exists()){}else{System.out.println("Images root test failed: File does not exist");}
		if(f1.isDirectory()){}else{System.out.println("Images root test failed: File is not a directory");}
		if(f1.canRead()){}else{System.out.println("Images root test failed: File cannot be read");}
	}
	@Test
	public void testConfig()
	{
		File f1 = new File("/home/zkiihne/config.txt");
		if(f1.exists()){}else{System.out.println("Config file test failed: File does not exist");}
		if(f1.canRead()){}else{System.out.println("Config file test failed: File cannot be read");}
	}
	
	
	
	//@Test
//	public void testClassifyEnMasse()
//	{
//		for(double i = 0.45; i < 0.55; i += 0.01)
//		{
//			testClassify(i);
//			//0.45 gives 100% on both graphs and pics
//		}
//	}
//	
////	@Test
//		public void testThread()
//		{
////			methods m = new methods();
////			methods n = new methods();
//////			m.start("a");
//////			n.start("b");
////			String a;
////			while(true)
////			{
////				a = "";
////			}
//		}
//		@Test
//		public void testXML()
//		{
//			//methods m = new methods();
//			//m.createXML();
//		}
//	
//	//@Test
//	public void sort()
//	{
//		File journ = new File(runner.IMAGES_ROOT + "AJ/0111/Thumbnails/");
//		File[] list = journ.listFiles();
//		BufferedImage buff;
//		try{
//			File f;
//		for(int i = 0; i < 100;i++)
//		{
//			f = list[i];
//			System.out.println("Num: " + i + "    "+ f.getName());
//			buff = ImageIO.read(f);
//			int d = 0;//methods.classifyAsGraph(buff,0.0);
//			if(d == 0 )
//			{
//				ImageIO.write(buff, "png", new File("Testing images/0/" + f.getName()));
//			}
//			else if(d == 1)
//			{
//				ImageIO.write(buff, "png", new File("Testing images/1/" + f.getName()));
//			}
//			else
//			{
//				ImageIO.write(buff, "png", new File("Testing images/2/" + f.getName()));
//			}
//		}	
//		
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	public void testClassify(double det) {
//		// graph =    new File("C:/Users/zkiihne/workspace/Image extractor 1.3/IMAGES/seri/AJ/0111/Thumbnails/1996AJ....111...29O_7_1_thmb.png");
//		//File notgraph = new File("IMAGES/seri/AJ/0111/Thumbnails/1996AJ....111...29O_2_1_thmb.png");
//		//returns empty on an empty file
//		//returns the correct # for the 3 test files
//		System.out.println("Determinant: " + det);
//		BufferedImage bi = new BufferedImage(1877,2006,BufferedImage.TYPE_INT_ARGB);
//		//BufferedImage bi2 = new BufferedImage(2343,3297,BufferedImage.TYPE_INT_ARGB);
//		try{
//		File graphs = new File("Testing images/Graphs/");
//		File pictures = new File("Testing images/Pictures/");
//		File text = new File("Testing images/Text/");
//		int wrong = 0; int right = 0;
//		File[] list = graphs.listFiles();
//		for(File f: list)
//		{
//			
//		bi =  ImageIO.read(f);
//		if(methods.classifyAsGraph(bi,det))
//		{
//			right++;
//		}
//		else
//		{
//			wrong++;
//		}
//		}
//		System.out.println("Error ratio: " + (wrong + 0.0)/right);
//		wrong = 0;
//		right = 0;
//		list = pictures.listFiles();
//		for(File f: list)
//		{
//			bi =  ImageIO.read(f);
//			if(!methods.classifyAsGraph(bi,det))
//			{
//				right++;
//			}
//			else
//			{
//				wrong++;
//			}
//		}
//		System.out.println("Error ratio: " + (wrong + 0.0)/(wrong +right));
//		wrong = 0;
//		right = 0;
//		list = text.listFiles();
//		for(File f: list)
//		{
//			bi =  ImageIO.read(f);
//			if(!methods.classifyAsGraph(bi,det))
//			{
//				right++;
//			}
//			else
//			{
//				wrong++;
//			}
//		}
//		System.out.println("Error ratio: " + (wrong + 0.0)/(wrong +right));
//		}
//		catch(Exception e)
//		{e.printStackTrace();}
//		
//
//		
//	}
//	//@Test
//	public void testGraph() {
//		// graph =    new File("C:/Users/zkiihne/workspace/Image extractor 1.3/IMAGES/seri/AJ/0111/Thumbnails/1996AJ....111...29O_7_1_thmb.png");
//		//File notgraph = new File("IMAGES/seri/AJ/0111/Thumbnails/1996AJ....111...29O_2_1_thmb.png");
//		//returns empty on an empty file
//		//returns the correct # for the 3 test files
//		//BufferedImage bi = new BufferedImage(1877,2006,BufferedImage.TYPE_INT_ARGB);
//		//BufferedImage bi2 = new BufferedImage(2343,3297,BufferedImage.TYPE_INT_ARGB);
//		//try{
//		//File graphs = new File("Testing images/Graphs/");
//			
//		//bi =  ImageIO.read(f);
//		//if(true)
////		{
////			right++;
////		}
////		else
////		{
////			wrong++;
////		}
////		}
////		System.out.println("Error ratio: " + (wrong + 0.0)/right);
////		wrong = 0;
////		right = 0;
////		list = pictures.listFiles();
////		for(File f: list)
////		{
////			bi =  ImageIO.read(f);
////			if(true)
////			{
////				right++;
////			}
////			else
////			{
////				wrong++;
////			}
////		}
////		System.out.println("Error ratio: " + (wrong + 0.0)/(wrong +right));
////		wrong = 0;
////		right = 0;
////		list = text.listFiles();
////		for(File f: list)
////		{
////			bi =  ImageIO.read(f);
////			if(true)
////			{
////				right++;
////			}
////			else
////			{
////				wrong++;
////			}
////		}
////		System.out.println("Error ratio: " + (wrong + 0.0)/(wrong +right));
//		//}
//		//catch(Exception e)
//		//{e.printStackTrace();}
//		
////
////		
//	}
//
//	//@Test
//	public void testComandLine() {
//		String[] args = {"-j","AJ","-v","0111","-b","1996AJ....111....1S"};
//		//String[] args2 = {"-j","AJ","-v","0111"};
////		String[] args3 = {"-j","AJ"};
////		String[] bad = {"-j","AJ","-v"};
//		//runner.main(bad);
//		//runner.main(new String[0]);
//		runner.main(args);
//		//runner.main(args2);
//		//methods.datFileRunThrough("AJ/0111");
//		//runner.main(args3);
//	}
//
//	//@Test
//	public void testCut() {
//		//makes sure the cutImage is taking the right image
//		try{	
//			//The tif image
//			String address = "TestFiles/";
//			String nameOfFile = "subImage1";
//			RandomAccessFile raf = new RandomAccessFile("TestFiles/0001034.000.tif", "r");
//			//Decodes the tif, turning it into a buffered image object
//			TiffDecoder decoder = new TiffDecoder(raf);
//			BufferedImage decodedImage = decoder.read();
//			raf.close();
//			decodedImage = decodedImage.getSubimage(0,0,100,100);
//			methods.cutImage(decodedImage, 0, 0, 100, 100, nameOfFile, address,true);
//			File saveFile = new File(address + "/Thumbnails/" + nameOfFile +  ".jpg");
//			BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
//			bi = ImageIO.read(saveFile); 
//			boolean a = TestSuites.compareImages(decodedImage,bi);
//			assertTrue(a);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//	}
//	//@Test
//	public void testDeriveDat() {
//		String a = methods.deriveDatName("AJ", "0111");
//		assertTrue(a.equals("AJ...0111.dat"));
//		
//	}
//	/**
//	 * Compares two images pixel by pixel.
//	 *
//	 * @param imgA the first image.
//	 * @param imgB the second image.
//	 * @return whether the images are both the same or not.
//	 */
//	public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
//	  // The images must be the same size.
//	  if (imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
//	    int width = imgA.getWidth();
//	    int height = imgA.getHeight();
//
//	    // Loop over every pixel.
//	    for (int y = 0; y < height; y++) {
//	      for (int x = 0; x < width; x++) {
//	        // Compare the pixels for equality.
//	        if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
//	          return false;
//	        }
//	      }
//	    }
//	  } else {
//	    return false;
//	  }
//
//	  return true;
//	}
}

