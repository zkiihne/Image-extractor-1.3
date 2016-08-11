package Main;

import java.io.File;
import java.util.Scanner;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
/**
 * 
 * @author zkiihne
 *
 */
public class runner {

	/**
	 * These are the data roots, as read from the config file OCR: Where the
	 * .xml files are stored METADATA: Where the .dat files are stored IMAGES:
	 * Where the .tif files are stored Please include up to and including the
	 * seri ex: C:/Users/Example/Desktop/IMAGES/seri/
	 */

	public static String OCR_ROOT = "";
	public static String METADATA_ROOT = "";
	public static String IMAGES_ROOT = "";
	public static String VERSION = "server";
	public static String BIB = "";
	public static String VOLUME = "";
	public static String JOURNAL = "";
	public static long TIME = 0;

	public static void main(String args[]) {
		TIME = System.currentTimeMillis();
		File config;
		String[] arguements;
		boolean functionPerformed = false;
		
		//if there is only one arguement, -help this can cause issues without the if statement here
		if (args[0].equals("-help") || args[0].equals("-h")) {

		} else {
			
			arguements = new String[args.length - 2];
			//This code changes the config file from the default
			if (args[0].equals("-c") || args[0].equals("-config")) {
				config = new File(args[1]);
				for (int i = 2; i < args.length; i++) {
					arguements[i - 2] = args[i];
				}
				System.out.println(config.getAbsolutePath());
				System.out.println(args.length);
				methods.writeToLog("Text", "Run on config file " + config.getAbsolutePath());
				args = arguements;
				functionPerformed = true;
			} else {
				//default config location
					config = new File("/home/zkiihne/config.txt");
			}

			try {
				/**
				 * Config file: Where the root data is stored
				 */

				Scanner sc = new Scanner(config);
				String line;
				while (sc.hasNextLine()) {
					// reads through the config file
					line = sc.nextLine();
					System.out.println(line);

					// finds the proper roots and saves them
					if (line.substring(0, 3).equals("OCR")) {
						OCR_ROOT = line.substring(10);
					} else if (line.substring(0, 6).equals("IMAGES")) {
						IMAGES_ROOT = line.substring(13);
					} else if (line.substring(0, 8).equals("METADATA")) {
						METADATA_ROOT = line.substring(15);
					}
				}
				sc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//prints out the help menu
		if (args[0].equals("-help") || args[0].equals("-h")) {
			System.out.println("Commands:");
			System.out.print("-journal/-j:  ");
			System.out.println(" Extracts the image from a whole journal");
			System.out.println(" ex: -j AJ...");
			System.out.print("-volume/-v:   ");
			System.out.println(" Extracts the image from a whole volume, specify jounral first");
			System.out.println(" ex: -j AJ... -v 0111");
			System.out.print("-bibicode/-b: ");
			System.out.println(" Extracts the image from a specific file, specify jounral and then the volume");
			System.out.println(" ex: -j AJ... -v 0111 -b 1996AJ....111..109S");
			System.out.print("-sort/-s:     ");
			System.out.println(" Sorts the specified volume into Graphs, In Between and Pictures");
			System.out.println(" ex: -s AJ... 0111");
			System.out.print("-config/-c:   ");
			System.out.println(" Put this in front of any command you want to use to change the config file it usese");
			System.out.println(
					"               To permanently change the config file the code must be modified. Please see the documentation to see what the config file contains");
			System.out.println(" ex: -c newConfig.txt -j AJ...");
			System.out.print("-test/-t: ");
			System.out.println(
					"     Runs a series of unit tests. These test will identify if the specified journal and volume can be worked with");
			System.out.println(" ex: -t AJ... 0111");

			functionPerformed = true;
		//sorts the specificed volume
		} else if (args[0].equals("-sort") || args[0].equals("-s")) {
			JOURNAL = args[1];
			VOLUME = args[2];
			System.out.println(
					"File location: " + runner.IMAGES_ROOT + JOURNAL + "/" + VOLUME + "/" + "600" + "/Thumbnails/");
			methods.sort(new File(runner.IMAGES_ROOT + JOURNAL + "/" + VOLUME + "/" + "600" + "/Thumbnails/"));

			methods.writeToLog("Text", "Sorted: " + JOURNAL + "/" + VOLUME + "/");
			functionPerformed = true;
		//tests the journal and volume
		} else if (args[0].equals("-t") || args[0].equals("-test")) {
			JOURNAL = args[1];
			VOLUME = args[2];
			System.out.println(JOURNAL + "/" + VOLUME);
			JUnitCore junit = new JUnitCore();
			Result result = junit.run(TestSuites.class);
			result.toString();
			functionPerformed = true;
		}
		// only works for the bibicodes
		else if (args.length == 6) {
			if (((args[2].equals("-v") || args[2].equals("-volume"))
					&& (args[0].equals("-j") || args[0].equals("-journal")))
					&& (args[4].equals("-b") || args[4].equals("-bibicode"))) // &&
																				// args[0].equals("-j")&&args)
			{
				// prints out what it did
				// runs on specific file
				// DAT/seri/journal/volume/s
				String volume = args[3];
				String journal = args[1];
				String bib = args[5];
				if (volume.length() != 4) {
					System.out.println(
							"Volume name is not correct, please make sure it is 4 characters. Test it with -t first");
				} else if (journal.length() != 5) {
					System.out.println(
							"Journal name is not correct, please make sure it is 5 characters. Test it with -t first");
				} else {

					System.out.println("Journal: " + journal + " Volume: " + volume + " Bib:" + bib);
					String dat = methods.deriveDatName(journal, volume);
					System.out.println(dat);
					VOLUME = volume;
					JOURNAL = journal;
					BIB = bib;
					if (runner.VERSION.equals("server")) {

						methods.run(journal + "/" + volume + "/" + bib + ".luratech.xml", journal + "/" + dat);
					} else {
						methods.run(journal + "/" + volume + "/" + bib + ".luratech.xml",
								journal + "/" + volume + "/" + dat);
					}

					methods.writeToLog("Text", "Cut out images for bib: " + JOURNAL + "/" + VOLUME + "/" + BIB);
				}
				functionPerformed = true;

			}

		}
		// only works for volumes
		else if (args.length == 4) {
			if ((args[2].equals("-v") || args[2].equals("-volume"))
					&& (args[0].equals("-j") || args[0].equals("-journal"))) {
				// prints out what it did
				// runs on whole volume
				// DAT/seri/journal/s
				String volume = args[3];
				String journal = args[1];

				if (volume.length() != 4) {
					System.out.println(
							"Volume name is not correct, please make sure it is 4 characters. Test it with -t first");
				} else if (journal.length() != 5) {
					System.out.println(
							"Journal name is not correct, please make sure it is 5 characters. Test it with -t first");
				} else {
					VOLUME = volume;
					JOURNAL = journal;
					System.out.println("Journal: " + journal + " Volume: " + volume);
					String dat = methods.deriveDatName(journal, volume);

					if (runner.VERSION.equals("server")) {
						File thumbnails = new File(IMAGES_ROOT + "/" + journal + "/" + volume + "/600/Thumbnails/");

						if (thumbnails.isDirectory()) {
							File[] listOfFiles = thumbnails.listFiles();
							for (int i = 0; i < listOfFiles.length; i++) {
								listOfFiles[i].delete();
							}
						} else {

						}
						methods.datFileRunThrough(journal + "/" + dat);
					} else {
						methods.datFileRunThrough(journal + "/" + volume + "/" + dat);
					}

					methods.writeToLog("Text", "Cut out images for volume: " + JOURNAL + "/" + VOLUME + "/");
				}
				functionPerformed = true;
			}

		}
		// only works for jounrals
		else if (args.length == 2) {
			if (args[0].equals("-j") || args[0].equals("-journal")) {
				// prints out what it did
				// runs on whole volume
				// DAT/seri/journal/s
				String journal = args[1];
				System.out.println("Journal:" + journal);
				JOURNAL = journal;
				if (journal.length() != 5) {
					System.out.println(
							"Journal name is not correct, please make sure it is 5 characters. Test it with -t first");
				} else {
					methods.journalRunThrough(journal);
					methods.writeToLog("Text", "Cut out images for journal: " + JOURNAL + "/");
				}
				functionPerformed = true;
			} else if (args[0].equals("-b") || args[0].equals("-bib")) {
				String bib = args[1];
				if (bib.length() != 19) {
					System.out.println(
							"Bib code is not correct, please make sure it is 19 characters. Test it with -t first");
				} else {
					JOURNAL = bib.substring(4, 9);
					VOLUME = bib.substring(9, 13).replace(".", "0");
					BIB = bib;

					System.out.println("V: " + VOLUME);
					System.out.println("Bib " + bib);

					String dat = methods.deriveDatName(JOURNAL, VOLUME);
					if (runner.VERSION.equals("server")) {

						methods.run(JOURNAL + "/" + VOLUME + "/" + BIB + ".luratech.xml", JOURNAL + "/" + dat);
					}
					methods.writeToLog("Text", "Cut out images for bib: " + JOURNAL + "/" + VOLUME + "/" + BIB);
				}
				functionPerformed = true;
			}

		}
		// Catch all if they didnt do anything
		if (functionPerformed == false) {
			System.out.println();
			System.out.println("What you inputed was not a recognizable command");
			System.out.println();
			System.out.println("Please try again");
			System.out.println();
			System.out.println("For help please use the arguement -help");
			System.out.println();

		}

		long end = System.currentTimeMillis();
		System.out.println("Time taken: " + (end - TIME));
	}

}
