/**
 * 
 */
package tagcleaner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

/**
 * 
 */
public class TagCleaner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to TagCleaner! Let's clean those dirty tags.");
		try (Scanner scanner = new Scanner(System.in)) {
			boolean havePath = false;
			String path = null;
			File folder = null;
			while (!havePath) {
				System.out.print("Enter the path of a folder with mp3 files: ");
				path = scanner.nextLine();
				folder = new File(path);
				if (!folder.exists()) {
					System.out.println("Folder doesn't exist.");
					continue;
				}
				if (!folder.isDirectory()) {
					System.out.println("Path points to a file, not a folder.");
					continue;
				}
				havePath = true;
			}
			if (!path.endsWith(File.separator)) {
				path = path + File.separator;
			}
			String[] filenames = folder.list();
			System.out.print("Found " + filenames.length + " files, ");
			List<String> mp3Filenames = new ArrayList<String>();
			for (String filename : filenames) {
				if (filename.endsWith(".mp3") || filename.endsWith(".MP3")) {
					mp3Filenames.add(filename);
				}
			}
			System.out.println(mp3Filenames.size() + " are mp3s.");
			if (mp3Filenames.size() > 0) {
				System.out.print("What would you like removed from the tags? ");
				String removeMe = scanner.nextLine();
				if (removeMe.isEmpty()) {
					System.out.println("Nothing to remove, we're done");
					System.exit(0);
				}
				boolean haveTag = false;
				int tagNumber = 0;
				while (!haveTag) {
					System.out.print(
							"Which tag would you like cleaned?\n1 - Title\n2 - Artist\n3 - Album\nEnter tag number: ");
					tagNumber = scanner.nextInt();
					if (tagNumber < 1 || tagNumber > 3) {
						System.out.println("Invalid entry: " + tagNumber);
						continue;
					} else {
						haveTag = true;
					}
				}
				File cleanedFolder = new File(path + "cleaned" + File.separator);
				if (!cleanedFolder.exists()) {
					cleanedFolder.mkdir();
				}
				for (String mp3Filename : mp3Filenames) {
					Mp3File mp3File = new Mp3File(path + mp3Filename);
					if (mp3File.hasId3v2Tag()) {
						ID3v2 id3v2Tag = mp3File.getId3v2Tag();
						String value = null;
						switch (tagNumber) {
						case 1:
							value = id3v2Tag.getTitle();
							break;
						case 2:
							value = id3v2Tag.getArtist();
							break;
						case 3:
							value = id3v2Tag.getAlbum();
							break;
						default:
							// should never happen because of check above
						}
						if (value.contains(removeMe)) {
							value = value.replace(removeMe, "").trim();
							switch (tagNumber) {
							case 1:
								id3v2Tag.setTitle(value);
								break;
							case 2:
								id3v2Tag.setArtist(removeMe);
								break;
							case 3:
								id3v2Tag.setAlbum(removeMe);
								break;
							}
							mp3File.save(path + "cleaned/" + mp3Filename);
							System.out.println("Cleaned " + mp3Filename);
						}
					}
				}
			}
			System.out.println("We're done.");
		} catch (UnsupportedTagException | InvalidDataException | IOException | NotSupportedException e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}
}
