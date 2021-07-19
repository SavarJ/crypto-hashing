import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class MySystem {
	private final String filePath;
	public final static String ENCRYPTION = "SHA-256";

	public MySystem(String filePath) {
		this.filePath = filePath;
	}

	public void deleteAccount(String user, String pass) {
		// Creating file objects with appropriate paths
		File inputFile = new File(this.filePath);
		File tempFile = new File(this.filePath.substring(0, this.filePath.lastIndexOf("/") + 1) + "tempFile.txt");

		Scanner scan = null;
		PrintWriter writer = null;
		try {
			scan = new Scanner(new FileReader(inputFile));
			writer = new PrintWriter(new FileWriter(tempFile));
		} catch (Exception e) {
			System.out.println("Problem with the file. Try again later");
			System.exit(1);
		}

		String currentLine;

		while (scan.hasNext()) {
			currentLine = scan.nextLine();
			String[] data = currentLine.trim().split(":");
			if (data[0].equals(user)) {
				pass = pass + data[1]; // adding salt
				try {
					pass = hashPass(pass);
				} catch (NoSuchAlgorithmException e) {
					System.out.println("Problem with comparing password securely. Try again later");
					System.exit(1);
				}
				if (!data[2].equals(pass)) {
					writer.println(currentLine);
				}
			} else {
				writer.println(currentLine);
			}
		}
		writer.close();
		scan.close();
		inputFile.delete();
		boolean successful = tempFile.renameTo(inputFile);
		if (successful) {
			System.out.println("Account delelted succesfully if user/pass were found!");
			System.exit(0);
		} else {
			System.out.println("Account could not be deleted!");
			System.exit(2);

		}
	}

	public void logIn(String user, String pass) {

		Scanner scan = null;
		try {
			scan = new Scanner(new FileInputStream(new File(this.filePath)));
		} catch (FileNotFoundException e) {
			System.out.println("Problem with the file. Try again later");
			System.exit(1);
		}

		int exitCode = 2;

		while (scan.hasNext()) {
			String[] data = scan.nextLine().split(":");
			if (data[0].equals(user)) {
				pass = pass + data[1]; // adding salt
				try {
					pass = hashPass(pass);
				} catch (NoSuchAlgorithmException e) {
					System.out.println("Problem with comparing password securely. Try again later");
					exitCode = 1;
					break;
				}
				if (data[2].equals(pass)) {
					System.out.println("LOGIN SUCCESSFUL");
					exitCode = 0;
					break;
				}
			}

		}
		scan.close();
		if (exitCode == 0 || exitCode == 1) {
			System.exit(exitCode);
		}
		System.out.println("INCORRECT PASSWORD or USERNAME. Try Again later");
		System.exit(2);

	}

	public void signUp(String user, String pass) {

		// generate salt and hash
		String salt = generateSalt();
		pass += salt;
		try {
			pass = hashPass(pass);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Password could not be hashed. Try again later.");
			System.exit(1);
		}

		// save it to the file
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(this.filePath, true));

			// user:salt:hash:"SHA-256"
			pw.println(user + ":" + salt + ":" + pass + ":" + ENCRYPTION);
			System.out.println("Account created successfully");
		} catch (Exception e) {
			System.out.println("Problem with file. Account could not be created");
		} finally {
			pw.close();

			// Explicitly setting password to null
			pass = null;
		}

	}

	private static String hashPass(String pass) throws NoSuchAlgorithmException {
		return toHexString(getSHA(pass));
	}

	private static String generateSalt() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		return generatedString;
	}

	private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
		// Static getInstance method is called with hashing SHA
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		// digest() method called
		// to calculate message digest of an input
		// and return array of byte
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}

	private static String toHexString(byte[] hash) {
		// Convert byte array into signum representation
		BigInteger number = new BigInteger(1, hash);

		// Convert message digest into hex value
		StringBuilder hexString = new StringBuilder(number.toString(16));

		// Pad with leading zeros
		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}

		return hexString.toString();
	}

}
